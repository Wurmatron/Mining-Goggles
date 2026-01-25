package io.wurmatron.mining_goggles.items;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.api.MiningGogglesApi;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.property.Properties;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAttunmentCrystal extends ItemCrystal {

    public ItemAttunmentCrystal(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult<ItemStack> use(Level Level, Player player, InteractionHand InteractionHand) {
        return InteractionResult.pass(player.getItemInInteractionHand(InteractionHand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level Level,
                                List<TextComponent> text, TooltipFlag flag) {
        if (stack.getTag() != null && !stack.getTag().isEmpty()) {
            if (stack.getTag().getString("type").isEmpty()) {
                text.add(new TextComponent("Unattuned"));
            } else {
                text.add(new TextComponent(
                        ChatFormatting.GRAY + (stack.getTag().getInt("completed") == 1 ? "" : computeProgress(stack) + " % ") + "Attuned to '" + ChatFormatting.LIGHT_PURPLE
                                + stack.getTag().getString("type") + ChatFormatting.GRAY + "'"));
            }
        } else {
            super.appendHoverText(stack, Level, text, flag);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().getInt("completed") == 1) {
            return true;
        }
        return super.isFoil(stack);
    }

    public static String computeProgress(ItemStack stack) {
        try {
            return String.format("%.1f", (((double) stack.getTag().getInt("count")) / MiningGogglesApi.oreTuning.get(
                    stack.getTag().getString("type"))) * 100);
        } catch (Exception e) {
            return "0.0";
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab group,
                                 NonNullList<ItemStack> items) {
        if (group == MiningGoggles.TAB_GOGGLES) {
            items.add(new ItemStack(MiningItems.attunmentCrystal));
            for (String ore : MiningGogglesApi.oreTuning.keySet()) {
                items.add(create(ore));
            }
        }
    }

    public static ItemStack create(String ore) {
        ItemStack stack = new ItemStack(MiningItems.attunmentCrystal);
        stack.setTag(new CompoundTag());
        stack.getTag().putInt("completed", 1);
        stack.getTag().putInt("count", MiningGogglesApi.oreTuning.get(ore));
        stack.getTag().putString("type", ore);
        stack.getTag().putInt("min-wavelength", MiningGogglesApi.oreWavelengths.get(ore) - 5);
        stack.getTag().putInt("max-wavelength", MiningGogglesApi.oreWavelengths.get(ore) + 5);
        return stack;
    }
}
