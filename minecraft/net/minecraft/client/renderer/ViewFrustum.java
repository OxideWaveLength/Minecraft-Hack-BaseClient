package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ViewFrustum {
	protected final RenderGlobal renderGlobal;
	protected final World world;
	protected int countChunksY;
	protected int countChunksX;
	protected int countChunksZ;
	public RenderChunk[] renderChunks;

	public ViewFrustum(World worldIn, int renderDistanceChunks, RenderGlobal p_i46246_3_, IRenderChunkFactory renderChunkFactory) {
		this.renderGlobal = p_i46246_3_;
		this.world = worldIn;
		this.setCountChunksXYZ(renderDistanceChunks);
		this.createRenderChunks(renderChunkFactory);
	}

	protected void createRenderChunks(IRenderChunkFactory renderChunkFactory) {
		int i = this.countChunksX * this.countChunksY * this.countChunksZ;
		this.renderChunks = new RenderChunk[i];
		int j = 0;

		for (int k = 0; k < this.countChunksX; ++k) {
			for (int l = 0; l < this.countChunksY; ++l) {
				for (int i1 = 0; i1 < this.countChunksZ; ++i1) {
					int j1 = (i1 * this.countChunksY + l) * this.countChunksX + k;
					BlockPos blockpos = new BlockPos(k * 16, l * 16, i1 * 16);
					this.renderChunks[j1] = renderChunkFactory.makeRenderChunk(this.world, this.renderGlobal, blockpos, j++);
				}
			}
		}
	}

	public void deleteGlResources() {
		for (RenderChunk renderchunk : this.renderChunks) {
			renderchunk.deleteGlResources();
		}
	}

	protected void setCountChunksXYZ(int renderDistanceChunks) {
		int i = renderDistanceChunks * 2 + 1;
		this.countChunksX = i;
		this.countChunksY = 16;
		this.countChunksZ = i;
	}

	public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
		int i = MathHelper.floor_double(viewEntityX) - 8;
		int j = MathHelper.floor_double(viewEntityZ) - 8;
		int k = this.countChunksX * 16;

		for (int l = 0; l < this.countChunksX; ++l) {
			int i1 = this.func_178157_a(i, k, l);

			for (int j1 = 0; j1 < this.countChunksZ; ++j1) {
				int k1 = this.func_178157_a(j, k, j1);

				for (int l1 = 0; l1 < this.countChunksY; ++l1) {
					int i2 = l1 * 16;
					RenderChunk renderchunk = this.renderChunks[(j1 * this.countChunksY + l1) * this.countChunksX + l];
					BlockPos blockpos = new BlockPos(i1, i2, k1);

					if (!blockpos.equals(renderchunk.getPosition())) {
						renderchunk.setPosition(blockpos);
					}
				}
			}
		}
	}

	private int func_178157_a(int p_178157_1_, int p_178157_2_, int p_178157_3_) {
		int i = p_178157_3_ * 16;
		int j = i - p_178157_1_ + p_178157_2_ / 2;

		if (j < 0) {
			j -= p_178157_2_ - 1;
		}

		return i - j / p_178157_2_ * p_178157_2_;
	}

	public void markBlocksForUpdate(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
		int i = MathHelper.bucketInt(fromX, 16);
		int j = MathHelper.bucketInt(fromY, 16);
		int k = MathHelper.bucketInt(fromZ, 16);
		int l = MathHelper.bucketInt(toX, 16);
		int i1 = MathHelper.bucketInt(toY, 16);
		int j1 = MathHelper.bucketInt(toZ, 16);

		for (int k1 = i; k1 <= l; ++k1) {
			int l1 = k1 % this.countChunksX;

			if (l1 < 0) {
				l1 += this.countChunksX;
			}

			for (int i2 = j; i2 <= i1; ++i2) {
				int j2 = i2 % this.countChunksY;

				if (j2 < 0) {
					j2 += this.countChunksY;
				}

				for (int k2 = k; k2 <= j1; ++k2) {
					int l2 = k2 % this.countChunksZ;

					if (l2 < 0) {
						l2 += this.countChunksZ;
					}

					int i3 = (l2 * this.countChunksY + j2) * this.countChunksX + l1;
					RenderChunk renderchunk = this.renderChunks[i3];
					renderchunk.setNeedsUpdate(true);
				}
			}
		}
	}

	protected RenderChunk getRenderChunk(BlockPos pos) {
		int i = MathHelper.bucketInt(pos.getX(), 16);
		int j = MathHelper.bucketInt(pos.getY(), 16);
		int k = MathHelper.bucketInt(pos.getZ(), 16);

		if (j >= 0 && j < this.countChunksY) {
			i = i % this.countChunksX;

			if (i < 0) {
				i += this.countChunksX;
			}

			k = k % this.countChunksZ;

			if (k < 0) {
				k += this.countChunksZ;
			}

			int l = (k * this.countChunksY + j) * this.countChunksX + i;
			return this.renderChunks[l];
		} else {
			return null;
		}
	}
}
