package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.api.MiningGogglesApi;
import io.wurmatron.mining_goggles.inventory.ContainerCrystalBag;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerCrystalBag;
import io.wurmatron.mining_goggles.items.providers.CapabilityProviderCrystalBag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemCrystalBag extends Item {

    public ItemCrystalBag(Properties prop) {
        super(prop);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level Level, Player player,
                                                  @Nonnull InteractionHand InteractionHand) {
        ItemStack stack = player.getItemInHand(InteractionHand);
        if (!Level.isClientSide) {
            MenuProvider containerProvider = new ContainerProvidedCrystalBag(stack);
            NetworkHooks.openGui((ServerPlayer) player, containerProvider,
                    (packetBuffer) -> {
                    });
        }
        return InteractionResultHolder.pass(stack);
    }

    @Nonnull
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        Level Level = ctx.getLevel();
        if (Level.isClientSide()) {
            return InteractionResult.PASS;
        }
        BlockPos pos = ctx.getClickedPos();
        Direction side = ctx.getClickedFace();
        ItemStack itemStack = ctx.getItemInHand();
        BlockEntity tile = Level.getBlockEntity(pos);
        if (tile == null) {
            return InteractionResult.PASS;
        }
        if (Level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        IItemHandler tileInventory;
        LazyOptional<IItemHandler> capability = tile.getCapability(
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        if (capability.isPresent()) {
            tileInventory = capability.orElseThrow(AssertionError::new);
        } else if (tile instanceof Container) {
            tileInventory = new InvWrapper((Container) tile);
        } else {
            return InteractionResult.FAIL;
        }
        ItemStackHandlerCrystalBag itemStackInteractionHandler = getItemStackCrystalBag(itemStack);
        for (int i = 0; i < itemStackInteractionHandler.getSlots(); i++) {
            ItemStack flower = itemStackInteractionHandler.getStackInSlot(i);
            ItemStack flowersWhichDidNotFit = itemStackInteractionHandler.insertItem(tileInventory,
                    flower, false);
            itemStackInteractionHandler.setStackInSlot(i, flowersWhichDidNotFit);
        }
        tile.setChanged();
        CompoundTag nbt = itemStack.getOrCreateTag();
        int dirtyCounter = nbt.getInt("dirtyCounter");
        nbt.putInt("dirtyCounter", dirtyCounter + 1);
        itemStack.setTag(nbt);
        return InteractionResult.SUCCESS;
    }

    private static class ContainerProvidedCrystalBag implements MenuProvider {

        private ItemStack stackBag;

        public ContainerProvidedCrystalBag(ItemStack bagStack) {
            this.stackBag = bagStack;
        }

        @Override
        public Component getDisplayName() {
            return stackBag.getDisplayName();
        }

        @Override
        public ContainerCrystalBag createMenu(int windowID, Inventory inventory,
                                              Player player) {
            return ContainerCrystalBag.createContainerServerSide(windowID, inventory,
                    getItemStackCrystalBag(stackBag), stackBag);
        }
    }

    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag oldCapNbt) {
        return new CapabilityProviderCrystalBag();
    }

    private static ItemStackHandlerCrystalBag getItemStackCrystalBag(
            ItemStack itemStack) {
        IItemHandler crystalBag = itemStack.getCapability(
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (!(crystalBag instanceof ItemStackHandlerCrystalBag)) {
            return new ItemStackHandlerCrystalBag();
        }
        return (ItemStackHandlerCrystalBag) crystalBag;
    }

    private final String BASE_NBT_TAG = "base";
    private final String CAPABILITY_NBT_TAG = "cap";

    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag baseTag = stack.getTag();
        ItemStackHandlerCrystalBag itemStackInteractionHandler = getItemStackCrystalBag(stack);
        CompoundTag capabilityTag = itemStackInteractionHandler.serializeNBT();
        CompoundTag combinedTag = new CompoundTag();
        if (baseTag != null) {
            combinedTag.put(BASE_NBT_TAG, baseTag);
        }
        if (capabilityTag != null) {
            combinedTag.put(CAPABILITY_NBT_TAG, capabilityTag);
        }
        return combinedTag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        if (nbt == null) {
            stack.setTag(null);
            return;
        }
        stack.setTag(nbt.getCompound(BASE_NBT_TAG));
        ItemStackHandlerCrystalBag itemStackInteractionHandlerFlowerBag = getItemStackCrystalBag(
                stack);
        itemStackInteractionHandlerFlowerBag.deserializeNBT(nbt.getCompound(CAPABILITY_NBT_TAG));
    }

    public static float getFullnessPropertyOverride(ItemStack itemStack,
                                                    @Nullable Level Level, @Nullable LivingEntity livingEntity) {
        ItemStackHandlerCrystalBag itemStackInteractionHandler = getItemStackCrystalBag(itemStack);
        float fractionEmpty =
                itemStackInteractionHandler.getNumberOfEmptySlots() / (float) itemStackInteractionHandler.getSlots();
        return 1.0F - fractionEmpty;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group,
                                 NonNullList<ItemStack> items) {
        if (group == MiningGoggles.TAB_GOGGLES) {
            items.add(new ItemStack(MiningItems.bag));
            ItemStack filled = create(listCrystals());
            items.add(filled);
        }
    }

    private static ItemStack[] listCrystals() {
        ItemStack[] items = new ItemStack[MiningGogglesApi.oreTuning.keySet().size()];
        for (int x = 0; x < MiningGogglesApi.oreTuning.keySet().size(); x++) {
            items[x] = ItemAttunmentCrystal.create(
                    (String) MiningGogglesApi.oreTuning.keySet().toArray()[x]);
        }
        return items;
    }

    public static ItemStack create(ItemStack[] items) {
        ItemStack bag = new ItemStack(MiningItems.bag);
        ItemStackHandlerCrystalBag InteractionHandler = getItemStackCrystalBag(bag);
        for (int x = 0; x < InteractionHandler.getNumberOfEmptySlots(); x++) {
            if (items.length > x) {
                InteractionHandler.insertItem(x, items[x], false);
            }
        }
        return bag;
    }

}
