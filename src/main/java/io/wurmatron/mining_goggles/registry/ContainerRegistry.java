package io.wurmatron.mining_goggles.registry;

import io.wurmatron.mining_goggles.inventory.ContainerCrystalBag;
import io.wurmatron.mining_goggles.inventory.ContainerMiningGoggles_1;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ContainerRegistry {

  //Containers
  public static ContainerType<ContainerCrystalBag> containerTypeCrystalBag;
  public static ContainerType<ContainerMiningGoggles_1> containerTypeGoggles_1;

  @SubscribeEvent
  public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
    // Crystal Bag
    containerTypeCrystalBag = IForgeContainerType.create(ContainerCrystalBag::createContainerClientSide);
    containerTypeCrystalBag.setRegistryName("container_crystalbag");
    event.getRegistry().register(containerTypeCrystalBag);
    // Goggles 1
    containerTypeGoggles_1 = IForgeContainerType.create(ContainerMiningGoggles_1::createContainerClientSide);
    containerTypeGoggles_1.setRegistryName("container_goggles1");
    event.getRegistry().register(containerTypeGoggles_1);
  }

}