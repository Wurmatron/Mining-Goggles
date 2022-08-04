package io.wurmatron.mining_goggles.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wurmatron.mining_goggles.inventory.ContainerTuningFork;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenTuningFork extends ContainerScreen<ContainerTuningFork> {

  private static final ResourceLocation TEXTURE = new ResourceLocation("mininggoggles",
      "textures/gui/tuning_fork.png");

  public static final float PLAYER_LABEL_XPOS = 8;
  public static final float PLAYER_LABEL_DISTANCE_FROM_BOTTOM = 87;

  public ScreenTuningFork(ContainerTuningFork container,
      PlayerInventory playerInv,
      ITextComponent title) {
    super(container, playerInv, title);
    this.imageWidth = 176;
    this.imageHeight = 142;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY,
      float partialTicks) {
    this.renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    super.renderTooltip(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {}

  @Override
  protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX,
      int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bind(TEXTURE);
    int edgeSpacingX = (this.width - this.getXSize()) / 2;
    int edgeSpacingY = (this.height - this.getXSize()) / 2;
    blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, getXSize(), getYSize());
  }
}
