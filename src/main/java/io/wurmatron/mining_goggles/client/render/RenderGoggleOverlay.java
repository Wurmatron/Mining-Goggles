package io.wurmatron.mining_goggles.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3d;
import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.api.MiningGogglesCollector;
import io.wurmatron.mining_goggles.config.OreConfigLoader;
import io.wurmatron.mining_goggles.items.MiningItems;
import io.wurmatron.mining_goggles.utils.WavelengthCalculator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.blaze3d.systems.RenderSystem.*;

public class RenderGoggleOverlay {

    public static NonBlockingHashMap<BlockPos, Float[]> activeRendering = new NonBlockingHashMap<>();
    public static final AABB BOX = new AABB(0, 0, 0, 1, 1, 1);

    // Configurable
    public static int MAX_GROWTH_PER_UPDATE = MiningGoggles.config.maxBlocksPerUpdate; // count
    public static int RENDER_UPDATE_TIMER = MiningGoggles.config.renderUpdateTimerTicks; // tick
    public static int RENDER_CLEANUP_TIMER = MiningGoggles.config.renderCleanup; // multiple's of RENDER_UPDATE_TIMER
    public static double FUZZY_RANGE_BEST = MiningGoggles.config.fuzzyRangeBest; // overall total (1 * x)
    public static double FUZZY_RANGE_LOW = MiningGoggles.config.fuzzyRangeLow; // overall total (1 * x)
    public static int RESCAN_INTERVAL = MiningGoggles.config.rescanInterval; // multiple's of RENDER_UPDATE_TIMER
    public static int DAMAGE_INTERVAL = MiningGoggles.config.damageInterval; // sec (in Level ticks at 20tps)

