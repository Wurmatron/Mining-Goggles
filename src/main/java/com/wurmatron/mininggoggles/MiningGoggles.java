package com.wurmatron.mininggoggles;

import com.wurmatron.mininggoggles.common.config.ConfigHolder;
import com.wurmatron.mininggoggles.common.reference.Global;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Global.MODID)
public class MiningGoggles {

  public static final Logger LOGGER = LogManager.getLogger();
  public static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  public MiningGoggles() {
    LOGGER.info("Loading Mining Goggles");
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    MinecraftForge.EVENT_BUS.register(this);
    ModLoadingContext loadContext = ModLoadingContext.get();
    loadContext.registerConfig(Type.CLIENT,ConfigHolder.CLIENT_SPEC);
    loadContext.registerConfig(Type.SERVER, ConfigHolder.SERVER_SPEC);
  }

  private void setup(FMLCommonSetupEvent e) {

  }

  private void setupClient(FMLClientSetupEvent e) {

  }

  @SubscribeEvent
  public void onServerStarting(FMLServerStartingEvent e) {

  }
}
