package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class ItemAdjustableCrystal extends ItemCrystal {

  public ItemAdjustableCrystal(Properties properties) {
    super(properties);
  }

  @Override
  public void fillItemCategory(ItemGroup group,
      NonNullList<ItemStack> items) {
    for (ItemStack stack : items) {
      if (stack.getItem().equals(MiningItems.crystal)) {
        items.remove(stack);
      }
    }
    if (group == MiningGoggles.TAB_GOGGLES) {
      items.add(new ItemStack(MiningItems.adjustableCrystal, 1));
    }
  }

  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    return ActionResult.pass(player.getItemInHand(hand));
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable World world,
      List<ITextComponent> text, ITooltipFlag flag) {
    if (stack.getTag() != null && !stack.getTag().isEmpty() && stack.getTag().getInt("min-wavelength") == 0) {
      text.add(new StringTextComponent("Unattuned"));
    } else
      super.appendHoverText(stack,world,text,flag);
  }
}
