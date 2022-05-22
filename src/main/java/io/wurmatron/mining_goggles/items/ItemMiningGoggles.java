package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.inventory.ContainerMiningGoggles_1;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_1;
import io.wurmatron.mining_goggles.items.providers.CapabilityProviderCrystalBag;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerCrystalBag;
import io.wurmatron.mining_goggles.items.providers.CapabilityProviderGoggles_1;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
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

public class ItemMiningGoggles extends ArmorItem {

  public ItemMiningGoggles(Properties prop) {
    super(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD,prop);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity player,
      @Nonnull Hand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (!world.isClientSide) {
      INamedContainerProvider containerProvider = new ContainerProvidedGoggles_1(stack);
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
    ItemStackHandlerGoggles_1 itemStackHandler = getItemStackGoggles_1(itemStack);
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

  private static class ContainerProvidedGoggles_1 implements INamedContainerProvider {

    private ItemStack stackBag;

    public ContainerProvidedGoggles_1(ItemStack bagStack) {
      this.stackBag = bagStack;
    }

    @Override
    public ITextComponent getDisplayName() {
      return stackBag.getDisplayName();
    }


    @Override
    public ContainerMiningGoggles_1 createMenu(int windowID, PlayerInventory inventory,
        PlayerEntity player) {
      return ContainerMiningGoggles_1.createContainerServerSide(windowID, inventory,
          getItemStackGoggles_1(stackBag), stackBag);
    }
  }

  @Nonnull
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT oldCapNbt) {
    return new CapabilityProviderGoggles_1();
  }

  private static ItemStackHandlerGoggles_1 getItemStackGoggles_1(
      ItemStack itemStack) {
    IItemHandler goggles = itemStack.getCapability(
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
    if (!(goggles instanceof ItemStackHandlerGoggles_1)) {
      return new ItemStackHandlerGoggles_1();
    }
    return (ItemStackHandlerGoggles_1) goggles;
  }

  private final String BASE_NBT_TAG = "base";
  private final String CAPABILITY_NBT_TAG = "cap";

  @Nullable
  @Override
  public CompoundNBT getShareTag(ItemStack stack) {
    CompoundNBT baseTag = stack.getTag();
    ItemStackHandlerGoggles_1 itemStackHandler = getItemStackGoggles_1(stack);
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
    ItemStackHandlerGoggles_1 itemStackHandlerGoggles_1 = getItemStackGoggles_1(
        stack);
    itemStackHandlerGoggles_1.deserializeNBT(nbt.getCompound(CAPABILITY_NBT_TAG));
  }

  public static float getFullnessPropertyOverride(ItemStack itemStack,
      @Nullable World world, @Nullable LivingEntity livingEntity) {
    ItemStackHandlerGoggles_1 itemStackHandler = getItemStackGoggles_1(itemStack);
    float fractionEmpty =
        itemStackHandler.getNumberOfEmptySlots() / (float) itemStackHandler.getSlots();
    return 1.0F - fractionEmpty;
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot,
      String type) {
    return "mininggoggles:textures/models/goggles_t1.png"; // TODO Dynamic based on lens / modules
  }
}
