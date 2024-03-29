package io.wurmatron.mining_goggles.inventory;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_1;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_2;
import io.wurmatron.mining_goggles.registry.ContainerRegistry;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerMiningGoggles_2 extends Container {

  private final ItemStackHandlerGoggles_2 itemStackHandler;
  public final ItemStack itemStackBeingHeld;

  private static final int HOTBAR_SLOT_COUNT = 9;
  private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
  private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
  private static final int PLAYER_INVENTORY_SLOT_COUNT =
      PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
  private static final int VANILLA_SLOT_COUNT =
      HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
  private static final int VANILLA_FIRST_SLOT_INDEX = 0;
  private static final int BAG_INVENTORY_FIRST_SLOT_INDEX =
      VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
  public static final int PLAYER_INVENTORY_YPOS = 107;

  private ContainerMiningGoggles_2(int windowId, PlayerInventory playerInv,
      ItemStackHandlerGoggles_2 itemStackHandlerGoggles_2,
      ItemStack itemStackBeingHeld) {
    super(ContainerRegistry.containerTypeGoggles_2, windowId);
    this.itemStackHandler = itemStackHandlerGoggles_2;
    this.itemStackBeingHeld = itemStackBeingHeld;
    int SLOT_X_SPACING = 18;
    int SLOT_Y_SPACING = 18;
    int HOTBAR_XPOS = 8;
    int HOTBAR_YPOS = 165;
    // Hotbar
    for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
      int slotNumber = x;
      addSlot(
          new Slot(playerInv, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
    }
    // Player Inventory
    final int PLAYER_INVENTORY_XPOS = 8;
    for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
      for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
        addSlot(
            new Slot(playerInv, HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x,
                PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING,
                PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING));
      }
    }
    addSlot(new SlotItemHandler(itemStackHandlerGoggles_2, 0,34,35));
    addSlot(new SlotItemHandler(itemStackHandlerGoggles_2, 1,34,56));
    addSlot(new SlotItemHandler(itemStackHandlerGoggles_2, 2,34,77));
    addSlot(new SlotItemHandler(itemStackHandlerGoggles_2, 3,124,35));
    addSlot(new SlotItemHandler(itemStackHandlerGoggles_2, 4,124,56));
    addSlot(new SlotItemHandler(itemStackHandlerGoggles_2, 5,124,77));
  }

  @Override
  public boolean stillValid(@Nonnull PlayerEntity player) {
    ItemStack main = player.getMainHandItem();
    ItemStack off = player.getOffhandItem();
    return (!main.isEmpty() && main == itemStackBeingHeld) ||
        (!off.isEmpty() && off == itemStackBeingHeld);
  }

  @Nonnull
  @Override
  public ItemStack quickMoveStack(PlayerEntity player, int sourceSlotIndex) {
    Slot sourceSlot = slots.get(sourceSlotIndex);
    if (sourceSlot == null || !sourceSlot.hasItem()) {
      return ItemStack.EMPTY;
    }
    ItemStack sourceStack = sourceSlot.getItem();
    ItemStack copyOfSourceStack = sourceStack.copy();
    int BAG_SLOT_COUNT = itemStackHandler.getSlots();

    if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX
        && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
      if (!moveItemStackTo(sourceStack, BAG_INVENTORY_FIRST_SLOT_INDEX,
          BAG_INVENTORY_FIRST_SLOT_INDEX + BAG_SLOT_COUNT, false)) {
        return ItemStack.EMPTY;
      }
    } else if (sourceSlotIndex >= BAG_INVENTORY_FIRST_SLOT_INDEX
        && sourceSlotIndex < BAG_INVENTORY_FIRST_SLOT_INDEX + BAG_SLOT_COUNT) {
      if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX,
          VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
        return ItemStack.EMPTY;
      }
    } else {
      return ItemStack.EMPTY;
    }
    if (sourceStack.getCount() == 0) {
      sourceSlot.set(ItemStack.EMPTY);
    } else {
      sourceSlot.setChanged();
    }
    sourceSlot.onTake(player, sourceStack);
    return copyOfSourceStack;
  }

  public static ContainerMiningGoggles_2 createContainerServerSide(int windowID,
      PlayerInventory playerInventory, ItemStackHandlerGoggles_2 goggleContents,
      ItemStack bag) {
    return new ContainerMiningGoggles_2(windowID, playerInventory, goggleContents, bag);
  }

  public static ContainerMiningGoggles_2 createContainerClientSide(int windowID,
      PlayerInventory playerInventory, PacketBuffer extraData) {
    try {
      ItemStackHandlerGoggles_2 itemStackHandler = new ItemStackHandlerGoggles_2();
      return new ContainerMiningGoggles_2(windowID, playerInventory, itemStackHandler,
          ItemStack.EMPTY);
    } catch (IllegalArgumentException e) {
      MiningGoggles.LOGGER.warn(e);
    }
    return null;
  }


  @Override
  public void broadcastChanges() {
    if (itemStackHandler.isDirty()) {
      CompoundNBT nbt = itemStackBeingHeld.getOrCreateTag();
      int dirtyCounter = nbt.getInt("dirtyCounter");
      nbt.putInt("dirtyCounter", dirtyCounter + 1);
      itemStackBeingHeld.setTag(nbt);
    }
    super.broadcastChanges();
  }


  private static final Logger LOGGER = LogManager.getLogger();

}
