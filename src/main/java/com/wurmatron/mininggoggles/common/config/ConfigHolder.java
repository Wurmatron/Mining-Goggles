package com.wurmatron.mininggoggles.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {

  public static final ForgeConfigSpec CLIENT_SPEC;
  public static final ForgeConfigSpec SERVER_SPEC;
  static final ClientConfig CLIENT;
  static final ServerConfig SERVER;

  static {
    // Client
    Pair<ClientConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
    CLIENT = clientPair.getLeft();
    CLIENT_SPEC = clientPair.getRight();
    // Server
    final Pair<ServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
    SERVER = serverPair.getLeft();
    SERVER_SPEC = serverPair.getRight();
  }
}
