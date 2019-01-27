package com.wurmatron.mininggoggles.client.gui;

import com.wurmatron.mininggoggles.common.network.container.ContainerModules;
import com.wurmatron.mininggoggles.common.reference.Global;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiGoggleModules extends GuiContainer {

  /**
   * x and y size of the inventory window in pixels. Defined as float, passed as int These are used
   * for drawing the player model.
   */
  private float xSize_lo;
  private float ySize_lo;

  /**
   * ResourceLocation takes 2 parameters: ModId, path to texture at the location:
   * "src/minecraft/assets/modid/"
   *
   * I have provided a sample texture file that works with this tutorial. Download it from
   * Forge_Tutorials/textures/gui/
   */
  private static final ResourceLocation iconLocation = new ResourceLocation(Global.MODID,
      "textures/gui/goggleModules.png");

  /**
   * The inventory to render on screen
   */
  private final ContainerModules inventory;

  public GuiGoggleModules(ContainerModules containerItem) {
    super(containerItem);
    this.inventory = containerItem;
  }


  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    super.renderHoveredToolTip(mouseX, mouseY);
    this.xSize_lo = (float) mouseX;
    this.ySize_lo = (float) mouseY;
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the items)
   */
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(iconLocation);
    int k = (this.width - this.xSize) / 2;
    int l = (this.height - this.ySize) / 2;
    this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    drawPlayerModel(k + 51, l + 75, 30, (float) (k + 51) - this.xSize_lo,
        (float) (l + 75 - 50) - this.ySize_lo, this.mc.player);
  }

  /**
   * This renders the player model in standard inventory position (in later versions of Minecraft /
   * Forge, you can simply call GuiInventory.drawEntityOnScreen directly instead of copying this
   * code)
   */
  public static void drawPlayerModel(int posX, int posY, int scale, float mouseX, float mouseY,
      EntityLivingBase ent) {
    GlStateManager.enableColorMaterial();
    GlStateManager.pushMatrix();
    GlStateManager.translate((float) posX, (float) posY, 50.0F);
    GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    float f = ent.renderYawOffset;
    float f1 = ent.rotationYaw;
    float f2 = ent.rotationPitch;
    float f3 = ent.prevRotationYawHead;
    float f4 = ent.rotationYawHead;
    GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager
        .rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
    ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
    ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
    ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
    ent.rotationYawHead = ent.rotationYaw;
    ent.prevRotationYawHead = ent.rotationYaw;
    GlStateManager.translate(0.0F, 0.0F, 0.0F);
    RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
    rendermanager.setPlayerViewY(180.0F);
    rendermanager.setRenderShadow(false);
    rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
    rendermanager.setRenderShadow(true);
    ent.renderYawOffset = f;
    ent.rotationYaw = f1;
    ent.rotationPitch = f2;
    ent.prevRotationYawHead = f3;
    ent.rotationYawHead = f4;
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
  }
}
