package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase {
	/** The string value for the tag (cannot be empty). */
	private String data;

	public NBTTagString() {
		this.data = "";
	}

	public NBTTagString(String data) {
		this.data = data;

		if (data == null) {
			throw new IllegalArgumentException("Empty string not allowed");
		}
	}

	/**
	 * Write the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	void write(DataOutput output) throws IOException {
		output.writeUTF(this.data);
	}

	void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
		sizeTracker.read(288L);
		this.data = input.readUTF();
		sizeTracker.read((long) (16 * this.data.length()));
	}

	/**
	 * Gets the type byte for the tag.
	 */
	public byte getId() {
		return (byte) 8;
	}

	public String toString() {
		return "\"" + this.data.replace("\"", "\\\"") + "\"";
	}

	/**
	 * Creates a clone of the tag.
	 */
	public NBTBase copy() {
		return new NBTTagString(this.data);
	}

	/**
	 * Return whether this compound has no tags.
	 */
	public boolean hasNoTags() {
		return this.data.isEmpty();
	}

	public boolean equals(Object p_equals_1_) {
		if (!super.equals(p_equals_1_)) {
			return false;
		} else {
			NBTTagString nbttagstring = (NBTTagString) p_equals_1_;
			return this.data == null && nbttagstring.data == null || this.data != null && this.data.equals(nbttagstring.data);
		}
	}

	public int hashCode() {
		return super.hashCode() ^ this.data.hashCode();
	}

	public String getString() {
		return this.data;
	}
}
