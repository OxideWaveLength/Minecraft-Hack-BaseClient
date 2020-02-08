package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityEnderEye extends Entity {
	/** 'x' location the eye should float towards. */
	private double targetX;

	/** 'y' location the eye should float towards. */
	private double targetY;

	/** 'z' location the eye should float towards. */
	private double targetZ;
	private int despawnTimer;
	private boolean shatterOrDrop;

	public EntityEnderEye(World worldIn) {
		super(worldIn);
		this.setSize(0.25F, 0.25F);
	}

	protected void entityInit() {
	}

	/**
	 * Checks if the entity is in range to render by using the past in distance and
	 * comparing it to its average edge length * 64 * renderDistanceWeight Args:
	 * distance
	 */
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

		if (Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}

	public EntityEnderEye(World worldIn, double x, double y, double z) {
		super(worldIn);
		this.despawnTimer = 0;
		this.setSize(0.25F, 0.25F);
		this.setPosition(x, y, z);
	}

	public void moveTowards(BlockPos p_180465_1_) {
		double d0 = (double) p_180465_1_.getX();
		int i = p_180465_1_.getY();
		double d1 = (double) p_180465_1_.getZ();
		double d2 = d0 - this.posX;
		double d3 = d1 - this.posZ;
		float f = MathHelper.sqrt_double(d2 * d2 + d3 * d3);

		if (f > 12.0F) {
			this.targetX = this.posX + d2 / (double) f * 12.0D;
			this.targetZ = this.posZ + d3 / (double) f * 12.0D;
			this.targetY = this.posY + 8.0D;
		} else {
			this.targetX = d0;
			this.targetY = (double) i;
			this.targetZ = d1;
		}

		this.despawnTimer = 0;
		this.shatterOrDrop = this.rand.nextInt(5) > 0;
	}

	/**
	 * Sets the velocity to the args. Args: x, y, z
	 */
	public void setVelocity(double x, double y, double z) {
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt_double(x * x + z * z);
			this.prevRotationYaw = this.rotationYaw = (float) (MathHelper.func_181159_b(x, z) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (MathHelper.func_181159_b(y, (double) f) * 180.0D / Math.PI);
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		super.onUpdate();
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float) (MathHelper.func_181159_b(this.motionX, this.motionZ) * 180.0D / Math.PI);

		for (this.rotationPitch = (float) (MathHelper.func_181159_b(this.motionY, (double) f) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
			;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

		if (!this.worldObj.isRemote) {
			double d0 = this.targetX - this.posX;
			double d1 = this.targetZ - this.posZ;
			float f1 = (float) Math.sqrt(d0 * d0 + d1 * d1);
			float f2 = (float) MathHelper.func_181159_b(d1, d0);
			double d2 = (double) f + (double) (f1 - f) * 0.0025D;

			if (f1 < 1.0F) {
				d2 *= 0.8D;
				this.motionY *= 0.8D;
			}

			this.motionX = Math.cos((double) f2) * d2;
			this.motionZ = Math.sin((double) f2) * d2;

			if (this.posY < this.targetY) {
				this.motionY += (1.0D - this.motionY) * 0.014999999664723873D;
			} else {
				this.motionY += (-1.0D - this.motionY) * 0.014999999664723873D;
			}
		}

		float f3 = 0.25F;

		if (this.isInWater()) {
			for (int i = 0; i < 4; ++i) {
				this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double) f3, this.posY - this.motionY * (double) f3, this.posZ - this.motionZ * (double) f3, this.motionX, this.motionY, this.motionZ, new int[0]);
			}
		} else {
			this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX - this.motionX * (double) f3 + this.rand.nextDouble() * 0.6D - 0.3D, this.posY - this.motionY * (double) f3 - 0.5D, this.posZ - this.motionZ * (double) f3 + this.rand.nextDouble() * 0.6D - 0.3D, this.motionX, this.motionY, this.motionZ, new int[0]);
		}

		if (!this.worldObj.isRemote) {
			this.setPosition(this.posX, this.posY, this.posZ);
			++this.despawnTimer;

			if (this.despawnTimer > 80 && !this.worldObj.isRemote) {
				this.setDead();

				if (this.shatterOrDrop) {
					this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Items.ender_eye)));
				} else {
					this.worldObj.playAuxSFX(2003, new BlockPos(this), 0);
				}
			}
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
	}

	/**
	 * Gets how bright this entity is.
	 */
	public float getBrightness(float partialTicks) {
		return 1.0F;
	}

	public int getBrightnessForRender(float partialTicks) {
		return 15728880;
	}

	/**
	 * If returns false, the item will not inflict any damage against entities.
	 */
	public boolean canAttackWithItem() {
		return false;
	}
}
