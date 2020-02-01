package net.minecraft.world.chunk.storage;

public class NibbleArrayReader
{
    public final byte[] data;
    private final int depthBits;
    private final int depthBitsPlusFour;

    public NibbleArrayReader(byte[] dataIn, int depthBitsIn)
    {
        this.data = dataIn;
        this.depthBits = depthBitsIn;
        this.depthBitsPlusFour = depthBitsIn + 4;
    }

    public int get(int p_76686_1_, int p_76686_2_, int p_76686_3_)
    {
        int i = p_76686_1_ << this.depthBitsPlusFour | p_76686_3_ << this.depthBits | p_76686_2_;
        int j = i >> 1;
        int k = i & 1;
        return k == 0 ? this.data[j] & 15 : this.data[j] >> 4 & 15;
    }
}
