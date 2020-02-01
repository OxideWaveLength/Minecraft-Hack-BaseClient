package net.minecraft.block;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<BlockStairs.EnumHalf> HALF = PropertyEnum.<BlockStairs.EnumHalf>create("half", BlockStairs.EnumHalf.class);
    public static final PropertyEnum<BlockStairs.EnumShape> SHAPE = PropertyEnum.<BlockStairs.EnumShape>create("shape", BlockStairs.EnumShape.class);
    private static final int[][] field_150150_a = new int[][] {{4, 5}, {5, 7}, {6, 7}, {4, 6}, {0, 1}, {1, 3}, {2, 3}, {0, 2}};
    private final Block modelBlock;
    private final IBlockState modelState;
    private boolean hasRaytraced;
    private int rayTracePass;

    protected BlockStairs(IBlockState modelState)
    {
        super(modelState.getBlock().blockMaterial);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, BlockStairs.EnumHalf.BOTTOM).withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT));
        this.modelBlock = modelState.getBlock();
        this.modelState = modelState;
        this.setHardness(this.modelBlock.blockHardness);
        this.setResistance(this.modelBlock.blockResistance / 3.0F);
        this.setStepSound(this.modelBlock.stepSound);
        this.setLightOpacity(255);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        if (this.hasRaytraced)
        {
            this.setBlockBounds(0.5F * (float)(this.rayTracePass % 2), 0.5F * (float)(this.rayTracePass / 4 % 2), 0.5F * (float)(this.rayTracePass / 2 % 2), 0.5F + 0.5F * (float)(this.rayTracePass % 2), 0.5F + 0.5F * (float)(this.rayTracePass / 4 % 2), 0.5F + 0.5F * (float)(this.rayTracePass / 2 % 2));
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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

    /**
     * Set the block bounds as the collision bounds for the stairs at the given position
     */
    public void setBaseCollisionBounds(IBlockAccess worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos).getValue(HALF) == BlockStairs.EnumHalf.TOP)
        {
            this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    /**
     * Checks if a block is stairs
     */
    public static boolean isBlockStairs(Block blockIn)
    {
        return blockIn instanceof BlockStairs;
    }

    /**
     * Check whether there is a stair block at the given position and it has the same properties as the given BlockState
     */
    public static boolean isSameStair(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return isBlockStairs(block) && iblockstate.getValue(HALF) == state.getValue(HALF) && iblockstate.getValue(FACING) == state.getValue(FACING);
    }

    public int func_176307_f(IBlockAccess blockAccess, BlockPos pos)
    {
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(FACING);
        BlockStairs.EnumHalf blockstairs$enumhalf = (BlockStairs.EnumHalf)iblockstate.getValue(HALF);
        boolean flag = blockstairs$enumhalf == BlockStairs.EnumHalf.TOP;

        if (enumfacing == EnumFacing.EAST)
        {
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.east());
            Block block = iblockstate1.getBlock();

            if (isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF))
            {
                EnumFacing enumfacing1 = (EnumFacing)iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate))
                {
                    return flag ? 1 : 2;
                }

                if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate))
                {
                    return flag ? 2 : 1;
                }
            }
        }
        else if (enumfacing == EnumFacing.WEST)
        {
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.west());
            Block block1 = iblockstate2.getBlock();

            if (isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF))
            {
                EnumFacing enumfacing2 = (EnumFacing)iblockstate2.getValue(FACING);

                if (enumfacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate))
                {
                    return flag ? 2 : 1;
                }

                if (enumfacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate))
                {
                    return flag ? 1 : 2;
                }
            }
        }
        else if (enumfacing == EnumFacing.SOUTH)
        {
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.south());
            Block block2 = iblockstate3.getBlock();

            if (isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF))
            {
                EnumFacing enumfacing3 = (EnumFacing)iblockstate3.getValue(FACING);

                if (enumfacing3 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iblockstate))
                {
                    return flag ? 2 : 1;
                }

                if (enumfacing3 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iblockstate))
                {
                    return flag ? 1 : 2;
                }
            }
        }
        else if (enumfacing == EnumFacing.NORTH)
        {
            IBlockState iblockstate4 = blockAccess.getBlockState(pos.north());
            Block block3 = iblockstate4.getBlock();

            if (isBlockStairs(block3) && blockstairs$enumhalf == iblockstate4.getValue(HALF))
            {
                EnumFacing enumfacing4 = (EnumFacing)iblockstate4.getValue(FACING);

                if (enumfacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iblockstate))
                {
                    return flag ? 1 : 2;
                }

                if (enumfacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iblockstate))
                {
                    return flag ? 2 : 1;
                }
            }
        }

        return 0;
    }

    public int func_176305_g(IBlockAccess blockAccess, BlockPos pos)
    {
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(FACING);
        BlockStairs.EnumHalf blockstairs$enumhalf = (BlockStairs.EnumHalf)iblockstate.getValue(HALF);
        boolean flag = blockstairs$enumhalf == BlockStairs.EnumHalf.TOP;

        if (enumfacing == EnumFacing.EAST)
        {
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.west());
            Block block = iblockstate1.getBlock();

            if (isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF))
            {
                EnumFacing enumfacing1 = (EnumFacing)iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iblockstate))
                {
                    return flag ? 1 : 2;
                }

                if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iblockstate))
                {
                    return flag ? 2 : 1;
                }
            }
        }
        else if (enumfacing == EnumFacing.WEST)
        {
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.east());
            Block block1 = iblockstate2.getBlock();

            if (isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF))
            {
                EnumFacing enumfacing2 = (EnumFacing)iblockstate2.getValue(FACING);

                if (enumfacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iblockstate))
                {
                    return flag ? 2 : 1;
                }

                if (enumfacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iblockstate))
                {
                    return flag ? 1 : 2;
                }
            }
        }
        else if (enumfacing == EnumFacing.SOUTH)
        {
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.north());
            Block block2 = iblockstate3.getBlock();

            if (isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF))
            {
                EnumFacing enumfacing3 = (EnumFacing)iblockstate3.getValue(FACING);

                if (enumfacing3 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iblockstate))
                {
                    return flag ? 2 : 1;
                }

                if (enumfacing3 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iblockstate))
                {
                    return flag ? 1 : 2;
                }
            }
        }
        else if (enumfacing == EnumFacing.NORTH)
        {
            IBlockState iblockstate4 = blockAccess.getBlockState(pos.south());
            Block block3 = iblockstate4.getBlock();

            if (isBlockStairs(block3) && blockstairs$enumhalf == iblockstate4.getValue(HALF))
            {
                EnumFacing enumfacing4 = (EnumFacing)iblockstate4.getValue(FACING);

                if (enumfacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iblockstate))
                {
                    return flag ? 1 : 2;
                }

                if (enumfacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iblockstate))
                {
                    return flag ? 2 : 1;
                }
            }
        }

        return 0;
    }

    public boolean func_176306_h(IBlockAccess blockAccess, BlockPos pos)
    {
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(FACING);
        BlockStairs.EnumHalf blockstairs$enumhalf = (BlockStairs.EnumHalf)iblockstate.getValue(HALF);
        boolean flag = blockstairs$enumhalf == BlockStairs.EnumHalf.TOP;
        float f = 0.5F;
        float f1 = 1.0F;

        if (flag)
        {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.5F;
        boolean flag1 = true;

        if (enumfacing == EnumFacing.EAST)
        {
            f2 = 0.5F;
            f5 = 1.0F;
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.east());
            Block block = iblockstate1.getBlock();

            if (isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF))
            {
                EnumFacing enumfacing1 = (EnumFacing)iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate))
                {
                    f5 = 0.5F;
                    flag1 = false;
                }
                else if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate))
                {
                    f4 = 0.5F;
                    flag1 = false;
                }
            }
        }
        else if (enumfacing == EnumFacing.WEST)
        {
            f3 = 0.5F;
            f5 = 1.0F;
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.west());
            Block block1 = iblockstate2.getBlock();

            if (isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF))
            {
                EnumFacing enumfacing2 = (EnumFacing)iblockstate2.getValue(FACING);

                if (enumfacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), iblockstate))
                {
                    f5 = 0.5F;
                    flag1 = false;
                }
                else if (enumfacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), iblockstate))
                {
                    f4 = 0.5F;
                    flag1 = false;
                }
            }
        }
        else if (enumfacing == EnumFacing.SOUTH)
        {
            f4 = 0.5F;
            f5 = 1.0F;
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.south());
            Block block2 = iblockstate3.getBlock();

            if (isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF))
            {
                EnumFacing enumfacing3 = (EnumFacing)iblockstate3.getValue(FACING);

                if (enumfacing3 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iblockstate))
                {
                    f3 = 0.5F;
                    flag1 = false;
                }
                else if (enumfacing3 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iblockstate))
                {
                    f2 = 0.5F;
                    flag1 = false;
                }
            }
        }
        else if (enumfacing == EnumFacing.NORTH)
        {
            IBlockState iblockstate4 = blockAccess.getBlockState(pos.north());
            Block block3 = iblockstate4.getBlock();

            if (isBlockStairs(block3) && blockstairs$enumhalf == iblockstate4.getValue(HALF))
            {
                EnumFacing enumfacing4 = (EnumFacing)iblockstate4.getValue(FACING);

                if (enumfacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), iblockstate))
                {
                    f3 = 0.5F;
                    flag1 = false;
                }
                else if (enumfacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), iblockstate))
                {
                    f2 = 0.5F;
                    flag1 = false;
                }
            }
        }

        this.setBlockBounds(f2, f, f4, f3, f1, f5);
        return flag1;
    }

    public boolean func_176304_i(IBlockAccess blockAccess, BlockPos pos)
    {
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(FACING);
        BlockStairs.EnumHalf blockstairs$enumhalf = (BlockStairs.EnumHalf)iblockstate.getValue(HALF);
        boolean flag = blockstairs$enumhalf == BlockStairs.EnumHalf.TOP;
        float f = 0.5F;
        float f1 = 1.0F;

        if (flag)
        {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = 0.5F;
        float f5 = 1.0F;
        boolean flag1 = false;

        if (enumfacing == EnumFacing.EAST)
        {
            IBlockState iblockstate1 = blockAccess.getBlockState(pos.west());
            Block block = iblockstate1.getBlock();

            if (isBlockStairs(block) && blockstairs$enumhalf == iblockstate1.getValue(HALF))
            {
                EnumFacing enumfacing1 = (EnumFacing)iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iblockstate))
                {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag1 = true;
                }
                else if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iblockstate))
                {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag1 = true;
                }
            }
        }
        else if (enumfacing == EnumFacing.WEST)
        {
            IBlockState iblockstate2 = blockAccess.getBlockState(pos.east());
            Block block1 = iblockstate2.getBlock();

            if (isBlockStairs(block1) && blockstairs$enumhalf == iblockstate2.getValue(HALF))
            {
                f2 = 0.5F;
                f3 = 1.0F;
                EnumFacing enumfacing2 = (EnumFacing)iblockstate2.getValue(FACING);

                if (enumfacing2 == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), iblockstate))
                {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag1 = true;
                }
                else if (enumfacing2 == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), iblockstate))
                {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag1 = true;
                }
            }
        }
        else if (enumfacing == EnumFacing.SOUTH)
        {
            IBlockState iblockstate3 = blockAccess.getBlockState(pos.north());
            Block block2 = iblockstate3.getBlock();

            if (isBlockStairs(block2) && blockstairs$enumhalf == iblockstate3.getValue(HALF))
            {
                f4 = 0.0F;
                f5 = 0.5F;
                EnumFacing enumfacing3 = (EnumFacing)iblockstate3.getValue(FACING);

                if (enumfacing3 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iblockstate))
                {
                    flag1 = true;
                }
                else if (enumfacing3 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iblockstate))
                {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag1 = true;
                }
            }
        }
        else if (enumfacing == EnumFacing.NORTH)
        {
            IBlockState iblockstate4 = blockAccess.getBlockState(pos.south());
            Block block3 = iblockstate4.getBlock();

            if (isBlockStairs(block3) && blockstairs$enumhalf == iblockstate4.getValue(HALF))
            {
                EnumFacing enumfacing4 = (EnumFacing)iblockstate4.getValue(FACING);

                if (enumfacing4 == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), iblockstate))
                {
                    flag1 = true;
                }
                else if (enumfacing4 == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), iblockstate))
                {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag1 = true;
                }
            }
        }

        if (flag1)
        {
            this.setBlockBounds(f2, f, f4, f3, f1, f5);
        }

        return flag1;
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     */
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        this.setBaseCollisionBounds(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        boolean flag = this.func_176306_h(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);

        if (flag && this.func_176304_i(worldIn, pos))
        {
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        this.modelBlock.randomDisplayTick(worldIn, pos, state, rand);
    }

    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        this.modelBlock.onBlockClicked(worldIn, pos, playerIn);
    }

    /**
     * Called when a player destroys this Block
     */
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
        this.modelBlock.onBlockDestroyedByPlayer(worldIn, pos, state);
    }

    public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos)
    {
        return this.modelBlock.getMixedBrightnessForBlock(worldIn, pos);
    }

    /**
     * Returns how much this block can resist explosions from the passed in entity.
     */
    public float getExplosionResistance(Entity exploder)
    {
        return this.modelBlock.getExplosionResistance(exploder);
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return this.modelBlock.getBlockLayer();
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return this.modelBlock.tickRate(worldIn);
    }

    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
    {
        return this.modelBlock.getSelectedBoundingBox(worldIn, pos);
    }

    public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion)
    {
        return this.modelBlock.modifyAcceleration(worldIn, pos, entityIn, motion);
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean isCollidable()
    {
        return this.modelBlock.isCollidable();
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return this.modelBlock.canCollideCheck(state, hitIfLiquid);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return this.modelBlock.canPlaceBlockAt(worldIn, pos);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.onNeighborBlockChange(worldIn, pos, this.modelState, Blocks.air);
        this.modelBlock.onBlockAdded(worldIn, pos, this.modelState);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        this.modelBlock.breakBlock(worldIn, pos, this.modelState);
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block)
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn)
    {
        this.modelBlock.onEntityCollidedWithBlock(worldIn, pos, entityIn);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        this.modelBlock.updateTick(worldIn, pos, state, rand);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return this.modelBlock.onBlockActivated(worldIn, pos, this.modelState, playerIn, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
    }

    /**
     * Called when this Block is destroyed by an Explosion
     */
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        this.modelBlock.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state)
    {
        return this.modelBlock.getMapColor(this.modelState);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState iblockstate = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        iblockstate = iblockstate.withProperty(FACING, placer.getHorizontalFacing()).withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
        return facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double)hitY <= 0.5D) ? iblockstate.withProperty(HALF, BlockStairs.EnumHalf.BOTTOM) : iblockstate.withProperty(HALF, BlockStairs.EnumHalf.TOP);
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     */
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end)
    {
        MovingObjectPosition[] amovingobjectposition = new MovingObjectPosition[8];
        IBlockState iblockstate = worldIn.getBlockState(pos);
        int i = ((EnumFacing)iblockstate.getValue(FACING)).getHorizontalIndex();
        boolean flag = iblockstate.getValue(HALF) == BlockStairs.EnumHalf.TOP;
        int[] aint = field_150150_a[i + (flag ? 4 : 0)];
        this.hasRaytraced = true;

        for (int j = 0; j < 8; ++j)
        {
            this.rayTracePass = j;

            if (Arrays.binarySearch(aint, j) < 0)
            {
                amovingobjectposition[j] = super.collisionRayTrace(worldIn, pos, start, end);
            }
        }

        for (int k : aint)
        {
            amovingobjectposition[k] = null;
        }

        MovingObjectPosition movingobjectposition1 = null;
        double d1 = 0.0D;

        for (MovingObjectPosition movingobjectposition : amovingobjectposition)
        {
            if (movingobjectposition != null)
            {
                double d0 = movingobjectposition.hitVec.squareDistanceTo(end);

                if (d0 > d1)
                {
                    movingobjectposition1 = movingobjectposition;
                    d1 = d0;
                }
            }
        }

        return movingobjectposition1;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(HALF, (meta & 4) > 0 ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM);
        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
        return iblockstate;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;

        if (state.getValue(HALF) == BlockStairs.EnumHalf.TOP)
        {
            i |= 4;
        }

        i = i | 5 - ((EnumFacing)state.getValue(FACING)).getIndex();
        return i;
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (this.func_176306_h(worldIn, pos))
        {
            switch (this.func_176305_g(worldIn, pos))
            {
                case 0:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
                    break;

                case 1:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.INNER_RIGHT);
                    break;

                case 2:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.INNER_LEFT);
            }
        }
        else
        {
            switch (this.func_176307_f(worldIn, pos))
            {
                case 0:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
                    break;

                case 1:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
                    break;

                case 2:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
            }
        }

        return state;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, HALF, SHAPE});
    }

    public static enum EnumHalf implements IStringSerializable
    {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        private EnumHalf(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }

    public static enum EnumShape implements IStringSerializable
    {
        STRAIGHT("straight"),
        INNER_LEFT("inner_left"),
        INNER_RIGHT("inner_right"),
        OUTER_LEFT("outer_left"),
        OUTER_RIGHT("outer_right");

        private final String name;

        private EnumShape(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}
