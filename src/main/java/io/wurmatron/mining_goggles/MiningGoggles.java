package io.wurmatron.mining_goggles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wurmatron.mining_goggles.api.MiningGogglesApi;
import io.wurmatron.mining_goggles.client.gui.*;
import io.wurmatron.mining_goggles.client.render.RenderGoggleOverlay;
import io.wurmatron.mining_goggles.config.MiningGogglesConfig;
import io.wurmatron.mining_goggles.config.OreConfigLoader;
import io.wurmatron.mining_goggles.config.wrapper.OreWavelength;
import io.wurmatron.mining_goggles.event.TuningBlockEvents;
import io.wurmatron.mining_goggles.items.MiningItems;
import io.wurmatron.mining_goggles.network.PacketUtils;
import io.wurmatron.mining_goggles.registry.ContainerRegistry;
import io.wurmatron.mining_goggles.tab.MiningGogglesItemGroup;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod("mininggoggles")
public class MiningGoggles {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mininggoggles";

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(4);
    public static CreativeModeTab TAB_GOGGLES = new MiningGogglesItemGroup("tab.goggles");
    public static MiningGogglesConfig config;

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public MiningGoggles() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        config = MiningGogglesConfig.load();

        // Registry
        modBus.addListener(this::doClientStuff);
        modBus.register(ContainerRegistry.class);
        ClientOnly clientOnly = new ClientOnly(modBus);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> clientOnly::registerClientOnlyEvents);
        PacketUtils.register();
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

    private void doClientStuff(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            () -> MenuScreens.register(ContainerRegistry.containerTypeCrystalBag, ScreenCrystalBag::new);
            () -> MenuScreens.register(ContainerRegistry.containerTypeGoggles_1, ScreenMiningGoggles_1::new);
            () -> MenuScreens.register(ContainerRegistry.containerTypeGoggles_2, ScreenMiningGoggles_2::new);
            () -> MenuScreens.register(ContainerRegistry.containerTuningFork, ScreenTuningFork::new);
            () -> MenuScreens.register(ContainerRegistry.containerTypeGoggles_Digital, ScreenFilterDigital::new);
        });
    }
}
