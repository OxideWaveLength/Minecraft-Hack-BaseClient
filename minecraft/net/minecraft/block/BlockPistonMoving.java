package net.minecraft.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {
	public static final PropertyDirection FACING = BlockPistonExtension.FACING;
	public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockPistonExtension.TYPE;

	public BlockPistonMoving() {
		super(Material.piston);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT));
		this.setHardness(-1.0F);
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the
	 * block.
	 */
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return null;
	}

	public static TileEntity newTileEntity(IBlockState state, EnumFacing facing, boolean extending, boolean renderHead) {
		return new TileEntityPiston(state, facing, extending, renderHead);
	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileEntityPiston) {
			((TileEntityPiston) tileentity).clearPistonTileEntity();
		} else {
			super.breakBlock(worldIn, pos, state);
		}
	}

	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return false;
	}

	/**
	 * Check whether this Block can be placed on the given side
	 */
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	/**
	 * Called when a player destroys this Block
	 */
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		BlockPos blockpos = pos.offset(((EnumFacing) state.getValue(FACING)).getOpposite());
		IBlockState iblockstate = worldIn.getBlockState(blockpos);

		if (iblockstate.getBlock() instanceof BlockPistonBase && ((Boolean) iblockstate.getValue(BlockPistonBase.EXTENDED)).booleanValue()) {
			worldIn.setBlockToAir(blockpos);
		}
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for
	 * render
	 */
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean isFullCube() {
		return false;
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
			worldIn.setBlockToAir(pos);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	/**
	 * Spawns this Block's drops into the World as EntityItems.
	 */
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		if (!worldIn.isRemote) {
			TileEntityPiston tileentitypiston = this.getTileEntity(worldIn, pos);

			if (tileentitypiston != null) {
				IBlockState iblockstate = tileentitypiston.getPistonState();
				iblockstate.getBlock().dropBlockAsItem(worldIn, pos, iblockstate, 0);
			}
		}
	}

	/**
	 * Ray traces through the blocks collision from start vector to end vector
	 * returning a ray trace hit.
	 */
	public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
		return null;
	}

	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!worldIn.isRemote) {
			worldIn.getTileEntity(pos);
		}
	}

	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		TileEntityPiston tileentitypiston = this.getTileEntity(worldIn, pos);

		if (tileentitypiston == null) {
			return null;
		} else {
			float f = tileentitypiston.getProgress(0.0F);

			if (tileentitypiston.isExtending()) {
				f = 1.0F - f;
			}

			return this.getBoundingBox(worldIn, pos, tileentitypiston.getPistonState(), f, tileentitypiston.getFacing());
		}
	}

	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		TileEntityPiston tileentitypiston = this.getTileEntity(worldIn, pos);

		if (tileentitypiston != null) {
			IBlockState iblockstate = tileentitypiston.getPistonState();
			Block block = iblockstate.getBlock();

			if (block == this || block.getMaterial() == Material.air) {
				return;
			}

			float f = tileentitypiston.getProgress(0.0F);

			if (tileentitypiston.isExtending()) {
				f = 1.0F - f;
			}

			block.setBlockBoundsBasedOnState(worldIn, pos);

			if (block == Blocks.piston || block == Blocks.sticky_piston) {
				f = 0.0F;
			}

			EnumFacing enumfacing = tileentitypiston.getFacing();
			this.minX = block.getBlockBoundsMinX() - (double) ((float) enumfacing.getFrontOffsetX() * f);
			this.minY = block.getBlockBoundsMinY() - (double) ((float) enumfacing.getFrontOffsetY() * f);
			this.minZ = block.getBlockBoundsMinZ() - (double) ((float) enumfacing.getFrontOffsetZ() * f);
			this.maxX = block.getBlockBoundsMaxX() - (double) ((float) enumfacing.getFrontOffsetX() * f);
			this.maxY = block.getBlockBoundsMaxY() - (double) ((float) enumfacing.getFrontOffsetY() * f);
			this.maxZ = block.getBlockBoundsMaxZ() - (double) ((float) enumfacing.getFrontOffsetZ() * f);
		}
	}

	public AxisAlignedBB getBoundingBox(World worldIn, BlockPos pos, IBlockState extendingBlock, float progress, EnumFacing direction) {
		if (extendingBlock.getBlock() != this && extendingBlock.getBlock().getMaterial() != Material.air) {
			AxisAlignedBB axisalignedbb = extendingBlock.getBlock().getCollisionBoundingBox(worldIn, pos, extendingBlock);

			if (axisalignedbb == null) {
				return null;
			} else {
				double d0 = axisalignedbb.minX;
				double d1 = axisalignedbb.minY;
				double d2 = axisalignedbb.minZ;
				double d3 = axisalignedbb.maxX;
				double d4 = axisalignedbb.maxY;
				double d5 = axisalignedbb.maxZ;

				if (direction.getFrontOffsetX() < 0) {
					d0 -= (double) ((float) direction.getFrontOffsetX() * progress);
				} else {
					d3 -= (double) ((float) direction.getFrontOffsetX() * progress);
				}

				if (direction.getFrontOffsetY() < 0) {
					d1 -= (double) ((float) direction.getFrontOffsetY() * progress);
				} else {
					d4 -= (double) ((float) direction.getFrontOffsetY() * progress);
				}

				if (direction.getFrontOffsetZ() < 0) {
					d2 -= (double) ((float) direction.getFrontOffsetZ() * progress);
				} else {
					d5 -= (double) ((float) direction.getFrontOffsetZ() * progress);
				}

				return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
			}
		} else {
			return null;
		}
	}

	private TileEntityPiston getTileEntity(IBlockAccess worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity instanceof TileEntityPiston ? (TileEntityPiston) tileentity : null;
	}

	public Item getItem(World worldIn, BlockPos pos) {
		return null;
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, BlockPistonExtension.getFacing(meta)).withProperty(TYPE, (meta & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i | ((EnumFacing) state.getValue(FACING)).getIndex();

		if (state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
			i |= 8;
		}

		return i;
	}

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { FACING, TYPE });
	}
}
