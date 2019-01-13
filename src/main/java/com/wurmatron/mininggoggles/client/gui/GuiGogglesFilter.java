package com.wurmatron.mininggoggles.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public class GuiGogglesFilter extends GuiScreen {

  private ItemStack stack;

  public GuiGogglesFilter(ItemStack stack) {
    this.stack = stack;
  }
}
