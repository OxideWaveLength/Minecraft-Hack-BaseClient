package net.minecraft.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSnow extends Block {
	public static final PropertyInteger LAYERS = PropertyInteger.create("layers", 1, 8);

	protected BlockSnow() {
		super(Material.snow);
		this.setDefaultState(this.blockState.getBaseState().withProperty(LAYERS, Integer.valueOf(1)));
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		this.setTickRandomly(true);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setBlockBoundsForItemRender();
	}

	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return ((Integer) worldIn.getBlockState(pos).getValue(LAYERS)).intValue() < 5;
	}

	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		int i = ((Integer) state.getValue(LAYERS)).intValue() - 1;
		float f = 0.125F;
		return new AxisAlignedBB((double) pos.getX() + this.minX, (double) pos.getY() + this.minY, (double) pos.getZ() + this.minZ, (double) pos.getX() + this.maxX, (double) ((float) pos.getY() + (float) i * f), (double) pos.getZ() + this.maxZ);
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

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	public void setBlockBoundsForItemRender() {
		this.getBoundsForLayers(0);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		this.getBoundsForLayers(((Integer) iblockstate.getValue(LAYERS)).intValue());
	}

	protected void getBoundsForLayers(int p_150154_1_) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (float) p_150154_1_ / 8.0F, 1.0F);
	}

	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos.down());
		Block block = iblockstate.getBlock();
		return block != Blocks.ice && block != Blocks.packed_ice ? (block.getMaterial() == Material.leaves ? true : (block == this && ((Integer) iblockstate.getValue(LAYERS)).intValue() >= 7 ? true : block.isOpaqueCube() && block.blockMaterial.blocksMovement())) : false;
	}

	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	private boolean checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canPlaceBlockAt(worldIn, pos)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			return false;
		} else {
			return true;
		}
	}

	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
		spawnAsEntity(worldIn, pos, new ItemStack(Items.snowball, ((Integer) state.getValue(LAYERS)).intValue() + 1, 0));
		worldIn.setBlockToAir(pos);
		player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.snowball;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random random) {
		return 0;
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11) {
			this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
			worldIn.setBlockToAir(pos);
		}
	}

	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.UP ? true : super.shouldSideBeRendered(worldIn, pos, side);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(LAYERS, Integer.valueOf((meta & 7) + 1));
	}

	/**
	 * Whether this Block can be replaced directly by other blocks (true for e.g.
	 * tall grass)
	 */
	public boolean isReplaceable(World worldIn, BlockPos pos) {
		return ((Integer) worldIn.getBlockState(pos).getValue(LAYERS)).intValue() == 1;
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return ((Integer) state.getValue(LAYERS)).intValue() - 1;
	}

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { LAYERS });
	}
}
