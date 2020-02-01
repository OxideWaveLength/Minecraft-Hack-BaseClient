package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonExtension extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = PropertyEnum.<BlockPistonExtension.EnumPistonType>create("type", BlockPistonExtension.EnumPistonType.class);
    public static final PropertyBool SHORT = PropertyBool.create("short");

    public BlockPistonExtension()
    {
        super(Material.piston);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT).withProperty(SHORT, Boolean.valueOf(false)));
        this.setStepSound(soundTypePiston);
        this.setHardness(0.5F);
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

            if (enumfacing != null)
            {
                BlockPos blockpos = pos.offset(enumfacing.getOpposite());
                Block block = worldIn.getBlockState(blockpos).getBlock();

                if (block == Blocks.piston || block == Blocks.sticky_piston)
                {
                    worldIn.setBlockToAir(blockpos);
                }
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        EnumFacing enumfacing = ((EnumFacing)state.getValue(FACING)).getOpposite();
        pos = pos.offset(enumfacing);
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if ((iblockstate.getBlock() == Blocks.piston || iblockstate.getBlock() == Blocks.sticky_piston) && ((Boolean)iblockstate.getValue(BlockPistonBase.EXTENDED)).booleanValue())
        {
            iblockstate.getBlock().dropBlockAsItem(worldIn, pos, iblockstate, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean isFullCube()
    {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return false;
    }

    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return false;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     */
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        this.applyHeadBounds(state);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.applyCoreBounds(state);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    private void applyCoreBounds(IBlockState state)
    {
        float f = 0.25F;
        float f1 = 0.375F;
        float f2 = 0.625F;
        float f3 = 0.25F;
        float f4 = 0.75F;

        switch ((EnumFacing)state.getValue(FACING))
        {
            case DOWN:
                this.setBlockBounds(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
                break;

            case UP:
                this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
                break;

            case NORTH:
                this.setBlockBounds(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
                break;

            case SOUTH:
                this.setBlockBounds(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
                break;

            case WEST:
                this.setBlockBounds(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
                break;

            case EAST:
                this.setBlockBounds(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        this.applyHeadBounds(worldIn.getBlockState(pos));
    }

    public void applyHeadBounds(IBlockState state)
    {
        float f = 0.25F;
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

        if (enumfacing != null)
        {
            switch (enumfacing)
            {
                case DOWN:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                    break;

                case UP:
                    this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;

                case NORTH:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                    break;

                case SOUTH:
                    this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                    break;

                case WEST:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                    break;

                case EAST:
                    this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing.getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() != Blocks.piston && iblockstate.getBlock() != Blocks.sticky_piston)
        {
            worldIn.setBlockToAir(pos);
        }
        else
        {
            iblockstate.getBlock().onNeighborBlockChange(worldIn, blockpos, iblockstate, neighborBlock);
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    public static EnumFacing getFacing(int meta)
    {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY ? Item.getItemFromBlock(Blocks.sticky_piston) : Item.getItemFromBlock(Blocks.piston);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(TYPE, (meta & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY)
        {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, TYPE, SHORT});
    }

    public static enum EnumPistonType implements IStringSerializable
    {
        DEFAULT("normal"),
        STICKY("sticky");

        private final String VARIANT;

        private EnumPistonType(String name)
        {
            this.VARIANT = name;
        }

        public String toString()
        {
            return this.VARIANT;
        }

        public String getName()
        {
            return this.VARIANT;
        }
    }
}
