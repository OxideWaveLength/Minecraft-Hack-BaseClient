package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.MathHelper;

public class NBTTagFloat extends NBTBase.NBTPrimitive
{
    /** The float value for the tag. */
    private float data;

    NBTTagFloat()
    {
    }

    public NBTTagFloat(float data)
    {
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException
    {
        output.writeFloat(this.data);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException
    {
        sizeTracker.read(96L);
        this.data = input.readFloat();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)5;
    }

    public String toString()
    {
        return "" + this.data + "f";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagFloat(this.data);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (super.equals(p_equals_1_))
        {
            NBTTagFloat nbttagfloat = (NBTTagFloat)p_equals_1_;
            return this.data == nbttagfloat.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ Float.floatToIntBits(this.data);
    }

    public long getLong()
    {
        return (long)this.data;
    }

    public int getInt()
    {
        return MathHelper.floor_float(this.data);
    }

    public short getShort()
    {
        return (short)(MathHelper.floor_float(this.data) & 65535);
    }

    public byte getByte()
    {
        return (byte)(MathHelper.floor_float(this.data) & 255);
    }

    public double getDouble()
    {
        return (double)this.data;
    }

    public float getFloat()
    {
        return this.data;
    }
}
