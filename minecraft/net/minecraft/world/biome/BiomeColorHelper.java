package net.minecraft.world.biome;

import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BiomeColorHelper
{
    private static final BiomeColorHelper.ColorResolver field_180291_a = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(BiomeGenBase p_180283_1_, BlockPos blockPosition)
        {
            return p_180283_1_.getGrassColorAtPos(blockPosition);
        }
    };
    private static final BiomeColorHelper.ColorResolver field_180289_b = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(BiomeGenBase p_180283_1_, BlockPos blockPosition)
        {
            return p_180283_1_.getFoliageColorAtPos(blockPosition);
        }
    };
    private static final BiomeColorHelper.ColorResolver field_180290_c = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(BiomeGenBase p_180283_1_, BlockPos blockPosition)
        {
            return p_180283_1_.waterColorMultiplier;
        }
    };

    private static int func_180285_a(IBlockAccess p_180285_0_, BlockPos p_180285_1_, BiomeColorHelper.ColorResolver p_180285_2_)
    {
        int i = 0;
        int j = 0;
        int k = 0;

        for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(p_180285_1_.add(-1, 0, -1), p_180285_1_.add(1, 0, 1)))
        {
            int l = p_180285_2_.getColorAtPos(p_180285_0_.getBiomeGenForCoords(blockpos$mutableblockpos), blockpos$mutableblockpos);
            i += (l & 16711680) >> 16;
            j += (l & 65280) >> 8;
            k += l & 255;
        }

        return (i / 9 & 255) << 16 | (j / 9 & 255) << 8 | k / 9 & 255;
    }

    public static int getGrassColorAtPos(IBlockAccess p_180286_0_, BlockPos p_180286_1_)
    {
        return func_180285_a(p_180286_0_, p_180286_1_, field_180291_a);
    }

    public static int getFoliageColorAtPos(IBlockAccess p_180287_0_, BlockPos p_180287_1_)
    {
        return func_180285_a(p_180287_0_, p_180287_1_, field_180289_b);
    }

    public static int getWaterColorAtPos(IBlockAccess p_180288_0_, BlockPos p_180288_1_)
    {
        return func_180285_a(p_180288_0_, p_180288_1_, field_180290_c);
    }

    interface ColorResolver
    {
        int getColorAtPos(BiomeGenBase p_180283_1_, BlockPos blockPosition);
    }
}
