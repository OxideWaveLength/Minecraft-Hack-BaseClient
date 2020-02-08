package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderDebug;

public class Chunk {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Used to store block IDs, block MSBs, Sky-light maps, Block-light maps, and
	 * metadata. Each entry corresponds to a logical segment of 16x16x16 blocks,
	 * stacked vertically.
	 */
	private final ExtendedBlockStorage[] storageArrays;

	/**
	 * Contains a 16x16 mapping on the X/Z plane of the biome ID to which each colum
	 * belongs.
	 */
	private final byte[] blockBiomeArray;

	/**
	 * A map, similar to heightMap, that tracks how far down precipitation can fall.
	 */
	private final int[] precipitationHeightMap;

	/** Which columns need their skylightMaps updated. */
	private final boolean[] updateSkylightColumns;

	/** Whether or not this Chunk is currently loaded into the World */
	private boolean isChunkLoaded;

	/** Reference to the World object. */
	private final World worldObj;
	private final int[] heightMap;

	/** The x coordinate of the chunk. */
	public final int xPosition;

	/** The z coordinate of the chunk. */
	public final int zPosition;
	private boolean isGapLightingUpdated;
	private final Map<BlockPos, TileEntity> chunkTileEntityMap;
	private final ClassInheritanceMultiMap<Entity>[] entityLists;

	/** Boolean value indicating if the terrain is populated. */
	private boolean isTerrainPopulated;
	private boolean isLightPopulated;
	private boolean field_150815_m;

	/**
	 * Set to true if the chunk has been modified and needs to be updated
	 * internally.
	 */
	private boolean isModified;

	/**
	 * Whether this Chunk has any Entities and thus requires saving on every tick
	 */
	private boolean hasEntities;

	/** The time according to World.worldTime when this chunk was last saved */
	private long lastSaveTime;

	/** Lowest value in the heightmap. */
	private int heightMapMinimum;

	/** the cumulative number of ticks players have been in this chunk */
	private long inhabitedTime;

	/**
	 * Contains the current round-robin relight check index, and is implied as the
	 * relight check location as well.
	 */
	private int queuedLightChecks;
	private ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue;

	public Chunk(World worldIn, int x, int z) {
		this.storageArrays = new ExtendedBlockStorage[16];
		this.blockBiomeArray = new byte[256];
		this.precipitationHeightMap = new int[256];
		this.updateSkylightColumns = new boolean[256];
		this.chunkTileEntityMap = Maps.<BlockPos, TileEntity>newHashMap();
		this.queuedLightChecks = 4096;
		this.tileEntityPosQueue = Queues.<BlockPos>newConcurrentLinkedQueue();
		this.entityLists = (ClassInheritanceMultiMap[]) (new ClassInheritanceMultiMap[16]);
		this.worldObj = worldIn;
		this.xPosition = x;
		this.zPosition = z;
		this.heightMap = new int[256];

		for (int i = 0; i < this.entityLists.length; ++i) {
			this.entityLists[i] = new ClassInheritanceMultiMap(Entity.class);
		}

		Arrays.fill((int[]) this.precipitationHeightMap, (int) -999);
		Arrays.fill(this.blockBiomeArray, (byte) -1);
	}

	public Chunk(World worldIn, ChunkPrimer primer, int x, int z) {
		this(worldIn, x, z);
		int i = 256;
		boolean flag = !worldIn.provider.getHasNoSky();

		for (int j = 0; j < 16; ++j) {
			for (int k = 0; k < 16; ++k) {
				for (int l = 0; l < i; ++l) {
					int i1 = j * i * 16 | k * i | l;
					IBlockState iblockstate = primer.getBlockState(i1);

					if (iblockstate.getBlock().getMaterial() != Material.air) {
						int j1 = l >> 4;

						if (this.storageArrays[j1] == null) {
							this.storageArrays[j1] = new ExtendedBlockStorage(j1 << 4, flag);
						}

						this.storageArrays[j1].set(j, l & 15, k, iblockstate);
					}
				}
			}
		}
	}

	/**
	 * Checks whether the chunk is at the X/Z location specified
	 */
	public boolean isAtLocation(int x, int z) {
		return x == this.xPosition && z == this.zPosition;
	}

	public int getHeight(BlockPos pos) {
		return this.getHeightValue(pos.getX() & 15, pos.getZ() & 15);
	}

	/**
	 * Returns the value in the height map at this x, z coordinate in the chunk
	 */
	public int getHeightValue(int x, int z) {
		return this.heightMap[z << 4 | x];
	}

	/**
	 * Returns the topmost ExtendedBlockStorage instance for this Chunk that
	 * actually contains a block.
	 */
	public int getTopFilledSegment() {
		for (int i = this.storageArrays.length - 1; i >= 0; --i) {
			if (this.storageArrays[i] != null) {
				return this.storageArrays[i].getYLocation();
			}
		}

		return 0;
	}

	/**
	 * Returns the ExtendedBlockStorage array for this Chunk.
	 */
	public ExtendedBlockStorage[] getBlockStorageArray() {
		return this.storageArrays;
	}

	/**
	 * Generates the height map for a chunk from scratch
	 */
	protected void generateHeightMap() {
		int i = this.getTopFilledSegment();
		this.heightMapMinimum = Integer.MAX_VALUE;

		for (int j = 0; j < 16; ++j) {
			for (int k = 0; k < 16; ++k) {
				this.precipitationHeightMap[j + (k << 4)] = -999;

				for (int l = i + 16; l > 0; --l) {
					Block block = this.getBlock0(j, l - 1, k);

					if (block.getLightOpacity() != 0) {
						this.heightMap[k << 4 | j] = l;

						if (l < this.heightMapMinimum) {
							this.heightMapMinimum = l;
						}

						break;
					}
				}
			}
		}

		this.isModified = true;
	}

