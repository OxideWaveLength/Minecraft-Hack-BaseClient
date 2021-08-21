package net.minecraft.entity.ai.attributes;

import net.minecraft.util.MathHelper;

public class RangedAttribute extends BaseAttribute {
	private final double minimumValue;
	private final double maximumValue;
	private String description;

	public RangedAttribute(IAttribute p_i45891_1_, String unlocalizedNameIn, double defaultValue, double minimumValueIn, double maximumValueIn) {
		super(p_i45891_1_, unlocalizedNameIn, defaultValue);
		this.minimumValue = minimumValueIn;
		this.maximumValue = maximumValueIn;

		if (minimumValueIn > maximumValueIn) {
			throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
		} else if (defaultValue < minimumValueIn) {
			throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
		} else if (defaultValue > maximumValueIn) {
			throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
		}
	}

	public RangedAttribute setDescription(String descriptionIn) {
		this.description = descriptionIn;
		return this;
	}

	public String getDescription() {
		return this.description;
	}

	public double clampValue(double p_111109_1_) {
		p_111109_1_ = MathHelper.clamp_double(p_111109_1_, this.minimumValue, this.maximumValue);
		return p_111109_1_;
	}
}
