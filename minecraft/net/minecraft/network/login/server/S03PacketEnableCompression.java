package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S03PacketEnableCompression implements Packet<INetHandlerLoginClient>
{
    private int compressionTreshold;

    public S03PacketEnableCompression()
    {
    }

    public S03PacketEnableCompression(int compressionTresholdIn)
    {
        this.compressionTreshold = compressionTresholdIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.compressionTreshold = buf.readVarIntFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.compressionTreshold);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerLoginClient handler)
    {
        handler.handleEnableCompression(this);
    }

    public int getCompressionTreshold()
    {
        return this.compressionTreshold;
    }
}
