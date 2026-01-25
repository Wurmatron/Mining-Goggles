package io.wurmatron.mining_goggles.items.providers;

import io.wurmatron.mining_goggles.items.InteractionHandler.ItemStackInteractionHandlerTuningFork;
import net.minecraft.core.Direction;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemInteractionHandler;
import net.minecraftforge.items.IItemInteractionHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityProviderTuningFork implements ICapabilitySerializable<INBT> {

    private final Direction NO_SPECIFIC_SIDE = null;
    private ItemStackInteractionHandlerTuningFork itemStackInteractionHandlerTuningFork;

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
                                             @Nullable Direction facing) {
        if (CapabilityItemInteractionHandler.ITEM_InteractionHandLER_CAPABILITY == capability) {
            return (LazyOptional<T>) (lazyInitialisionSupplier);
        }
        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return CapabilityItemInteractionHandler.ITEM_InteractionHandLER_CAPABILITY.writeNBT(getCachedInventory(),
                NO_SPECIFIC_SIDE);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CapabilityItemInteractionHandler.ITEM_InteractionHandLER_CAPABILITY.readNBT(getCachedInventory(),
                NO_SPECIFIC_SIDE, nbt);
    }

    private ItemStackInteractionHandlerTuningFork getCachedInventory() {
        if (itemStackInteractionHandlerTuningFork == null) {
            itemStackInteractionHandlerTuningFork = new ItemStackInteractionHandlerTuningFork();
        }
        return itemStackInteractionHandlerTuningFork;
    }


    private final LazyOptional<IItemInteractionHandler> lazyInitialisionSupplier = LazyOptional.of(
            this::getCachedInventory);
}