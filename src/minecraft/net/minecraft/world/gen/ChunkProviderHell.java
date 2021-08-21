package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class ChunkProviderHell implements IChunkProvider {
	/** Is the world that the nether is getting generated. */
	private final World worldObj;
	private final boolean field_177466_i;
	private final Random hellRNG;

	/**
	 * Holds the noise used to determine whether slowsand can be generated at a
	 * location
	 */
	private double[] slowsandNoise = new double[256];
	private double[] gravelNoise = new double[256];

	/**
	 * Holds the noise used to determine whether something other than netherrack can
	 * be generated at a location
	 */
	private double[] netherrackExclusivityNoise = new double[256];
	private double[] noiseField;

	/** A NoiseGeneratorOctaves used in generating nether terrain */
	private final NoiseGeneratorOctaves netherNoiseGen1;
	private final NoiseGeneratorOctaves netherNoiseGen2;
	private final NoiseGeneratorOctaves netherNoiseGen3;

	/** Determines whether slowsand or gravel can be generated at a location */
	private final NoiseGeneratorOctaves slowsandGravelNoiseGen;

	/**
	 * Determines whether something other than nettherack can be generated at a
	 * location
	 */
	private final NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
	public final NoiseGeneratorOctaves netherNoiseGen6;
	public final NoiseGeneratorOctaves netherNoiseGen7;
	private final WorldGenFire field_177470_t = new WorldGenFire();
	private final WorldGenGlowStone1 field_177469_u = new WorldGenGlowStone1();
	private final WorldGenGlowStone2 field_177468_v = new WorldGenGlowStone2();
	private final WorldGenerator field_177467_w = new WorldGenMinable(Blocks.quartz_ore.getDefaultState(), 14, BlockHelper.forBlock(Blocks.netherrack));
	private final WorldGenHellLava field_177473_x = new WorldGenHellLava(Blocks.flowing_lava, true);
	private final WorldGenHellLava field_177472_y = new WorldGenHellLava(Blocks.flowing_lava, false);
	private final GeneratorBushFeature field_177471_z = new GeneratorBushFeature(Blocks.brown_mushroom);
	private final GeneratorBushFeature field_177465_A = new GeneratorBushFeature(Blocks.red_mushroom);
	private final MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
	private final MapGenBase netherCaveGenerator = new MapGenCavesHell();
	double[] noiseData1;
	double[] noiseData2;
	double[] noiseData3;
	double[] noiseData4;
	double[] noiseData5;

	public ChunkProviderHell(World worldIn, boolean p_i45637_2_, long p_i45637_3_) {
		this.worldObj = worldIn;
		this.field_177466_i = p_i45637_2_;
		this.hellRNG = new Random(p_i45637_3_);
		this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
		this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
		this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
		this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
		this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
		this.netherNoiseGen6 = new NoiseGeneratorOctaves(this.hellRNG, 10);
		this.netherNoiseGen7 = new NoiseGeneratorOctaves(this.hellRNG, 16);
		worldIn.func_181544_b(63);
	}

	public void func_180515_a(int p_180515_1_, int p_180515_2_, ChunkPrimer p_180515_3_) {
		int i = 4;
		int j = this.worldObj.func_181545_F() / 2 + 1;
		int k = i + 1;
		int l = 17;
		int i1 = i + 1;
		this.noiseField = this.initializeNoiseField(this.noiseField, p_180515_1_ * i, 0, p_180515_2_ * i, k, l, i1);

		for (int j1 = 0; j1 < i; ++j1) {
			for (int k1 = 0; k1 < i; ++k1) {
				for (int l1 = 0; l1 < 16; ++l1) {
					double d0 = 0.125D;
					double d1 = this.noiseField[((j1 + 0) * i1 + k1 + 0) * l + l1 + 0];
					double d2 = this.noiseField[((j1 + 0) * i1 + k1 + 1) * l + l1 + 0];
					double d3 = this.noiseField[((j1 + 1) * i1 + k1 + 0) * l + l1 + 0];
					double d4 = this.noiseField[((j1 + 1) * i1 + k1 + 1) * l + l1 + 0];
					double d5 = (this.noiseField[((j1 + 0) * i1 + k1 + 0) * l + l1 + 1] - d1) * d0;
					double d6 = (this.noiseField[((j1 + 0) * i1 + k1 + 1) * l + l1 + 1] - d2) * d0;
					double d7 = (this.noiseField[((j1 + 1) * i1 + k1 + 0) * l + l1 + 1] - d3) * d0;
					double d8 = (this.noiseField[((j1 + 1) * i1 + k1 + 1) * l + l1 + 1] - d4) * d0;

					for (int i2 = 0; i2 < 8; ++i2) {
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int j2 = 0; j2 < 4; ++j2) {
							double d14 = 0.25D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for (int k2 = 0; k2 < 4; ++k2) {
								IBlockState iblockstate = null;

								if (l1 * 8 + i2 < j) {
									iblockstate = Blocks.lava.getDefaultState();
								}

								if (d15 > 0.0D) {
									iblockstate = Blocks.netherrack.getDefaultState();
								}

								int l2 = j2 + j1 * 4;
								int i3 = i2 + l1 * 8;
								int j3 = k2 + k1 * 4;
								p_180515_3_.setBlockState(l2, i3, j3, iblockstate);
								d15 += d16;
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}

	public void func_180516_b(int p_180516_1_, int p_180516_2_, ChunkPrimer p_180516_3_) {
		int i = this.worldObj.func_181545_F() + 1;
		double d0 = 0.03125D;
		this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, p_180516_1_ * 16, p_180516_2_ * 16, 0, 16, 16, 1, d0, d0, 1.0D);
		this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, p_180516_1_ * 16, 109, p_180516_2_ * 16, 16, 1, 16, d0, 1.0D, d0);
		this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen.generateNoiseOctaves(this.netherrackExclusivityNoise, p_180516_1_ * 16, p_180516_2_ * 16, 0, 16, 16, 1, d0 * 2.0D, d0 * 2.0D, d0 * 2.0D);

		for (int j = 0; j < 16; ++j) {
			for (int k = 0; k < 16; ++k) {
				boolean flag = this.slowsandNoise[j + k * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
				boolean flag1 = this.gravelNoise[j + k * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
				int l = (int) (this.netherrackExclusivityNoise[j + k * 16] / 3.0D + 3.0D + this.hellRNG.nextDouble() * 0.25D);
				int i1 = -1;
				IBlockState iblockstate = Blocks.netherrack.getDefaultState();
				IBlockState iblockstate1 = Blocks.netherrack.getDefaultState();

				for (int j1 = 127; j1 >= 0; --j1) {
					if (j1 < 127 - this.hellRNG.nextInt(5) && j1 > this.hellRNG.nextInt(5)) {
						IBlockState iblockstate2 = p_180516_3_.getBlockState(k, j1, j);

						if (iblockstate2.getBlock() != null && iblockstate2.getBlock().getMaterial() != Material.air) {
							if (iblockstate2.getBlock() == Blocks.netherrack) {
								if (i1 == -1) {
									if (l <= 0) {
										iblockstate = null;
										iblockstate1 = Blocks.netherrack.getDefaultState();
									} else if (j1 >= i - 4 && j1 <= i + 1) {
										iblockstate = Blocks.netherrack.getDefaultState();
										iblockstate1 = Blocks.netherrack.getDefaultState();

										if (flag1) {
											iblockstate = Blocks.gravel.getDefaultState();
											iblockstate1 = Blocks.netherrack.getDefaultState();
										}

										if (flag) {
											iblockstate = Blocks.soul_sand.getDefaultState();
											iblockstate1 = Blocks.soul_sand.getDefaultState();
										}
									}

									if (j1 < i && (iblockstate == null || iblockstate.getBlock().getMaterial() == Material.air)) {
										iblockstate = Blocks.lava.getDefaultState();
									}

									i1 = l;

									if (j1 >= i - 1) {
										p_180516_3_.setBlockState(k, j1, j, iblockstate);
									} else {
										p_180516_3_.setBlockState(k, j1, j, iblockstate1);
									}
								} else if (i1 > 0) {
									--i1;
									p_180516_3_.setBlockState(k, j1, j, iblockstate1);
								}
							}
						} else {
							i1 = -1;
						}
					} else {
						p_180516_3_.setBlockState(k, j1, j, Blocks.bedrock.getDefaultState());
					}
				}
			}
		}
	}

	/**
	 * Will return back a chunk, if it doesn't exist and its not a MP client it will
	 * generates all the blocks for the specified chunk from the map seed and chunk
	 * seed
	 */
	public Chunk provideChunk(int x, int z) {
		this.hellRNG.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		this.func_180515_a(x, z, chunkprimer);
		this.func_180516_b(x, z, chunkprimer);
		this.netherCaveGenerator.generate(this, this.worldObj, x, z, chunkprimer);

		if (this.field_177466_i) {
			this.genNetherBridge.generate(this, this.worldObj, x, z, chunkprimer);
		}

		Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
		BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[]) null, x * 16, z * 16, 16, 16);
		byte[] abyte = chunk.getBiomeArray();

		for (int i = 0; i < abyte.length; ++i) {
			abyte[i] = (byte) abiomegenbase[i].biomeID;
		}

		chunk.resetRelightChecks();
		return chunk;
	}

	/**
	 * generates a subset of the level's terrain data. Takes 7 arguments: the
	 * [empty] noise array, the position, and the size.
	 */
	private double[] initializeNoiseField(double[] p_73164_1_, int p_73164_2_, int p_73164_3_, int p_73164_4_, int p_73164_5_, int p_73164_6_, int p_73164_7_) {
		if (p_73164_1_ == null) {
			p_73164_1_ = new double[p_73164_5_ * p_73164_6_ * p_73164_7_];
		}

		double d0 = 684.412D;
		double d1 = 2053.236D;
		this.noiseData4 = this.netherNoiseGen6.generateNoiseOctaves(this.noiseData4, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, 1, p_73164_7_, 1.0D, 0.0D, 1.0D);
		this.noiseData5 = this.netherNoiseGen7.generateNoiseOctaves(this.noiseData5, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, 1, p_73164_7_, 100.0D, 0.0D, 100.0D);
		this.noiseData1 = this.netherNoiseGen3.generateNoiseOctaves(this.noiseData1, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, d0 / 80.0D, d1 / 60.0D, d0 / 80.0D);
		this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, d0, d1, d0);
		this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, d0, d1, d0);
		int i = 0;
		double[] adouble = new double[p_73164_6_];

		for (int j = 0; j < p_73164_6_; ++j) {
			adouble[j] = Math.cos((double) j * Math.PI * 6.0D / (double) p_73164_6_) * 2.0D;
			double d2 = (double) j;

			if (j > p_73164_6_ / 2) {
				d2 = (double) (p_73164_6_ - 1 - j);
			}

			if (d2 < 4.0D) {
				d2 = 4.0D - d2;
				adouble[j] -= d2 * d2 * d2 * 10.0D;
			}
		}

		for (int l = 0; l < p_73164_5_; ++l) {
			for (int i1 = 0; i1 < p_73164_7_; ++i1) {
				double d3 = 0.0D;

				for (int k = 0; k < p_73164_6_; ++k) {
					double d4 = 0.0D;
					double d5 = adouble[k];
					double d6 = this.noiseData2[i] / 512.0D;
					double d7 = this.noiseData3[i] / 512.0D;
					double d8 = (this.noiseData1[i] / 10.0D + 1.0D) / 2.0D;

					if (d8 < 0.0D) {
						d4 = d6;
					} else if (d8 > 1.0D) {
						d4 = d7;
					} else {
						d4 = d6 + (d7 - d6) * d8;
					}

					d4 = d4 - d5;

					if (k > p_73164_6_ - 4) {
						double d9 = (double) ((float) (k - (p_73164_6_ - 4)) / 3.0F);
						d4 = d4 * (1.0D - d9) + -10.0D * d9;
					}

					if ((double) k < d3) {
						double d10 = (d3 - (double) k) / 4.0D;
						d10 = MathHelper.clamp_double(d10, 0.0D, 1.0D);
						d4 = d4 * (1.0D - d10) + -10.0D * d10;
					}

					p_73164_1_[i] = d4;
					++i;
				}
			}
		}

		return p_73164_1_;
	}

	/**
	 * Checks to see if a chunk exists at x, z
	 */
	public boolean chunkExists(int x, int z) {
		return true;
	}

	/**
	 * Populates chunk with ores etc etc
	 */
	public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {
		BlockFalling.fallInstantly = true;
		BlockPos blockpos = new BlockPos(p_73153_2_ * 16, 0, p_73153_3_ * 16);
		ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(p_73153_2_, p_73153_3_);
		this.genNetherBridge.generateStructure(this.worldObj, this.hellRNG, chunkcoordintpair);

		for (int i = 0; i < 8; ++i) {
			this.field_177472_y.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
		}

		for (int j = 0; j < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1; ++j) {
			this.field_177470_t.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
		}

		for (int k = 0; k < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1); ++k) {
			this.field_177469_u.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
		}

		for (int l = 0; l < 10; ++l) {
			this.field_177468_v.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
		}

		if (this.hellRNG.nextBoolean()) {
			this.field_177471_z.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
		}

		if (this.hellRNG.nextBoolean()) {
			this.field_177465_A.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
		}

		for (int i1 = 0; i1 < 16; ++i1) {
			this.field_177467_w.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
		}

		for (int j1 = 0; j1 < 16; ++j1) {
			this.field_177473_x.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
		}

		BlockFalling.fallInstantly = false;
	}

	public boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_) {
		return false;
	}

	/**
	 * Two modes of operation: if passed true, save all Chunks in one go. If passed
	 * false, save up to two chunks. Return true if all chunks have been saved.
	 */
	public boolean saveChunks(boolean p_73151_1_, IProgressUpdate progressCallback) {
		return true;
	}

	/**
	 * Save extra data not associated with any Chunk. Not saved during autosave,
	 * only during world unload. Currently unimplemented.
	 */
	public void saveExtraData() {
	}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to
	 * unload every such chunk.
	 */
	public boolean unloadQueuedChunks() {
		return false;
	}

	/**
	 * Returns if the IChunkProvider supports saving.
	 */
	public boolean canSave() {
		return true;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	public String makeString() {
		return "HellRandomLevelSource";
	}

	public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		if (creatureType == EnumCreatureType.MONSTER) {
			if (this.genNetherBridge.func_175795_b(pos)) {
				return this.genNetherBridge.getSpawnList();
			}

			if (this.genNetherBridge.func_175796_a(this.worldObj, pos) && this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.nether_brick) {
				return this.genNetherBridge.getSpawnList();
			}
		}

		BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(pos);
		return biomegenbase.getSpawnableList(creatureType);
	}

	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
		return null;
	}

	public int getLoadedChunkCount() {
		return 0;
	}

	public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_) {
		this.genNetherBridge.generate(this, this.worldObj, p_180514_2_, p_180514_3_, (ChunkPrimer) null);
	}

	public Chunk provideChunk(BlockPos blockPosIn) {
		return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
	}
}
