package io.wurmatron.mining_goggles.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.inventory.ContainerFilter;
import io.wurmatron.mining_goggles.items.ItemMiningGogglesDigital;
import io.wurmatron.mining_goggles.network.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

import static net.minecraftforge.client.gui.GuiUtils.drawTexturedModalRect;

public class ScreenFilterDigital extends ContainerScreen<ContainerFilter> {

    public final static ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MiningGoggles.MODID, "textures/gui/gogglesfilter.png");
    private static final int xSize = 217;
    private static final int ySize = 153;

    private GuiColorFilter[] filters = new GuiColorFilter[16];
    private int startingIndex = 0;
    private ItemStack stack;

    public ScreenFilterDigital(ContainerFilter containerFilter, Inventory inventory, TextComponent title) {
        super(containerFilter, inventory, title);
        stack = inventory.getSelected();
    }

    @Override
    protected void init() {
        super.init();
        if (filters == null || filters[0] == null) {
            int x = 0;
            for (int index = 0; index < 16; index++) {
                GuiColorFilter entry = new GuiColorFilter(this.font, new TranslatableComponent("Title"), index);
                entry.init(Minecraft.getInstance(), width, height);
                if (stack.hasTag()) {
                    CompoundTag colorNBT = stack.getTagElement("color_" + index);
                    if (colorNBT != null) {
                        String filter = colorNBT.getString("filter");
                        boolean active = colorNBT.getInt("active") == 1;
                        entry.text.setValue(filter);
                        entry.text.setEditable(true);
                        entry.text.setTextColor(Color.WHITE.getRGB());
                    } else {
                        colorNBT = new CompoundTag();
                        colorNBT.putString("filter", "");
                        colorNBT.putInt("active", 0);
                    }
                } else {
                    stack.setTag(new CompoundTag());
                }
                this.filters[index] = entry;
                this.children.add(entry.text);
                x++;
            }
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        this.minecraft.getTextureManager().bindForSetup(BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int edgeSpacingX = (this.width - xSize) / 2;
        int edgeSpacingY = (this.height - ySize) / 2;
        this.blit(stack, edgeSpacingX, edgeSpacingY, 0, 0, xSize, ySize);
        // Bar
        drawTexturedModalRect(stack, ((width - 218) / 2) + 199,
                (((height - 154) / 2) + 6) + (int) (((142f / 16) * startingIndex)), 219, 6, 12, 53, 1);
        // Boxes
        for (int index = startingIndex; index < startingIndex + 6; index++) {
            filters[index].text.x = edgeSpacingX + 29;
            filters[index].text.y = edgeSpacingY + 8 + (24 * (index - startingIndex));
            filters[index].draw(stack, this.minecraft);
        }
        for (int index = startingIndex; index < startingIndex + 6; index++) {
            filters[index].text.render(stack, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void renderBg(PoseStack PoseStack, float v, int i, int i1) {
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        InputMappings.Input mouseKey = InputMappings.getKey(p_231046_1_, p_231046_2_);
        if (!(mouseKey.getValue() == 69))
            return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
        return false;
    }

    @Override
    public boolean mouseScrolled(double a, double b, double direction) {
        if (direction == 1) {
            moveUp();
        } else if (direction == -1) {
            moveDown();
        }
        return super.mouseScrolled(a, b, direction);
    }

    @Override
    public boolean mouseReleased(double x, double y, int type) {
        if (x <= this.leftPos + 6 && x <= this.leftPos + 28) {
            int yx = (int) (y - (this.topPos + 8)) / 24;
            filters[yx + startingIndex].enabled = !filters[yx + startingIndex].enabled;
        }
        return super.mouseReleased(x, y, type);
    }

    private void moveDown() {
        startingIndex++;
        if (startingIndex + 6 >= 16) {
            startingIndex = 10;
        }
    }

    private void moveUp() {
        startingIndex--;
        if (startingIndex < 0) {
            startingIndex = 0;
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        CompoundTag stackNBT = stack.getTag();
        for (int index = 0; index < 16; index++) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("filter", filters[index].text.getValue());
            nbt.putInt("active", filters[index].enabled ? 1 : 0);
            stackNBT.put("color_" + index, nbt);
        }
        stack.setTag(stackNBT);
        MiningGoggles.NETWORK.sendToServer(new PacketUtils.UpdateHelmet(stack));
        ItemMiningGogglesDigital.detectedBlocks.clear(); // Resets Client upon edit
    }
}
