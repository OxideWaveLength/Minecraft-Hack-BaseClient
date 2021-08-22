package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemRedstone extends Item {
	public ItemRedstone() {
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
		BlockPos blockpos = flag ? pos : pos.offset(side);

		if (!playerIn.canPlayerEdit(blockpos, side, stack)) {
			return false;
		} else {
			Block block = worldIn.getBlockState(blockpos).getBlock();

			if (!worldIn.canBlockBePlaced(block, blockpos, false, side, (Entity) null, stack)) {
				return false;
			} else if (Blocks.redstone_wire.canPlaceBlockAt(worldIn, blockpos)) {
				--stack.stackSize;
				worldIn.setBlockState(blockpos, Blocks.redstone_wire.getDefaultState());
				return true;
			} else {
				return false;
			}
		}
	}
}
