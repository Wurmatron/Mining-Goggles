package com.wurmatron.mininggoggles.client;

import com.wurmatron.mininggoggles.client.render.MiningGoggleEffect;
import com.wurmatron.mininggoggles.common.CommonProxy;
import com.wurmatron.mininggoggles.common.reference.Global;
import com.wurmatron.mininggoggles.common.registry.Registry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {

  @Override
  public void preInit() {
    MinecraftForge.EVENT_BUS.register(new ClientProxy());
  }

  @Override
  public void init() {
    MinecraftForge.EVENT_BUS.register(new MiningGoggleEffect());
  }

  @SubscribeEvent
  public void modelBakeEvent(ModelRegistryEvent e) {
  }

  private static void createModel(Block block, int meta, String name) {
    ModelLoader.setCustomModelResourceLocation(Registry.blockItems.get(block), meta,
        new ModelResourceLocation(Global.MODID + ":" + name, "inventory"));
  }

  private static void createModel(Item item, String name) {
    createModel(item, 0, name);
  }

  private static void createModel(Item item, int meta, String name) {
    ModelLoader.setCustomModelResourceLocation(item, meta,
        new ModelResourceLocation(Global.MODID + ":" + name, "inventory"));
  }


  @Override
  public EntityPlayer getPlayer(MessageContext ctx) {
    return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayer(ctx));
  }

  @Override
  public IThreadListener getThreadFromCTX(MessageContext ctx) {
    return (ctx.side.isClient() ? Minecraft.getMinecraft() : super.getThreadFromCTX(ctx));
  }
}
