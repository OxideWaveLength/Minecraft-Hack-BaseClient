package net.minecraft.entity.boss;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityDragon extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob {
	public double targetX;
	public double targetY;
	public double targetZ;

	/**
	 * Ring buffer array for the last 64 Y-positions and yaw rotations. Used to
	 * calculate offsets for the animations.
	 */
	public double[][] ringBuffer = new double[64][3];

	/**
	 * Index into the ring buffer. Incremented once per tick and restarts at 0 once
	 * it reaches the end of the buffer.
	 */
	public int ringBufferIndex = -1;

	/** An array containing all body parts of this dragon */
	public EntityDragonPart[] dragonPartArray;

	/** The head bounding box of a dragon */
	public EntityDragonPart dragonPartHead;

	/** The body bounding box of a dragon */
	public EntityDragonPart dragonPartBody;
	public EntityDragonPart dragonPartTail1;
	public EntityDragonPart dragonPartTail2;
	public EntityDragonPart dragonPartTail3;
	public EntityDragonPart dragonPartWing1;
	public EntityDragonPart dragonPartWing2;

	/** Animation time at previous tick. */
	public float prevAnimTime;

	/**
	 * Animation time, used to control the speed of the animation cycles (wings
	 * flapping, jaw opening, etc.)
	 */
	public float animTime;

	/** Force selecting a new flight target at next tick if set to true. */
	public boolean forceNewTarget;

	/**
	 * Activated if the dragon is flying though obsidian, white stone or bedrock.
	 * Slows movement and animation speed.
	 */
	public boolean slowed;
	private Entity target;
	public int deathTicks;

	/** The current endercrystal that is healing this dragon */
	public EntityEnderCrystal healingEnderCrystal;

	public EntityDragon(World worldIn) {
		super(worldIn);
		this.dragonPartArray = new EntityDragonPart[] { this.dragonPartHead = new EntityDragonPart(this, "head", 6.0F, 6.0F), this.dragonPartBody = new EntityDragonPart(this, "body", 8.0F, 8.0F), this.dragonPartTail1 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail2 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail3 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartWing1 = new EntityDragonPart(this, "wing", 4.0F, 4.0F), this.dragonPartWing2 = new EntityDragonPart(this, "wing", 4.0F, 4.0F) };
		this.setHealth(this.getMaxHealth());
		this.setSize(16.0F, 8.0F);
		this.noClip = true;
		this.isImmuneToFire = true;
		this.targetY = 100.0D;
		this.ignoreFrustumCheck = true;
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0D);
	}

	protected void entityInit() {
		super.entityInit();
	}

	/**
	 * Returns a double[3] array with movement offsets, used to calculate trailing
	 * tail/neck positions. [0] = yaw offset, [1] = y offset, [2] = unused, always
	 * 0. Parameters: buffer index offset, partial ticks.
	 */
	public double[] getMovementOffsets(int p_70974_1_, float p_70974_2_) {
		if (this.getHealth() <= 0.0F) {
			p_70974_2_ = 0.0F;
		}

		p_70974_2_ = 1.0F - p_70974_2_;
		int i = this.ringBufferIndex - p_70974_1_ * 1 & 63;
		int j = this.ringBufferIndex - p_70974_1_ * 1 - 1 & 63;
		double[] adouble = new double[3];
		double d0 = this.ringBuffer[i][0];
		double d1 = MathHelper.wrapAngleTo180_double(this.ringBuffer[j][0] - d0);
		adouble[0] = d0 + d1 * (double) p_70974_2_;
		d0 = this.ringBuffer[i][1];
		d1 = this.ringBuffer[j][1] - d0;
		adouble[1] = d0 + d1 * (double) p_70974_2_;
		adouble[2] = this.ringBuffer[i][2] + (this.ringBuffer[j][2] - this.ringBuffer[i][2]) * (double) p_70974_2_;
		return adouble;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required.
	 * For example, zombies and skeletons use this to react to sunlight and start to
	 * burn.
	 */
	public void onLivingUpdate() {
		if (this.worldObj.isRemote) {
			float f = MathHelper.cos(this.animTime * (float) Math.PI * 2.0F);
			float f1 = MathHelper.cos(this.prevAnimTime * (float) Math.PI * 2.0F);

			if (f1 <= -0.3F && f >= -0.3F && !this.isSilent()) {
				this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
			}
		}

		this.prevAnimTime = this.animTime;

		if (this.getHealth() <= 0.0F) {
			float f11 = (this.rand.nextFloat() - 0.5F) * 8.0F;
			float f13 = (this.rand.nextFloat() - 0.5F) * 4.0F;
			float f14 = (this.rand.nextFloat() - 0.5F) * 8.0F;
			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX + (double) f11, this.posY + 2.0D + (double) f13, this.posZ + (double) f14, 0.0D, 0.0D, 0.0D, new int[0]);
		} else {
			this.updateDragonEnderCrystal();
			float f10 = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
			f10 = f10 * (float) Math.pow(2.0D, this.motionY);

			if (this.slowed) {
				this.animTime += f10 * 0.5F;
			} else {
				this.animTime += f10;
			}

			this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);

			if (this.isAIDisabled()) {
				this.animTime = 0.5F;
			} else {
				if (this.ringBufferIndex < 0) {
					for (int i = 0; i < this.ringBuffer.length; ++i) {
						this.ringBuffer[i][0] = (double) this.rotationYaw;
						this.ringBuffer[i][1] = this.posY;
					}
				}

				if (++this.ringBufferIndex == this.ringBuffer.length) {
					this.ringBufferIndex = 0;
				}

				this.ringBuffer[this.ringBufferIndex][0] = (double) this.rotationYaw;
				this.ringBuffer[this.ringBufferIndex][1] = this.posY;

				if (this.worldObj.isRemote) {
					if (this.newPosRotationIncrements > 0) {
						double d10 = this.posX + (this.newPosX - this.posX) / (double) this.newPosRotationIncrements;
						double d0 = this.posY + (this.newPosY - this.posY) / (double) this.newPosRotationIncrements;
						double d1 = this.posZ + (this.newPosZ - this.posZ) / (double) this.newPosRotationIncrements;
						double d2 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double) this.rotationYaw);
						this.rotationYaw = (float) ((double) this.rotationYaw + d2 / (double) this.newPosRotationIncrements);
						this.rotationPitch = (float) ((double) this.rotationPitch + (this.newRotationPitch - (double) this.rotationPitch) / (double) this.newPosRotationIncrements);
						--this.newPosRotationIncrements;
						this.setPosition(d10, d0, d1);
						this.setRotation(this.rotationYaw, this.rotationPitch);
					}
				} else {
					double d11 = this.targetX - this.posX;
					double d12 = this.targetY - this.posY;
					double d13 = this.targetZ - this.posZ;
					double d14 = d11 * d11 + d12 * d12 + d13 * d13;

					if (this.target != null) {
						this.targetX = this.target.posX;
						this.targetZ = this.target.posZ;
						double d3 = this.targetX - this.posX;
						double d5 = this.targetZ - this.posZ;
						double d7 = Math.sqrt(d3 * d3 + d5 * d5);
						double d8 = 0.4000000059604645D + d7 / 80.0D - 1.0D;

						if (d8 > 10.0D) {
							d8 = 10.0D;
						}

						this.targetY = this.target.getEntityBoundingBox().minY + d8;
					} else {
						this.targetX += this.rand.nextGaussian() * 2.0D;
						this.targetZ += this.rand.nextGaussian() * 2.0D;
					}

					if (this.forceNewTarget || d14 < 100.0D || d14 > 22500.0D || this.isCollidedHorizontally || this.isCollidedVertically) {
						this.setNewTarget();
					}

					d12 = d12 / (double) MathHelper.sqrt_double(d11 * d11 + d13 * d13);
					float f17 = 0.6F;
					d12 = MathHelper.clamp_double(d12, (double) (-f17), (double) f17);
					this.motionY += d12 * 0.10000000149011612D;
					this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
					double d4 = 180.0D - MathHelper.func_181159_b(d11, d13) * 180.0D / Math.PI;
					double d6 = MathHelper.wrapAngleTo180_double(d4 - (double) this.rotationYaw);

					if (d6 > 50.0D) {
						d6 = 50.0D;
					}

					if (d6 < -50.0D) {
						d6 = -50.0D;
					}

					Vec3 vec3 = (new Vec3(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ)).normalize();
					double d15 = (double) (-MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F));
					Vec3 vec31 = (new Vec3((double) MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F), this.motionY, d15)).normalize();
					float f5 = ((float) vec31.dotProduct(vec3) + 0.5F) / 1.5F;

					if (f5 < 0.0F) {
						f5 = 0.0F;
					}

					this.randomYawVelocity *= 0.8F;
					float f6 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
					double d9 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0D + 1.0D;

					if (d9 > 40.0D) {
						d9 = 40.0D;
					}

					this.randomYawVelocity = (float) ((double) this.randomYawVelocity + d6 * (0.699999988079071D / d9 / (double) f6));
					this.rotationYaw += this.randomYawVelocity * 0.1F;
					float f7 = (float) (2.0D / (d9 + 1.0D));
					float f8 = 0.06F;
					this.moveFlying(0.0F, -1.0F, f8 * (f5 * f7 + (1.0F - f7)));

					if (this.slowed) {
						this.moveEntity(this.motionX * 0.800000011920929D, this.motionY * 0.800000011920929D, this.motionZ * 0.800000011920929D);
					} else {
						this.moveEntity(this.motionX, this.motionY, this.motionZ);
					}

					Vec3 vec32 = (new Vec3(this.motionX, this.motionY, this.motionZ)).normalize();
					float f9 = ((float) vec32.dotProduct(vec31) + 1.0F) / 2.0F;
					f9 = 0.8F + 0.15F * f9;
					this.motionX *= (double) f9;
					this.motionZ *= (double) f9;
					this.motionY *= 0.9100000262260437D;
				}

				this.renderYawOffset = this.rotationYaw;
				this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
				this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
				this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
				this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
				this.dragonPartBody.height = 3.0F;
				this.dragonPartBody.width = 5.0F;
				this.dragonPartWing1.height = 2.0F;
				this.dragonPartWing1.width = 4.0F;
				this.dragonPartWing2.height = 3.0F;
				this.dragonPartWing2.width = 4.0F;
				float f12 = (float) (this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
				float f2 = MathHelper.cos(f12);
				float f15 = -MathHelper.sin(f12);
				float f3 = this.rotationYaw * (float) Math.PI / 180.0F;
				float f16 = MathHelper.sin(f3);
				float f4 = MathHelper.cos(f3);
				this.dragonPartBody.onUpdate();
				this.dragonPartBody.setLocationAndAngles(this.posX + (double) (f16 * 0.5F), this.posY, this.posZ - (double) (f4 * 0.5F), 0.0F, 0.0F);
				this.dragonPartWing1.onUpdate();
				this.dragonPartWing1.setLocationAndAngles(this.posX + (double) (f4 * 4.5F), this.posY + 2.0D, this.posZ + (double) (f16 * 4.5F), 0.0F, 0.0F);
				this.dragonPartWing2.onUpdate();
				this.dragonPartWing2.setLocationAndAngles(this.posX - (double) (f4 * 4.5F), this.posY + 2.0D, this.posZ - (double) (f16 * 4.5F), 0.0F, 0.0F);

				if (!this.worldObj.isRemote && this.hurtTime == 0) {
					this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
					this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
					this.attackEntitiesInList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.getEntityBoundingBox().expand(1.0D, 1.0D, 1.0D)));
				}

				double[] adouble1 = this.getMovementOffsets(5, 1.0F);
				double[] adouble = this.getMovementOffsets(0, 1.0F);
				float f18 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
				float f19 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
				this.dragonPartHead.onUpdate();
				this.dragonPartHead.setLocationAndAngles(this.posX + (double) (f18 * 5.5F * f2), this.posY + (adouble[1] - adouble1[1]) * 1.0D + (double) (f15 * 5.5F), this.posZ - (double) (f19 * 5.5F * f2), 0.0F, 0.0F);

				for (int j = 0; j < 3; ++j) {
					EntityDragonPart entitydragonpart = null;

					if (j == 0) {
						entitydragonpart = this.dragonPartTail1;
					}

					if (j == 1) {
						entitydragonpart = this.dragonPartTail2;
					}

					if (j == 2) {
						entitydragonpart = this.dragonPartTail3;
					}

					double[] adouble2 = this.getMovementOffsets(12 + j * 2, 1.0F);
					float f20 = this.rotationYaw * (float) Math.PI / 180.0F + this.simplifyAngle(adouble2[0] - adouble1[0]) * (float) Math.PI / 180.0F * 1.0F;
					float f21 = MathHelper.sin(f20);
					float f22 = MathHelper.cos(f20);
					float f23 = 1.5F;
					float f24 = (float) (j + 1) * 2.0F;
					entitydragonpart.onUpdate();
					entitydragonpart.setLocationAndAngles(this.posX - (double) ((f16 * f23 + f21 * f24) * f2), this.posY + (adouble2[1] - adouble1[1]) * 1.0D - (double) ((f24 + f23) * f15) + 1.5D, this.posZ + (double) ((f4 * f23 + f22 * f24) * f2), 0.0F, 0.0F);
				}

				if (!this.worldObj.isRemote) {
					this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getEntityBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartBody.getEntityBoundingBox());
				}
			}
		}
	}

	/**
	 * Updates the state of the enderdragon's current endercrystal.
	 */
	private void updateDragonEnderCrystal() {
		if (this.healingEnderCrystal != null) {
			if (this.healingEnderCrystal.isDead) {
				if (!this.worldObj.isRemote) {
					this.attackEntityFromPart(this.dragonPartHead, DamageSource.setExplosionSource((Explosion) null), 10.0F);
				}

				this.healingEnderCrystal = null;
			} else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
				this.setHealth(this.getHealth() + 1.0F);
			}
		}

		if (this.rand.nextInt(10) == 0) {
			float f = 32.0F;
			List<EntityEnderCrystal> list = this.worldObj.<EntityEnderCrystal>getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().expand((double) f, (double) f, (double) f));
			EntityEnderCrystal entityendercrystal = null;
			double d0 = Double.MAX_VALUE;

			for (EntityEnderCrystal entityendercrystal1 : list) {
				double d1 = entityendercrystal1.getDistanceSqToEntity(this);

				if (d1 < d0) {
					d0 = d1;
					entityendercrystal = entityendercrystal1;
				}
			}

			this.healingEnderCrystal = entityendercrystal;
		}
	}

	/**
	 * Pushes all entities inside the list away from the enderdragon.
	 */
	private void collideWithEntities(List<Entity> p_70970_1_) {
		double d0 = (this.dragonPartBody.getEntityBoundingBox().minX + this.dragonPartBody.getEntityBoundingBox().maxX) / 2.0D;
		double d1 = (this.dragonPartBody.getEntityBoundingBox().minZ + this.dragonPartBody.getEntityBoundingBox().maxZ) / 2.0D;

		for (Entity entity : p_70970_1_) {
			if (entity instanceof EntityLivingBase) {
				double d2 = entity.posX - d0;
				double d3 = entity.posZ - d1;
				double d4 = d2 * d2 + d3 * d3;
				entity.addVelocity(d2 / d4 * 4.0D, 0.20000000298023224D, d3 / d4 * 4.0D);
			}
		}
	}

	/**
	 * Attacks all entities inside this list, dealing 5 hearts of damage.
	 */
	private void attackEntitiesInList(List<Entity> p_70971_1_) {
		for (int i = 0; i < p_70971_1_.size(); ++i) {
			Entity entity = (Entity) p_70971_1_.get(i);

			if (entity instanceof EntityLivingBase) {
				entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
				this.applyEnchantments(this, entity);
			}
		}
	}

	/**
	 * Sets a new target for the flight AI. It can be a random coordinate or a
	 * nearby player.
	 */
	private void setNewTarget() {
		this.forceNewTarget = false;
		List<EntityPlayer> list = Lists.newArrayList(this.worldObj.playerEntities);
		Iterator<EntityPlayer> iterator = list.iterator();

		while (iterator.hasNext()) {
			if (((EntityPlayer) iterator.next()).isSpectator()) {
				iterator.remove();
			}
		}

		if (this.rand.nextInt(2) == 0 && !list.isEmpty()) {
			this.target = (Entity) list.get(this.rand.nextInt(list.size()));
		} else {
			while (true) {
				this.targetX = 0.0D;
				this.targetY = (double) (70.0F + this.rand.nextFloat() * 50.0F);
				this.targetZ = 0.0D;
				this.targetX += (double) (this.rand.nextFloat() * 120.0F - 60.0F);
				this.targetZ += (double) (this.rand.nextFloat() * 120.0F - 60.0F);
				double d0 = this.posX - this.targetX;
				double d1 = this.posY - this.targetY;
				double d2 = this.posZ - this.targetZ;
				boolean flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;

				if (flag) {
					break;
				}
			}

			this.target = null;
		}
	}

	/**
	 * Simplifies the value of a number by adding/subtracting 180 to the point that
	 * the number is between -180 and 180.
	 */
	private float simplifyAngle(double p_70973_1_) {
		return (float) MathHelper.wrapAngleTo180_double(p_70973_1_);
	}

	/**
	 * Destroys all blocks that aren't associated with 'The End' inside the given
	 * bounding box.
	 */
	private boolean destroyBlocksInAABB(AxisAlignedBB p_70972_1_) {
		int i = MathHelper.floor_double(p_70972_1_.minX);
		int j = MathHelper.floor_double(p_70972_1_.minY);
		int k = MathHelper.floor_double(p_70972_1_.minZ);
		int l = MathHelper.floor_double(p_70972_1_.maxX);
		int i1 = MathHelper.floor_double(p_70972_1_.maxY);
		int j1 = MathHelper.floor_double(p_70972_1_.maxZ);
		boolean flag = false;
		boolean flag1 = false;

		for (int k1 = i; k1 <= l; ++k1) {
			for (int l1 = j; l1 <= i1; ++l1) {
				for (int i2 = k; i2 <= j1; ++i2) {
					BlockPos blockpos = new BlockPos(k1, l1, i2);
					Block block = this.worldObj.getBlockState(blockpos).getBlock();

					if (block.getMaterial() != Material.air) {
						if (block != Blocks.barrier && block != Blocks.obsidian && block != Blocks.end_stone && block != Blocks.bedrock && block != Blocks.command_block && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
							flag1 = this.worldObj.setBlockToAir(blockpos) || flag1;
						} else {
							flag = true;
						}
					}
				}
			}
		}

		if (flag1) {
			double d0 = p_70972_1_.minX + (p_70972_1_.maxX - p_70972_1_.minX) * (double) this.rand.nextFloat();
			double d1 = p_70972_1_.minY + (p_70972_1_.maxY - p_70972_1_.minY) * (double) this.rand.nextFloat();
			double d2 = p_70972_1_.minZ + (p_70972_1_.maxZ - p_70972_1_.minZ) * (double) this.rand.nextFloat();
			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
		}

		return flag;
	}

	public boolean attackEntityFromPart(EntityDragonPart dragonPart, DamageSource source, float p_70965_3_) {
		if (dragonPart != this.dragonPartHead) {
			p_70965_3_ = p_70965_3_ / 4.0F + 1.0F;
		}

		float f = this.rotationYaw * (float) Math.PI / 180.0F;
		float f1 = MathHelper.sin(f);
		float f2 = MathHelper.cos(f);
		this.targetX = this.posX + (double) (f1 * 5.0F) + (double) ((this.rand.nextFloat() - 0.5F) * 2.0F);
		this.targetY = this.posY + (double) (this.rand.nextFloat() * 3.0F) + 1.0D;
		this.targetZ = this.posZ - (double) (f2 * 5.0F) + (double) ((this.rand.nextFloat() - 0.5F) * 2.0F);
		this.target = null;

		if (source.getEntity() instanceof EntityPlayer || source.isExplosion()) {
			this.attackDragonFrom(source, p_70965_3_);
		}

		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source instanceof EntityDamageSource && ((EntityDamageSource) source).getIsThornsDamage()) {
			this.attackDragonFrom(source, amount);
		}

		return false;
	}

	/**
	 * Provides a way to cause damage to an ender dragon.
	 */
	protected boolean attackDragonFrom(DamageSource source, float amount) {
		return super.attackEntityFrom(source, amount);
	}

	/**
	 * Called by the /kill command.
	 */
	public void onKillCommand() {
		this.setDead();
	}

	/**
	 * handles entity death timer, experience orb and particle creation
	 */
	protected void onDeathUpdate() {
		++this.deathTicks;

		if (this.deathTicks >= 180 && this.deathTicks <= 200) {
			float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
			float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
			float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + (double) f, this.posY + 2.0D + (double) f1, this.posZ + (double) f2, 0.0D, 0.0D, 0.0D, new int[0]);
		}

		boolean flag = this.worldObj.getGameRules().getBoolean("doMobLoot");

		if (!this.worldObj.isRemote) {
			if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && flag) {
				int i = 1000;

				while (i > 0) {
					int k = EntityXPOrb.getXPSplit(i);
					i -= k;
					this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, k));
				}
			}

			if (this.deathTicks == 1) {
				this.worldObj.playBroadcastSound(1018, new BlockPos(this), 0);
			}
		}

		this.moveEntity(0.0D, 0.10000000149011612D, 0.0D);
		this.renderYawOffset = this.rotationYaw += 20.0F;

		if (this.deathTicks == 200 && !this.worldObj.isRemote) {
			if (flag) {
				int j = 2000;

				while (j > 0) {
					int l = EntityXPOrb.getXPSplit(j);
					j -= l;
					this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, l));
				}
			}

			this.generatePortal(new BlockPos(this.posX, 64.0D, this.posZ));
			this.setDead();
		}
	}

	/**
	 * Generate the portal when the dragon dies
	 */
	private void generatePortal(BlockPos pos) {
		int i = 4;
		double d0 = 12.25D;
		double d1 = 6.25D;

		for (int j = -1; j <= 32; ++j) {
			for (int k = -4; k <= 4; ++k) {
				for (int l = -4; l <= 4; ++l) {
					double d2 = (double) (k * k + l * l);

					if (d2 <= 12.25D) {
						BlockPos blockpos = pos.add(k, j, l);

						if (j < 0) {
							if (d2 <= 6.25D) {
								this.worldObj.setBlockState(blockpos, Blocks.bedrock.getDefaultState());
							}
						} else if (j > 0) {
							this.worldObj.setBlockState(blockpos, Blocks.air.getDefaultState());
						} else if (d2 > 6.25D) {
							this.worldObj.setBlockState(blockpos, Blocks.bedrock.getDefaultState());
						} else {
							this.worldObj.setBlockState(blockpos, Blocks.end_portal.getDefaultState());
						}
					}
				}
			}
		}

		this.worldObj.setBlockState(pos, Blocks.bedrock.getDefaultState());
		this.worldObj.setBlockState(pos.up(), Blocks.bedrock.getDefaultState());
		BlockPos blockpos1 = pos.up(2);
		this.worldObj.setBlockState(blockpos1, Blocks.bedrock.getDefaultState());
		this.worldObj.setBlockState(blockpos1.west(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
		this.worldObj.setBlockState(blockpos1.east(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
		this.worldObj.setBlockState(blockpos1.north(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
		this.worldObj.setBlockState(blockpos1.south(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
		this.worldObj.setBlockState(pos.up(3), Blocks.bedrock.getDefaultState());
		this.worldObj.setBlockState(pos.up(4), Blocks.dragon_egg.getDefaultState());
	}

	/**
	 * Makes the entity despawn if requirements are reached
	 */
	protected void despawnEntity() {
	}

	/**
	 * Return the Entity parts making up this Entity (currently only for dragons)
	 */
	public Entity[] getParts() {
		return this.dragonPartArray;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this
	 * Entity.
	 */
	public boolean canBeCollidedWith() {
		return false;
	}

	public World getWorld() {
		return this.worldObj;
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound() {
		return "mob.enderdragon.growl";
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		return "mob.enderdragon.hit";
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 5.0F;
	}
}
