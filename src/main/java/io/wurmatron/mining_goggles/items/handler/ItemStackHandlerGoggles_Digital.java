package io.wurmatron.mining_goggles.items.InteractionHandler;

import net.minecraftforge.items.ItemStackInteractionHandler;

import javax.annotation.Nonnull;


public class ItemStackInteractionHandlerGoggles_Digital extends ItemStackInteractionHandler {

    public ItemStackInteractionHandlerGoggles_Digital() {
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
