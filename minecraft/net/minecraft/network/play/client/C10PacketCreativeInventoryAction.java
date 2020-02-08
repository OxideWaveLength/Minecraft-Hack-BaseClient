package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C10PacketCreativeInventoryAction implements Packet<INetHandlerPlayServer> {
	private int slotId;
	private ItemStack stack;

	public C10PacketCreativeInventoryAction() {
	}

	public C10PacketCreativeInventoryAction(int slotIdIn, ItemStack stackIn) {
		this.slotId = slotIdIn;
		this.stack = stackIn != null ? stackIn.copy() : null;
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayServer handler) {
		handler.processCreativeInventoryAction(this);
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.slotId = buf.readShort();
		this.stack = buf.readItemStackFromBuffer();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeShort(this.slotId);
		buf.writeItemStackToBuffer(this.stack);
	}

	public int getSlotId() {
		return this.slotId;
	}

	public ItemStack getStack() {
		return this.stack;
	}
}
