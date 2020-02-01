package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class S0APacketUseBed implements Packet<INetHandlerPlayClient>
{
    private int playerID;

    /** Block location of the head part of the bed */
    private BlockPos bedPos;

    public S0APacketUseBed()
    {
    }

    public S0APacketUseBed(EntityPlayer player, BlockPos bedPosIn)
    {
        this.playerID = player.getEntityId();
        this.bedPos = bedPosIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.playerID = buf.readVarIntFromBuffer();
        this.bedPos = buf.readBlockPos();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.playerID);
        buf.writeBlockPos(this.bedPos);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleUseBed(this);
    }

    public EntityPlayer getPlayer(World worldIn)
    {
        return (EntityPlayer)worldIn.getEntityByID(this.playerID);
    }

    public BlockPos getBedPosition()
    {
        return this.bedPos;
    }
}
