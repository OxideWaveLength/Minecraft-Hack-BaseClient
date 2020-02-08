package net.minecraft.world.gen;

import java.util.Random;

import net.minecraft.util.MathHelper;

public class NoiseGeneratorOctaves extends NoiseGenerator {
	/**
	 * Collection of noise generation functions. Output is combined to produce
	 * different octaves of noise.
	 */
	private NoiseGeneratorImproved[] generatorCollection;
	private int octaves;

	public NoiseGeneratorOctaves(Random p_i2111_1_, int p_i2111_2_) {
		this.octaves = p_i2111_2_;
		this.generatorCollection = new NoiseGeneratorImproved[p_i2111_2_];

		for (int i = 0; i < p_i2111_2_; ++i) {
			this.generatorCollection[i] = new NoiseGeneratorImproved(p_i2111_1_);
		}
	}

	/**
	 * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect)
	 * (pars5,6,7=x,y,zArraySize),(pars8,10,12 = x,y,z noiseScale)
	 */
	public double[] generateNoiseOctaves(double[] p_76304_1_, int p_76304_2_, int p_76304_3_, int p_76304_4_, int p_76304_5_, int p_76304_6_, int p_76304_7_, double p_76304_8_, double p_76304_10_, double p_76304_12_) {
		if (p_76304_1_ == null) {
			p_76304_1_ = new double[p_76304_5_ * p_76304_6_ * p_76304_7_];
		} else {
			for (int i = 0; i < p_76304_1_.length; ++i) {
				p_76304_1_[i] = 0.0D;
			}
		}

		double d3 = 1.0D;

		for (int j = 0; j < this.octaves; ++j) {
			double d0 = (double) p_76304_2_ * d3 * p_76304_8_;
			double d1 = (double) p_76304_3_ * d3 * p_76304_10_;
			double d2 = (double) p_76304_4_ * d3 * p_76304_12_;
			long k = MathHelper.floor_double_long(d0);
			long l = MathHelper.floor_double_long(d2);
			d0 = d0 - (double) k;
			d2 = d2 - (double) l;
			k = k % 16777216L;
			l = l % 16777216L;
			d0 = d0 + (double) k;
			d2 = d2 + (double) l;
			this.generatorCollection[j].populateNoiseArray(p_76304_1_, d0, d1, d2, p_76304_5_, p_76304_6_, p_76304_7_, p_76304_8_ * d3, p_76304_10_ * d3, p_76304_12_ * d3, d3);
			d3 /= 2.0D;
		}

		return p_76304_1_;
	}

	/**
	 * Bouncer function to the main one with some default arguments.
	 */
	public double[] generateNoiseOctaves(double[] p_76305_1_, int p_76305_2_, int p_76305_3_, int p_76305_4_, int p_76305_5_, double p_76305_6_, double p_76305_8_, double p_76305_10_) {
		return this.generateNoiseOctaves(p_76305_1_, p_76305_2_, 10, p_76305_3_, p_76305_4_, 1, p_76305_5_, p_76305_6_, 1.0D, p_76305_8_);
	}
}
