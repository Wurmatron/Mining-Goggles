package io.wurmatron.mining_goggles.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.inventory.ContainerFilter;
import io.wurmatron.mining_goggles.network.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.NBTTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;

import static net.minecraftforge.fml.client.gui.GuiUtils.drawTexturedModalRect;

public class ScreenFilterDigital extends ContainerScreen<ContainerFilter> {

    public final static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(MiningGoggles.MODID, "textures/gui/gogglesfilter.png");
    private static final int xSize = 217;
    private static final int ySize = 153;

    private GuiColorFilter[] filters = new GuiColorFilter[16];
    private int startingIndex = 0;
    private ItemStack stack;

    public ScreenFilterDigital(ContainerFilter containerFilter, PlayerInventory inventory, ITextComponent title) {
        super(containerFilter, inventory, title);
        stack = inventory.getSelected();
    }

    @Override
    protected void init() {
        super.init();
        if (filters == null || filters[0] == null) {
            int x = 0;
            for (int index = 0; index < 16; index++) {
                GuiColorFilter entry = new GuiColorFilter(this.font, new TranslationTextComponent("Title"));
                entry.init(Minecraft.getInstance(), width, height);
                if (stack.hasTag()) {
                    CompoundNBT colorNBT = stack.getTagElement("color_" + index);
                    if (colorNBT != null) {
                        String filter = colorNBT.getString("filter");
                        boolean active = colorNBT.getInt("active") == 1;
                        entry.text.setValue(filter);
                        entry.text.setEditable(true);
                        entry.text.setTextColor(Color.WHITE.getRGB());
                    } else {
                        colorNBT = new CompoundNBT();
                        colorNBT.putString("filter", "");
                        colorNBT.putInt("active", 0);
                    }
                } else {
                    stack.setTag(new CompoundNBT());
                }
                this.filters[index] = entry;
                this.children.add(entry.text);
                x++;
            }
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int edgeSpacingX = (this.width - xSize) / 2;
        int edgeSpacingY = (this.height - ySize) / 2;
        this.blit(stack, edgeSpacingX, edgeSpacingY, 0, 0, this.xSize, this.ySize);
        // Bar
        drawTexturedModalRect(((width - 218) / 2) + 199,
                (((height - 154) / 2) + 6) + (int) (((142f / 16) * startingIndex)), 219, 6, 12, 53, 1);
        // Boxes
        for (int index = startingIndex; index < startingIndex + 6; index++) {
            filters[index].text.x = edgeSpacingX + 29;
            filters[index].text.y = edgeSpacingY + 8 + (24 * (index - startingIndex));
            filters[index].draw(this.minecraft);
        }
        for (int index = startingIndex; index < startingIndex + 6; index++) {
            filters[index].text.render(stack, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float v, int i, int i1) {
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
        CompoundNBT stackNBT = stack.getTag();
        for (int index = 0; index < 16; index++) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("filter", filters[index].text.getValue());
            nbt.putInt("active", filters[index].enabled ? 1 : 0);
            stackNBT.put("color_" + index, nbt);
        }
        stack.setTag(stackNBT);
        MiningGoggles.NETWORK.sendToServer(new PacketUtils.UpdateHelmet(stack));
    }
}
