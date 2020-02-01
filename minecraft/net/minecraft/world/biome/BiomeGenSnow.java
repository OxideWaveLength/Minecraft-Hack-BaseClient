package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenIcePath;
import net.minecraft.world.gen.feature.WorldGenIceSpike;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

public class BiomeGenSnow extends BiomeGenBase
{
    private boolean field_150615_aC;
    private WorldGenIceSpike field_150616_aD = new WorldGenIceSpike();
    private WorldGenIcePath field_150617_aE = new WorldGenIcePath(4);

    public BiomeGenSnow(int p_i45378_1_, boolean p_i45378_2_)
    {
        super(p_i45378_1_);
        this.field_150615_aC = p_i45378_2_;

        if (p_i45378_2_)
        {
            this.topBlock = Blocks.snow.getDefaultState();
        }

        this.spawnableCreatureList.clear();
    }

    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        if (this.field_150615_aC)
        {
            for (int i = 0; i < 3; ++i)
            {
                int j = rand.nextInt(16) + 8;
                int k = rand.nextInt(16) + 8;
                this.field_150616_aD.generate(worldIn, rand, worldIn.getHeight(pos.add(j, 0, k)));
            }

            for (int l = 0; l < 2; ++l)
            {
                int i1 = rand.nextInt(16) + 8;
                int j1 = rand.nextInt(16) + 8;
                this.field_150617_aE.generate(worldIn, rand, worldIn.getHeight(pos.add(i1, 0, j1)));
            }
        }

        super.decorate(worldIn, rand, pos);
    }

    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return new WorldGenTaiga2(false);
    }

    protected BiomeGenBase createMutatedBiome(int p_180277_1_)
    {
        BiomeGenBase biomegenbase = (new BiomeGenSnow(p_180277_1_, true)).func_150557_a(13828095, true).setBiomeName(this.biomeName + " Spikes").setEnableSnow().setTemperatureRainfall(0.0F, 0.5F).setHeight(new BiomeGenBase.Height(this.minHeight + 0.1F, this.maxHeight + 0.1F));
        biomegenbase.minHeight = this.minHeight + 0.3F;
        biomegenbase.maxHeight = this.maxHeight + 0.4F;
        return biomegenbase;
    }
}
