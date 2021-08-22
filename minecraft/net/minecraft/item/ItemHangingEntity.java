package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
	private final Class<? extends EntityHanging> hangingEntityClass;

	public ItemHangingEntity(Class<? extends EntityHanging> entityClass) {
		this.hangingEntityClass = entityClass;
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (side == EnumFacing.DOWN) {
			return false;
		} else if (side == EnumFacing.UP) {
			return false;
		} else {
			BlockPos blockpos = pos.offset(side);

			if (!playerIn.canPlayerEdit(blockpos, side, stack)) {
				return false;
			} else {
				EntityHanging entityhanging = this.createEntity(worldIn, blockpos, side);

				if (entityhanging != null && entityhanging.onValidSurface()) {
					if (!worldIn.isRemote) {
						worldIn.spawnEntityInWorld(entityhanging);
					}

					--stack.stackSize;
				}

				return true;
			}
		}
	}

	private EntityHanging createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide) {
		return (EntityHanging) (this.hangingEntityClass == EntityPainting.class ? new EntityPainting(worldIn, pos, clickedSide) : (this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(worldIn, pos, clickedSide) : null));
	}
}
