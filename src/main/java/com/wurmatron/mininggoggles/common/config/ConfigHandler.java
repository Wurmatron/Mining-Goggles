package com.wurmatron.mininggoggles.common.config;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigHandler {

  public static ModConfig clientConfig;
  public static ModConfig serverConfig;

  // Config Values
  // Client
  public static int gogglesUpdateFrequency;
  // Server
  public static String[] disabledModules;

  @SubscribeEvent
  public static void onModConfigEvent(ModConfig.ModConfigEvent e) {
    final ModConfig config = e.getConfig();
    // Rebake the configs when they change
    if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
      bakeClient(config);
    } else if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
      bakeServer(config);
    }
  }

  public static void bakeClient(ModConfig config) {
    clientConfig = config;
    gogglesUpdateFrequency = ConfigHolder.CLIENT.gogglesUpdateFrequency.get();
  }

  public static void bakeServer(ModConfig config) {
    serverConfig = config;
    disabledModules= ConfigHolder.SERVER.disabledModules.get();
  }
}
