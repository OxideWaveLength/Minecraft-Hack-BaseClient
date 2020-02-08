package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

public class S1EPacketRemoveEntityEffect implements Packet<INetHandlerPlayClient> {
	private int entityId;
	private int effectId;

	public S1EPacketRemoveEntityEffect() {
	}

	public S1EPacketRemoveEntityEffect(int entityIdIn, PotionEffect effect) {
		this.entityId = entityIdIn;
		this.effectId = effect.getPotionID();
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.entityId = buf.readVarIntFromBuffer();
		this.effectId = buf.readUnsignedByte();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.entityId);
		buf.writeByte(this.effectId);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleRemoveEntityEffect(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public int getEffectId() {
		return this.effectId;
	}
}
