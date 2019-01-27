package com.wurmatron.mininggoggles.common.network.packets;

import com.wurmatron.mininggoggles.common.items.ItemGogglesMining;
import com.wurmatron.mininggoggles.common.network.GuiHandler;
import com.wurmatron.mininggoggles.common.network.utils.CustomMessage;
import com.wurmatron.mininggoggles.common.reference.Global;
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
    ItemStack stack = GuiHandler.getActiveGoggles(player);
    if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemGogglesMining) {
      NBTTagCompound currentNBT = stack.getTagCompound();
      // Fix Invalid Stack
      if (!stack.hasTagCompound()) {
        currentNBT = new NBTTagCompound();
        currentNBT.setInteger(Global.NBT_RANGE, 4);
        currentNBT.setTag(Global.NBT_MODULES, new NBTTagCompound());
      }
      currentNBT.setTag(Global.NBT_FILTERS, nbt);
      stack.setTagCompound(currentNBT);
    }
  }
}