	/**
	 * Generates the initial skylight map for the chunk upon generation or load.
	 */
	public void generateSkylightMap() {
		int i = this.getTopFilledSegment();
		this.heightMapMinimum = Integer.MAX_VALUE;

		for (int j = 0; j < 16; ++j) {
			for (int k = 0; k < 16; ++k) {
				this.precipitationHeightMap[j + (k << 4)] = -999;

				for (int l = i + 16; l > 0; --l) {
					if (this.getBlockLightOpacity(j, l - 1, k) != 0) {
						this.heightMap[k << 4 | j] = l;

						if (l < this.heightMapMinimum) {
							this.heightMapMinimum = l;
						}

						break;
					}
				}

				if (!this.worldObj.provider.getHasNoSky()) {
					int k1 = 15;
					int i1 = i + 16 - 1;

					while (true) {
						int j1 = this.getBlockLightOpacity(j, i1, k);

						if (j1 == 0 && k1 != 15) {
							j1 = 1;
						}

						k1 -= j1;

						if (k1 > 0) {
							ExtendedBlockStorage extendedblockstorage = this.storageArrays[i1 >> 4];

							if (extendedblockstorage != null) {
								extendedblockstorage.setExtSkylightValue(j, i1 & 15, k, k1);
								this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + j, i1, (this.zPosition << 4) + k));
							}
						}

						--i1;

						if (i1 <= 0 || k1 <= 0) {
							break;
						}
					}
				}
			}
		}

		this.isModified = true;
	}

	/**
	 * Propagates a given sky-visible block's light value downward and upward to
	 * neighboring blocks as necessary.
	 */
	private void propagateSkylightOcclusion(int x, int z) {
		this.updateSkylightColumns[x + z * 16] = true;
		this.isGapLightingUpdated = true;
	}

	private void recheckGaps(boolean p_150803_1_) {
		this.worldObj.theProfiler.startSection("recheckGaps");

		if (this.worldObj.isAreaLoaded(new BlockPos(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8), 16)) {
			for (int i = 0; i < 16; ++i) {
				for (int j = 0; j < 16; ++j) {
					if (this.updateSkylightColumns[i + j * 16]) {
						this.updateSkylightColumns[i + j * 16] = false;
						int k = this.getHeightValue(i, j);
						int l = this.xPosition * 16 + i;
						int i1 = this.zPosition * 16 + j;
						int j1 = Integer.MAX_VALUE;

						for (Object enumfacing0 : EnumFacing.Plane.HORIZONTAL) {
							EnumFacing enumfacing = (EnumFacing) enumfacing0;
							j1 = Math.min(j1, this.worldObj.getChunksLowestHorizon(l + enumfacing.getFrontOffsetX(), i1 + enumfacing.getFrontOffsetZ()));
						}

						this.checkSkylightNeighborHeight(l, i1, j1);

						for (Object enumfacing10 : EnumFacing.Plane.HORIZONTAL) {
							EnumFacing enumfacing1 = (EnumFacing) enumfacing10;
							this.checkSkylightNeighborHeight(l + enumfacing1.getFrontOffsetX(), i1 + enumfacing1.getFrontOffsetZ(), k);
						}

						if (p_150803_1_) {
							this.worldObj.theProfiler.endSection();
							return;
						}
					}
				}
			}

			this.isGapLightingUpdated = false;
		}

		this.worldObj.theProfiler.endSection();
	}

	/**
	 * Checks the height of a block next to a sky-visible block and schedules a
	 * lighting update as necessary.
	 */
	private void checkSkylightNeighborHeight(int x, int z, int maxValue) {
		int i = this.worldObj.getHeight(new BlockPos(x, 0, z)).getY();

		if (i > maxValue) {
			this.updateSkylightNeighborHeight(x, z, maxValue, i + 1);
		} else if (i < maxValue) {
			this.updateSkylightNeighborHeight(x, z, i, maxValue + 1);
		}
	}

	private void updateSkylightNeighborHeight(int x, int z, int startY, int endY) {
		if (endY > startY && this.worldObj.isAreaLoaded(new BlockPos(x, 0, z), 16)) {
			for (int i = startY; i < endY; ++i) {
				this.worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, i, z));
			}

			this.isModified = true;
		}
	}

	/**
	 * Initiates the recalculation of both the block-light and sky-light for a given
	 * block inside a chunk.
	 */
	private void relightBlock(int x, int y, int z) {
		int i = this.heightMap[z << 4 | x] & 255;
		int j = i;

		if (y > i) {
			j = y;
		}

		while (j > 0 && this.getBlockLightOpacity(x, j - 1, z) == 0) {
			--j;
		}

		if (j != i) {
			this.worldObj.markBlocksDirtyVertical(x + this.xPosition * 16, z + this.zPosition * 16, j, i);
			this.heightMap[z << 4 | x] = j;
			int k = this.xPosition * 16 + x;
			int l = this.zPosition * 16 + z;

			if (!this.worldObj.provider.getHasNoSky()) {
				if (j < i) {
					for (int j1 = j; j1 < i; ++j1) {
						ExtendedBlockStorage extendedblockstorage2 = this.storageArrays[j1 >> 4];

						if (extendedblockstorage2 != null) {
							extendedblockstorage2.setExtSkylightValue(x, j1 & 15, z, 15);
							this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, j1, (this.zPosition << 4) + z));
						}
					}
				} else {
					for (int i1 = i; i1 < j; ++i1) {
						ExtendedBlockStorage extendedblockstorage = this.storageArrays[i1 >> 4];

						if (extendedblockstorage != null) {
							extendedblockstorage.setExtSkylightValue(x, i1 & 15, z, 0);
							this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, i1, (this.zPosition << 4) + z));
						}
					}
				}

				int k1 = 15;

				while (j > 0 && k1 > 0) {
					--j;
					int i2 = this.getBlockLightOpacity(x, j, z);

					if (i2 == 0) {
						i2 = 1;
					}

					k1 -= i2;

					if (k1 < 0) {
						k1 = 0;
					}

					ExtendedBlockStorage extendedblockstorage1 = this.storageArrays[j >> 4];

					if (extendedblockstorage1 != null) {
						extendedblockstorage1.setExtSkylightValue(x, j & 15, z, k1);
					}
				}
			}

			int l1 = this.heightMap[z << 4 | x];
			int j2 = i;
			int k2 = l1;

			if (l1 < i) {
				j2 = l1;
				k2 = i;
			}

			if (l1 < this.heightMapMinimum) {
				this.heightMapMinimum = l1;
			}

			if (!this.worldObj.provider.getHasNoSky()) {
				for (Object enumfacing0 : EnumFacing.Plane.HORIZONTAL) {
					EnumFacing enumfacing = (EnumFacing) enumfacing0;
					this.updateSkylightNeighborHeight(k + enumfacing.getFrontOffsetX(), l + enumfacing.getFrontOffsetZ(), j2, k2);
				}

				this.updateSkylightNeighborHeight(k, l, j2, k2);
			}

			this.isModified = true;
		}
	}

	public int getBlockLightOpacity(BlockPos pos) {
		return this.getBlock(pos).getLightOpacity();
	}

	private int getBlockLightOpacity(int x, int y, int z) {
		return this.getBlock0(x, y, z).getLightOpacity();
	}

	/**
	 * Returns the block corresponding to the given coordinates inside a chunk.
	 */
	private Block getBlock0(int x, int y, int z) {
		Block block = Blocks.air;

		if (y >= 0 && y >> 4 < this.storageArrays.length) {
			ExtendedBlockStorage extendedblockstorage = this.storageArrays[y >> 4];

			if (extendedblockstorage != null) {
				try {
					block = extendedblockstorage.getBlockByExtId(x, y & 15, z);
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block");
					throw new ReportedException(crashreport);
				}
			}
		}

		return block;
	}

	public Block getBlock(final int x, final int y, final int z) {
		try {
			return this.getBlock0(x & 15, y, z & 15);
		} catch (ReportedException reportedexception) {
			CrashReportCategory crashreportcategory = reportedexception.getCrashReport().makeCategory("Block being got");
			crashreportcategory.addCrashSectionCallable("Location", new Callable<String>() {
				public String call() throws Exception {
					return CrashReportCategory.getCoordinateInfo(new BlockPos(Chunk.this.xPosition * 16 + x, y, Chunk.this.zPosition * 16 + z));
				}
			});
			throw reportedexception;
		}
	}

	public Block getBlock(final BlockPos pos) {
		try {
			return this.getBlock0(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
		} catch (ReportedException reportedexception) {
			CrashReportCategory crashreportcategory = reportedexception.getCrashReport().makeCategory("Block being got");
			crashreportcategory.addCrashSectionCallable("Location", new Callable<String>() {
				public String call() throws Exception {
					return CrashReportCategory.getCoordinateInfo(pos);
				}
			});
			throw reportedexception;
		}
	}

	public IBlockState getBlockState(final BlockPos pos) {
		if (this.worldObj.getWorldType() == WorldType.DEBUG_WORLD) {
			IBlockState iblockstate = null;

			if (pos.getY() == 60) {
				iblockstate = Blocks.barrier.getDefaultState();
			}

			if (pos.getY() == 70) {
				iblockstate = ChunkProviderDebug.func_177461_b(pos.getX(), pos.getZ());
			}

			return iblockstate == null ? Blocks.air.getDefaultState() : iblockstate;
		} else {
			try {
				if (pos.getY() >= 0 && pos.getY() >> 4 < this.storageArrays.length) {
					ExtendedBlockStorage extendedblockstorage = this.storageArrays[pos.getY() >> 4];

					if (extendedblockstorage != null) {
						int j = pos.getX() & 15;
						int k = pos.getY() & 15;
						int i = pos.getZ() & 15;
						return extendedblockstorage.get(j, k, i);
					}
				}

				return Blocks.air.getDefaultState();
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block state");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
				crashreportcategory.addCrashSectionCallable("Location", new Callable<String>() {
					public String call() throws Exception {
						return CrashReportCategory.getCoordinateInfo(pos);
					}
				});
				throw new ReportedException(crashreport);
			}
		}
	}

	/**
	 * Return the metadata corresponding to the given coordinates inside a chunk.
	 */
	private int getBlockMetadata(int x, int y, int z) {
		if (y >> 4 >= this.storageArrays.length) {
			return 0;
		} else {
			ExtendedBlockStorage extendedblockstorage = this.storageArrays[y >> 4];
			return extendedblockstorage != null ? extendedblockstorage.getExtBlockMetadata(x, y & 15, z) : 0;
		}
	}

	public int getBlockMetadata(BlockPos pos) {
		return this.getBlockMetadata(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
	}

	public IBlockState setBlockState(BlockPos pos, IBlockState state) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		int l = k << 4 | i;

		if (j >= this.precipitationHeightMap[l] - 1) {
			this.precipitationHeightMap[l] = -999;
		}

		int i1 = this.heightMap[l];
		IBlockState iblockstate = this.getBlockState(pos);

		if (iblockstate == state) {
			return null;
		} else {
			Block block = state.getBlock();
			Block block1 = iblockstate.getBlock();
			ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
			boolean flag = false;

			if (extendedblockstorage == null) {
				if (block == Blocks.air) {
					return null;
				}

				extendedblockstorage = this.storageArrays[j >> 4] = new ExtendedBlockStorage(j >> 4 << 4, !this.worldObj.provider.getHasNoSky());
				flag = j >= i1;
			}

			extendedblockstorage.set(i, j & 15, k, state);

			if (block1 != block) {
				if (!this.worldObj.isRemote) {
					block1.breakBlock(this.worldObj, pos, iblockstate);
				} else if (block1 instanceof ITileEntityProvider) {
					this.worldObj.removeTileEntity(pos);
				}
			}

			if (extendedblockstorage.getBlockByExtId(i, j & 15, k) != block) {
				return null;
			} else {
				if (flag) {
					this.generateSkylightMap();
				} else {
					int j1 = block.getLightOpacity();
					int k1 = block1.getLightOpacity();

					if (j1 > 0) {
						if (j >= i1) {
							this.relightBlock(i, j + 1, k);
						}
					} else if (j == i1 - 1) {
						this.relightBlock(i, j, k);
					}

					if (j1 != k1 && (j1 < k1 || this.getLightFor(EnumSkyBlock.SKY, pos) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, pos) > 0)) {
						this.propagateSkylightOcclusion(i, k);
					}
				}

				if (block1 instanceof ITileEntityProvider) {
					TileEntity tileentity = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);

					if (tileentity != null) {
						tileentity.updateContainingBlockInfo();
					}
				}

				if (!this.worldObj.isRemote && block1 != block) {
					block.onBlockAdded(this.worldObj, pos, state);
				}

				if (block instanceof ITileEntityProvider) {
					TileEntity tileentity1 = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);

					if (tileentity1 == null) {
						tileentity1 = ((ITileEntityProvider) block).createNewTileEntity(this.worldObj, block.getMetaFromState(state));
						this.worldObj.setTileEntity(pos, tileentity1);
					}

					if (tileentity1 != null) {
						tileentity1.updateContainingBlockInfo();
					}
				}

				this.isModified = true;
				return iblockstate;
			}
		}
	}

	public int getLightFor(EnumSkyBlock p_177413_1_, BlockPos pos) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
		return extendedblockstorage == null ? (this.canSeeSky(pos) ? p_177413_1_.defaultLightValue : 0) : (p_177413_1_ == EnumSkyBlock.SKY ? (this.worldObj.provider.getHasNoSky() ? 0 : extendedblockstorage.getExtSkylightValue(i, j & 15, k)) : (p_177413_1_ == EnumSkyBlock.BLOCK ? extendedblockstorage.getExtBlocklightValue(i, j & 15, k) : p_177413_1_.defaultLightValue));
	}

	public void setLightFor(EnumSkyBlock p_177431_1_, BlockPos pos, int value) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];

		if (extendedblockstorage == null) {
			extendedblockstorage = this.storageArrays[j >> 4] = new ExtendedBlockStorage(j >> 4 << 4, !this.worldObj.provider.getHasNoSky());
			this.generateSkylightMap();
		}

		this.isModified = true;

		if (p_177431_1_ == EnumSkyBlock.SKY) {
			if (!this.worldObj.provider.getHasNoSky()) {
				extendedblockstorage.setExtSkylightValue(i, j & 15, k, value);
			}
		} else if (p_177431_1_ == EnumSkyBlock.BLOCK) {
			extendedblockstorage.setExtBlocklightValue(i, j & 15, k, value);
		}
	}

	public int getLightSubtracted(BlockPos pos, int amount) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];

		if (extendedblockstorage == null) {
			return !this.worldObj.provider.getHasNoSky() && amount < EnumSkyBlock.SKY.defaultLightValue ? EnumSkyBlock.SKY.defaultLightValue - amount : 0;
		} else {
			int l = this.worldObj.provider.getHasNoSky() ? 0 : extendedblockstorage.getExtSkylightValue(i, j & 15, k);
			l = l - amount;
			int i1 = extendedblockstorage.getExtBlocklightValue(i, j & 15, k);

			if (i1 > l) {
				l = i1;
			}

			return l;
		}
	}

	/**
	 * Adds an entity to the chunk. Args: entity
	 */
	public void addEntity(Entity entityIn) {
		this.hasEntities = true;
		int i = MathHelper.floor_double(entityIn.posX / 16.0D);
		int j = MathHelper.floor_double(entityIn.posZ / 16.0D);

		if (i != this.xPosition || j != this.zPosition) {
			logger.warn("Wrong location! (" + i + ", " + j + ") should be (" + this.xPosition + ", " + this.zPosition + "), " + entityIn, new Object[] { entityIn });
			entityIn.setDead();
		}

		int k = MathHelper.floor_double(entityIn.posY / 16.0D);

		if (k < 0) {
			k = 0;
		}

		if (k >= this.entityLists.length) {
			k = this.entityLists.length - 1;
		}

		entityIn.addedToChunk = true;
		entityIn.chunkCoordX = this.xPosition;
		entityIn.chunkCoordY = k;
		entityIn.chunkCoordZ = this.zPosition;
		this.entityLists[k].add(entityIn);
	}

	/**
	 * removes entity using its y chunk coordinate as its index
	 */
	public void removeEntity(Entity entityIn) {
		this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
	}

	/**
	 * Removes entity at the specified index from the entity array.
	 */
	public void removeEntityAtIndex(Entity entityIn, int p_76608_2_) {
		if (p_76608_2_ < 0) {
			p_76608_2_ = 0;
		}

		if (p_76608_2_ >= this.entityLists.length) {
			p_76608_2_ = this.entityLists.length - 1;
		}

		this.entityLists[p_76608_2_].remove(entityIn);
	}

	public boolean canSeeSky(BlockPos pos) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		return j >= this.heightMap[k << 4 | i];
	}

	private TileEntity createNewTileEntity(BlockPos pos) {
		Block block = this.getBlock(pos);
		return !block.hasTileEntity() ? null : ((ITileEntityProvider) block).createNewTileEntity(this.worldObj, this.getBlockMetadata(pos));
	}

	public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType p_177424_2_) {
		TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(pos);

		if (tileentity == null) {
			if (p_177424_2_ == Chunk.EnumCreateEntityType.IMMEDIATE) {
				tileentity = this.createNewTileEntity(pos);
				this.worldObj.setTileEntity(pos, tileentity);
			} else if (p_177424_2_ == Chunk.EnumCreateEntityType.QUEUED) {
				this.tileEntityPosQueue.add(pos);
			}
		} else if (tileentity.isInvalid()) {
			this.chunkTileEntityMap.remove(pos);
			return null;
		}

		return tileentity;
	}

	public void addTileEntity(TileEntity tileEntityIn) {
		this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);

		if (this.isChunkLoaded) {
			this.worldObj.addTileEntity(tileEntityIn);
		}
	}

	public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
		tileEntityIn.setWorldObj(this.worldObj);
		tileEntityIn.setPos(pos);

		if (this.getBlock(pos) instanceof ITileEntityProvider) {
			if (this.chunkTileEntityMap.containsKey(pos)) {
				((TileEntity) this.chunkTileEntityMap.get(pos)).invalidate();
			}

			tileEntityIn.validate();
			this.chunkTileEntityMap.put(pos, tileEntityIn);
		}
	}

	public void removeTileEntity(BlockPos pos) {
		if (this.isChunkLoaded) {
			TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.remove(pos);

			if (tileentity != null) {
				tileentity.invalidate();
			}
		}
	}

	/**
	 * Called when this Chunk is loaded by the ChunkProvider
	 */
	public void onChunkLoad() {
		this.isChunkLoaded = true;
		this.worldObj.addTileEntities(this.chunkTileEntityMap.values());

		for (int i = 0; i < this.entityLists.length; ++i) {
			for (Entity entity : this.entityLists[i]) {
				entity.onChunkLoad();
			}

			this.worldObj.loadEntities(this.entityLists[i]);
		}
	}

	/**
	 * Called when this Chunk is unloaded by the ChunkProvider
	 */
	public void onChunkUnload() {
		this.isChunkLoaded = false;

		for (TileEntity tileentity : this.chunkTileEntityMap.values()) {
			this.worldObj.markTileEntityForRemoval(tileentity);
		}

		for (int i = 0; i < this.entityLists.length; ++i) {
			this.worldObj.unloadEntities(this.entityLists[i]);
		}
	}

	/**
	 * Sets the isModified flag for this Chunk
	 */
	public void setChunkModified() {
		this.isModified = true;
	}

	/**
	 * Fills the given list of all entities that intersect within the given bounding
	 * box that aren't the passed entity.
	 */
	public void getEntitiesWithinAABBForEntity(Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> p_177414_4_) {
		int i = MathHelper.floor_double((aabb.minY - 2.0D) / 16.0D);
		int j = MathHelper.floor_double((aabb.maxY + 2.0D) / 16.0D);
		i = MathHelper.clamp_int(i, 0, this.entityLists.length - 1);
		j = MathHelper.clamp_int(j, 0, this.entityLists.length - 1);

		for (int k = i; k <= j; ++k) {
			if (!this.entityLists[k].isEmpty()) {
				for (Entity entity : this.entityLists[k]) {
					if (entity.getEntityBoundingBox().intersectsWith(aabb) && entity != entityIn) {
						if (p_177414_4_ == null || p_177414_4_.apply(entity)) {
							listToFill.add(entity);
						}

						Entity[] aentity = entity.getParts();

						if (aentity != null) {
							for (int l = 0; l < aentity.length; ++l) {
								entity = aentity[l];

								if (entity != entityIn && entity.getEntityBoundingBox().intersectsWith(aabb) && (p_177414_4_ == null || p_177414_4_.apply(entity))) {
									listToFill.add(entity);
								}
							}
						}
					}
				}
			}
		}
	}

	public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> p_177430_4_) {
		int i = MathHelper.floor_double((aabb.minY - 2.0D) / 16.0D);
		int j = MathHelper.floor_double((aabb.maxY + 2.0D) / 16.0D);
		i = MathHelper.clamp_int(i, 0, this.entityLists.length - 1);
		j = MathHelper.clamp_int(j, 0, this.entityLists.length - 1);

		for (int k = i; k <= j; ++k) {
			for (T t : this.entityLists[k].getByClass(entityClass)) {
				if (t.getEntityBoundingBox().intersectsWith(aabb) && (p_177430_4_ == null || p_177430_4_.apply(t))) {
					listToFill.add(t);
				}
			}
		}
	}

	/**
	 * Returns true if this Chunk needs to be saved
	 */
	public boolean needsSaving(boolean p_76601_1_) {
		if (p_76601_1_) {
			if (this.hasEntities && this.worldObj.getTotalWorldTime() != this.lastSaveTime || this.isModified) {
				return true;
			}
		} else if (this.hasEntities && this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L) {
			return true;
		}

		return this.isModified;
	}

	public Random getRandomWithSeed(long seed) {
		return new Random(this.worldObj.getSeed() + (long) (this.xPosition * this.xPosition * 4987142) + (long) (this.xPosition * 5947611) + (long) (this.zPosition * this.zPosition) * 4392871L + (long) (this.zPosition * 389711) ^ seed);
	}

	public boolean isEmpty() {
		return false;
	}

	public void populateChunk(IChunkProvider p_76624_1_, IChunkProvider p_76624_2_, int p_76624_3_, int p_76624_4_) {
		boolean flag = p_76624_1_.chunkExists(p_76624_3_, p_76624_4_ - 1);
		boolean flag1 = p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_);
		boolean flag2 = p_76624_1_.chunkExists(p_76624_3_, p_76624_4_ + 1);
		boolean flag3 = p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_);
		boolean flag4 = p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_ - 1);
		boolean flag5 = p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_ + 1);
		boolean flag6 = p_76624_1_.chunkExists(p_76624_3_ - 1, p_76624_4_ + 1);
		boolean flag7 = p_76624_1_.chunkExists(p_76624_3_ + 1, p_76624_4_ - 1);

		if (flag1 && flag2 && flag5) {
			if (!this.isTerrainPopulated) {
				p_76624_1_.populate(p_76624_2_, p_76624_3_, p_76624_4_);
			} else {
				p_76624_1_.func_177460_a(p_76624_2_, this, p_76624_3_, p_76624_4_);
			}
		}

		if (flag3 && flag2 && flag6) {
			Chunk chunk = p_76624_1_.provideChunk(p_76624_3_ - 1, p_76624_4_);

			if (!chunk.isTerrainPopulated) {
				p_76624_1_.populate(p_76624_2_, p_76624_3_ - 1, p_76624_4_);
			} else {
				p_76624_1_.func_177460_a(p_76624_2_, chunk, p_76624_3_ - 1, p_76624_4_);
			}
		}

		if (flag && flag1 && flag7) {
			Chunk chunk1 = p_76624_1_.provideChunk(p_76624_3_, p_76624_4_ - 1);

			if (!chunk1.isTerrainPopulated) {
				p_76624_1_.populate(p_76624_2_, p_76624_3_, p_76624_4_ - 1);
			} else {
				p_76624_1_.func_177460_a(p_76624_2_, chunk1, p_76624_3_, p_76624_4_ - 1);
			}
		}

		if (flag4 && flag && flag3) {
			Chunk chunk2 = p_76624_1_.provideChunk(p_76624_3_ - 1, p_76624_4_ - 1);

			if (!chunk2.isTerrainPopulated) {
				p_76624_1_.populate(p_76624_2_, p_76624_3_ - 1, p_76624_4_ - 1);
			} else {
				p_76624_1_.func_177460_a(p_76624_2_, chunk2, p_76624_3_ - 1, p_76624_4_ - 1);
			}
		}
	}

	public BlockPos getPrecipitationHeight(BlockPos pos) {
		int i = pos.getX() & 15;
		int j = pos.getZ() & 15;
		int k = i | j << 4;
		BlockPos blockpos = new BlockPos(pos.getX(), this.precipitationHeightMap[k], pos.getZ());

		if (blockpos.getY() == -999) {
			int l = this.getTopFilledSegment() + 15;
			blockpos = new BlockPos(pos.getX(), l, pos.getZ());
			int i1 = -1;

			while (blockpos.getY() > 0 && i1 == -1) {
				Block block = this.getBlock(blockpos);
				Material material = block.getMaterial();

				if (!material.blocksMovement() && !material.isLiquid()) {
					blockpos = blockpos.down();
				} else {
					i1 = blockpos.getY() + 1;
				}
			}

			this.precipitationHeightMap[k] = i1;
		}

		return new BlockPos(pos.getX(), this.precipitationHeightMap[k], pos.getZ());
	}

	public void func_150804_b(boolean p_150804_1_) {
		if (this.isGapLightingUpdated && !this.worldObj.provider.getHasNoSky() && !p_150804_1_) {
			this.recheckGaps(this.worldObj.isRemote);
		}

		this.field_150815_m = true;

		if (!this.isLightPopulated && this.isTerrainPopulated) {
			this.func_150809_p();
		}

		while (!this.tileEntityPosQueue.isEmpty()) {
			BlockPos blockpos = (BlockPos) this.tileEntityPosQueue.poll();

			if (this.getTileEntity(blockpos, Chunk.EnumCreateEntityType.CHECK) == null && this.getBlock(blockpos).hasTileEntity()) {
				TileEntity tileentity = this.createNewTileEntity(blockpos);
				this.worldObj.setTileEntity(blockpos, tileentity);
				this.worldObj.markBlockRangeForRenderUpdate(blockpos, blockpos);
			}
		}
	}

	public boolean isPopulated() {
		return this.field_150815_m && this.isTerrainPopulated && this.isLightPopulated;
	}

	/**
	 * Gets a ChunkCoordIntPair representing the Chunk's position.
	 */
	public ChunkCoordIntPair getChunkCoordIntPair() {
		return new ChunkCoordIntPair(this.xPosition, this.zPosition);
	}

	/**
	 * Returns whether the ExtendedBlockStorages containing levels (in blocks) from
	 * arg 1 to arg 2 are fully empty (true) or not (false).
	 */
	public boolean getAreLevelsEmpty(int startY, int endY) {
		if (startY < 0) {
			startY = 0;
		}

		if (endY >= 256) {
			endY = 255;
		}

		for (int i = startY; i <= endY; i += 16) {
			ExtendedBlockStorage extendedblockstorage = this.storageArrays[i >> 4];

			if (extendedblockstorage != null && !extendedblockstorage.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public void setStorageArrays(ExtendedBlockStorage[] newStorageArrays) {
		if (this.storageArrays.length != newStorageArrays.length) {
			logger.warn("Could not set level chunk sections, array length is " + newStorageArrays.length + " instead of " + this.storageArrays.length);
		} else {
			for (int i = 0; i < this.storageArrays.length; ++i) {
				this.storageArrays[i] = newStorageArrays[i];
			}
		}
	}

	/**
	 * Initialize this chunk with new binary data.
	 */
	public void fillChunk(byte[] p_177439_1_, int p_177439_2_, boolean p_177439_3_) {
		int i = 0;
		boolean flag = !this.worldObj.provider.getHasNoSky();

		for (int j = 0; j < this.storageArrays.length; ++j) {
			if ((p_177439_2_ & 1 << j) != 0) {
				if (this.storageArrays[j] == null) {
					this.storageArrays[j] = new ExtendedBlockStorage(j << 4, flag);
				}

				char[] achar = this.storageArrays[j].getData();

				for (int k = 0; k < achar.length; ++k) {
					achar[k] = (char) ((p_177439_1_[i + 1] & 255) << 8 | p_177439_1_[i] & 255);
					i += 2;
				}
			} else if (p_177439_3_ && this.storageArrays[j] != null) {
				this.storageArrays[j] = null;
			}
		}

		for (int l = 0; l < this.storageArrays.length; ++l) {
			if ((p_177439_2_ & 1 << l) != 0 && this.storageArrays[l] != null) {
				NibbleArray nibblearray = this.storageArrays[l].getBlocklightArray();
				System.arraycopy(p_177439_1_, i, nibblearray.getData(), 0, nibblearray.getData().length);
				i += nibblearray.getData().length;
			}
		}

		if (flag) {
			for (int i1 = 0; i1 < this.storageArrays.length; ++i1) {
				if ((p_177439_2_ & 1 << i1) != 0 && this.storageArrays[i1] != null) {
					NibbleArray nibblearray1 = this.storageArrays[i1].getSkylightArray();
					System.arraycopy(p_177439_1_, i, nibblearray1.getData(), 0, nibblearray1.getData().length);
					i += nibblearray1.getData().length;
				}
			}
		}

		if (p_177439_3_) {
			System.arraycopy(p_177439_1_, i, this.blockBiomeArray, 0, this.blockBiomeArray.length);
			int k1 = i + this.blockBiomeArray.length;
		}

		for (int j1 = 0; j1 < this.storageArrays.length; ++j1) {
			if (this.storageArrays[j1] != null && (p_177439_2_ & 1 << j1) != 0) {
				this.storageArrays[j1].removeInvalidBlocks();
			}
		}

		this.isLightPopulated = true;
		this.isTerrainPopulated = true;
		this.generateHeightMap();

		for (TileEntity tileentity : this.chunkTileEntityMap.values()) {
			tileentity.updateContainingBlockInfo();
		}
	}

	public BiomeGenBase getBiome(BlockPos pos, WorldChunkManager chunkManager) {
		int i = pos.getX() & 15;
		int j = pos.getZ() & 15;
		int k = this.blockBiomeArray[j << 4 | i] & 255;

		if (k == 255) {
			BiomeGenBase biomegenbase = chunkManager.getBiomeGenerator(pos, BiomeGenBase.plains);
			k = biomegenbase.biomeID;
			this.blockBiomeArray[j << 4 | i] = (byte) (k & 255);
		}

		BiomeGenBase biomegenbase1 = BiomeGenBase.getBiome(k);
		return biomegenbase1 == null ? BiomeGenBase.plains : biomegenbase1;
	}

	/**
	 * Returns an array containing a 16x16 mapping on the X/Z of block positions in
	 * this Chunk to biome IDs.
	 */
	public byte[] getBiomeArray() {
		return this.blockBiomeArray;
	}

	/**
	 * Accepts a 256-entry array that contains a 16x16 mapping on the X/Z plane of
	 * block positions in this Chunk to biome IDs.
	 */
	public void setBiomeArray(byte[] biomeArray) {
		if (this.blockBiomeArray.length != biomeArray.length) {
			logger.warn("Could not set level chunk biomes, array length is " + biomeArray.length + " instead of " + this.blockBiomeArray.length);
		} else {
			for (int i = 0; i < this.blockBiomeArray.length; ++i) {
				this.blockBiomeArray[i] = biomeArray[i];
			}
		}
	}

	/**
	 * Resets the relight check index to 0 for this Chunk.
	 */
	public void resetRelightChecks() {
		this.queuedLightChecks = 0;
	}

	/**
	 * Called once-per-chunk-per-tick, and advances the round-robin relight check
	 * index by up to 8 blocks at a time. In a worst-case scenario, can potentially
	 * take up to 25.6 seconds, calculated via (4096/8)/20, to re-check all blocks
	 * in a chunk, which may explain lagging light updates on initial world
	 * generation.
	 */
	public void enqueueRelightChecks() {
		BlockPos blockpos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);

		for (int i = 0; i < 8; ++i) {
			if (this.queuedLightChecks >= 4096) {
				return;
			}

			int j = this.queuedLightChecks % 16;
			int k = this.queuedLightChecks / 16 % 16;
			int l = this.queuedLightChecks / 256;
			++this.queuedLightChecks;

			for (int i1 = 0; i1 < 16; ++i1) {
				BlockPos blockpos1 = blockpos.add(k, (j << 4) + i1, l);
				boolean flag = i1 == 0 || i1 == 15 || k == 0 || k == 15 || l == 0 || l == 15;

				if (this.storageArrays[j] == null && flag || this.storageArrays[j] != null && this.storageArrays[j].getBlockByExtId(k, i1, l).getMaterial() == Material.air) {
					for (EnumFacing enumfacing : EnumFacing.values()) {
						BlockPos blockpos2 = blockpos1.offset(enumfacing);

						if (this.worldObj.getBlockState(blockpos2).getBlock().getLightValue() > 0) {
							this.worldObj.checkLight(blockpos2);
						}
					}

					this.worldObj.checkLight(blockpos1);
				}
			}
		}
	}

	public void func_150809_p() {
		this.isTerrainPopulated = true;
		this.isLightPopulated = true;
		BlockPos blockpos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);

		if (!this.worldObj.provider.getHasNoSky()) {
			if (this.worldObj.isAreaLoaded(blockpos.add(-1, 0, -1), blockpos.add(16, this.worldObj.func_181545_F(), 16))) {
				label92:

				for (int i = 0; i < 16; ++i) {
					for (int j = 0; j < 16; ++j) {
						if (!this.func_150811_f(i, j)) {
							this.isLightPopulated = false;
							break label92;
						}
					}
				}

				if (this.isLightPopulated) {
					for (Object enumfacing0 : EnumFacing.Plane.HORIZONTAL) {
						EnumFacing enumfacing = (EnumFacing) enumfacing0;
						int k = enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 16 : 1;
						this.worldObj.getChunkFromBlockCoords(blockpos.offset(enumfacing, k)).func_180700_a(enumfacing.getOpposite());
					}

					this.func_177441_y();
				}
			} else {
				this.isLightPopulated = false;
			}
		}
	}

	private void func_177441_y() {
		for (int i = 0; i < this.updateSkylightColumns.length; ++i) {
			this.updateSkylightColumns[i] = true;
		}

		this.recheckGaps(false);
	}

	private void func_180700_a(EnumFacing p_180700_1_) {
		if (this.isTerrainPopulated) {
			if (p_180700_1_ == EnumFacing.EAST) {
				for (int i = 0; i < 16; ++i) {
					this.func_150811_f(15, i);
				}
			} else if (p_180700_1_ == EnumFacing.WEST) {
				for (int j = 0; j < 16; ++j) {
					this.func_150811_f(0, j);
				}
			} else if (p_180700_1_ == EnumFacing.SOUTH) {
				for (int k = 0; k < 16; ++k) {
					this.func_150811_f(k, 15);
				}
			} else if (p_180700_1_ == EnumFacing.NORTH) {
				for (int l = 0; l < 16; ++l) {
					this.func_150811_f(l, 0);
				}
			}
		}
	}

	private boolean func_150811_f(int x, int z) {
		int i = this.getTopFilledSegment();
		boolean flag = false;
		boolean flag1 = false;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos((this.xPosition << 4) + x, 0, (this.zPosition << 4) + z);

		for (int j = i + 16 - 1; j > this.worldObj.func_181545_F() || j > 0 && !flag1; --j) {
			blockpos$mutableblockpos.func_181079_c(blockpos$mutableblockpos.getX(), j, blockpos$mutableblockpos.getZ());
			int k = this.getBlockLightOpacity(blockpos$mutableblockpos);

			if (k == 255 && blockpos$mutableblockpos.getY() < this.worldObj.func_181545_F()) {
				flag1 = true;
			}

			if (!flag && k > 0) {
				flag = true;
			} else if (flag && k == 0 && !this.worldObj.checkLight(blockpos$mutableblockpos)) {
				return false;
			}
		}

		for (int l = blockpos$mutableblockpos.getY(); l > 0; --l) {
			blockpos$mutableblockpos.func_181079_c(blockpos$mutableblockpos.getX(), l, blockpos$mutableblockpos.getZ());

			if (this.getBlock(blockpos$mutableblockpos).getLightValue() > 0) {
				this.worldObj.checkLight(blockpos$mutableblockpos);
			}
		}

		return true;
	}

	public boolean isLoaded() {
		return this.isChunkLoaded;
	}

	public void setChunkLoaded(boolean loaded) {
		this.isChunkLoaded = loaded;
	}

	public World getWorld() {
		return this.worldObj;
	}

	public int[] getHeightMap() {
		return this.heightMap;
	}

	public void setHeightMap(int[] newHeightMap) {
		if (this.heightMap.length != newHeightMap.length) {
			logger.warn("Could not set level chunk heightmap, array length is " + newHeightMap.length + " instead of " + this.heightMap.length);
		} else {
			for (int i = 0; i < this.heightMap.length; ++i) {
				this.heightMap[i] = newHeightMap[i];
			}
		}
	}

	public Map<BlockPos, TileEntity> getTileEntityMap() {
		return this.chunkTileEntityMap;
	}

	public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
		return this.entityLists;
	}

	public boolean isTerrainPopulated() {
		return this.isTerrainPopulated;
	}

	public void setTerrainPopulated(boolean terrainPopulated) {
		this.isTerrainPopulated = terrainPopulated;
	}

	public boolean isLightPopulated() {
		return this.isLightPopulated;
	}

	public void setLightPopulated(boolean lightPopulated) {
		this.isLightPopulated = lightPopulated;
	}

	public void setModified(boolean modified) {
		this.isModified = modified;
	}

	public void setHasEntities(boolean hasEntitiesIn) {
		this.hasEntities = hasEntitiesIn;
	}

	public void setLastSaveTime(long saveTime) {
		this.lastSaveTime = saveTime;
	}

	public int getLowestHeight() {
		return this.heightMapMinimum;
	}

	public long getInhabitedTime() {
		return this.inhabitedTime;
	}

	public void setInhabitedTime(long newInhabitedTime) {
		this.inhabitedTime = newInhabitedTime;
	}

	public static enum EnumCreateEntityType {
		IMMEDIATE, QUEUED, CHECK;
	}
}
