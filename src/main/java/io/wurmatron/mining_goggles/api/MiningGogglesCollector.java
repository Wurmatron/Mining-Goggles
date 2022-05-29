package io.wurmatron.mining_goggles.api;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public interface MiningGogglesCollector {

  NonBlockingHashMap<BlockPos, Float[]> findBlocks(PlayerEntity player, ItemStack stack, boolean rescan);

  int maxRange(ItemStack stack);

  boolean canSeeBlock(PlayerEntity player, ItemStack stack, BlockPos pos, int wavelength);

  int[][] getWavelength(ItemStack stack, int side);

  void damageCrystals(Random rand, ItemStack stack);

}
