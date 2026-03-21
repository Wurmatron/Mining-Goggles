package io.wurmatron.mining_goggles.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wurmatron.mining_goggles.inventory.ContainerMiningGoggles_1;
import io.wurmatron.mining_goggles.items.ItemCrystal;
import io.wurmatron.mining_goggles.items.ItemMiningGoggles;
import io.wurmatron.mining_goggles.utils.WavelengthCalculator;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ScreenMiningGoggles_1 extends AbstractContainerScreen<ContainerMiningGoggles_1> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("mininggoggles",
            "textures/gui/goggles_1.png");

    public static final float BAG_LABEL_YPOS = -2;
    public static final float PLAYER_LABEL_XPOS = 8;
    public static final float PLAYER_LABEL_DISTANCE_FROM_BOTTOM = 97;

    public Inventory playerInv;

    public ScreenMiningGoggles_1(ContainerMiningGoggles_1 container,
                                 Inventory playerInv,
                                 Component title) {
        super(container, playerInv, title);
        this.playerInv = playerInv;
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
        font.draw(PoseStack, this.playerInventoryTitle.getString(), PLAYER_LABEL_XPOS,
                PLAYER_LABEL_YPOS, Color.darkGray.getRGB());
        // Render Wavelength
        NonNullList<ItemStack> items = this.getMenu().getItems();
        int[] leftWavelength = WavelengthCalculator.computeWavelength(
                new int[][]{ItemCrystal.getWavelength(items.get(36)),
                        ItemCrystal.getWavelength(items.get(37))});
        int[] rightWavelength = WavelengthCalculator.computeWavelength(
                new int[][]{ItemCrystal.getWavelength(items.get(38)),
                        ItemCrystal.getWavelength(items.get(39))});
        GL11.glPushMatrix();
        GL11.glScalef(.75f, .75f, .75f);
        // Left
        font.draw(PoseStack, display(leftWavelength[0]), 10, 22, Color.RED.getRGB());
        font.draw(PoseStack, display(leftWavelength[1]), 83, 22, Color.RED.getRGB());
        long avg = (leftWavelength[0] + leftWavelength[1]) / 2;
        font.draw(PoseStack, display(avg), 48, -2, Color.RED.getRGB());
        // Right
        font.draw(PoseStack, display(rightWavelength[0]), 130, 22, Color.BLUE.getRGB());
        font.draw(PoseStack, display(rightWavelength[1]), 203, 22, Color.BLUE.getRGB());
        avg = (rightWavelength[0] + rightWavelength[1]) / 2;
        font.draw(PoseStack, display(avg), 165, -2, Color.BLUE.getRGB());
        GL11.glScalef(1f, 1f, 1f);
        GL11.glPushMatrix();
        GL11.glPopMatrix();
        GL11.glScalef(.6f, .6f, .6f);
        // Range
        font.draw(PoseStack, new TranslatableComponent("stat.max_range.name").append(
                        " " + ItemMiningGoggles.getMaxRange(playerInv.armor.get(3))), 107, 48,
                Color.BLACK.getRGB());
        font.draw(PoseStack, new TranslatableComponent("stat.optimal_range.name").append(
                " " + (int) ((double) ItemMiningGoggles.getMaxRange(playerInv.armor.get(3))
                        * .3)), 107, 58, Color.BLACK.getRGB());
        GL11.glScalef(1f, 1f, 1f);
        GL11.glPopMatrix();
    }

    @Override
    protected void renderBg(PoseStack PoseStack, float partialTicks, int mouseX,
                            int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindForSetup(TEXTURE);
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
