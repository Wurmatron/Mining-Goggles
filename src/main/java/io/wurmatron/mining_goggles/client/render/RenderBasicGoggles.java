package io.wurmatron.mining_goggles.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.config.OreConfigLoader;
import io.wurmatron.mining_goggles.items.ItemMiningGoggles;
import io.wurmatron.mining_goggles.items.ItemMiningGogglesUpgraded;
import io.wurmatron.mining_goggles.items.MiningItems;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_1;
import io.wurmatron.mining_goggles.utils.WavelengthCalculator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
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
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;
import org.cliffc.high_scale_lib.NonBlockingHashSet;
import org.lwjgl.opengl.GL11;

public class RenderBasicGoggles {

  public static NonBlockingHashSet<BlockPos> detectedBlocks = new NonBlockingHashSet<>();
  public static NonBlockingHashSet<BlockPos> activeRender = new NonBlockingHashSet<>();

  public static int MAX_RADIUS = 32;
  public static int MAX_GROWTH_PER_TICK = 5;
  public static AxisAlignedBB box = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

  @SubscribeEvent
  public void onRenderWorldEvent(RenderWorldLastEvent e) {
    if (activeRender.size() > 0) {
      final GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
      gameRenderer.resetProjectionMatrix(e.getProjectionMatrix());
      for (BlockPos p : activeRender) {
        float[] color = getColor(p);
        drawBoundingBoxAtBlockPos(e.getMatrixStack(), box, color[0], color[1], color[2],
            .75f, p);
      }
    }
  }

  public static void addNewRendering(PlayerEntity player) {
    ItemStack stack = player.inventory.armor.get(3);
    if (!stack.getItem().getItem().equals(MiningItems.goggles) && !stack.getItem()
        .getItem().equals(MiningItems.gogglesUpgraded)) {
      activeRender.clear();
      return;
    }
    damageCrystals(player.level.random, stack);
    if (activeRender.size() < detectedBlocks.size()) {
      for (int x = 0; x < MAX_GROWTH_PER_TICK; x++) {
        int currentIndex = activeRender.size() + x;
        if (detectedBlocks.size() > currentIndex) {
          for (BlockPos exists : detectedBlocks) {
            if (!activeRender.contains(exists)) {
              activeRender.add(exists);
              break;
            }
          }
        } else {
          break;
        }
      }
    }
  }

  static int maxRadius = MAX_RADIUS;
  int count = 0;
  int renderUpdater = 0;

  private static void damageCrystals(Random rand, ItemStack stack) {
    ItemStackHandler stackHandler = null;
    int chance = 2;
    if (stack.getItem().equals(MiningItems.goggles)) {
      stackHandler = ItemMiningGoggles.getItemStackGoggles_1(stack);
    } else if (stack.getItem().equals(MiningItems.gogglesUpgraded)) {
      stackHandler = ItemMiningGogglesUpgraded.getItemStackGoggles_2(stack);
      chance = 4;
    }
    if (stackHandler != null) {
      for (int count = 0; count < stackHandler.getSlots(); count++) {
        damageCrystal(rand, stackHandler, count, chance);
      }
    }
  }

  // TODO Server support
  // TODO Enable / disable ?
  private static void damageCrystal(Random rand, ItemStackHandler stackHandler, int index,
      int chance) {
    if (!stackHandler.getStackInSlot(index).isEmpty() && rand.nextInt(chance) == 0) {
      stackHandler.getStackInSlot(index)
          .setDamageValue(stackHandler.getStackInSlot(index).getDamageValue() + 1);
      if (stackHandler.getStackInSlot(index).getDamageValue() >= stackHandler.getStackInSlot(index).getMaxDamage()) {
        stackHandler.setStackInSlot(index, ItemStack.EMPTY);
      }
    }
  }

