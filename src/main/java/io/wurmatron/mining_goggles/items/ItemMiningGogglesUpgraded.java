package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.api.MiningGogglesCollector;
import io.wurmatron.mining_goggles.client.render.RenderGoggleOverlay;
import io.wurmatron.mining_goggles.inventory.ContainerMiningGoggles_2;
import io.wurmatron.mining_goggles.items.InteractionHandler.ItemStackInteractionHandlerGoggles_2;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_2;
import io.wurmatron.mining_goggles.items.providers.CapabilityProviderGoggles_2;
import io.wurmatron.mining_goggles.utils.WavelengthCalculator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkHooks;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static io.wurmatron.mining_goggles.client.render.RenderGoggleOverlay.generateList;

public class ItemMiningGogglesUpgraded extends ArmorItem implements
        MiningGogglesCollector {

    public static int MAX_RADIUS = MiningGoggles.config.goggles.maxRadius > 0 ? MiningGoggles.config.primitiveGoggles.maxRadius : 1;

    public ItemMiningGogglesUpgraded(Properties prop) {
        super(ArmorMaterials.NETHERITE, EquipmentSlot.HEAD, prop);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level Level, Player player,
                                                  @Nonnull InteractionHand InteractionHand) {
        ItemStack stack = player.getItemInHand(InteractionHand);
        if (!Level.isClientSide) {
            INamedContainerProvider containerProvider = new ContainerProvidedGoggles_1(stack);
            NetworkHooks.openGui((Player) player, containerProvider,
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
        ItemStackHandlerGoggles_2 itemStackInteractionHandler = getItemStackGoggles_2(itemStack);
        for (int i = 0; i < itemStackInteractionHandler.getSlots(); i++) {
            ItemStack flower = itemStackInteractionHandler.getStackInSlot(i);
            ItemStack flowersWhichDidNotFit = ItemHandlerHelper.insertItemStacked(tileInventory,
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

    private static class ContainerProvidedGoggles_1 implements INamedContainerProvider {

        private ItemStack stackBag;

        public ContainerProvidedGoggles_1(ItemStack bagStack) {
            this.stackBag = bagStack;
        }

        @Override
        public Component getDisplayName() {
            return stackBag.getDisplayName();
        }


        @Override
        public ContainerMiningGoggles_2 createMenu(int windowID, Inventory inventory,
                                                   Player player) {
            return ContainerMiningGoggles_2.createContainerServerSide(windowID, inventory,
                    getItemStackGoggles_2(stackBag), stackBag);
        }
    }

    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag oldCapNbt) {
        return new CapabilityProviderGoggles_2();
    }

    public static ItemStackHandlerGoggles_2 getItemStackGoggles_2(
            ItemStack itemStack) {
        IItemHandler goggles = itemStack.getCapability(
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (!(goggles instanceof ItemStackHandlerGoggles_2)) {
            return new ItemStackHandlerGoggles_2();
        }
        return (ItemStackHandlerGoggles_2) goggles;
    }

    private final String BASE_NBT_TAG = "base";
    private final String CAPABILITY_NBT_TAG = "cap";

    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag baseTag = stack.getTag();
        ItemStackHandlerGoggles_2 itemStackInteractionHandler = getItemStackGoggles_2(stack);
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
        ItemStackHandlerGoggles_2 itemStackInteractionHandlerGoggles_2 = getItemStackGoggles_2(
                stack);
        itemStackInteractionHandlerGoggles_2.deserializeNBT(nbt.getCompound(CAPABILITY_NBT_TAG));
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
                                  String type) {
        return "mininggoggles:textures/models/goggles_t2.png"; // TODO Dynamic based on lens / modules
    }

    public int[][] getWavelength(ItemStack stack, int side) {
        if (side == 0) {
            ItemStack crystal0 = getItemStackGoggles_2(stack).getStackInSlot(0);
            ItemStack crystal1 = getItemStackGoggles_2(stack).getStackInSlot(1);
            ItemStack crystal2 = getItemStackGoggles_2(stack).getStackInSlot(2);
            int[][] crystalWavelengths = new int[3][2];
            crystalWavelengths[0] = ItemCrystal.getWavelength(crystal0);
            crystalWavelengths[1] = ItemCrystal.getWavelength(crystal1);
            crystalWavelengths[2] = ItemCrystal.getWavelength(crystal2);
            return crystalWavelengths;
        } else if (side == 1) {
            ItemStack crystal3 = getItemStackGoggles_2(stack).getStackInSlot(3);
            ItemStack crystal4 = getItemStackGoggles_2(stack).getStackInSlot(4);
            ItemStack crystal5 = getItemStackGoggles_2(stack).getStackInSlot(5);
            int[][] crystalWavelengths = new int[3][2];
            crystalWavelengths[0] = ItemCrystal.getWavelength(crystal3);
            crystalWavelengths[1] = ItemCrystal.getWavelength(crystal4);
            crystalWavelengths[1] = ItemCrystal.getWavelength(crystal5);
            return crystalWavelengths;
        }
        return new int[0][0];
    }

    public static int getMaxRange(ItemStack stack) {
        return MAX_RADIUS;
    }

    public static NonBlockingHashMap<BlockPos, Float[]> detectedBlocks = new NonBlockingHashMap<>();


    @Override
    public NonBlockingHashMap<BlockPos, Float[]> findBlocks(Player player,
                                                            ItemStack stack, boolean rescan) {
        if (!rescan) {
            return detectedBlocks;
        }
        detectedBlocks.clear();
        MiningGoggles.EXECUTORS.submit(() -> {
            int maxRadius = maxRange(stack);
            MiningGoggles.EXECUTORS.submit(() -> {
                List<BlockPos> fullBlockList = generateList(
                        (int) (player.getX() - maxRadius),
                        (int) (player.getY() - maxRadius), (int) (player.getZ() - maxRadius),
                        (int) (player.getX() + maxRadius), (int) (player.getY() + maxRadius),
                        (int) (player.getZ() + maxRadius));
                BlockPos[] subA = Arrays.copyOfRange(fullBlockList.toArray(new BlockPos[0]),
                        0, fullBlockList.size() / 2);
                BlockPos[] subB = Arrays.copyOfRange(fullBlockList.toArray(new BlockPos[0]),
                        fullBlockList.size() / 2, fullBlockList.size());
                // Test Group A
                MiningGoggles.EXECUTORS.submit(() -> {
                    for (BlockPos a : subA) {
                        if (RenderGoggleOverlay.isValidPos(player, a)) {
                            detectedBlocks.put(a, getColors());
                        }
                    }
                });
                // Test Group B
                MiningGoggles.EXECUTORS.submit(() -> {
                    for (BlockPos b : subB) {
                        if (RenderGoggleOverlay.isValidPos(player, b)) {
                            detectedBlocks.put(b, getColors());
                        }
                    }
                });
            });
        });
        return detectedBlocks;
    }

    @Override
    public int maxRange(ItemStack stack) {
        return MAX_RADIUS;
    }

    private static Float[] getColors() {
        return new Float[]{MiningGoggles.config.goggles.defaultColor[0],
                MiningGoggles.config.goggles.defaultColor[1],
                MiningGoggles.config.goggles.defaultColor[2],
                MiningGoggles.config.goggles.defaultColor[3]};
    }

    @Override
    public boolean canSeeBlock(Player player, ItemStack stack, BlockPos pos,
                               int wavelength) {
        if (wavelength != -1) {
            int[] visibleLeft = WavelengthCalculator.computeWavelength(getWavelength(stack, 0));
            if (wavelength >= visibleLeft[0] && wavelength <= visibleLeft[1]) {
                return true;
            }
            int[] visibleRight = WavelengthCalculator.computeWavelength(getWavelength(stack, 1));
            return wavelength >= visibleRight[0] && wavelength <= visibleRight[1];
        }
        return false;
    }

    @Override
    public void damageCrystals(Random random, ItemStack stack) {
        ItemStackHandlerGoggles_2 handler = getItemStackGoggles_2(stack);
        for (int index = 0; index < handler.getSlots(); index++) {
            damageCrystal(random, handler, index);
        }
    }

    public static final int DAMAGE_CHANCE = MiningGoggles.config.goggles.crystalDamageChance > 0 ? MiningGoggles.config.primitiveGoggles.maxRadius : 1;

    private static void damageCrystal(Random rand, ItemStackHandler InteractionHandler, int index) {
        if (!InteractionHandler.getStackInSlot(index).isEmpty()) {
            if (rand.nextInt(DAMAGE_CHANCE) == 0) {
                InteractionHandler.getStackInSlot(index)
                        .setDamageValue(InteractionHandler.getStackInSlot(index).getDamageValue() + 1);
                if (InteractionHandler.getStackInSlot(index).getDamageValue() == InteractionHandler.getStackInSlot(
                        index).getMaxDamage()) {
                    InteractionHandler.setStackInSlot(index, ItemStack.EMPTY);
                }
            }
        }
    }
}
