package net.minecraft.world.biome;

import java.util.Random;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;

public class BiomeGenForest extends BiomeGenBase {
	private int field_150632_aF;
	protected static final WorldGenForest field_150629_aC = new WorldGenForest(false, true);
	protected static final WorldGenForest field_150630_aD = new WorldGenForest(false, false);
	protected static final WorldGenCanopyTree field_150631_aE = new WorldGenCanopyTree(false);

	public BiomeGenForest(int p_i45377_1_, int p_i45377_2_) {
		super(p_i45377_1_);
		this.field_150632_aF = p_i45377_2_;
		this.theBiomeDecorator.treesPerChunk = 10;
		this.theBiomeDecorator.grassPerChunk = 2;

		if (this.field_150632_aF == 1) {
			this.theBiomeDecorator.treesPerChunk = 6;
			this.theBiomeDecorator.flowersPerChunk = 100;
			this.theBiomeDecorator.grassPerChunk = 1;
		}

		this.setFillerBlockMetadata(5159473);
		this.setTemperatureRainfall(0.7F, 0.8F);

		if (this.field_150632_aF == 2) {
			this.field_150609_ah = 353825;
			this.color = 3175492;
			this.setTemperatureRainfall(0.6F, 0.6F);
		}

		if (this.field_150632_aF == 0) {
			this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 5, 4, 4));
		}

		if (this.field_150632_aF == 3) {
			this.theBiomeDecorator.treesPerChunk = -999;
		}
	}

	protected BiomeGenBase func_150557_a(int p_150557_1_, boolean p_150557_2_) {
		if (this.field_150632_aF == 2) {
			this.field_150609_ah = 353825;
			this.color = p_150557_1_;

			if (p_150557_2_) {
				this.field_150609_ah = (this.field_150609_ah & 16711422) >> 1;
			}

			return this;
		} else {
			return super.func_150557_a(p_150557_1_, p_150557_2_);
		}
	}

	public WorldGenAbstractTree genBigTreeChance(Random rand) {
		return (WorldGenAbstractTree) (this.field_150632_aF == 3 && rand.nextInt(3) > 0 ? field_150631_aE : (this.field_150632_aF != 2 && rand.nextInt(5) != 0 ? this.worldGeneratorTrees : field_150630_aD));
	}

	public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
		if (this.field_150632_aF == 1) {
			double d0 = MathHelper.clamp_double((1.0D + GRASS_COLOR_NOISE.func_151601_a((double) pos.getX() / 48.0D, (double) pos.getZ() / 48.0D)) / 2.0D, 0.0D, 0.9999D);
			BlockFlower.EnumFlowerType blockflower$enumflowertype = BlockFlower.EnumFlowerType.values()[(int) (d0 * (double) BlockFlower.EnumFlowerType.values().length)];
			return blockflower$enumflowertype == BlockFlower.EnumFlowerType.BLUE_ORCHID ? BlockFlower.EnumFlowerType.POPPY : blockflower$enumflowertype;
		} else {
			return super.pickRandomFlower(rand, pos);
		}
	}

	public void decorate(World worldIn, Random rand, BlockPos pos) {
		if (this.field_150632_aF == 3) {
			for (int i = 0; i < 4; ++i) {
				for (int j = 0; j < 4; ++j) {
					int k = i * 4 + 1 + 8 + rand.nextInt(3);
					int l = j * 4 + 1 + 8 + rand.nextInt(3);
					BlockPos blockpos = worldIn.getHeight(pos.add(k, 0, l));

					if (rand.nextInt(20) == 0) {
						WorldGenBigMushroom worldgenbigmushroom = new WorldGenBigMushroom();
						worldgenbigmushroom.generate(worldIn, rand, blockpos);
					} else {
						WorldGenAbstractTree worldgenabstracttree = this.genBigTreeChance(rand);
						worldgenabstracttree.func_175904_e();

						if (worldgenabstracttree.generate(worldIn, rand, blockpos)) {
							worldgenabstracttree.func_180711_a(worldIn, rand, blockpos);
						}
					}
				}
			}
		}

		int j1 = rand.nextInt(5) - 3;

		if (this.field_150632_aF == 1) {
			j1 += 2;
		}

		for (int k1 = 0; k1 < j1; ++k1) {
			int l1 = rand.nextInt(3);

			if (l1 == 0) {
				DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SYRINGA);
			} else if (l1 == 1) {
				DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.ROSE);
			} else if (l1 == 2) {
				DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.PAEONIA);
			}

			for (int i2 = 0; i2 < 5; ++i2) {
				int j2 = rand.nextInt(16) + 8;
				int k2 = rand.nextInt(16) + 8;
				int i1 = rand.nextInt(worldIn.getHeight(pos.add(j2, 0, k2)).getY() + 32);

				if (DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, new BlockPos(pos.getX() + j2, i1, pos.getZ() + k2))) {
					break;
				}
			}
		}

		super.decorate(worldIn, rand, pos);
	}

	public int getGrassColorAtPos(BlockPos pos) {
		int i = super.getGrassColorAtPos(pos);
		return this.field_150632_aF == 3 ? (i & 16711422) + 2634762 >> 1 : i;
	}

	protected BiomeGenBase createMutatedBiome(final int p_180277_1_) {
		if (this.biomeID == BiomeGenBase.forest.biomeID) {
			BiomeGenForest biomegenforest = new BiomeGenForest(p_180277_1_, 1);
			biomegenforest.setHeight(new BiomeGenBase.Height(this.minHeight, this.maxHeight + 0.2F));
			biomegenforest.setBiomeName("Flower Forest");
			biomegenforest.func_150557_a(6976549, true);
			biomegenforest.setFillerBlockMetadata(8233509);
			return biomegenforest;
		} else {
			return this.biomeID != BiomeGenBase.birchForest.biomeID && this.biomeID != BiomeGenBase.birchForestHills.biomeID ? new BiomeGenMutated(p_180277_1_, this) {
				public void decorate(World worldIn, Random rand, BlockPos pos) {
					this.baseBiome.decorate(worldIn, rand, pos);
				}
			} : new BiomeGenMutated(p_180277_1_, this) {
				public WorldGenAbstractTree genBigTreeChance(Random rand) {
					return rand.nextBoolean() ? BiomeGenForest.field_150629_aC : BiomeGenForest.field_150630_aD;
				}
			};
		}
	}
}
