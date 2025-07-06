package io.wurmatron.mining_goggles.network;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.items.MiningItems;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUtils {

    private static int id = 0;

    public static void register() {
        MiningGoggles.NETWORK.registerMessage(id++, UpdateHelmet.class, UpdateHelmet::encode, UpdateHelmet::decode, UpdateHelmet::handle);
    }

    public static class UpdateHelmet {

        public ItemStack helmet;

        public UpdateHelmet(ItemStack helmet) {
            this.helmet = helmet;
        }

        public static void encode(UpdateHelmet packet, PacketBuffer buf) {
            buf.writeItemStack(packet.helmet, true);
        }

        public static UpdateHelmet decode(PacketBuffer buf) {
            return new UpdateHelmet(buf.readItem());
        }

        public static void handle(UpdateHelmet update, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player.inventory.getItem(player.inventory.selected).getItem().equals(MiningItems.gogglesDigital)) {
                    player.inventory.setItem(player.inventory.selected, update.helmet);
                } else {
                    MiningGoggles.LOGGER.error("Failed to find helmet, to update!");
                }
            });
        }
    }
}
