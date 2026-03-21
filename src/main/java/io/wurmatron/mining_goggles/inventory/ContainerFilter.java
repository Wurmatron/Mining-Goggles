package io.wurmatron.mining_goggles.inventory;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.registry.ContainerRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ContainerFilter extends AbstractContainerMenu {

    public ItemStack helmet;

    public ContainerFilter(int id, ItemStack helmet) {
        super(ContainerRegistry.containerTypeGoggles_Digital.get(), id);
        this.helmet = helmet;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static ContainerFilter createContainerClientSide(int windowID,
                                                            Inventory Inventory, FriendlyByteBuf extraData) {
        try {
            return new ContainerFilter(windowID, ItemStack.EMPTY);
        } catch (IllegalArgumentException e) {
            MiningGoggles.LOGGER.warn(e);
        }
        return null;
    }

    public static ContainerFilter createContainerServerSide(int windowID, Inventory Inventory, ItemStack helmet) {
        return new ContainerFilter(windowID, helmet);
    }
}
