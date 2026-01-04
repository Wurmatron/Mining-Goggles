package io.wurmatron.mining_goggles.inventory;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.registry.ContainerRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ContainerFilter extends Container {

    public ItemStack helmet;

    public ContainerFilter(int id, ItemStack helmet) {
        super(ContainerRegistry.containerTypeGoggles_Digital, id);
        this.helmet = helmet;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static ContainerFilter createContainerClientSide(int windowID,
                                                            PlayerInventory playerInventory, PacketBuffer extraData) {
        try {
            return new ContainerFilter(windowID, ItemStack.EMPTY);
        } catch (IllegalArgumentException e) {
            MiningGoggles.LOGGER.warn(e);
        }
        return null;
    }

    public static ContainerFilter createContainerServerSide(int windowID, PlayerInventory playerInventory, ItemStack helmet) {
        return new ContainerFilter(windowID, helmet);
    }
}
