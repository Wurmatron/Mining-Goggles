package com.wurmatron.mininggoggles.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;

public class ItemModule extends Item {

  public static String[] metaItems;

  public ItemModule(String[] items) {
    setCreativeTab(CreativeTabs.MISC);
    setHasSubtypes(true);
    this.metaItems = items;
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    if (tab == CreativeTabs.MISC) {
      for (int index = 0; index < metaItems.length; index++) {
        items.add(new ItemStack(this, 1, index));
      }
    }
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    if (stack.getItemDamage() < metaItems.length) {
      return I18n.translateToLocal("module." + metaItems[stack.getItemDamage()] + ".name") + " "
          + I18n.translateToLocal("item.module.name");
    }
    return "item.null.name";
  }
}
