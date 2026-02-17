package io.wurmatron.mining_goggles.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wurmatron.mining_goggles.inventory.ContainerTuningFork;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ScreenTuningFork extends ContainerScreen<ContainerTuningFork> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("mininggoggles",
            "textures/gui/tuning_fork.png");

    public static final float PLAYER_LABEL_XPOS = 8;
    public static final float PLAYER_LABEL_DISTANCE_FROM_BOTTOM = 87;

    public ScreenTuningFork(ContainerTuningFork container,
                            Inventory playerInv,
                            TextComponent title) {
        super(container, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 142;
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
