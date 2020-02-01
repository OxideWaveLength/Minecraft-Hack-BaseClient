package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenHellLava extends WorldGenerator
{
    private final Block field_150553_a;
    private final boolean field_94524_b;

    public WorldGenHellLava(Block p_i45453_1_, boolean p_i45453_2_)
    {
        this.field_150553_a = p_i45453_1_;
        this.field_94524_b = p_i45453_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (worldIn.getBlockState(position.up()).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.air && worldIn.getBlockState(position).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else
        {
            int i = 0;

            if (worldIn.getBlockState(position.west()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.east()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.north()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.south()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.down()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            int j = 0;

            if (worldIn.isAirBlock(position.west()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.east()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.north()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.south()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.down()))
            {
                ++j;
            }

            if (!this.field_94524_b && i == 4 && j == 1 || i == 5)
            {
                worldIn.setBlockState(position, this.field_150553_a.getDefaultState(), 2);
                worldIn.forceBlockUpdateTick(this.field_150553_a, position, rand);
            }

            return true;
        }
    }
}
