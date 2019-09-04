package com.wurmatron.mininggoggles.common.config;

import com.wurmatron.mininggoggles.common.reference.Global;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

  ForgeConfigSpec.ConfigValue<Integer> gogglesUpdateFrequency;

  ClientConfig(ForgeConfigSpec.Builder builder) {
    builder.push("general");
    gogglesUpdateFrequency = builder.comment("How many ticks between goggle re-scan's").translation(Global.MODID + ":" + ".config"
        + ".gogglesUpdateFrequency").define("gogglesUpdateFrequency", 10);
    builder.pop();
  }

}
