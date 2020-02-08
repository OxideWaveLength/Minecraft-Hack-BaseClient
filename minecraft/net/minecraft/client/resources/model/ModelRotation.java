package net.minecraft.client.resources.model;

import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Maps;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public enum ModelRotation {
	X0_Y0(0, 0), X0_Y90(0, 90), X0_Y180(0, 180), X0_Y270(0, 270), X90_Y0(90, 0), X90_Y90(90, 90), X90_Y180(90, 180), X90_Y270(90, 270), X180_Y0(180, 0), X180_Y90(180, 90), X180_Y180(180, 180), X180_Y270(180, 270), X270_Y0(270, 0), X270_Y90(270, 90), X270_Y180(270, 180), X270_Y270(270, 270);

	private static final Map<Integer, ModelRotation> mapRotations = Maps.<Integer, ModelRotation>newHashMap();
	private final int combinedXY;
	private final Matrix4f matrix4d;
	private final int quartersX;
	private final int quartersY;

	private static int combineXY(int p_177521_0_, int p_177521_1_) {
		return p_177521_0_ * 360 + p_177521_1_;
	}

	private ModelRotation(int p_i46087_3_, int p_i46087_4_) {
		this.combinedXY = combineXY(p_i46087_3_, p_i46087_4_);
		this.matrix4d = new Matrix4f();
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.setIdentity();
		Matrix4f.rotate((float) (-p_i46087_3_) * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
		this.quartersX = MathHelper.abs_int(p_i46087_3_ / 90);
		Matrix4f matrix4f1 = new Matrix4f();
		matrix4f1.setIdentity();
		Matrix4f.rotate((float) (-p_i46087_4_) * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), matrix4f1, matrix4f1);
		this.quartersY = MathHelper.abs_int(p_i46087_4_ / 90);
		Matrix4f.mul(matrix4f1, matrix4f, this.matrix4d);
	}

	public Matrix4f getMatrix4d() {
		return this.matrix4d;
	}

	public EnumFacing rotateFace(EnumFacing p_177523_1_) {
		EnumFacing enumfacing = p_177523_1_;

		for (int i = 0; i < this.quartersX; ++i) {
			enumfacing = enumfacing.rotateAround(EnumFacing.Axis.X);
		}

		if (enumfacing.getAxis() != EnumFacing.Axis.Y) {
			for (int j = 0; j < this.quartersY; ++j) {
				enumfacing = enumfacing.rotateAround(EnumFacing.Axis.Y);
			}
		}

		return enumfacing;
	}

	public int rotateVertex(EnumFacing facing, int vertexIndex) {
		int i = vertexIndex;

		if (facing.getAxis() == EnumFacing.Axis.X) {
			i = (vertexIndex + this.quartersX) % 4;
		}

		EnumFacing enumfacing = facing;

		for (int j = 0; j < this.quartersX; ++j) {
			enumfacing = enumfacing.rotateAround(EnumFacing.Axis.X);
		}

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			i = (i + this.quartersY) % 4;
		}

		return i;
	}

	public static ModelRotation getModelRotation(int p_177524_0_, int p_177524_1_) {
		return (ModelRotation) mapRotations.get(Integer.valueOf(combineXY(MathHelper.normalizeAngle(p_177524_0_, 360), MathHelper.normalizeAngle(p_177524_1_, 360))));
	}

	static {
		for (ModelRotation modelrotation : values()) {
			mapRotations.put(Integer.valueOf(modelrotation.combinedXY), modelrotation);
		}
	}
}
