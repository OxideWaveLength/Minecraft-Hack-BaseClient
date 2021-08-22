package net.minecraft.network.play.server;

import java.io.IOException;

import org.apache.commons.lang3.Validate;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S29PacketSoundEffect implements Packet<INetHandlerPlayClient> {
	private String soundName;
	private int posX;
	private int posY = Integer.MAX_VALUE;
	private int posZ;
	private float soundVolume;
	private int soundPitch;

	public S29PacketSoundEffect() {
	}

	public S29PacketSoundEffect(String soundNameIn, double soundX, double soundY, double soundZ, float volume, float pitch) {
		Validate.notNull(soundNameIn, "name", new Object[0]);
		this.soundName = soundNameIn;
		this.posX = (int) (soundX * 8.0D);
		this.posY = (int) (soundY * 8.0D);
		this.posZ = (int) (soundZ * 8.0D);
		this.soundVolume = volume;
		this.soundPitch = (int) (pitch * 63.0F);
		pitch = MathHelper.clamp_float(pitch, 0.0F, 255.0F);
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.soundName = buf.readStringFromBuffer(256);
		this.posX = buf.readInt();
		this.posY = buf.readInt();
		this.posZ = buf.readInt();
		this.soundVolume = buf.readFloat();
		this.soundPitch = buf.readUnsignedByte();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeString(this.soundName);
		buf.writeInt(this.posX);
		buf.writeInt(this.posY);
		buf.writeInt(this.posZ);
		buf.writeFloat(this.soundVolume);
		buf.writeByte(this.soundPitch);
	}

	public String getSoundName() {
		return this.soundName;
	}

	public double getX() {
		return (double) ((float) this.posX / 8.0F);
	}

	public double getY() {
		return (double) ((float) this.posY / 8.0F);
	}

	public double getZ() {
		return (double) ((float) this.posZ / 8.0F);
	}

	public float getVolume() {
		return this.soundVolume;
	}

	public float getPitch() {
		return (float) this.soundPitch / 63.0F;
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleSoundEffect(this);
	}
}
