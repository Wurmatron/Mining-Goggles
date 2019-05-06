package com.wurmatron.mininggoggles;

import com.wurmatron.mininggoggles.api.IModule;
import com.wurmatron.mininggoggles.common.CommonProxy;
import com.wurmatron.mininggoggles.common.items.ItemGogglesMining;
import com.wurmatron.mininggoggles.common.items.MiningRegistry;
import com.wurmatron.mininggoggles.common.network.GuiHandler;
import com.wurmatron.mininggoggles.common.network.NetworkHandler;
import com.wurmatron.mininggoggles.common.reference.Global;
import com.wurmatron.mininggoggles.common.registry.ModuleRegistry;
import com.wurmatron.mininggoggles.common.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;

@Mod(modid = Global.MODID, name = Global.NAME, version = Global.VERSION, dependencies = Global.DEPENDENCIES)
public class MiningGoggles {

  @Mod.Instance(Global.MODID)
  public static MiningGoggles instance;

  @SidedProxy(serverSide = Global.SERVER_PROXY, clientSide = Global.CLIENT_PROXY)
  public static CommonProxy proxy;

  public static Logger logger;

  public static ExecutorService EXECUTORS = Executors
      .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  @EventHandler
  public void onPreInit(FMLPreInitializationEvent e) {
    logger = e.getModLog();
    ModuleRegistry.registerModules();
    MinecraftForge.EVENT_BUS.register(new Registry());
    MiningRegistry.registerItems();
    proxy.preInit();
  }

  @EventHandler
  public void onInit(FMLInitializationEvent e) {
    proxy.init();
    try {
      ResourceLocation RECIPE_GROUP = new ResourceLocation(Global.MODID, "Recipes");
      // Goggles
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "miningGoggles4"),
          RECIPE_GROUP,
          ItemGogglesMining.create(4, new IModule[0]), "LGL", "IXI", 'L', Items.LEATHER, 'G',
          "ingotGold", 'I', "ingotIron", 'X',
          Items.ENDER_PEARL);
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "miningGoggles8"),
          RECIPE_GROUP,
          ItemGogglesMining.create(8, new IModule[0]), "LGL", "IXI", 'L', Items.LEATHER, 'G',
          "gemDiamond", 'I', "ingotGold", 'X',
          ItemGogglesMining.create(4, new IModule[0]));
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "miningGoggles12"),
          RECIPE_GROUP,
          ItemGogglesMining.create(12, new IModule[0]), "LGL", "IXI", 'L', Items.LEATHER, 'G',
          "gemEmerald", 'I', "gemDiamond", 'X',
          ItemGogglesMining.create(8, new IModule[0]));
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "miningGoggles16"),
          RECIPE_GROUP,
          ItemGogglesMining.create(16, new IModule[0]), "LGL", "IXI", 'L', Items.LEATHER, 'G',
          "blockDiamond", 'I', "gemEmerald", 'X',
          ItemGogglesMining.create(12, new IModule[0]));
      // Modules
      GameRegistry
          .addShapedRecipe(new ResourceLocation(Global.MODID, "moduleNightVision"), RECIPE_GROUP,
              new ItemStack(MiningRegistry.itemModule, 1, 1), "PPP", "XDX", "PPP", 'P',
              createPotion("minecraft:night_vision"), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
      GameRegistry
          .addShapedRecipe(new ResourceLocation(Global.MODID, "moduleNightVision2"), RECIPE_GROUP,
              new ItemStack(MiningRegistry.itemModule, 1, 1), "PXP", "PDP", "PXP", 'P',
              createPotion("minecraft:night_vision"), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
      GameRegistry
          .addShapedRecipe(new ResourceLocation(Global.MODID, "moduleAutoFeed"), RECIPE_GROUP,
              new ItemStack(MiningRegistry.itemModule, 1, 0), "PPP", "XDX", "PPP", 'P',
              new ItemStack(Items.GOLDEN_APPLE, 1, 1), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
      GameRegistry
          .addShapedRecipe(new ResourceLocation(Global.MODID, "moduleAutoFeed2"), RECIPE_GROUP,
              new ItemStack(MiningRegistry.itemModule, 1, 0), "PXP", "PDP", "PXP", 'P',
              new ItemStack(Items.GOLDEN_APPLE, 1, 1), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "moduleHaste"), RECIPE_GROUP,
          new ItemStack(MiningRegistry.itemModule, 1, 2), "PPP", "XDX", "PPP", 'P',
          new ItemStack(Items.GOLDEN_CARROT, 1, 0), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "moduleHaste2"), RECIPE_GROUP,
          new ItemStack(MiningRegistry.itemModule, 1, 2), "PXP", "PDP", "PXP", 'P',
          new ItemStack(Items.GOLDEN_CARROT, 1, 0), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "moduleSpeed"), RECIPE_GROUP,
          new ItemStack(MiningRegistry.itemModule, 1, 3), "PPP", "XDX", "PPP", 'P',
          new ItemStack(Items.CAKE, 1, 0), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "moduleSpeed2"), RECIPE_GROUP,
          new ItemStack(MiningRegistry.itemModule, 1, 3), "PXP", "PDP", "PXP", 'P',
          new ItemStack(Items.CAKE, 1, 0), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
      GameRegistry
          .addShapedRecipe(new ResourceLocation(Global.MODID, "moduleResistance"), RECIPE_GROUP,
              new ItemStack(MiningRegistry.itemModule, 1, 4), "PPP", "XDX", "PPP", 'P',
              new ItemStack(Items.SHIELD, 1,
                  OreDictionary.WILDCARD_VALUE), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
      GameRegistry
          .addShapedRecipe(new ResourceLocation(Global.MODID, "moduleResistance2"), RECIPE_GROUP,
              new ItemStack(MiningRegistry.itemModule, 1, 4), "PXP", "PDP", "PXP", 'P',
              new ItemStack(Items.SHIELD, 1,
                  OreDictionary.WILDCARD_VALUE), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
      GameRegistry
          .addShapedRecipe(new ResourceLocation(Global.MODID, "moduleWaterBreathing"), RECIPE_GROUP,
              new ItemStack(MiningRegistry.itemModule, 1, 5), "PPP", "XDX", "PPP", 'P',
              createPotion("minecraft:water_breathing"), 'X', Items.ENDER_EYE, 'D',
              Blocks.END_STONE);
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "moduleWaterBreathing2"),
          RECIPE_GROUP, new ItemStack(MiningRegistry.itemModule, 1, 5), "PXP", "PDP", "PXP", 'P',
          createPotion("minecraft:water_breathing"), 'X', Items.ENDER_EYE, 'D', Blocks.END_STONE);
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  private ItemStack createPotion(String id) {
    NBTTagCompound nbt = new NBTTagCompound();
    nbt.setString("Potion", id);
    ItemStack potion = new ItemStack(Items.POTIONITEM);
    potion.setTagCompound(nbt);
    return potion;
  }

  @EventHandler
  public void onPostInit(FMLPostInitializationEvent e) {
    NetworkHandler.registerPackets();
    NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    proxy.postInit();
  }
}
