package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

public class S26PacketMapChunkBulk implements Packet<INetHandlerPlayClient> {
	private int[] xPositions;
	private int[] zPositions;
	private S21PacketChunkData.Extracted[] chunksData;
	private boolean isOverworld;

	public S26PacketMapChunkBulk() {
	}

	public S26PacketMapChunkBulk(List<Chunk> chunks) {
		int i = chunks.size();
		this.xPositions = new int[i];
		this.zPositions = new int[i];
		this.chunksData = new S21PacketChunkData.Extracted[i];
		this.isOverworld = !((Chunk) chunks.get(0)).getWorld().provider.getHasNoSky();

		for (int j = 0; j < i; ++j) {
			Chunk chunk = (Chunk) chunks.get(j);
			S21PacketChunkData.Extracted s21packetchunkdata$extracted = S21PacketChunkData.func_179756_a(chunk, true, this.isOverworld, 65535);
			this.xPositions[j] = chunk.xPosition;
			this.zPositions[j] = chunk.zPosition;
			this.chunksData[j] = s21packetchunkdata$extracted;
		}
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.isOverworld = buf.readBoolean();
		int i = buf.readVarIntFromBuffer();
		this.xPositions = new int[i];
		this.zPositions = new int[i];
		this.chunksData = new S21PacketChunkData.Extracted[i];

		for (int j = 0; j < i; ++j) {
			this.xPositions[j] = buf.readInt();
			this.zPositions[j] = buf.readInt();
			this.chunksData[j] = new S21PacketChunkData.Extracted();
			this.chunksData[j].dataSize = buf.readShort() & 65535;
			this.chunksData[j].data = new byte[S21PacketChunkData.func_180737_a(Integer.bitCount(this.chunksData[j].dataSize), this.isOverworld, true)];
		}

		for (int k = 0; k < i; ++k) {
			buf.readBytes(this.chunksData[k].data);
		}
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeBoolean(this.isOverworld);
		buf.writeVarIntToBuffer(this.chunksData.length);

		for (int i = 0; i < this.xPositions.length; ++i) {
			buf.writeInt(this.xPositions[i]);
			buf.writeInt(this.zPositions[i]);
			buf.writeShort((short) (this.chunksData[i].dataSize & 65535));
		}

		for (int j = 0; j < this.xPositions.length; ++j) {
			buf.writeBytes(this.chunksData[j].data);
		}
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleMapChunkBulk(this);
	}

	public int getChunkX(int p_149255_1_) {
		return this.xPositions[p_149255_1_];
	}

	public int getChunkZ(int p_149253_1_) {
		return this.zPositions[p_149253_1_];
	}

	public int getChunkCount() {
		return this.xPositions.length;
	}

	public byte[] getChunkBytes(int p_149256_1_) {
		return this.chunksData[p_149256_1_].data;
	}

	public int getChunkSize(int p_179754_1_) {
		return this.chunksData[p_179754_1_].dataSize;
	}
}