    // Timers
    public static int renderTimer;
    public static int cleanupTimer;
    public static int rescanTimer;
    public static int damageTimer;

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent e) {
        if (e.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS)
            if (!activeRendering.isEmpty()) {
                Minecraft.getInstance().gameRenderer.resetProjectionMatrix(e.getProjectionMatrix());
                GL11.glPushMatrix();
                enableBlend();
                GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                disableTexture();
                GlStateManager._disableCull();
                enableDepthTest();
                GlStateManager._clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
                for (BlockPos pos : activeRendering.keySet()) {
                    Float[] color = activeRendering.get(pos);
                    drawBoundingBoxAtBlockPos(e.getPoseStack(), BOX, color[0], color[1], color[2],
                            color[3], pos);
                }
                GL11.glPopMatrix();
            }
    }

    @SubscribeEvent
    public void onClientTick(PlayerTickEvent e) {
        if (e.side.isClient()) {
            // Check for goggles
            if (!(e.player.getInventory().armor.get(3)
                    .getItem() instanceof MiningGogglesCollector)) {
                activeRendering.clear();
                return;
            }
            // Update Rendering
            if (renderTimer == 0) {
                updateActiveRendering(e.player);
                renderTimer = RENDER_UPDATE_TIMER;
                if (cleanupTimer == 0) {
                    cleanupTimer = RENDER_CLEANUP_TIMER;
                    cleanupRenderEntries(e.player);
                } else {
                    cleanupTimer--;
                }
            } else {
                renderTimer--;
            }

        } else if (e.side.isServer()) {
            if (e.player.getInventory().armor.get(3).getItem() instanceof MiningGogglesCollector) {
                MiningGogglesCollector collector = (MiningGogglesCollector) e.player.getInventory().armor.get(
                        3).getItem();
                if (damageTimer == 0) {
                    collector.damageCrystals(e.player.level.random, e.player.getInventory().armor.get(3));
                    damageTimer = 20 * DAMAGE_INTERVAL;
                } else {
                    damageTimer--;
                }
            }
        }
    }

    private static void updateActiveRendering(Player player) {
        ItemStack stack = player.getInventory().armor.get(3);
        MiningGoggles.EXECUTORS.submit(() -> {
            NonBlockingHashMap<BlockPos, Float[]> detectedBlocks = collectDetectedBlocks(player,
                    stack, rescanTimer == 0);
            // Track for next rescan
            if (rescanTimer == 0 || rescanTimer >= 40 && activeRendering.isEmpty()) {
                rescanTimer = RESCAN_INTERVAL;
            } else {
                rescanTimer--;
            }
            if (activeRendering.size() < detectedBlocks.size()) {
                for (int count = 0; count < MAX_GROWTH_PER_UPDATE; count++) {
                    int index = activeRendering.size() + count;
                    if (detectedBlocks.size() > index) {
                        for (BlockPos pos : detectedBlocks.keySet()) {
                            if (!activeRendering.containsKey(pos) && isValidPos(player, pos)) {
                                activeRendering.put(pos, detectedBlocks.get(pos));
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
        });
    }

    private static void cleanupRenderEntries(Player player) {
        MiningGoggles.EXECUTORS.submit(() -> {
            for (BlockPos pos : activeRendering.keySet()) {
                if (!isValidPos(player, pos)) {
                    activeRendering.remove(pos);
                }
            }
        });
    }

    public static boolean isValidPos(Player player, BlockPos pos) {
        BlockState state = player.level.getBlockState(pos);
        if (state.is(Blocks.AIR))
            return false;
        List<String> names = getBlockNames(state);
        MiningGogglesCollector collector = ((MiningGogglesCollector) player.getInventory().armor.get(
                3).getItem());
        if (!player.getInventory().armor.get(3).getItem().equals(MiningItems.gogglesDigital)) {
            for (String name : names) {
                int wavelength = OreConfigLoader.get(name);
                if (wavelength != -1 &&
                        withinRange(player, player.getInventory().armor.get(3), pos,
                                name, wavelength)
                        && collector.canSeeBlock(player, player.getInventory().armor.get(3), pos,
                        wavelength)) {
                    return true;
                }
            }
        } else {
            for (String name : names) {
                if (withinRange(player, player.getInventory().armor.get(3), pos, name, 0)
                        && collector.canSeeBlock(player, player.getInventory().armor.get(3), pos, 0))
                    return true;
            }
        }
        return false;
    }

    public static List<String> getBlockNames(BlockState block) {
        ItemStack itemBlock = new ItemStack(
                Item.BY_BLOCK.get(block.getBlock()), 1);
        if (!itemBlock.equals(ItemStack.EMPTY)) {
            List<String> ores = new ArrayList<>();
            for (ResourceLocation loc : ItemTags.getAllTags()
                    .getMatchingTags(itemBlock.getItem())) {
                ores.add(loc.getNamespace() + ":" + loc.getPath());
            }
            return ores;
        }
        return new ArrayList<>();
    }

    private static boolean withinRange(Player player, ItemStack stack, BlockPos pos,
                                       String ore, int wavelength) {
        int waveLength = OreConfigLoader.get(ore);
        MiningGogglesCollector collector = ((MiningGogglesCollector) player.getInventory().armor.get(
                3).getItem());
        double range = collector.maxRange(stack);
        if (!stack.getItem().equals(MiningItems.gogglesDigital)) {
            range = getBlockRadius(range, waveLength,
                    WavelengthCalculator.computeWavelength(
                            collector.getWavelength(player.getInventory().armor.get(3), 0))) + .9; // Add .9 to avoid flicking when near edge of block
            if (range == -1 || range == 0) {
                range = collector.maxRange(player.getInventory().armor.get(3));
                range = getBlockRadius(range, waveLength,
                        WavelengthCalculator.computeWavelength(
                                ((MiningGogglesCollector) player.getInventory().armor.get(3)
                                        .getItem()).getWavelength(player.getInventory().armor.get(3), 1)));
            }
        }
        if (isClose(new BlockPos(player.getX(), player.getY(), player.getZ()), pos, range, .99)) {
            BlockState state = player.level.getBlockState(pos);
            return !state.getBlock().is(Blocks.AIR);
        }
        return false;
    }

    private static boolean isClose(BlockPos playerPos, BlockPos blockPos, double range, double fuzzyDistance) {
        if (playerPos.getX() - range - fuzzyDistance < blockPos.getX() && playerPos.getY() - range - fuzzyDistance < blockPos.getY() && playerPos.getZ() - range - fuzzyDistance < blockPos.getZ()) { // Fuzzy Min check
            // Fuzzy Max check
            return playerPos.getX() + range + fuzzyDistance > blockPos.getX() && playerPos.getY() + range + fuzzyDistance > blockPos.getY() && playerPos.getZ() + range + fuzzyDistance > blockPos.getZ();
        }
        return false;
    }

    public static int getBlockRadius(double maxRange, int wavelength,
                                     int[] minMaxHelmetWavelength) {
        if (minMaxHelmetWavelength[0] == -1 || minMaxHelmetWavelength[1] == -1) {
            return -1;
        }
        double middle = ((double) minMaxHelmetWavelength[0] + minMaxHelmetWavelength[1]) / 2;
        double variation = ((double) minMaxHelmetWavelength[1] - minMaxHelmetWavelength[0])
                / minMaxHelmetWavelength[1];
        // No distance degradation
        if (wavelength == middle
                || wavelength < (middle * (1 + FUZZY_RANGE_BEST)) && wavelength > (middle * (1
                - FUZZY_RANGE_BEST))) {
            return (int) maxRange;
        }
        // Dynamic Range
        int diff = (int) Math.abs(middle - wavelength);
        double adjustedRange = ((1 - variation) * diff) / 10;
        adjustedRange = (maxRange + adjustedRange) / maxRange;
        if (adjustedRange < (maxRange * FUZZY_RANGE_LOW)) {
            return 0;
        }
        return (int) adjustedRange;
    }

    private static NonBlockingHashMap<BlockPos, Float[]> collectDetectedBlocks(
            Player player, ItemStack stack, boolean rescan) {
        if (stack.getItem() instanceof MiningGogglesCollector) {
            return ((MiningGogglesCollector) stack.getItem()).findBlocks(player, stack, rescan);
        }
        return new NonBlockingHashMap<>();
    }

    public void drawBoundingBoxAtBlockPos(PoseStack PoseStackIn, AABB aabbIn,
                                          float red, float green, float blue, float alpha, BlockPos pos) {
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double camX = cam.x(), camY = cam.y(), camZ = cam.z();
        drawShapeOutline(PoseStackIn, VoxelShape.create(aabbIn), pos.getX() - camX,
                pos.getY() - camY, pos.getZ() - camZ, red, green, blue, alpha);
    }

    private void drawShapeOutline(PoseStack PoseStack, VoxelShape voxelShape,
                                  double originX, double originY, double originZ, float red, float green, float blue,
                                  float alpha) {
        Matrix4f matrix4f = PoseStack.last().pose();
        MultiBufferSource.Impl renderTypeBuffer = Minecraft.getInstance().renderBuffers()
                .bufferSource();
        VertexConsumer bufferIn = renderTypeBuffer.getBuffer(RenderType.lines());
        voxelShape.forAllEdges((xMin, yMin, zMin, xMax, yMax, zMax) -> {
            bufferIn.vertex(matrix4f, (float) (xMin + originX), (float) (yMin + originY),
                    (float) (zMin + originZ)).color(red, green, blue, alpha).endVertex();
            bufferIn.vertex(matrix4f, (float) (xMax + originX), (float) (yMax + originY),
                    (float) (zMax + originZ)).color(red, green, blue, alpha).endVertex();
        });
        renderTypeBuffer.endBatch(RenderType.lines());
    }

    public static List<BlockPos> generateList(int minX, int minY, int minZ, int maxX,
                                              int maxY, int maxZ) {
        List<BlockPos> locations = new ArrayList<>();
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    locations.add(new BlockPos(x, y, z));
                }
            }
        }
        return locations;
    }
}
