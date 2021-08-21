package net.minecraft.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenHugeTrees extends WorldGenAbstractTree {
	/** The base height of the tree */
	protected final int baseHeight;

	/** Sets the metadata for the wood blocks used */
	protected final IBlockState woodMetadata;

	/** Sets the metadata for the leaves used in huge trees */
	protected final IBlockState leavesMetadata;
	protected int extraRandomHeight;

	public WorldGenHugeTrees(boolean p_i46447_1_, int p_i46447_2_, int p_i46447_3_, IBlockState p_i46447_4_, IBlockState p_i46447_5_) {
		super(p_i46447_1_);
		this.baseHeight = p_i46447_2_;
		this.extraRandomHeight = p_i46447_3_;
		this.woodMetadata = p_i46447_4_;
		this.leavesMetadata = p_i46447_5_;
	}

	protected int func_150533_a(Random p_150533_1_) {
		int i = p_150533_1_.nextInt(3) + this.baseHeight;

		if (this.extraRandomHeight > 1) {
			i += p_150533_1_.nextInt(this.extraRandomHeight);
		}

		return i;
	}

	private boolean func_175926_c(World worldIn, BlockPos p_175926_2_, int p_175926_3_) {
		boolean flag = true;

		if (p_175926_2_.getY() >= 1 && p_175926_2_.getY() + p_175926_3_ + 1 <= 256) {
			for (int i = 0; i <= 1 + p_175926_3_; ++i) {
				int j = 2;

				if (i == 0) {
					j = 1;
				} else if (i >= 1 + p_175926_3_ - 2) {
					j = 2;
				}

				for (int k = -j; k <= j && flag; ++k) {
					for (int l = -j; l <= j && flag; ++l) {
						if (p_175926_2_.getY() + i < 0 || p_175926_2_.getY() + i >= 256 || !this.func_150523_a(worldIn.getBlockState(p_175926_2_.add(k, i, l)).getBlock())) {
							flag = false;
						}
					}
				}
			}

			return flag;
		} else {
			return false;
		}
	}

	private boolean func_175927_a(BlockPos p_175927_1_, World worldIn) {
		BlockPos blockpos = p_175927_1_.down();
		Block block = worldIn.getBlockState(blockpos).getBlock();

		if ((block == Blocks.grass || block == Blocks.dirt) && p_175927_1_.getY() >= 2) {
			this.func_175921_a(worldIn, blockpos);
			this.func_175921_a(worldIn, blockpos.east());
			this.func_175921_a(worldIn, blockpos.south());
			this.func_175921_a(worldIn, blockpos.south().east());
			return true;
		} else {
			return false;
		}
	}

	protected boolean func_175929_a(World worldIn, Random p_175929_2_, BlockPos p_175929_3_, int p_175929_4_) {
		return this.func_175926_c(worldIn, p_175929_3_, p_175929_4_) && this.func_175927_a(p_175929_3_, worldIn);
	}

	protected void func_175925_a(World worldIn, BlockPos p_175925_2_, int p_175925_3_) {
		int i = p_175925_3_ * p_175925_3_;

		for (int j = -p_175925_3_; j <= p_175925_3_ + 1; ++j) {
			for (int k = -p_175925_3_; k <= p_175925_3_ + 1; ++k) {
				int l = j - 1;
				int i1 = k - 1;

				if (j * j + k * k <= i || l * l + i1 * i1 <= i || j * j + i1 * i1 <= i || l * l + k * k <= i) {
					BlockPos blockpos = p_175925_2_.add(j, 0, k);
					Material material = worldIn.getBlockState(blockpos).getBlock().getMaterial();

					if (material == Material.air || material == Material.leaves) {
						this.setBlockAndNotifyAdequately(worldIn, blockpos, this.leavesMetadata);
					}
				}
			}
		}
	}

	protected void func_175928_b(World worldIn, BlockPos p_175928_2_, int p_175928_3_) {
		int i = p_175928_3_ * p_175928_3_;

		for (int j = -p_175928_3_; j <= p_175928_3_; ++j) {
			for (int k = -p_175928_3_; k <= p_175928_3_; ++k) {
				if (j * j + k * k <= i) {
					BlockPos blockpos = p_175928_2_.add(j, 0, k);
					Material material = worldIn.getBlockState(blockpos).getBlock().getMaterial();

					if (material == Material.air || material == Material.leaves) {
						this.setBlockAndNotifyAdequately(worldIn, blockpos, this.leavesMetadata);
					}
				}
			}
		}
	}
}
