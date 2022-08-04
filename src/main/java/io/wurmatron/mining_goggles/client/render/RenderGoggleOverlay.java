package io.wurmatron.mining_goggles.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.api.MiningGogglesCollector;
import io.wurmatron.mining_goggles.config.OreConfigLoader;
import io.wurmatron.mining_goggles.utils.WavelengthCalculator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.lwjgl.opengl.GL11;

public class RenderGoggleOverlay {

  public static NonBlockingHashMap<BlockPos, Float[]> activeRendering = new NonBlockingHashMap<>();
  public static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

  // Configurable
  public static final int MAX_GROWTH_PER_UPDATE = 5; // count
  public static final int RENDER_UPDATE_TIMER = 20; // tick
  public static final int RENDER_CLEANUP_TIMER = 5; // multiple's of RENDER_UPDATE_TIMER
  public static final double FUZZY_RANGE_BEST = .2; // overall total (1 * x)
  public static final double FUZZY_RANGE_LOW = .3; // overall total (1 * x)
  public static final int RESCAN_INTERVAL = 5; // multiple's of RENDER_UPDATE_TIMER
  public static final int DAMAGE_INTERVAL = 5; // sec (in world ticks at 20tps)

  // Timers
  public static int renderTimer;
  public static int cleanupTimer;
  public static int rescanTimer;
  public static int damageTimer;

