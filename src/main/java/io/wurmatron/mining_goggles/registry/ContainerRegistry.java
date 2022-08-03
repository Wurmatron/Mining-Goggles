package io.wurmatron.mining_goggles.registry;

import io.wurmatron.mining_goggles.inventory.ContainerCrystalBag;
import io.wurmatron.mining_goggles.inventory.ContainerMiningGoggles_1;
import io.wurmatron.mining_goggles.inventory.ContainerMiningGoggles_2;
import io.wurmatron.mining_goggles.inventory.ContainerTuningFork;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ContainerRegistry {

  //Containers
  public static ContainerType<ContainerCrystalBag> containerTypeCrystalBag;
  public static ContainerType<ContainerMiningGoggles_1> containerTypeGoggles_1;
  public static ContainerType<ContainerMiningGoggles_2> containerTypeGoggles_2;
  public static ContainerType<ContainerTuningFork> containerTuningFork;

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
    // Goggles 2 (Goggles Upgraded)
    containerTypeGoggles_2 = IForgeContainerType.create(ContainerMiningGoggles_2::createContainerClientSide);
    containerTypeGoggles_2.setRegistryName("container_goggles2");
    event.getRegistry().register(containerTypeGoggles_2);
    // Tuning Fork
    containerTuningFork = IForgeContainerType.create(ContainerTuningFork::createContainerClientSide);
    containerTuningFork.setRegistryName("container_tuningfork");
    event.getRegistry().register(containerTuningFork);
  }

}
