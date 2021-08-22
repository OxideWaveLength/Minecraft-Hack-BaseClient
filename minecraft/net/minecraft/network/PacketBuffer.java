package net.minecraft.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.UUID;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufProcessor;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

public class PacketBuffer extends ByteBuf {
	private final ByteBuf buf;

	public PacketBuffer(ByteBuf wrapped) {
		this.buf = wrapped;
	}

	/**
	 * Calculates the number of bytes required to fit the supplied int (0-5) if it
	 * were to be read/written using readVarIntFromBuffer or writeVarIntToBuffer
	 */
	public static int getVarIntSize(int input) {
		for (int i = 1; i < 5; ++i) {
			if ((input & -1 << i * 7) == 0) {
				return i;
			}
		}

		return 5;
	}

	public void writeByteArray(byte[] array) {
		this.writeVarIntToBuffer(array.length);
		this.writeBytes(array);
	}

	public byte[] readByteArray() {
		byte[] abyte = new byte[this.readVarIntFromBuffer()];
		this.readBytes(abyte);
		return abyte;
	}

	public BlockPos readBlockPos() {
		return BlockPos.fromLong(this.readLong());
	}

	public void writeBlockPos(BlockPos pos) {
		this.writeLong(pos.toLong());
	}

	public IChatComponent readChatComponent() throws IOException {
		return IChatComponent.Serializer.jsonToComponent(this.readStringFromBuffer(32767));
	}

	public void writeChatComponent(IChatComponent component) throws IOException {
		this.writeString(IChatComponent.Serializer.componentToJson(component));
	}

	public <T extends Enum<T>> T readEnumValue(Class<T> enumClass) {
		return (T) ((Enum[]) enumClass.getEnumConstants())[this.readVarIntFromBuffer()];
	}

	public void writeEnumValue(Enum<?> value) {
		this.writeVarIntToBuffer(value.ordinal());
	}

	/**
	 * Reads a compressed int from the buffer. To do so it maximally reads 5
	 * byte-sized chunks whose most significant bit dictates whether another byte
	 * should be read.
	 */
	public int readVarIntFromBuffer() {
		int i = 0;
		int j = 0;

		while (true) {
			byte b0 = this.readByte();
			i |= (b0 & 127) << j++ * 7;

			if (j > 5) {
				throw new RuntimeException("VarInt too big");
			}

			if ((b0 & 128) != 128) {
				break;
			}
		}

		return i;
	}

	public long readVarLong() {
		long i = 0L;
		int j = 0;

		while (true) {
			byte b0 = this.readByte();
			i |= (long) (b0 & 127) << j++ * 7;

			if (j > 10) {
				throw new RuntimeException("VarLong too big");
			}

			if ((b0 & 128) != 128) {
				break;
			}
		}

		return i;
	}

	public void writeUuid(UUID uuid) {
		this.writeLong(uuid.getMostSignificantBits());
		this.writeLong(uuid.getLeastSignificantBits());
	}

	public UUID readUuid() {
		return new UUID(this.readLong(), this.readLong());
	}

	/**
	 * Writes a compressed int to the buffer. The smallest number of bytes to fit
	 * the passed int will be written. Of each such byte only 7 bits will be used to
	 * describe the actual value since its most significant bit dictates whether the
	 * next byte is part of that same int. Micro-optimization for int values that
	 * are expected to have values below 128.
	 */
	public void writeVarIntToBuffer(int input) {
		while ((input & -128) != 0) {
			this.writeByte(input & 127 | 128);
			input >>>= 7;
		}

		this.writeByte(input);
	}

	public void writeVarLong(long value) {
		while ((value & -128L) != 0L) {
			this.writeByte((int) (value & 127L) | 128);
			value >>>= 7;
		}

		this.writeByte((int) value);
	}

	/**
	 * Writes a compressed NBTTagCompound to this buffer
	 */
	public void writeNBTTagCompoundToBuffer(NBTTagCompound nbt) {
		if (nbt == null) {
			this.writeByte(0);
		} else {
			try {
				CompressedStreamTools.write(nbt, new ByteBufOutputStream(this));
			} catch (IOException ioexception) {
				throw new EncoderException(ioexception);
			}
		}
	}

	/**
	 * Reads a compressed NBTTagCompound from this buffer
	 */
	public NBTTagCompound readNBTTagCompoundFromBuffer() throws IOException {
		int i = this.readerIndex();
		byte b0 = this.readByte();

		if (b0 == 0) {
			return null;
		} else {
			this.readerIndex(i);
			return CompressedStreamTools.read(new ByteBufInputStream(this), new NBTSizeTracker(2097152L));
		}
	}

