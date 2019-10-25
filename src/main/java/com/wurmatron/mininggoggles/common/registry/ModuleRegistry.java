package com.wurmatron.mininggoggles.common.registry;

import com.wurmatron.mininggoggles.api.IModule;
import com.wurmatron.mininggoggles.common.registry.support.AutoFeedSOLModule;
import com.wurmatron.mininggoggles.common.registry.support.MobRepelModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class ModuleRegistry {

  public static List<IModule> modules = new ArrayList<>();
  public static HashMap<String, Integer> moduleIDCache = new HashMap<>();
  public static HashMap<Integer, String> moduleNameCache = new HashMap<>();

  public static IModule getModuleForName(String name) {
    for (IModule module : modules) {
      if (module.getName().equals(name)) {
        return module;
      }
    }
    return null;
  }

  public static int getModuleIDFromName(String name) {
    if (moduleIDCache.size() <= 0) {
      for (int index = 0; index < modules.size(); index++) {
        moduleIDCache.put(modules.get(index).getName(), index);
      }
    }
    return moduleIDCache.getOrDefault(name, -1);
  }

  public static String getModuleNameFromID(int id) {
    if (moduleNameCache.size() <= 0) {
      for (int index = 0; index < modules.size(); index++) {
        moduleNameCache.put(index, modules.get(index).getName());
      }
    }
    return moduleNameCache.get(id);
  }

  public static String[] getNames() {
    if (moduleIDCache.size() <= 0) {
      getModuleIDFromName("");
    }
    return moduleIDCache.keySet().toArray(new String[0]);
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
            new PotionEffect(Potion.getPotionFromResourceLocation("night_vision"), 240));
      }

      @Override
      public boolean renderOnModel() {
        return true;
      }
    });
    if (Loader.isModLoaded("spiceoflife")) {
      modules.add(new AutoFeedSOLModule());
    } else {
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
                  player.inventory.markDirty();
                }
              }
            }
          }
        }

        @Override
        public boolean renderOnModel() {
          return true;
        }
      });
    }
    modules.add(new IModule() {

      @Override
      public String getName() {
        return "haste";
      }

      @Override
      public void onTick(EntityPlayer player, String data) {
        player.addPotionEffect(
            new PotionEffect(Potion.getPotionFromResourceLocation("haste"), 240,
                1));
      }

      @Override
      public boolean renderOnModel() {
        return false;
      }
    });

    modules.add(new IModule() {

      @Override
      public String getName() {
        return "movementSpeed";
      }

      @Override
      public void onTick(EntityPlayer player, String data) {
        player.addPotionEffect(
            new PotionEffect(Potion.getPotionFromResourceLocation("speed"), 240,
                1));
      }

      @Override
      public boolean renderOnModel() {
        return false;
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
            new PotionEffect(Potion.getPotionFromResourceLocation("resistance"), 240, 1));
      }

      @Override
      public boolean renderOnModel() {
        return true;
      }
    });

    modules.add(new IModule() {
      @Override
      public String getName() {
        return "waterBreathing";
      }

      @Override
      public void onTick(EntityPlayer player, String data) {
        player.addPotionEffect(
            new PotionEffect(Potion.getPotionFromResourceLocation("water_breathing"), 240, 1));
      }

      @Override
      public boolean renderOnModel() {
        return true;
      }
    });
    if(Loader.isModLoaded("viral")) {
      modules.add(new MobRepelModule());
      MinecraftForge.EVENT_BUS.register(new MobRepelModule());
    }
  }
}
