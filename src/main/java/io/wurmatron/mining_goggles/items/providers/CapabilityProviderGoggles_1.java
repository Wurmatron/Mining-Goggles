package io.wurmatron.mining_goggles.items.providers;

import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_1;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityProviderGoggles_1 implements ICapabilitySerializable<INBT> {

    private final Direction NO_SPECIFIC_SIDE = null;
    private ItemStackHandlerGoggles_1 itemStackInteractionHandlerGoggles_1;

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
                                             @Nullable Direction facing) {
        if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) {
            return (LazyOptional<T>) (lazyInitialisionSupplier);
        }
        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(getCachedInventory(),
                NO_SPECIFIC_SIDE);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(getCachedInventory(),
                NO_SPECIFIC_SIDE, nbt);
    }

    private ItemStackHandlerGoggles_1 getCachedInventory() {
        if (itemStackInteractionHandlerGoggles_1 == null) {
            itemStackInteractionHandlerGoggles_1 = new ItemStackHandlerGoggles_1();
        }
        return itemStackInteractionHandlerGoggles_1;
    }


    private final LazyOptional<IItemHandler> lazyInitialisionSupplier = LazyOptional.of(
            this::getCachedInventory);
}