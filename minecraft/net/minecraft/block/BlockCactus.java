package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockCactus extends Block
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

    protected BlockCactus()
    {
        super(Material.cactus);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        BlockPos blockpos = pos.up();

        if (worldIn.isAirBlock(blockpos))
        {
            int i;

            for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i)
            {
                ;
            }

            if (i < 3)
            {
                int j = ((Integer)state.getValue(AGE)).intValue();

                if (j == 15)
                {
                    worldIn.setBlockState(blockpos, this.getDefaultState());
                    IBlockState iblockstate = state.withProperty(AGE, Integer.valueOf(0));
                    worldIn.setBlockState(pos, iblockstate, 4);
                    this.onNeighborBlockChange(worldIn, blockpos, iblockstate, this);
                }
                else
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(j + 1)), 4);
                }
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        float f = 0.0625F;
        return new AxisAlignedBB((double)((float)pos.getX() + f), (double)pos.getY(), (double)((float)pos.getZ() + f), (double)((float)(pos.getX() + 1) - f), (double)((float)(pos.getY() + 1) - f), (double)((float)(pos.getZ() + 1) - f));
    }

    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
    {
        float f = 0.0625F;
        return new AxisAlignedBB((double)((float)pos.getX() + f), (double)pos.getY(), (double)((float)pos.getZ() + f), (double)((float)(pos.getX() + 1) - f), (double)(pos.getY() + 1), (double)((float)(pos.getZ() + 1) - f));
    }

    public boolean isFullCube()
    {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) ? this.canBlockStay(worldIn, pos) : false;
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!this.canBlockStay(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos)
    {
        for (Object enumfacing0 : EnumFacing.Plane.HORIZONTAL)
        {
            EnumFacing enumfacing = (EnumFacing) enumfacing0;

            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getMaterial().isSolid())
            {
                return false;
            }
        }

        Block block = worldIn.getBlockState(pos.down()).getBlock();
        return block == Blocks.cactus || block == Blocks.sand;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        entityIn.attackEntityFrom(DamageSource.cactus, 1.0F);
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(AGE)).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {AGE});
    }
}
