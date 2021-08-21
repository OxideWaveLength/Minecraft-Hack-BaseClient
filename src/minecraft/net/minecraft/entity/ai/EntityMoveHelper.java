package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;

public class EntityMoveHelper {
	/** The EntityLiving that is being moved */
	protected EntityLiving entity;
	protected double posX;
	protected double posY;
	protected double posZ;

	/** The speed at which the entity should move */
	protected double speed;
	protected boolean update;

	public EntityMoveHelper(EntityLiving entitylivingIn) {
		this.entity = entitylivingIn;
		this.posX = entitylivingIn.posX;
		this.posY = entitylivingIn.posY;
		this.posZ = entitylivingIn.posZ;
	}

	public boolean isUpdating() {
		return this.update;
	}

	public double getSpeed() {
		return this.speed;
	}

	/**
	 * Sets the speed and location to move to
	 */
	public void setMoveTo(double x, double y, double z, double speedIn) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.speed = speedIn;
		this.update = true;
	}

	public void onUpdateMoveHelper() {
		this.entity.setMoveForward(0.0F);

		if (this.update) {
			this.update = false;
			int i = MathHelper.floor_double(this.entity.getEntityBoundingBox().minY + 0.5D);
			double d0 = this.posX - this.entity.posX;
			double d1 = this.posZ - this.entity.posZ;
			double d2 = this.posY - (double) i;
			double d3 = d0 * d0 + d2 * d2 + d1 * d1;

			if (d3 >= 2.500000277905201E-7D) {
				float f = (float) (MathHelper.func_181159_b(d1, d0) * 180.0D / Math.PI) - 90.0F;
				this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f, 30.0F);
				this.entity.setAIMoveSpeed((float) (this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));

				if (d2 > 0.0D && d0 * d0 + d1 * d1 < 1.0D) {
					this.entity.getJumpHelper().setJumping();
				}
			}
		}
	}

	/**
	 * Limits the given angle to a upper and lower limit.
	 */
	protected float limitAngle(float p_75639_1_, float p_75639_2_, float p_75639_3_) {
		float f = MathHelper.wrapAngleTo180_float(p_75639_2_ - p_75639_1_);

		if (f > p_75639_3_) {
			f = p_75639_3_;
		}

		if (f < -p_75639_3_) {
			f = -p_75639_3_;
		}

		float f1 = p_75639_1_ + f;

		if (f1 < 0.0F) {
			f1 += 360.0F;
		} else if (f1 > 360.0F) {
			f1 -= 360.0F;
		}

		return f1;
	}

	public double getX() {
		return this.posX;
	}

	public double getY() {
		return this.posY;
	}

	public double getZ() {
		return this.posZ;
	}
}
