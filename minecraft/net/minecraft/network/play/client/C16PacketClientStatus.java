package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C16PacketClientStatus implements Packet<INetHandlerPlayServer>
{
    private C16PacketClientStatus.EnumState status;

    public C16PacketClientStatus()
    {
    }

    public C16PacketClientStatus(C16PacketClientStatus.EnumState statusIn)
    {
        this.status = statusIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.status = (C16PacketClientStatus.EnumState)buf.readEnumValue(C16PacketClientStatus.EnumState.class);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.status);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processClientStatus(this);
    }

    public C16PacketClientStatus.EnumState getStatus()
    {
        return this.status;
    }

    public static enum EnumState
    {
        PERFORM_RESPAWN,
        REQUEST_STATS,
        OPEN_INVENTORY_ACHIEVEMENT;
    }
}
