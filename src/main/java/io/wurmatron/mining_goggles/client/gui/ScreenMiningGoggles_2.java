package io.wurmatron.mining_goggles.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wurmatron.mining_goggles.inventory.ContainerMiningGoggles_2;
import io.wurmatron.mining_goggles.items.ItemCrystal;
import io.wurmatron.mining_goggles.items.ItemMiningGogglesUpgraded;
import io.wurmatron.mining_goggles.utils.WavelengthCalculator;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

public class ScreenMiningGoggles_2 extends ContainerScreen<ContainerMiningGoggles_2> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("mininggoggles",
            "textures/gui/goggles_2.png");

    public static final float PLAYER_LABEL_XPOS = 8;
    public static final float PLAYER_LABEL_DISTANCE_FROM_BOTTOM = 87;

    public ScreenMiningGoggles_2(ContainerMiningGoggles_2 container,
                                 Inventory playerInv,
                                 TextComponent title) {
        super(container, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 185;
    }

    @Override
    public void render(PoseStack PoseStack, int mouseX, int mouseY,
                       float partialTicks) {
        this.renderBackground(PoseStack);
        super.render(PoseStack, mouseX, mouseY, partialTicks);
        super.renderTooltip(PoseStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack PoseStack, int mouseX, int mouseY) {
        float PLAYER_LABEL_YPOS = getYSize() - PLAYER_LABEL_DISTANCE_FROM_BOTTOM;
        font.draw(PoseStack, this.inventory.getDisplayName(), PLAYER_LABEL_XPOS,
                PLAYER_LABEL_YPOS, Color.darkGray.getRGB());
        // Render Wavelength
        NonNullList<ItemStack> items = this.getMenu().getItems();
        int[] leftWavelength = WavelengthCalculator.computeWavelength(
                new int[][]{ItemCrystal.getWavelength(items.get(36)),
                        ItemCrystal.getWavelength(items.get(37)), ItemCrystal.getWavelength(items.get(38))});
        int[] rightWavelength = WavelengthCalculator.computeWavelength(
                new int[][]{ItemCrystal.getWavelength(items.get(39)),
                        ItemCrystal.getWavelength(items.get(40)), ItemCrystal.getWavelength(items.get(41))});
        RenderSystem.pushMatrix();
        RenderSystem.scalef(.75f, .75f, .75f);
        // Left
        font.draw(PoseStack, display(leftWavelength[0]), 10, 34, Color.RED.getRGB());
        font.draw(PoseStack, display(leftWavelength[1]), 83, 34, Color.RED.getRGB());
        long avg = (leftWavelength[0] + leftWavelength[1]) / 2;
        font.draw(PoseStack, display(avg), 48, 10, Color.RED.getRGB());
        // Right
        font.draw(PoseStack, display(rightWavelength[0]), 130, 34, Color.BLUE.getRGB());
        font.draw(PoseStack, display(rightWavelength[1]), 203, 34, Color.BLUE.getRGB());
        avg = (rightWavelength[0] + rightWavelength[1]) / 2;
        font.draw(PoseStack, display(avg), 165, 10, Color.BLUE.getRGB());
        RenderSystem.scalef(1f, 1f, 1f);
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        RenderSystem.scalef(.6f, .6f, .6f);
        // Range
        font.draw(PoseStack, new TranslationTextComponent("stat.max_range.name").append(
                        " " + ItemMiningGogglesUpgraded.getMaxRange(this.inventory.armor.get(3))), 107, 68,
                Color.BLACK.getRGB());
        font.draw(PoseStack, new TranslationTextComponent("stat.optimal_range.name").append(
                " " + (int) ((double) ItemMiningGogglesUpgraded.getMaxRange(this.inventory.armor.get(3))
                        * .3)), 107, 78, Color.BLACK.getRGB());
        RenderSystem.scalef(1f, 1f, 1f);
        RenderSystem.popMatrix();
    }

    @Override
    protected void renderBg(PoseStack PoseStack, float partialTicks, int mouseX,
                            int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(TEXTURE);
        int edgeSpacingX = (this.width - this.getXSize()) / 2;
        int edgeSpacingY = (this.height - this.getXSize()) / 2;
        blit(PoseStack, edgeSpacingX, edgeSpacingY, 0, 0, getXSize(), getYSize());
    }

    private static String display(long num) {
        if (num > -1) {
            return "" + num;
        } else {
            return "n/a";
        }
    }
}
