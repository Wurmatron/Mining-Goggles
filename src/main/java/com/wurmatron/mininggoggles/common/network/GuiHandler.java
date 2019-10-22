package com.wurmatron.mininggoggles.common.network;

import com.wurmatron.mininggoggles.client.gui.GuiGoggleModules;
import com.wurmatron.mininggoggles.client.gui.GuiGogglesFilter;
import com.wurmatron.mininggoggles.common.items.ItemGogglesMining;
import com.wurmatron.mininggoggles.common.network.container.ContainerModules;
import com.wurmatron.mininggoggles.common.network.container.InventoryGoggles;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

  public static final int GOGGLES_FILTER = 0;
  public static final int GOGGLES_MODULES = 1;

  @Nullable
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (ID == GOGGLES_MODULES) {
      return new ContainerModules(player, player.inventory,
          new InventoryGoggles(getActiveGoggles(player)));
    }
    return new ContainerPlayer(player.inventory, false, player);
  }

  @Nullable
  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (getActiveGoggles(player) != ItemStack.EMPTY) {
      switch (ID) {
        case (GOGGLES_FILTER):
          return new GuiGogglesFilter(getActiveGoggles(player));
        case (GOGGLES_MODULES):
          return new GuiGoggleModules(
              new ContainerModules(player, player.inventory,
                  new InventoryGoggles(getActiveGoggles(player))));
        default:
          return null;
      }
    }
    return null;
  }

  public static ItemStack getActiveGoggles(EntityPlayer player) {
    if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand()
        .getItem() instanceof ItemGogglesMining) {
      return player.getHeldItemMainhand();
    } else if (player.inventory.armorInventory.get(3) != ItemStack.EMPTY && player.inventory
        .armorInventory.get(3).getItem() instanceof ItemGogglesMining) {
      return player.inventory.armorInventory.get(3);
    }
    return ItemStack.EMPTY;
  }
}
