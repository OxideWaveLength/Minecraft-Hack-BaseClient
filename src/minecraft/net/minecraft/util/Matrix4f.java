package net.minecraft.util;

public class Matrix4f extends org.lwjgl.util.vector.Matrix4f {
	public Matrix4f(float[] p_i46413_1_) {
		this.m00 = p_i46413_1_[0];
		this.m01 = p_i46413_1_[1];
		this.m02 = p_i46413_1_[2];
		this.m03 = p_i46413_1_[3];
		this.m10 = p_i46413_1_[4];
		this.m11 = p_i46413_1_[5];
		this.m12 = p_i46413_1_[6];
		this.m13 = p_i46413_1_[7];
		this.m20 = p_i46413_1_[8];
		this.m21 = p_i46413_1_[9];
		this.m22 = p_i46413_1_[10];
		this.m23 = p_i46413_1_[11];
		this.m30 = p_i46413_1_[12];
		this.m31 = p_i46413_1_[13];
		this.m32 = p_i46413_1_[14];
		this.m33 = p_i46413_1_[15];
	}

	public Matrix4f() {
		this.m00 = this.m01 = this.m02 = this.m03 = this.m10 = this.m11 = this.m12 = this.m13 = this.m20 = this.m21 = this.m22 = this.m23 = this.m30 = this.m31 = this.m32 = this.m33 = 0.0F;
	}
}
