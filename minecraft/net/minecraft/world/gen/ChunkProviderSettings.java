package net.minecraft.world.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.biome.BiomeGenBase;

public class ChunkProviderSettings
{
    public final float coordinateScale;
    public final float heightScale;
    public final float upperLimitScale;
    public final float lowerLimitScale;
    public final float depthNoiseScaleX;
    public final float depthNoiseScaleZ;
    public final float depthNoiseScaleExponent;
    public final float mainNoiseScaleX;
    public final float mainNoiseScaleY;
    public final float mainNoiseScaleZ;
    public final float baseSize;
    public final float stretchY;
    public final float biomeDepthWeight;
    public final float biomeDepthOffSet;
    public final float biomeScaleWeight;
    public final float biomeScaleOffset;
    public final int seaLevel;
    public final boolean useCaves;
    public final boolean useDungeons;
    public final int dungeonChance;
    public final boolean useStrongholds;
    public final boolean useVillages;
    public final boolean useMineShafts;
    public final boolean useTemples;
    public final boolean useMonuments;
    public final boolean useRavines;
    public final boolean useWaterLakes;
    public final int waterLakeChance;
    public final boolean useLavaLakes;
    public final int lavaLakeChance;
    public final boolean useLavaOceans;
    public final int fixedBiome;
    public final int biomeSize;
    public final int riverSize;
    public final int dirtSize;
    public final int dirtCount;
    public final int dirtMinHeight;
    public final int dirtMaxHeight;
    public final int gravelSize;
    public final int gravelCount;
    public final int gravelMinHeight;
    public final int gravelMaxHeight;
    public final int graniteSize;
    public final int graniteCount;
    public final int graniteMinHeight;
    public final int graniteMaxHeight;
    public final int dioriteSize;
    public final int dioriteCount;
    public final int dioriteMinHeight;
    public final int dioriteMaxHeight;
    public final int andesiteSize;
    public final int andesiteCount;
    public final int andesiteMinHeight;
    public final int andesiteMaxHeight;
    public final int coalSize;
    public final int coalCount;
    public final int coalMinHeight;
    public final int coalMaxHeight;
    public final int ironSize;
    public final int ironCount;
    public final int ironMinHeight;
    public final int ironMaxHeight;
    public final int goldSize;
    public final int goldCount;
    public final int goldMinHeight;
    public final int goldMaxHeight;
    public final int redstoneSize;
    public final int redstoneCount;
    public final int redstoneMinHeight;
    public final int redstoneMaxHeight;
    public final int diamondSize;
    public final int diamondCount;
    public final int diamondMinHeight;
    public final int diamondMaxHeight;
    public final int lapisSize;
    public final int lapisCount;
    public final int lapisCenterHeight;
    public final int lapisSpread;

    private ChunkProviderSettings(ChunkProviderSettings.Factory settingsFactory)
    {
        this.coordinateScale = settingsFactory.coordinateScale;
        this.heightScale = settingsFactory.heightScale;
        this.upperLimitScale = settingsFactory.upperLimitScale;
        this.lowerLimitScale = settingsFactory.lowerLimitScale;
        this.depthNoiseScaleX = settingsFactory.depthNoiseScaleX;
        this.depthNoiseScaleZ = settingsFactory.depthNoiseScaleZ;
        this.depthNoiseScaleExponent = settingsFactory.depthNoiseScaleExponent;
        this.mainNoiseScaleX = settingsFactory.mainNoiseScaleX;
        this.mainNoiseScaleY = settingsFactory.mainNoiseScaleY;
        this.mainNoiseScaleZ = settingsFactory.mainNoiseScaleZ;
        this.baseSize = settingsFactory.baseSize;
        this.stretchY = settingsFactory.stretchY;
        this.biomeDepthWeight = settingsFactory.biomeDepthWeight;
        this.biomeDepthOffSet = settingsFactory.biomeDepthOffset;
        this.biomeScaleWeight = settingsFactory.biomeScaleWeight;
        this.biomeScaleOffset = settingsFactory.biomeScaleOffset;
        this.seaLevel = settingsFactory.seaLevel;
        this.useCaves = settingsFactory.useCaves;
        this.useDungeons = settingsFactory.useDungeons;
        this.dungeonChance = settingsFactory.dungeonChance;
        this.useStrongholds = settingsFactory.useStrongholds;
        this.useVillages = settingsFactory.useVillages;
        this.useMineShafts = settingsFactory.useMineShafts;
        this.useTemples = settingsFactory.useTemples;
        this.useMonuments = settingsFactory.useMonuments;
        this.useRavines = settingsFactory.useRavines;
        this.useWaterLakes = settingsFactory.useWaterLakes;
        this.waterLakeChance = settingsFactory.waterLakeChance;
        this.useLavaLakes = settingsFactory.useLavaLakes;
        this.lavaLakeChance = settingsFactory.lavaLakeChance;
        this.useLavaOceans = settingsFactory.useLavaOceans;
        this.fixedBiome = settingsFactory.fixedBiome;
        this.biomeSize = settingsFactory.biomeSize;
        this.riverSize = settingsFactory.riverSize;
        this.dirtSize = settingsFactory.dirtSize;
        this.dirtCount = settingsFactory.dirtCount;
        this.dirtMinHeight = settingsFactory.dirtMinHeight;
        this.dirtMaxHeight = settingsFactory.dirtMaxHeight;
        this.gravelSize = settingsFactory.gravelSize;
        this.gravelCount = settingsFactory.gravelCount;
        this.gravelMinHeight = settingsFactory.gravelMinHeight;
        this.gravelMaxHeight = settingsFactory.gravelMaxHeight;
        this.graniteSize = settingsFactory.graniteSize;
        this.graniteCount = settingsFactory.graniteCount;
        this.graniteMinHeight = settingsFactory.graniteMinHeight;
        this.graniteMaxHeight = settingsFactory.graniteMaxHeight;
        this.dioriteSize = settingsFactory.dioriteSize;
        this.dioriteCount = settingsFactory.dioriteCount;
        this.dioriteMinHeight = settingsFactory.dioriteMinHeight;
        this.dioriteMaxHeight = settingsFactory.dioriteMaxHeight;
        this.andesiteSize = settingsFactory.andesiteSize;
        this.andesiteCount = settingsFactory.andesiteCount;
        this.andesiteMinHeight = settingsFactory.andesiteMinHeight;
        this.andesiteMaxHeight = settingsFactory.andesiteMaxHeight;
        this.coalSize = settingsFactory.coalSize;
        this.coalCount = settingsFactory.coalCount;
        this.coalMinHeight = settingsFactory.coalMinHeight;
        this.coalMaxHeight = settingsFactory.coalMaxHeight;
        this.ironSize = settingsFactory.ironSize;
        this.ironCount = settingsFactory.ironCount;
        this.ironMinHeight = settingsFactory.ironMinHeight;
        this.ironMaxHeight = settingsFactory.ironMaxHeight;
        this.goldSize = settingsFactory.goldSize;
        this.goldCount = settingsFactory.goldCount;
        this.goldMinHeight = settingsFactory.goldMinHeight;
        this.goldMaxHeight = settingsFactory.goldMaxHeight;
        this.redstoneSize = settingsFactory.redstoneSize;
        this.redstoneCount = settingsFactory.redstoneCount;
        this.redstoneMinHeight = settingsFactory.redstoneMinHeight;
        this.redstoneMaxHeight = settingsFactory.redstoneMaxHeight;
        this.diamondSize = settingsFactory.diamondSize;
        this.diamondCount = settingsFactory.diamondCount;
        this.diamondMinHeight = settingsFactory.diamondMinHeight;
        this.diamondMaxHeight = settingsFactory.diamondMaxHeight;
        this.lapisSize = settingsFactory.lapisSize;
        this.lapisCount = settingsFactory.lapisCount;
        this.lapisCenterHeight = settingsFactory.lapisCenterHeight;
        this.lapisSpread = settingsFactory.lapisSpread;
    }

