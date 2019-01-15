package com.wurmatron.mininggoggles.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RenderOre {

  public final World world;
  public final BlockPos pos;
  public final EntityPlayer player;
  public TileEntity te;
  private int color;

  public RenderOre(World world, EntityPlayer player, BlockPos pos, TileEntity te, int color) {
    this.world = world;
    this.player = player;
    this.pos = pos;
    this.te = te;
    this.color = color;
  }

  public void render(float partialTicks) {
    double offsetX = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
    double offsetY = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks;
    double offsetZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;
    IBlockState state = world.getBlockState(pos);
    GlStateManager.pushMatrix();
    GlStateManager.translate(pos.getX() - offsetX, pos.getY() - offsetY, pos.getZ() - offsetZ);
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    GlStateManager.color(((color >> 16) & 0xff) / 255f,
        ((color >> 8) & 0xff) / 255f,
        ((color) & 0xff) / 255f);
    GlStateManager.disableDepth();
    AxisAlignedBB box = state.getBlock().getBoundingBox(state, world, pos);
    Tessellator tess = Tessellator.getInstance();
    BufferBuilder r = tess.getBuffer();
    r.begin(3, DefaultVertexFormats.POSITION);
    r.pos(box.minX, box.minY, box.minZ).endVertex();
    r.pos(box.maxX, box.minY, box.minZ).endVertex();
    r.pos(box.maxX, box.minY, box.maxZ).endVertex();
    r.pos(box.minX, box.minY, box.maxZ).endVertex();
    r.pos(box.minX, box.minY, box.minZ).endVertex();
    tess.draw();
    r.begin(3, DefaultVertexFormats.POSITION);
    r.pos(box.minX, box.maxY, box.minZ).endVertex();
    r.pos(box.maxX, box.maxY, box.minZ).endVertex();
    r.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    r.pos(box.minX, box.maxY, box.maxZ).endVertex();
    r.pos(box.minX, box.maxY, box.minZ).endVertex();
    tess.draw();
    r.begin(1, DefaultVertexFormats.POSITION);
    r.pos(box.minX, box.minY, box.minZ).endVertex();
    r.pos(box.minX, box.maxY, box.minZ).endVertex();
    r.pos(box.maxX, box.minY, box.minZ).endVertex();
    r.pos(box.maxX, box.maxY, box.minZ).endVertex();
    r.pos(box.maxX, box.minY, box.maxZ).endVertex();
    r.pos(box.maxX, box.maxY, box.maxZ).endVertex();
    r.pos(box.minX, box.minY, box.maxZ).endVertex();
    r.pos(box.minX, box.maxY, box.maxZ).endVertex();
    tess.draw();
    GlStateManager.enableDepth();
    GlStateManager.disableBlend();
    GlStateManager.enableTexture2D();
    GlStateManager.popMatrix();
  }

}
