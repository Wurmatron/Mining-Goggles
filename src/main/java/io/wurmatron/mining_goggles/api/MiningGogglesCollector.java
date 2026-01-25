package io.wurmatron.mining_goggles.api;

import net.minecraft.core.BlockPos;
import net.minecraft.Level.entity.player.Player;
import net.minecraft.Level.item.ItemStack;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.Random;


public interface MiningGogglesCollector {

    NonBlockingHashMap<BlockPos, Float[]> findBlocks(Player player, ItemStack stack, boolean rescan);

    int maxRange(ItemStack stack);

    boolean canSeeBlock(Player player, ItemStack stack, BlockPos pos, int wavelength);

    int[][] getWavelength(ItemStack stack, int side);

    void damageCrystals(Random rand, ItemStack stack);

}
