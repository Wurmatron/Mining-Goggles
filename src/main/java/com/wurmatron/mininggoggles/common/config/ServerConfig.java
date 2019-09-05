package com.wurmatron.mininggoggles.common.config;

import com.wurmatron.mininggoggles.common.reference.Global;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

  ForgeConfigSpec.ConfigValue<String[]> disabledModules;

  ServerConfig(ForgeConfigSpec.Builder builder) {
    builder.push("general");
    disabledModules = builder.comment("Modules to prevent from being loaded").translation(Global.MODID + ":" + ".config"
        + ".disabledModules").define("disabledModules",new String[0]);
    builder.pop();
  }

}
