package com.wurmatron.mininggoggles.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IModule {

  String getName();

  void onTick(EntityPlayer player, String data);

}
