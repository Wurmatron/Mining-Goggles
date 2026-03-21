package io.wurmatron.mining_goggles.registry;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.inventory.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraftforge.network.NetworkEvent.RegistrationChangeType.REGISTER;

public class ContainerRegistry {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MiningGoggles.MODID);

    //Containers
    public static RegistryObject<MenuType<ContainerCrystalBag>> containerTypeCrystalBag = CONTAINERS.register("container_crystalbag", ()-> new MenuType<>(ContainerCrystalBag::new));
    public static RegistryObject<MenuType<ContainerMiningGoggles_1>> containerTypeGoggles_1;
    public static RegistryObject<MenuType<ContainerMiningGoggles_2>> containerTypeGoggles_2;
    public static RegistryObject<MenuType<ContainerTuningFork>> containerTuningFork;
    public static RegistryObject<MenuType<ContainerFilter>> containerTypeGoggles_Digital;

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().register(containerTypeCrystalBag.get());
        // Goggles 1
        containerTypeGoggles_1 = MenuType.create(ContainerMiningGoggles_1::createContainerClientSide);
        containerTypeGoggles_1.setRegistryName("container_goggles1");
        event.getRegistry().register(containerTypeGoggles_1);
        // Goggles 2 (Goggles Upgraded)
        containerTypeGoggles_2 = MenuType.create(ContainerMiningGoggles_2::createContainerClientSide);
        containerTypeGoggles_2.setRegistryName("container_goggles2");
        event.getRegistry().register(containerTypeGoggles_2);
        // Tuning Fork
        containerTuningFork = MenuType.create(ContainerTuningFork::createContainerClientSide);
        containerTuningFork.setRegistryName("container_tuningfork");
        event.getRegistry().register(containerTuningFork);
        // Goggles (Digital)
        containerTypeGoggles_Digital = MenuType.create(ContainerFilter::createContainerClientSide);
        containerTypeGoggles_Digital.setRegistryName("container_filterdigital");
        event.getRegistry().register(containerTypeGoggles_Digital);
    }

}
