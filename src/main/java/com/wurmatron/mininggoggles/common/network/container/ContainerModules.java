package com.wurmatron.mininggoggles.common.network.container;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerModules extends Container {

  private static final int ARMOR_START = InventoryGoggles.INV_SIZE;
  private static final int ARMOR_END = ARMOR_START + 3;
  private static final int INV_START = ARMOR_END + 1;
  private static final int INV_END = INV_START + 26;
  private static final int HOTBAR_START = INV_END + 1;
  private static final int HOTBAR_END = HOTBAR_START + 8;


  // ContainerPlayer
  private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{
      EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS,
      EntityEquipmentSlot.FEET};

  private EntityPlayer player;
  private InventoryPlayer playerInventory;
  public InventoryGoggles gogglesInventory;

  public ContainerModules(EntityPlayer player,
      InventoryPlayer playerInventory,
      InventoryGoggles gogglesInventory) {
    this.player = player;
    this.playerInventory = playerInventory;
    this.gogglesInventory = gogglesInventory;
    addPlayerSlots();
    addGoggleSlots();
  }

  private void addPlayerSlots() {
    // Armor Slots (Container Player)
    for (int k = 0; k < 4; ++k) {
      final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
      this.addSlotToContainer(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18) {
        public int getSlotStackLimit() {
          return 1;
        }

        public boolean isItemValid(ItemStack stack) {
          return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
        }

        public boolean canTakeStack(EntityPlayer playerIn) {
          ItemStack itemstack = this.getStack();
          return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper
              .hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
        }

        @Nullable
        @SideOnly(Side.CLIENT)
        public String getSlotTexture() {
          return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
        }
      });
    }
    // HotBar (Container Player)
    for (int i1 = 0; i1 < 9; ++i1) {
      this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
    }
    // Main Inventory
    for (int l = 0; l < 3; ++l) {
      for (int j1 = 0; j1 < 9; ++j1) {
        addSlotToContainer(
            new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
      }
    }
  }

  private void addGoggleSlots() {
    for (int index = 0; index < InventoryGoggles.INV_SIZE; ++index) {
      addSlotToContainer(
          new SlotModule(gogglesInventory, index, 80 + (18 * (index / 4)), 8 + (18 * (index % 4))));
    }
  }


  @Override
  public boolean canInteractWith(EntityPlayer player) {
    return gogglesInventory.isUsableByPlayer(player);
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex) {
    Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
    if (sourceSlot == null || !sourceSlot.getHasStack()) {
      return ItemStack.EMPTY;
    }
    ItemStack sourceStack = sourceSlot.getStack();
    ItemStack copyOfSourceStack = sourceStack.copy();
    if (sourceSlotIndex >= INV_START && sourceSlotIndex < INV_START + HOTBAR_END) {
      if (!mergeItemStack(sourceStack, 0, InventoryGoggles.INV_SIZE, false)) {
        return ItemStack.EMPTY;
      }
    } else if (sourceSlotIndex >= 0 && sourceSlotIndex < InventoryGoggles.INV_SIZE) {
      if (!mergeItemStack(sourceStack, INV_START, INV_START + HOTBAR_END, false)) {
        return ItemStack.EMPTY;
      }
    } else {
      return ItemStack.EMPTY;
    }
    if (sourceStack.getCount() == 0) {
      sourceSlot.putStack(ItemStack.EMPTY);
    } else {
      sourceSlot.onSlotChanged();
    }
    sourceSlot.onTake(player, sourceStack);
    return copyOfSourceStack;
  }
}
