package io.wurmatron.mining_goggles.items;

import static io.wurmatron.mining_goggles.client.render.RenderGoggleOverlay.generateList;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.api.MiningGogglesCollector;
import io.wurmatron.mining_goggles.client.render.RenderGoggleOverlay;
import io.wurmatron.mining_goggles.config.OreConfigLoader;
import io.wurmatron.mining_goggles.inventory.ContainerMiningGoggles_1;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_1;
import io.wurmatron.mining_goggles.items.providers.CapabilityProviderGoggles_1;
import io.wurmatron.mining_goggles.utils.WavelengthCalculator;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.cliffc.high_scale_lib.NonBlockingHashSet;
import org.lwjgl.system.CallbackI.P;

public class ItemMiningGoggles extends ArmorItem implements MiningGogglesCollector {

  public static final int MAX_RADIUS = 8;

  public ItemMiningGoggles(Properties prop) {
    super(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD, prop);
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

  public static ItemStackHandlerGoggles_1 getItemStackGoggles_1(
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

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot,
      String type) {
    return "mininggoggles:textures/models/goggles_t1.png"; // TODO Dynamic based on lens / modules
  }

  public int[][] getWavelength(ItemStack stack, int side) {
    if (side == 0) {
      ItemStack crystal0 = getItemStackGoggles_1(stack).getStackInSlot(0);
      ItemStack crystal1 = getItemStackGoggles_1(stack).getStackInSlot(1);
      int[][] crystalWavelengths = new int[2][2];
      crystalWavelengths[0] = ItemCrystal.getWavelength(crystal0);
      crystalWavelengths[1] = ItemCrystal.getWavelength(crystal1);
      return crystalWavelengths;
    } else if (side == 1) {
      ItemStack crystal2 = getItemStackGoggles_1(stack).getStackInSlot(2);
      ItemStack crystal3 = getItemStackGoggles_1(stack).getStackInSlot(3);
      int[][] crystalWavelengths = new int[2][2];
      crystalWavelengths[0] = ItemCrystal.getWavelength(crystal2);
      crystalWavelengths[1] = ItemCrystal.getWavelength(crystal3);
      return crystalWavelengths;
    }
    return new int[0][0];
  }

  public static int getMaxRange(ItemStack stack) {
    return MAX_RADIUS;
  }

  public static NonBlockingHashMap<BlockPos, Float[]> detectedBlocks = new NonBlockingHashMap<>();

  @Override
  public NonBlockingHashMap<BlockPos, Float[]> findBlocks(PlayerEntity player,
      ItemStack stack, boolean rescan) {
    if (!rescan) {
      return detectedBlocks;
    }
    MiningGoggles.EXECUTORS.submit(() -> {
      int maxRadius = maxRange(stack);
      detectedBlocks.clear();
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

  private static Float[] getColors() {
    return new Float[]{1f, 0f, 0f, 1f};
  }

  @Override
  public int maxRange(ItemStack stack) {
    return MAX_RADIUS;
  }

  @Override
  public boolean canSeeBlock(PlayerEntity player, ItemStack stack, BlockPos pos,
      int wavelength) {
    if (wavelength != -1) {
      int[] visibleLeft = WavelengthCalculator.computeWavelength(getWavelength(stack, 0));
      if (wavelength >= visibleLeft[0] && wavelength <= visibleLeft[1]) {
        return true;
      }
      int[] visibleRight = WavelengthCalculator.computeWavelength(
          getWavelength(stack, 1));
      if (wavelength >= visibleRight[0] && wavelength <= visibleRight[1]) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void damageCrystals(Random random, ItemStack stack) {
    ItemStackHandlerGoggles_1 handler = getItemStackGoggles_1(stack);
    for (int index = 0; index < handler.getSlots(); index++) {
      damageCrystal(random, handler, index);
    }
  }

  public static final int DAMAGE_CHANCE = 2;

  private static void damageCrystal(Random rand, ItemStackHandler handler, int index) {
    if (!handler.getStackInSlot(index).isEmpty()) {
      if (rand.nextInt(DAMAGE_CHANCE) == 0) {
        handler.getStackInSlot(index)
            .setDamageValue(handler.getStackInSlot(index).getDamageValue() + 1);
        if (handler.getStackInSlot(index).getDamageValue() == handler.getStackInSlot(
            index).getMaxDamage()) {
          handler.setStackInSlot(index, ItemStack.EMPTY);
        }
      }
    }
  }
}
