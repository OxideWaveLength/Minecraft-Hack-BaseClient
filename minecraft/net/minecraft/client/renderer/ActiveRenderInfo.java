package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ActiveRenderInfo {
	/** The current GL viewport */
	private static final IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);

	/** The current GL modelview matrix */
	private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);

	/** The current GL projection matrix */
	private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);

	/** The computed view object coordinates */
	private static final FloatBuffer OBJECTCOORDS = GLAllocation.createDirectFloatBuffer(3);
	private static Vec3 position = new Vec3(0.0D, 0.0D, 0.0D);

	/** The X component of the entity's yaw rotation */
	private static float rotationX;

	/** The combined X and Z components of the entity's pitch rotation */
	private static float rotationXZ;

	/** The Z component of the entity's yaw rotation */
	private static float rotationZ;

	/**
	 * The Y component (scaled along the Z axis) of the entity's pitch rotation
	 */
	private static float rotationYZ;

	/**
	 * The Y component (scaled along the X axis) of the entity's pitch rotation
	 */
	private static float rotationXY;

	/**
	 * Updates the current render info and camera location based on entity look
	 * angles and 1st/3rd person view mode
	 */
	public static void updateRenderInfo(EntityPlayer entityplayerIn, boolean p_74583_1_) {
		GlStateManager.getFloat(2982, MODELVIEW);
		GlStateManager.getFloat(2983, PROJECTION);
		GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);
		float f = (float) ((VIEWPORT.get(0) + VIEWPORT.get(2)) / 2);
		float f1 = (float) ((VIEWPORT.get(1) + VIEWPORT.get(3)) / 2);
		GLU.gluUnProject(f, f1, 0.0F, MODELVIEW, PROJECTION, VIEWPORT, OBJECTCOORDS);
		position = new Vec3((double) OBJECTCOORDS.get(0), (double) OBJECTCOORDS.get(1), (double) OBJECTCOORDS.get(2));
		int i = p_74583_1_ ? 1 : 0;
		float f2 = entityplayerIn.rotationPitch;
		float f3 = entityplayerIn.rotationYaw;
		rotationX = MathHelper.cos(f3 * (float) Math.PI / 180.0F) * (float) (1 - i * 2);
		rotationZ = MathHelper.sin(f3 * (float) Math.PI / 180.0F) * (float) (1 - i * 2);
		rotationYZ = -rotationZ * MathHelper.sin(f2 * (float) Math.PI / 180.0F) * (float) (1 - i * 2);
		rotationXY = rotationX * MathHelper.sin(f2 * (float) Math.PI / 180.0F) * (float) (1 - i * 2);
		rotationXZ = MathHelper.cos(f2 * (float) Math.PI / 180.0F);
	}

	public static Vec3 projectViewFromEntity(Entity p_178806_0_, double p_178806_1_) {
		double d0 = p_178806_0_.prevPosX + (p_178806_0_.posX - p_178806_0_.prevPosX) * p_178806_1_;
		double d1 = p_178806_0_.prevPosY + (p_178806_0_.posY - p_178806_0_.prevPosY) * p_178806_1_;
		double d2 = p_178806_0_.prevPosZ + (p_178806_0_.posZ - p_178806_0_.prevPosZ) * p_178806_1_;
		double d3 = d0 + position.xCoord;
		double d4 = d1 + position.yCoord;
		double d5 = d2 + position.zCoord;
		return new Vec3(d3, d4, d5);
	}

	public static Block getBlockAtEntityViewpoint(World worldIn, Entity p_180786_1_, float p_180786_2_) {
		Vec3 vec3 = projectViewFromEntity(p_180786_1_, (double) p_180786_2_);
		BlockPos blockpos = new BlockPos(vec3);
		IBlockState iblockstate = worldIn.getBlockState(blockpos);
		Block block = iblockstate.getBlock();

		if (block.getMaterial().isLiquid()) {
			float f = 0.0F;

			if (iblockstate.getBlock() instanceof BlockLiquid) {
				f = BlockLiquid.getLiquidHeightPercent(((Integer) iblockstate.getValue(BlockLiquid.LEVEL)).intValue()) - 0.11111111F;
			}

			float f1 = (float) (blockpos.getY() + 1) - f;

			if (vec3.yCoord >= (double) f1) {
				block = worldIn.getBlockState(blockpos.up()).getBlock();
			}
		}

		return block;
	}

	public static Vec3 getPosition() {
		return position;
	}

	public static float getRotationX() {
		return rotationX;
	}

	public static float getRotationXZ() {
		return rotationXZ;
	}

	public static float getRotationZ() {
		return rotationZ;
	}

	public static float getRotationYZ() {
		return rotationYZ;
	}

	public static float getRotationXY() {
		return rotationXY;
	}
}
