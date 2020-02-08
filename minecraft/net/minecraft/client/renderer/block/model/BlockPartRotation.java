package net.minecraft.client.renderer.block.model;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.util.EnumFacing;

public class BlockPartRotation {
	public final Vector3f origin;
	public final EnumFacing.Axis axis;
	public final float angle;
	public final boolean rescale;

	public BlockPartRotation(Vector3f originIn, EnumFacing.Axis axisIn, float angleIn, boolean rescaleIn) {
		this.origin = originIn;
		this.axis = axisIn;
		this.angle = angleIn;
		this.rescale = rescaleIn;
	}
}