  @SubscribeEvent
  public void onRenderWorld(RenderWorldLastEvent e) {
    if (!activeRendering.isEmpty()) {
      Minecraft.getInstance().gameRenderer.resetProjectionMatrix(e.getProjectionMatrix());
      for (BlockPos pos : activeRendering.keySet()) {
        Float[] color = activeRendering.get(pos);
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), BOX, color[0], color[1], color[2],
            color[3], pos);
      }
    }
  }

  @SubscribeEvent
  public void onClientTick(PlayerTickEvent e) {
    if (e.side.isClient()) {
      // Check for goggles
      if (!(e.player.inventory.armor.get(3)
          .getItem() instanceof MiningGogglesCollector)) {
        activeRendering.clear();
        return;
      }
      // Update Rendering
      if (renderTimer == 0) {
        updateActiveRendering(e.player);
        renderTimer = RENDER_UPDATE_TIMER;
        if (cleanupTimer == 0) {
          cleanupTimer = RENDER_CLEANUP_TIMER;
          cleanupRenderEntries(e.player);
        } else {
          cleanupTimer--;
        }
      } else {
        renderTimer--;
      }

    } else if (e.side.isServer()) {
      if (e.player.inventory.armor.get(3).getItem() instanceof MiningGogglesCollector) {
        MiningGogglesCollector collector = (MiningGogglesCollector) e.player.inventory.armor.get(
            3).getItem();
        if (damageTimer == 0) {
          collector.damageCrystals(e.player.level.random,e.player.inventory.armor.get(3));
          damageTimer = 20 * DAMAGE_INTERVAL;
        } else {
          damageTimer--;
        }
      }
    }
  }

  private static void updateActiveRendering(PlayerEntity player) {
    ItemStack stack = player.inventory.armor.get(3);
    MiningGoggles.EXECUTORS.submit(() -> {
      NonBlockingHashMap<BlockPos, Float[]> detectedBlocks = collectDetectedBlocks(player,
          stack, rescanTimer == 0);
      // Track for next rescan
      if (rescanTimer == 0 || rescanTimer >= 40 && activeRendering.size() == 0) {
        rescanTimer = RESCAN_INTERVAL;
      } else {
        rescanTimer--;
      }
      if (activeRendering.size() < detectedBlocks.size()) {
        for (int count = 0; count < MAX_GROWTH_PER_UPDATE; count++) {
          int index = activeRendering.size() + count;
          if (detectedBlocks.size() > index) {
            for (BlockPos pos : detectedBlocks.keySet()) {
              if (!activeRendering.containsKey(pos)) {
                activeRendering.put(pos, detectedBlocks.get(pos));
                break;
              }
            }
          } else {
            break;
          }
        }
      }
    });
  }

  private static void cleanupRenderEntries(PlayerEntity player) {
    MiningGoggles.EXECUTORS.submit(() -> {
      for (BlockPos pos : activeRendering.keySet()) {
        if (!isValidPos(player, pos)) {
          activeRendering.remove(pos);
        }
      }
    });
  }

  public static boolean isValidPos(PlayerEntity player, BlockPos pos) {
    BlockState state = player.level.getBlockState(pos);
    List<String> names = getBlockNames(state);
    MiningGogglesCollector collector = ((MiningGogglesCollector) player.inventory.armor.get(
        3).getItem());
    for (String name : names) {
      int wavelength = OreConfigLoader.get(name);
      if (wavelength != -1 && withinRange(player, player.inventory.armor.get(3), pos,
          name, wavelength)
          && collector.canSeeBlock(player, player.inventory.armor.get(3), pos,
          wavelength)) {
        return true;
      }
    }
    return false;
  }

  public static List<String> getBlockNames(BlockState block) {
    ItemStack itemBlock = new ItemStack(
        Item.BY_BLOCK.get(block.getBlock()), 1);
    if (!itemBlock.equals(ItemStack.EMPTY)) {
      List<String> ores = new ArrayList<>();
      for (ResourceLocation loc : ItemTags.getAllTags()
          .getMatchingTags(itemBlock.getItem())) {
        ores.add(loc.getNamespace() + ":" + loc.getPath());
      }
      return ores;
    }
    return new ArrayList<>();
  }

  private static boolean withinRange(PlayerEntity player, ItemStack stack, BlockPos pos,
      String ore, int wavelength) {
    int waveLength = OreConfigLoader.get(ore);
    MiningGogglesCollector collector = ((MiningGogglesCollector) player.inventory.armor.get(
        3).getItem());
    double range = collector.maxRange(stack);
    range = getBlockRadius(range, waveLength,
        WavelengthCalculator.computeWavelength(
            collector.getWavelength(player.inventory.armor.get(3), 0)));
    if (range == -1 || range == 0) {
      range = collector.maxRange(player.inventory.armor.get(3));
      range = getBlockRadius(range, waveLength,
          WavelengthCalculator.computeWavelength(
              ((MiningGogglesCollector) player.inventory.armor.get(3)
                  .getItem()).getWavelength(player.inventory.armor.get(3), 1)));
    }
    if (pos.closerThan(new Vector3i(player.getX(), player.getY(), player.getZ()),
        range)) {
      BlockState state = player.level.getBlockState(pos);
      if (!state.getBlock().is(Blocks.AIR)) {
        return true;
      }
    }
    return false;
  }

  public static int getBlockRadius(double maxRange, int wavelength,
      int[] minMaxHelmetWavelength) {
    if (minMaxHelmetWavelength[0] == -1 || minMaxHelmetWavelength[1] == -1) {
      return -1;
    }
    double middle = ((double) minMaxHelmetWavelength[0] + minMaxHelmetWavelength[1]) / 2;
    double variation = ((double) minMaxHelmetWavelength[1] - minMaxHelmetWavelength[0])
        / minMaxHelmetWavelength[1];
    // No distance degradation
    if (wavelength == middle
        || wavelength < (middle * (1 + FUZZY_RANGE_BEST)) && wavelength > (middle * (1
        - FUZZY_RANGE_BEST))) {
      return (int) maxRange;
    }
    // Dynamic Range
    int diff = (int) Math.abs(middle - wavelength);
    double adjustedRange = ((1 - variation) * diff) / 10;
    adjustedRange = (maxRange + adjustedRange) / maxRange;
    if (adjustedRange < (maxRange * FUZZY_RANGE_LOW)) {
      return 0;
    }
    return (int) adjustedRange;
  }

  private static NonBlockingHashMap<BlockPos, Float[]> collectDetectedBlocks(
      PlayerEntity player, ItemStack stack, boolean rescan) {
    if (stack.getItem() instanceof MiningGogglesCollector) {
      return ((MiningGogglesCollector) stack.getItem()).findBlocks(player, stack, rescan);
    }
    return new NonBlockingHashMap<>();
  }

  public void drawBoundingBoxAtBlockPos(MatrixStack matrixStackIn, AxisAlignedBB aabbIn,
      float red, float green, float blue, float alpha, BlockPos pos) {
    Vector3d cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    double camX = cam.x(), camY = cam.y(), camZ = cam.z();
    matrixStackIn.pushPose();
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    drawShapeOutline(matrixStackIn, VoxelShapes.create(aabbIn), pos.getX() - camX,
        pos.getY() - camY, pos.getZ() - camZ, red, green, blue, alpha);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    matrixStackIn.popPose();
  }

  private void drawShapeOutline(MatrixStack matrixStack, VoxelShape voxelShape,
      double originX, double originY, double originZ, float red, float green, float blue,
      float alpha) {
    Matrix4f matrix4f = matrixStack.last().pose();
    IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().renderBuffers()
        .bufferSource();
    IVertexBuilder bufferIn = renderTypeBuffer.getBuffer(RenderType.lines());
    voxelShape.forAllEdges((xMin, yMin, zMin, xMax, yMax, zMax) -> {
      bufferIn.vertex(matrix4f, (float) (xMin + originX), (float) (yMin + originY),
          (float) (zMin + originZ)).color(red, green, blue, alpha).endVertex();
      bufferIn.vertex(matrix4f, (float) (xMax + originX), (float) (yMax + originY),
          (float) (zMax + originZ)).color(red, green, blue, alpha).endVertex();
    });
    renderTypeBuffer.endBatch(RenderType.lines());
  }

  public static List<BlockPos> generateList(int minX, int minY, int minZ, int maxX,
      int maxY, int maxZ) {
    List<BlockPos> locations = new ArrayList<>();
    for (int x = minX; x < maxX; x++) {
      for (int y = minY; y < maxY; y++) {
        for (int z = minZ; z < maxZ; z++) {
          locations.add(new BlockPos(x, y, z));
        }
      }
    }
    return locations;
  }
}
