package io.wurmatron.mining_goggles.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class ItemCrystal extends Item {

  public ItemCrystal(Properties properties) {
    super(properties);
  }

  public static ItemStack set(int minWavelength, int maxWavelength) {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt("min-wavelength", minWavelength);
    nbt.putInt("max-wavelength", maxWavelength);
    ItemStack stack =  new ItemStack(MiningItems.crystal, 1);
    stack.setTag(nbt);
    return stack;
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    return true;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable World world,
      List<ITextComponent> text, ITooltipFlag flag) {
    super.appendHoverText(stack, world, text, flag);
    if (stack.getTag() != null && !stack.getTag().isEmpty()) {
      text.add(new StringTextComponent(
          "Wavelength: " + stack.getTag().getInt("min-wavelength") + " : "
              + stack.getTag().getInt("max-wavelength")));
    }
  }

  @Override
  public void fillItemCategory(ItemGroup group,
      NonNullList<ItemStack> items) {
      items.add(set(250, 350));
      items.add(set(400, 550));
      items.add(set(600, 850));
      items.add(set(900, 1100));
    super.fillItemCategory(group, items);
  }
}
