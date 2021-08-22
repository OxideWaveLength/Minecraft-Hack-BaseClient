package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemFireball extends Item {
	public ItemFireball() {
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		} else {
			pos = pos.offset(side);

			if (!playerIn.canPlayerEdit(pos, side, stack)) {
				return false;
			} else {
				if (worldIn.getBlockState(pos).getBlock().getMaterial() == Material.air) {
					worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, "item.fireCharge.use", 1.0F, (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2F + 1.0F);
					worldIn.setBlockState(pos, Blocks.fire.getDefaultState());
				}

				if (!playerIn.capabilities.isCreativeMode) {
					--stack.stackSize;
				}

				return true;
			}
		}
	}
}
