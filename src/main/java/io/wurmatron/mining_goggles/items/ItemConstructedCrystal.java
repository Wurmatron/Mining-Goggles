package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;

public class ItemConstructedCrystal extends ItemCrystal {

    public ItemConstructedCrystal(Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(ItemGroup group,
                                 NonNullList<ItemStack> items) {
        if (group == MiningGoggles.TAB_GOGGLES) {
            items.add(new ItemStack(MiningItems.constructedCrystal, 1));
        }
    }
}