    public static class Factory
    {
        static final Gson JSON_ADAPTER = (new GsonBuilder()).registerTypeAdapter(ChunkProviderSettings.Factory.class, new ChunkProviderSettings.Serializer()).create();
        public float coordinateScale = 684.412F;
        public float heightScale = 684.412F;
        public float upperLimitScale = 512.0F;
        public float lowerLimitScale = 512.0F;
        public float depthNoiseScaleX = 200.0F;
        public float depthNoiseScaleZ = 200.0F;
        public float depthNoiseScaleExponent = 0.5F;
        public float mainNoiseScaleX = 80.0F;
        public float mainNoiseScaleY = 160.0F;
        public float mainNoiseScaleZ = 80.0F;
        public float baseSize = 8.5F;
        public float stretchY = 12.0F;
        public float biomeDepthWeight = 1.0F;
        public float biomeDepthOffset = 0.0F;
        public float biomeScaleWeight = 1.0F;
        public float biomeScaleOffset = 0.0F;
        public int seaLevel = 63;
        public boolean useCaves = true;
        public boolean useDungeons = true;
        public int dungeonChance = 8;
        public boolean useStrongholds = true;
        public boolean useVillages = true;
        public boolean useMineShafts = true;
        public boolean useTemples = true;
        public boolean useMonuments = true;
        public boolean useRavines = true;
        public boolean useWaterLakes = true;
        public int waterLakeChance = 4;
        public boolean useLavaLakes = true;
        public int lavaLakeChance = 80;
        public boolean useLavaOceans = false;
        public int fixedBiome = -1;
        public int biomeSize = 4;
        public int riverSize = 4;
        public int dirtSize = 33;
        public int dirtCount = 10;
        public int dirtMinHeight = 0;
        public int dirtMaxHeight = 256;
        public int gravelSize = 33;
        public int gravelCount = 8;
        public int gravelMinHeight = 0;
        public int gravelMaxHeight = 256;
        public int graniteSize = 33;
        public int graniteCount = 10;
        public int graniteMinHeight = 0;
        public int graniteMaxHeight = 80;
        public int dioriteSize = 33;
        public int dioriteCount = 10;
        public int dioriteMinHeight = 0;
        public int dioriteMaxHeight = 80;
        public int andesiteSize = 33;
        public int andesiteCount = 10;
        public int andesiteMinHeight = 0;
        public int andesiteMaxHeight = 80;
        public int coalSize = 17;
        public int coalCount = 20;
        public int coalMinHeight = 0;
        public int coalMaxHeight = 128;
        public int ironSize = 9;
        public int ironCount = 20;
        public int ironMinHeight = 0;
        public int ironMaxHeight = 64;
        public int goldSize = 9;
        public int goldCount = 2;
        public int goldMinHeight = 0;
        public int goldMaxHeight = 32;
        public int redstoneSize = 8;
        public int redstoneCount = 8;
        public int redstoneMinHeight = 0;
        public int redstoneMaxHeight = 16;
        public int diamondSize = 8;
        public int diamondCount = 1;
        public int diamondMinHeight = 0;
        public int diamondMaxHeight = 16;
        public int lapisSize = 7;
        public int lapisCount = 1;
        public int lapisCenterHeight = 16;
        public int lapisSpread = 16;

        public static ChunkProviderSettings.Factory jsonToFactory(String p_177865_0_)
        {
            if (p_177865_0_.length() == 0)
            {
                return new ChunkProviderSettings.Factory();
            }
            else
            {
                try
                {
                    return (ChunkProviderSettings.Factory)JSON_ADAPTER.fromJson(p_177865_0_, ChunkProviderSettings.Factory.class);
                }
                catch (Exception var2)
                {
                    return new ChunkProviderSettings.Factory();
                }
            }
        }

        public String toString()
        {
            return JSON_ADAPTER.toJson((Object)this);
        }

        public Factory()
        {
            this.func_177863_a();
        }

