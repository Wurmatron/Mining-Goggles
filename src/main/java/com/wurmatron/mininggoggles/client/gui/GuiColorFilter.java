package com.wurmatron.mininggoggles.client.gui;

import static net.minecraft.client.Minecraft.getMinecraft;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

public class GuiColorFilter extends Gui {

  public static int ID_COUNTER = 0;

  public GuiTextField text;
  private int ID;
  public boolean enabled = true;

  public GuiColorFilter() {
    this.text = new GuiTextField(ID, getMinecraft().fontRenderer, 0, 0, 162, 17);
    this.text.setMaxStringLength(500);
  }

  public void init() {
    this.ID = ID_COUNTER++;
  }

  public void draw() {
    getMinecraft().renderEngine.bindTexture(GuiGogglesFilter.TEXTURE);
    drawTexturedModalRect(text.x - 22, text.y - 2, 1, 158, 187, 22);
    drawTexturedModalRect(text.x - 18, text.y + 2, ID * 14, 183, 14, 14);
    if (!enabled) {
      drawTexturedModalRect(text.x - 20, text.y, 0, 198, 18, 18);
    }
  }

  public int getID() {
    return ID;
  }
}
