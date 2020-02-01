package net.minecraft.world.gen;

import com.google.common.base.Objects;
import java.util.Random;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenCaves extends MapGenBase
{
    protected void func_180703_a(long p_180703_1_, int p_180703_3_, int p_180703_4_, ChunkPrimer p_180703_5_, double p_180703_6_, double p_180703_8_, double p_180703_10_)
    {
        this.func_180702_a(p_180703_1_, p_180703_3_, p_180703_4_, p_180703_5_, p_180703_6_, p_180703_8_, p_180703_10_, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    protected void func_180702_a(long p_180702_1_, int p_180702_3_, int p_180702_4_, ChunkPrimer p_180702_5_, double p_180702_6_, double p_180702_8_, double p_180702_10_, float p_180702_12_, float p_180702_13_, float p_180702_14_, int p_180702_15_, int p_180702_16_, double p_180702_17_)
    {
        double d0 = (double)(p_180702_3_ * 16 + 8);
        double d1 = (double)(p_180702_4_ * 16 + 8);
        float f = 0.0F;
        float f1 = 0.0F;
        Random random = new Random(p_180702_1_);

        if (p_180702_16_ <= 0)
        {
            int i = this.range * 16 - 16;
            p_180702_16_ = i - random.nextInt(i / 4);
        }

        boolean flag2 = false;

        if (p_180702_15_ == -1)
        {
            p_180702_15_ = p_180702_16_ / 2;
            flag2 = true;
        }

        int j = random.nextInt(p_180702_16_ / 2) + p_180702_16_ / 4;

        for (boolean flag = random.nextInt(6) == 0; p_180702_15_ < p_180702_16_; ++p_180702_15_)
        {
            double d2 = 1.5D + (double)(MathHelper.sin((float)p_180702_15_ * (float)Math.PI / (float)p_180702_16_) * p_180702_12_ * 1.0F);
            double d3 = d2 * p_180702_17_;
            float f2 = MathHelper.cos(p_180702_14_);
            float f3 = MathHelper.sin(p_180702_14_);
            p_180702_6_ += (double)(MathHelper.cos(p_180702_13_) * f2);
            p_180702_8_ += (double)f3;
            p_180702_10_ += (double)(MathHelper.sin(p_180702_13_) * f2);

            if (flag)
            {
                p_180702_14_ = p_180702_14_ * 0.92F;
            }
            else
            {
                p_180702_14_ = p_180702_14_ * 0.7F;
            }

            p_180702_14_ = p_180702_14_ + f1 * 0.1F;
            p_180702_13_ += f * 0.1F;
            f1 = f1 * 0.9F;
            f = f * 0.75F;
            f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f = f + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!flag2 && p_180702_15_ == j && p_180702_12_ > 1.0F && p_180702_16_ > 0)
            {
                this.func_180702_a(random.nextLong(), p_180702_3_, p_180702_4_, p_180702_5_, p_180702_6_, p_180702_8_, p_180702_10_, random.nextFloat() * 0.5F + 0.5F, p_180702_13_ - ((float)Math.PI / 2F), p_180702_14_ / 3.0F, p_180702_15_, p_180702_16_, 1.0D);
                this.func_180702_a(random.nextLong(), p_180702_3_, p_180702_4_, p_180702_5_, p_180702_6_, p_180702_8_, p_180702_10_, random.nextFloat() * 0.5F + 0.5F, p_180702_13_ + ((float)Math.PI / 2F), p_180702_14_ / 3.0F, p_180702_15_, p_180702_16_, 1.0D);
                return;
            }

            if (flag2 || random.nextInt(4) != 0)
            {
                double d4 = p_180702_6_ - d0;
                double d5 = p_180702_10_ - d1;
                double d6 = (double)(p_180702_16_ - p_180702_15_);
                double d7 = (double)(p_180702_12_ + 2.0F + 16.0F);

                if (d4 * d4 + d5 * d5 - d6 * d6 > d7 * d7)
                {
                    return;
                }

                if (p_180702_6_ >= d0 - 16.0D - d2 * 2.0D && p_180702_10_ >= d1 - 16.0D - d2 * 2.0D && p_180702_6_ <= d0 + 16.0D + d2 * 2.0D && p_180702_10_ <= d1 + 16.0D + d2 * 2.0D)
                {
                    int k2 = MathHelper.floor_double(p_180702_6_ - d2) - p_180702_3_ * 16 - 1;
                    int k = MathHelper.floor_double(p_180702_6_ + d2) - p_180702_3_ * 16 + 1;
                    int l2 = MathHelper.floor_double(p_180702_8_ - d3) - 1;
                    int l = MathHelper.floor_double(p_180702_8_ + d3) + 1;
                    int i3 = MathHelper.floor_double(p_180702_10_ - d2) - p_180702_4_ * 16 - 1;
                    int i1 = MathHelper.floor_double(p_180702_10_ + d2) - p_180702_4_ * 16 + 1;

                    if (k2 < 0)
                    {
                        k2 = 0;
                    }

                    if (k > 16)
                    {
                        k = 16;
                    }

                    if (l2 < 1)
                    {
                        l2 = 1;
                    }

                    if (l > 248)
                    {
                        l = 248;
                    }

                    if (i3 < 0)
                    {
                        i3 = 0;
                    }

                    if (i1 > 16)
                    {
                        i1 = 16;
                    }

                    boolean flag3 = false;

                    for (int j1 = k2; !flag3 && j1 < k; ++j1)
                    {
                        for (int k1 = i3; !flag3 && k1 < i1; ++k1)
                        {
                            for (int l1 = l + 1; !flag3 && l1 >= l2 - 1; --l1)
                            {
                                if (l1 >= 0 && l1 < 256)
                                {
                                    IBlockState iblockstate = p_180702_5_.getBlockState(j1, l1, k1);

                                    if (iblockstate.getBlock() == Blocks.flowing_water || iblockstate.getBlock() == Blocks.water)
                                    {
                                        flag3 = true;
                                    }

                                    if (l1 != l2 - 1 && j1 != k2 && j1 != k - 1 && k1 != i3 && k1 != i1 - 1)
                                    {
                                        l1 = l2;
                                    }
                                }
                            }
                        }
                    }

                    if (!flag3)
                    {
                        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                        for (int j3 = k2; j3 < k; ++j3)
                        {
                            double d10 = ((double)(j3 + p_180702_3_ * 16) + 0.5D - p_180702_6_) / d2;

                            for (int i2 = i3; i2 < i1; ++i2)
                            {
                                double d8 = ((double)(i2 + p_180702_4_ * 16) + 0.5D - p_180702_10_) / d2;
                                boolean flag1 = false;

                                if (d10 * d10 + d8 * d8 < 1.0D)
                                {
                                    for (int j2 = l; j2 > l2; --j2)
                                    {
                                        double d9 = ((double)(j2 - 1) + 0.5D - p_180702_8_) / d3;

                                        if (d9 > -0.7D && d10 * d10 + d9 * d9 + d8 * d8 < 1.0D)
                                        {
                                            IBlockState iblockstate1 = p_180702_5_.getBlockState(j3, j2, i2);
                                            IBlockState iblockstate2 = (IBlockState)Objects.firstNonNull(p_180702_5_.getBlockState(j3, j2 + 1, i2), Blocks.air.getDefaultState());

                                            if (iblockstate1.getBlock() == Blocks.grass || iblockstate1.getBlock() == Blocks.mycelium)
                                            {
                                                flag1 = true;
                                            }

                                            if (this.func_175793_a(iblockstate1, iblockstate2))
                                            {
                                                if (j2 - 1 < 10)
                                                {
                                                    p_180702_5_.setBlockState(j3, j2, i2, Blocks.lava.getDefaultState());
                                                }
                                                else
                                                {
                                                    p_180702_5_.setBlockState(j3, j2, i2, Blocks.air.getDefaultState());

                                                    if (iblockstate2.getBlock() == Blocks.sand)
                                                    {
                                                        p_180702_5_.setBlockState(j3, j2 + 1, i2, iblockstate2.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ? Blocks.red_sandstone.getDefaultState() : Blocks.sandstone.getDefaultState());
                                                    }

                                                    if (flag1 && p_180702_5_.getBlockState(j3, j2 - 1, i2).getBlock() == Blocks.dirt)
                                                    {
                                                        blockpos$mutableblockpos.func_181079_c(j3 + p_180702_3_ * 16, 0, i2 + p_180702_4_ * 16);
                                                        p_180702_5_.setBlockState(j3, j2 - 1, i2, this.worldObj.getBiomeGenForCoords(blockpos$mutableblockpos).topBlock.getBlock().getDefaultState());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (flag2)
                        {
                            break;
                        }
                    }
                }
            }
        }
    }

    protected boolean func_175793_a(IBlockState p_175793_1_, IBlockState p_175793_2_)
    {
        return p_175793_1_.getBlock() == Blocks.stone ? true : (p_175793_1_.getBlock() == Blocks.dirt ? true : (p_175793_1_.getBlock() == Blocks.grass ? true : (p_175793_1_.getBlock() == Blocks.hardened_clay ? true : (p_175793_1_.getBlock() == Blocks.stained_hardened_clay ? true : (p_175793_1_.getBlock() == Blocks.sandstone ? true : (p_175793_1_.getBlock() == Blocks.red_sandstone ? true : (p_175793_1_.getBlock() == Blocks.mycelium ? true : (p_175793_1_.getBlock() == Blocks.snow_layer ? true : (p_175793_1_.getBlock() == Blocks.sand || p_175793_1_.getBlock() == Blocks.gravel) && p_175793_2_.getBlock().getMaterial() != Material.water))))))));
    }

    /**
     * Recursively called by generate()
     */
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn)
    {
        int i = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);

        if (this.rand.nextInt(7) != 0)
        {
            i = 0;
        }

        for (int j = 0; j < i; ++j)
        {
            double d0 = (double)(chunkX * 16 + this.rand.nextInt(16));
            double d1 = (double)this.rand.nextInt(this.rand.nextInt(120) + 8);
            double d2 = (double)(chunkZ * 16 + this.rand.nextInt(16));
            int k = 1;

            if (this.rand.nextInt(4) == 0)
            {
                this.func_180703_a(this.rand.nextLong(), p_180701_4_, p_180701_5_, chunkPrimerIn, d0, d1, d2);
                k += this.rand.nextInt(4);
            }

            for (int l = 0; l < k; ++l)
            {
                float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                if (this.rand.nextInt(10) == 0)
                {
                    f2 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.func_180702_a(this.rand.nextLong(), p_180701_4_, p_180701_5_, chunkPrimerIn, d0, d1, d2, f2, f, f1, 0, 0, 1.0D);
            }
        }
    }
}
