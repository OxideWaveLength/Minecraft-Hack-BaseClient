package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBlockBlob extends WorldGenerator
{
    private final Block field_150545_a;
    private final int field_150544_b;

    public WorldGenBlockBlob(Block p_i45450_1_, int p_i45450_2_)
    {
        super(false);
        this.field_150545_a = p_i45450_1_;
        this.field_150544_b = p_i45450_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        while (true)
        {
            label0:
            {
                if (position.getY() > 3)
                {
                    if (worldIn.isAirBlock(position.down()))
                    {
                        break label0;
                    }

                    Block block = worldIn.getBlockState(position.down()).getBlock();

                    if (block != Blocks.grass && block != Blocks.dirt && block != Blocks.stone)
                    {
                        break label0;
                    }
                }

                if (position.getY() <= 3)
                {
                    return false;
                }

                int i1 = this.field_150544_b;

                for (int i = 0; i1 >= 0 && i < 3; ++i)
                {
                    int j = i1 + rand.nextInt(2);
                    int k = i1 + rand.nextInt(2);
                    int l = i1 + rand.nextInt(2);
                    float f = (float)(j + k + l) * 0.333F + 0.5F;

                    for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l)))
                    {
                        if (blockpos.distanceSq(position) <= (double)(f * f))
                        {
                            worldIn.setBlockState(blockpos, this.field_150545_a.getDefaultState(), 4);
                        }
                    }

                    position = position.add(-(i1 + 1) + rand.nextInt(2 + i1 * 2), 0 - rand.nextInt(2), -(i1 + 1) + rand.nextInt(2 + i1 * 2));
                }

                return true;
            }
            position = position.down();
        }
    }
}
