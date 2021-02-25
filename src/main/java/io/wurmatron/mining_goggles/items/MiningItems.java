package io.wurmatron.mining_goggles.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class MiningItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "mininggoggles");

    public static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> sup) {
        return ITEMS.register(name, sup);
    }
}
