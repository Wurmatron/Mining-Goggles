package io.wurmatron.mining_goggles.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.config.OreConfigLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.cliffc.high_scale_lib.NonBlockingHashSet;
import org.lwjgl.opengl.GL11;

public class RenderBlock {

  public static NonBlockingHashSet<BlockPos> detectedBlocks = new NonBlockingHashSet<>();
  public static NonBlockingHashSet<BlockPos> activeRender = new NonBlockingHashSet<>();

  public static int MAX_RADIUS = 32;
  public static int MAX_GROWTH_PER_TICK = 5;
  public static AxisAlignedBB box = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

  @SubscribeEvent
  public void onRenderWorldEvent(RenderWorldLastEvent e) {
    final GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
    gameRenderer.resetProjectionMatrix(e.getProjectionMatrix());
    for (BlockPos p : activeRender) {
      drawBoundingBoxAtBlockPos(e.getMatrixStack(), box, .75F, 0.25F, .25F, 1.0F, p);
    }
  }

  public static void addNewRendering() {
//    MiningGoggles.EXECUTORS.submit(() -> {
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
//    });
  }

  static int maxRadius = MAX_RADIUS;
  int count = 0;
  int renderUpdater = 0;

  @SubscribeEvent
  public void onClientTick(PlayerTickEvent e) {
    if (e.side.isClient()) {
      if (renderUpdater >= 20) {
        addNewRendering();
        renderUpdater = 0;
      } else {
        renderUpdater++;
      }
      if (count >= 100 || detectedBlocks.size() == 0 && count <= 5) {
        count = 0;
        // 1st Half
        MiningGoggles.EXECUTORS.submit(() -> {
          PlayerEntity player = Minecraft.getInstance().player;
          World world = player.level;
          // Remove old entries
          removeOldEntries(player, world);
          MiningGoggles.EXECUTORS.submit(() -> {
            List<BlockPos> fullBlockList = generateList((int) (player.getX() - maxRadius),
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
        if (!withinRange(player, pos)) {
          detectedBlocks.remove(pos);
          activeRender.remove(pos);
        }
      }
    });
  }

  private static boolean withinRange(PlayerEntity player, BlockPos pos) {
    if (pos.closerThan(new Vector3i(player.getX(), player.getY(), player.getZ()),
        maxRadius)) {
      BlockState state = player.level.getBlockState(pos);
      if (!state.getBlock().is(Blocks.AIR)) {
        return true;
      }
    }
    return false;
  }

  private static void validatePos(PlayerEntity player, BlockPos pos) {
    BlockState state = player.level.getBlockState(pos);
    if (!state.getBlock().is(Blocks.AIR)) {
      String[] names = getBlockNames(state);
      for (String name : names) {
        if (name.contains("forge:ores") && withinRange(player, pos)) {
          add(name, pos);
        }
      }
    }
  }

  private static void add(String name, BlockPos pos) {
    int wavelength = OreConfigLoader.get(name);
    if (wavelength != -1) {
      detectedBlocks.add(pos);
    }
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


}
