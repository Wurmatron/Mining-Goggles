package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.api.MiningGogglesCollector;
import io.wurmatron.mining_goggles.inventory.ContainerFilter;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_2;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_Digital;
import io.wurmatron.mining_goggles.registry.ContainerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;
import java.util.Random;

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
        return true;
    }

    @Override
    public int[][] getWavelength(ItemStack stack, int side) {
        return new int[0][];
    }

    @Override
    public void damageCrystals(Random rand, ItemStack stack) {
        // Does not damage crystals, infact does not use them
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot,
                                  String type) {
        return "mininggoggles:textures/models/goggles_digital.png"; // TODO Dynamic based on lens / modules
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof ServerPlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (player.isSecondaryUseActive()) {
                INamedContainerProvider containerFilter = new ContainerProviderFilter(stack);
                NetworkHooks.openGui((ServerPlayerEntity) player, containerFilter);
            }
        }
        return super.onEntitySwing(stack, entity);
    }

    private static class ContainerProvidedGoggles_1 implements INamedContainerProvider {

        private ItemStack helmet;

        public ContainerProvidedGoggles_1(ItemStack bagStack) {
            this.helmet = bagStack;
        }

        @Override
        public ITextComponent getDisplayName() {
            return helmet.getDisplayName();
        }


        @Override
        public ContainerFilter createMenu(int windowID, PlayerInventory inventory, PlayerEntity player) {
            return ContainerFilter.createContainerClientSide(windowID, inventory, null);
        }
    }

    public static ItemStackHandlerGoggles_Digital getItemStackGoggles_Digital(
            ItemStack itemStack) {
        IItemHandler goggles = itemStack.getCapability(
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (!(goggles instanceof ItemStackHandlerGoggles_Digital)) {
            return new ItemStackHandlerGoggles_Digital();
        }
        return (ItemStackHandlerGoggles_Digital) goggles;
    }

    private static class ContainerProviderFilter implements INamedContainerProvider {
        private ItemStack helmet;

        public ContainerProviderFilter(ItemStack helmet) {
            this.helmet = helmet;
        }

        @Override
        public ITextComponent getDisplayName() {
            return helmet.getDisplayName();
        }

        @Override
        public ContainerFilter createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            return ContainerFilter.createContainerServerSide(windowID, playerInventory, playerEntity.getMainHandItem());
        }
    }

}
