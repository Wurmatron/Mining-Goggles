package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.api.MiningGogglesCollector;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class ItemMiningGogglesDigital extends ArmorItem implements
    MiningGogglesCollector {

  public static final int MAX_RADIUS = 24;

  public ItemMiningGogglesDigital(Properties prop) {
    super(ArmorMaterial.NETHERITE, EquipmentSlotType.HEAD, prop);
  }

  @Override
  public NonBlockingHashMap<BlockPos, Float[]> findBlocks(PlayerEntity player,
      ItemStack stack, boolean rescan) {
    return null;
  }

  @Override
  public int maxRange(ItemStack stack) {
    return MAX_RADIUS;
  }

  @Override
  public boolean canSeeBlock(PlayerEntity player, ItemStack stack, BlockPos pos,
      int wavelength) {
    return false;
  }

  @Override
  public int[][] getWavelength(ItemStack stack, int side) {
    return new int[0][];
  }

  @Override
  public void damageCrystals(Random rand, ItemStack stack) {

  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot,
      String type) {
    return "mininggoggles:textures/models/goggles_digital.png"; // TODO Dynamic based on lens / modules
  }
}
