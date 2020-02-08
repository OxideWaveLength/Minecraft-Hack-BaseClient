package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

public abstract class BlockRotatedPillar extends Block {
	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis", EnumFacing.Axis.class);

	protected BlockRotatedPillar(Material materialIn) {
		super(materialIn, materialIn.getMaterialMapColor());
	}

	protected BlockRotatedPillar(Material p_i46385_1_, MapColor p_i46385_2_) {
		super(p_i46385_1_, p_i46385_2_);
	}
}
