package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockBreakable extends Block
{
    private boolean ignoreSimilarity;

    protected BlockBreakable(Material materialIn, boolean ignoreSimilarityIn)
    {
        this(materialIn, ignoreSimilarityIn, materialIn.getMaterialMapColor());
    }

    protected BlockBreakable(Material p_i46393_1_, boolean p_i46393_2_, MapColor p_i46393_3_)
    {
        super(p_i46393_1_, p_i46393_3_);
        this.ignoreSimilarity = p_i46393_2_;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (this == Blocks.glass || this == Blocks.stained_glass)
        {
            if (worldIn.getBlockState(pos.offset(side.getOpposite())) != iblockstate)
            {
                return true;
            }

            if (block == this)
            {
                return false;
            }
        }

        return !this.ignoreSimilarity && block == this ? false : super.shouldSideBeRendered(worldIn, pos, side);
    }
}
