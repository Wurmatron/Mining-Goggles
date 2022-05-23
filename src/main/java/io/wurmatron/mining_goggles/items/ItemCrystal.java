package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
    ItemStack stack = new ItemStack(MiningItems.crystal, 1);
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
      String min = stack.getTag().getInt("min-wavelength") > 0 ? "" + stack.getTag()
          .getInt("min-wavelength") : "?";
      String max = stack.getTag().getInt("max-wavelength") > 0 ? "" + stack.getTag()
          .getInt("max-wavelength") : "?";
      text.add(new StringTextComponent(
          "Wavelength: " + min + " ->  " + max));
    }
  }

  @Override
  public ActionResult<ItemStack> use(World world, PlayerEntity player,
      Hand hand) {
    CompoundNBT nbt = player.getItemInHand(hand).getTag();
    Random RAND = world.random;
    if (nbt == null) {
      nbt = new CompoundNBT();
    }
    if (nbt.getInt("min-wavelength") == 0) {
      int rarity = RAND.nextInt(3);
      if (rarity == 0) {
        nbt.putInt("min-wavelength", RAND.nextInt(500));
        nbt.putInt("max-wavelength", 500 + RAND.nextInt(500));
      } else if (rarity == 1) {
        int type = random.nextInt(4);
        int base = 500;
        int shift = 250;
        int starting = 0;
        if (type == 1) {
          base = 250;
          shift = 200;
          starting = 250;
        } else if (type == 2) {
          starting = 500;
          shift = 200;
          base = 250;
        } else if (type == 3) {
          starting = 750;
          shift = 200;
          base = 100;
        }
        nbt.putInt("min-wavelength", starting + RAND.nextInt(base));
        nbt.putInt("max-wavelength", starting + shift + RAND.nextInt(base));
      } else {
        int shift = RAND.nextInt(25);
        int base = 100;
        shift = shift * 100;
        nbt.putInt("min-wavelength", (base) + shift);
        nbt.putInt("max-wavelength", (base + 50) + shift);
      }
      player.getItemInHand(hand).setTag(nbt);
      return ActionResult.pass(player.getItemInHand(hand));
    }
    return super.use(world, player, hand);
  }

  @Override
  public void fillItemCategory(ItemGroup group,
      NonNullList<ItemStack> items) {
    if (group == MiningGoggles.TAB_GOGGLES) {
      items.add(set(250, 350));
      items.add(set(400, 550));
      items.add(set(600, 850));
      items.add(set(900, 1100));
    }
    super.fillItemCategory(group, items);
  }

  public static int[] getWavelength(ItemStack stack) {
    if (stack != null && stack.hasTag() && !stack.getTag().isEmpty()) {
      return new int[]{stack.getTag().getInt("min-wavelength"),
          stack.getTag().getInt("max-wavelength")};
    }
    return new int[]{-1, -1};
  }

  @Override
  public Rarity getRarity(ItemStack stack) {
    if (stack.hasTag() && stack.getTag() != null
        && stack.getTag().getInt("min-wavelength") > 0) {
      int max = stack.getTag().getInt("max-wavelength");
      int min = stack.getTag().getInt("min-wavelength");
      int diff = max - min;
      if (diff <= 50) {
        return Rarity.EPIC;
      } else if (diff <= 100) {
        return Rarity.RARE;
      } else if (diff <= 250) {
        return Rarity.UNCOMMON;
      }
    }
    return super.getRarity(stack);
  }

  @Override
  public boolean isFoil(ItemStack stack) {
    if (getRarity(stack).equals(Rarity.EPIC)) {
      return true;
    }
    return false;
  }
}
