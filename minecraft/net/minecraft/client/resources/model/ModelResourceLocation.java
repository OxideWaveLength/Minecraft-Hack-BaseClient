package net.minecraft.client.resources.model;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.ResourceLocation;

public class ModelResourceLocation extends ResourceLocation {
	private final String variant;

	protected ModelResourceLocation(int p_i46078_1_, String... p_i46078_2_) {
		super(0, new String[] { p_i46078_2_[0], p_i46078_2_[1] });
		this.variant = StringUtils.isEmpty(p_i46078_2_[2]) ? "normal" : p_i46078_2_[2].toLowerCase();
	}

	public ModelResourceLocation(String p_i46079_1_) {
		this(0, parsePathString(p_i46079_1_));
	}

	public ModelResourceLocation(ResourceLocation p_i46080_1_, String p_i46080_2_) {
		this(p_i46080_1_.toString(), p_i46080_2_);
	}

	public ModelResourceLocation(String p_i46081_1_, String p_i46081_2_) {
		this(0, parsePathString(p_i46081_1_ + '#' + (p_i46081_2_ == null ? "normal" : p_i46081_2_)));
	}

	protected static String[] parsePathString(String p_177517_0_) {
		String[] astring = new String[] { null, p_177517_0_, null };
		int i = p_177517_0_.indexOf(35);
		String s = p_177517_0_;

		if (i >= 0) {
			astring[2] = p_177517_0_.substring(i + 1, p_177517_0_.length());

			if (i > 1) {
				s = p_177517_0_.substring(0, i);
			}
		}

		System.arraycopy(ResourceLocation.splitObjectName(s), 0, astring, 0, 2);
		return astring;
	}

	public String getVariant() {
		return this.variant;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (p_equals_1_ instanceof ModelResourceLocation && super.equals(p_equals_1_)) {
			ModelResourceLocation modelresourcelocation = (ModelResourceLocation) p_equals_1_;
			return this.variant.equals(modelresourcelocation.variant);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return 31 * super.hashCode() + this.variant.hashCode();
	}

	public String toString() {
		return super.toString() + '#' + this.variant;
	}
}
