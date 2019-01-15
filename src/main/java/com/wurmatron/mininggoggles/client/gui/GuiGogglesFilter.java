package com.wurmatron.mininggoggles.client.gui;

import com.wurmatron.mininggoggles.common.network.NetworkHandler;
import com.wurmatron.mininggoggles.common.network.packets.UpdateHelmetConfig;
import com.wurmatron.mininggoggles.common.reference.Global;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiGogglesFilter extends GuiScreen {

  public static final ResourceLocation TEXTURE = new ResourceLocation(Global.MODID,
      "textures/gui/gogglesFilter.png");

  private GuiColorFilter[] filters = new GuiColorFilter[16];
  private int startingIndex = 0;
  private ItemStack stack;

  public GuiGogglesFilter(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    int guiWidth = (width - 218) / 2;
    int guiHeight = (height - 154) / 2;
    mc.renderEngine.bindTexture(TEXTURE);
    drawTexturedModalRect((width - 218) / 2, (height - 154) / 2, 0, 0, 218, 154);
    drawTexturedModalRect(((width - 218) / 2) + 199,
        (((height - 154) / 2) + 6) + (int) (((142f / 16) * startingIndex)), 219, 6, 12, 53);
    for (int index = startingIndex; index < startingIndex + 6; index++) {
      filters[index].text.x = guiWidth + 29;
      filters[index].text.y = guiHeight + 8 + (24 * (index - startingIndex));
      filters[index].draw();
    }
    for (int index = startingIndex; index < startingIndex + 6; index++) {
      filters[index].text.drawTextBox();
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    GuiColorFilter.ID_COUNTER = 0;
    int guiWidth = (width - 218) / 2;
    int guiHeight = (height - 154) / 2;
    for (int index = 0; index < 16; index++) {
      filters[index] = new GuiColorFilter();
      filters[index].init();
      filters[index].text.x = guiWidth + 29;
      filters[index].text.y = guiHeight + 8 + (24 * index);
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Global.NBT_FILTERS) && stack
          .getTagCompound().getCompoundTag(Global.NBT_FILTERS).hasKey(Global.NBT_COLOR + index)) {
        String oreEntry = stack.getTagCompound().getCompoundTag(Global.NBT_FILTERS)
            .getString(Global.NBT_COLOR + index);
        if (oreEntry.contains("9")) {
          oreEntry = oreEntry.substring(0, oreEntry.length() - 1);
          filters[index].enabled = false;
        } else {
          filters[index].enabled = true;
        }
        filters[index].text.setText(oreEntry);
      }
    }
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    for (GuiColorFilter filter : filters) {
      filter.text.mouseClicked(mouseX, mouseY, mouseButton);
      if (isWithin(mouseX, mouseY, filter.text.x - 20, filter.text.y,
          +filter.text.x,
          +filter.text.y + 18)) {
        filter.enabled = !filter.enabled;
      }
    }
  }

  private boolean isWithin(int mouseX, int mouseY, int minX, int minY, int maxX, int maxY) {
    return mouseX >= minX && mouseY >= minY && mouseX <= maxX && mouseY <= maxY;
  }

  @Override
  public void updateScreen() {
    super.updateScreen();
    for (GuiColorFilter filter : filters) {
      filter.text.updateCursorCounter();
    }
    int dWheel = Mouse.getDWheel();
    if (dWheel < 0) {
      moveDown();
    } else if (dWheel > 0) {
      moveUp();
    }
  }

  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    NetworkHandler.sendToServer(new UpdateHelmetConfig(collectNBTData()));
  }

  private NBTTagCompound collectNBTData() {
    NBTTagCompound nbt = new NBTTagCompound();
    for (GuiColorFilter filter : filters) {
      nbt.setString("color" + filter.getID(), filter.text.getText() + (filter.enabled ? "" : "9"));
    }
    return nbt;
  }

  @Override
  public void keyTyped(char c, int i) throws IOException {
    if (i == Keyboard.KEY_ESCAPE) {
      super.keyTyped(c, i);
    } else if (i == Keyboard.KEY_DOWN) {
      moveDown();
    } else if (i == Keyboard.KEY_UP) {
      moveUp();
    } else if (i == Keyboard.KEY_RETURN) {
      for (GuiColorFilter filter : filters) {
        filter.text.setFocused(false);
      }
    } else {
      for (GuiColorFilter filter : filters) {
        if (filter.text.isFocused()) {
          if (Character.isAlphabetic(c) || i == Keyboard.KEY_SEMICOLON) {
            filter.text.textboxKeyTyped(c, i);
          } else if (i == Keyboard.KEY_DELETE || i == Keyboard.KEY_BACK) {
            if (!filter.text.getText().isEmpty()) {
              filter.text
                  .setText(filter.text.getText().substring(0, filter.text.getText().length() - 1));
            }
          }
        }
      }
    }
  }

  private void resetFocus() {
    for (GuiColorFilter filter : filters) {
      filter.text.setFocused(false);
    }
  }

  private void moveDown() {
    resetFocus();
    startingIndex++;
    if (startingIndex + 6 >= 16) {
      startingIndex = 10;
    }
  }

  private void moveUp() {
    resetFocus();
    startingIndex--;
    if (startingIndex < 0) {
      startingIndex = 0;
    }
  }
}
