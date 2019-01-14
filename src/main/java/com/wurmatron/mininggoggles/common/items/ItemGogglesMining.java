package com.wurmatron.mininggoggles.common.items;

import static com.wurmatron.mininggoggles.common.registry.ModuleRegistry.getModuleForName;
import static com.wurmatron.mininggoggles.common.registry.ModuleRegistry.modules;

import com.wurmatron.mininggoggles.api.IModule;
import com.wurmatron.mininggoggles.api.ModuleData;
import com.wurmatron.mininggoggles.common.reference.Global;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
}
