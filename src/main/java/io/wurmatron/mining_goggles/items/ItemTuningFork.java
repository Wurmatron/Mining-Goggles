package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.inventory.ContainerTuningFork;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerTuningFork;
import io.wurmatron.mining_goggles.items.providers.CapabilityProviderTuningFork;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemTuningFork extends Item {

  public ItemTuningFork(Properties prop) {
    super(prop);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity player,
      @Nonnull Hand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (!world.isClientSide) {
      INamedContainerProvider containerProvider = new ContainerProvidedTuningFork(stack);
      NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider,
          (packetBuffer) -> {
          });
    }
    return ActionResult.pass(stack);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable World world,
      List<ITextComponent> list, ITooltipFlag tip) {
    ItemStackHandlerTuningFork stackHandler = getItemStackHandler(stack);
    if (!stackHandler.getStackInSlot(0).isEmpty()) {
      ItemStack adjustableCrystal = stackHandler.getStackInSlot(0);
      int progress = 0;
      if (adjustableCrystal.hasTag() && adjustableCrystal.getTag().contains("progress")) {
        progress = adjustableCrystal.getTag().getInt("progress");
      }
      list.add(new StringTextComponent(
          ItemAttunmentCrystal.computeProgress(stackHandler.getStackInSlot(0)) + " "
              + "%"));
      if (!stackHandler.getStackInSlot(0).getTag().getString("type").isEmpty()) {
        list.add(new StringTextComponent(
            stackHandler.getStackInSlot(0).getTag().getString("type")));
      }
    } else {
      list.add(new StringTextComponent("Empty"));
    }
    super.appendHoverText(stack, world, list, tip);
  }

  private static class ContainerProvidedTuningFork implements INamedContainerProvider {

    private ItemStack stackBag;

    public ContainerProvidedTuningFork(ItemStack bagStack) {
      this.stackBag = bagStack;
    }

    @Override
    public ITextComponent getDisplayName() {
      return stackBag.getDisplayName();
    }


    @Override
    public ContainerTuningFork createMenu(int windowID, PlayerInventory inventory,
        PlayerEntity player) {
      return ContainerTuningFork.createContainerServerSide(windowID, inventory,
          getItemStackHandler(stackBag), stackBag);
    }
  }

  @Nonnull
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT oldCapNbt) {
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
  public CompoundNBT getShareTag(ItemStack stack) {
    CompoundNBT baseTag = stack.getTag();
    ItemStackHandlerTuningFork itemStackHandler = getItemStackHandler(stack);
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
