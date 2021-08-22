package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0EPacketSpawnObject implements Packet<INetHandlerPlayClient> {
	private int entityId;
	private int x;
	private int y;
	private int z;
	private int speedX;
	private int speedY;
	private int speedZ;
	private int pitch;
	private int yaw;
	private int type;
	private int field_149020_k;

	public S0EPacketSpawnObject() {
	}

	public S0EPacketSpawnObject(Entity entityIn, int typeIn) {
		this(entityIn, typeIn, 0);
	}

	public S0EPacketSpawnObject(Entity entityIn, int typeIn, int p_i45166_3_) {
		this.entityId = entityIn.getEntityId();
		this.x = MathHelper.floor_double(entityIn.posX * 32.0D);
		this.y = MathHelper.floor_double(entityIn.posY * 32.0D);
		this.z = MathHelper.floor_double(entityIn.posZ * 32.0D);
		this.pitch = MathHelper.floor_float(entityIn.rotationPitch * 256.0F / 360.0F);
		this.yaw = MathHelper.floor_float(entityIn.rotationYaw * 256.0F / 360.0F);
		this.type = typeIn;
		this.field_149020_k = p_i45166_3_;

		if (p_i45166_3_ > 0) {
			double d0 = entityIn.motionX;
			double d1 = entityIn.motionY;
			double d2 = entityIn.motionZ;
			double d3 = 3.9D;

			if (d0 < -d3) {
				d0 = -d3;
			}

			if (d1 < -d3) {
				d1 = -d3;
			}

			if (d2 < -d3) {
				d2 = -d3;
			}

			if (d0 > d3) {
				d0 = d3;
			}

			if (d1 > d3) {
				d1 = d3;
			}

			if (d2 > d3) {
				d2 = d3;
			}

			this.speedX = (int) (d0 * 8000.0D);
			this.speedY = (int) (d1 * 8000.0D);
			this.speedZ = (int) (d2 * 8000.0D);
		}
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.entityId = buf.readVarIntFromBuffer();
		this.type = buf.readByte();
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.pitch = buf.readByte();
		this.yaw = buf.readByte();
		this.field_149020_k = buf.readInt();

		if (this.field_149020_k > 0) {
			this.speedX = buf.readShort();
			this.speedY = buf.readShort();
			this.speedZ = buf.readShort();
		}
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.entityId);
		buf.writeByte(this.type);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeByte(this.pitch);
		buf.writeByte(this.yaw);
		buf.writeInt(this.field_149020_k);

		if (this.field_149020_k > 0) {
			buf.writeShort(this.speedX);
			buf.writeShort(this.speedY);
			buf.writeShort(this.speedZ);
		}
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleSpawnObject(this);
	}

	public int getEntityID() {
		return this.entityId;
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

	public int getSpeedX() {
		return this.speedX;
	}

	public int getSpeedY() {
		return this.speedY;
	}

	public int getSpeedZ() {
		return this.speedZ;
	}

	public int getPitch() {
		return this.pitch;
	}

	public int getYaw() {
		return this.yaw;
	}

	public int getType() {
		return this.type;
	}

	public int func_149009_m() {
		return this.field_149020_k;
	}

	public void setX(int newX) {
		this.x = newX;
	}

	public void setY(int newY) {
		this.y = newY;
	}

	public void setZ(int newZ) {
		this.z = newZ;
	}

	public void setSpeedX(int newSpeedX) {
		this.speedX = newSpeedX;
	}

	public void setSpeedY(int newSpeedY) {
		this.speedY = newSpeedY;
	}

	public void setSpeedZ(int newSpeedZ) {
		this.speedZ = newSpeedZ;
	}

	public void func_149002_g(int p_149002_1_) {
		this.field_149020_k = p_149002_1_;
	}
}
