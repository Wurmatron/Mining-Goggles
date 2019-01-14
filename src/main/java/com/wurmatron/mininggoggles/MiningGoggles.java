package com.wurmatron.mininggoggles;

import com.wurmatron.mininggoggles.common.CommonProxy;
import com.wurmatron.mininggoggles.common.items.MiningRegistry;
import com.wurmatron.mininggoggles.common.network.GuiHandler;
import com.wurmatron.mininggoggles.common.network.NetworkHandler;
import com.wurmatron.mininggoggles.common.reference.Global;
import com.wurmatron.mininggoggles.common.registry.ModuleRegistry;
import com.wurmatron.mininggoggles.common.registry.Registry;
import java.util.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = Global.MODID, name = Global.NAME, version = Global.VERSION, dependencies = Global.DEPENDENCIES)
public class MiningGoggles {

  @Mod.Instance(Global.MODID)
  public static MiningGoggles instance;

  @SidedProxy(serverSide = Global.SERVER_PROXY, clientSide = Global.CLIENT_PROXY)
  public static CommonProxy proxy;

  public static Logger logger;

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
  }

  @EventHandler
  public void onPostInit(FMLPostInitializationEvent e) {
    NetworkHandler.registerPackets();
    NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    proxy.postInit();
  }
}
