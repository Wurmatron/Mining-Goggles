package com.wurmatron.mininggoggles.common.network.packets;

import com.wurmatron.mininggoggles.common.items.ItemGogglesMining;
import com.wurmatron.mininggoggles.common.network.utils.CustomMessage;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class UpdateHelmetConfig extends CustomMessage.CustomtServerMessage<UpdateHelmetConfig> {

  private NBTTagCompound nbt;

  public UpdateHelmetConfig() {
  }

  public UpdateHelmetConfig(NBTTagCompound nbt) {
    this.nbt = nbt;
  }

  @Override
  protected void read(PacketBuffer buff) throws IOException {
    nbt = buff.readCompoundTag();
  }

  @Override
  protected void write(PacketBuffer buff) throws IOException {
    buff.writeCompoundTag(nbt);
  }

  @Override
  public void process(EntityPlayer player, Side side) {
    ItemStack stack = player.getHeldItemMainhand();
    if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemGogglesMining) {
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("range")) {
        nbt.setInteger("range", stack.getTagCompound().getInteger("range"));
      }
      stack.setTagCompound(nbt);
    }
  }
}
