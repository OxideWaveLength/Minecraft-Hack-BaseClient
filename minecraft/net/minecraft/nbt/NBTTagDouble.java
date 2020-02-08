package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.util.MathHelper;

public class NBTTagDouble extends NBTBase.NBTPrimitive {
	/** The double value for the tag. */
	private double data;

	NBTTagDouble() {
	}

	public NBTTagDouble(double data) {
		this.data = data;
	}

	/**
	 * Write the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	void write(DataOutput output) throws IOException {
		output.writeDouble(this.data);
	}

	void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
		sizeTracker.read(128L);
		this.data = input.readDouble();
	}

	/**
	 * Gets the type byte for the tag.
	 */
	public byte getId() {
		return (byte) 6;
	}

	public String toString() {
		return "" + this.data + "d";
	}

	/**
	 * Creates a clone of the tag.
	 */
	public NBTBase copy() {
		return new NBTTagDouble(this.data);
	}

	public boolean equals(Object p_equals_1_) {
		if (super.equals(p_equals_1_)) {
			NBTTagDouble nbttagdouble = (NBTTagDouble) p_equals_1_;
			return this.data == nbttagdouble.data;
		} else {
			return false;
		}
	}

	public int hashCode() {
		long i = Double.doubleToLongBits(this.data);
		return super.hashCode() ^ (int) (i ^ i >>> 32);
	}

	public long getLong() {
		return (long) Math.floor(this.data);
	}

	public int getInt() {
		return MathHelper.floor_double(this.data);
	}

	public short getShort() {
		return (short) (MathHelper.floor_double(this.data) & 65535);
	}

	public byte getByte() {
		return (byte) (MathHelper.floor_double(this.data) & 255);
	}

	public double getDouble() {
		return this.data;
	}

	public float getFloat() {
		return (float) this.data;
	}
}
