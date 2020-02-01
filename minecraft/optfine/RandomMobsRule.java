package optfine;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class RandomMobsRule
{
    private ResourceLocation baseResLoc = null;
    private int[] skins = null;
    private ResourceLocation[] resourceLocations = null;
    private int[] weights = null;
    private BiomeGenBase[] biomes = null;
    private RangeListInt heights = null;
    public int[] sumWeights = null;
    public int sumAllWeights = 1;

    public RandomMobsRule(ResourceLocation p_i53_1_, int[] p_i53_2_, int[] p_i53_3_, BiomeGenBase[] p_i53_4_, RangeListInt p_i53_5_)
    {
        this.baseResLoc = p_i53_1_;
        this.skins = p_i53_2_;
        this.weights = p_i53_3_;
        this.biomes = p_i53_4_;
        this.heights = p_i53_5_;
    }

    public boolean isValid(String p_isValid_1_)
    {
        this.resourceLocations = new ResourceLocation[this.skins.length];
        ResourceLocation resourcelocation = RandomMobs.getMcpatcherLocation(this.baseResLoc);

        if (resourcelocation == null)
        {
            Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
            return false;
        }
        else
        {
            for (int i = 0; i < this.resourceLocations.length; ++i)
            {
                int j = this.skins[i];

                if (j <= 1)
                {
                    this.resourceLocations[i] = this.baseResLoc;
                }
                else
                {
                    ResourceLocation resourcelocation1 = RandomMobs.getLocationIndexed(resourcelocation, j);

                    if (resourcelocation1 == null)
                    {
                        Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
                        return false;
                    }

                    if (!Config.hasResource(resourcelocation1))
                    {
                        Config.warn("Texture not found: " + resourcelocation1.getResourcePath());
                        return false;
                    }

                    this.resourceLocations[i] = resourcelocation1;
                }
            }

            if (this.weights != null)
            {
                if (this.weights.length > this.resourceLocations.length)
                {
                    Config.warn("More weights defined than skins, trimming weights: " + p_isValid_1_);
                    int[] aint = new int[this.resourceLocations.length];
                    System.arraycopy(this.weights, 0, aint, 0, aint.length);
                    this.weights = aint;
                }

                if (this.weights.length < this.resourceLocations.length)
                {
                    Config.warn("Less weights defined than skins, expanding weights: " + p_isValid_1_);
                    int[] aint1 = new int[this.resourceLocations.length];
                    System.arraycopy(this.weights, 0, aint1, 0, this.weights.length);
                    int l = ConnectedUtils.getAverage(this.weights);

                    for (int j1 = this.weights.length; j1 < aint1.length; ++j1)
                    {
                        aint1[j1] = l;
                    }

                    this.weights = aint1;
                }

                this.sumWeights = new int[this.weights.length];
                int k = 0;

                for (int i1 = 0; i1 < this.weights.length; ++i1)
                {
                    if (this.weights[i1] < 0)
                    {
                        Config.warn("Invalid weight: " + this.weights[i1]);
                        return false;
                    }

                    k += this.weights[i1];
                    this.sumWeights[i1] = k;
                }

                this.sumAllWeights = k;

                if (this.sumAllWeights <= 0)
                {
                    Config.warn("Invalid sum of all weights: " + k);
                    this.sumAllWeights = 1;
                }
            }

            return true;
        }
    }

    public boolean matches(EntityLiving p_matches_1_)
    {
        if (this.biomes != null)
        {
            BiomeGenBase biomegenbase = p_matches_1_.spawnBiome;
            boolean flag = false;

            for (int i = 0; i < this.biomes.length; ++i)
            {
                BiomeGenBase biomegenbase1 = this.biomes[i];

                if (biomegenbase1 == biomegenbase)
                {
                    flag = true;
                    break;
                }
            }

            if (!flag)
            {
                return false;
            }
        }

        return this.heights != null && p_matches_1_.spawnPosition != null ? this.heights.isInRange(p_matches_1_.spawnPosition.getY()) : true;
    }

    public ResourceLocation getTextureLocation(ResourceLocation p_getTextureLocation_1_, int p_getTextureLocation_2_)
    {
        int i = 0;

        if (this.weights == null)
        {
            i = p_getTextureLocation_2_ % this.resourceLocations.length;
        }
        else
        {
            int j = p_getTextureLocation_2_ % this.sumAllWeights;

            for (int k = 0; k < this.sumWeights.length; ++k)
            {
                if (this.sumWeights[k] > j)
                {
                    i = k;
                    break;
                }
            }
        }

        return this.resourceLocations[i];
    }
}
