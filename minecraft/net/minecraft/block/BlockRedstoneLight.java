package net.minecraft.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRedstoneLight extends Block {
	private final boolean isOn;

	public BlockRedstoneLight(boolean isOn) {
		super(Material.redstoneLight);
		this.isOn = isOn;

		if (isOn) {
			this.setLightLevel(1.0F);
		}
	}

	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			if (this.isOn && !worldIn.isBlockPowered(pos)) {
				worldIn.setBlockState(pos, Blocks.redstone_lamp.getDefaultState(), 2);
			} else if (!this.isOn && worldIn.isBlockPowered(pos)) {
				worldIn.setBlockState(pos, Blocks.lit_redstone_lamp.getDefaultState(), 2);
			}
		}
	}

	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!worldIn.isRemote) {
			if (this.isOn && !worldIn.isBlockPowered(pos)) {
				worldIn.scheduleUpdate(pos, this, 4);
			} else if (!this.isOn && worldIn.isBlockPowered(pos)) {
				worldIn.setBlockState(pos, Blocks.lit_redstone_lamp.getDefaultState(), 2);
			}
		}
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			if (this.isOn && !worldIn.isBlockPowered(pos)) {
				worldIn.setBlockState(pos, Blocks.redstone_lamp.getDefaultState(), 2);
			}
		}
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Blocks.redstone_lamp);
	}

	public Item getItem(World worldIn, BlockPos pos) {
		return Item.getItemFromBlock(Blocks.redstone_lamp);
	}

	protected ItemStack createStackedBlock(IBlockState state) {
		return new ItemStack(Blocks.redstone_lamp);
	}
}
