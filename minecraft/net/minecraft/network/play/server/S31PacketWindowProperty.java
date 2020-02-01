package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S31PacketWindowProperty implements Packet<INetHandlerPlayClient>
{
    private int windowId;
    private int varIndex;
    private int varValue;

    public S31PacketWindowProperty()
    {
    }

    public S31PacketWindowProperty(int windowIdIn, int varIndexIn, int varValueIn)
    {
        this.windowId = windowIdIn;
        this.varIndex = varIndexIn;
        this.varValue = varValueIn;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleWindowProperty(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.windowId = buf.readUnsignedByte();
        this.varIndex = buf.readShort();
        this.varValue = buf.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.windowId);
        buf.writeShort(this.varIndex);
        buf.writeShort(this.varValue);
    }

    public int getWindowId()
    {
        return this.windowId;
    }

    public int getVarIndex()
    {
        return this.varIndex;
    }

    public int getVarValue()
    {
        return this.varValue;
    }
}
