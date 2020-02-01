package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderEnd implements IChunkProvider
{
    private Random endRNG;
    private NoiseGeneratorOctaves noiseGen1;
    private NoiseGeneratorOctaves noiseGen2;
    private NoiseGeneratorOctaves noiseGen3;
    public NoiseGeneratorOctaves noiseGen4;
    public NoiseGeneratorOctaves noiseGen5;
    private World endWorld;
    private double[] densities;

    /** The biomes that are used to generate the chunk */
    private BiomeGenBase[] biomesForGeneration;
    double[] noiseData1;
    double[] noiseData2;
    double[] noiseData3;
    double[] noiseData4;
    double[] noiseData5;

    public ChunkProviderEnd(World worldIn, long p_i2007_2_)
    {
        this.endWorld = worldIn;
        this.endRNG = new Random(p_i2007_2_);
        this.noiseGen1 = new NoiseGeneratorOctaves(this.endRNG, 16);
        this.noiseGen2 = new NoiseGeneratorOctaves(this.endRNG, 16);
        this.noiseGen3 = new NoiseGeneratorOctaves(this.endRNG, 8);
        this.noiseGen4 = new NoiseGeneratorOctaves(this.endRNG, 10);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.endRNG, 16);
    }

    public void func_180520_a(int p_180520_1_, int p_180520_2_, ChunkPrimer p_180520_3_)
    {
        int i = 2;
        int j = i + 1;
        int k = 33;
        int l = i + 1;
        this.densities = this.initializeNoiseField(this.densities, p_180520_1_ * i, 0, p_180520_2_ * i, j, k, l);

        for (int i1 = 0; i1 < i; ++i1)
        {
            for (int j1 = 0; j1 < i; ++j1)
            {
                for (int k1 = 0; k1 < 32; ++k1)
                {
                    double d0 = 0.25D;
                    double d1 = this.densities[((i1 + 0) * l + j1 + 0) * k + k1 + 0];
                    double d2 = this.densities[((i1 + 0) * l + j1 + 1) * k + k1 + 0];
                    double d3 = this.densities[((i1 + 1) * l + j1 + 0) * k + k1 + 0];
                    double d4 = this.densities[((i1 + 1) * l + j1 + 1) * k + k1 + 0];
                    double d5 = (this.densities[((i1 + 0) * l + j1 + 0) * k + k1 + 1] - d1) * d0;
                    double d6 = (this.densities[((i1 + 0) * l + j1 + 1) * k + k1 + 1] - d2) * d0;
                    double d7 = (this.densities[((i1 + 1) * l + j1 + 0) * k + k1 + 1] - d3) * d0;
                    double d8 = (this.densities[((i1 + 1) * l + j1 + 1) * k + k1 + 1] - d4) * d0;

                    for (int l1 = 0; l1 < 4; ++l1)
                    {
                        double d9 = 0.125D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i2 = 0; i2 < 8; ++i2)
                        {
                            double d14 = 0.125D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * d14;

                            for (int j2 = 0; j2 < 8; ++j2)
                            {
                                IBlockState iblockstate = null;

                                if (d15 > 0.0D)
                                {
                                    iblockstate = Blocks.end_stone.getDefaultState();
                                }

                                int k2 = i2 + i1 * 8;
                                int l2 = l1 + k1 * 4;
                                int i3 = j2 + j1 * 8;
                                p_180520_3_.setBlockState(k2, l2, i3, iblockstate);
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

    public void func_180519_a(ChunkPrimer p_180519_1_)
    {
        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                int k = 1;
                int l = -1;
                IBlockState iblockstate = Blocks.end_stone.getDefaultState();
                IBlockState iblockstate1 = Blocks.end_stone.getDefaultState();

                for (int i1 = 127; i1 >= 0; --i1)
                {
                    IBlockState iblockstate2 = p_180519_1_.getBlockState(i, i1, j);

                    if (iblockstate2.getBlock().getMaterial() == Material.air)
                    {
                        l = -1;
                    }
                    else if (iblockstate2.getBlock() == Blocks.stone)
                    {
                        if (l == -1)
                        {
                            if (k <= 0)
                            {
                                iblockstate = Blocks.air.getDefaultState();
                                iblockstate1 = Blocks.end_stone.getDefaultState();
                            }

                            l = k;

                            if (i1 >= 0)
                            {
                                p_180519_1_.setBlockState(i, i1, j, iblockstate);
                            }
                            else
                            {
                                p_180519_1_.setBlockState(i, i1, j, iblockstate1);
                            }
                        }
                        else if (l > 0)
                        {
                            --l;
                            p_180519_1_.setBlockState(i, i1, j, iblockstate1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int x, int z)
    {
        this.endRNG.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.biomesForGeneration = this.endWorld.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.func_180520_a(x, z, chunkprimer);
        this.func_180519_a(chunkprimer);
        Chunk chunk = new Chunk(this.endWorld, chunkprimer, x, z);
        byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i)
        {
            abyte[i] = (byte)this.biomesForGeneration[i].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    /**
     * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the
     * size.
     */
    private double[] initializeNoiseField(double[] p_73187_1_, int p_73187_2_, int p_73187_3_, int p_73187_4_, int p_73187_5_, int p_73187_6_, int p_73187_7_)
    {
        if (p_73187_1_ == null)
        {
            p_73187_1_ = new double[p_73187_5_ * p_73187_6_ * p_73187_7_];
        }

        double d0 = 684.412D;
        double d1 = 684.412D;
        this.noiseData4 = this.noiseGen4.generateNoiseOctaves(this.noiseData4, p_73187_2_, p_73187_4_, p_73187_5_, p_73187_7_, 1.121D, 1.121D, 0.5D);
        this.noiseData5 = this.noiseGen5.generateNoiseOctaves(this.noiseData5, p_73187_2_, p_73187_4_, p_73187_5_, p_73187_7_, 200.0D, 200.0D, 0.5D);
        d0 = d0 * 2.0D;
        this.noiseData1 = this.noiseGen3.generateNoiseOctaves(this.noiseData1, p_73187_2_, p_73187_3_, p_73187_4_, p_73187_5_, p_73187_6_, p_73187_7_, d0 / 80.0D, d1 / 160.0D, d0 / 80.0D);
        this.noiseData2 = this.noiseGen1.generateNoiseOctaves(this.noiseData2, p_73187_2_, p_73187_3_, p_73187_4_, p_73187_5_, p_73187_6_, p_73187_7_, d0, d1, d0);
        this.noiseData3 = this.noiseGen2.generateNoiseOctaves(this.noiseData3, p_73187_2_, p_73187_3_, p_73187_4_, p_73187_5_, p_73187_6_, p_73187_7_, d0, d1, d0);
        int i = 0;

        for (int j = 0; j < p_73187_5_; ++j)
        {
            for (int k = 0; k < p_73187_7_; ++k)
            {
                float f = (float)(j + p_73187_2_) / 1.0F;
                float f1 = (float)(k + p_73187_4_) / 1.0F;
                float f2 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * 8.0F;

                if (f2 > 80.0F)
                {
                    f2 = 80.0F;
                }

                if (f2 < -100.0F)
                {
                    f2 = -100.0F;
                }

                for (int l = 0; l < p_73187_6_; ++l)
                {
                    double d2 = 0.0D;
                    double d3 = this.noiseData2[i] / 512.0D;
                    double d4 = this.noiseData3[i] / 512.0D;
                    double d5 = (this.noiseData1[i] / 10.0D + 1.0D) / 2.0D;

                    if (d5 < 0.0D)
                    {
                        d2 = d3;
                    }
                    else if (d5 > 1.0D)
                    {
                        d2 = d4;
                    }
                    else
                    {
                        d2 = d3 + (d4 - d3) * d5;
                    }

                    d2 = d2 - 8.0D;
                    d2 = d2 + (double)f2;
                    int i1 = 2;

                    if (l > p_73187_6_ / 2 - i1)
                    {
                        double d6 = (double)((float)(l - (p_73187_6_ / 2 - i1)) / 64.0F);
                        d6 = MathHelper.clamp_double(d6, 0.0D, 1.0D);
                        d2 = d2 * (1.0D - d6) + -3000.0D * d6;
                    }

                    i1 = 8;

                    if (l < i1)
                    {
                        double d7 = (double)((float)(i1 - l) / ((float)i1 - 1.0F));
                        d2 = d2 * (1.0D - d7) + -30.0D * d7;
                    }

                    p_73187_1_[i] = d2;
                    ++i;
                }
            }
        }

        return p_73187_1_;
    }

    /**
     * Checks to see if a chunk exists at x, z
     */
    public boolean chunkExists(int x, int z)
    {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
    {
        BlockFalling.fallInstantly = true;
        BlockPos blockpos = new BlockPos(p_73153_2_ * 16, 0, p_73153_3_ * 16);
        this.endWorld.getBiomeGenForCoords(blockpos.add(16, 0, 16)).decorate(this.endWorld, this.endWorld.rand, blockpos);
        BlockFalling.fallInstantly = false;
    }

    public boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_)
    {
        return false;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean p_73151_1_, IProgressUpdate progressCallback)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData()
    {
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "RandomLevelSource";
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return this.endWorld.getBiomeGenForCoords(pos).getSpawnableList(creatureType);
    }

    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
    {
        return null;
    }

    public int getLoadedChunkCount()
    {
        return 0;
    }

    public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_)
    {
    }

    public Chunk provideChunk(BlockPos blockPosIn)
    {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
