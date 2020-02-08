package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S49PacketUpdateEntityNBT implements Packet<INetHandlerPlayClient> {
	private int entityId;
	private NBTTagCompound tagCompound;

	public S49PacketUpdateEntityNBT() {
	}

	public S49PacketUpdateEntityNBT(int entityIdIn, NBTTagCompound tagCompoundIn) {
		this.entityId = entityIdIn;
		this.tagCompound = tagCompoundIn;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.entityId = buf.readVarIntFromBuffer();
		this.tagCompound = buf.readNBTTagCompoundFromBuffer();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.entityId);
		buf.writeNBTTagCompoundToBuffer(this.tagCompound);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleEntityNBT(this);
	}

	public NBTTagCompound getTagCompound() {
		return this.tagCompound;
	}

	public Entity getEntity(World worldIn) {
		return worldIn.getEntityByID(this.entityId);
	}
}
