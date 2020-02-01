package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class FlatLayerInfo
{
    private final int field_175902_a;
    private IBlockState field_175901_b;

    /** Amount of layers for this set of layers. */
    private int layerCount;
    private int layerMinimumY;

    public FlatLayerInfo(int p_i45467_1_, Block p_i45467_2_)
    {
        this(3, p_i45467_1_, p_i45467_2_);
    }

    public FlatLayerInfo(int p_i45627_1_, int p_i45627_2_, Block p_i45627_3_)
    {
        this.layerCount = 1;
        this.field_175902_a = p_i45627_1_;
        this.layerCount = p_i45627_2_;
        this.field_175901_b = p_i45627_3_.getDefaultState();
    }

    public FlatLayerInfo(int p_i45628_1_, int p_i45628_2_, Block p_i45628_3_, int p_i45628_4_)
    {
        this(p_i45628_1_, p_i45628_2_, p_i45628_3_);
        this.field_175901_b = p_i45628_3_.getStateFromMeta(p_i45628_4_);
    }

    /**
     * Return the amount of layers for this set of layers.
     */
    public int getLayerCount()
    {
        return this.layerCount;
    }

    public IBlockState func_175900_c()
    {
        return this.field_175901_b;
    }

    private Block func_151536_b()
    {
        return this.field_175901_b.getBlock();
    }

    /**
     * Return the block metadata used on this set of layers.
     */
    private int getFillBlockMeta()
    {
        return this.field_175901_b.getBlock().getMetaFromState(this.field_175901_b);
    }

    /**
     * Return the minimum Y coordinate for this layer, set during generation.
     */
    public int getMinY()
    {
        return this.layerMinimumY;
    }

    /**
     * Set the minimum Y coordinate for this layer.
     */
    public void setMinY(int p_82660_1_)
    {
        this.layerMinimumY = p_82660_1_;
    }

    public String toString()
    {
        String s;

        if (this.field_175902_a >= 3)
        {
            ResourceLocation resourcelocation = (ResourceLocation)Block.blockRegistry.getNameForObject(this.func_151536_b());
            s = resourcelocation == null ? "null" : resourcelocation.toString();

            if (this.layerCount > 1)
            {
                s = this.layerCount + "*" + s;
            }
        }
        else
        {
            s = Integer.toString(Block.getIdFromBlock(this.func_151536_b()));

            if (this.layerCount > 1)
            {
                s = this.layerCount + "x" + s;
            }
        }

        int i = this.getFillBlockMeta();

        if (i > 0)
        {
            s = s + ":" + i;
        }

        return s;
    }
}
