package io.wurmatron.mining_goggles.client.gui;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import static io.wurmatron.mining_goggles.client.gui.ScreenFilterDigital.BACKGROUND_TEXTURE;
import static net.minecraftforge.client.gui.GuiUtils.drawTexturedModalRect;

public class GuiColorFilter extends Screen {

    public EditBox text;
    private int ID;
    public boolean enabled = true;

    protected GuiColorFilter(Font font, Component title, int ID) {
        super(title);
        this.text = new EditBox(font, 0, 0, 162, 17, new TranslatableComponent("Nep"));
        this.text.setMaxLength(500);
        this.ID = ID;
    }

    public void draw(PoseStack stack, Minecraft mc) {
        mc.getTextureManager().bindForSetup(BACKGROUND_TEXTURE);
        drawTexturedModalRect(stack, text.x - 22, text.y - 2, 1, 158, 187, 22, 0);
        drawTexturedModalRect(stack, text.x - 18, text.y + 2, ID * 14, 183, 14, 14, 0);
        if (!enabled) {
            drawTexturedModalRect(stack, text.x - 20, text.y, 0, 198, 18, 18, 0);
        } else {
            drawTexturedModalRect(stack, text.x - 18, text.y + 2, ID * 14, 183, 14, 14, 0);
        }
    }
}
