package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.inventory.ContainerTuningFork;
import io.wurmatron.mining_goggles.items.InteractionHandler.ItemStackInteractionHandlerTuningFork;
import io.wurmatron.mining_goggles.items.providers.CapabilityProviderTuningFork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.Level.InteractionInteractionHand;
import net.minecraft.Level.InterInteractionResultHolder;
import net.minecraft.Level.entity.player.Inventory;
import net.minecraft.Level.entity.player.Player;
import net.minecraft.Level.item.Item;
import net.minecraft.Level.item.ItemStack;
import net.minecraft.Level.item.TooltipFlag;
import net.minecraft.Level.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemInteractionHandler;
import net.minecraftforge.items.IItemInteractionHandler;
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
    public InterInteractionResultHolder<ItemStack> use(Level Level, Player player,
                                                  @Nonnull InteractionInteractionHand InteractionHand) {
        ItemStack stack = player.getItemInInteractionHand(InteractionHand);
        if (!Level.isClientSide) {
            INamedContainerProvider containerProvider = new ContainerProvidedTuningFork(stack);
            NetworkHooks.openGui((ServerPlayer) player, containerProvider,
                    (packetBuffer) -> {
                    });
        }
        return InterInteractionResultHolder.pass(stack);
    }



    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level Level,
                                List<TextComponent> list, TooltipFlag tip) {
        ItemStackInteractionHandlerTuningFork stackInteractionHandler = getItemStackInteractionHandler(stack);
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

    private static class ContainerProvidedTuningFork implements INamedContainerProvider {

        private ItemStack stackBag;

        public ContainerProvidedTuningFork(ItemStack bagStack) {
            this.stackBag = bagStack;
        }

        @Override
        public TextComponent getDisplayName() {
            return stackBag.getDisplayName();
        }


        @Override
        public ContainerTuningFork createMenu(int windowID, Inventory inventory,
                                              Player player) {
            return ContainerTuningFork.createContainerServerSide(windowID, inventory,
                    getItemStackInteractionHandler(stackBag), stackBag);
        }
    }

    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag oldCapNbt) {
        return new CapabilityProviderTuningFork();
    }

    public static ItemStackInteractionHandlerTuningFork getItemStackInteractionHandler(
            ItemStack itemStack) {
        IItemInteractionHandler TuningFork = itemStack.getCapability(
                CapabilityItemInteractionHandler.ITEM_InteractionHandLER_CAPABILITY).orElse(null);
        if (!(TuningFork instanceof ItemStackInteractionHandlerTuningFork)) {
            return new ItemStackInteractionHandlerTuningFork();
        }
        return (ItemStackInteractionHandlerTuningFork) TuningFork;
    }

    private final String BASE_NBT_TAG = "base";
    private final String CAPABILITY_NBT_TAG = "cap";

    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag baseTag = stack.getTag();
        ItemStackInteractionHandlerTuningFork itemStackInteractionHandler = getItemStackInteractionHandler(stack);
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
        ItemStackInteractionHandlerTuningFork itemStackInteractionHandlerFlowerBag = getItemStackInteractionHandler(
                stack);
        itemStackInteractionHandlerFlowerBag.deserializeNBT(nbt.getCompound(CAPABILITY_NBT_TAG));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        ItemStackInteractionHandlerTuningFork fork = getItemStackInteractionHandler(stack);
        if (!fork.getStackInSlot(0).isEmpty()) {
            return true;
        }
        return super.isFoil(stack);
    }
}
