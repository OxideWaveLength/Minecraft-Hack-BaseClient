package net.minecraft.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenAbstractTree {
	private static final IBlockState field_181629_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
	private static final IBlockState field_181630_b = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH).withProperty(BlockOldLeaf.CHECK_DECAY, Boolean.valueOf(false));
	private boolean useExtraRandomHeight;

	public WorldGenForest(boolean p_i45449_1_, boolean p_i45449_2_) {
		super(p_i45449_1_);
		this.useExtraRandomHeight = p_i45449_2_;
	}

	public boolean generate(World worldIn, Random rand, BlockPos position) {
		int i = rand.nextInt(3) + 5;

		if (this.useExtraRandomHeight) {
			i += rand.nextInt(7);
		}

		boolean flag = true;

		if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
			for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
				int k = 1;

				if (j == position.getY()) {
					k = 0;
				}

				if (j >= position.getY() + 1 + i - 2) {
					k = 2;
				}

				BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

				for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
					for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
						if (j >= 0 && j < 256) {
							if (!this.func_150523_a(worldIn.getBlockState(blockpos$mutableblockpos.func_181079_c(l, j, i1)).getBlock())) {
								flag = false;
							}
						} else {
							flag = false;
						}
					}
				}
			}

			if (!flag) {
				return false;
			} else {
				Block block1 = worldIn.getBlockState(position.down()).getBlock();

				if ((block1 == Blocks.grass || block1 == Blocks.dirt || block1 == Blocks.farmland) && position.getY() < 256 - i - 1) {
					this.func_175921_a(worldIn, position.down());

					for (int i2 = position.getY() - 3 + i; i2 <= position.getY() + i; ++i2) {
						int k2 = i2 - (position.getY() + i);
						int l2 = 1 - k2 / 2;

						for (int i3 = position.getX() - l2; i3 <= position.getX() + l2; ++i3) {
							int j1 = i3 - position.getX();

							for (int k1 = position.getZ() - l2; k1 <= position.getZ() + l2; ++k1) {
								int l1 = k1 - position.getZ();

								if (Math.abs(j1) != l2 || Math.abs(l1) != l2 || rand.nextInt(2) != 0 && k2 != 0) {
									BlockPos blockpos = new BlockPos(i3, i2, k1);
									Block block = worldIn.getBlockState(blockpos).getBlock();

									if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
										this.setBlockAndNotifyAdequately(worldIn, blockpos, field_181630_b);
									}
								}
							}
						}
					}

					for (int j2 = 0; j2 < i; ++j2) {
						Block block2 = worldIn.getBlockState(position.up(j2)).getBlock();

						if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
							this.setBlockAndNotifyAdequately(worldIn, position.up(j2), field_181629_a);
						}
					}

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
}
