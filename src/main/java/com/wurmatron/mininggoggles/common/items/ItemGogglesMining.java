package com.wurmatron.mininggoggles.common.items;

import static com.wurmatron.mininggoggles.common.registry.ModuleRegistry.getModuleForName;
import static com.wurmatron.mininggoggles.common.registry.ModuleRegistry.modules;

import com.wurmatron.mininggoggles.api.IModule;
import com.wurmatron.mininggoggles.common.network.GuiHandler;
import com.wurmatron.mininggoggles.common.network.NetworkHandler;
import com.wurmatron.mininggoggles.common.network.container.InventoryGoggles;
import com.wurmatron.mininggoggles.common.network.packets.OpenGuiMessage;
import com.wurmatron.mininggoggles.common.reference.Global;
import com.wurmatron.mininggoggles.common.registry.ModuleRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import org.lwjgl.input.Keyboard;

public class ItemGogglesMining extends ItemArmor{

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
        if (stack.getTagCompound().hasKey(Global.NBT_MODULES)
            && world.getTotalWorldTime() % 20 == 0) {
          NBTTagList moduleNBT = stack.getTagCompound()
              .getTagList(Global.NBT_MODULES, NBT.TAG_COMPOUND);
          ItemStack lastModule = ItemStack.EMPTY;
          for (int index = 0; index < InventoryGoggles.INV_SIZE; index++) {
            ItemStack moduleStack = new ItemStack(moduleNBT.getCompoundTagAt(index));
            if (!moduleStack.isEmpty() && !moduleStack.isItemEqual(lastModule)) {
              lastModule = moduleStack;
              IModule module = ModuleRegistry
                  .getModuleForName(
                      ModuleRegistry.getModuleNameFromID(moduleStack.getItemDamage()));
              if (module != null) {
                module.onTick(player,
                    moduleStack.getTagCompound() != null ? moduleStack.getTagCompound().toString()
                        : "");
              }
            }
          }
        }
      }
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> sub) {
    if (tab == CreativeTabs.MISC) {
      try {
        IModule[] modules = new IModule[]{
            getModuleForName("nightVision"),
            getModuleForName("autoFeed")};
        sub.add(create(3, modules));
        sub.add(create(4, modules));
        sub.add(create(6, modules));
        sub.add(create(8, modules));
        sub.add(create(16, modules));
        sub.add(create(32, modules));
        sub.add(create(64, modules));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static ItemStack create(int range, IModule[] modules) throws Exception {
    NBTTagCompound nbt = new NBTTagCompound();
    nbt.setInteger(Global.NBT_RANGE, range);
    if (modules != null && modules.length > 0) {
      NBTTagList nbtModules = new NBTTagList();
      if (modules.length > InventoryGoggles.INV_SIZE) {
        throw new Exception(
            "Too many modules, max is " + InventoryGoggles.INV_SIZE + " you tried adding "
                + modules.length);
      }
      for (IModule module : modules) {
        ItemStack stack = new ItemStack(MiningRegistry.itemModule, 1,
            ModuleRegistry.getModuleIDFromName(module.getName()));
        nbtModules.appendTag(stack.writeToNBT(new NBTTagCompound()));
      }
      if (modules.length < InventoryGoggles.INV_SIZE) {
        int amountLeft = InventoryGoggles.INV_SIZE - modules.length;
        for (int index = amountLeft; index < InventoryGoggles.INV_SIZE; index++) {
          nbtModules.appendTag(ItemStack.EMPTY.writeToNBT(new NBTTagCompound()));
        }
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
          NBTTagList moduleNBT = stack.getTagCompound()
              .getTagList(Global.NBT_MODULES, NBT.TAG_COMPOUND);
          ItemStack lastModule = ItemStack.EMPTY;
          for (int index = 0; index < InventoryGoggles.INV_SIZE; index++) {
            ItemStack moduleStack = new ItemStack(moduleNBT.getCompoundTagAt(index));
            if (!moduleStack.isEmpty() && !moduleStack.isItemEqual(lastModule)) {
              lastModule = moduleStack;
              IModule module = ModuleRegistry
                  .getModuleForName(
                      ModuleRegistry.getModuleNameFromID(moduleStack.getItemDamage()));
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

  @Override
  public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
        || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) && !entity.world.isRemote) {
      NetworkHandler
          .sendToServer(new OpenGuiMessage(GuiHandler.GOGGLES_FILTER, entity.getPosition()));
    }
    return super.onEntitySwing(entity, stack);
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot,
      String type) {
    return Global.MODID + ":textures/models/" + getTexture(stack) + ".png";
  }

  public static int getRange(ItemStack stack) {
    return stack.hasTagCompound() && stack.getTagCompound().hasKey(Global.NBT_RANGE) ? stack
        .getTagCompound().getInteger(Global.NBT_RANGE) : 0;
  }

  public static IModule[] getModules(ItemStack stack) {
    List<IModule> modules = new ArrayList<>();
    if (stack.getTagCompound().hasKey(Global.NBT_MODULES)) {
      NBTTagList moduleNBT = stack.getTagCompound()
          .getTagList(Global.NBT_MODULES, NBT.TAG_COMPOUND);
      ItemStack lastModule = ItemStack.EMPTY;
      for (int index = 0; index < InventoryGoggles.INV_SIZE; index++) {
        ItemStack moduleStack = new ItemStack(moduleNBT.getCompoundTagAt(index));
        if (!moduleStack.isEmpty() && !moduleStack.isItemEqual(lastModule)) {
          lastModule = moduleStack;
          IModule module = ModuleRegistry
              .getModuleForName(
                  ModuleRegistry.getModuleNameFromID(moduleStack.getItemDamage()));
          if (module != null) {
            modules.add(module);
          }
        }
      }
    }
    return sortModulesBasedOnID(modules.toArray(new IModule[0]));
  }

  private static IModule[] sortModulesBasedOnID(IModule[] modules) {
    TreeMap<Integer, IModule> sortedModules = new TreeMap<>(Collections.reverseOrder());
    for (IModule module : modules) {
      sortedModules.put(ModuleRegistry.getModuleIDFromName(module.getName()), module);
    }
    return sortedModules.descendingMap().values().toArray(new IModule[0]);
  }

  public String getTexture(ItemStack stack) {
    IModule[] modules = getModules(stack);
    StringBuilder builder = new StringBuilder();
    builder.append("goggles");
    if (modules.length > 0) {
      for (IModule module : modules) {
        if (module.renderOnModel()) {
          builder.append("_" + module.getName());
        }
      }
      return builder.toString();
    }
    return "goggles_default";
  }


}
