package io.wurmatron.mining_goggles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wurmatron.mining_goggles.api.json.MiningGogglesApi;
import io.wurmatron.mining_goggles.client.render.RenderBlock;
import io.wurmatron.mining_goggles.config.OreConfigLoader;
import io.wurmatron.mining_goggles.items.ItemCrystal;
import io.wurmatron.mining_goggles.items.ItemMiningGoggles;
import io.wurmatron.mining_goggles.items.MiningItems;
import java.io.IOException;
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

  public static final Logger LOGGER = LogManager.getLogger();

  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public MiningGoggles() throws IOException {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    MiningItems.register("goggles", () -> MiningItems.goggles);
    MiningItems.register("crystal", () -> MiningItems.crystal);

    MiningItems.ITEMS.register(modBus);
    MinecraftForge.EVENT_BUS.register(new RenderBlock());
    MiningGogglesApi.oreWavelengths = OreConfigLoader.load();
  }

  private void setup(final FMLCommonSetupEvent event) {

  }

  private void doClientStuff(final FMLClientSetupEvent event) {

  }
}
