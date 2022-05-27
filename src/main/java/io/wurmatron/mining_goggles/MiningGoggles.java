package io.wurmatron.mining_goggles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wurmatron.mining_goggles.api.MiningGogglesApi;
import io.wurmatron.mining_goggles.client.gui.ScreenCrystalBag;
import io.wurmatron.mining_goggles.client.gui.ScreenMiningGoggles_1;
import io.wurmatron.mining_goggles.client.gui.ScreenMiningGoggles_2;
import io.wurmatron.mining_goggles.client.render.RenderBasicGoggles;
import io.wurmatron.mining_goggles.config.OreConfigLoader;
import io.wurmatron.mining_goggles.items.MiningItems;
import io.wurmatron.mining_goggles.registry.ContainerRegistry;
import io.wurmatron.mining_goggles.tab.MiningGogglesItemGroup;
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
    MinecraftForge.EVENT_BUS.register(new RenderBasicGoggles());
    MinecraftForge.EVENT_BUS.register(new MiningItems());
    // Items
    MiningItems.register("goggles", () -> MiningItems.goggles);
    MiningItems.register("crystal", () -> MiningItems.crystal);
    MiningItems.register("bag_crystal", () -> MiningItems.bag);
    MiningItems.register("goggles_upgraded", () -> MiningItems.gogglesUpgraded);
    MiningItems.ITEMS.register(modBus);
    // Config
    MiningGogglesApi.oreWavelengths = OreConfigLoader.load();

  }

  private void doClientStuff(final FMLClientSetupEvent event) {
    ScreenManager.register(ContainerRegistry.containerTypeCrystalBag,
        ScreenCrystalBag::new);
    ScreenManager.register(ContainerRegistry.containerTypeGoggles_1,
        ScreenMiningGoggles_1::new);
    ScreenManager.register(ContainerRegistry.containerTypeGoggles_2,
        ScreenMiningGoggles_2::new);
  }
}
