package com.wurmatron.mininggoggles.client;

import com.wurmatron.mininggoggles.client.render.MiningGoggleEffect;
import com.wurmatron.mininggoggles.common.CommonProxy;
import com.wurmatron.mininggoggles.common.items.ItemModule;
import com.wurmatron.mininggoggles.common.items.MiningRegistry;
import com.wurmatron.mininggoggles.common.network.GuiHandler;
import com.wurmatron.mininggoggles.common.network.NetworkHandler;
import com.wurmatron.mininggoggles.common.network.packets.OpenGuiMessage;
import com.wurmatron.mininggoggles.common.reference.Global;
import com.wurmatron.mininggoggles.common.registry.Registry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

  public static KeyBinding[] keyBindings;

  @Override
  public void preInit() {
    MinecraftForge.EVENT_BUS.register(new ClientProxy());
    keyBindings = new KeyBinding[]{new KeyBinding("key.toggle.desc", Keyboard.KEY_N,
        "key.mininggoggles.category"),
        new KeyBinding("key.filter.desc", Keyboard.KEY_B, "key.mininggoggles.category")};
    for (KeyBinding key : keyBindings) {
      ClientRegistry.registerKeyBinding(key);
    }
  }

  @Override
  public void init() {
    MinecraftForge.EVENT_BUS.register(new MiningGoggleEffect());
  }

  @SubscribeEvent
  public void modelBakeEvent(ModelRegistryEvent e) {
    createModel(MiningRegistry.gogglesMining,
        MiningRegistry.gogglesMining.getUnlocalizedName().substring(5));
    for (int index = 0; index < ItemModule.metaItems.length; index++) {
      createModel(MiningRegistry.itemModule, index, ItemModule.metaItems[index]);
    }
  }

  @SubscribeEvent
  public void textureStich(TextureStitchEvent.Pre e) {
    e.getMap().registerSprite(new ResourceLocation(Global.MODID, "gui/moduleicon"));
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

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onClientTick(TickEvent.ClientTickEvent event) throws Exception {
    if (keyBindings[0].isKeyDown()) {
      NetworkHandler.sendToServer(new OpenGuiMessage(GuiHandler.GOGGLES_MODULES,
          Minecraft.getMinecraft().player.getPosition()));
    } else if (keyBindings[1].isKeyDown()) {
      NetworkHandler.sendToServer(new OpenGuiMessage(GuiHandler.GOGGLES_FILTER,
          Minecraft.getMinecraft().player.getPosition()));
    }
  }
}
