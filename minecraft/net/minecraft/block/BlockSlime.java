package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockSlime extends BlockBreakable {
	public BlockSlime() {
		super(Material.clay, false, MapColor.grassColor);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.slipperiness = 0.8F;
	}

	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.TRANSLUCENT;
	}

	/**
	 * Block's chance to react to a living entity falling on it.
	 */
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		if (entityIn.isSneaking()) {
			super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
		} else {
			entityIn.fall(fallDistance, 0.0F);
		}
	}

	/**
	 * Called when an Entity lands on this Block. This method *must* update motionY
	 * because the entity will not do that on its own
	 */
	public void onLanded(World worldIn, Entity entityIn) {
		if (entityIn.isSneaking()) {
			super.onLanded(worldIn, entityIn);
		} else if (entityIn.motionY < 0.0D) {
			entityIn.motionY = -entityIn.motionY;
		}
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block)
	 */
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
		if (Math.abs(entityIn.motionY) < 0.1D && !entityIn.isSneaking()) {
			double d0 = 0.4D + Math.abs(entityIn.motionY) * 0.2D;
			entityIn.motionX *= d0;
			entityIn.motionZ *= d0;
		}

		super.onEntityCollidedWithBlock(worldIn, pos, entityIn);
	}
}
