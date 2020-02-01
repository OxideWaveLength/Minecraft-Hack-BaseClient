package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1BPacketEntityAttach implements Packet<INetHandlerPlayClient>
{
    private int leash;
    private int entityId;
    private int vehicleEntityId;

    public S1BPacketEntityAttach()
    {
    }

    public S1BPacketEntityAttach(int leashIn, Entity entityIn, Entity vehicle)
    {
        this.leash = leashIn;
        this.entityId = entityIn.getEntityId();
        this.vehicleEntityId = vehicle != null ? vehicle.getEntityId() : -1;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readInt();
        this.vehicleEntityId = buf.readInt();
        this.leash = buf.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeInt(this.entityId);
        buf.writeInt(this.vehicleEntityId);
        buf.writeByte(this.leash);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEntityAttach(this);
    }

    public int getLeash()
    {
        return this.leash;
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public int getVehicleEntityId()
    {
        return this.vehicleEntityId;
    }
}
