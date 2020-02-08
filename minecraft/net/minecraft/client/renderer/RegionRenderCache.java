package net.minecraft.client.renderer;

import java.util.Arrays;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class RegionRenderCache extends ChunkCache {
	private static final IBlockState DEFAULT_STATE = Blocks.air.getDefaultState();
	private final BlockPos position;
	private int[] combinedLights;
	private IBlockState[] blockStates;

	public RegionRenderCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
		super(worldIn, posFromIn, posToIn, subIn);
		this.position = posFromIn.subtract(new Vec3i(subIn, subIn, subIn));
		int i = 8000;
		this.combinedLights = new int[8000];
		Arrays.fill((int[]) this.combinedLights, (int) -1);
		this.blockStates = new IBlockState[8000];
	}

	public TileEntity getTileEntity(BlockPos pos) {
		int i = (pos.getX() >> 4) - this.chunkX;
		int j = (pos.getZ() >> 4) - this.chunkZ;
		return this.chunkArray[i][j].getTileEntity(pos, Chunk.EnumCreateEntityType.QUEUED);
	}

	public int getCombinedLight(BlockPos pos, int lightValue) {
		int i = this.getPositionIndex(pos);
		int j = this.combinedLights[i];

		if (j == -1) {
			j = super.getCombinedLight(pos, lightValue);
			this.combinedLights[i] = j;
		}

		return j;
	}

	public IBlockState getBlockState(BlockPos pos) {
		int i = this.getPositionIndex(pos);
		IBlockState iblockstate = this.blockStates[i];

		if (iblockstate == null) {
			iblockstate = this.getBlockStateRaw(pos);
			this.blockStates[i] = iblockstate;
		}

		return iblockstate;
	}

	private IBlockState getBlockStateRaw(BlockPos pos) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			int i = (pos.getX() >> 4) - this.chunkX;
			int j = (pos.getZ() >> 4) - this.chunkZ;
			return this.chunkArray[i][j].getBlockState(pos);
		} else {
			return DEFAULT_STATE;
		}
	}

	private int getPositionIndex(BlockPos p_175630_1_) {
		int i = p_175630_1_.getX() - this.position.getX();
		int j = p_175630_1_.getY() - this.position.getY();
		int k = p_175630_1_.getZ() - this.position.getZ();
		return i * 400 + k * 20 + j;
	}
}
