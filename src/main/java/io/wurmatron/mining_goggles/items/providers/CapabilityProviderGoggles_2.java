package io.wurmatron.mining_goggles.items.providers;

import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_1;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_2;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CapabilityProviderGoggles_2 implements ICapabilitySerializable<INBT> {

  private final Direction NO_SPECIFIC_SIDE = null;
  private ItemStackHandlerGoggles_2 itemStackHandlerGoggles_2;

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

  private ItemStackHandlerGoggles_2 getCachedInventory() {
    if (itemStackHandlerGoggles_2 == null) {
      itemStackHandlerGoggles_2 = new ItemStackHandlerGoggles_2();
    }
    return itemStackHandlerGoggles_2;
  }


  private final LazyOptional<IItemHandler> lazyInitialisionSupplier = LazyOptional.of(
      this::getCachedInventory);
}