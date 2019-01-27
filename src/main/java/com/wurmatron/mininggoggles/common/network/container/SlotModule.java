package com.wurmatron.mininggoggles.common.network.container;

import com.wurmatron.mininggoggles.common.items.ItemModule;
import com.wurmatron.mininggoggles.common.reference.Global;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class SlotModule extends Slot {

  public SlotModule(IInventory inventoryIn, int index, int xPosition,
      int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
    this.backgroundName = Global.MODID + ":" + "gui/moduleicon";
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return stack.getItem() instanceof ItemModule;
  }
}