        public void func_177863_a()
        {
            this.coordinateScale = 684.412F;
            this.heightScale = 684.412F;
            this.upperLimitScale = 512.0F;
            this.lowerLimitScale = 512.0F;
            this.depthNoiseScaleX = 200.0F;
            this.depthNoiseScaleZ = 200.0F;
            this.depthNoiseScaleExponent = 0.5F;
            this.mainNoiseScaleX = 80.0F;
            this.mainNoiseScaleY = 160.0F;
            this.mainNoiseScaleZ = 80.0F;
            this.baseSize = 8.5F;
            this.stretchY = 12.0F;
            this.biomeDepthWeight = 1.0F;
            this.biomeDepthOffset = 0.0F;
            this.biomeScaleWeight = 1.0F;
            this.biomeScaleOffset = 0.0F;
            this.seaLevel = 63;
            this.useCaves = true;
            this.useDungeons = true;
            this.dungeonChance = 8;
            this.useStrongholds = true;
            this.useVillages = true;
            this.useMineShafts = true;
            this.useTemples = true;
            this.useMonuments = true;
            this.useRavines = true;
            this.useWaterLakes = true;
            this.waterLakeChance = 4;
            this.useLavaLakes = true;
            this.lavaLakeChance = 80;
            this.useLavaOceans = false;
            this.fixedBiome = -1;
            this.biomeSize = 4;
            this.riverSize = 4;
            this.dirtSize = 33;
            this.dirtCount = 10;
            this.dirtMinHeight = 0;
            this.dirtMaxHeight = 256;
            this.gravelSize = 33;
            this.gravelCount = 8;
            this.gravelMinHeight = 0;
            this.gravelMaxHeight = 256;
            this.graniteSize = 33;
            this.graniteCount = 10;
            this.graniteMinHeight = 0;
            this.graniteMaxHeight = 80;
            this.dioriteSize = 33;
            this.dioriteCount = 10;
            this.dioriteMinHeight = 0;
            this.dioriteMaxHeight = 80;
            this.andesiteSize = 33;
            this.andesiteCount = 10;
            this.andesiteMinHeight = 0;
            this.andesiteMaxHeight = 80;
            this.coalSize = 17;
            this.coalCount = 20;
            this.coalMinHeight = 0;
            this.coalMaxHeight = 128;
            this.ironSize = 9;
            this.ironCount = 20;
            this.ironMinHeight = 0;
            this.ironMaxHeight = 64;
            this.goldSize = 9;
            this.goldCount = 2;
            this.goldMinHeight = 0;
            this.goldMaxHeight = 32;
            this.redstoneSize = 8;
            this.redstoneCount = 8;
            this.redstoneMinHeight = 0;
            this.redstoneMaxHeight = 16;
            this.diamondSize = 8;
            this.diamondCount = 1;
            this.diamondMinHeight = 0;
            this.diamondMaxHeight = 16;
            this.lapisSize = 7;
            this.lapisCount = 1;
            this.lapisCenterHeight = 16;
            this.lapisSpread = 16;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
            {
                ChunkProviderSettings.Factory chunkprovidersettings$factory = (ChunkProviderSettings.Factory)p_equals_1_;
                return this.andesiteCount != chunkprovidersettings$factory.andesiteCount ? false : (this.andesiteMaxHeight != chunkprovidersettings$factory.andesiteMaxHeight ? false : (this.andesiteMinHeight != chunkprovidersettings$factory.andesiteMinHeight ? false : (this.andesiteSize != chunkprovidersettings$factory.andesiteSize ? false : (Float.compare(chunkprovidersettings$factory.baseSize, this.baseSize) != 0 ? false : (Float.compare(chunkprovidersettings$factory.biomeDepthOffset, this.biomeDepthOffset) != 0 ? false : (Float.compare(chunkprovidersettings$factory.biomeDepthWeight, this.biomeDepthWeight) != 0 ? false : (Float.compare(chunkprovidersettings$factory.biomeScaleOffset, this.biomeScaleOffset) != 0 ? false : (Float.compare(chunkprovidersettings$factory.biomeScaleWeight, this.biomeScaleWeight) != 0 ? false : (this.biomeSize != chunkprovidersettings$factory.biomeSize ? false : (this.coalCount != chunkprovidersettings$factory.coalCount ? false : (this.coalMaxHeight != chunkprovidersettings$factory.coalMaxHeight ? false : (this.coalMinHeight != chunkprovidersettings$factory.coalMinHeight ? false : (this.coalSize != chunkprovidersettings$factory.coalSize ? false : (Float.compare(chunkprovidersettings$factory.coordinateScale, this.coordinateScale) != 0 ? false : (Float.compare(chunkprovidersettings$factory.depthNoiseScaleExponent, this.depthNoiseScaleExponent) != 0 ? false : (Float.compare(chunkprovidersettings$factory.depthNoiseScaleX, this.depthNoiseScaleX) != 0 ? false : (Float.compare(chunkprovidersettings$factory.depthNoiseScaleZ, this.depthNoiseScaleZ) != 0 ? false : (this.diamondCount != chunkprovidersettings$factory.diamondCount ? false : (this.diamondMaxHeight != chunkprovidersettings$factory.diamondMaxHeight ? false : (this.diamondMinHeight != chunkprovidersettings$factory.diamondMinHeight ? false : (this.diamondSize != chunkprovidersettings$factory.diamondSize ? false : (this.dioriteCount != chunkprovidersettings$factory.dioriteCount ? false : (this.dioriteMaxHeight != chunkprovidersettings$factory.dioriteMaxHeight ? false : (this.dioriteMinHeight != chunkprovidersettings$factory.dioriteMinHeight ? false : (this.dioriteSize != chunkprovidersettings$factory.dioriteSize ? false : (this.dirtCount != chunkprovidersettings$factory.dirtCount ? false : (this.dirtMaxHeight != chunkprovidersettings$factory.dirtMaxHeight ? false : (this.dirtMinHeight != chunkprovidersettings$factory.dirtMinHeight ? false : (this.dirtSize != chunkprovidersettings$factory.dirtSize ? false : (this.dungeonChance != chunkprovidersettings$factory.dungeonChance ? false : (this.fixedBiome != chunkprovidersettings$factory.fixedBiome ? false : (this.goldCount != chunkprovidersettings$factory.goldCount ? false : (this.goldMaxHeight != chunkprovidersettings$factory.goldMaxHeight ? false : (this.goldMinHeight != chunkprovidersettings$factory.goldMinHeight ? false : (this.goldSize != chunkprovidersettings$factory.goldSize ? false : (this.graniteCount != chunkprovidersettings$factory.graniteCount ? false : (this.graniteMaxHeight != chunkprovidersettings$factory.graniteMaxHeight ? false : (this.graniteMinHeight != chunkprovidersettings$factory.graniteMinHeight ? false : (this.graniteSize != chunkprovidersettings$factory.graniteSize ? false : (this.gravelCount != chunkprovidersettings$factory.gravelCount ? false : (this.gravelMaxHeight != chunkprovidersettings$factory.gravelMaxHeight ? false : (this.gravelMinHeight != chunkprovidersettings$factory.gravelMinHeight ? false : (this.gravelSize != chunkprovidersettings$factory.gravelSize ? false : (Float.compare(chunkprovidersettings$factory.heightScale, this.heightScale) != 0 ? false : (this.ironCount != chunkprovidersettings$factory.ironCount ? false : (this.ironMaxHeight != chunkprovidersettings$factory.ironMaxHeight ? false : (this.ironMinHeight != chunkprovidersettings$factory.ironMinHeight ? false : (this.ironSize != chunkprovidersettings$factory.ironSize ? false : (this.lapisCenterHeight != chunkprovidersettings$factory.lapisCenterHeight ? false : (this.lapisCount != chunkprovidersettings$factory.lapisCount ? false : (this.lapisSize != chunkprovidersettings$factory.lapisSize ? false : (this.lapisSpread != chunkprovidersettings$factory.lapisSpread ? false : (this.lavaLakeChance != chunkprovidersettings$factory.lavaLakeChance ? false : (Float.compare(chunkprovidersettings$factory.lowerLimitScale, this.lowerLimitScale) != 0 ? false : (Float.compare(chunkprovidersettings$factory.mainNoiseScaleX, this.mainNoiseScaleX) != 0 ? false : (Float.compare(chunkprovidersettings$factory.mainNoiseScaleY, this.mainNoiseScaleY) != 0 ? false : (Float.compare(chunkprovidersettings$factory.mainNoiseScaleZ, this.mainNoiseScaleZ) != 0 ? false : (this.redstoneCount != chunkprovidersettings$factory.redstoneCount ? false : (this.redstoneMaxHeight != chunkprovidersettings$factory.redstoneMaxHeight ? false : (this.redstoneMinHeight != chunkprovidersettings$factory.redstoneMinHeight ? false : (this.redstoneSize != chunkprovidersettings$factory.redstoneSize ? false : (this.riverSize != chunkprovidersettings$factory.riverSize ? false : (this.seaLevel != chunkprovidersettings$factory.seaLevel ? false : (Float.compare(chunkprovidersettings$factory.stretchY, this.stretchY) != 0 ? false : (Float.compare(chunkprovidersettings$factory.upperLimitScale, this.upperLimitScale) != 0 ? false : (this.useCaves != chunkprovidersettings$factory.useCaves ? false : (this.useDungeons != chunkprovidersettings$factory.useDungeons ? false : (this.useLavaLakes != chunkprovidersettings$factory.useLavaLakes ? false : (this.useLavaOceans != chunkprovidersettings$factory.useLavaOceans ? false : (this.useMineShafts != chunkprovidersettings$factory.useMineShafts ? false : (this.useRavines != chunkprovidersettings$factory.useRavines ? false : (this.useStrongholds != chunkprovidersettings$factory.useStrongholds ? false : (this.useTemples != chunkprovidersettings$factory.useTemples ? false : (this.useMonuments != chunkprovidersettings$factory.useMonuments ? false : (this.useVillages != chunkprovidersettings$factory.useVillages ? false : (this.useWaterLakes != chunkprovidersettings$factory.useWaterLakes ? false : this.waterLakeChance == chunkprovidersettings$factory.waterLakeChance))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))));
            }
            else
            {
                return false;
            }
        }

        public int hashCode()
        {
            int i = this.coordinateScale != 0.0F ? Float.floatToIntBits(this.coordinateScale) : 0;
            i = 31 * i + (this.heightScale != 0.0F ? Float.floatToIntBits(this.heightScale) : 0);
            i = 31 * i + (this.upperLimitScale != 0.0F ? Float.floatToIntBits(this.upperLimitScale) : 0);
            i = 31 * i + (this.lowerLimitScale != 0.0F ? Float.floatToIntBits(this.lowerLimitScale) : 0);
            i = 31 * i + (this.depthNoiseScaleX != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleX) : 0);
            i = 31 * i + (this.depthNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleZ) : 0);
            i = 31 * i + (this.depthNoiseScaleExponent != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleExponent) : 0);
            i = 31 * i + (this.mainNoiseScaleX != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleX) : 0);
            i = 31 * i + (this.mainNoiseScaleY != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleY) : 0);
            i = 31 * i + (this.mainNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleZ) : 0);
            i = 31 * i + (this.baseSize != 0.0F ? Float.floatToIntBits(this.baseSize) : 0);
            i = 31 * i + (this.stretchY != 0.0F ? Float.floatToIntBits(this.stretchY) : 0);
            i = 31 * i + (this.biomeDepthWeight != 0.0F ? Float.floatToIntBits(this.biomeDepthWeight) : 0);
            i = 31 * i + (this.biomeDepthOffset != 0.0F ? Float.floatToIntBits(this.biomeDepthOffset) : 0);
            i = 31 * i + (this.biomeScaleWeight != 0.0F ? Float.floatToIntBits(this.biomeScaleWeight) : 0);
            i = 31 * i + (this.biomeScaleOffset != 0.0F ? Float.floatToIntBits(this.biomeScaleOffset) : 0);
            i = 31 * i + this.seaLevel;
            i = 31 * i + (this.useCaves ? 1 : 0);
            i = 31 * i + (this.useDungeons ? 1 : 0);
            i = 31 * i + this.dungeonChance;
            i = 31 * i + (this.useStrongholds ? 1 : 0);
            i = 31 * i + (this.useVillages ? 1 : 0);
            i = 31 * i + (this.useMineShafts ? 1 : 0);
            i = 31 * i + (this.useTemples ? 1 : 0);
            i = 31 * i + (this.useMonuments ? 1 : 0);
            i = 31 * i + (this.useRavines ? 1 : 0);
            i = 31 * i + (this.useWaterLakes ? 1 : 0);
            i = 31 * i + this.waterLakeChance;
            i = 31 * i + (this.useLavaLakes ? 1 : 0);
            i = 31 * i + this.lavaLakeChance;
            i = 31 * i + (this.useLavaOceans ? 1 : 0);
            i = 31 * i + this.fixedBiome;
            i = 31 * i + this.biomeSize;
            i = 31 * i + this.riverSize;
            i = 31 * i + this.dirtSize;
            i = 31 * i + this.dirtCount;
            i = 31 * i + this.dirtMinHeight;
            i = 31 * i + this.dirtMaxHeight;
            i = 31 * i + this.gravelSize;
            i = 31 * i + this.gravelCount;
            i = 31 * i + this.gravelMinHeight;
            i = 31 * i + this.gravelMaxHeight;
            i = 31 * i + this.graniteSize;
            i = 31 * i + this.graniteCount;
            i = 31 * i + this.graniteMinHeight;
            i = 31 * i + this.graniteMaxHeight;
            i = 31 * i + this.dioriteSize;
            i = 31 * i + this.dioriteCount;
            i = 31 * i + this.dioriteMinHeight;
            i = 31 * i + this.dioriteMaxHeight;
            i = 31 * i + this.andesiteSize;
            i = 31 * i + this.andesiteCount;
            i = 31 * i + this.andesiteMinHeight;
            i = 31 * i + this.andesiteMaxHeight;
            i = 31 * i + this.coalSize;
            i = 31 * i + this.coalCount;
            i = 31 * i + this.coalMinHeight;
            i = 31 * i + this.coalMaxHeight;
            i = 31 * i + this.ironSize;
            i = 31 * i + this.ironCount;
            i = 31 * i + this.ironMinHeight;
            i = 31 * i + this.ironMaxHeight;
            i = 31 * i + this.goldSize;
            i = 31 * i + this.goldCount;
            i = 31 * i + this.goldMinHeight;
            i = 31 * i + this.goldMaxHeight;
            i = 31 * i + this.redstoneSize;
            i = 31 * i + this.redstoneCount;
            i = 31 * i + this.redstoneMinHeight;
            i = 31 * i + this.redstoneMaxHeight;
            i = 31 * i + this.diamondSize;
            i = 31 * i + this.diamondCount;
            i = 31 * i + this.diamondMinHeight;
            i = 31 * i + this.diamondMaxHeight;
            i = 31 * i + this.lapisSize;
            i = 31 * i + this.lapisCount;
            i = 31 * i + this.lapisCenterHeight;
            i = 31 * i + this.lapisSpread;
            return i;
        }

        public ChunkProviderSettings func_177864_b()
        {
            return new ChunkProviderSettings(this);
        }
    }

    public static class Serializer implements JsonDeserializer<ChunkProviderSettings.Factory>, JsonSerializer<ChunkProviderSettings.Factory>
    {
        public ChunkProviderSettings.Factory deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            ChunkProviderSettings.Factory chunkprovidersettings$factory = new ChunkProviderSettings.Factory();

            try
            {
                chunkprovidersettings$factory.coordinateScale = JsonUtils.getFloat(jsonobject, "coordinateScale", chunkprovidersettings$factory.coordinateScale);
                chunkprovidersettings$factory.heightScale = JsonUtils.getFloat(jsonobject, "heightScale", chunkprovidersettings$factory.heightScale);
                chunkprovidersettings$factory.lowerLimitScale = JsonUtils.getFloat(jsonobject, "lowerLimitScale", chunkprovidersettings$factory.lowerLimitScale);
                chunkprovidersettings$factory.upperLimitScale = JsonUtils.getFloat(jsonobject, "upperLimitScale", chunkprovidersettings$factory.upperLimitScale);
                chunkprovidersettings$factory.depthNoiseScaleX = JsonUtils.getFloat(jsonobject, "depthNoiseScaleX", chunkprovidersettings$factory.depthNoiseScaleX);
                chunkprovidersettings$factory.depthNoiseScaleZ = JsonUtils.getFloat(jsonobject, "depthNoiseScaleZ", chunkprovidersettings$factory.depthNoiseScaleZ);
                chunkprovidersettings$factory.depthNoiseScaleExponent = JsonUtils.getFloat(jsonobject, "depthNoiseScaleExponent", chunkprovidersettings$factory.depthNoiseScaleExponent);
                chunkprovidersettings$factory.mainNoiseScaleX = JsonUtils.getFloat(jsonobject, "mainNoiseScaleX", chunkprovidersettings$factory.mainNoiseScaleX);
                chunkprovidersettings$factory.mainNoiseScaleY = JsonUtils.getFloat(jsonobject, "mainNoiseScaleY", chunkprovidersettings$factory.mainNoiseScaleY);
                chunkprovidersettings$factory.mainNoiseScaleZ = JsonUtils.getFloat(jsonobject, "mainNoiseScaleZ", chunkprovidersettings$factory.mainNoiseScaleZ);
                chunkprovidersettings$factory.baseSize = JsonUtils.getFloat(jsonobject, "baseSize", chunkprovidersettings$factory.baseSize);
                chunkprovidersettings$factory.stretchY = JsonUtils.getFloat(jsonobject, "stretchY", chunkprovidersettings$factory.stretchY);
                chunkprovidersettings$factory.biomeDepthWeight = JsonUtils.getFloat(jsonobject, "biomeDepthWeight", chunkprovidersettings$factory.biomeDepthWeight);
                chunkprovidersettings$factory.biomeDepthOffset = JsonUtils.getFloat(jsonobject, "biomeDepthOffset", chunkprovidersettings$factory.biomeDepthOffset);
                chunkprovidersettings$factory.biomeScaleWeight = JsonUtils.getFloat(jsonobject, "biomeScaleWeight", chunkprovidersettings$factory.biomeScaleWeight);
                chunkprovidersettings$factory.biomeScaleOffset = JsonUtils.getFloat(jsonobject, "biomeScaleOffset", chunkprovidersettings$factory.biomeScaleOffset);
                chunkprovidersettings$factory.seaLevel = JsonUtils.getInt(jsonobject, "seaLevel", chunkprovidersettings$factory.seaLevel);
                chunkprovidersettings$factory.useCaves = JsonUtils.getBoolean(jsonobject, "useCaves", chunkprovidersettings$factory.useCaves);
                chunkprovidersettings$factory.useDungeons = JsonUtils.getBoolean(jsonobject, "useDungeons", chunkprovidersettings$factory.useDungeons);
                chunkprovidersettings$factory.dungeonChance = JsonUtils.getInt(jsonobject, "dungeonChance", chunkprovidersettings$factory.dungeonChance);
                chunkprovidersettings$factory.useStrongholds = JsonUtils.getBoolean(jsonobject, "useStrongholds", chunkprovidersettings$factory.useStrongholds);
                chunkprovidersettings$factory.useVillages = JsonUtils.getBoolean(jsonobject, "useVillages", chunkprovidersettings$factory.useVillages);
                chunkprovidersettings$factory.useMineShafts = JsonUtils.getBoolean(jsonobject, "useMineShafts", chunkprovidersettings$factory.useMineShafts);
                chunkprovidersettings$factory.useTemples = JsonUtils.getBoolean(jsonobject, "useTemples", chunkprovidersettings$factory.useTemples);
                chunkprovidersettings$factory.useMonuments = JsonUtils.getBoolean(jsonobject, "useMonuments", chunkprovidersettings$factory.useMonuments);
                chunkprovidersettings$factory.useRavines = JsonUtils.getBoolean(jsonobject, "useRavines", chunkprovidersettings$factory.useRavines);
                chunkprovidersettings$factory.useWaterLakes = JsonUtils.getBoolean(jsonobject, "useWaterLakes", chunkprovidersettings$factory.useWaterLakes);
                chunkprovidersettings$factory.waterLakeChance = JsonUtils.getInt(jsonobject, "waterLakeChance", chunkprovidersettings$factory.waterLakeChance);
                chunkprovidersettings$factory.useLavaLakes = JsonUtils.getBoolean(jsonobject, "useLavaLakes", chunkprovidersettings$factory.useLavaLakes);
                chunkprovidersettings$factory.lavaLakeChance = JsonUtils.getInt(jsonobject, "lavaLakeChance", chunkprovidersettings$factory.lavaLakeChance);
                chunkprovidersettings$factory.useLavaOceans = JsonUtils.getBoolean(jsonobject, "useLavaOceans", chunkprovidersettings$factory.useLavaOceans);
                chunkprovidersettings$factory.fixedBiome = JsonUtils.getInt(jsonobject, "fixedBiome", chunkprovidersettings$factory.fixedBiome);

                if (chunkprovidersettings$factory.fixedBiome < 38 && chunkprovidersettings$factory.fixedBiome >= -1)
                {
                    if (chunkprovidersettings$factory.fixedBiome >= BiomeGenBase.hell.biomeID)
                    {
                        chunkprovidersettings$factory.fixedBiome += 2;
                    }
                }
                else
                {
                    chunkprovidersettings$factory.fixedBiome = -1;
                }

                chunkprovidersettings$factory.biomeSize = JsonUtils.getInt(jsonobject, "biomeSize", chunkprovidersettings$factory.biomeSize);
                chunkprovidersettings$factory.riverSize = JsonUtils.getInt(jsonobject, "riverSize", chunkprovidersettings$factory.riverSize);
                chunkprovidersettings$factory.dirtSize = JsonUtils.getInt(jsonobject, "dirtSize", chunkprovidersettings$factory.dirtSize);
                chunkprovidersettings$factory.dirtCount = JsonUtils.getInt(jsonobject, "dirtCount", chunkprovidersettings$factory.dirtCount);
                chunkprovidersettings$factory.dirtMinHeight = JsonUtils.getInt(jsonobject, "dirtMinHeight", chunkprovidersettings$factory.dirtMinHeight);
                chunkprovidersettings$factory.dirtMaxHeight = JsonUtils.getInt(jsonobject, "dirtMaxHeight", chunkprovidersettings$factory.dirtMaxHeight);
                chunkprovidersettings$factory.gravelSize = JsonUtils.getInt(jsonobject, "gravelSize", chunkprovidersettings$factory.gravelSize);
                chunkprovidersettings$factory.gravelCount = JsonUtils.getInt(jsonobject, "gravelCount", chunkprovidersettings$factory.gravelCount);
                chunkprovidersettings$factory.gravelMinHeight = JsonUtils.getInt(jsonobject, "gravelMinHeight", chunkprovidersettings$factory.gravelMinHeight);
                chunkprovidersettings$factory.gravelMaxHeight = JsonUtils.getInt(jsonobject, "gravelMaxHeight", chunkprovidersettings$factory.gravelMaxHeight);
                chunkprovidersettings$factory.graniteSize = JsonUtils.getInt(jsonobject, "graniteSize", chunkprovidersettings$factory.graniteSize);
                chunkprovidersettings$factory.graniteCount = JsonUtils.getInt(jsonobject, "graniteCount", chunkprovidersettings$factory.graniteCount);
                chunkprovidersettings$factory.graniteMinHeight = JsonUtils.getInt(jsonobject, "graniteMinHeight", chunkprovidersettings$factory.graniteMinHeight);
                chunkprovidersettings$factory.graniteMaxHeight = JsonUtils.getInt(jsonobject, "graniteMaxHeight", chunkprovidersettings$factory.graniteMaxHeight);
                chunkprovidersettings$factory.dioriteSize = JsonUtils.getInt(jsonobject, "dioriteSize", chunkprovidersettings$factory.dioriteSize);
                chunkprovidersettings$factory.dioriteCount = JsonUtils.getInt(jsonobject, "dioriteCount", chunkprovidersettings$factory.dioriteCount);
                chunkprovidersettings$factory.dioriteMinHeight = JsonUtils.getInt(jsonobject, "dioriteMinHeight", chunkprovidersettings$factory.dioriteMinHeight);
                chunkprovidersettings$factory.dioriteMaxHeight = JsonUtils.getInt(jsonobject, "dioriteMaxHeight", chunkprovidersettings$factory.dioriteMaxHeight);
                chunkprovidersettings$factory.andesiteSize = JsonUtils.getInt(jsonobject, "andesiteSize", chunkprovidersettings$factory.andesiteSize);
                chunkprovidersettings$factory.andesiteCount = JsonUtils.getInt(jsonobject, "andesiteCount", chunkprovidersettings$factory.andesiteCount);
                chunkprovidersettings$factory.andesiteMinHeight = JsonUtils.getInt(jsonobject, "andesiteMinHeight", chunkprovidersettings$factory.andesiteMinHeight);
                chunkprovidersettings$factory.andesiteMaxHeight = JsonUtils.getInt(jsonobject, "andesiteMaxHeight", chunkprovidersettings$factory.andesiteMaxHeight);
                chunkprovidersettings$factory.coalSize = JsonUtils.getInt(jsonobject, "coalSize", chunkprovidersettings$factory.coalSize);
                chunkprovidersettings$factory.coalCount = JsonUtils.getInt(jsonobject, "coalCount", chunkprovidersettings$factory.coalCount);
                chunkprovidersettings$factory.coalMinHeight = JsonUtils.getInt(jsonobject, "coalMinHeight", chunkprovidersettings$factory.coalMinHeight);
                chunkprovidersettings$factory.coalMaxHeight = JsonUtils.getInt(jsonobject, "coalMaxHeight", chunkprovidersettings$factory.coalMaxHeight);
                chunkprovidersettings$factory.ironSize = JsonUtils.getInt(jsonobject, "ironSize", chunkprovidersettings$factory.ironSize);
                chunkprovidersettings$factory.ironCount = JsonUtils.getInt(jsonobject, "ironCount", chunkprovidersettings$factory.ironCount);
                chunkprovidersettings$factory.ironMinHeight = JsonUtils.getInt(jsonobject, "ironMinHeight", chunkprovidersettings$factory.ironMinHeight);
                chunkprovidersettings$factory.ironMaxHeight = JsonUtils.getInt(jsonobject, "ironMaxHeight", chunkprovidersettings$factory.ironMaxHeight);
                chunkprovidersettings$factory.goldSize = JsonUtils.getInt(jsonobject, "goldSize", chunkprovidersettings$factory.goldSize);
                chunkprovidersettings$factory.goldCount = JsonUtils.getInt(jsonobject, "goldCount", chunkprovidersettings$factory.goldCount);
                chunkprovidersettings$factory.goldMinHeight = JsonUtils.getInt(jsonobject, "goldMinHeight", chunkprovidersettings$factory.goldMinHeight);
                chunkprovidersettings$factory.goldMaxHeight = JsonUtils.getInt(jsonobject, "goldMaxHeight", chunkprovidersettings$factory.goldMaxHeight);
                chunkprovidersettings$factory.redstoneSize = JsonUtils.getInt(jsonobject, "redstoneSize", chunkprovidersettings$factory.redstoneSize);
                chunkprovidersettings$factory.redstoneCount = JsonUtils.getInt(jsonobject, "redstoneCount", chunkprovidersettings$factory.redstoneCount);
                chunkprovidersettings$factory.redstoneMinHeight = JsonUtils.getInt(jsonobject, "redstoneMinHeight", chunkprovidersettings$factory.redstoneMinHeight);
                chunkprovidersettings$factory.redstoneMaxHeight = JsonUtils.getInt(jsonobject, "redstoneMaxHeight", chunkprovidersettings$factory.redstoneMaxHeight);
                chunkprovidersettings$factory.diamondSize = JsonUtils.getInt(jsonobject, "diamondSize", chunkprovidersettings$factory.diamondSize);
                chunkprovidersettings$factory.diamondCount = JsonUtils.getInt(jsonobject, "diamondCount", chunkprovidersettings$factory.diamondCount);
                chunkprovidersettings$factory.diamondMinHeight = JsonUtils.getInt(jsonobject, "diamondMinHeight", chunkprovidersettings$factory.diamondMinHeight);
                chunkprovidersettings$factory.diamondMaxHeight = JsonUtils.getInt(jsonobject, "diamondMaxHeight", chunkprovidersettings$factory.diamondMaxHeight);
                chunkprovidersettings$factory.lapisSize = JsonUtils.getInt(jsonobject, "lapisSize", chunkprovidersettings$factory.lapisSize);
                chunkprovidersettings$factory.lapisCount = JsonUtils.getInt(jsonobject, "lapisCount", chunkprovidersettings$factory.lapisCount);
                chunkprovidersettings$factory.lapisCenterHeight = JsonUtils.getInt(jsonobject, "lapisCenterHeight", chunkprovidersettings$factory.lapisCenterHeight);
                chunkprovidersettings$factory.lapisSpread = JsonUtils.getInt(jsonobject, "lapisSpread", chunkprovidersettings$factory.lapisSpread);
            }
            catch (Exception var7)
            {
                ;
            }

            return chunkprovidersettings$factory;
        }

        public JsonElement serialize(ChunkProviderSettings.Factory p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("coordinateScale", (Number)Float.valueOf(p_serialize_1_.coordinateScale));
            jsonobject.addProperty("heightScale", (Number)Float.valueOf(p_serialize_1_.heightScale));
            jsonobject.addProperty("lowerLimitScale", (Number)Float.valueOf(p_serialize_1_.lowerLimitScale));
            jsonobject.addProperty("upperLimitScale", (Number)Float.valueOf(p_serialize_1_.upperLimitScale));
            jsonobject.addProperty("depthNoiseScaleX", (Number)Float.valueOf(p_serialize_1_.depthNoiseScaleX));
            jsonobject.addProperty("depthNoiseScaleZ", (Number)Float.valueOf(p_serialize_1_.depthNoiseScaleZ));
            jsonobject.addProperty("depthNoiseScaleExponent", (Number)Float.valueOf(p_serialize_1_.depthNoiseScaleExponent));
            jsonobject.addProperty("mainNoiseScaleX", (Number)Float.valueOf(p_serialize_1_.mainNoiseScaleX));
            jsonobject.addProperty("mainNoiseScaleY", (Number)Float.valueOf(p_serialize_1_.mainNoiseScaleY));
            jsonobject.addProperty("mainNoiseScaleZ", (Number)Float.valueOf(p_serialize_1_.mainNoiseScaleZ));
            jsonobject.addProperty("baseSize", (Number)Float.valueOf(p_serialize_1_.baseSize));
            jsonobject.addProperty("stretchY", (Number)Float.valueOf(p_serialize_1_.stretchY));
            jsonobject.addProperty("biomeDepthWeight", (Number)Float.valueOf(p_serialize_1_.biomeDepthWeight));
            jsonobject.addProperty("biomeDepthOffset", (Number)Float.valueOf(p_serialize_1_.biomeDepthOffset));
            jsonobject.addProperty("biomeScaleWeight", (Number)Float.valueOf(p_serialize_1_.biomeScaleWeight));
            jsonobject.addProperty("biomeScaleOffset", (Number)Float.valueOf(p_serialize_1_.biomeScaleOffset));
            jsonobject.addProperty("seaLevel", (Number)Integer.valueOf(p_serialize_1_.seaLevel));
            jsonobject.addProperty("useCaves", Boolean.valueOf(p_serialize_1_.useCaves));
            jsonobject.addProperty("useDungeons", Boolean.valueOf(p_serialize_1_.useDungeons));
            jsonobject.addProperty("dungeonChance", (Number)Integer.valueOf(p_serialize_1_.dungeonChance));
            jsonobject.addProperty("useStrongholds", Boolean.valueOf(p_serialize_1_.useStrongholds));
            jsonobject.addProperty("useVillages", Boolean.valueOf(p_serialize_1_.useVillages));
            jsonobject.addProperty("useMineShafts", Boolean.valueOf(p_serialize_1_.useMineShafts));
            jsonobject.addProperty("useTemples", Boolean.valueOf(p_serialize_1_.useTemples));
            jsonobject.addProperty("useMonuments", Boolean.valueOf(p_serialize_1_.useMonuments));
            jsonobject.addProperty("useRavines", Boolean.valueOf(p_serialize_1_.useRavines));
            jsonobject.addProperty("useWaterLakes", Boolean.valueOf(p_serialize_1_.useWaterLakes));
            jsonobject.addProperty("waterLakeChance", (Number)Integer.valueOf(p_serialize_1_.waterLakeChance));
            jsonobject.addProperty("useLavaLakes", Boolean.valueOf(p_serialize_1_.useLavaLakes));
            jsonobject.addProperty("lavaLakeChance", (Number)Integer.valueOf(p_serialize_1_.lavaLakeChance));
            jsonobject.addProperty("useLavaOceans", Boolean.valueOf(p_serialize_1_.useLavaOceans));
            jsonobject.addProperty("fixedBiome", (Number)Integer.valueOf(p_serialize_1_.fixedBiome));
            jsonobject.addProperty("biomeSize", (Number)Integer.valueOf(p_serialize_1_.biomeSize));
            jsonobject.addProperty("riverSize", (Number)Integer.valueOf(p_serialize_1_.riverSize));
            jsonobject.addProperty("dirtSize", (Number)Integer.valueOf(p_serialize_1_.dirtSize));
            jsonobject.addProperty("dirtCount", (Number)Integer.valueOf(p_serialize_1_.dirtCount));
            jsonobject.addProperty("dirtMinHeight", (Number)Integer.valueOf(p_serialize_1_.dirtMinHeight));
            jsonobject.addProperty("dirtMaxHeight", (Number)Integer.valueOf(p_serialize_1_.dirtMaxHeight));
            jsonobject.addProperty("gravelSize", (Number)Integer.valueOf(p_serialize_1_.gravelSize));
            jsonobject.addProperty("gravelCount", (Number)Integer.valueOf(p_serialize_1_.gravelCount));
            jsonobject.addProperty("gravelMinHeight", (Number)Integer.valueOf(p_serialize_1_.gravelMinHeight));
            jsonobject.addProperty("gravelMaxHeight", (Number)Integer.valueOf(p_serialize_1_.gravelMaxHeight));
            jsonobject.addProperty("graniteSize", (Number)Integer.valueOf(p_serialize_1_.graniteSize));
            jsonobject.addProperty("graniteCount", (Number)Integer.valueOf(p_serialize_1_.graniteCount));
            jsonobject.addProperty("graniteMinHeight", (Number)Integer.valueOf(p_serialize_1_.graniteMinHeight));
            jsonobject.addProperty("graniteMaxHeight", (Number)Integer.valueOf(p_serialize_1_.graniteMaxHeight));
            jsonobject.addProperty("dioriteSize", (Number)Integer.valueOf(p_serialize_1_.dioriteSize));
            jsonobject.addProperty("dioriteCount", (Number)Integer.valueOf(p_serialize_1_.dioriteCount));
            jsonobject.addProperty("dioriteMinHeight", (Number)Integer.valueOf(p_serialize_1_.dioriteMinHeight));
            jsonobject.addProperty("dioriteMaxHeight", (Number)Integer.valueOf(p_serialize_1_.dioriteMaxHeight));
            jsonobject.addProperty("andesiteSize", (Number)Integer.valueOf(p_serialize_1_.andesiteSize));
            jsonobject.addProperty("andesiteCount", (Number)Integer.valueOf(p_serialize_1_.andesiteCount));
            jsonobject.addProperty("andesiteMinHeight", (Number)Integer.valueOf(p_serialize_1_.andesiteMinHeight));
            jsonobject.addProperty("andesiteMaxHeight", (Number)Integer.valueOf(p_serialize_1_.andesiteMaxHeight));
            jsonobject.addProperty("coalSize", (Number)Integer.valueOf(p_serialize_1_.coalSize));
            jsonobject.addProperty("coalCount", (Number)Integer.valueOf(p_serialize_1_.coalCount));
            jsonobject.addProperty("coalMinHeight", (Number)Integer.valueOf(p_serialize_1_.coalMinHeight));
            jsonobject.addProperty("coalMaxHeight", (Number)Integer.valueOf(p_serialize_1_.coalMaxHeight));
            jsonobject.addProperty("ironSize", (Number)Integer.valueOf(p_serialize_1_.ironSize));
            jsonobject.addProperty("ironCount", (Number)Integer.valueOf(p_serialize_1_.ironCount));
            jsonobject.addProperty("ironMinHeight", (Number)Integer.valueOf(p_serialize_1_.ironMinHeight));
            jsonobject.addProperty("ironMaxHeight", (Number)Integer.valueOf(p_serialize_1_.ironMaxHeight));
            jsonobject.addProperty("goldSize", (Number)Integer.valueOf(p_serialize_1_.goldSize));
            jsonobject.addProperty("goldCount", (Number)Integer.valueOf(p_serialize_1_.goldCount));
            jsonobject.addProperty("goldMinHeight", (Number)Integer.valueOf(p_serialize_1_.goldMinHeight));
            jsonobject.addProperty("goldMaxHeight", (Number)Integer.valueOf(p_serialize_1_.goldMaxHeight));
            jsonobject.addProperty("redstoneSize", (Number)Integer.valueOf(p_serialize_1_.redstoneSize));
            jsonobject.addProperty("redstoneCount", (Number)Integer.valueOf(p_serialize_1_.redstoneCount));
            jsonobject.addProperty("redstoneMinHeight", (Number)Integer.valueOf(p_serialize_1_.redstoneMinHeight));
            jsonobject.addProperty("redstoneMaxHeight", (Number)Integer.valueOf(p_serialize_1_.redstoneMaxHeight));
            jsonobject.addProperty("diamondSize", (Number)Integer.valueOf(p_serialize_1_.diamondSize));
            jsonobject.addProperty("diamondCount", (Number)Integer.valueOf(p_serialize_1_.diamondCount));
            jsonobject.addProperty("diamondMinHeight", (Number)Integer.valueOf(p_serialize_1_.diamondMinHeight));
            jsonobject.addProperty("diamondMaxHeight", (Number)Integer.valueOf(p_serialize_1_.diamondMaxHeight));
            jsonobject.addProperty("lapisSize", (Number)Integer.valueOf(p_serialize_1_.lapisSize));
            jsonobject.addProperty("lapisCount", (Number)Integer.valueOf(p_serialize_1_.lapisCount));
            jsonobject.addProperty("lapisCenterHeight", (Number)Integer.valueOf(p_serialize_1_.lapisCenterHeight));
            jsonobject.addProperty("lapisSpread", (Number)Integer.valueOf(p_serialize_1_.lapisSpread));
            return jsonobject;
        }
    }
}
