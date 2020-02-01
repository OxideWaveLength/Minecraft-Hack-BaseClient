package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S18PacketEntityTeleport implements Packet<INetHandlerPlayClient>
{
    private int entityId;
    private int posX;
    private int posY;
    private int posZ;
    private byte yaw;
    private byte pitch;
    private boolean onGround;

    public S18PacketEntityTeleport()
    {
    }

    public S18PacketEntityTeleport(Entity entityIn)
    {
        this.entityId = entityIn.getEntityId();
        this.posX = MathHelper.floor_double(entityIn.posX * 32.0D);
        this.posY = MathHelper.floor_double(entityIn.posY * 32.0D);
        this.posZ = MathHelper.floor_double(entityIn.posZ * 32.0D);
        this.yaw = (byte)((int)(entityIn.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte)((int)(entityIn.rotationPitch * 256.0F / 360.0F));
        this.onGround = entityIn.onGround;
    }

    public S18PacketEntityTeleport(int entityIdIn, int posXIn, int posYIn, int posZIn, byte yawIn, byte pitchIn, boolean onGroundIn)
    {
        this.entityId = entityIdIn;
        this.posX = posXIn;
        this.posY = posYIn;
        this.posZ = posZIn;
        this.yaw = yawIn;
        this.pitch = pitchIn;
        this.onGround = onGroundIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarIntFromBuffer();
        this.posX = buf.readInt();
        this.posY = buf.readInt();
        this.posZ = buf.readInt();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.onGround = buf.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityId);
        buf.writeInt(this.posX);
        buf.writeInt(this.posY);
        buf.writeInt(this.posZ);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeBoolean(this.onGround);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEntityTeleport(this);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public int getX()
    {
        return this.posX;
    }

    public int getY()
    {
        return this.posY;
    }

    public int getZ()
    {
        return this.posZ;
    }

    public byte getYaw()
    {
        return this.yaw;
    }

    public byte getPitch()
    {
        return this.pitch;
    }

    public boolean getOnGround()
    {
        return this.onGround;
    }
}
