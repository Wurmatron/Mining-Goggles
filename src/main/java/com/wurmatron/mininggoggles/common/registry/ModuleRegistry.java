package com.wurmatron.mininggoggles.common.registry;

import com.wurmatron.mininggoggles.api.IModule;
import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;

public class ModuleRegistry {

  public static List<IModule> modules = new ArrayList<>();

  public static IModule getModuleForName(String name) {
    for (IModule module : modules) {
      if (module.getName().equals(name)) {
        return module;
      }
    }
    return null;
  }

  public static void registerModules() {
    modules.add(new IModule() {
      @Override
      public String getName() {
        return "nightVision";
      }

      @Override
      public void onTick(EntityPlayer player, String data) {
        player.addPotionEffect(
            new PotionEffect(Potion.getPotionFromResourceLocation("night_vision"), 60));
      }
    });
    modules.add(new IModule() {
      @Override
      public String getName() {
        return "autoFeed";
      }

      @Override
      public void onTick(EntityPlayer player, String data) {
        if (player.getFoodStats().needFood()) {
          for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.getItem() instanceof ItemFood) {
              ItemFood food = (ItemFood) stack.getItem();
              if (player.getFoodStats().getFoodLevel() + food.getHealAmount(stack) <= 20) {
                player.getFoodStats().addStats(food, stack);
                player.addStat(StatList.getObjectUseStats(food));
                if (stack.getCount() > 1) {
                  stack.setCount(stack.getCount() - 1);
                } else {
                  player.inventory.deleteStack(stack);
                }
              }
            }
          }
        }
      }
    });
    modules.add(new IModule() {
      @Override
      public String getName() {
        return "haste";
      }

      @Override
      public void onTick(EntityPlayer player, String data) {
        player.addPotionEffect(
            new PotionEffect(Potion.getPotionFromResourceLocation("haste"), 60,
                Integer.parseInt(data) > 0 ? Integer.parseInt(data) : 1));
      }
    });

    modules.add(new IModule() {
      @Override
      public String getName() {
        return "movmentSpeed";
      }

      @Override
      public void onTick(EntityPlayer player, String data) {
        player.addPotionEffect(
            new PotionEffect(Potion.getPotionFromResourceLocation("speed"), 60,
                Integer.parseInt(data) > 0 ? Integer.parseInt(data) : 1));
      }
    });
    modules.add(new IModule() {
      @Override
      public String getName() {
        return "resistance";
      }

      @Override
      public void onTick(EntityPlayer player, String data) {
        player.addPotionEffect(
            new PotionEffect(Potion.getPotionFromResourceLocation("resistance"), 60,
                Integer.parseInt(data) > 0 ? Integer.parseInt(data) : 1));
      }
    });
  }


}
