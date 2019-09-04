package com.wurmatron.mininggoggles.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

  ServerConfig(ForgeConfigSpec.Builder builder) {
    builder.push("general");

    builder.pop();
  }

}
