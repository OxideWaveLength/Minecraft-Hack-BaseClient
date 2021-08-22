package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature {
	protected int growingAge;
	protected int field_175502_b;
	protected int field_175503_c;
	private float ageWidth = -1.0F;
	private float ageHeight;

	public EntityAgeable(World worldIn) {
		super(worldIn);
	}

	public abstract EntityAgeable createChild(EntityAgeable ageable);

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow, gets
	 * into the saddle on a pig.
	 */
	public boolean interact(EntityPlayer player) {
		ItemStack itemstack = player.inventory.getCurrentItem();

		if (itemstack != null && itemstack.getItem() == Items.spawn_egg) {
			if (!this.worldObj.isRemote) {
				Class<? extends Entity> oclass = EntityList.getClassFromID(itemstack.getMetadata());

				if (oclass != null && this.getClass() == oclass) {
					EntityAgeable entityageable = this.createChild(this);

					if (entityageable != null) {
						entityageable.setGrowingAge(-24000);
						entityageable.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
						this.worldObj.spawnEntityInWorld(entityageable);

						if (itemstack.hasDisplayName()) {
							entityageable.setCustomNameTag(itemstack.getDisplayName());
						}

						if (!player.capabilities.isCreativeMode) {
							--itemstack.stackSize;

							if (itemstack.stackSize <= 0) {
								player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack) null);
							}
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(12, Byte.valueOf((byte) 0));
	}

	/**
	 * The age value may be negative or positive or zero. If it's negative, it get's
	 * incremented on each tick, if it's positive, it get's decremented each tick.
	 * Don't confuse this with EntityLiving.getAge. With a negative value the Entity
	 * is considered a child.
	 */
	public int getGrowingAge() {
		return this.worldObj.isRemote ? this.dataWatcher.getWatchableObjectByte(12) : this.growingAge;
	}

	public void func_175501_a(int p_175501_1_, boolean p_175501_2_) {
		int i = this.getGrowingAge();
		int j = i;
		i = i + p_175501_1_ * 20;

		if (i > 0) {
			i = 0;

			if (j < 0) {
				this.onGrowingAdult();
			}
		}

		int k = i - j;
		this.setGrowingAge(i);

		if (p_175501_2_) {
			this.field_175502_b += k;

			if (this.field_175503_c == 0) {
				this.field_175503_c = 40;
			}
		}

		if (this.getGrowingAge() == 0) {
			this.setGrowingAge(this.field_175502_b);
		}
	}

	/**
	 * "Adds the value of the parameter times 20 to the age of this entity. If the
	 * entity is an adult (if the entity's age is greater than 0), it will have no
	 * effect."
	 */
	public void addGrowth(int growth) {
		this.func_175501_a(growth, false);
	}

	/**
	 * The age value may be negative or positive or zero. If it's negative, it get's
	 * incremented on each tick, if it's positive, it get's decremented each tick.
	 * With a negative value the Entity is considered a child.
	 */
	public void setGrowingAge(int age) {
		this.dataWatcher.updateObject(12, Byte.valueOf((byte) MathHelper.clamp_int(age, -1, 1)));
		this.growingAge = age;
		this.setScaleForAge(this.isChild());
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		super.writeEntityToNBT(tagCompound);
		tagCompound.setInteger("Age", this.getGrowingAge());
		tagCompound.setInteger("ForcedAge", this.field_175502_b);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
		super.readEntityFromNBT(tagCompund);
		this.setGrowingAge(tagCompund.getInteger("Age"));
		this.field_175502_b = tagCompund.getInteger("ForcedAge");
	}

	/**
	 * Called frequently so the entity can update its state every tick as required.
	 * For example, zombies and skeletons use this to react to sunlight and start to
	 * burn.
	 */
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (this.worldObj.isRemote) {
			if (this.field_175503_c > 0) {
				if (this.field_175503_c % 4 == 0) {
					this.worldObj.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, 0.0D, 0.0D, 0.0D, new int[0]);
				}

				--this.field_175503_c;
			}

			this.setScaleForAge(this.isChild());
		} else {
			int i = this.getGrowingAge();

			if (i < 0) {
				++i;
				this.setGrowingAge(i);

				if (i == 0) {
					this.onGrowingAdult();
				}
			} else if (i > 0) {
				--i;
				this.setGrowingAge(i);
			}
		}
	}

	/**
	 * This is called when Entity's growing age timer reaches 0 (negative values are
	 * considered as a child, positive as an adult)
	 */
	protected void onGrowingAdult() {
	}

	/**
	 * If Animal, checks if the age timer is negative
	 */
	public boolean isChild() {
		return this.getGrowingAge() < 0;
	}

	/**
	 * "Sets the scale for an ageable entity according to the boolean parameter,
	 * which says if it's a child."
	 */
	public void setScaleForAge(boolean p_98054_1_) {
		this.setScale(p_98054_1_ ? 0.5F : 1.0F);
	}

	/**
	 * Sets the width and height of the entity. Args: width, height
	 */
	protected final void setSize(float width, float height) {
		boolean flag = this.ageWidth > 0.0F;
		this.ageWidth = width;
		this.ageHeight = height;

		if (!flag) {
			this.setScale(1.0F);
		}
	}

	protected final void setScale(float scale) {
		super.setSize(this.ageWidth * scale, this.ageHeight * scale);
	}
}
