package net.minecraft.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.LadderClimbEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLivingBase extends Entity {

	private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
	private static final AttributeModifier sprintingSpeedBoostModifier = (new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.30000001192092896D, 2)).setSaved(false);
	private BaseAttributeMap attributeMap;
	private final CombatTracker _combatTracker = new CombatTracker(this);
	private final Map<Integer, PotionEffect> activePotionsMap = Maps.<Integer, PotionEffect>newHashMap();

	/** The equipment this mob was previously wearing, used for syncing. */
	private final ItemStack[] previousEquipment = new ItemStack[5];

	/** Whether an arm swing is currently in progress. */
	public boolean isSwingInProgress;
	public int swingProgressInt;
	public int arrowHitTimer;

	/**
	 * The amount of time remaining this entity should act 'hurt'. (Visual appearance of red tint)
	 */
	public int hurtTime;

	/** What the hurt time was max set to last. */
	public int maxHurtTime;

	/** The yaw at which this entity was last attacked from. */
	public float attackedAtYaw;

	/**
	 * The amount of time remaining this entity should act 'dead', i.e. have a corpse in the world.
	 */
	public int deathTime;
	public float prevSwingProgress;
	public float swingProgress;
	public float prevLimbSwingAmount;
	public float limbSwingAmount;

	/**
	 * Only relevant when limbYaw is not 0(the entity is moving). Influences where in its swing legs and arms currently are.
	 */
	public float limbSwing;
	public int maxHurtResistantTime = 20;
	public float prevCameraPitch;
	public float cameraPitch;
	public float field_70769_ao;
	public float field_70770_ap;
	public float renderYawOffset;
	public float prevRenderYawOffset;

	/** Entity head rotation yaw */
	public float rotationYawHead;

	/** Entity head rotation yaw at previous tick */
	public float prevRotationYawHead;

	/**
	 * A factor used to determine how far this entity will move each tick if it is jumping or falling.
	 */
	public float jumpMovementFactor = 0.02F;

	/** The most recent player that has attacked this entity */
	protected EntityPlayer attackingPlayer;

	/**
	 * Set to 60 when hit by the player or the player's wolf, then decrements. Used to determine whether the entity should drop items on death.
	 */
	protected int recentlyHit;

	/**
	 * This gets set on entity death, but never used. Looks like a duplicate of isDead
	 */
	protected boolean dead;

	/** The age of this EntityLiving (used to determine when it dies) */
	protected int entityAge;
	protected float prevOnGroundSpeedFactor;
	protected float onGroundSpeedFactor;
	protected float movedDistance;
	protected float prevMovedDistance;
	protected float field_70741_aB;

	/** The score value of the Mob, the amount of points the mob is worth. */
	protected int scoreValue;

	/**
	 * Damage taken in the last hit. Mobs are resistant to damage less than this for a short time after taking damage.
	 */
	protected float lastDamage;

	/** used to check whether entity is jumping. */
	protected boolean isJumping;
	public float moveStrafing;
	public float moveForward;
	protected float randomYawVelocity;

	/**
	 * The number of updates over which the new position and rotation are to be applied to the entity.
	 */
	protected int newPosRotationIncrements;

	/** The new X position to be applied to the entity. */
	protected double newPosX;

	/** The new Y position to be applied to the entity. */
	protected double newPosY;
	protected double newPosZ;

	/** The new yaw rotation to be applied to the entity. */
	protected double newRotationYaw;

	/** The new yaw rotation to be applied to the entity. */
	protected double newRotationPitch;

	/** Whether the DataWatcher needs to be updated with the active potions */
	private boolean potionsNeedUpdate = true;

	/** is only being set, has no uses as of MC 1.1 */
	private EntityLivingBase entityLivingToAttack;
	private int revengeTimer;
	private EntityLivingBase lastAttacker;

	/** Holds the value of ticksExisted when setLastAttacker was last called. */
	private int lastAttackerTime;

	/**
	 * A factor used to determine how far this entity will move each tick if it is walking on land. Adjusted by speed, and slipperiness of the current block.
	 */
	private float landMovementFactor;

	/** Number of ticks since last jump */
	private int jumpTicks;
	private float absorptionAmount;

	/**
	 * Called by the /kill command.
	 */
	public void onKillCommand() {
		this.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
	}

	public EntityLivingBase(World worldIn) {
		super(worldIn);
		this.applyEntityAttributes();
		this.setHealth(this.getMaxHealth());
		this.preventEntitySpawning = true;
		this.field_70770_ap = (float) ((Math.random() + 1.0D) * 0.009999999776482582D);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.field_70769_ao = (float) Math.random() * 12398.0F;
		this.rotationYaw = (float) (Math.random() * Math.PI * 2.0D);
		this.rotationYawHead = this.rotationYaw;
		this.stepHeight = 0.6F;
	}

	protected void entityInit() {
		this.dataWatcher.addObject(7, Integer.valueOf(0));
		this.dataWatcher.addObject(8, Byte.valueOf((byte) 0));
		this.dataWatcher.addObject(9, Byte.valueOf((byte) 0));
		this.dataWatcher.addObject(6, Float.valueOf(1.0F));
	}

	protected void applyEntityAttributes() {
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
	}

	protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
		if (!this.isInWater()) {
			this.handleWaterMovement();
		}

		if (!this.worldObj.isRemote && this.fallDistance > 3.0F && onGroundIn) {
			IBlockState iblockstate = this.worldObj.getBlockState(pos);
			Block block = iblockstate.getBlock();
			float f = (float) MathHelper.ceiling_float_int(this.fallDistance - 3.0F);

			if (block.getMaterial() != Material.air) {
				double d0 = (double) Math.min(0.2F + f / 15.0F, 10.0F);

				if (d0 > 2.5D) {
					d0 = 2.5D;
				}

				int i = (int) (150.0D * d0);
				((WorldServer) this.worldObj).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, new int[] { Block.getStateId(iblockstate) });
			}
		}

		super.updateFallState(y, onGroundIn, blockIn, pos);
	}

	public boolean canBreatheUnderwater() {
		return false;
	}

	/**
	 * Gets called every tick from main Entity class
	 */
	public void onEntityUpdate() {
		this.prevSwingProgress = this.swingProgress;
		super.onEntityUpdate();
		this.worldObj.theProfiler.startSection("livingEntityBaseTick");
		boolean flag = this instanceof EntityPlayer;

		if (this.isEntityAlive()) {
			if (this.isEntityInsideOpaqueBlock()) {
				this.attackEntityFrom(DamageSource.inWall, 1.0F);
			} else if (flag && !this.worldObj.getWorldBorder().contains(this.getEntityBoundingBox())) {
				double d0 = this.worldObj.getWorldBorder().getClosestDistance(this) + this.worldObj.getWorldBorder().getDamageBuffer();

				if (d0 < 0.0D) {
					this.attackEntityFrom(DamageSource.inWall, (float) Math.max(1, MathHelper.floor_double(-d0 * this.worldObj.getWorldBorder().getDamageAmount())));
				}
			}
		}

		if (this.isImmuneToFire() || this.worldObj.isRemote) {
			this.extinguish();
		}

		boolean flag1 = flag && ((EntityPlayer) this).capabilities.disableDamage;

		if (this.isEntityAlive()) {
			if (this.isInsideOfMaterial(Material.water)) {
				if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !flag1) {
					this.setAir(this.decreaseAirSupply(this.getAir()));

					if (this.getAir() == -20) {
						this.setAir(0);

						for (int i = 0; i < 8; ++i) {
							float f = this.rand.nextFloat() - this.rand.nextFloat();
							float f1 = this.rand.nextFloat() - this.rand.nextFloat();
							float f2 = this.rand.nextFloat() - this.rand.nextFloat();
							this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (double) f, this.posY + (double) f1, this.posZ + (double) f2, this.motionX, this.motionY, this.motionZ, new int[0]);
						}

						this.attackEntityFrom(DamageSource.drown, 2.0F);
					}
				}

				if (!this.worldObj.isRemote && this.isRiding() && this.ridingEntity instanceof EntityLivingBase) {
					this.mountEntity((Entity) null);
				}
			} else {
				this.setAir(300);
			}
		}

		if (this.isEntityAlive() && this.isWet()) {
			this.extinguish();
		}

		this.prevCameraPitch = this.cameraPitch;

		if (this.hurtTime > 0) {
			--this.hurtTime;
		}

		if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP)) {
			--this.hurtResistantTime;
		}

		if (this.getHealth() <= 0.0F) {
			this.onDeathUpdate();
		}

		if (this.recentlyHit > 0) {
			--this.recentlyHit;
		} else {
			this.attackingPlayer = null;
		}

		if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive()) {
			this.lastAttacker = null;
		}

		if (this.entityLivingToAttack != null) {
			if (!this.entityLivingToAttack.isEntityAlive()) {
				this.setRevengeTarget((EntityLivingBase) null);
			} else if (this.ticksExisted - this.revengeTimer > 100) {
				this.setRevengeTarget((EntityLivingBase) null);
			}
		}

		this.updatePotionEffects();
		this.prevMovedDistance = this.movedDistance;
		this.prevRenderYawOffset = this.renderYawOffset;
		this.prevRotationYawHead = this.rotationYawHead;
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationPitch = this.rotationPitch;
		this.worldObj.theProfiler.endSection();
	}

	/**
	 * If Animal, checks if the age timer is negative
	 */
	public boolean isChild() {
		return false;
	}

	/**
	 * handles entity death timer, experience orb and particle creation
	 */
	protected void onDeathUpdate() {
		++this.deathTime;

		if (this.deathTime == 20) {
			if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")) {
				int i = this.getExperiencePoints(this.attackingPlayer);

				while (i > 0) {
					int j = EntityXPOrb.getXPSplit(i);
					i -= j;
					this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
				}
			}

			this.setDead();

			for (int k = 0; k < 20; ++k) {
				double d2 = this.rand.nextGaussian() * 0.02D;
				double d0 = this.rand.nextGaussian() * 0.02D;
				double d1 = this.rand.nextGaussian() * 0.02D;
				this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1, new int[0]);
			}
		}
	}

	/**
	 * Entity won't drop items or experience points if this returns false
	 */
	protected boolean canDropLoot() {
		return !this.isChild();
	}

	/**
	 * Decrements the entity's air supply when underwater
	 */
	protected int decreaseAirSupply(int p_70682_1_) {
		int i = EnchantmentHelper.getRespiration(this);
		return i > 0 && this.rand.nextInt(i + 1) > 0 ? p_70682_1_ : p_70682_1_ - 1;
	}

	/**
	 * Get the experience points the entity currently has.
	 */
	protected int getExperiencePoints(EntityPlayer player) {
		return 0;
	}

	/**
	 * Only use is to identify if class is an instance of player for experience dropping
	 */
	protected boolean isPlayer() {
		return false;
	}

	public Random getRNG() {
		return this.rand;
	}

	public EntityLivingBase getAITarget() {
		return this.entityLivingToAttack;
	}

	public int getRevengeTimer() {
		return this.revengeTimer;
	}

	public void setRevengeTarget(EntityLivingBase livingBase) {
		this.entityLivingToAttack = livingBase;
		this.revengeTimer = this.ticksExisted;
	}

	public EntityLivingBase getLastAttacker() {
		return this.lastAttacker;
	}

	public int getLastAttackerTime() {
		return this.lastAttackerTime;
	}

	public void setLastAttacker(Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) {
			this.lastAttacker = (EntityLivingBase) entityIn;
		} else {
			this.lastAttacker = null;
		}

		this.lastAttackerTime = this.ticksExisted;
	}

	public int getAge() {
		return this.entityAge;
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		tagCompound.setFloat("HealF", this.getHealth());
		tagCompound.setShort("Health", (short) ((int) Math.ceil((double) this.getHealth())));
		tagCompound.setShort("HurtTime", (short) this.hurtTime);
		tagCompound.setInteger("HurtByTimestamp", this.revengeTimer);
		tagCompound.setShort("DeathTime", (short) this.deathTime);
		tagCompound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());

		for (ItemStack itemstack : this.getInventory()) {
			if (itemstack != null) {
				this.attributeMap.removeAttributeModifiers(itemstack.getAttributeModifiers());
			}
		}

		tagCompound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));

		for (ItemStack itemstack1 : this.getInventory()) {
			if (itemstack1 != null) {
				this.attributeMap.applyAttributeModifiers(itemstack1.getAttributeModifiers());
			}
		}

		if (!this.activePotionsMap.isEmpty()) {
			NBTTagList nbttaglist = new NBTTagList();

			for (PotionEffect potioneffect : this.activePotionsMap.values()) {
				nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
			}

			tagCompound.setTag("ActiveEffects", nbttaglist);
		}
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
		this.setAbsorptionAmount(tagCompund.getFloat("AbsorptionAmount"));

		if (tagCompund.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote) {
			SharedMonsterAttributes.func_151475_a(this.getAttributeMap(), tagCompund.getTagList("Attributes", 10));
		}

		if (tagCompund.hasKey("ActiveEffects", 9)) {
			NBTTagList nbttaglist = tagCompund.getTagList("ActiveEffects", 10);

			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound);

				if (potioneffect != null) {
					this.activePotionsMap.put(Integer.valueOf(potioneffect.getPotionID()), potioneffect);
				}
			}
		}

		if (tagCompund.hasKey("HealF", 99)) {
			this.setHealth(tagCompund.getFloat("HealF"));
		} else {
			NBTBase nbtbase = tagCompund.getTag("Health");

			if (nbtbase == null) {
				this.setHealth(this.getMaxHealth());
			} else if (nbtbase.getId() == 5) {
				this.setHealth(((NBTTagFloat) nbtbase).getFloat());
			} else if (nbtbase.getId() == 2) {
				this.setHealth((float) ((NBTTagShort) nbtbase).getShort());
			}
		}

		this.hurtTime = tagCompund.getShort("HurtTime");
		this.deathTime = tagCompund.getShort("DeathTime");
		this.revengeTimer = tagCompund.getInteger("HurtByTimestamp");
	}

	protected void updatePotionEffects() {
		Iterator<Integer> iterator = this.activePotionsMap.keySet().iterator();

		while (iterator.hasNext()) {
			Integer integer = (Integer) iterator.next();
			PotionEffect potioneffect = (PotionEffect) this.activePotionsMap.get(integer);

			if (!potioneffect.onUpdate(this)) {
				if (!this.worldObj.isRemote) {
					iterator.remove();
					this.onFinishedPotionEffect(potioneffect);
				}
			} else if (potioneffect.getDuration() % 600 == 0) {
				this.onChangedPotionEffect(potioneffect, false);
			}
		}

		if (this.potionsNeedUpdate) {
			if (!this.worldObj.isRemote) {
				this.updatePotionMetadata();
			}

			this.potionsNeedUpdate = false;
		}

		int i = this.dataWatcher.getWatchableObjectInt(7);
		boolean flag1 = this.dataWatcher.getWatchableObjectByte(8) > 0;

		if (i > 0) {
			boolean flag = false;

			if (!this.isInvisible()) {
				flag = this.rand.nextBoolean();
			} else {
				flag = this.rand.nextInt(15) == 0;
			}

			if (flag1) {
				flag &= this.rand.nextInt(5) == 0;
			}

			if (flag && i > 0) {
				double d0 = (double) (i >> 16 & 255) / 255.0D;
				double d1 = (double) (i >> 8 & 255) / 255.0D;
				double d2 = (double) (i >> 0 & 255) / 255.0D;
				this.worldObj.spawnParticle(flag1 ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, d0, d1, d2, new int[0]);
			}
		}
	}

	/**
	 * Clears potion metadata values if the entity has no potion effects. Otherwise, updates potion effect color, ambience, and invisibility metadata values
	 */
	protected void updatePotionMetadata() {
		if (this.activePotionsMap.isEmpty()) {
			this.resetPotionEffectMetadata();
			this.setInvisible(false);
		} else {
			int i = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
			this.dataWatcher.updateObject(8, Byte.valueOf((byte) (PotionHelper.getAreAmbient(this.activePotionsMap.values()) ? 1 : 0)));
			this.dataWatcher.updateObject(7, Integer.valueOf(i));
			this.setInvisible(this.isPotionActive(Potion.invisibility.id));
		}
	}

	/**
	 * Resets the potion effect color and ambience metadata values
	 */
	protected void resetPotionEffectMetadata() {
		this.dataWatcher.updateObject(8, Byte.valueOf((byte) 0));
		this.dataWatcher.updateObject(7, Integer.valueOf(0));
	}

	public void clearActivePotions() {
		Iterator<Integer> iterator = this.activePotionsMap.keySet().iterator();

		while (iterator.hasNext()) {
			Integer integer = (Integer) iterator.next();
			PotionEffect potioneffect = (PotionEffect) this.activePotionsMap.get(integer);

			if (!this.worldObj.isRemote) {
				iterator.remove();
				this.onFinishedPotionEffect(potioneffect);
			}
		}
	}

	public Collection<PotionEffect> getActivePotionEffects() {
		return this.activePotionsMap.values();
	}

	public boolean isPotionActive(int potionId) {
		return this.activePotionsMap.containsKey(Integer.valueOf(potionId));
	}

	public boolean isPotionActive(Potion potionIn) {
		return this.activePotionsMap.containsKey(Integer.valueOf(potionIn.id));
	}

	/**
	 * returns the PotionEffect for the supplied Potion if it is active, null otherwise.
	 */
	public PotionEffect getActivePotionEffect(Potion potionIn) {
		return (PotionEffect) this.activePotionsMap.get(Integer.valueOf(potionIn.id));
	}

	/**
	 * adds a PotionEffect to the entity
	 */
	public void addPotionEffect(PotionEffect potioneffectIn) {
		if (this.isPotionApplicable(potioneffectIn)) {
			if (this.activePotionsMap.containsKey(Integer.valueOf(potioneffectIn.getPotionID()))) {
				((PotionEffect) this.activePotionsMap.get(Integer.valueOf(potioneffectIn.getPotionID()))).combine(potioneffectIn);
				this.onChangedPotionEffect((PotionEffect) this.activePotionsMap.get(Integer.valueOf(potioneffectIn.getPotionID())), true);
			} else {
				this.activePotionsMap.put(Integer.valueOf(potioneffectIn.getPotionID()), potioneffectIn);
				this.onNewPotionEffect(potioneffectIn);
			}
		}
	}

	public boolean isPotionApplicable(PotionEffect potioneffectIn) {
		if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
			int i = potioneffectIn.getPotionID();

			if (i == Potion.regeneration.id || i == Potion.poison.id) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns true if this entity is undead.
	 */
	public boolean isEntityUndead() {
		return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
	}

	/**
	 * Remove the speified potion effect from this entity.
	 */
	public void removePotionEffectClient(int potionId) {
		this.activePotionsMap.remove(Integer.valueOf(potionId));
	}

	/**
	 * Remove the specified potion effect from this entity.
	 */
	public void removePotionEffect(int potionId) {
		PotionEffect potioneffect = (PotionEffect) this.activePotionsMap.remove(Integer.valueOf(potionId));

		if (potioneffect != null) {
			this.onFinishedPotionEffect(potioneffect);
		}
	}

	protected void onNewPotionEffect(PotionEffect id) {
		this.potionsNeedUpdate = true;

		if (!this.worldObj.isRemote) {
			Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
		}
	}

	protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_) {
		this.potionsNeedUpdate = true;

		if (p_70695_2_ && !this.worldObj.isRemote) {
			Potion.potionTypes[id.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), id.getAmplifier());
			Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
		}
	}

	protected void onFinishedPotionEffect(PotionEffect p_70688_1_) {
		this.potionsNeedUpdate = true;

		if (!this.worldObj.isRemote) {
			Potion.potionTypes[p_70688_1_.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), p_70688_1_.getAmplifier());
		}
	}

	/**
	 * Heal living entity (param: amount of half-hearts)
	 */
	public void heal(float healAmount) {
		float f = this.getHealth();

		if (f > 0.0F) {
			this.setHealth(f + healAmount);
		}
	}

	public final float getHealth() {
		return this.dataWatcher.getWatchableObjectFloat(6);
	}

	public void setHealth(float health) {
		this.dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(health, 0.0F, this.getMaxHealth())));
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else if (this.worldObj.isRemote) {
			return false;
		} else {
			this.entityAge = 0;

			if (this.getHealth() <= 0.0F) {
				return false;
			} else if (source.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
				return false;
			} else {
				if ((source == DamageSource.anvil || source == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null) {
					this.getEquipmentInSlot(4).damageItem((int) (amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), this);
					amount *= 0.75F;
				}

				this.limbSwingAmount = 1.5F;
				boolean flag = true;

				if ((float) this.hurtResistantTime > (float) this.maxHurtResistantTime / 2.0F) {
					if (amount <= this.lastDamage) {
						return false;
					}

					this.damageEntity(source, amount - this.lastDamage);
					this.lastDamage = amount;
					flag = false;
				} else {
					this.lastDamage = amount;
					this.hurtResistantTime = this.maxHurtResistantTime;
					this.damageEntity(source, amount);
					this.hurtTime = this.maxHurtTime = 10;
				}

				this.attackedAtYaw = 0.0F;
				Entity entity = source.getEntity();

				if (entity != null) {
					if (entity instanceof EntityLivingBase) {
						this.setRevengeTarget((EntityLivingBase) entity);
					}

					if (entity instanceof EntityPlayer) {
						this.recentlyHit = 100;
						this.attackingPlayer = (EntityPlayer) entity;
					} else if (entity instanceof EntityWolf) {
						EntityWolf entitywolf = (EntityWolf) entity;

						if (entitywolf.isTamed()) {
							this.recentlyHit = 100;
							this.attackingPlayer = null;
						}
					}
				}

				if (flag) {
					this.worldObj.setEntityState(this, (byte) 2);

					if (source != DamageSource.drown) {
						this.setBeenAttacked();
					}

					if (entity != null) {
						double d1 = entity.posX - this.posX;
						double d0;

						for (d0 = entity.posZ - this.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
							d1 = (Math.random() - Math.random()) * 0.01D;
						}

						this.attackedAtYaw = (float) (MathHelper.func_181159_b(d0, d1) * 180.0D / Math.PI - (double) this.rotationYaw);
						this.knockBack(entity, amount, d1, d0);
					} else {
						this.attackedAtYaw = (float) ((int) (Math.random() * 2.0D) * 180);
					}
				}

				if (this.getHealth() <= 0.0F) {
					String s = this.getDeathSound();

					if (flag && s != null) {
						this.playSound(s, this.getSoundVolume(), this.getSoundPitch());
					}

					this.onDeath(source);
				} else {
					String s1 = this.getHurtSound();

					if (flag && s1 != null) {
						this.playSound(s1, this.getSoundVolume(), this.getSoundPitch());
					}
				}

				return true;
			}
		}
	}

	/**
	 * Renders broken item particles using the given ItemStack
	 */
	public void renderBrokenItemStack(ItemStack stack) {
		this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

		for (int i = 0; i < 5; ++i) {
			Vec3 vec3 = new Vec3(((double) this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			vec3 = vec3.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
			vec3 = vec3.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
			double d0 = (double) (-this.rand.nextFloat()) * 0.6D - 0.3D;
			Vec3 vec31 = new Vec3(((double) this.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
			vec31 = vec31.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
			vec31 = vec31.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
			vec31 = vec31.addVector(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ);
			this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord, new int[] { Item.getIdFromItem(stack.getItem()) });
		}
	}

	/**
	 * Called when the mob's health reaches 0.
	 */
	public void onDeath(DamageSource cause) {
		Entity entity = cause.getEntity();
		EntityLivingBase entitylivingbase = this.func_94060_bK();

		if (this.scoreValue >= 0 && entitylivingbase != null) {
			entitylivingbase.addToPlayerScore(this, this.scoreValue);
		}

		if (entity != null) {
			entity.onKillEntity(this);
		}

		this.dead = true;
		this.getCombatTracker().reset();

		if (!this.worldObj.isRemote) {
			int i = 0;

			if (entity instanceof EntityPlayer) {
				i = EnchantmentHelper.getLootingModifier((EntityLivingBase) entity);
			}

			if (this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")) {
				this.dropFewItems(this.recentlyHit > 0, i);
				this.dropEquipment(this.recentlyHit > 0, i);

				if (this.recentlyHit > 0 && this.rand.nextFloat() < 0.025F + (float) i * 0.01F) {
					this.addRandomDrop();
				}
			}
		}

		this.worldObj.setEntityState(this, (byte) 3);
	}

	/**
	 * Drop the equipment for this entity.
	 */
	protected void dropEquipment(boolean p_82160_1_, int p_82160_2_) {
	}

	/**
	 * knocks back this entity
	 */
	public void knockBack(Entity entityIn, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
		if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
			this.isAirBorne = true;
			float f = MathHelper.sqrt_double(p_70653_3_ * p_70653_3_ + p_70653_5_ * p_70653_5_);
			float f1 = 0.4F;
			this.motionX /= 2.0D;
			this.motionY /= 2.0D;
			this.motionZ /= 2.0D;
			this.motionX -= p_70653_3_ / (double) f * (double) f1;
			this.motionY += (double) f1;
			this.motionZ -= p_70653_5_ / (double) f * (double) f1;

			if (this.motionY > 0.4000000059604645D) {
				this.motionY = 0.4000000059604645D;
			}
		}
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		return "game.neutral.hurt";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound() {
		return "game.neutral.die";
	}

	/**
	 * Causes this Entity to drop a random item.
	 */
	protected void addRandomDrop() {
	}

	/**
	 * Drop 0-2 items of this living's type
	 */
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
	}

	/**
	 * returns true if this entity is by a ladder, false otherwise
	 */
	public boolean isOnLadder() {
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.getEntityBoundingBox().minY);
		int k = MathHelper.floor_double(this.posZ);
		Block block = this.worldObj.getBlockState(new BlockPos(i, j, k)).getBlock();
		return (block == Blocks.ladder || block == Blocks.vine) && (!(this instanceof EntityPlayer) || !((EntityPlayer) this).isSpectator());
	}

	/**
	 * Checks whether target entity is alive.
	 */
	public boolean isEntityAlive() {
		return !this.isDead && this.getHealth() > 0.0F;
	}

	public void fall(float distance, float damageMultiplier) {
		super.fall(distance, damageMultiplier);
		PotionEffect potioneffect = this.getActivePotionEffect(Potion.jump);
		float f = potioneffect != null ? (float) (potioneffect.getAmplifier() + 1) : 0.0F;
		int i = MathHelper.ceiling_float_int((distance - 3.0F - f) * damageMultiplier);

		if (i > 0) {
			this.playSound(this.getFallSoundString(i), 1.0F, 1.0F);
			this.attackEntityFrom(DamageSource.fall, (float) i);
			int j = MathHelper.floor_double(this.posX);
			int k = MathHelper.floor_double(this.posY - 0.20000000298023224D);
			int l = MathHelper.floor_double(this.posZ);
			Block block = this.worldObj.getBlockState(new BlockPos(j, k, l)).getBlock();

			if (block.getMaterial() != Material.air) {
				Block.SoundType block$soundtype = block.stepSound;
				this.playSound(block$soundtype.getStepSound(), block$soundtype.getVolume() * 0.5F, block$soundtype.getFrequency() * 0.75F);
			}
		}
	}

	protected String getFallSoundString(int damageValue) {
		return damageValue > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
	}

	/**
	 * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
	 */
	public void performHurtAnimation() {
		this.hurtTime = this.maxHurtTime = 10;
		this.attackedAtYaw = 0.0F;
	}

	/**
	 * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
	 */
	public int getTotalArmorValue() {
		int i = 0;

		for (ItemStack itemstack : this.getInventory()) {
			if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
				int j = ((ItemArmor) itemstack.getItem()).damageReduceAmount;
				i += j;
			}
		}

		return i;
	}

	protected void damageArmor(float p_70675_1_) {
	}

	/**
	 * Reduces damage, depending on armor
	 */
	protected float applyArmorCalculations(DamageSource source, float damage) {
		if (!source.isUnblockable()) {
			int i = 25 - this.getTotalArmorValue();
			float f = damage * (float) i;
			this.damageArmor(damage);
			damage = f / 25.0F;
		}

		return damage;
	}

	/**
	 * Reduces damage, depending on potions
	 */
	protected float applyPotionDamageCalculations(DamageSource source, float damage) {
		if (source.isDamageAbsolute()) {
			return damage;
		} else {
			if (this.isPotionActive(Potion.resistance) && source != DamageSource.outOfWorld) {
				int i = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float f = damage * (float) j;
				damage = f / 25.0F;
			}

			if (damage <= 0.0F) {
				return 0.0F;
			} else {
				int k = EnchantmentHelper.getEnchantmentModifierDamage(this.getInventory(), source);

				if (k > 20) {
					k = 20;
				}

				if (k > 0 && k <= 20) {
					int l = 25 - k;
					float f1 = damage * (float) l;
					damage = f1 / 25.0F;
				}

				return damage;
			}
		}
	}

	/**
	 * Deals damage to the entity. If its a EntityPlayer then will take damage from the armor first and then health second with the reduced value. Args: damageAmount
	 */
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		if (!this.isEntityInvulnerable(damageSrc)) {
			damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
			damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
			float f = damageAmount;
			damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - damageAmount));

			if (damageAmount != 0.0F) {
				float f1 = this.getHealth();
				this.setHealth(f1 - damageAmount);
				this.getCombatTracker().trackDamage(damageSrc, f1, damageAmount);
				this.setAbsorptionAmount(this.getAbsorptionAmount() - damageAmount);
			}
		}
	}

	public CombatTracker getCombatTracker() {
		return this._combatTracker;
	}

	public EntityLivingBase func_94060_bK() {
		return (EntityLivingBase) (this._combatTracker.func_94550_c() != null ? this._combatTracker.func_94550_c() : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null)));
	}

	public final float getMaxHealth() {
		return (float) this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
	}

	/**
	 * counts the amount of arrows stuck in the entity. getting hit by arrows increases this, used in rendering
	 */
	public final int getArrowCountInEntity() {
		return this.dataWatcher.getWatchableObjectByte(9);
	}

	/**
	 * sets the amount of arrows stuck in the entity. used for rendering those
	 */
	public final void setArrowCountInEntity(int count) {
		this.dataWatcher.updateObject(9, Byte.valueOf((byte) count));
	}

	/**
	 * Returns an integer indicating the end point of the swing animation, used by {@link #swingProgress} to provide a progress indicator. Takes dig speed enchantments into account.
	 */
	private int getArmSwingAnimationEnd() {
		return this.isPotionActive(Potion.digSpeed) ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
	}

	/**
	 * Swings the item the player is holding.
	 */
	public void swingItem() {
		if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
			this.swingProgressInt = -1;
			this.isSwingInProgress = true;

			if (this.worldObj instanceof WorldServer) {
				((WorldServer) this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S0BPacketAnimation(this, 0));
			}
		}
	}

	public void handleStatusUpdate(byte id) {
		if (id == 2) {
			this.limbSwingAmount = 1.5F;
			this.hurtResistantTime = this.maxHurtResistantTime;
			this.hurtTime = this.maxHurtTime = 10;
			this.attackedAtYaw = 0.0F;
			String s = this.getHurtSound();

			if (s != null) {
				this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}

			this.attackEntityFrom(DamageSource.generic, 0.0F);
		} else if (id == 3) {
			String s1 = this.getDeathSound();

			if (s1 != null) {
				this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}

			this.setHealth(0.0F);
			this.onDeath(DamageSource.generic);
		} else {
			super.handleStatusUpdate(id);
		}
	}

	/**
	 * sets the dead flag. Used when you fall off the bottom of the world.
	 */
	protected void kill() {
		this.attackEntityFrom(DamageSource.outOfWorld, 4.0F);
	}

	/**
	 * Updates the arm swing progress counters and animation progress
	 */
	protected void updateArmSwingProgress() {
		int i = this.getArmSwingAnimationEnd();

		if (this.isSwingInProgress) {
			++this.swingProgressInt;

			if (this.swingProgressInt >= i) {
				this.swingProgressInt = 0;
				this.isSwingInProgress = false;
			}
		} else {
			this.swingProgressInt = 0;
		}

		this.swingProgress = (float) this.swingProgressInt / (float) i;
	}

	public IAttributeInstance getEntityAttribute(IAttribute attribute) {
		return this.getAttributeMap().getAttributeInstance(attribute);
	}

	public BaseAttributeMap getAttributeMap() {
		if (this.attributeMap == null) {
			this.attributeMap = new ServersideAttributeMap();
		}

		return this.attributeMap;
	}

	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEFINED;
	}

	/**
	 * Returns the item that this EntityLiving is holding, if any.
	 */
	public abstract ItemStack getHeldItem();

	/**
	 * 0: Tool in Hand; 1-4: Armor
	 */
	public abstract ItemStack getEquipmentInSlot(int slotIn);

	public abstract ItemStack getCurrentArmor(int slotIn);

	/**
	 * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
	 */
	public abstract void setCurrentItemOrArmor(int slotIn, ItemStack stack);

	/**
	 * Set sprinting switch for Entity.
	 */
	public void setSprinting(boolean sprinting) {
		super.setSprinting(sprinting);
		IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

		if (iattributeinstance.getModifier(sprintingSpeedBoostModifierUUID) != null) {
			iattributeinstance.removeModifier(sprintingSpeedBoostModifier);
		}

		if (sprinting) {
			iattributeinstance.applyModifier(sprintingSpeedBoostModifier);
		}
	}

	/**
	 * returns the inventory of this entity (only used in EntityPlayerMP it seems)
	 */
	public abstract ItemStack[] getInventory();

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 1.0F;
	}

	/**
	 * Gets the pitch of living sounds in living entities.
	 */
	protected float getSoundPitch() {
		return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
	}

	/**
	 * Dead and sleeping entities cannot move
	 */
	protected boolean isMovementBlocked() {
		return this.getHealth() <= 0.0F;
	}

	/**
	 * Moves the entity to a position out of the way of its mount.
	 */
	public void dismountEntity(Entity p_110145_1_) {
		double d0 = p_110145_1_.posX;
		double d1 = p_110145_1_.getEntityBoundingBox().minY + (double) p_110145_1_.height;
		double d2 = p_110145_1_.posZ;
		int i = 1;

		for (int j = -i; j <= i; ++j) {
			for (int k = -i; k < i; ++k) {
				if (j != 0 || k != 0) {
					int l = (int) (this.posX + (double) j);
					int i1 = (int) (this.posZ + (double) k);
					AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().offset((double) j, 1.0D, (double) k);

					if (this.worldObj.func_147461_a(axisalignedbb).isEmpty()) {
						if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(l, (int) this.posY, i1))) {
							this.setPositionAndUpdate(this.posX + (double) j, this.posY + 1.0D, this.posZ + (double) k);
							return;
						}

						if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(l, (int) this.posY - 1, i1)) || this.worldObj.getBlockState(new BlockPos(l, (int) this.posY - 1, i1)).getBlock().getMaterial() == Material.water) {
							d0 = this.posX + (double) j;
							d1 = this.posY + 1.0D;
							d2 = this.posZ + (double) k;
						}
					}
				}
			}
		}

		this.setPositionAndUpdate(d0, d1, d2);
	}

	public boolean getAlwaysRenderNameTagForRender() {
		return false;
	}

	protected float getJumpUpwardsMotion() {
		return 0.42F;
	}

	/**
	 * Causes this entity to do an upwards motion (jumping).
	 */
	protected void jump() {
		this.motionY = (double) this.getJumpUpwardsMotion();

		if (this.isPotionActive(Potion.jump)) {
			this.motionY += (double) ((float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
		}

		if (this.isSprinting()) {
			float f = this.rotationYaw * 0.017453292F;
			this.motionX -= (double) (MathHelper.sin(f) * 0.2F);
			this.motionZ += (double) (MathHelper.cos(f) * 0.2F);
		}

		this.isAirBorne = true;
	}

	/**
	 * main AI tick function, replaces updateEntityActionState
	 */
	protected void updateAITick() {
		this.motionY += 0.03999999910593033D;
	}

	protected void handleJumpLava() {
		this.motionY += 0.03999999910593033D;
	}

	/**
	 * Moves the entity based on the specified heading. Args: strafe, forward
	 */
	public void moveEntityWithHeading(float strafe, float forward) {
		if (this.isServerWorld()) {
			if (!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer) this).capabilities.isFlying) {
				if (!this.isInLava() || this instanceof EntityPlayer && ((EntityPlayer) this).capabilities.isFlying) {
					float f4 = 0.91F;

					if (this.onGround) {
						f4 = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
					}

					float f = 0.16277136F / (f4 * f4 * f4);
					float f5;

					if (this.onGround) {
						f5 = this.getAIMoveSpeed() * f;
					} else {
						f5 = this.jumpMovementFactor;
					}

					this.moveFlying(strafe, forward, f5);
					f4 = 0.91F;

					if (this.onGround) {
						f4 = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
					}

					if (this.isOnLadder()) {
						float f6 = 0.15F;
						this.motionX = MathHelper.clamp_double(this.motionX, (double) (-f6), (double) f6);
						this.motionZ = MathHelper.clamp_double(this.motionZ, (double) (-f6), (double) f6);
						this.fallDistance = 0.0F;

						if (this.motionY < -0.15D) {
							this.motionY = -0.15D;
						}

						boolean flag = this.isSneaking() && this instanceof EntityPlayer;

						if (flag && this.motionY < 0.0D) {
							this.motionY = 0.0D;
						}
					}

					this.moveEntity(this.motionX, this.motionY, this.motionZ);

					if (this.isCollidedHorizontally && this.isOnLadder()) {
						LadderClimbEvent event = (LadderClimbEvent) BaseClient.instance.getEventManager().call(new LadderClimbEvent(0.2D));
						if (!(event.isCancelled()))
							this.motionY = event.getMotionY();
					}

					if (this.worldObj.isRemote && (!this.worldObj.isBlockLoaded(new BlockPos((int) this.posX, 0, (int) this.posZ)) || !this.worldObj.getChunkFromBlockCoords(new BlockPos((int) this.posX, 0, (int) this.posZ)).isLoaded())) {
						if (this.posY > 0.0D) {
							this.motionY = -0.1D;
						} else {
							this.motionY = 0.0D;
						}
					} else {
						this.motionY -= 0.08D;
					}

					this.motionY *= 0.9800000190734863D;
					this.motionX *= (double) f4;
					this.motionZ *= (double) f4;
				} else {
					double d1 = this.posY;
					this.moveFlying(strafe, forward, 0.02F);
					this.moveEntity(this.motionX, this.motionY, this.motionZ);
					this.motionX *= 0.5D;
					this.motionY *= 0.5D;
					this.motionZ *= 0.5D;
					this.motionY -= 0.02D;

					if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d1, this.motionZ)) {
						this.motionY = 0.30000001192092896D;
					}
				}
			} else {
				double d0 = this.posY;
				float f1 = 0.8F;
				float f2 = 0.02F;
				float f3 = (float) EnchantmentHelper.getDepthStriderModifier(this);

				if (f3 > 3.0F) {
					f3 = 3.0F;
				}

				if (!this.onGround) {
					f3 *= 0.5F;
				}

				if (f3 > 0.0F) {
					f1 += (0.54600006F - f1) * f3 / 3.0F;
					f2 += (this.getAIMoveSpeed() * 1.0F - f2) * f3 / 3.0F;
				}

				this.moveFlying(strafe, forward, f2);
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= (double) f1;
				this.motionY *= 0.800000011920929D;
				this.motionZ *= (double) f1;
				this.motionY -= 0.02D;

				if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ)) {
					this.motionY = 0.30000001192092896D;
				}
			}
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double d2 = this.posX - this.prevPosX;
		double d3 = this.posZ - this.prevPosZ;
		float f7 = MathHelper.sqrt_double(d2 * d2 + d3 * d3) * 4.0F;

		if (f7 > 1.0F) {
			f7 = 1.0F;
		}

		this.limbSwingAmount += (f7 - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	/**
	 * the movespeed used for the new AI system
	 */
	public float getAIMoveSpeed() {
		return this.landMovementFactor;
	}

	/**
	 * set the movespeed used for the new AI system
	 */
	public void setAIMoveSpeed(float speedIn) {
		this.landMovementFactor = speedIn;
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		this.setLastAttacker(entityIn);
		return false;
	}

	/**
	 * Returns whether player is sleeping or not
	 */
	public boolean isPlayerSleeping() {
		return false;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		super.onUpdate();

		if (!this.worldObj.isRemote) {
			int i = this.getArrowCountInEntity();

			if (i > 0) {
				if (this.arrowHitTimer <= 0) {
					this.arrowHitTimer = 20 * (30 - i);
				}

				--this.arrowHitTimer;

				if (this.arrowHitTimer <= 0) {
					this.setArrowCountInEntity(i - 1);
				}
			}

			for (int j = 0; j < 5; ++j) {
				ItemStack itemstack = this.previousEquipment[j];
				ItemStack itemstack1 = this.getEquipmentInSlot(j);

				if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
					((WorldServer) this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S04PacketEntityEquipment(this.getEntityId(), j, itemstack1));

					if (itemstack != null) {
						this.attributeMap.removeAttributeModifiers(itemstack.getAttributeModifiers());
					}

					if (itemstack1 != null) {
						this.attributeMap.applyAttributeModifiers(itemstack1.getAttributeModifiers());
					}

					this.previousEquipment[j] = itemstack1 == null ? null : itemstack1.copy();
				}
			}

			if (this.ticksExisted % 20 == 0) {
				this.getCombatTracker().reset();
			}
		}

		this.onLivingUpdate();
		double d0 = this.posX - this.prevPosX;
		double d1 = this.posZ - this.prevPosZ;
		float f = (float) (d0 * d0 + d1 * d1);
		float f1 = this.renderYawOffset;
		float f2 = 0.0F;
		this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
		float f3 = 0.0F;

		if (f > 0.0025000002F) {
			f3 = 1.0F;
			f2 = (float) Math.sqrt((double) f) * 3.0F;
			f1 = (float) MathHelper.func_181159_b(d1, d0) * 180.0F / (float) Math.PI - 90.0F;
		}

		if (this.swingProgress > 0.0F) {
			f1 = this.rotationYaw;
		}

		if (!this.onGround) {
			f3 = 0.0F;
		}

		this.onGroundSpeedFactor += (f3 - this.onGroundSpeedFactor) * 0.3F;
		this.worldObj.theProfiler.startSection("headTurn");
		f2 = this.func_110146_f(f1, f2);
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("rangeChecks");

		while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
			this.prevRenderYawOffset -= 360.0F;
		}

		while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
			this.prevRenderYawOffset += 360.0F;
		}

		while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
			this.prevRotationPitch -= 360.0F;
		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
			this.prevRotationYawHead -= 360.0F;
		}

		while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
			this.prevRotationYawHead += 360.0F;
		}

		this.worldObj.theProfiler.endSection();
		this.movedDistance += f2;
	}

	protected float func_110146_f(float p_110146_1_, float p_110146_2_) {
		float f = MathHelper.wrapAngleTo180_float(p_110146_1_ - this.renderYawOffset);
		this.renderYawOffset += f * 0.3F;
		float f1 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
		boolean flag = f1 < -90.0F || f1 >= 90.0F;

		if (f1 < -75.0F) {
			f1 = -75.0F;
		}

		if (f1 >= 75.0F) {
			f1 = 75.0F;
		}

		this.renderYawOffset = this.rotationYaw - f1;

		if (f1 * f1 > 2500.0F) {
			this.renderYawOffset += f1 * 0.2F;
		}

		if (flag) {
			p_110146_2_ *= -1.0F;
		}

		return p_110146_2_;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons use this to react to sunlight and start to burn.
	 */
	public void onLivingUpdate() {
		if (this.jumpTicks > 0) {
			--this.jumpTicks;
		}

		if (this.newPosRotationIncrements > 0) {
			double d0 = this.posX + (this.newPosX - this.posX) / (double) this.newPosRotationIncrements;
			double d1 = this.posY + (this.newPosY - this.posY) / (double) this.newPosRotationIncrements;
			double d2 = this.posZ + (this.newPosZ - this.posZ) / (double) this.newPosRotationIncrements;
			double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.newPosRotationIncrements);
			this.rotationPitch = (float) ((double) this.rotationPitch + (this.newRotationPitch - (double) this.rotationPitch) / (double) this.newPosRotationIncrements);
			--this.newPosRotationIncrements;
			this.setPosition(d0, d1, d2);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		} else if (!this.isServerWorld()) {
			this.motionX *= 0.98D;
			this.motionY *= 0.98D;
			this.motionZ *= 0.98D;
		}

		if (Math.abs(this.motionX) < 0.005D) {
			this.motionX = 0.0D;
		}

		if (Math.abs(this.motionY) < 0.005D) {
			this.motionY = 0.0D;
		}

		if (Math.abs(this.motionZ) < 0.005D) {
			this.motionZ = 0.0D;
		}

		this.worldObj.theProfiler.startSection("ai");

		if (this.isMovementBlocked()) {
			this.isJumping = false;
			this.moveStrafing = 0.0F;
			this.moveForward = 0.0F;
			this.randomYawVelocity = 0.0F;
		} else if (this.isServerWorld()) {
			this.worldObj.theProfiler.startSection("newAi");
			this.updateEntityActionState();
			this.worldObj.theProfiler.endSection();
		}

		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("jump");

		if (this.isJumping) {
			if (this.isInWater()) {
				this.updateAITick();
			} else if (this.isInLava()) {
				this.handleJumpLava();
			} else if (this.onGround && this.jumpTicks == 0) {
				this.jump();
				this.jumpTicks = 10;
			}
		} else {
			this.jumpTicks = 0;
		}

		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("travel");
		this.moveStrafing *= 0.98F;
		this.moveForward *= 0.98F;
		this.randomYawVelocity *= 0.9F;
		this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
		this.worldObj.theProfiler.endSection();
		this.worldObj.theProfiler.startSection("push");

		if (!this.worldObj.isRemote) {
			this.collideWithNearbyEntities();
		}

		this.worldObj.theProfiler.endSection();
	}

	protected void updateEntityActionState() {
	}

	protected void collideWithNearbyEntities() {
		List<Entity> list = this.worldObj.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D), Predicates.<Entity>and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
			public boolean apply(Entity p_apply_1_) {
				return p_apply_1_.canBePushed();
			}
		}));

		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); ++i) {
				Entity entity = (Entity) list.get(i);
				this.collideWithEntity(entity);
			}
		}
	}

	protected void collideWithEntity(Entity p_82167_1_) {
		p_82167_1_.applyEntityCollision(this);
	}

	/**
	 * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
	 */
	public void mountEntity(Entity entityIn) {
		if (this.ridingEntity != null && entityIn == null) {
			if (!this.worldObj.isRemote) {
				this.dismountEntity(this.ridingEntity);
			}

			if (this.ridingEntity != null) {
				this.ridingEntity.riddenByEntity = null;
			}

			this.ridingEntity = null;
		} else {
			super.mountEntity(entityIn);
		}
	}

	/**
	 * Handles updating while being ridden by an entity
	 */
	public void updateRidden() {
		super.updateRidden();
		this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
		this.onGroundSpeedFactor = 0.0F;
		this.fallDistance = 0.0F;
	}

	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
		this.newPosX = x;
		this.newPosY = y;
		this.newPosZ = z;
		this.newRotationYaw = (double) yaw;
		this.newRotationPitch = (double) pitch;
		this.newPosRotationIncrements = posRotationIncrements;
	}

	public void setJumping(boolean p_70637_1_) {
		this.isJumping = p_70637_1_;
	}

	/**
	 * Called whenever an item is picked up from walking over it. Args: pickedUpEntity, stackSize
	 */
	public void onItemPickup(Entity p_71001_1_, int p_71001_2_) {
		if (!p_71001_1_.isDead && !this.worldObj.isRemote) {
			EntityTracker entitytracker = ((WorldServer) this.worldObj).getEntityTracker();

			if (p_71001_1_ instanceof EntityItem) {
				entitytracker.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
			}

			if (p_71001_1_ instanceof EntityArrow) {
				entitytracker.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
			}

			if (p_71001_1_ instanceof EntityXPOrb) {
				entitytracker.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
			}
		}
	}

	/**
	 * returns true if the entity provided in the argument can be seen. (Raytrace)
	 */
	public boolean canEntityBeSeen(Entity entityIn) {
		return this.worldObj.rayTraceBlocks(new Vec3(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ), new Vec3(entityIn.posX, entityIn.posY + (double) entityIn.getEyeHeight(), entityIn.posZ)) == null;
	}

	/**
	 * returns a (normalized) vector of where this entity is looking
	 */
	public Vec3 getLookVec() {
		return this.getLook(1.0F);
	}

	/**
	 * interpolated look vector
	 */
	public Vec3 getLook(float partialTicks) {
		if (partialTicks == 1.0F) {
			return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
		} else {
			float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
			float f1 = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
			return this.getVectorForRotation(f, f1);
		}
	}

	/**
	 * Returns where in the swing animation the living entity is (from 0 to 1). Args: partialTickTime
	 */
	public float getSwingProgress(float partialTickTime) {
		float f = this.swingProgress - this.prevSwingProgress;

		if (f < 0.0F) {
			++f;
		}

		return this.prevSwingProgress + f * partialTickTime;
	}

	/**
	 * Returns whether the entity is in a server world
	 */
	public boolean isServerWorld() {
		return !this.worldObj.isRemote;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when colliding.
	 */
	public boolean canBePushed() {
		return !this.isDead;
	}

	/**
	 * Sets that this entity has been attacked.
	 */
	protected void setBeenAttacked() {
		this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
	}

	public float getRotationYawHead() {
		return this.rotationYawHead;
	}

	/**
	 * Sets the head's yaw rotation of the entity.
	 */
	public void setRotationYawHead(float rotation) {
		this.rotationYawHead = rotation;
	}

	public void func_181013_g(float p_181013_1_) {
		this.renderYawOffset = p_181013_1_;
	}

	public float getAbsorptionAmount() {
		return this.absorptionAmount;
	}

	public void setAbsorptionAmount(float amount) {
		if (amount < 0.0F) {
			amount = 0.0F;
		}

		this.absorptionAmount = amount;
	}

	public Team getTeam() {
		return this.worldObj.getScoreboard().getPlayersTeam(this.getUniqueID().toString());
	}

	public boolean isOnSameTeam(EntityLivingBase otherEntity) {
		return this.isOnTeam(otherEntity.getTeam());
	}

	/**
	 * Returns true if the entity is on a specific team.
	 */
	public boolean isOnTeam(Team p_142012_1_) {
		return this.getTeam() != null ? this.getTeam().isSameTeam(p_142012_1_) : false;
	}

	/**
	 * Sends an ENTER_COMBAT packet to the client
	 */
	public void sendEnterCombat() {
	}

	/**
	 * Sends an END_COMBAT packet to the client
	 */
	public void sendEndCombat() {
	}

	protected void markPotionsDirty() {
		this.potionsNeedUpdate = true;
	}

}