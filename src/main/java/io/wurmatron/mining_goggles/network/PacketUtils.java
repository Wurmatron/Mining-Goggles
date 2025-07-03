package io.wurmatron.mining_goggles.network;

import io.wurmatron.mining_goggles.MiningGoggles;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
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

        // TODO Update Itemstack server-side
        public static void handle(UpdateHelmet update, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                System.out.println("Update: " + update);
            });
        }
    }
}
