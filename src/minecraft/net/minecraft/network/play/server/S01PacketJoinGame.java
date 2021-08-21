package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class S01PacketJoinGame implements Packet<INetHandlerPlayClient> {
	private int entityId;
	private boolean hardcoreMode;
	private WorldSettings.GameType gameType;
	private int dimension;
	private EnumDifficulty difficulty;
	private int maxPlayers;
	private WorldType worldType;
	private boolean reducedDebugInfo;

	public S01PacketJoinGame() {
	}

	public S01PacketJoinGame(int entityIdIn, WorldSettings.GameType gameTypeIn, boolean hardcoreModeIn, int dimensionIn, EnumDifficulty difficultyIn, int maxPlayersIn, WorldType worldTypeIn, boolean reducedDebugInfoIn) {
		this.entityId = entityIdIn;
		this.dimension = dimensionIn;
		this.difficulty = difficultyIn;
		this.gameType = gameTypeIn;
		this.maxPlayers = maxPlayersIn;
		this.hardcoreMode = hardcoreModeIn;
		this.worldType = worldTypeIn;
		this.reducedDebugInfo = reducedDebugInfoIn;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.entityId = buf.readInt();
		int i = buf.readUnsignedByte();
		this.hardcoreMode = (i & 8) == 8;
		i = i & -9;
		this.gameType = WorldSettings.GameType.getByID(i);
		this.dimension = buf.readByte();
		this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
		this.maxPlayers = buf.readUnsignedByte();
		this.worldType = WorldType.parseWorldType(buf.readStringFromBuffer(16));

		if (this.worldType == null) {
			this.worldType = WorldType.DEFAULT;
		}

		this.reducedDebugInfo = buf.readBoolean();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeInt(this.entityId);
		int i = this.gameType.getID();

		if (this.hardcoreMode) {
			i |= 8;
		}

		buf.writeByte(i);
		buf.writeByte(this.dimension);
		buf.writeByte(this.difficulty.getDifficultyId());
		buf.writeByte(this.maxPlayers);
		buf.writeString(this.worldType.getWorldTypeName());
		buf.writeBoolean(this.reducedDebugInfo);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleJoinGame(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public boolean isHardcoreMode() {
		return this.hardcoreMode;
	}

	public WorldSettings.GameType getGameType() {
		return this.gameType;
	}

	public int getDimension() {
		return this.dimension;
	}

	public EnumDifficulty getDifficulty() {
		return this.difficulty;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public WorldType getWorldType() {
		return this.worldType;
	}

	public boolean isReducedDebugInfo() {
		return this.reducedDebugInfo;
	}
}
