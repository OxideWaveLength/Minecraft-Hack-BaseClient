package net.minecraft.network.play.client;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload implements Packet<INetHandlerPlayServer> {
	private String channel;
	private PacketBuffer data;

	public C17PacketCustomPayload() {
	}

	public C17PacketCustomPayload(String channelIn, PacketBuffer dataIn) {
		this.channel = channelIn;
		this.data = dataIn;

		if (dataIn.writerIndex() > 32767) {
			throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
		}
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.channel = buf.readStringFromBuffer(20);
		int i = buf.readableBytes();

		if (i >= 0 && i <= 32767) {
			this.data = new PacketBuffer(buf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 32767 bytes");
		}
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeString(this.channel);
		buf.writeBytes((ByteBuf) this.data);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayServer handler) {
		handler.processVanilla250Packet(this);
	}

	public String getChannelName() {
		return this.channel;
	}

	public PacketBuffer getBufferData() {
		return this.data;
	}
}
