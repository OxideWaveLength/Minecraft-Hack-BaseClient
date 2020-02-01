package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool EXTENDED = PropertyBool.create("extended");

    /** This piston is the sticky one? */
    private final boolean isSticky;

    public BlockPistonBase(boolean isSticky)
    {
        super(Material.piston);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EXTENDED, Boolean.valueOf(false)));
        this.isSticky = isSticky;
        this.setStepSound(soundTypePiston);
        this.setHardness(0.5F);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)), 2);

        if (!worldIn.isRemote)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!worldIn.isRemote)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)).withProperty(EXTENDED, Boolean.valueOf(false));
    }

    private void checkForMove(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

        if (flag && !((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            if ((new BlockPistonStructureHelper(worldIn, pos, enumfacing, true)).canMove())
            {
                worldIn.addBlockEvent(pos, this, 0, enumfacing.getIndex());
            }
        }
        else if (!flag && ((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(false)), 2);
            worldIn.addBlockEvent(pos, this, 1, enumfacing.getIndex());
        }
    }

    private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (enumfacing != facing && worldIn.isSidePowered(pos.offset(enumfacing), enumfacing))
            {
                return true;
            }
        }

        if (worldIn.isSidePowered(pos, EnumFacing.DOWN))
        {
            return true;
        }
        else
        {
            BlockPos blockpos = pos.up();

            for (EnumFacing enumfacing1 : EnumFacing.values())
            {
                if (enumfacing1 != EnumFacing.DOWN && worldIn.isSidePowered(blockpos.offset(enumfacing1), enumfacing1))
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Called on both Client and Server when World#addBlockEvent is called
     */
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

        if (!worldIn.isRemote)
        {
            boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

            if (flag && eventID == 1)
            {
                worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
                return false;
            }

            if (!flag && eventID == 0)
            {
                return false;
            }
        }

        if (eventID == 0)
        {
            if (!this.doMove(worldIn, pos, enumfacing, true))
            {
                return false;
            }

            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "tile.piston.out", 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
        }
        else if (eventID == 1)
        {
            TileEntity tileentity1 = worldIn.getTileEntity(pos.offset(enumfacing));

            if (tileentity1 instanceof TileEntityPiston)
            {
                ((TileEntityPiston)tileentity1).clearPistonTileEntity();
            }

            worldIn.setBlockState(pos, Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, enumfacing).withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
            worldIn.setTileEntity(pos, BlockPistonMoving.newTileEntity(this.getStateFromMeta(eventParam), enumfacing, false, true));

            if (this.isSticky)
            {
                BlockPos blockpos = pos.add(enumfacing.getFrontOffsetX() * 2, enumfacing.getFrontOffsetY() * 2, enumfacing.getFrontOffsetZ() * 2);
                Block block = worldIn.getBlockState(blockpos).getBlock();
                boolean flag1 = false;

                if (block == Blocks.piston_extension)
                {
                    TileEntity tileentity = worldIn.getTileEntity(blockpos);

                    if (tileentity instanceof TileEntityPiston)
                    {
                        TileEntityPiston tileentitypiston = (TileEntityPiston)tileentity;

                        if (tileentitypiston.getFacing() == enumfacing && tileentitypiston.isExtending())
                        {
                            tileentitypiston.clearPistonTileEntity();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && block.getMaterial() != Material.air && canPush(block, worldIn, blockpos, enumfacing.getOpposite(), false) && (block.getMobilityFlag() == 0 || block == Blocks.piston || block == Blocks.sticky_piston))
                {
                    this.doMove(worldIn, pos, enumfacing, false);
                }
            }
            else
            {
                worldIn.setBlockToAir(pos.offset(enumfacing));
            }

            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "tile.piston.in", 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == this && ((Boolean)iblockstate.getValue(EXTENDED)).booleanValue())
        {
            float f = 0.25F;
            EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(FACING);

            if (enumfacing != null)
            {
                switch (enumfacing)
                {
                    case DOWN:
                        this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                        break;

                    case UP:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                        break;

                    case NORTH:
                        this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                        break;

                    case SOUTH:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                        break;

                    case WEST:
                        this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                        break;

                    case EAST:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
                }
            }
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     */
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    public boolean isFullCube()
    {
        return false;
    }

    public static EnumFacing getFacing(int meta)
    {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn)
    {
        if (MathHelper.abs((float)entityIn.posX - (float)clickedBlock.getX()) < 2.0F && MathHelper.abs((float)entityIn.posZ - (float)clickedBlock.getZ()) < 2.0F)
        {
            double d0 = entityIn.posY + (double)entityIn.getEyeHeight();

            if (d0 - (double)clickedBlock.getY() > 2.0D)
            {
                return EnumFacing.UP;
            }

            if ((double)clickedBlock.getY() - d0 > 0.0D)
            {
                return EnumFacing.DOWN;
            }
        }

        return entityIn.getHorizontalFacing().getOpposite();
    }

    public static boolean canPush(Block blockIn, World worldIn, BlockPos pos, EnumFacing direction, boolean allowDestroy)
    {
        if (blockIn == Blocks.obsidian)
        {
            return false;
        }
        else if (!worldIn.getWorldBorder().contains(pos))
        {
            return false;
        }
        else if (pos.getY() >= 0 && (direction != EnumFacing.DOWN || pos.getY() != 0))
        {
            if (pos.getY() <= worldIn.getHeight() - 1 && (direction != EnumFacing.UP || pos.getY() != worldIn.getHeight() - 1))
            {
                if (blockIn != Blocks.piston && blockIn != Blocks.sticky_piston)
                {
                    if (blockIn.getBlockHardness(worldIn, pos) == -1.0F)
                    {
                        return false;
                    }

                    if (blockIn.getMobilityFlag() == 2)
                    {
                        return false;
                    }

                    if (blockIn.getMobilityFlag() == 1)
                    {
                        if (!allowDestroy)
                        {
                            return false;
                        }

                        return true;
                    }
                }
                else if (((Boolean)worldIn.getBlockState(pos).getValue(EXTENDED)).booleanValue())
                {
                    return false;
                }

                return !(blockIn instanceof ITileEntityProvider);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending)
    {
        if (!extending)
        {
            worldIn.setBlockToAir(pos.offset(direction));
        }

        BlockPistonStructureHelper blockpistonstructurehelper = new BlockPistonStructureHelper(worldIn, pos, direction, extending);
        List<BlockPos> list = blockpistonstructurehelper.getBlocksToMove();
        List<BlockPos> list1 = blockpistonstructurehelper.getBlocksToDestroy();

        if (!blockpistonstructurehelper.canMove())
        {
            return false;
        }
        else
        {
            int i = list.size() + list1.size();
            Block[] ablock = new Block[i];
            EnumFacing enumfacing = extending ? direction : direction.getOpposite();

            for (int j = list1.size() - 1; j >= 0; --j)
            {
                BlockPos blockpos = (BlockPos)list1.get(j);
                Block block = worldIn.getBlockState(blockpos).getBlock();
                block.dropBlockAsItem(worldIn, blockpos, worldIn.getBlockState(blockpos), 0);
                worldIn.setBlockToAir(blockpos);
                --i;
                ablock[i] = block;
            }

            for (int k = list.size() - 1; k >= 0; --k)
            {
                BlockPos blockpos2 = (BlockPos)list.get(k);
                IBlockState iblockstate = worldIn.getBlockState(blockpos2);
                Block block1 = iblockstate.getBlock();
                block1.getMetaFromState(iblockstate);
                worldIn.setBlockToAir(blockpos2);
                blockpos2 = blockpos2.offset(enumfacing);
                worldIn.setBlockState(blockpos2, Blocks.piston_extension.getDefaultState().withProperty(FACING, direction), 4);
                worldIn.setTileEntity(blockpos2, BlockPistonMoving.newTileEntity(iblockstate, direction, extending, false));
                --i;
                ablock[i] = block1;
            }

            BlockPos blockpos1 = pos.offset(direction);

            if (extending)
            {
                BlockPistonExtension.EnumPistonType blockpistonextension$enumpistontype = this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
                IBlockState iblockstate1 = Blocks.piston_head.getDefaultState().withProperty(BlockPistonExtension.FACING, direction).withProperty(BlockPistonExtension.TYPE, blockpistonextension$enumpistontype);
                IBlockState iblockstate2 = Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, direction).withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
                worldIn.setBlockState(blockpos1, iblockstate2, 4);
                worldIn.setTileEntity(blockpos1, BlockPistonMoving.newTileEntity(iblockstate1, direction, true, false));
            }

            for (int l = list1.size() - 1; l >= 0; --l)
            {
                worldIn.notifyNeighborsOfStateChange((BlockPos)list1.get(l), ablock[i++]);
            }

            for (int i1 = list.size() - 1; i1 >= 0; --i1)
            {
                worldIn.notifyNeighborsOfStateChange((BlockPos)list.get(i1), ablock[i++]);
            }

            if (extending)
            {
                worldIn.notifyNeighborsOfStateChange(blockpos1, Blocks.piston_head);
                worldIn.notifyNeighborsOfStateChange(pos, this);
            }

            return true;
        }
    }

    /**
     * Possibly modify the given BlockState before rendering it on an Entity (Minecarts, Endermen, ...)
     */
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.UP);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(EXTENDED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, EXTENDED});
    }
}
