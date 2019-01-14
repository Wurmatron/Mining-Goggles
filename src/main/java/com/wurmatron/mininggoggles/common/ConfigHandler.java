package com.wurmatron.mininggoggles.common;

import com.wurmatron.mininggoggles.common.reference.Global;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber (modid = Global.MODID)
@Config(modid = Global.MODID)
public class ConfigHandler {

  @Comment("How many ticks between goggle re-scan's")
  public static int gogglesUpdateFrequency = 10;

}
