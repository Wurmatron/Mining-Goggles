package io.wurmatron.mining_goggles.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wurmatron.mining_goggles.inventory.ContainerCrystalBag;
import io.wurmatron.mining_goggles.items.MiningItems;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

public class ScreenCrystalBag extends AbstractContainerScreen<ContainerCrystalBag> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("mininggoggles",
            "textures/gui/crystal_bag.png");

    public static final float BAG_LABEL_YPOS = -2;
    public static final float PLAYER_LABEL_XPOS = 8;
    public static final float PLAYER_LABEL_DISTANCE_FROM_BOTTOM = 106;

    public ScreenCrystalBag(ContainerCrystalBag container, Inventory playerInv,
                            Component title) {
        super(container, playerInv, title);
    }

    @Override
    public void render(PoseStack PoseStack, int mouseX, int mouseY,
                       float partialTicks) {
        this.renderBackground(PoseStack);
        super.render(PoseStack, mouseX, mouseY, partialTicks);
        super.renderTooltip(PoseStack, mouseX, mouseY);
        this.renderLabels(PoseStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack PoseStack, int mouseX, int mouseY) {
        TranslatableComponent bagLabel = new TranslatableComponent(
                "item." + MiningItems.bag.getRegistryName().getNamespace() + "."
                        + MiningItems.bag.getRegistryName().getPath());
        float BAG_LABEL_XPOS =
                (getXSize() / 2.0F) - font.getSplitter().stringWidth(bagLabel.getString()) / 2.0F;
        font.draw(PoseStack, bagLabel, BAG_LABEL_XPOS, BAG_LABEL_YPOS,
                Color.darkGray.getRGB());
        float PLAYER_LABEL_YPOS = getYSize() - PLAYER_LABEL_DISTANCE_FROM_BOTTOM;
        font.draw(PoseStack, this.playerInventoryTitle.getString(), PLAYER_LABEL_XPOS,
                PLAYER_LABEL_YPOS, Color.darkGray.getRGB());
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
}
