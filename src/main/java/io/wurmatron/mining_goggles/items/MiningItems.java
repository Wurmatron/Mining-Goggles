package io.wurmatron.mining_goggles.items;

import java.util.function.Supplier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MiningItems {

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
      ForgeRegistries.ITEMS, "mininggoggles");

  public static Item goggles = new ItemMiningGoggles(ArmorMaterial.DIAMOND,
      EquipmentSlotType.HEAD,
      new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_COMBAT));
  public static Item crystal = new ItemCrystal(
      new Item.Properties().stacksTo(4).tab(ItemGroup.TAB_COMBAT).durability(100)
          .setNoRepair().fireResistant());
  public static Item bag = new ItemCrystalBag(
      new Properties().stacksTo(1).tab(ItemGroup.TAB_COMBAT));

  public static <T extends Item> RegistryObject<T> register(final String name,
      final Supplier<T> sup) {
    return ITEMS.register(name, sup);
  }

  @SubscribeEvent
  public void registerModel(RegistryEvent.Register<Item> e) {
    e.getRegistry().register(goggles);
    e.getRegistry().register(crystal);
    e.getRegistry().register(bag);
  }
}
