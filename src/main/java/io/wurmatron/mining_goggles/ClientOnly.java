package io.wurmatron.mining_goggles;

import io.wurmatron.mining_goggles.items.MiningItems;
import io.wurmatron.mining_goggles.items.ItemCrystalBag;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientOnly {

  private final IEventBus eventBus;

  /**
   * @param eventBus an instance of the mod event bus
   */
  public ClientOnly(IEventBus eventBus) {
    this.eventBus = eventBus;
  }

  public void registerClientOnlyEvents() {
    registerPropertyOverride();
  }

  public static void registerPropertyOverride() {
    ItemModelsProperties.register(MiningItems.bag, new ResourceLocation("fullness"), ItemCrystalBag::getFullnessPropertyOverride);
  }
}
