package io.wurmatron.mining_goggles.registry;

import io.wurmatron.mining_goggles.inventory.ContainerCrystalBag;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ContainerRegistry {

  //Containers
  public static ContainerType<ContainerCrystalBag> containerTypeMiningGoggles;


  @SubscribeEvent
  public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
    containerTypeMiningGoggles = IForgeContainerType.create(ContainerCrystalBag::createContainerClientSide);
    containerTypeMiningGoggles.setRegistryName("container_crystalbag");
    event.getRegistry().register(containerTypeMiningGoggles);
  }

}
