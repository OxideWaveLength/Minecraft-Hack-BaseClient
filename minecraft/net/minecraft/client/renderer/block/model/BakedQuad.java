package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class BakedQuad
{
    /**
     * Joined 4 vertex records, each has 7 fields (x, y, z, shadeColor, u, v, <unused>), see
     * FaceBakery.storeVertexData()
     */
    protected final int[] vertexData;
    protected final int tintIndex;
    protected final EnumFacing face;
    private static final String __OBFID = "CL_00002512";
    private TextureAtlasSprite sprite = null;
    private int[] vertexDataSingle = null;

    public BakedQuad(int[] p_i8_1_, int p_i8_2_, EnumFacing p_i8_3_, TextureAtlasSprite p_i8_4_)
    {
        this.vertexData = p_i8_1_;
        this.tintIndex = p_i8_2_;
        this.face = p_i8_3_;
        this.sprite = p_i8_4_;
    }

    public TextureAtlasSprite getSprite()
    {
        return this.sprite;
    }

    public String toString()
    {
        return "vertex: " + this.vertexData.length / 7 + ", tint: " + this.tintIndex + ", facing: " + this.face + ", sprite: " + this.sprite;
    }

    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn)
    {
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
    }

    public int[] getVertexData()
    {
        return this.vertexData;
    }

    public boolean hasTintIndex()
    {
        return this.tintIndex != -1;
    }

    public int getTintIndex()
    {
        return this.tintIndex;
    }

    public EnumFacing getFace()
    {
        return this.face;
    }

    public int[] getVertexDataSingle()
    {
        if (this.vertexDataSingle == null)
        {
            this.vertexDataSingle = makeVertexDataSingle(this.vertexData, this.sprite);
        }

        return this.vertexDataSingle;
    }

    private static int[] makeVertexDataSingle(int[] p_makeVertexDataSingle_0_, TextureAtlasSprite p_makeVertexDataSingle_1_)
    {
        int[] aint = new int[p_makeVertexDataSingle_0_.length];

        for (int i = 0; i < aint.length; ++i)
        {
            aint[i] = p_makeVertexDataSingle_0_[i];
        }

        int i1 = p_makeVertexDataSingle_1_.sheetWidth / p_makeVertexDataSingle_1_.getIconWidth();
        int j = p_makeVertexDataSingle_1_.sheetHeight / p_makeVertexDataSingle_1_.getIconHeight();

        for (int k = 0; k < 4; ++k)
        {
            int l = k * 7;
            float f = Float.intBitsToFloat(aint[l + 4]);
            float f1 = Float.intBitsToFloat(aint[l + 4 + 1]);
            float f2 = p_makeVertexDataSingle_1_.toSingleU(f);
            float f3 = p_makeVertexDataSingle_1_.toSingleV(f1);
            aint[l + 4] = Float.floatToRawIntBits(f2);
            aint[l + 4 + 1] = Float.floatToRawIntBits(f3);
        }

        return aint;
    }
}
