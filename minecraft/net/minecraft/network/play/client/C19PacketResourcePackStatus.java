package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C19PacketResourcePackStatus implements Packet<INetHandlerPlayServer>
{
    private String hash;
    private C19PacketResourcePackStatus.Action status;

    public C19PacketResourcePackStatus()
    {
    }

    public C19PacketResourcePackStatus(String hashIn, C19PacketResourcePackStatus.Action statusIn)
    {
        if (hashIn.length() > 40)
        {
            hashIn = hashIn.substring(0, 40);
        }

        this.hash = hashIn;
        this.status = statusIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.hash = buf.readStringFromBuffer(40);
        this.status = (C19PacketResourcePackStatus.Action)buf.readEnumValue(C19PacketResourcePackStatus.Action.class);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.hash);
        buf.writeEnumValue(this.status);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.handleResourcePackStatus(this);
    }

    public static enum Action
    {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED;
    }
}
