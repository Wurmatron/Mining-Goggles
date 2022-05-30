package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemConstructedCrystal extends ItemCrystal {

  public ItemConstructedCrystal(Properties properties) {
    super(properties);
  }

  @Override
  public void fillItemCategory(ItemGroup group,
      NonNullList<ItemStack> items) {
    for (ItemStack stack : items) {
      if (stack.getItem().equals(MiningItems.crystal)) {
        items.remove(stack);
      }
    }
    if (group == MiningGoggles.TAB_GOGGLES) {
      items.add(new ItemStack(MiningItems.constructedCrystal, 1));
    }
  }
}
