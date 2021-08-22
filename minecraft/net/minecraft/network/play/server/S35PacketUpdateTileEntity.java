package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S35PacketUpdateTileEntity implements Packet<INetHandlerPlayClient> {
	private BlockPos blockPos;

	/** Used only for vanilla tile entities */
	private int metadata;
	private NBTTagCompound nbt;

	public S35PacketUpdateTileEntity() {
	}

	public S35PacketUpdateTileEntity(BlockPos blockPosIn, int metadataIn, NBTTagCompound nbtIn) {
		this.blockPos = blockPosIn;
		this.metadata = metadataIn;
		this.nbt = nbtIn;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.blockPos = buf.readBlockPos();
		this.metadata = buf.readUnsignedByte();
		this.nbt = buf.readNBTTagCompoundFromBuffer();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeBlockPos(this.blockPos);
		buf.writeByte((byte) this.metadata);
		buf.writeNBTTagCompoundToBuffer(this.nbt);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleUpdateTileEntity(this);
	}

	public BlockPos getPos() {
		return this.blockPos;
	}

	public int getTileEntityType() {
		return this.metadata;
	}

	public NBTTagCompound getNbtCompound() {
		return this.nbt;
	}
}