	/**
	 * Writes the ItemStack's ID (short), then size (byte), then damage. (short)
	 */
	public void writeItemStackToBuffer(ItemStack stack) {
		if (stack == null) {
			this.writeShort(-1);
		} else {
			this.writeShort(Item.getIdFromItem(stack.getItem()));
			this.writeByte(stack.stackSize);
			this.writeShort(stack.getMetadata());
			NBTTagCompound nbttagcompound = null;

			if (stack.getItem().isDamageable() || stack.getItem().getShareTag()) {
				nbttagcompound = stack.getTagCompound();
			}

			this.writeNBTTagCompoundToBuffer(nbttagcompound);
		}
	}

	/**
	 * Reads an ItemStack from this buffer
	 */
	public ItemStack readItemStackFromBuffer() throws IOException {
		ItemStack itemstack = null;
		int i = this.readShort();

		if (i >= 0) {
			int j = this.readByte();
			int k = this.readShort();
			itemstack = new ItemStack(Item.getItemById(i), j, k);
			itemstack.setTagCompound(this.readNBTTagCompoundFromBuffer());
		}

		return itemstack;
	}

	/**
	 * Reads a string from this buffer. Expected parameter is maximum allowed string
	 * length. Will throw IOException if string length exceeds this value!
	 */
	public String readStringFromBuffer(int maxLength) {
		int i = this.readVarIntFromBuffer();

		if (i > maxLength * 4) {
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
		} else if (i < 0) {
			throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
		} else {
			String s = new String(this.readBytes(i).array(), Charsets.UTF_8);

			if (s.length() > maxLength) {
				throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
			} else {
				return s;
			}
		}
	}

	public PacketBuffer writeString(String string) {
		byte[] abyte = string.getBytes(Charsets.UTF_8);

		if (abyte.length > 32767) {
			throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");
		} else {
			this.writeVarIntToBuffer(abyte.length);
			this.writeBytes(abyte);
			return this;
		}
	}

	public int capacity() {
		return this.buf.capacity();
	}

	public ByteBuf capacity(int p_capacity_1_) {
		return this.buf.capacity(p_capacity_1_);
	}

	public int maxCapacity() {
		return this.buf.maxCapacity();
	}

	public ByteBufAllocator alloc() {
		return this.buf.alloc();
	}

	public ByteOrder order() {
		return this.buf.order();
	}

	public ByteBuf order(ByteOrder p_order_1_) {
		return this.buf.order(p_order_1_);
	}

	public ByteBuf unwrap() {
		return this.buf.unwrap();
	}

	public boolean isDirect() {
		return this.buf.isDirect();
	}

	public int readerIndex() {
		return this.buf.readerIndex();
	}

	public ByteBuf readerIndex(int p_readerIndex_1_) {
		return this.buf.readerIndex(p_readerIndex_1_);
	}

	public int writerIndex() {
		return this.buf.writerIndex();
	}

	public ByteBuf writerIndex(int p_writerIndex_1_) {
		return this.buf.writerIndex(p_writerIndex_1_);
	}

	public ByteBuf setIndex(int p_setIndex_1_, int p_setIndex_2_) {
		return this.buf.setIndex(p_setIndex_1_, p_setIndex_2_);
	}

	public int readableBytes() {
		return this.buf.readableBytes();
	}

	public int writableBytes() {
		return this.buf.writableBytes();
	}

	public int maxWritableBytes() {
		return this.buf.maxWritableBytes();
	}

	public boolean isReadable() {
		return this.buf.isReadable();
	}

	public boolean isReadable(int p_isReadable_1_) {
		return this.buf.isReadable(p_isReadable_1_);
	}

	public boolean isWritable() {
		return this.buf.isWritable();
	}

	public boolean isWritable(int p_isWritable_1_) {
		return this.buf.isWritable(p_isWritable_1_);
	}

	public ByteBuf clear() {
		return this.buf.clear();
	}

	public ByteBuf markReaderIndex() {
		return this.buf.markReaderIndex();
	}

	public ByteBuf resetReaderIndex() {
		return this.buf.resetReaderIndex();
	}

	public ByteBuf markWriterIndex() {
		return this.buf.markWriterIndex();
	}

	public ByteBuf resetWriterIndex() {
		return this.buf.resetWriterIndex();
	}

	public ByteBuf discardReadBytes() {
		return this.buf.discardReadBytes();
	}

	public ByteBuf discardSomeReadBytes() {
		return this.buf.discardSomeReadBytes();
	}

