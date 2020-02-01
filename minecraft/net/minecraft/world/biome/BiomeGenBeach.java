package net.minecraft.world.biome;

import net.minecraft.init.Blocks;

public class BiomeGenBeach extends BiomeGenBase
{
    public BiomeGenBeach(int p_i1969_1_)
    {
        super(p_i1969_1_);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.sand.getDefaultState();
        this.fillerBlock = Blocks.sand.getDefaultState();
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 0;
        this.theBiomeDecorator.reedsPerChunk = 0;
        this.theBiomeDecorator.cactiPerChunk = 0;
    }
}
