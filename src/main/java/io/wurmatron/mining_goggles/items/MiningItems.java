package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MiningItems {

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
      ForgeRegistries.ITEMS, "mininggoggles");

  public static Item goggles = new ItemMiningGoggles(
      new Item.Properties().stacksTo(1).tab(MiningGoggles.TAB_GOGGLES));
  public static Item crystal = new ItemCrystal(
      new Item.Properties().stacksTo(4).tab(MiningGoggles.TAB_GOGGLES).durability(100)
          .setNoRepair().fireResistant());
  public static Item bag = new ItemCrystalBag(
      new Properties().stacksTo(1).tab(MiningGoggles.TAB_GOGGLES));
  public static Item gogglesUpgraded = new ItemMiningGogglesUpgraded(new Item.Properties().stacksTo(1).tab(MiningGoggles.TAB_GOGGLES));

  public static <T extends Item> RegistryObject<T> register(final String name,
      final Supplier<T> sup) {
    return ITEMS.register(name, sup);
  }

  @SubscribeEvent
  public void registerModel(RegistryEvent.Register<Item> e) {
    e.getRegistry().register(goggles);
    e.getRegistry().register(crystal);
    e.getRegistry().register(bag);
    e.getRegistry().register(gogglesUpgraded);
  }
}
