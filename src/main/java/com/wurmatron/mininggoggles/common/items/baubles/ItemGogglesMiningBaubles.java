package com.wurmatron.mininggoggles.common.items.baubles;

import static com.wurmatron.mininggoggles.common.registry.ModuleRegistry.getModuleForName;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import com.wurmatron.mininggoggles.api.IModule;
import com.wurmatron.mininggoggles.common.items.ItemGogglesMining;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemGogglesMiningBaubles extends ItemGogglesMining implements IBauble {

  public ItemGogglesMiningBaubles(ArmorMaterial material) {
    super(material);
    setCreativeTab(CreativeTabs.MISC);
    setUnlocalizedName("gogglesMining");
  }

  @Override
  public BaubleType getBaubleType(ItemStack stack) {
    return BaubleType.HEAD;
  }

  @Override
  public void onWornTick(ItemStack stack, EntityLivingBase player) {
    // Uncomment to enables modules for baubles goggles
//    if (player instanceof EntityPlayer) {
//      onArmorTick(player.world, (EntityPlayer) player, stack);
//    }
  }

  @Override
  public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
    ItemGogglesMining.armorDetection = true;
  }

  @Override
  public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
    ItemGogglesMining.armorDetection = false;
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

  @Override
  public void addInformation(ItemStack stack, @Nullable World world, List<String> tip,
      ITooltipFlag flag) {
    super.addInformation(stack, world, tip, flag);
    tip.add(I18n.translateToLocal("tooltip.baubles.name"));
  }

  @SubscribeEvent
  public void onPlayerJoin(PlayerLoggedInEvent e) {
    ItemGogglesMining.armorDetection = false;
    if (BaublesApi.getBaublesHandler(e.player).getStackInSlot(4).getItem() instanceof ItemGogglesMining) {
      ItemGogglesMining.armorDetection = true;
    }
  }
}
