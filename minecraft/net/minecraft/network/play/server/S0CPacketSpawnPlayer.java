package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0CPacketSpawnPlayer implements Packet<INetHandlerPlayClient> {
	private int entityId;
	private UUID playerId;
	private int x;
	private int y;
	private int z;
	private byte yaw;
	private byte pitch;
	private int currentItem;
	private DataWatcher watcher;
	private List<DataWatcher.WatchableObject> field_148958_j;

	public S0CPacketSpawnPlayer() {
	}

	public S0CPacketSpawnPlayer(EntityPlayer player) {
		this.entityId = player.getEntityId();
		this.playerId = player.getGameProfile().getId();
		this.x = MathHelper.floor_double(player.posX * 32.0D);
		this.y = MathHelper.floor_double(player.posY * 32.0D);
		this.z = MathHelper.floor_double(player.posZ * 32.0D);
		this.yaw = (byte) ((int) (player.rotationYaw * 256.0F / 360.0F));
		this.pitch = (byte) ((int) (player.rotationPitch * 256.0F / 360.0F));
		ItemStack itemstack = player.inventory.getCurrentItem();
		this.currentItem = itemstack == null ? 0 : Item.getIdFromItem(itemstack.getItem());
		this.watcher = player.getDataWatcher();
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.entityId = buf.readVarIntFromBuffer();
		this.playerId = buf.readUuid();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.yaw = buf.readByte();
		this.pitch = buf.readByte();
		this.currentItem = buf.readShort();
		this.field_148958_j = DataWatcher.readWatchedListFromPacketBuffer(buf);
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.entityId);
		buf.writeUuid(this.playerId);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeByte(this.yaw);
		buf.writeByte(this.pitch);
		buf.writeShort(this.currentItem);
		this.watcher.writeTo(buf);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleSpawnPlayer(this);
	}

	public List<DataWatcher.WatchableObject> func_148944_c() {
		if (this.field_148958_j == null) {
			this.field_148958_j = this.watcher.getAllWatched();
		}

		return this.field_148958_j;
	}

	public int getEntityID() {
		return this.entityId;
	}

	public UUID getPlayer() {
		return this.playerId;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public byte getYaw() {
		return this.yaw;
	}

	public byte getPitch() {
		return this.pitch;
	}

	public int getCurrentItemID() {
		return this.currentItem;
	}
}
