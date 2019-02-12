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
import net.minecraft.init.Items;
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
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "miningGoggles4"),
          new ResourceLocation(Global.MODID, "Recipes"),
          ItemGogglesMining.create(4, new IModule[0]), "LGL", "IXI", 'L', Items.LEATHER, 'G',
          "ingotGold", 'I', "ingotIron", 'X',
          Items.ENDER_PEARL);
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "miningGoggles8"),
          new ResourceLocation(Global.MODID, "Recipes"),
          ItemGogglesMining.create(8, new IModule[0]), "LGL", "IXI", 'L', Items.LEATHER, 'G',
          "gemDiamond", 'I', "ingotGold", 'X',
          ItemGogglesMining.create(4, new IModule[0]));
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "miningGoggles12"),
          new ResourceLocation(Global.MODID, "Recipes"),
          ItemGogglesMining.create(12, new IModule[0]), "LGL", "IXI", 'L', Items.LEATHER, 'G',
          "gemEmerald", 'I', "gemDiamond", 'X',
          ItemGogglesMining.create(8, new IModule[0]));
      GameRegistry.addShapedRecipe(new ResourceLocation(Global.MODID, "miningGoggles16"),
          new ResourceLocation(Global.MODID, "Recipes"),
          ItemGogglesMining.create(16, new IModule[0]), "LGL", "IXI", 'L', Items.LEATHER, 'G',
          "blockDiamond", 'I', "gemEmerald", 'X',
          ItemGogglesMining.create(12, new IModule[0]));
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  @EventHandler
  public void onPostInit(FMLPostInitializationEvent e) {
    NetworkHandler.registerPackets();
    NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    proxy.postInit();
  }
}
