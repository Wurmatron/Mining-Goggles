package io.wurmatron.mining_goggles.client.gui;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.awt.font.FontRenderContext;

import static io.wurmatron.mining_goggles.client.gui.ScreenFilterDigital.BACKGROUND_TEXTURE;
import static net.minecraftforge.client.gui.GuiUtils.drawTexturedModalRect;

public class GuiColorFilter extends Screen {

    public TextFieldWidget text;
    private int ID;
    public boolean enabled = true;

    protected GuiColorFilter(FontRenderContext font, TextComponent title, int ID) {
        super(title);
        this.text = new TextFieldWidget(font, 0, 0, 162, 17, new TranslatableComponent("Nep"));
        this.text.setMaxLength(500);
        this.ID = ID;
    }

    public void draw(MatrixStack stack, Minecraft mc) {
        mc.getTextureManager().bindForSetup(BACKGROUND_TEXTURE);
        drawTexturedModalRect(text.x - 22, text.y - 2, 1, 158, 187, 22, 0);
        drawTexturedModalRect(text.x - 18, text.y + 2, ID * 14, 183, 14, 14, 0);
        if (!enabled) {
            drawTexturedModalRect(text.x - 20, text.y, 0, 198, 18, 18, 0);
        } else {
            drawTexturedModalRect(text.x - 18, text.y + 2, ID * 14, 183, 14, 14, 0);
        }
    }
}
