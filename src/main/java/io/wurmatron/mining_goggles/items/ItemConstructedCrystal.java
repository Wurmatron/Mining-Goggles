package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.property.Properties;

public class ItemConstructedCrystal extends ItemCrystal {

    public ItemConstructedCrystal(Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group,
                                 NonNullList<ItemStack> items) {
        if (group == MiningGoggles.TAB_GOGGLES) {
            items.add(new ItemStack(MiningItems.constructedCrystal, 1));
        }
    }
}
