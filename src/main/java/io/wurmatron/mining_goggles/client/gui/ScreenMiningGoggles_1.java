package io.wurmatron.mining_goggles.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wurmatron.mining_goggles.inventory.ContainerCrystalBag;
import io.wurmatron.mining_goggles.inventory.ContainerMiningGoggles_1;
import io.wurmatron.mining_goggles.items.ItemCrystal;
import io.wurmatron.mining_goggles.items.ItemMiningGoggles;
import io.wurmatron.mining_goggles.items.MiningItems;
import io.wurmatron.mining_goggles.utils.WavelengthCalculator;
import java.awt.Color;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenMiningGoggles_1 extends ContainerScreen<ContainerMiningGoggles_1> {

  private static final ResourceLocation TEXTURE = new ResourceLocation("mininggoggles",
      "textures/gui/goggles_1.png");

  public static final float BAG_LABEL_YPOS = -2;
  public static final float PLAYER_LABEL_XPOS = 8;
  public static final float PLAYER_LABEL_DISTANCE_FROM_BOTTOM = 97;

  public ScreenMiningGoggles_1(ContainerMiningGoggles_1 container,
      PlayerInventory playerInv,
      ITextComponent title) {
    super(container, playerInv, title);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY,
      float partialTicks) {
    this.renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    super.renderTooltip(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
    float PLAYER_LABEL_YPOS = getYSize() - PLAYER_LABEL_DISTANCE_FROM_BOTTOM;
    font.draw(matrixStack, this.inventory.getDisplayName(), PLAYER_LABEL_XPOS,
        PLAYER_LABEL_YPOS, Color.darkGray.getRGB());
    // Render Wavelength
    NonNullList<ItemStack> items = this.getMenu().getItems();
    int[] leftWavelength = WavelengthCalculator.computeWavelength(
        new int[][]{ItemCrystal.getWavelength(items.get(36)),
            ItemCrystal.getWavelength(items.get(37))});
    int[] rightWavelength = WavelengthCalculator.computeWavelength(
        new int[][]{ItemCrystal.getWavelength(items.get(38)),
            ItemCrystal.getWavelength(items.get(39))});
    RenderSystem.pushMatrix();
    RenderSystem.scalef(.75f, .75f, .75f);
    // Left
    font.draw(matrixStack, display(leftWavelength[0]), 10, 22, Color.RED.getRGB());
    font.draw(matrixStack, display(leftWavelength[1]), 83, 22, Color.RED.getRGB());
    long avg = (leftWavelength[0] + leftWavelength[1]) / 2;
    font.draw(matrixStack, display(avg), 48, -2, Color.RED.getRGB());
    // Right
    font.draw(matrixStack, display(rightWavelength[0]), 130, 22, Color.BLUE.getRGB());
    font.draw(matrixStack, display(rightWavelength[1]), 203, 22, Color.BLUE.getRGB());
    avg = (rightWavelength[0] + rightWavelength[1]) / 2;
    font.draw(matrixStack, display(avg), 165, -2, Color.BLUE.getRGB());
    RenderSystem.scalef(1f, 1f, 1f);
    RenderSystem.popMatrix();
    RenderSystem.pushMatrix();
    RenderSystem.scalef(.6f, .6f, .6f);
    // Range
    font.draw(matrixStack, new TranslationTextComponent("stat.max_range.name").append(
            " " + ItemMiningGoggles.getMaxRange(this.inventory.armor.get(3))), 107, 48,
        Color.BLACK.getRGB());
    font.draw(matrixStack, new TranslationTextComponent("stat.optimal_range.name").append(
        " " + (int) ((double) ItemMiningGoggles.getMaxRange(this.inventory.armor.get(3))
            * .3)), 107, 58, Color.BLACK.getRGB());
    RenderSystem.scalef(1f, 1f, 1f);
    RenderSystem.popMatrix();
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

  private static String display(long num) {
    if (num > -1) {
      return "" + num;
    } else {
      return "n/a";
    }
  }
}
