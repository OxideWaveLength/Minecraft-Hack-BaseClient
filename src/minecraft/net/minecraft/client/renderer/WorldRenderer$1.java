package net.minecraft.client.renderer;

import java.util.Comparator;

import com.google.common.primitives.Floats;

class WorldRenderer$1 implements Comparator {
	final float[] field_181659_a;
	final WorldRenderer field_181660_b;

	WorldRenderer$1(WorldRenderer p_i46500_1_, float[] p_i46500_2_) {
		this.field_181660_b = p_i46500_1_;
		this.field_181659_a = p_i46500_2_;
	}

	public int compare(Integer p_compare_1_, Integer p_compare_2_) {
		return Floats.compare(this.field_181659_a[p_compare_2_.intValue()], this.field_181659_a[p_compare_1_.intValue()]);
	}

	public int compare(Object p_compare_1_, Object p_compare_2_) {
		return this.compare((Integer) p_compare_1_, (Integer) p_compare_2_);
	}
}
