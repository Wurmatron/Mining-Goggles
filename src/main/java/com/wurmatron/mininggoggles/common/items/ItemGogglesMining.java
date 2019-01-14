package com.wurmatron.mininggoggles.common.items;

import static com.wurmatron.mininggoggles.common.registry.ModuleRegistry.getModuleForName;
import static com.wurmatron.mininggoggles.common.registry.ModuleRegistry.modules;

import com.wurmatron.mininggoggles.api.IModule;
import com.wurmatron.mininggoggles.api.ModuleData;
import com.wurmatron.mininggoggles.common.reference.Global;
import com.wurmatron.mininggoggles.common.registry.ModuleRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class ItemGogglesMining extends ItemArmor {

  @SideOnly(Side.CLIENT)
  public static boolean armorDetection = false;

  public ItemGogglesMining(ArmorMaterial material) {
    super(material, 0, EntityEquipmentSlot.HEAD);
    setCreativeTab(CreativeTabs.MISC);
    setUnlocalizedName("gogglesMining");
  }

  @Override
  public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
    super.onArmorTick(world, player, stack);
    // Client Side
    if (world.isRemote) {
      armorDetection = true;
    } else
      // Server Side
      if (stack != ItemStack.EMPTY && stack.hasTagCompound()) {
        if (stack.getTagCompound().hasKey(Global.NBT_MODULES)) {
          for (IModule module : modules) {
            if (stack.getTagCompound().getCompoundTag(Global.NBT_MODULES)
                .hasKey(module.getName())) {
              module.onTick(player, stack.getTagCompound().getString(module.getName()));
            }
          }
        }
      }
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> sub) {
    ModuleData[] modules = new ModuleData[]{
        new ModuleData(getModuleForName("nightVision"), ""),
        new ModuleData(getModuleForName("autoFeed"), "")};
    sub.add(create(4, modules));
  }

  public static ItemStack create(int range, ModuleData[] modules) {
    NBTTagCompound nbt = new NBTTagCompound();
    nbt.setInteger(Global.NBT_RANGE, range);
    if (modules != null && modules.length > 0) {
      NBTTagCompound nbtModules = new NBTTagCompound();
      for (ModuleData module : modules) {
        nbtModules.setString(module.module.getName(), module.data);
      }
      nbt.setTag(Global.NBT_MODULES, nbtModules);
    }
    NBTTagCompound filters = new NBTTagCompound();
    for (int index = 0; index < 16; index++) {
      filters.setString(Global.NBT_COLOR + index, "");
    }
    nbt.setTag(Global.NBT_FILTERS, filters);
    ItemStack stack = new ItemStack(MiningRegistry.gogglesMining, 1);
    stack.setTagCompound(nbt);
    return stack;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World world, List<String> tip,
      ITooltipFlag flag) {
    super.addInformation(stack, world, tip, flag);
    if (stack.hasTagCompound()) {
      if (stack.getTagCompound().hasKey(Global.NBT_RANGE)) {
        tip.add(TextFormatting.GOLD + I18n.translateToLocal("tooltip.range.name") + ": " + stack
            .getTagCompound().getInteger(Global.NBT_RANGE));
      }
      if (stack.getTagCompound().hasKey(Global.NBT_MODULES)) {
        tip.add(TextFormatting.RED + I18n.translateToLocal("tooltip.modules.name"));
        NBTTagCompound nbt = stack.getTagCompound().getCompoundTag(Global.NBT_MODULES);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
          for (IModule module : ModuleRegistry.modules) {
            if (nbt.hasKey(module.getName())) {
              tip.add(TextFormatting.LIGHT_PURPLE + I18n
                  .translateToLocal("module." + module.getName() + ".name") + (
                  nbt.getString(module.getName()).length() > 0 ? nbt
                      .getString(module.getName()) : ""));
            }
          }
        } else {
          tip.add(TextFormatting.GRAY + I18n.translateToLocal("tooltip.holdshift.name"));
        }
      }
    }
  }
}
