package io.wurmatron.mining_goggles.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wurmatron.mining_goggles.inventory.ContainerCrystalBag;
import io.wurmatron.mining_goggles.items.MiningItems;
import java.awt.Color;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenCrystalBag extends ContainerScreen<ContainerCrystalBag> {

  private static final ResourceLocation TEXTURE = new ResourceLocation("mininggoggles",
      "textures/gui/crystal_bag.png");

  public static final float BAG_LABEL_YPOS = -2;
  public static final float PLAYER_LABEL_XPOS = 8;
  public static final float PLAYER_LABEL_DISTANCE_FROM_BOTTOM = 106;

  public ScreenCrystalBag(ContainerCrystalBag container, PlayerInventory playerInv,
      ITextComponent title) {
    super(container, playerInv, title);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY,
      float partialTicks) {
    this.renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    super.renderTooltip(matrixStack, mouseX, mouseY);
    this.renderLabels(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
    TranslationTextComponent bagLabel = new TranslationTextComponent(
        "item." + MiningItems.bag.getRegistryName().getNamespace() + "."
            + MiningItems.bag.getRegistryName().getPath());
    float BAG_LABEL_XPOS =
        (getXSize() / 2.0F) - font.getSplitter().stringWidth(bagLabel.getString()) / 2.0F;
    font.draw(matrixStack, bagLabel, BAG_LABEL_XPOS, BAG_LABEL_YPOS,
        Color.darkGray.getRGB());
    float PLAYER_LABEL_YPOS = getYSize() - PLAYER_LABEL_DISTANCE_FROM_BOTTOM;
    font.draw(matrixStack, this.inventory.getDisplayName(), PLAYER_LABEL_XPOS,
        PLAYER_LABEL_YPOS, Color.darkGray.getRGB());
  }

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
