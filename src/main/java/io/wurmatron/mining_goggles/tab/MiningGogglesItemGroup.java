package io.wurmatron.mining_goggles.tab;

import io.wurmatron.mining_goggles.items.MiningItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MiningGogglesItemGroup extends CreativeModeTab {

    public MiningGogglesItemGroup(String label) {
        super(label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(MiningItems.goggles, 1);
    }
}
