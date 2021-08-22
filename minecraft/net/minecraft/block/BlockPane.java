package net.minecraft.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPane extends Block {
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	private final boolean canDrop;

	protected BlockPane(Material materialIn, boolean canDrop) {
		super(materialIn);
		this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
		this.canDrop = canDrop;
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	/**
	 * Get the actual Block state of this Block at the given position. This applies
	 * properties not visible in the metadata, such as fence connections.
	 */
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(NORTH, Boolean.valueOf(this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()).getBlock()))).withProperty(SOUTH, Boolean.valueOf(this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()).getBlock()))).withProperty(WEST, Boolean.valueOf(this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()).getBlock()))).withProperty(EAST, Boolean.valueOf(this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()).getBlock())));
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return !this.canDrop ? null : super.getItemDropped(state, rand, fortune);
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

	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return worldIn.getBlockState(pos).getBlock() == this ? false : super.shouldSideBeRendered(worldIn, pos, side);
	}

	/**
	 * Add all collision boxes of this Block to the list that intersect with the
	 * given mask.
	 */
	public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		boolean flag = this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()).getBlock());
		boolean flag1 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()).getBlock());
		boolean flag2 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()).getBlock());
		boolean flag3 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()).getBlock());

		if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
			if (flag2) {
				this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
				super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
			} else if (flag3) {
				this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
				super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
			}
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
			super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
		}

		if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
			if (flag) {
				this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
				super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
			} else if (flag1) {
				this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
				super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
			}
		} else {
			this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
			super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
		}
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		float f = 0.4375F;
		float f1 = 0.5625F;
		float f2 = 0.4375F;
		float f3 = 0.5625F;
		boolean flag = this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()).getBlock());
		boolean flag1 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()).getBlock());
		boolean flag2 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()).getBlock());
		boolean flag3 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()).getBlock());

		if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
			if (flag2) {
				f = 0.0F;
			} else if (flag3) {
				f1 = 1.0F;
			}
		} else {
			f = 0.0F;
			f1 = 1.0F;
		}

		if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
			if (flag) {
				f2 = 0.0F;
			} else if (flag1) {
				f3 = 1.0F;
			}
		} else {
			f2 = 0.0F;
			f3 = 1.0F;
		}

		this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
	}

	public final boolean canPaneConnectToBlock(Block blockIn) {
		return blockIn.isFullBlock() || blockIn == this || blockIn == Blocks.glass || blockIn == Blocks.stained_glass || blockIn == Blocks.stained_glass_pane || blockIn instanceof BlockPane;
	}

	protected boolean canSilkHarvest() {
		return true;
	}

	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT_MIPPED;
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { NORTH, EAST, WEST, SOUTH });
	}
}
