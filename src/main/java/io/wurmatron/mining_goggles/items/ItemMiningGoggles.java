package io.wurmatron.mining_goggles.items;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;

public class ItemMiningGoggles extends ArmorItem {
    public ItemMiningGoggles(IArmorMaterial material, EquipmentSlotType slot, Properties properties) {
        super(material, slot, properties);
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot,
        String type) {
        return "mininggoggles:textures/models/goggles_t1.png"; // TODO Dynamic based on lens / modules
    }
}
