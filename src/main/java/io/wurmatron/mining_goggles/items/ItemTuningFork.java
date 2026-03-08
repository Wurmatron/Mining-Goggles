package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.inventory.ContainerTuningFork;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerTuningFork;
import io.wurmatron.mining_goggles.items.providers.CapabilityProviderTuningFork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemTuningFork extends Item {

    public ItemTuningFork(Properties prop) {
        super(prop);
    }



    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level Level, Player player,
                                                       @Nonnull InteractionHand InteractionHand) {
        ItemStack stack = player.getItemInHand(InteractionHand);
        if (!Level.isClientSide) {
            INamedContainerProvider containerProvider = new ContainerProvidedTuningFork(stack);
            NetworkHooks.openGui((ServerPlayer) player, containerProvider,
                    (packetBuffer) -> {
                    });
        }
        return InteractionResultHolder.pass(stack);
    }



    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level Level,
                                List<TextComponent> list, TooltipFlag tip) {
        ItemStackHandlerTuningFork stackInteractionHandler = getItemStackHandler(stack);
        if (!stackInteractionHandler.getStackInSlot(0).isEmpty()) {
            ItemStack adjustableCrystal = stackInteractionHandler.getStackInSlot(0);
            int progress = 0;
            if (adjustableCrystal.hasTag() && adjustableCrystal.getTag().contains("progress")) {
                progress = adjustableCrystal.getTag().getInt("progress");
            }
            list.add(new TextComponent(
                    ItemAttunmentCrystal.computeProgress(stackInteractionHandler.getStackInSlot(0)) + " "
                            + "%"));
            if (!stackInteractionHandler.getStackInSlot(0).getTag().getString("type").isEmpty()) {
                list.add(new TextComponent(
                        stackInteractionHandler.getStackInSlot(0).getTag().getString("type")));
            }
        } else {
            list.add(new TextComponent("Empty"));
        }
        super.appendHoverText(stack, Level, list, tip);
    }

    private static class ContainerProvidedTuningFork implements MenuProvider {

        private ItemStack stackBag;

        public ContainerProvidedTuningFork(ItemStack bagStack) {
            this.stackBag = bagStack;
        }

        @Override
        public Component getDisplayName() {
            return stackBag.getDisplayName();
        }


        @Override
        public ContainerTuningFork createMenu(int windowID, Inventory inventory,
                                              Player player) {
            return ContainerTuningFork.createContainerServerSide(windowID, inventory,
                    getItemStackHandler(stackBag), stackBag);
        }
    }

    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag oldCapNbt) {
        return new CapabilityProviderTuningFork();
    }

    public static ItemStackHandlerTuningFork getItemStackHandler(
            ItemStack itemStack) {
        IItemHandler TuningFork = itemStack.getCapability(
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (!(TuningFork instanceof ItemStackHandlerTuningFork)) {
            return new ItemStackHandlerTuningFork();
        }
        return (ItemStackHandlerTuningFork) TuningFork;
    }

    private final String BASE_NBT_TAG = "base";
    private final String CAPABILITY_NBT_TAG = "cap";

    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag baseTag = stack.getTag();
        ItemStackHandlerTuningFork itemStackHandler = getItemStackHandler(stack);
        CompoundTag capabilityTag = itemStackHandler.serializeNBT();
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
        ItemStackHandlerTuningFork itemStackHandlerFlowerBag = getItemStackHandler(
                stack);
        itemStackHandlerFlowerBag.deserializeNBT(nbt.getCompound(CAPABILITY_NBT_TAG));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        ItemStackHandlerTuningFork fork = getItemStackHandler(stack);
        if (!fork.getStackInSlot(0).isEmpty()) {
            return true;
        }
        return super.isFoil(stack);
    }
}
