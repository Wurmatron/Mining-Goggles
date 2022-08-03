package io.wurmatron.mining_goggles.inventory;


import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_2;
import io.wurmatron.mining_goggles.registry.ContainerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class ContainerTuningFork extends Container {

  private ContainerTuningFork(int windowId, PlayerInventory playerInv,
      ItemStackHandlerGoggles_2 itemStackHandlerGoggles_2,
      ItemStack itemStackBeingHeld) {
    super(ContainerRegistry.containerTuningFork, windowId);
  }

  @Override
  public boolean stillValid(PlayerEntity player) {
    return false;
  }

  public static ContainerTuningFork createContainerClientSide(int windowID,
      PlayerInventory playerInventory, PacketBuffer extraData) {
    try {
      ItemStackHandlerGoggles_2 itemStackHandler = new ItemStackHandlerGoggles_2();
      return new ContainerTuningFork(windowID, playerInventory, itemStackHandler, ItemStack.EMPTY);
    } catch (IllegalArgumentException e) {
      MiningGoggles.LOGGER.warn(e);
    }
    return null;
  }
}
