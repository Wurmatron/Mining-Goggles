package io.wurmatron.mining_goggles;

import io.wurmatron.mining_goggles.client.render.RenderBlock;
import io.wurmatron.mining_goggles.items.ItemMiningGoggles;
import io.wurmatron.mining_goggles.items.MiningItems;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("mininggoggles")
public class MiningGoggles {

  private static final Logger LOGGER = LogManager.getLogger();

  public MiningGoggles() {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    MiningItems.register("goggles",
        () -> new ItemMiningGoggles(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD,
            new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_COMBAT)));
    MiningItems.ITEMS.register(modBus);
    MinecraftForge.EVENT_BUS.register(new RenderBlock());
  }

  private void setup(final FMLCommonSetupEvent event) {

  }

  private void doClientStuff(final FMLClientSetupEvent event) {

  }
}
