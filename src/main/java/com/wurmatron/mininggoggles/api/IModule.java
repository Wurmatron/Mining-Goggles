package com.wurmatron.mininggoggles.api;

import net.minecraft.entity.player.PlayerEntity;

public interface IModule {

  String getName();

  void onTick(PlayerEntity player, String data);

  boolean renderOnModel();

}
