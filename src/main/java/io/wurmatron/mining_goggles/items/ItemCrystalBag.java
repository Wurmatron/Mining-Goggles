package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.items.providers.CapabilityProviderCrystalBag;
import io.wurmatron.mining_goggles.inventory.ContainerCrystalBag;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerCrystalBag;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ItemCrystalBag extends Item {

  public ItemCrystalBag(Properties prop) {
    super(prop);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity player,
      @Nonnull Hand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (!world.isClientSide) {
      INamedContainerProvider containerProvider = new ContainerProvidedCrystalBag(stack);
      NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider,
          (packetBuffer) -> {
          });
    }
    return ActionResult.pass(stack);
  }

  @Nonnull
  @Override
  public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
    World world = ctx.getLevel();
    if (world.isClientSide()) {
      return ActionResultType.PASS;
    }
    BlockPos pos = ctx.getClickedPos();
    Direction side = ctx.getClickedFace();
    ItemStack itemStack = ctx.getItemInHand();
    TileEntity tile = world.getBlockEntity(pos);
    if (tile == null) {
      return ActionResultType.PASS;
    }
    if (world.isClientSide()) {
      return ActionResultType.SUCCESS;
    }
    IItemHandler tileInventory;
    LazyOptional<IItemHandler> capability = tile.getCapability(
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
    if (capability.isPresent()) {
      tileInventory = capability.orElseThrow(AssertionError::new);
    } else if (tile instanceof IInventory) {
      tileInventory = new InvWrapper((IInventory) tile);
    } else {
      return ActionResultType.FAIL;
    }
    ItemStackHandlerCrystalBag itemStackHandler = getItemStackHandlerFlowerBag(itemStack);
    for (int i = 0; i < itemStackHandler.getSlots(); i++) {
      ItemStack flower = itemStackHandler.getStackInSlot(i);
      ItemStack flowersWhichDidNotFit = ItemHandlerHelper.insertItemStacked(tileInventory,
          flower, false);
      itemStackHandler.setStackInSlot(i, flowersWhichDidNotFit);
    }
    tile.setChanged();
    CompoundNBT nbt = itemStack.getOrCreateTag();
    int dirtyCounter = nbt.getInt("dirtyCounter");
    nbt.putInt("dirtyCounter", dirtyCounter + 1);
    itemStack.setTag(nbt);
    return ActionResultType.SUCCESS;
  }

  private static class ContainerProvidedCrystalBag implements INamedContainerProvider {

    private ItemStack stackBag;

    public ContainerProvidedCrystalBag(ItemStack bagStack) {
      this.stackBag = bagStack;
    }

    @Override
    public ITextComponent getDisplayName() {
      return stackBag.getDisplayName();
    }


    @Override
    public ContainerCrystalBag createMenu(int windowID, PlayerInventory inventory,
        PlayerEntity player) {
      return ContainerCrystalBag.createContainerServerSide(windowID, inventory,
          getItemStackHandlerFlowerBag(stackBag), stackBag);
    }
  }

  @Nonnull
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT oldCapNbt) {
    return new CapabilityProviderCrystalBag();
  }

  private static ItemStackHandlerCrystalBag getItemStackHandlerFlowerBag(
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
  public CompoundNBT getShareTag(ItemStack stack) {
    CompoundNBT baseTag = stack.getTag();
    ItemStackHandlerCrystalBag itemStackHandler = getItemStackHandlerFlowerBag(stack);
    CompoundNBT capabilityTag = itemStackHandler.serializeNBT();
    CompoundNBT combinedTag = new CompoundNBT();
    if (baseTag != null) {
      combinedTag.put(BASE_NBT_TAG, baseTag);
    }
    if (capabilityTag != null) {
      combinedTag.put(CAPABILITY_NBT_TAG, capabilityTag);
    }
    return combinedTag;
  }

  @Override
  public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
    if (nbt == null) {
      stack.setTag(null);
      return;
    }
    stack.setTag(nbt.getCompound(BASE_NBT_TAG));
    ItemStackHandlerCrystalBag itemStackHandlerFlowerBag = getItemStackHandlerFlowerBag(
        stack);
    itemStackHandlerFlowerBag.deserializeNBT(nbt.getCompound(CAPABILITY_NBT_TAG));
  }

  public static float getFullnessPropertyOverride(ItemStack itemStack,
      @Nullable World world, @Nullable LivingEntity livingEntity) {
    ItemStackHandlerCrystalBag itemStackHandler = getItemStackHandlerFlowerBag(itemStack);
    float fractionEmpty =
        itemStackHandler.getNumberOfEmptySlots() / (float) itemStackHandler.getSlots();
    return 1.0F - fractionEmpty;
  }
}
