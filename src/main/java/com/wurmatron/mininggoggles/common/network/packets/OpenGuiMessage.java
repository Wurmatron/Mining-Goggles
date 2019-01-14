package com.wurmatron.mininggoggles.common.network.packets;

import com.wurmatron.mininggoggles.MiningGoggles;
import com.wurmatron.mininggoggles.common.network.utils.CustomMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class OpenGuiMessage extends CustomMessage.CustomtServerMessage<OpenGuiMessage> {

  private int id;
  private int[] loc;

  public OpenGuiMessage() {
  }

  public OpenGuiMessage(int id, int x, int y, int z) {
    this.id = id;
    loc = new int[]{x, y, z};
  }

  public OpenGuiMessage(int id, BlockPos loc) {
    this.id = id;
    this.loc = new int[]{loc.getX(), loc.getY(), loc.getZ()};
  }

  @Override
  protected void read(PacketBuffer buff) {
    id = buff.readInt();
    loc = buff.readVarIntArray();
  }

  @Override
  protected void write(PacketBuffer buff) {
    buff.writeInt(id);
    buff.writeVarIntArray(loc);
  }

  @Override
  public void process(EntityPlayer player, Side side) {
    player.openGui(MiningGoggles.instance, id, player.world, loc[0], loc[1], loc[2]);
  }
}
