package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S11PacketSpawnExperienceOrb implements Packet<INetHandlerPlayClient> {
	private int entityID;
	private int posX;
	private int posY;
	private int posZ;
	private int xpValue;

	public S11PacketSpawnExperienceOrb() {
	}

	public S11PacketSpawnExperienceOrb(EntityXPOrb xpOrb) {
		this.entityID = xpOrb.getEntityId();
		this.posX = MathHelper.floor_double(xpOrb.posX * 32.0D);
		this.posY = MathHelper.floor_double(xpOrb.posY * 32.0D);
		this.posZ = MathHelper.floor_double(xpOrb.posZ * 32.0D);
		this.xpValue = xpOrb.getXpValue();
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.entityID = buf.readVarIntFromBuffer();
		this.posX = buf.readInt();
		this.posY = buf.readInt();
		this.posZ = buf.readInt();
		this.xpValue = buf.readShort();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.entityID);
		buf.writeInt(this.posX);
		buf.writeInt(this.posY);
		buf.writeInt(this.posZ);
		buf.writeShort(this.xpValue);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleSpawnExperienceOrb(this);
	}

	public int getEntityID() {
		return this.entityID;
	}

	public int getX() {
		return this.posX;
	}

	public int getY() {
		return this.posY;
	}

	public int getZ() {
		return this.posZ;
	}

	public int getXPValue() {
		return this.xpValue;
	}
}
