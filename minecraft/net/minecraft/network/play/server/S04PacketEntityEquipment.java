package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S04PacketEntityEquipment implements Packet<INetHandlerPlayClient> {
	private int entityID;
	private int equipmentSlot;
	private ItemStack itemStack;

	public S04PacketEntityEquipment() {
	}

	public S04PacketEntityEquipment(int entityIDIn, int p_i45221_2_, ItemStack itemStackIn) {
		this.entityID = entityIDIn;
		this.equipmentSlot = p_i45221_2_;
		this.itemStack = itemStackIn == null ? null : itemStackIn.copy();
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.entityID = buf.readVarIntFromBuffer();
		this.equipmentSlot = buf.readShort();
		this.itemStack = buf.readItemStackFromBuffer();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.entityID);
		buf.writeShort(this.equipmentSlot);
		buf.writeItemStackToBuffer(this.itemStack);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleEntityEquipment(this);
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}

	public int getEntityID() {
		return this.entityID;
	}

	public int getEquipmentSlot() {
		return this.equipmentSlot;
	}
}
