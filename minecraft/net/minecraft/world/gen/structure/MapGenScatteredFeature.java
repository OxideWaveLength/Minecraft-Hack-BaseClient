package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenScatteredFeature extends MapGenStructure
{
    private static final List<BiomeGenBase> biomelist = Arrays.<BiomeGenBase>asList(new BiomeGenBase[] {BiomeGenBase.desert, BiomeGenBase.desertHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.swampland});
    private List<BiomeGenBase.SpawnListEntry> scatteredFeatureSpawnList;

    /** the maximum distance between scattered features */
    private int maxDistanceBetweenScatteredFeatures;

    /** the minimum distance between scattered features */
    private int minDistanceBetweenScatteredFeatures;

    public MapGenScatteredFeature()
    {
        this.scatteredFeatureSpawnList = Lists.<BiomeGenBase.SpawnListEntry>newArrayList();
        this.maxDistanceBetweenScatteredFeatures = 32;
        this.minDistanceBetweenScatteredFeatures = 8;
        this.scatteredFeatureSpawnList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 1, 1, 1));
    }

    public MapGenScatteredFeature(Map<String, String> p_i2061_1_)
    {
        this();

        for (Entry<String, String> entry : p_i2061_1_.entrySet())
        {
            if (((String)entry.getKey()).equals("distance"))
            {
                this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Temple";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int k = chunkX / this.maxDistanceBetweenScatteredFeatures;
        int l = chunkZ / this.maxDistanceBetweenScatteredFeatures;
        Random random = this.worldObj.setRandomSeed(k, l, 14357617);
        k = k * this.maxDistanceBetweenScatteredFeatures;
        l = l * this.maxDistanceBetweenScatteredFeatures;
        k = k + random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
        l = l + random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);

        if (i == k && j == l)
        {
            BiomeGenBase biomegenbase = this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(i * 16 + 8, 0, j * 16 + 8));

            if (biomegenbase == null)
            {
                return false;
            }

            for (BiomeGenBase biomegenbase1 : biomelist)
            {
                if (biomegenbase == biomegenbase1)
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new MapGenScatteredFeature.Start(this.worldObj, this.rand, chunkX, chunkZ);
    }

    public boolean func_175798_a(BlockPos p_175798_1_)
    {
        StructureStart structurestart = this.func_175797_c(p_175798_1_);

        if (structurestart != null && structurestart instanceof MapGenScatteredFeature.Start && !structurestart.components.isEmpty())
        {
            StructureComponent structurecomponent = (StructureComponent)structurestart.components.getFirst();
            return structurecomponent instanceof ComponentScatteredFeaturePieces.SwampHut;
        }
        else
        {
            return false;
        }
    }

    public List<BiomeGenBase.SpawnListEntry> getScatteredFeatureSpawnList()
    {
        return this.scatteredFeatureSpawnList;
    }

    public static class Start extends StructureStart
    {
        public Start()
        {
        }

        public Start(World worldIn, Random p_i2060_2_, int p_i2060_3_, int p_i2060_4_)
        {
            super(p_i2060_3_, p_i2060_4_);
            BiomeGenBase biomegenbase = worldIn.getBiomeGenForCoords(new BlockPos(p_i2060_3_ * 16 + 8, 0, p_i2060_4_ * 16 + 8));

            if (biomegenbase != BiomeGenBase.jungle && biomegenbase != BiomeGenBase.jungleHills)
            {
                if (biomegenbase == BiomeGenBase.swampland)
                {
                    ComponentScatteredFeaturePieces.SwampHut componentscatteredfeaturepieces$swamphut = new ComponentScatteredFeaturePieces.SwampHut(p_i2060_2_, p_i2060_3_ * 16, p_i2060_4_ * 16);
                    this.components.add(componentscatteredfeaturepieces$swamphut);
                }
                else if (biomegenbase == BiomeGenBase.desert || biomegenbase == BiomeGenBase.desertHills)
                {
                    ComponentScatteredFeaturePieces.DesertPyramid componentscatteredfeaturepieces$desertpyramid = new ComponentScatteredFeaturePieces.DesertPyramid(p_i2060_2_, p_i2060_3_ * 16, p_i2060_4_ * 16);
                    this.components.add(componentscatteredfeaturepieces$desertpyramid);
                }
            }
            else
            {
                ComponentScatteredFeaturePieces.JunglePyramid componentscatteredfeaturepieces$junglepyramid = new ComponentScatteredFeaturePieces.JunglePyramid(p_i2060_2_, p_i2060_3_ * 16, p_i2060_4_ * 16);
                this.components.add(componentscatteredfeaturepieces$junglepyramid);
            }

            this.updateBoundingBox();
        }
    }
}
