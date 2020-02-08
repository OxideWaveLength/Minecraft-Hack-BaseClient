package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityMagmaCube extends EntitySlime {
	public EntityMagmaCube(World worldIn) {
		super(worldIn);
		this.isImmuneToFire = true;
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20000000298023224D);
	}

	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */
	public boolean getCanSpawnHere() {
		return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
	}

	/**
	 * Checks that the entity is not colliding with any blocks / liquids
	 */
	public boolean isNotColliding() {
		return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
	}

	/**
	 * Returns the current armor value as determined by a call to
	 * InventoryPlayer.getTotalArmorValue
	 */
	public int getTotalArmorValue() {
		return this.getSlimeSize() * 3;
	}

	public int getBrightnessForRender(float partialTicks) {
		return 15728880;
	}

	/**
	 * Gets how bright this entity is.
	 */
	public float getBrightness(float partialTicks) {
		return 1.0F;
	}

	protected EnumParticleTypes getParticleType() {
		return EnumParticleTypes.FLAME;
	}

	protected EntitySlime createInstance() {
		return new EntityMagmaCube(this.worldObj);
	}

	protected Item getDropItem() {
		return Items.magma_cream;
	}

	/**
	 * Drop 0-2 items of this living's type
	 */
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
		Item item = this.getDropItem();

		if (item != null && this.getSlimeSize() > 1) {
			int i = this.rand.nextInt(4) - 2;

			if (p_70628_2_ > 0) {
				i += this.rand.nextInt(p_70628_2_ + 1);
			}

			for (int j = 0; j < i; ++j) {
				this.dropItem(item, 1);
			}
		}
	}

	/**
	 * Returns true if the entity is on fire. Used by render to add the fire effect
	 * on rendering.
	 */
	public boolean isBurning() {
		return false;
	}

	/**
	 * Gets the amount of time the slime needs to wait between jumps.
	 */
	protected int getJumpDelay() {
		return super.getJumpDelay() * 4;
	}

	protected void alterSquishAmount() {
		this.squishAmount *= 0.9F;
	}

	/**
	 * Causes this entity to do an upwards motion (jumping).
	 */
	protected void jump() {
		this.motionY = (double) (0.42F + (float) this.getSlimeSize() * 0.1F);
		this.isAirBorne = true;
	}

	protected void handleJumpLava() {
		this.motionY = (double) (0.22F + (float) this.getSlimeSize() * 0.05F);
		this.isAirBorne = true;
	}

	public void fall(float distance, float damageMultiplier) {
	}

	/**
	 * Indicates weather the slime is able to damage the player (based upon the
	 * slime's size)
	 */
	protected boolean canDamagePlayer() {
		return true;
	}

	/**
	 * Gets the amount of damage dealt to the player when "attacked" by the slime.
	 */
	protected int getAttackStrength() {
		return super.getAttackStrength() + 2;
	}

	/**
	 * Returns the name of the sound played when the slime jumps.
	 */
	protected String getJumpSound() {
		return this.getSlimeSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
	}

	/**
	 * Returns true if the slime makes a sound when it lands after a jump (based
	 * upon the slime's size)
	 */
	protected boolean makesSoundOnLand() {
		return true;
	}
}
