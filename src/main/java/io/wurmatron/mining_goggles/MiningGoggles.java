package io.wurmatron.mining_goggles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wurmatron.mining_goggles.api.MiningGogglesApi;
import io.wurmatron.mining_goggles.client.gui.ScreenCrystalBag;
import io.wurmatron.mining_goggles.client.gui.ScreenMiningGoggles_1;
import io.wurmatron.mining_goggles.client.gui.ScreenMiningGoggles_2;
import io.wurmatron.mining_goggles.client.gui.ScreenTuningFork;
import io.wurmatron.mining_goggles.client.render.RenderGoggleOverlay;
import io.wurmatron.mining_goggles.config.OreConfigLoader;
import io.wurmatron.mining_goggles.config.wrapper.OreWavelength;
import io.wurmatron.mining_goggles.event.TuningBlockEvents;
import io.wurmatron.mining_goggles.items.MiningItems;
import io.wurmatron.mining_goggles.registry.ContainerRegistry;
import io.wurmatron.mining_goggles.tab.MiningGogglesItemGroup;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("mininggoggles")
public class MiningGoggles {

  public static final Logger LOGGER = LogManager.getLogger();

  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(4);
  public static ItemGroup TAB_GOGGLES = new MiningGogglesItemGroup("tab.goggles");

  public MiningGoggles() {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    // Registry
    modBus.addListener(this::doClientStuff);
    modBus.register(ContainerRegistry.class);
    ClientOnly clientOnly = new ClientOnly(modBus);
    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> clientOnly::registerClientOnlyEvents);
    // Events
    MinecraftForge.EVENT_BUS.register(new RenderGoggleOverlay());
    MinecraftForge.EVENT_BUS.register(new MiningItems());
    MinecraftForge.EVENT_BUS.register(new TuningBlockEvents());
    // Items
    MiningItems.register("goggles", () -> MiningItems.goggles);
    MiningItems.register("crystal", () -> MiningItems.crystal);
    MiningItems.register("bag_crystal", () -> MiningItems.bag);
    MiningItems.register("goggles_upgraded", () -> MiningItems.gogglesUpgraded);
    MiningItems.register("crystal_constructed", () -> MiningItems.constructedCrystal);
    MiningItems.register("goggles_digital", () -> MiningItems.gogglesDigital);
    MiningItems.register("attunment_crystal", () -> MiningItems.attunmentCrystal);
    MiningItems.register("tuning_fork", () -> MiningItems.tuningFork);
    MiningItems.ITEMS.register(modBus);
    // Config
    HashMap<String, OreWavelength> loadedOres = OreConfigLoader.load();
    MiningGogglesApi.oreWavelengths = new HashMap<>();
    MiningGogglesApi.oreTuning = new HashMap<>();
    for (String name : loadedOres.keySet()) {
      MiningGogglesApi.oreWavelengths.put(name, loadedOres.get(name).optimalWavelength);
      MiningGogglesApi.oreTuning.put(name, loadedOres.get(name).tuning);
    }

  }

  private void doClientStuff(final FMLClientSetupEvent event) {
    ScreenManager.register(ContainerRegistry.containerTypeCrystalBag,
        ScreenCrystalBag::new);
    ScreenManager.register(ContainerRegistry.containerTypeGoggles_1,
        ScreenMiningGoggles_1::new);
    ScreenManager.register(ContainerRegistry.containerTypeGoggles_2,
        ScreenMiningGoggles_2::new);
    ScreenManager.register(ContainerRegistry.containerTuningFork,
        ScreenTuningFork::new);
  }
}
