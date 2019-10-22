package com.wurmatron.mininggoggles.common.items;

import com.wurmatron.mininggoggles.common.items.baubles.ItemGogglesMiningBaubles;
import com.wurmatron.mininggoggles.common.registry.ModuleRegistry;
import com.wurmatron.mininggoggles.common.registry.Registry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;

public class MiningRegistry {

  // Materials
  public static ArmorMaterial gogglesMaterial = EnumHelper
      .addArmorMaterial("gogglesMining", "mininggoggles", -1, new int[]{5, 0, 0, 0}, 30, null, 2);

  // Items
  public static Item gogglesMining;
  public static Item itemModule;

  public static void registerItems() {
    itemModule = Registry.registerItem(new ItemModule(ModuleRegistry.getNames()), "module");
    if (Loader.isModLoaded("baubles")) {
      gogglesMining = Registry.registerItem(new ItemGogglesMiningBaubles(gogglesMaterial), "gogglesMining");
    } else {
      gogglesMining = Registry.registerItem(new ItemGogglesMining(gogglesMaterial), "gogglesMining");
    }
  }
}
