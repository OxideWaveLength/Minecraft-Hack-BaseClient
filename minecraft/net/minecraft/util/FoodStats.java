package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;

public class FoodStats {
	/** The player's food level. */
	private int foodLevel = 20;

	/** The player's food saturation. */
	private float foodSaturationLevel = 5.0F;

	/** The player's food exhaustion. */
	private float foodExhaustionLevel;

	/** The player's food timer value. */
	private int foodTimer;
	private int prevFoodLevel = 20;

	/**
	 * Add food stats.
	 */
	public void addStats(int foodLevelIn, float foodSaturationModifier) {
		this.foodLevel = Math.min(foodLevelIn + this.foodLevel, 20);
		this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float) foodLevelIn * foodSaturationModifier * 2.0F, (float) this.foodLevel);
	}

	public void addStats(ItemFood foodItem, ItemStack p_151686_2_) {
		this.addStats(foodItem.getHealAmount(p_151686_2_), foodItem.getSaturationModifier(p_151686_2_));
	}

	/**
	 * Handles the food game logic.
	 */
	public void onUpdate(EntityPlayer player) {
		EnumDifficulty enumdifficulty = player.worldObj.getDifficulty();
		this.prevFoodLevel = this.foodLevel;

		if (this.foodExhaustionLevel > 4.0F) {
			this.foodExhaustionLevel -= 4.0F;

			if (this.foodSaturationLevel > 0.0F) {
				this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
			} else if (enumdifficulty != EnumDifficulty.PEACEFUL) {
				this.foodLevel = Math.max(this.foodLevel - 1, 0);
			}
		}

		if (player.worldObj.getGameRules().getBoolean("naturalRegeneration") && this.foodLevel >= 18 && player.shouldHeal()) {
			++this.foodTimer;

			if (this.foodTimer >= 80) {
				player.heal(1.0F);
				this.addExhaustion(3.0F);
				this.foodTimer = 0;
			}
		} else if (this.foodLevel <= 0) {
			++this.foodTimer;

			if (this.foodTimer >= 80) {
				if (player.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL) {
					player.attackEntityFrom(DamageSource.starve, 1.0F);
				}

				this.foodTimer = 0;
			}
		} else {
			this.foodTimer = 0;
		}
	}

	/**
	 * Reads the food data for the player.
	 */
	public void readNBT(NBTTagCompound p_75112_1_) {
		if (p_75112_1_.hasKey("foodLevel", 99)) {
			this.foodLevel = p_75112_1_.getInteger("foodLevel");
			this.foodTimer = p_75112_1_.getInteger("foodTickTimer");
			this.foodSaturationLevel = p_75112_1_.getFloat("foodSaturationLevel");
			this.foodExhaustionLevel = p_75112_1_.getFloat("foodExhaustionLevel");
		}
	}

	/**
	 * Writes the food data for the player.
	 */
	public void writeNBT(NBTTagCompound p_75117_1_) {
		p_75117_1_.setInteger("foodLevel", this.foodLevel);
		p_75117_1_.setInteger("foodTickTimer", this.foodTimer);
		p_75117_1_.setFloat("foodSaturationLevel", this.foodSaturationLevel);
		p_75117_1_.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
	}

	/**
	 * Get the player's food level.
	 */
	public int getFoodLevel() {
		return this.foodLevel;
	}

	public int getPrevFoodLevel() {
		return this.prevFoodLevel;
	}

	/**
	 * Get whether the player must eat food.
	 */
	public boolean needFood() {
		return this.foodLevel < 20;
	}

	/**
	 * adds input to foodExhaustionLevel to a max of 40
	 */
	public void addExhaustion(float p_75113_1_) {
		this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + p_75113_1_, 40.0F);
	}

	/**
	 * Get the player's food saturation level.
	 */
	public float getSaturationLevel() {
		return this.foodSaturationLevel;
	}

	public void setFoodLevel(int foodLevelIn) {
		this.foodLevel = foodLevelIn;
	}

	public void setFoodSaturationLevel(float foodSaturationLevelIn) {
		this.foodSaturationLevel = foodSaturationLevelIn;
	}
}
