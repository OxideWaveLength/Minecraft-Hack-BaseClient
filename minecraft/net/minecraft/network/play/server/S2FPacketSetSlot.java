package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2FPacketSetSlot implements Packet<INetHandlerPlayClient>
{
    private int windowId;
    private int slot;
    private ItemStack item;

    public S2FPacketSetSlot()
    {
    }

    public S2FPacketSetSlot(int windowIdIn, int slotIn, ItemStack itemIn)
    {
        this.windowId = windowIdIn;
        this.slot = slotIn;
        this.item = itemIn == null ? null : itemIn.copy();
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleSetSlot(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.windowId = buf.readByte();
        this.slot = buf.readShort();
        this.item = buf.readItemStackFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slot);
        buf.writeItemStackToBuffer(this.item);
    }

    public int func_149175_c()
    {
        return this.windowId;
    }

    public int func_149173_d()
    {
        return this.slot;
    }

    public ItemStack func_149174_e()
    {
        return this.item;
    }
}
