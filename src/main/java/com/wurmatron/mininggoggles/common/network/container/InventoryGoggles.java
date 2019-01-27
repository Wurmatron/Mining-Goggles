package com.wurmatron.mininggoggles.common.network.container;

import com.wurmatron.mininggoggles.common.reference.Global;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants.NBT;

public class InventoryGoggles implements IInventory {

  public static final int INV_SIZE = 8;

  private ItemStack stack;
  public ItemStack[] items;

  public InventoryGoggles(ItemStack stack) {
    this.stack = stack;
    items = new ItemStack[INV_SIZE];
    for (int index = 0; index < INV_SIZE; index++) {
      items[index] = ItemStack.EMPTY;
    }
    readFromNBT();
  }

  @Override
  public int getSizeInventory() {
    return INV_SIZE;
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack itemstack : items) {
      if (!itemstack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    return items[index];
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {
    ItemStack itemStackInSlot = getStackInSlot(index);
    if (itemStackInSlot.isEmpty()) {
      return ItemStack.EMPTY;
    }
    ItemStack itemStackRemoved;
    if (itemStackInSlot.getCount() <= count) {
      itemStackRemoved = itemStackInSlot;
      setInventorySlotContents(index, ItemStack.EMPTY);
    } else {
      itemStackRemoved = itemStackInSlot.splitStack(count);
      if (itemStackInSlot.getCount() == 0) {
        setInventorySlotContents(index, ItemStack.EMPTY);
      }
    }
    markDirty();
    return itemStackRemoved;
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    ItemStack itemStack = getStackInSlot(index);
    if (!itemStack.isEmpty()) {
      setInventorySlotContents(index, ItemStack.EMPTY);
    }
    return itemStack;
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {
    items[index] = stack;
    if (stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
      stack.setCount(getInventoryStackLimit());
    }
    markDirty();
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
  }

  @Override
  public boolean isUsableByPlayer(EntityPlayer player) {
    return true;
  }

  @Override
  public void openInventory(EntityPlayer player) {

  }

  @Override
  public void closeInventory(EntityPlayer player) {

  }

  @Override
  public boolean isItemValidForSlot(int index, ItemStack stack) {
    return true;
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {

  }

  @Override
  public int getFieldCount() {
    return 0;
  }

  @Override
  public void clear() {
    Arrays.fill(items, ItemStack.EMPTY);
  }

  @Override
  public String getName() {
    return "goggles";
  }

  @Override
  public boolean hasCustomName() {
    return true;
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TextComponentString("Goggles");
  }

  @Override
  public void markDirty() {
    writeToNBT();
  }

  public void readFromNBT() {
    NBTTagList nbt = stack.getTagCompound().getTagList(Global.NBT_MODULES, NBT.TAG_COMPOUND);
    for (int index = 0; index < INV_SIZE; index++) {
      if (items[index] == null) {
        items[index] = ItemStack.EMPTY;
      }
      items[index] = new ItemStack(nbt.getCompoundTagAt(index));
    }
  }

  public void writeToNBT() {
    NBTTagList nbt = new NBTTagList();
    for (ItemStack stack : items) {
      nbt.appendTag(stack.writeToNBT(new NBTTagCompound()));
    }
    NBTTagCompound stackNBT = stack.getTagCompound();
    if (stackNBT == null) {
      stackNBT = new NBTTagCompound();
    }
    stackNBT.setTag(Global.NBT_MODULES, nbt);
    stack.setTagCompound(stackNBT);
  }
}
