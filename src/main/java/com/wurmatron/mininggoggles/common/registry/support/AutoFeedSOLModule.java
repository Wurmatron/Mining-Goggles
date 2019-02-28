package com.wurmatron.mininggoggles.common.registry.support;

import com.wurmatron.mininggoggles.api.IModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandlerModifiable;
import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.food.FoodValues;
import squeek.spiceoflife.helpers.FoodHelper;
import squeek.spiceoflife.helpers.MealPrioritizationHelper;
import squeek.spiceoflife.inventory.FoodContainerInventory;
import squeek.spiceoflife.items.ItemFoodContainer;

public class AutoFeedSOLModule implements IModule {

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
          if (!FoodHelper.canFoodDiminish(stack) || FoodHelper.isFood(stack) && player
              .getFoodStats().needFood() && player.getFoodStats().getFoodLevel() < 20) {
            FoodValues values = AppleCoreAPI.accessor.getFoodValuesForPlayer(stack, player);
            if (values.hunger > 0 && player.getFoodStats().getFoodLevel() + values.hunger <= 20) {
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
        } else if (stack.getItem() instanceof ItemFoodContainer) {
          // Modified version of https://github.com/squeek502/SpiceOfLife/blob/1.12/java/squeek/spiceoflife/items/ItemFoodContainer.java#L254
          if (((ItemFoodContainer) stack.getItem()).canPlayerEatFrom(player, stack)) {
            IItemHandlerModifiable inventory = new FoodContainerInventory(
                (ItemFoodContainer) stack.getItem(), stack).getItemHandler();
            int slotWithBestFood = MealPrioritizationHelper
                .findBestFoodForPlayerToEat(player, inventory);
            ItemStack foodToEat = inventory.getStackInSlot(slotWithBestFood);
            if (!foodToEat.isEmpty()) {
              FoodValues values = AppleCoreAPI.accessor.getFoodValuesForPlayer(stack, player);
              if (values.hunger > 0 && player.getFoodStats().getFoodLevel() + values.hunger <= 20) {
                ItemStack result = foodToEat.onItemUseFinish(player.world, player);
                result = ForgeEventFactory.onItemUseFinish(player, foodToEat, 32, result);
                if (result.isEmpty() || result.getCount() <= 0) {
                  result = ItemStack.EMPTY;
                }
                inventory.setStackInSlot(slotWithBestFood, result);
              }
            }
          }
        }
      }
    }
  }

  @Override
  public boolean renderOnModel() {
    return true;
  }
}
