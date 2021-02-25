package io.wurmatron.mining_goggles;

import io.wurmatron.mining_goggles.items.ItemMiningGoggles;
import io.wurmatron.mining_goggles.items.MiningItems;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
        MiningItems.register("goggles", () -> new ItemMiningGoggles(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD, new Item.Properties().maxStackSize(1).group(ItemGroup.COMBAT)));
        MiningItems.ITEMS.register(modBus);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }
}
