package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C03PacketPlayer implements Packet<INetHandlerPlayServer> {
	protected double x;
	protected double y;
	protected double z;
	protected float yaw;
	protected float pitch;
	protected boolean onGround;
	protected boolean moving;
	protected boolean rotating;

	protected boolean fromMinecraft;

	public C03PacketPlayer() {
	}

	public C03PacketPlayer(boolean isOnGround) {
		this.onGround = isOnGround;

		this.fromMinecraft = true;
	}

	public C03PacketPlayer(boolean isOnGround, boolean fromMinecraft) {
		this.onGround = isOnGround;

		this.fromMinecraft = fromMinecraft;
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayServer handler) {
		handler.processPlayer(this);
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.onGround = buf.readUnsignedByte() != 0;
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeByte(this.onGround ? 1 : 0);
	}

	public double getPositionX() {
		return this.x;
	}

	public void setPositionX(double x) {
		this.x = x;
	}

	public double getPositionY() {
		return this.y;
	}

	public void setPositionY(double y) {
		this.y = y;
	}

	public double getPositionZ() {
		return this.z;
	}

	public void setPositionZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return this.yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public boolean isOnGround() {
		return this.onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public boolean isMoving() {
		return this.moving;
	}

	public boolean getRotating() {
		return this.rotating;
	}

	public void setRotating(boolean rotating) {
		this.rotating = rotating;
	}

	public void setMoving(boolean isMoving) {
		this.moving = isMoving;
	}

	public boolean isFromMinecraft() {
		return fromMinecraft;
	}

	public static class C04PacketPlayerPosition extends C03PacketPlayer {
		public C04PacketPlayerPosition() {
			this.moving = true;

			this.fromMinecraft = true;
		}

		public C04PacketPlayerPosition(boolean fromMinecraft) {
			this.moving = true;

			this.fromMinecraft = fromMinecraft;
		}

		public C04PacketPlayerPosition(double posX, double posY, double posZ, boolean isOnGround) {
			this.x = posX;
			this.y = posY;
			this.z = posZ;
			this.onGround = isOnGround;
			this.moving = true;

			this.fromMinecraft = true;
		}

		public C04PacketPlayerPosition(double posX, double posY, double posZ, boolean isOnGround, boolean fromMinecraft) {
			this.x = posX;
			this.y = posY;
			this.z = posZ;
			this.onGround = isOnGround;
			this.moving = true;

			this.fromMinecraft = fromMinecraft;
		}

		public void readPacketData(PacketBuffer buf) throws IOException {
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			super.readPacketData(buf);
		}

		public void writePacketData(PacketBuffer buf) throws IOException {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			super.writePacketData(buf);
		}
	}

	public static class C05PacketPlayerLook extends C03PacketPlayer {
		public C05PacketPlayerLook() {
			this.rotating = true;

			this.fromMinecraft = true;
		}

		public C05PacketPlayerLook(boolean fromMinecraft) {
			this.rotating = true;

			this.fromMinecraft = fromMinecraft;
		}

		public C05PacketPlayerLook(float playerYaw, float playerPitch, boolean isOnGround) {
			this.yaw = playerYaw;
			this.pitch = playerPitch;
			this.onGround = isOnGround;
			this.rotating = true;

			this.fromMinecraft = true;
		}

		public C05PacketPlayerLook(float playerYaw, float playerPitch, boolean isOnGround, boolean fromMinecraft) {
			this.yaw = playerYaw;
			this.pitch = playerPitch;
			this.onGround = isOnGround;
			this.rotating = true;

			this.fromMinecraft = fromMinecraft;
		}

		public void readPacketData(PacketBuffer buf) throws IOException {
			this.yaw = buf.readFloat();
			this.pitch = buf.readFloat();
			super.readPacketData(buf);
		}

		public void writePacketData(PacketBuffer buf) throws IOException {
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
			super.writePacketData(buf);
		}
	}

	public static class C06PacketPlayerPosLook extends C03PacketPlayer {
		public C06PacketPlayerPosLook() {
			this.moving = true;
			this.rotating = true;

			this.fromMinecraft = true;
		}

		public C06PacketPlayerPosLook(boolean fromMinecraft) {
			this.moving = true;
			this.rotating = true;

			this.fromMinecraft = fromMinecraft;
		}

		public C06PacketPlayerPosLook(double playerX, double playerY, double playerZ, float playerYaw, float playerPitch, boolean playerIsOnGround) {
			this.x = playerX;
			this.y = playerY;
			this.z = playerZ;
			this.yaw = playerYaw;
			this.pitch = playerPitch;
			this.onGround = playerIsOnGround;
			this.rotating = true;
			this.moving = true;

			this.fromMinecraft = true;
		}

		public C06PacketPlayerPosLook(double playerX, double playerY, double playerZ, float playerYaw, float playerPitch, boolean playerIsOnGround, boolean fromMinecraft) {
			this.x = playerX;
			this.y = playerY;
			this.z = playerZ;
			this.yaw = playerYaw;
			this.pitch = playerPitch;
			this.onGround = playerIsOnGround;
			this.rotating = true;
			this.moving = true;

			this.fromMinecraft = fromMinecraft;
		}

		public void readPacketData(PacketBuffer buf) throws IOException {
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			this.yaw = buf.readFloat();
			this.pitch = buf.readFloat();
			super.readPacketData(buf);
		}

		public void writePacketData(PacketBuffer buf) throws IOException {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			buf.writeFloat(this.yaw);
			buf.writeFloat(this.pitch);
			super.writePacketData(buf);
		}
	}
}
