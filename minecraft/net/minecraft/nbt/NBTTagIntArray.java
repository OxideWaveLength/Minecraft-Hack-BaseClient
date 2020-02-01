package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase
{
    /** The array of saved integers */
    private int[] intArray;

    NBTTagIntArray()
    {
    }

    public NBTTagIntArray(int[] p_i45132_1_)
    {
        this.intArray = p_i45132_1_;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException
    {
        output.writeInt(this.intArray.length);

        for (int i = 0; i < this.intArray.length; ++i)
        {
            output.writeInt(this.intArray[i]);
        }
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException
    {
        sizeTracker.read(192L);
        int i = input.readInt();
        sizeTracker.read((long)(32 * i));
        this.intArray = new int[i];

        for (int j = 0; j < i; ++j)
        {
            this.intArray[j] = input.readInt();
        }
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)11;
    }

    public String toString()
    {
        String s = "[";

        for (int i : this.intArray)
        {
            s = s + i + ",";
        }

        return s + "]";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        int[] aint = new int[this.intArray.length];
        System.arraycopy(this.intArray, 0, aint, 0, this.intArray.length);
        return new NBTTagIntArray(aint);
    }

    public boolean equals(Object p_equals_1_)
    {
        return super.equals(p_equals_1_) ? Arrays.equals(this.intArray, ((NBTTagIntArray)p_equals_1_).intArray) : false;
    }

    public int hashCode()
    {
        return super.hashCode() ^ Arrays.hashCode(this.intArray);
    }

    public int[] getIntArray()
    {
        return this.intArray;
    }
}