	public ByteBuf ensureWritable(int p_ensureWritable_1_) {
		return this.buf.ensureWritable(p_ensureWritable_1_);
	}

	public int ensureWritable(int p_ensureWritable_1_, boolean p_ensureWritable_2_) {
		return this.buf.ensureWritable(p_ensureWritable_1_, p_ensureWritable_2_);
	}

	public boolean getBoolean(int p_getBoolean_1_) {
		return this.buf.getBoolean(p_getBoolean_1_);
	}

	public byte getByte(int p_getByte_1_) {
		return this.buf.getByte(p_getByte_1_);
	}

	public short getUnsignedByte(int p_getUnsignedByte_1_) {
		return this.buf.getUnsignedByte(p_getUnsignedByte_1_);
	}

	public short getShort(int p_getShort_1_) {
		return this.buf.getShort(p_getShort_1_);
	}

	public int getUnsignedShort(int p_getUnsignedShort_1_) {
		return this.buf.getUnsignedShort(p_getUnsignedShort_1_);
	}

	public int getMedium(int p_getMedium_1_) {
		return this.buf.getMedium(p_getMedium_1_);
	}

	public int getUnsignedMedium(int p_getUnsignedMedium_1_) {
		return this.buf.getUnsignedMedium(p_getUnsignedMedium_1_);
	}

	public int getInt(int p_getInt_1_) {
		return this.buf.getInt(p_getInt_1_);
	}

	public long getUnsignedInt(int p_getUnsignedInt_1_) {
		return this.buf.getUnsignedInt(p_getUnsignedInt_1_);
	}

	public long getLong(int p_getLong_1_) {
		return this.buf.getLong(p_getLong_1_);
	}

	public char getChar(int p_getChar_1_) {
		return this.buf.getChar(p_getChar_1_);
	}

	public float getFloat(int p_getFloat_1_) {
		return this.buf.getFloat(p_getFloat_1_);
	}

	public double getDouble(int p_getDouble_1_) {
		return this.buf.getDouble(p_getDouble_1_);
	}

