package com.wurmatron.mininggoggles.common.items;

import com.wurmatron.mininggoggles.common.registry.Registry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class MiningRegistry {

  // Materials
  public static ArmorMaterial gogglesMaterial = EnumHelper
      .addArmorMaterial("gogglesMining", "mininggoggles", -1, new int[]{5, 0, 0, 0}, 30, null, 2);

  // Items
  public static Item gogglesMining;

  public static void registerItems() {
    gogglesMining = Registry.registerItem(new ItemGogglesMining(gogglesMaterial), "gogglesMining");
  }
}
