package com.wurmatron.mininggoggles;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("mininggoggles")
public class MiningGoggles {

  public static final Logger LOGGER = LogManager.getLogger();
  public static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  public MiningGoggles() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    MinecraftForge.EVENT_BUS.register(this);
  }

  private void setup(FMLCommonSetupEvent e) {

  }

  private void setupClient(FMLClientSetupEvent e) {

  }

  @SubscribeEvent
  public void onServerStarting(FMLServerStartingEvent e) {

  }
}
