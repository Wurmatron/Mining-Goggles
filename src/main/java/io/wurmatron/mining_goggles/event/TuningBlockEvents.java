package io.wurmatron.mining_goggles.event;

import io.wurmatron.mining_goggles.api.MiningGogglesApi;
import io.wurmatron.mining_goggles.client.render.RenderGoggleOverlay;
import io.wurmatron.mining_goggles.items.ItemTuningFork;
import io.wurmatron.mining_goggles.items.handler.ItemStackHandlerTuningFork;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class TuningBlockEvents {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        List<String> names = RenderGoggleOverlay.getBlockNames(e.getState());
        if (names.size() > 0) {
            int forkIndex = getFork(e.getPlayer());
            if (forkIndex > -1) {
                ItemStack fork = e.getPlayer().inventory.getItem(forkIndex);
                ItemStackHandlerTuningFork handler = ItemTuningFork.getItemStackHandler(fork);
                if (!handler.getStackInSlot(0).isEmpty()) {
                    String tunedTo = handler.getStackInSlot(0).getTag().getString("type");
                    if (!tunedTo.isEmpty()) { // Is Attuned
                        if (names.contains(tunedTo)) {
                            handler.setStackInSlot(0, incrementTuningFork(handler.getStackInSlot(0)));
                            fork.setDamageValue(fork.getDamageValue() + 1);
                            if (fork.getDamageValue() >= fork.getMaxDamage()) {
                                ItemStack storedCrystal = handler.getStackInSlot(0);
                                e.getPlayer().inventoryMenu.setItem(forkIndex, storedCrystal);
                            }
                        }
                    } else { // Needs to be attuned
                        String bestFit = getBestName(names); // TODO Best Fit config option
                        handler.getStackInSlot(0).getTag().putString("type", bestFit);
                    }
                }
            }
        }
    }

    public static int getFork(Player player) {
        for (int index = 0; index < player.inventoryMenu.getItems().size(); index++) {
            if (player.inventoryMenu.getItems().get(index).getItem() instanceof ItemTuningFork) {
                ItemStackHandlerTuningFork handler = ItemTuningFork.getItemStackHandler(
                        player.inventoryMenu.getItems().get(index));
                if (!handler.getStackInSlot(0).isEmpty()) {
                    if (handler.getStackInSlot(0) == ItemStack.EMPTY || handler.getStackInSlot(0).hasTag() && handler.getStackInSlot(0).getTag().getInt("completed") == 0) {
                        return index;
                    }
                }
            }
        }
        return -1;
    }

    public static ItemStack incrementTuningFork(ItemStack stack) {
        int currentProgress = stack.getTag().getInt("count");
        currentProgress = currentProgress + 1;
        if (currentProgress >= MiningGogglesApi.oreTuning.get(
                stack.getTag().getString("type"))) {
            stack.getTag().putInt("completed", 1);
            int oreWavelength = MiningGogglesApi.oreWavelengths.get(
                    stack.getTag().getString("type"));
            stack.getTag().putInt("min-wavelength", oreWavelength - 5);
            stack.getTag().putInt("max-wavelength", oreWavelength + 5);
        }
        stack.getTag().putInt("count", currentProgress);
        return stack;
    }

    public static String getBestName(List<String> names) {
        for (String name : names) {
            if (name.toLowerCase().contains("ores/") || name.toLowerCase().contains("gems/")) {
                if (MiningGogglesApi.oreWavelengths.containsKey(name)) {
                    return name;
                }
            }
        }
        for (String name : names) {
            if (MiningGogglesApi.oreWavelengths.containsKey(name)) {
                return name;
            }
        }
        return null;
    }
}
