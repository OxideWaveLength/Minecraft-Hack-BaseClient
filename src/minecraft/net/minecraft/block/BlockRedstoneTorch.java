package net.minecraft.block;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch {
	private static Map<World, List<BlockRedstoneTorch.Toggle>> toggles = Maps.<World, List<BlockRedstoneTorch.Toggle>>newHashMap();
	private final boolean isOn;

	private boolean isBurnedOut(World worldIn, BlockPos pos, boolean turnOff) {
		if (!toggles.containsKey(worldIn)) {
			toggles.put(worldIn, Lists.<BlockRedstoneTorch.Toggle>newArrayList());
		}

		List<BlockRedstoneTorch.Toggle> list = (List) toggles.get(worldIn);

		if (turnOff) {
			list.add(new BlockRedstoneTorch.Toggle(pos, worldIn.getTotalWorldTime()));
		}

		int i = 0;

		for (int j = 0; j < list.size(); ++j) {
			BlockRedstoneTorch.Toggle blockredstonetorch$toggle = (BlockRedstoneTorch.Toggle) list.get(j);

			if (blockredstonetorch$toggle.pos.equals(pos)) {
				++i;

				if (i >= 8) {
					return true;
				}
			}
		}

		return false;
	}

	protected BlockRedstoneTorch(boolean isOn) {
		this.isOn = isOn;
		this.setTickRandomly(true);
		this.setCreativeTab((CreativeTabs) null);
	}

	/**
	 * How many world ticks before ticking
	 */
	public int tickRate(World worldIn) {
		return 2;
	}

	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (this.isOn) {
			for (EnumFacing enumfacing : EnumFacing.values()) {
				worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
			}
		}
	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (this.isOn) {
			for (EnumFacing enumfacing : EnumFacing.values()) {
				worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
			}
		}
	}

	public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return this.isOn && state.getValue(FACING) != side ? 15 : 0;
	}

	private boolean shouldBeOff(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing enumfacing = ((EnumFacing) state.getValue(FACING)).getOpposite();
		return worldIn.isSidePowered(pos.offset(enumfacing), enumfacing);
	}

	/**
	 * Called randomly when setTickRandomly is set to true (used by e.g. crops to
	 * grow, etc.)
	 */
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		boolean flag = this.shouldBeOff(worldIn, pos, state);
		List<BlockRedstoneTorch.Toggle> list = (List) toggles.get(worldIn);

		while (list != null && !list.isEmpty() && worldIn.getTotalWorldTime() - ((BlockRedstoneTorch.Toggle) list.get(0)).time > 60L) {
			list.remove(0);
		}

		if (this.isOn) {
			if (flag) {
				worldIn.setBlockState(pos, Blocks.unlit_redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);

				if (this.isBurnedOut(worldIn, pos, true)) {
					worldIn.playSoundEffect((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

					for (int i = 0; i < 5; ++i) {
						double d0 = (double) pos.getX() + rand.nextDouble() * 0.6D + 0.2D;
						double d1 = (double) pos.getY() + rand.nextDouble() * 0.6D + 0.2D;
						double d2 = (double) pos.getZ() + rand.nextDouble() * 0.6D + 0.2D;
						worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
					}

					worldIn.scheduleUpdate(pos, worldIn.getBlockState(pos).getBlock(), 160);
				}
			}
		} else if (!flag && !this.isBurnedOut(worldIn, pos, false)) {
			worldIn.setBlockState(pos, Blocks.redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
		}
	}

	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!this.onNeighborChangeInternal(worldIn, pos, state)) {
			if (this.isOn == this.shouldBeOff(worldIn, pos, state)) {
				worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
			}
		}
	}

	public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return side == EnumFacing.DOWN ? this.getWeakPower(worldIn, pos, state, side) : 0;
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Blocks.redstone_torch);
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change
	 * based on its state.
	 */
	public boolean canProvidePower() {
		return true;
	}

	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (this.isOn) {
			double d0 = (double) pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d1 = (double) pos.getY() + 0.7D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d2 = (double) pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

			if (enumfacing.getAxis().isHorizontal()) {
				EnumFacing enumfacing1 = enumfacing.getOpposite();
				double d3 = 0.27D;
				d0 += 0.27D * (double) enumfacing1.getFrontOffsetX();
				d1 += 0.22D;
				d2 += 0.27D * (double) enumfacing1.getFrontOffsetZ();
			}

			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}

	public Item getItem(World worldIn, BlockPos pos) {
		return Item.getItemFromBlock(Blocks.redstone_torch);
	}

	public boolean isAssociatedBlock(Block other) {
		return other == Blocks.unlit_redstone_torch || other == Blocks.redstone_torch;
	}

	static class Toggle {
		BlockPos pos;
		long time;

		public Toggle(BlockPos pos, long time) {
			this.pos = pos;
			this.time = time;
		}
	}
}
