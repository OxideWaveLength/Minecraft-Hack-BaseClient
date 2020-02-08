package optfine;

import java.util.BitSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class RenderEnv {
	private IBlockAccess blockAccess;
	private IBlockState blockState;
	private BlockPos blockPos;
	private GameSettings gameSettings;
	private int blockId = -1;
	private int metadata = -1;
	private int breakingAnimation = -1;
	private float[] quadBounds = new float[EnumFacing.VALUES.length * 2];
	private BitSet boundsFlags = new BitSet(3);
	private BlockModelRenderer.AmbientOcclusionFace aoFace = new BlockModelRenderer.AmbientOcclusionFace();
	private BlockPosM colorizerBlockPos = null;
	private boolean[] borderFlags = null;
	private static ThreadLocal threadLocalInstance = new ThreadLocal();

	private RenderEnv(IBlockAccess p_i61_1_, IBlockState p_i61_2_, BlockPos p_i61_3_) {
		this.blockAccess = p_i61_1_;
		this.blockState = p_i61_2_;
		this.blockPos = p_i61_3_;
		this.gameSettings = Config.getGameSettings();
	}

	public static RenderEnv getInstance(IBlockAccess p_getInstance_0_, IBlockState p_getInstance_1_, BlockPos p_getInstance_2_) {
		RenderEnv renderenv = (RenderEnv) threadLocalInstance.get();

		if (renderenv == null) {
			renderenv = new RenderEnv(p_getInstance_0_, p_getInstance_1_, p_getInstance_2_);
			threadLocalInstance.set(renderenv);
			return renderenv;
		} else {
			renderenv.reset(p_getInstance_0_, p_getInstance_1_, p_getInstance_2_);
			return renderenv;
		}
	}

	private void reset(IBlockAccess p_reset_1_, IBlockState p_reset_2_, BlockPos p_reset_3_) {
		this.blockAccess = p_reset_1_;
		this.blockState = p_reset_2_;
		this.blockPos = p_reset_3_;
		this.blockId = -1;
		this.metadata = -1;
		this.breakingAnimation = -1;
		this.boundsFlags.clear();
	}

	public int getBlockId() {
		if (this.blockId < 0) {
			this.blockId = Block.getIdFromBlock(this.blockState.getBlock());
		}

		return this.blockId;
	}

	public int getMetadata() {
		if (this.metadata < 0) {
			this.metadata = this.blockState.getBlock().getMetaFromState(this.blockState);
		}

		return this.metadata;
	}

	public float[] getQuadBounds() {
		return this.quadBounds;
	}

	public BitSet getBoundsFlags() {
		return this.boundsFlags;
	}

	public BlockModelRenderer.AmbientOcclusionFace getAoFace() {
		return this.aoFace;
	}

	public boolean isBreakingAnimation(List p_isBreakingAnimation_1_) {
		if (this.breakingAnimation < 0 && p_isBreakingAnimation_1_.size() > 0) {
			if (p_isBreakingAnimation_1_.get(0) instanceof BreakingFour) {
				this.breakingAnimation = 1;
			} else {
				this.breakingAnimation = 0;
			}
		}

		return this.breakingAnimation == 1;
	}

	public boolean isBreakingAnimation(BakedQuad p_isBreakingAnimation_1_) {
		if (this.breakingAnimation < 0) {
			if (p_isBreakingAnimation_1_ instanceof BreakingFour) {
				this.breakingAnimation = 1;
			} else {
				this.breakingAnimation = 0;
			}
		}

		return this.breakingAnimation == 1;
	}

	public boolean isBreakingAnimation() {
		return this.breakingAnimation == 1;
	}

	public IBlockState getBlockState() {
		return this.blockState;
	}

	public BlockPosM getColorizerBlockPos() {
		if (this.colorizerBlockPos == null) {
			this.colorizerBlockPos = new BlockPosM(0, 0, 0);
		}

		return this.colorizerBlockPos;
	}

	public boolean[] getBorderFlags() {
		if (this.borderFlags == null) {
			this.borderFlags = new boolean[4];
		}

		return this.borderFlags;
	}
}
