package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.api.MiningGogglesCollector;
import io.wurmatron.mining_goggles.client.render.RenderGoggleOverlay;
import io.wurmatron.mining_goggles.inventory.ContainerFilter;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerGoggles_Digital;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;
import java.util.*;

import static io.wurmatron.mining_goggles.client.render.RenderGoggleOverlay.generateList;

public class ItemMiningGogglesDigital extends ArmorItem implements
        MiningGogglesCollector {

    public static int MAX_RADIUS = MiningGoggles.config.digitalGoggles.maxRadius;

    public ItemMiningGogglesDigital(Properties prop) {
        super(ArmorMaterial.NETHERITE, EquipmentSlotType.HEAD, prop);
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
            HashMap<Integer, String[]> helmetSettings = getSettings(stack);
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
                        detectedBlocks.put(a, getColors(player.level, helmetSettings, a));
                    }
                }
            });
            // Test Group B
            MiningGoggles.EXECUTORS.submit(() -> {
                for (BlockPos b : subB) {
                    if (RenderGoggleOverlay.isValidPos(player, b)) {
                        detectedBlocks.put(b, getColors(player.level, helmetSettings, b));
                    }
                }
            });
        });
        return detectedBlocks;
    }

    private static Float[] getColors(World world, HashMap<Integer, String[]> settings, BlockPos pos) {
        List<String> names = RenderGoggleOverlay.getBlockNames(world.getBlockState(pos));
        for (int index = 0; index < 16; index++) {
            if (settings.get(index) == null)
                continue;
            if (hasFilter(names, settings.get(index)))
                return COLORS[index];
        }
        return COLORS[0];
    }

    public static boolean hasFilter(List<String> names, String[] test) {
        for (String f : names)
            for (String t : test)
                if (f.equalsIgnoreCase(t))
                    return true;
        return false;
    }

    private static final Float[][] COLORS = new Float[][]{
            new Float[]{1f, 1f, 1f, 1f},
            new Float[]{1f, .65f, 0f, 1f},
            new Float[]{.67f, .85f, .9f, 1f},
            new Float[]{1f, 1f, 0f, 1f},
            new Float[]{1f, .74f, .7f, 1f},
            new Float[]{.09f, .09f, .09f, 1f},
            new Float[]{.36f, .36f, .36f, 1f},
            new Float[]{.93f, .5f, .93f, 1f},
            new Float[]{0f, 0f, 1f, 1f},
            new Float[]{.8f, .52f, .24f, 1f},
            new Float[]{0f, .50f, 0f, 1f},
            new Float[]{1f, 0f, 0f, 1f},
            new Float[]{0f, 0f, 0f, 1f},
    };

    @Override
    public int maxRange(ItemStack stack) {
        return MAX_RADIUS;
    }

    @Override
    public boolean canSeeBlock(PlayerEntity player, ItemStack stack, BlockPos pos, int wavelength) {
        String[] filters = getFilters(stack);
        for (String tag : RenderGoggleOverlay.getBlockNames(player.level.getBlockState(pos)))
            for (String f : filters)
                if (f.equalsIgnoreCase(tag))
                    return true;
        return false;
    }

    public String[] getFilters(ItemStack helmet) {
        HashMap<Integer, String[]> settings = getSettings(helmet);
        List<String> filters = new ArrayList<>();
        for (String[] s : settings.values())
            for (String x : s) {
                boolean valid = true;
                for (String blacklist : MiningGoggles.config.digitalGoggles.filterBlacklist) {
                    if (blacklist.equalsIgnoreCase(x)) {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                    filters.add(x);
            }
        return filters.toArray(new String[0]);
    }

    private HashMap<Integer, String[]> getSettings(ItemStack helmet) {
        if (helmet.hasTag()) {
            HashMap<Integer, String[]> map = new HashMap();
            for (int index = 0; index < 16; index++) {
                List<String> colorFilter = new ArrayList<>();
                CompoundNBT nbt = helmet.getTagElement("color_" + index);
                if (nbt != null) {
                    int enabled = nbt.getInt("active");
                    if (enabled == 0) {
                        continue;
                    }
                    String filter = nbt.getString("filter");
                    if (filter.isEmpty())
                        continue;
                    if (filter.contains(";"))
                        colorFilter.addAll(Arrays.asList(filter.split(";")));
                    if (filter.contains(","))
                        colorFilter.addAll(Arrays.asList(filter.split(",")));
                    else
                        colorFilter.add(filter);
                }
                map.put(index, colorFilter.toArray(new String[0]));
            }
            return map;
        }
        return new HashMap<>();
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
