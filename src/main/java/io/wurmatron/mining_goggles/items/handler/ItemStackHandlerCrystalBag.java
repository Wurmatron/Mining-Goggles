package io.wurmatron.mining_goggles.items.handler;

import io.wurmatron.mining_goggles.items.ItemCrystal;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;


public class ItemStackHandlerCrystalBag extends ItemStackHandler {

  public ItemStackHandlerCrystalBag() {
    super(21);
  }

  @Override
  public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    Item item = stack.getItem();
    if (item instanceof ItemCrystal) {
      return true;
    }
    return false;
  }

  public int getNumberOfEmptySlots() {
    final int NUMBER_OF_SLOTS = getSlots();
    int emptySlotCount = 0;
    for (int i = 0; i < NUMBER_OF_SLOTS; ++i) {
      if (getStackInSlot(i) == ItemStack.EMPTY) {
        ++emptySlotCount;
      }
    }
    return emptySlotCount;
  }

  public boolean isDirty() {
    boolean currentState = isDirty;
    isDirty = false;
    return currentState;
  }

  protected void onContentsChanged(int slot) {
    isDirty = true;
  }

  private boolean isDirty = true;

}
