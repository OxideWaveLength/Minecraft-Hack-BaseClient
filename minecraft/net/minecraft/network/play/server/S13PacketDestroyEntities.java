package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S13PacketDestroyEntities implements Packet<INetHandlerPlayClient>
{
    private int[] entityIDs;

    public S13PacketDestroyEntities()
    {
    }

    public S13PacketDestroyEntities(int... entityIDsIn)
    {
        this.entityIDs = entityIDsIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityIDs = new int[buf.readVarIntFromBuffer()];

        for (int i = 0; i < this.entityIDs.length; ++i)
        {
            this.entityIDs[i] = buf.readVarIntFromBuffer();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityIDs.length);

        for (int i = 0; i < this.entityIDs.length; ++i)
        {
            buf.writeVarIntToBuffer(this.entityIDs[i]);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleDestroyEntities(this);
    }

    public int[] getEntityIDs()
    {
        return this.entityIDs;
    }
}
