package com.wurmatron.mininggoggles.common.registry.support;

import com.wurmatron.mininggoggles.api.IModule;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import wurmatron.viral.Viral;
import wurmatron.viral.common.items.ItemBasic;

public class MobRepelModule implements IModule {

  private static HashMap<UUID, Integer> mobsDistracted = new HashMap<>();

  @Override
  public String getName() {
    return "mobRepel";
  }

  @Override
  public void onTick(EntityPlayer player, String data) {
    if (player.world.getWorldTime() % 60 == 0 && mobsDistracted.containsKey(player.getGameProfile().getId())) {
      consumeMobMash(player, mobsDistracted.get(player.getGameProfile().getId()) / 20);
    }
  }

  private void consumeMobMash(EntityPlayer player, int amount) {
    for (int index = 0; index < player.inventory.getSizeInventory(); index++) {
      if (player.inventory.getStackInSlot(index).getItem() instanceof ItemBasic
          && player.inventory.getStackInSlot(index).getItem() == Viral.mobMash) {
        ItemStack stack = player.inventory.getStackInSlot(index);
        if (stack.getCount() >= amount) {
          stack.setCount(stack.getCount() - amount);
          player.inventory.setInventorySlotContents(index, stack);
          mobsDistracted.put(player.getGameProfile().getId(), 0);
          break;
        } else {
          player.inventory.deleteStack(stack);
          mobsDistracted.put(player.getGameProfile().getId(), 0);
        }
      }
    }
  }

  @Override
  public boolean renderOnModel() {
    return false;
  }

  @SubscribeEvent
  public void onEntitySetTarget(LivingSetAttackTargetEvent e) {
    if (e.getTarget() != null && e.getTarget() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getTarget();
      if (player.inventory.hasItemStack(new ItemStack(Viral.mobMash))) {
        mobsDistracted.put(player.getGameProfile().getId(), mobsDistracted.getOrDefault(player.getGameProfile().getId(), 0) + 1);
        ((EntityLiving) e.getEntity()).setAttackTarget(null);
      }
    }
  }
}
