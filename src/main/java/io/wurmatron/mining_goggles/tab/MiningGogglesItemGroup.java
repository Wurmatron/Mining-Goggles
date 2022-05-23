package io.wurmatron.mining_goggles.tab;

import io.wurmatron.mining_goggles.items.MiningItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MiningGogglesItemGroup extends ItemGroup {

  public MiningGogglesItemGroup(String label) {
    super(label);
  }

  @Override
  public ItemStack makeIcon() {
    return new ItemStack(MiningItems.goggles, 1);
  }
}
