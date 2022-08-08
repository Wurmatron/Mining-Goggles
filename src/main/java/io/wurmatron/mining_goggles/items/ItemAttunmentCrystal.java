package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.api.MiningGogglesApi;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemAttunmentCrystal extends ItemCrystal {

  public ItemAttunmentCrystal(Properties properties) {
    super(properties);
  }

  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    return ActionResult.pass(player.getItemInHand(hand));
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable World world,
      List<ITextComponent> text, ITooltipFlag flag) {
    if (stack.getTag() != null && !stack.getTag().isEmpty()) {
      if (stack.getTag().getString("type").isEmpty()) {
        text.add(new StringTextComponent("Unattuned"));
      } else {
        text.add(new StringTextComponent(
            TextFormatting.GRAY +(stack.getTag().getInt("completed") == 1 ? "" : computeProgress(stack) + " % " ) +  "Attuned to '" + TextFormatting.LIGHT_PURPLE
                + stack.getTag().getString("type") + TextFormatting.GRAY + "'"));
      }
    } else {
      super.appendHoverText(stack, world, text, flag);
    }
  }

  @Override
  public boolean isFoil(ItemStack stack) {
    if (stack.hasTag() && stack.getTag().getInt("completed") == 1) {
      return true;
    }
    return super.isFoil(stack);
  }

  public static String computeProgress(ItemStack stack) {
    try {
      return String.format("%.1f", (((double)stack.getTag().getInt("count")) / MiningGogglesApi.oreTuning.get(
          stack.getTag().getString("type"))) * 100);
    } catch (Exception e) {
      return "0.0";
    }
  }

  @Override
  public void fillItemCategory(ItemGroup group,
      NonNullList<ItemStack> items) {
    if (group == MiningGoggles.TAB_GOGGLES) {
      items.add(new ItemStack(MiningItems.attunmentCrystal));
        for(String ore : MiningGogglesApi.oreTuning.keySet()) {
            items.add(create(ore));
        }
    }
  }

  public static ItemStack create(String ore) {
    ItemStack stack = new ItemStack(MiningItems.attunmentCrystal);
    stack.setTag(new CompoundNBT());
    stack.getTag().putInt("completed",1);
    stack.getTag().putInt("count", MiningGogglesApi.oreTuning.get(ore));
    stack.getTag().putString("type", ore);
    stack.getTag().putInt("min-wavelength", MiningGogglesApi.oreWavelengths.get(ore) - 5);
    stack.getTag().putInt("max-wavelength", MiningGogglesApi.oreWavelengths.get(ore) + 5);
    return stack;
  }
}
