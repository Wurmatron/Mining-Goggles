package io.wurmatron.mining_goggles.registry;

import io.wurmatron.mining_goggles.inventory.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ContainerRegistry {

    //Containers
    public static MenuType<ContainerCrystalBag> containerTypeCrystalBag;
    public static MenuType<ContainerMiningGoggles_1> containerTypeGoggles_1;
    public static MenuType<ContainerMiningGoggles_2> containerTypeGoggles_2;
    public static MenuType<ContainerTuningFork> containerTuningFork;
    public static MenuType<ContainerFilter> containerTypeGoggles_Digital;

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        // Crystal Bag
        containerTypeCrystalBag = IForgeContainerType.create(ContainerCrystalBag::createContainerClientSide);
        containerTypeCrystalBag.setRegistryName("container_crystalbag");
        event.getRegistry().register(containerTypeCrystalBag);
        // Goggles 1
        containerTypeGoggles_1 = IForgeContainerType.create(ContainerMiningGoggles_1::createContainerClientSide);
        containerTypeGoggles_1.setRegistryName("container_goggles1");
        event.getRegistry().register(containerTypeGoggles_1);
        // Goggles 2 (Goggles Upgraded)
        containerTypeGoggles_2 = IForgeContainerType.create(ContainerMiningGoggles_2::createContainerClientSide);
        containerTypeGoggles_2.setRegistryName("container_goggles2");
        event.getRegistry().register(containerTypeGoggles_2);
        // Tuning Fork
        containerTuningFork = IForgeContainerType.create(ContainerTuningFork::createContainerClientSide);
        containerTuningFork.setRegistryName("container_tuningfork");
        event.getRegistry().register(containerTuningFork);
        // Goggles (Digital)
        containerTypeGoggles_Digital = IForgeContainerType.create(ContainerFilter::createContainerClientSide);
        containerTypeGoggles_Digital.setRegistryName("container_filterdigital");
        event.getRegistry().register(containerTypeGoggles_Digital);
    }

}
