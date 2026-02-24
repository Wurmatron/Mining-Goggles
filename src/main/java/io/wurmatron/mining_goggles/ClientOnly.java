package io.wurmatron.mining_goggles;

import io.wurmatron.mining_goggles.items.ItemCrystalBag;
import io.wurmatron.mining_goggles.items.MiningItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
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
        ItemProperties.register(MiningItems.bag, ResourceLocation.parse("fullness"), ItemCrystalBag::getFullnessPropertyOverride);
    }
}
