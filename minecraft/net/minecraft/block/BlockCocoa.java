package net.minecraft.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCocoa extends BlockDirectional implements IGrowable {
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 2);

	public BlockCocoa() {
		super(Material.plants);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(AGE, Integer.valueOf(0)));
		this.setTickRandomly(true);
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlock(worldIn, pos, state);
		} else if (worldIn.rand.nextInt(5) == 0) {
			int i = ((Integer) state.getValue(AGE)).intValue();

			if (i < 2) {
				worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(i + 1)), 2);
			}
		}
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		pos = pos.offset((EnumFacing) state.getValue(FACING));
		IBlockState iblockstate = worldIn.getBlockState(pos);
		return iblockstate.getBlock() == Blocks.log && iblockstate.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE;
	}

	public boolean isFullCube() {
		return false;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for
	 * render
	 */
	public boolean isOpaqueCube() {
		return false;
	}

	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		this.setBlockBoundsBasedOnState(worldIn, pos);
		return super.getCollisionBoundingBox(worldIn, pos, state);
	}

	public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
		this.setBlockBoundsBasedOnState(worldIn, pos);
		return super.getSelectedBoundingBox(worldIn, pos);
	}

	@SuppressWarnings("incomplete-switch")
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		EnumFacing enumfacing = (EnumFacing) iblockstate.getValue(FACING);
		int i = ((Integer) iblockstate.getValue(AGE)).intValue();
		int j = 4 + i * 2;
		int k = 5 + i * 2;
		float f = (float) j / 2.0F;

		switch (enumfacing) {
		case SOUTH:
			this.setBlockBounds((8.0F - f) / 16.0F, (12.0F - (float) k) / 16.0F, (15.0F - (float) j) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);
			break;

		case NORTH:
			this.setBlockBounds((8.0F - f) / 16.0F, (12.0F - (float) k) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + (float) j) / 16.0F);
			break;

		case WEST:
			this.setBlockBounds(0.0625F, (12.0F - (float) k) / 16.0F, (8.0F - f) / 16.0F, (1.0F + (float) j) / 16.0F, 0.75F, (8.0F + f) / 16.0F);
			break;

		case EAST:
			this.setBlockBounds((15.0F - (float) j) / 16.0F, (12.0F - (float) k) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);
		}
	}

	/**
	 * Called by ItemBlocks after a block is set in the world, to allow post-place
	 * logic
	 */
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing enumfacing = EnumFacing.fromAngle((double) placer.rotationYaw);
		worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to
	 * allow for adjustments to the IBlockstate
	 */
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (!facing.getAxis().isHorizontal()) {
			facing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, facing.getOpposite()).withProperty(AGE, Integer.valueOf(0));
	}

	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlock(worldIn, pos, state);
		}
	}

	private void dropBlock(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.setBlockState(pos, Blocks.air.getDefaultState(), 3);
		this.dropBlockAsItem(worldIn, pos, state, 0);
	}

	/**
	 * Spawns this Block's drops into the World as EntityItems.
	 */
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		int i = ((Integer) state.getValue(AGE)).intValue();
		int j = 1;

		if (i >= 2) {
			j = 3;
		}

		for (int k = 0; k < j; ++k) {
			spawnAsEntity(worldIn, pos, new ItemStack(Items.dye, 1, EnumDyeColor.BROWN.getDyeDamage()));
		}
	}

	public Item getItem(World worldIn, BlockPos pos) {
		return Items.dye;
	}

	public int getDamageValue(World worldIn, BlockPos pos) {
		return EnumDyeColor.BROWN.getDyeDamage();
	}

	/**
	 * Whether this IGrowable can grow
	 */
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return ((Integer) state.getValue(AGE)).intValue() < 2;
	}

	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(((Integer) state.getValue(AGE)).intValue() + 1)), 2);
	}

	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(AGE, Integer.valueOf((meta & 15) >> 2));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i | ((EnumFacing) state.getValue(FACING)).getHorizontalIndex();
		i = i | ((Integer) state.getValue(AGE)).intValue() << 2;
		return i;
	}

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { FACING, AGE });
	}
}
