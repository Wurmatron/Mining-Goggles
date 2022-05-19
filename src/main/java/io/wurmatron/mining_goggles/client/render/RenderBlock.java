package io.wurmatron.mining_goggles.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class RenderBlock {

  @SubscribeEvent
  public void onRenderWorldEvent(RenderWorldLastEvent e) {
    World world = Minecraft.getInstance().level;
    final GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
    gameRenderer.resetProjectionMatrix(e.getProjectionMatrix());
    PlayerEntity player = Minecraft.getInstance().player;
    for (int x = -32; x < 32; x++) {
      for (int z = -32; z < 32; z++) {
        for (int y = -32; y < 32; y++) {
          BlockPos pos = new BlockPos(player.getX() + x, player.getY() + y,
              player.getZ() + z);
          BlockState state = world.getBlockState(pos);
          if (!state.is(Blocks.AIR) && state.getBlock().getRegistryName().getPath()
              .contains("ore")) {
            drawBoundingBoxAtBlockPos(e.getMatrixStack(),
                state.getShape(world, pos, ISelectionContext.of(player)).bounds(), 1.0F,
                0.0F, 0.0F, 1.0F, pos);
          }
        }
      }
    }
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
