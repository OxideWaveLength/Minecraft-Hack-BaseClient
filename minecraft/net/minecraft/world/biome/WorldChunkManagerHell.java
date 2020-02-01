package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.util.BlockPos;

public class WorldChunkManagerHell extends WorldChunkManager
{
    /** The biome generator object. */
    private BiomeGenBase biomeGenerator;

    /** The rainfall in the world */
    private float rainfall;

    public WorldChunkManagerHell(BiomeGenBase p_i45374_1_, float p_i45374_2_)
    {
        this.biomeGenerator = p_i45374_1_;
        this.rainfall = p_i45374_2_;
    }

    /**
     * Returns the biome generator
     */
    public BiomeGenBase getBiomeGenerator(BlockPos pos)
    {
        return this.biomeGenerator;
    }

    /**
     * Returns an array of biomes for the location input.
     */
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int height)
    {
        if (biomes == null || biomes.length < width * height)
        {
            biomes = new BiomeGenBase[width * height];
        }

        Arrays.fill(biomes, 0, width * height, this.biomeGenerator);
        return biomes;
    }

    /**
     * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
     */
    public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length)
    {
        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new float[width * length];
        }

        Arrays.fill(listToReuse, 0, width * length, this.rainfall);
        return listToReuse;
    }

    /**
     * Returns biomes to use for the blocks and loads the other data like temperature and humidity onto the
     * WorldChunkManager Args: oldBiomeList, x, z, width, depth
     */
    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x, int z, int width, int depth)
    {
        if (oldBiomeList == null || oldBiomeList.length < width * depth)
        {
            oldBiomeList = new BiomeGenBase[width * depth];
        }

        Arrays.fill(oldBiomeList, 0, width * depth, this.biomeGenerator);
        return oldBiomeList;
    }

    /**
     * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
     * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
     */
    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
    {
        return this.loadBlockGeneratorData(listToReuse, x, z, width, length);
    }

    public BlockPos findBiomePosition(int x, int z, int range, List<BiomeGenBase> biomes, Random random)
    {
        return biomes.contains(this.biomeGenerator) ? new BlockPos(x - range + random.nextInt(range * 2 + 1), 0, z - range + random.nextInt(range * 2 + 1)) : null;
    }

    /**
     * checks given Chunk's Biomes against List of allowed ones
     */
    public boolean areBiomesViable(int p_76940_1_, int p_76940_2_, int p_76940_3_, List<BiomeGenBase> p_76940_4_)
    {
        return p_76940_4_.contains(this.biomeGenerator);
    }
}