	public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_) {
		return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
	}

	public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_) {
		return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
	}

	public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
		return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
	}

	public ByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_) {
		return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
	}

	public ByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
		return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
	}

	public ByteBuf getBytes(int p_getBytes_1_, ByteBuffer p_getBytes_2_) {
		return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
	}

	public ByteBuf getBytes(int p_getBytes_1_, OutputStream p_getBytes_2_, int p_getBytes_3_) throws IOException {
		return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
	}

	public int getBytes(int p_getBytes_1_, GatheringByteChannel p_getBytes_2_, int p_getBytes_3_) throws IOException {
		return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
	}

	public ByteBuf setBoolean(int p_setBoolean_1_, boolean p_setBoolean_2_) {
		return this.buf.setBoolean(p_setBoolean_1_, p_setBoolean_2_);
	}

	public ByteBuf setByte(int p_setByte_1_, int p_setByte_2_) {
		return this.buf.setByte(p_setByte_1_, p_setByte_2_);
	}

	public ByteBuf setShort(int p_setShort_1_, int p_setShort_2_) {
		return this.buf.setShort(p_setShort_1_, p_setShort_2_);
	}

	public ByteBuf setMedium(int p_setMedium_1_, int p_setMedium_2_) {
		return this.buf.setMedium(p_setMedium_1_, p_setMedium_2_);
	}

	public ByteBuf setInt(int p_setInt_1_, int p_setInt_2_) {
		return this.buf.setInt(p_setInt_1_, p_setInt_2_);
	}

	public ByteBuf setLong(int p_setLong_1_, long p_setLong_2_) {
		return this.buf.setLong(p_setLong_1_, p_setLong_2_);
	}

	public ByteBuf setChar(int p_setChar_1_, int p_setChar_2_) {
		return this.buf.setChar(p_setChar_1_, p_setChar_2_);
	}

	public ByteBuf setFloat(int p_setFloat_1_, float p_setFloat_2_) {
		return this.buf.setFloat(p_setFloat_1_, p_setFloat_2_);
	}

	public ByteBuf setDouble(int p_setDouble_1_, double p_setDouble_2_) {
		return this.buf.setDouble(p_setDouble_1_, p_setDouble_2_);
	}

	public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_) {
		return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
	}

	public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_) {
		return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
	}

	public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
		return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
	}

	public ByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_) {
		return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
	}

	public ByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
		return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
	}

	public ByteBuf setBytes(int p_setBytes_1_, ByteBuffer p_setBytes_2_) {
		return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
	}

	public int setBytes(int p_setBytes_1_, InputStream p_setBytes_2_, int p_setBytes_3_) throws IOException {
		return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
	}

	public int setBytes(int p_setBytes_1_, ScatteringByteChannel p_setBytes_2_, int p_setBytes_3_) throws IOException {
		return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
	}

	public ByteBuf setZero(int p_setZero_1_, int p_setZero_2_) {
		return this.buf.setZero(p_setZero_1_, p_setZero_2_);
	}

	public boolean readBoolean() {
		return this.buf.readBoolean();
	}

	public byte readByte() {
		return this.buf.readByte();
	}

	public short readUnsignedByte() {
		return this.buf.readUnsignedByte();
	}

	public short readShort() {
		return this.buf.readShort();
	}

	public int readUnsignedShort() {
		return this.buf.readUnsignedShort();
	}

	public int readMedium() {
		return this.buf.readMedium();
	}

	public int readUnsignedMedium() {
		return this.buf.readUnsignedMedium();
	}

	public int readInt() {
		return this.buf.readInt();
	}

	public long readUnsignedInt() {
		return this.buf.readUnsignedInt();
	}

	public long readLong() {
		return this.buf.readLong();
	}

	public char readChar() {
		return this.buf.readChar();
	}

	public float readFloat() {
		return this.buf.readFloat();
	}

	public double readDouble() {
		return this.buf.readDouble();
	}

	public ByteBuf readBytes(int p_readBytes_1_) {
		return this.buf.readBytes(p_readBytes_1_);
	}

	public ByteBuf readSlice(int p_readSlice_1_) {
		return this.buf.readSlice(p_readSlice_1_);
	}

	public ByteBuf readBytes(ByteBuf p_readBytes_1_) {
		return this.buf.readBytes(p_readBytes_1_);
	}

	public ByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_) {
		return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
	}

	public ByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
		return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
	}

	public ByteBuf readBytes(byte[] p_readBytes_1_) {
		return this.buf.readBytes(p_readBytes_1_);
	}

	public ByteBuf readBytes(byte[] p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
		return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
	}

	public ByteBuf readBytes(ByteBuffer p_readBytes_1_) {
		return this.buf.readBytes(p_readBytes_1_);
	}

	public ByteBuf readBytes(OutputStream p_readBytes_1_, int p_readBytes_2_) throws IOException {
		return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
	}

	public int readBytes(GatheringByteChannel p_readBytes_1_, int p_readBytes_2_) throws IOException {
		return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
	}

	public ByteBuf skipBytes(int p_skipBytes_1_) {
		return this.buf.skipBytes(p_skipBytes_1_);
	}

	public ByteBuf writeBoolean(boolean p_writeBoolean_1_) {
		return this.buf.writeBoolean(p_writeBoolean_1_);
	}

	public ByteBuf writeByte(int p_writeByte_1_) {
		return this.buf.writeByte(p_writeByte_1_);
	}

	public ByteBuf writeShort(int p_writeShort_1_) {
		return this.buf.writeShort(p_writeShort_1_);
	}

	public ByteBuf writeMedium(int p_writeMedium_1_) {
		return this.buf.writeMedium(p_writeMedium_1_);
	}

	public ByteBuf writeInt(int p_writeInt_1_) {
		return this.buf.writeInt(p_writeInt_1_);
	}

	public ByteBuf writeLong(long p_writeLong_1_) {
		return this.buf.writeLong(p_writeLong_1_);
	}

	public ByteBuf writeChar(int p_writeChar_1_) {
		return this.buf.writeChar(p_writeChar_1_);
	}

	public ByteBuf writeFloat(float p_writeFloat_1_) {
		return this.buf.writeFloat(p_writeFloat_1_);
	}

	public ByteBuf writeDouble(double p_writeDouble_1_) {
		return this.buf.writeDouble(p_writeDouble_1_);
	}

	public ByteBuf writeBytes(ByteBuf p_writeBytes_1_) {
		return this.buf.writeBytes(p_writeBytes_1_);
	}

	public ByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_) {
		return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
	}

	public ByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
		return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
	}

	public ByteBuf writeBytes(byte[] p_writeBytes_1_) {
		return this.buf.writeBytes(p_writeBytes_1_);
	}

	public ByteBuf writeBytes(byte[] p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
		return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
	}

	public ByteBuf writeBytes(ByteBuffer p_writeBytes_1_) {
		return this.buf.writeBytes(p_writeBytes_1_);
	}

	public int writeBytes(InputStream p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
		return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
	}

	public int writeBytes(ScatteringByteChannel p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
		return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
	}

	public ByteBuf writeZero(int p_writeZero_1_) {
		return this.buf.writeZero(p_writeZero_1_);
	}

	public int indexOf(int p_indexOf_1_, int p_indexOf_2_, byte p_indexOf_3_) {
		return this.buf.indexOf(p_indexOf_1_, p_indexOf_2_, p_indexOf_3_);
	}

	public int bytesBefore(byte p_bytesBefore_1_) {
		return this.buf.bytesBefore(p_bytesBefore_1_);
	}

	public int bytesBefore(int p_bytesBefore_1_, byte p_bytesBefore_2_) {
		return this.buf.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_);
	}

	public int bytesBefore(int p_bytesBefore_1_, int p_bytesBefore_2_, byte p_bytesBefore_3_) {
		return this.buf.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_, p_bytesBefore_3_);
	}

	public int forEachByte(ByteBufProcessor p_forEachByte_1_) {
		return this.buf.forEachByte(p_forEachByte_1_);
	}

	public int forEachByte(int p_forEachByte_1_, int p_forEachByte_2_, ByteBufProcessor p_forEachByte_3_) {
		return this.buf.forEachByte(p_forEachByte_1_, p_forEachByte_2_, p_forEachByte_3_);
	}

	public int forEachByteDesc(ByteBufProcessor p_forEachByteDesc_1_) {
		return this.buf.forEachByteDesc(p_forEachByteDesc_1_);
	}

	public int forEachByteDesc(int p_forEachByteDesc_1_, int p_forEachByteDesc_2_, ByteBufProcessor p_forEachByteDesc_3_) {
		return this.buf.forEachByteDesc(p_forEachByteDesc_1_, p_forEachByteDesc_2_, p_forEachByteDesc_3_);
	}

	public ByteBuf copy() {
		return this.buf.copy();
	}

	public ByteBuf copy(int p_copy_1_, int p_copy_2_) {
		return this.buf.copy(p_copy_1_, p_copy_2_);
	}

	public ByteBuf slice() {
		return this.buf.slice();
	}

	public ByteBuf slice(int p_slice_1_, int p_slice_2_) {
		return this.buf.slice(p_slice_1_, p_slice_2_);
	}

	public ByteBuf duplicate() {
		return this.buf.duplicate();
	}

	public int nioBufferCount() {
		return this.buf.nioBufferCount();
	}

	public ByteBuffer nioBuffer() {
		return this.buf.nioBuffer();
	}

	public ByteBuffer nioBuffer(int p_nioBuffer_1_, int p_nioBuffer_2_) {
		return this.buf.nioBuffer(p_nioBuffer_1_, p_nioBuffer_2_);
	}

	public ByteBuffer internalNioBuffer(int p_internalNioBuffer_1_, int p_internalNioBuffer_2_) {
		return this.buf.internalNioBuffer(p_internalNioBuffer_1_, p_internalNioBuffer_2_);
	}

	public ByteBuffer[] nioBuffers() {
		return this.buf.nioBuffers();
	}

	public ByteBuffer[] nioBuffers(int p_nioBuffers_1_, int p_nioBuffers_2_) {
		return this.buf.nioBuffers(p_nioBuffers_1_, p_nioBuffers_2_);
	}

	public boolean hasArray() {
		return this.buf.hasArray();
	}

	public byte[] array() {
		return this.buf.array();
	}

	public int arrayOffset() {
		return this.buf.arrayOffset();
	}

	public boolean hasMemoryAddress() {
		return this.buf.hasMemoryAddress();
	}

	public long memoryAddress() {
		return this.buf.memoryAddress();
	}

	public String toString(Charset p_toString_1_) {
		return this.buf.toString(p_toString_1_);
	}

	public String toString(int p_toString_1_, int p_toString_2_, Charset p_toString_3_) {
		return this.buf.toString(p_toString_1_, p_toString_2_, p_toString_3_);
	}

	public int hashCode() {
		return this.buf.hashCode();
	}

	public boolean equals(Object p_equals_1_) {
		return this.buf.equals(p_equals_1_);
	}

	public int compareTo(ByteBuf p_compareTo_1_) {
		return this.buf.compareTo(p_compareTo_1_);
	}

	public String toString() {
		return this.buf.toString();
	}

	public ByteBuf retain(int p_retain_1_) {
		return this.buf.retain(p_retain_1_);
	}

	public ByteBuf retain() {
		return this.buf.retain();
	}

	public int refCnt() {
		return this.buf.refCnt();
	}

	public boolean release() {
		return this.buf.release();
	}

	public boolean release(int p_release_1_) {
		return this.buf.release(p_release_1_);
	}
}
