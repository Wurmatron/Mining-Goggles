package io.wurmatron.mining_goggles.items.handler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;


public class ItemStackHandlerGoggles_Digital extends ItemStackHandler {

    public ItemStackHandlerGoggles_Digital() {
        super(0);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }

    public int getNumberOfEmptySlots() {
        return 0;
    }
}
