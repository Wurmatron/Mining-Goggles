package io.wurmatron.mining_goggles.network;

import io.wurmatron.mining_goggles.MiningGoggles;
import io.wurmatron.mining_goggles.items.MiningItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUtils {

    private static int id = 0;

    public static void register() {
        MiningGoggles.NETWORK.registerMessage(id++, UpdateHelmet.class, UpdateHelmet::encode, UpdateHelmet::decode, UpdateHelmet::InteractionHandle);
    }

    public static class UpdateHelmet {

        public ItemStack helmet;

        public UpdateHelmet(ItemStack helmet) {
            this.helmet = helmet;
        }

        public static void encode(UpdateHelmet packet, FriendlyByteBuf buf) {
            buf.writeItemStack(packet.helmet, true);
        }

        public static UpdateHelmet decode(FriendlyByteBuf buf) {
            return new UpdateHelmet(buf.readItem());
        }

        public static void InteractionHandle(UpdateHelmet update, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player.getInventory().getItem(player.getInventory().selected).getItem().equals(MiningItems.gogglesDigital)) {
                    player.getInventory().setItem(player.getInventory().selected, update.helmet);
                } else {
                    MiningGoggles.LOGGER.error("Failed to find helmet, to update!");
                }
            });
        }
    }
}
