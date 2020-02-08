package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
	private static final IBehaviorDispenseItem dispenserMinecartBehavior = new BehaviorDefaultDispenseItem() {
		private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();

		public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
			World world = source.getWorld();
			double d0 = source.getX() + (double) enumfacing.getFrontOffsetX() * 1.125D;
			double d1 = Math.floor(source.getY()) + (double) enumfacing.getFrontOffsetY();
			double d2 = source.getZ() + (double) enumfacing.getFrontOffsetZ() * 1.125D;
			BlockPos blockpos = source.getBlockPos().offset(enumfacing);
			IBlockState iblockstate = world.getBlockState(blockpos);
			BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getBlock() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection) iblockstate.getValue(((BlockRailBase) iblockstate.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
			double d3;

			if (BlockRailBase.isRailBlock(iblockstate)) {
				if (blockrailbase$enumraildirection.isAscending()) {
					d3 = 0.6D;
				} else {
					d3 = 0.1D;
				}
			} else {
				if (iblockstate.getBlock().getMaterial() != Material.air || !BlockRailBase.isRailBlock(world.getBlockState(blockpos.down()))) {
					return this.behaviourDefaultDispenseItem.dispense(source, stack);
				}

				IBlockState iblockstate1 = world.getBlockState(blockpos.down());
				BlockRailBase.EnumRailDirection blockrailbase$enumraildirection1 = iblockstate1.getBlock() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection) iblockstate1.getValue(((BlockRailBase) iblockstate1.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;

				if (enumfacing != EnumFacing.DOWN && blockrailbase$enumraildirection1.isAscending()) {
					d3 = -0.4D;
				} else {
					d3 = -0.9D;
				}
			}

			EntityMinecart entityminecart = EntityMinecart.func_180458_a(world, d0, d1 + d3, d2, ((ItemMinecart) stack.getItem()).minecartType);

			if (stack.hasDisplayName()) {
				entityminecart.setCustomNameTag(stack.getDisplayName());
			}

			world.spawnEntityInWorld(entityminecart);
			stack.splitStack(1);
			return stack;
		}

		protected void playDispenseSound(IBlockSource source) {
			source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
		}
	};
	private final EntityMinecart.EnumMinecartType minecartType;

	public ItemMinecart(EntityMinecart.EnumMinecartType type) {
		this.maxStackSize = 1;
		this.minecartType = type;
		this.setCreativeTab(CreativeTabs.tabTransport);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserMinecartBehavior);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		IBlockState iblockstate = worldIn.getBlockState(pos);

		if (BlockRailBase.isRailBlock(iblockstate)) {
			if (!worldIn.isRemote) {
				BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getBlock() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection) iblockstate.getValue(((BlockRailBase) iblockstate.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
				double d0 = 0.0D;

				if (blockrailbase$enumraildirection.isAscending()) {
					d0 = 0.5D;
				}

				EntityMinecart entityminecart = EntityMinecart.func_180458_a(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.0625D + d0, (double) pos.getZ() + 0.5D, this.minecartType);

				if (stack.hasDisplayName()) {
					entityminecart.setCustomNameTag(stack.getDisplayName());
				}

				worldIn.spawnEntityInWorld(entityminecart);
			}

			--stack.stackSize;
			return true;
		} else {
			return false;
		}
	}
}