  @SubscribeEvent
  public void onClientTick(PlayerTickEvent e) {
    if (e.side.isClient()) {
      if (e.player.inventory.armor.get(3).getItem().equals(MiningItems.goggles)
          || e.player.inventory.armor.get(3).getItem()
          .equals(MiningItems.gogglesUpgraded)) {
        if (renderUpdater >= 20) {
          addNewRendering(e.player);
          renderUpdater = 0;
        } else {
          renderUpdater++;
        }
        if (count >= 100 || detectedBlocks.size() == 0 && count <= 60) {
          count = 0;
          // 1st Half
          MiningGoggles.EXECUTORS.submit(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            World world = player.level;
            // Remove old entries
            removeOldEntries(player, world);
            MiningGoggles.EXECUTORS.submit(() -> {
              List<BlockPos> fullBlockList = generateList(
                  (int) (player.getX() - maxRadius),
                  (int) (player.getY() - maxRadius), (int) (player.getZ() - maxRadius),
                  (int) (player.getX() + maxRadius), (int) (player.getY() + maxRadius),
                  (int) (player.getZ() + maxRadius));
              BlockPos[] subA = Arrays.copyOfRange(fullBlockList.toArray(new BlockPos[0]),
                  0, fullBlockList.size() / 2);
              BlockPos[] subB = Arrays.copyOfRange(fullBlockList.toArray(new BlockPos[0]),
                  fullBlockList.size() / 2, fullBlockList.size());
              // Test Group A
              MiningGoggles.EXECUTORS.submit(() -> {
                for (BlockPos a : subA) {
                  validatePos(player, a);
                }
              });
              // Test Group B
              MiningGoggles.EXECUTORS.submit(() -> {
                for (BlockPos b : subB) {
                  validatePos(player, b);
                }
              });
            });
          });
        } else {
          count++;
        }
      } else {
        activeRender.clear();
      }
    }
  }

  private static List<BlockPos> generateList(int minX, int minY, int minZ, int maxX,
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

  private static void removeOldEntries(PlayerEntity player, World world) {
    MiningGoggles.EXECUTORS.submit(() -> {
      for (BlockPos pos : detectedBlocks) {
        if (!validatePos(player, pos)) {
          detectedBlocks.remove(pos);
          activeRender.remove(pos);
        }
      }
    });
  }

  private static boolean withinRange(PlayerEntity player, BlockPos pos, String name) {
    int waveLength = OreConfigLoader.get(name);
    double range = getRange(player.inventory.armor.get(3));
    range = getBlockRadius(range, waveLength,
        WavelengthCalculator.computeWavelength(
            getWavelength(player.inventory.armor.get(3), 0)));
    if (range == -1 || range == 0) {
      range = getRange(player.inventory.armor.get(3));
      range = getBlockRadius(range, waveLength,
          WavelengthCalculator.computeWavelength(
              getWavelength(player.inventory.armor.get(3), 1)));
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

  public static int getRange(ItemStack stack) {
    if (stack.getItem().equals(MiningItems.goggles)) {
      return ItemMiningGoggles.getMaxRange(stack);
    }
    if (stack.getItem().equals(MiningItems.gogglesUpgraded)) {
      return ItemMiningGogglesUpgraded.getMaxRange(stack);
    }
    return 0;
  }

  public static int[][] getWavelength(ItemStack stack, int side) {
    if (stack.getItem().equals(MiningItems.goggles)) {
      return ItemMiningGoggles.getWavelength(stack, side);
    }
    if (stack.getItem().equals(MiningItems.gogglesUpgraded)) {
      return ItemMiningGogglesUpgraded.getWavelength(stack, side);
    }
    return new int[0][0];
  }

  public static boolean canSeeWavelength(BlockPos pos, PlayerEntity player, String name) {
    int wavelength = -1;
    wavelength = OreConfigLoader.get(name);
    if (wavelength != -1) {
      int[] visibleLeft = WavelengthCalculator.computeWavelength(
          getWavelength(player.inventory.armor.get(3), 0));
      if (wavelength >= visibleLeft[0] && wavelength <= visibleLeft[1]) {
        return true;
      }
      int[] visibleRight = WavelengthCalculator.computeWavelength(
          getWavelength(player.inventory.armor.get(3), 1));
      if (wavelength >= visibleRight[0] && wavelength <= visibleRight[1]) {
        return true;
      }
    }
    return false;
  }

  public static double BEST_RANGE_FUZZY = .2;
  public static double MIN_PERC_RANGE = .3;

  public static int getBlockRadius(double maxRange, int wavelength, int[] minMaxHelmet) {
    if (minMaxHelmet[0] == -1 || minMaxHelmet[1] == -1) {
      return -1;
    }
    double mid = ((double) minMaxHelmet[0] + minMaxHelmet[1]) / 2;
    double variation = (double) (minMaxHelmet[1] - minMaxHelmet[0]) / minMaxHelmet[1];
    // No Distance degradation
    if (wavelength == mid || wavelength < (mid * (1 + BEST_RANGE_FUZZY)) && wavelength > (
        mid
            * (1 - BEST_RANGE_FUZZY))) {
      return (int) maxRange;
    }
    // Dynamic
    int diff = (int) Math.abs(mid - wavelength);
    double adjustedRange = ((1 - variation) * diff) / 10;
    adjustedRange = (maxRange + adjustedRange) / maxRange;
    if (adjustedRange < (maxRange * MIN_PERC_RANGE)) {
      return 0;
    }
    return (int) adjustedRange;
  }

  private static boolean validatePos(PlayerEntity player, BlockPos pos) {
    BlockState state = player.level.getBlockState(pos);
    if (!state.getBlock().is(Blocks.AIR)) {
      String[] names = getBlockNames(state);
      for (String name : names) {
        if (canSeeWavelength(pos, player, name) && withinRange(player, pos, name)) {
          detectedBlocks.add(pos);
          return true;
        }
      }
    }
    return false;
  }

  private static String[] getBlockNames(BlockState block) {
    ItemStack itemBlock = new ItemStack(
        Item.BY_BLOCK.get(block.getBlock()), 1);
    if (!itemBlock.equals(ItemStack.EMPTY)) {
      List<String> ores = new ArrayList<>();
      for (ResourceLocation loc : ItemTags.getAllTags()
          .getMatchingTags(itemBlock.getItem())) {
        ores.add(loc.getNamespace() + ":" + loc.getPath());
      }
      return ores.toArray(new String[0]);
    }
    return new String[0];
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

  public static float[] getColor(BlockPos pos) {
    return new float[]{1.0f, .3f, .3f};
  }
}
