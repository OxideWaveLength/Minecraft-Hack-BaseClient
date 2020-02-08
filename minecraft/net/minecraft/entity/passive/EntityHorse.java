package net.minecraft.entity.passive;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityHorse extends EntityAnimal implements IInvBasic {
	private static final Predicate<Entity> horseBreedingSelector = new Predicate<Entity>() {
		public boolean apply(Entity p_apply_1_) {
			return p_apply_1_ instanceof EntityHorse && ((EntityHorse) p_apply_1_).isBreeding();
		}
	};
	private static final IAttribute horseJumpStrength = (new RangedAttribute((IAttribute) null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).setDescription("Jump Strength").setShouldWatch(true);
	private static final String[] horseArmorTextures = new String[] { null, "textures/entity/horse/armor/horse_armor_iron.png", "textures/entity/horse/armor/horse_armor_gold.png", "textures/entity/horse/armor/horse_armor_diamond.png" };
	private static final String[] HORSE_ARMOR_TEXTURES_ABBR = new String[] { "", "meo", "goo", "dio" };
	private static final int[] armorValues = new int[] { 0, 5, 7, 11 };
	private static final String[] horseTextures = new String[] { "textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png" };
	private static final String[] HORSE_TEXTURES_ABBR = new String[] { "hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb" };
	private static final String[] horseMarkingTextures = new String[] { null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png" };
	private static final String[] HORSE_MARKING_TEXTURES_ABBR = new String[] { "", "wo_", "wmo", "wdo", "bdo" };
	private int eatingHaystackCounter;
	private int openMouthCounter;
	private int jumpRearingCounter;
	public int field_110278_bp;
	public int field_110279_bq;
	protected boolean horseJumping;
	private AnimalChest horseChest;
	private boolean hasReproduced;

	/**
	 * "The higher this value, the more likely the horse is to be tamed next time a
	 * player rides it."
	 */
	protected int temper;
	protected float jumpPower;
	private boolean field_110294_bI;
	private float headLean;
	private float prevHeadLean;
	private float rearingAmount;
	private float prevRearingAmount;
	private float mouthOpenness;
	private float prevMouthOpenness;

	/** Used to determine the sound that the horse should make when it steps */
	private int gallopTime;
	private String texturePrefix;
	private String[] horseTexturesArray = new String[3];
	private boolean field_175508_bO = false;

	public EntityHorse(World worldIn) {
		super(worldIn);
		this.setSize(1.4F, 1.6F);
		this.isImmuneToFire = false;
		this.setChested(false);
		((PathNavigateGround) this.getNavigator()).setAvoidsWater(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 1.2D));
		this.tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2D));
		this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(4, new EntityAIFollowParent(this, 1.0D));
		this.tasks.addTask(6, new EntityAIWander(this, 0.7D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.initHorseChest();
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Integer.valueOf(0));
		this.dataWatcher.addObject(19, Byte.valueOf((byte) 0));
		this.dataWatcher.addObject(20, Integer.valueOf(0));
		this.dataWatcher.addObject(21, String.valueOf((Object) ""));
		this.dataWatcher.addObject(22, Integer.valueOf(0));
	}

	public void setHorseType(int type) {
		this.dataWatcher.updateObject(19, Byte.valueOf((byte) type));
		this.resetTexturePrefix();
	}

	/**
	 * Returns the horse type. 0 = Normal, 1 = Donkey, 2 = Mule, 3 = Undead Horse, 4
	 * = Skeleton Horse
	 */
	public int getHorseType() {
		return this.dataWatcher.getWatchableObjectByte(19);
	}

	public void setHorseVariant(int variant) {
		this.dataWatcher.updateObject(20, Integer.valueOf(variant));
		this.resetTexturePrefix();
	}

	public int getHorseVariant() {
		return this.dataWatcher.getWatchableObjectInt(20);
	}

	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	public String getName() {
		if (this.hasCustomName()) {
			return this.getCustomNameTag();
		} else {
			int i = this.getHorseType();

			switch (i) {
			case 0:
			default:
				return StatCollector.translateToLocal("entity.horse.name");

			case 1:
				return StatCollector.translateToLocal("entity.donkey.name");

			case 2:
				return StatCollector.translateToLocal("entity.mule.name");

			case 3:
				return StatCollector.translateToLocal("entity.zombiehorse.name");

			case 4:
				return StatCollector.translateToLocal("entity.skeletonhorse.name");
			}
		}
	}

	private boolean getHorseWatchableBoolean(int p_110233_1_) {
		return (this.dataWatcher.getWatchableObjectInt(16) & p_110233_1_) != 0;
	}

	private void setHorseWatchableBoolean(int p_110208_1_, boolean p_110208_2_) {
		int i = this.dataWatcher.getWatchableObjectInt(16);

		if (p_110208_2_) {
			this.dataWatcher.updateObject(16, Integer.valueOf(i | p_110208_1_));
		} else {
			this.dataWatcher.updateObject(16, Integer.valueOf(i & ~p_110208_1_));
		}
	}

	public boolean isAdultHorse() {
		return !this.isChild();
	}

	public boolean isTame() {
		return this.getHorseWatchableBoolean(2);
	}

	public boolean func_110253_bW() {
		return this.isAdultHorse();
	}

	/**
	 * Gets the horse's owner
	 */
	public String getOwnerId() {
		return this.dataWatcher.getWatchableObjectString(21);
	}

	public void setOwnerId(String id) {
		this.dataWatcher.updateObject(21, id);
	}

	public float getHorseSize() {
		return 0.5F;
	}

	/**
	 * "Sets the scale for an ageable entity according to the boolean parameter,
	 * which says if it's a child."
	 */
	public void setScaleForAge(boolean p_98054_1_) {
		if (p_98054_1_) {
			this.setScale(this.getHorseSize());
		} else {
			this.setScale(1.0F);
		}
	}

	public boolean isHorseJumping() {
		return this.horseJumping;
	}

	public void setHorseTamed(boolean tamed) {
		this.setHorseWatchableBoolean(2, tamed);
	}

	public void setHorseJumping(boolean jumping) {
		this.horseJumping = jumping;
	}

	public boolean allowLeashing() {
		return !this.isUndead() && super.allowLeashing();
	}

	protected void func_142017_o(float p_142017_1_) {
		if (p_142017_1_ > 6.0F && this.isEatingHaystack()) {
			this.setEatingHaystack(false);
		}
	}

	public boolean isChested() {
		return this.getHorseWatchableBoolean(8);
	}

	/**
	 * Returns type of armor from DataWatcher (0 = iron, 1 = gold, 2 = diamond)
	 */
	public int getHorseArmorIndexSynced() {
		return this.dataWatcher.getWatchableObjectInt(22);
	}

	/**
	 * 0 = iron, 1 = gold, 2 = diamond
	 */
	private int getHorseArmorIndex(ItemStack itemStackIn) {
		if (itemStackIn == null) {
			return 0;
		} else {
			Item item = itemStackIn.getItem();
			return item == Items.iron_horse_armor ? 1 : (item == Items.golden_horse_armor ? 2 : (item == Items.diamond_horse_armor ? 3 : 0));
		}
	}

	public boolean isEatingHaystack() {
		return this.getHorseWatchableBoolean(32);
	}

	public boolean isRearing() {
		return this.getHorseWatchableBoolean(64);
	}

	public boolean isBreeding() {
		return this.getHorseWatchableBoolean(16);
	}

	public boolean getHasReproduced() {
		return this.hasReproduced;
	}

	/**
	 * Set horse armor stack (for example: new ItemStack(Items.iron_horse_armor))
	 */
	public void setHorseArmorStack(ItemStack itemStackIn) {
		this.dataWatcher.updateObject(22, Integer.valueOf(this.getHorseArmorIndex(itemStackIn)));
		this.resetTexturePrefix();
	}

	public void setBreeding(boolean breeding) {
		this.setHorseWatchableBoolean(16, breeding);
	}

	public void setChested(boolean chested) {
		this.setHorseWatchableBoolean(8, chested);
	}

	public void setHasReproduced(boolean hasReproducedIn) {
		this.hasReproduced = hasReproducedIn;
	}

	public void setHorseSaddled(boolean saddled) {
		this.setHorseWatchableBoolean(4, saddled);
	}

	public int getTemper() {
		return this.temper;
	}

	public void setTemper(int temperIn) {
		this.temper = temperIn;
	}

	public int increaseTemper(int p_110198_1_) {
		int i = MathHelper.clamp_int(this.getTemper() + p_110198_1_, 0, this.getMaxTemper());
		this.setTemper(i);
		return i;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		Entity entity = source.getEntity();
		return this.riddenByEntity != null && this.riddenByEntity.equals(entity) ? false : super.attackEntityFrom(source, amount);
	}

	/**
	 * Returns the current armor value as determined by a call to
	 * InventoryPlayer.getTotalArmorValue
	 */
	public int getTotalArmorValue() {
		return armorValues[this.getHorseArmorIndexSynced()];
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when
	 * colliding.
	 */
	public boolean canBePushed() {
		return this.riddenByEntity == null;
	}

	public boolean prepareChunkForSpawn() {
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.posZ);
		this.worldObj.getBiomeGenForCoords(new BlockPos(i, 0, j));
		return true;
	}

	public void dropChests() {
		if (!this.worldObj.isRemote && this.isChested()) {
			this.dropItem(Item.getItemFromBlock(Blocks.chest), 1);
			this.setChested(false);
		}
	}

	private void func_110266_cB() {
		this.openHorseMouth();

		if (!this.isSilent()) {
			this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
		}
	}

	public void fall(float distance, float damageMultiplier) {
		if (distance > 1.0F) {
			this.playSound("mob.horse.land", 0.4F, 1.0F);
		}

		int i = MathHelper.ceiling_float_int((distance * 0.5F - 3.0F) * damageMultiplier);

		if (i > 0) {
			this.attackEntityFrom(DamageSource.fall, (float) i);

			if (this.riddenByEntity != null) {
				this.riddenByEntity.attackEntityFrom(DamageSource.fall, (float) i);
			}

			Block block = this.worldObj.getBlockState(new BlockPos(this.posX, this.posY - 0.2D - (double) this.prevRotationYaw, this.posZ)).getBlock();

			if (block.getMaterial() != Material.air && !this.isSilent()) {
				Block.SoundType block$soundtype = block.stepSound;
				this.worldObj.playSoundAtEntity(this, block$soundtype.getStepSound(), block$soundtype.getVolume() * 0.5F, block$soundtype.getFrequency() * 0.75F);
			}
		}
	}

	/**
	 * Returns number of slots depending horse type
	 */
	private int getChestSize() {
		int i = this.getHorseType();
		return !this.isChested() || i != 1 && i != 2 ? 2 : 17;
	}

	private void initHorseChest() {
		AnimalChest animalchest = this.horseChest;
		this.horseChest = new AnimalChest("HorseChest", this.getChestSize());
		this.horseChest.setCustomName(this.getName());

		if (animalchest != null) {
			animalchest.func_110132_b(this);
			int i = Math.min(animalchest.getSizeInventory(), this.horseChest.getSizeInventory());

			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = animalchest.getStackInSlot(j);

				if (itemstack != null) {
					this.horseChest.setInventorySlotContents(j, itemstack.copy());
				}
			}
		}

		this.horseChest.func_110134_a(this);
		this.updateHorseSlots();
	}

	/**
	 * Updates the items in the saddle and armor slots of the horse's inventory.
	 */
	private void updateHorseSlots() {
		if (!this.worldObj.isRemote) {
			this.setHorseSaddled(this.horseChest.getStackInSlot(0) != null);

			if (this.canWearArmor()) {
				this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
			}
		}
	}

	/**
	 * Called by InventoryBasic.onInventoryChanged() on a array that is never
	 * filled.
	 */
	public void onInventoryChanged(InventoryBasic p_76316_1_) {
		int i = this.getHorseArmorIndexSynced();
		boolean flag = this.isHorseSaddled();
		this.updateHorseSlots();

		if (this.ticksExisted > 20) {
			if (i == 0 && i != this.getHorseArmorIndexSynced()) {
				this.playSound("mob.horse.armor", 0.5F, 1.0F);
			} else if (i != this.getHorseArmorIndexSynced()) {
				this.playSound("mob.horse.armor", 0.5F, 1.0F);
			}

			if (!flag && this.isHorseSaddled()) {
				this.playSound("mob.horse.leather", 0.5F, 1.0F);
			}
		}
	}

	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */
	public boolean getCanSpawnHere() {
		this.prepareChunkForSpawn();
		return super.getCanSpawnHere();
	}

	protected EntityHorse getClosestHorse(Entity entityIn, double distance) {
		double d0 = Double.MAX_VALUE;
		Entity entity = null;

		for (Entity entity1 : this.worldObj.getEntitiesInAABBexcluding(entityIn, entityIn.getEntityBoundingBox().addCoord(distance, distance, distance), horseBreedingSelector)) {
			double d1 = entity1.getDistanceSq(entityIn.posX, entityIn.posY, entityIn.posZ);

			if (d1 < d0) {
				entity = entity1;
				d0 = d1;
			}
		}

		return (EntityHorse) entity;
	}

	public double getHorseJumpStrength() {
		return this.getEntityAttribute(horseJumpStrength).getAttributeValue();
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound() {
		this.openHorseMouth();
		int i = this.getHorseType();
		return i == 3 ? "mob.horse.zombie.death" : (i == 4 ? "mob.horse.skeleton.death" : (i != 1 && i != 2 ? "mob.horse.death" : "mob.horse.donkey.death"));
	}

	protected Item getDropItem() {
		boolean flag = this.rand.nextInt(4) == 0;
		int i = this.getHorseType();
		return i == 4 ? Items.bone : (i == 3 ? (flag ? null : Items.rotten_flesh) : Items.leather);
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		this.openHorseMouth();

		if (this.rand.nextInt(3) == 0) {
			this.makeHorseRear();
		}

		int i = this.getHorseType();
		return i == 3 ? "mob.horse.zombie.hit" : (i == 4 ? "mob.horse.skeleton.hit" : (i != 1 && i != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit"));
	}

	public boolean isHorseSaddled() {
		return this.getHorseWatchableBoolean(4);
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound() {
		this.openHorseMouth();

		if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked()) {
			this.makeHorseRear();
		}

		int i = this.getHorseType();
		return i == 3 ? "mob.horse.zombie.idle" : (i == 4 ? "mob.horse.skeleton.idle" : (i != 1 && i != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle"));
	}

	protected String getAngrySoundName() {
		this.openHorseMouth();
		this.makeHorseRear();
		int i = this.getHorseType();
		return i != 3 && i != 4 ? (i != 1 && i != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry") : null;
	}

	protected void playStepSound(BlockPos pos, Block blockIn) {
		Block.SoundType block$soundtype = blockIn.stepSound;

		if (this.worldObj.getBlockState(pos.up()).getBlock() == Blocks.snow_layer) {
			block$soundtype = Blocks.snow_layer.stepSound;
		}

		if (!blockIn.getMaterial().isLiquid()) {
			int i = this.getHorseType();

			if (this.riddenByEntity != null && i != 1 && i != 2) {
				++this.gallopTime;

				if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
					this.playSound("mob.horse.gallop", block$soundtype.getVolume() * 0.15F, block$soundtype.getFrequency());

					if (i == 0 && this.rand.nextInt(10) == 0) {
						this.playSound("mob.horse.breathe", block$soundtype.getVolume() * 0.6F, block$soundtype.getFrequency());
					}
				} else if (this.gallopTime <= 5) {
					this.playSound("mob.horse.wood", block$soundtype.getVolume() * 0.15F, block$soundtype.getFrequency());
				}
			} else if (block$soundtype == Block.soundTypeWood) {
				this.playSound("mob.horse.wood", block$soundtype.getVolume() * 0.15F, block$soundtype.getFrequency());
			} else {
				this.playSound("mob.horse.soft", block$soundtype.getVolume() * 0.15F, block$soundtype.getFrequency());
			}
		}
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(horseJumpStrength);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(53.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22499999403953552D);
	}

	/**
	 * Will return how many at most can spawn in a chunk at once.
	 */
	public int getMaxSpawnedInChunk() {
		return 6;
	}

	public int getMaxTemper() {
		return 100;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume() {
		return 0.8F;
	}

	/**
	 * Get number of ticks, at least during which the living entity will be silent.
	 */
	public int getTalkInterval() {
		return 400;
	}

	public boolean func_110239_cn() {
		return this.getHorseType() == 0 || this.getHorseArmorIndexSynced() > 0;
	}

	private void resetTexturePrefix() {
		this.texturePrefix = null;
	}

	public boolean func_175507_cI() {
		return this.field_175508_bO;
	}

	private void setHorseTexturePaths() {
		this.texturePrefix = "horse/";
		this.horseTexturesArray[0] = null;
		this.horseTexturesArray[1] = null;
		this.horseTexturesArray[2] = null;
		int i = this.getHorseType();
		int j = this.getHorseVariant();

		if (i == 0) {
			int k = j & 255;
			int l = (j & 65280) >> 8;

			if (k >= horseTextures.length) {
				this.field_175508_bO = false;
				return;
			}

			this.horseTexturesArray[0] = horseTextures[k];
			this.texturePrefix = this.texturePrefix + HORSE_TEXTURES_ABBR[k];

			if (l >= horseMarkingTextures.length) {
				this.field_175508_bO = false;
				return;
			}

			this.horseTexturesArray[1] = horseMarkingTextures[l];
			this.texturePrefix = this.texturePrefix + HORSE_MARKING_TEXTURES_ABBR[l];
		} else {
			this.horseTexturesArray[0] = "";
			this.texturePrefix = this.texturePrefix + "_" + i + "_";
		}

		int i1 = this.getHorseArmorIndexSynced();

		if (i1 >= horseArmorTextures.length) {
			this.field_175508_bO = false;
		} else {
			this.horseTexturesArray[2] = horseArmorTextures[i1];
			this.texturePrefix = this.texturePrefix + HORSE_ARMOR_TEXTURES_ABBR[i1];
			this.field_175508_bO = true;
		}
	}

	public String getHorseTexture() {
		if (this.texturePrefix == null) {
			this.setHorseTexturePaths();
		}

		return this.texturePrefix;
	}

	public String[] getVariantTexturePaths() {
		if (this.texturePrefix == null) {
			this.setHorseTexturePaths();
		}

		return this.horseTexturesArray;
	}

	public void openGUI(EntityPlayer playerEntity) {
		if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == playerEntity) && this.isTame()) {
			this.horseChest.setCustomName(this.getName());
			playerEntity.displayGUIHorse(this, this.horseChest);
		}
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow, gets
	 * into the saddle on a pig.
	 */
	public boolean interact(EntityPlayer player) {
		ItemStack itemstack = player.inventory.getCurrentItem();

		if (itemstack != null && itemstack.getItem() == Items.spawn_egg) {
			return super.interact(player);
		} else if (!this.isTame() && this.isUndead()) {
			return false;
		} else if (this.isTame() && this.isAdultHorse() && player.isSneaking()) {
			this.openGUI(player);
			return true;
		} else if (this.func_110253_bW() && this.riddenByEntity != null) {
			return super.interact(player);
		} else {
			if (itemstack != null) {
				boolean flag = false;

				if (this.canWearArmor()) {
					int i = -1;

					if (itemstack.getItem() == Items.iron_horse_armor) {
						i = 1;
					} else if (itemstack.getItem() == Items.golden_horse_armor) {
						i = 2;
					} else if (itemstack.getItem() == Items.diamond_horse_armor) {
						i = 3;
					}

					if (i >= 0) {
						if (!this.isTame()) {
							this.makeHorseRearWithSound();
							return true;
						}

						this.openGUI(player);
						return true;
					}
				}

				if (!flag && !this.isUndead()) {
					float f = 0.0F;
					int j = 0;
					int k = 0;

					if (itemstack.getItem() == Items.wheat) {
						f = 2.0F;
						j = 20;
						k = 3;
					} else if (itemstack.getItem() == Items.sugar) {
						f = 1.0F;
						j = 30;
						k = 3;
					} else if (Block.getBlockFromItem(itemstack.getItem()) == Blocks.hay_block) {
						f = 20.0F;
						j = 180;
					} else if (itemstack.getItem() == Items.apple) {
						f = 3.0F;
						j = 60;
						k = 3;
					} else if (itemstack.getItem() == Items.golden_carrot) {
						f = 4.0F;
						j = 60;
						k = 5;

						if (this.isTame() && this.getGrowingAge() == 0) {
							flag = true;
							this.setInLove(player);
						}
					} else if (itemstack.getItem() == Items.golden_apple) {
						f = 10.0F;
						j = 240;
						k = 10;

						if (this.isTame() && this.getGrowingAge() == 0) {
							flag = true;
							this.setInLove(player);
						}
					}

					if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
						this.heal(f);
						flag = true;
					}

					if (!this.isAdultHorse() && j > 0) {
						this.addGrowth(j);
						flag = true;
					}

					if (k > 0 && (flag || !this.isTame()) && k < this.getMaxTemper()) {
						flag = true;
						this.increaseTemper(k);
					}

					if (flag) {
						this.func_110266_cB();
					}
				}

				if (!this.isTame() && !flag) {
					if (itemstack != null && itemstack.interactWithEntity(player, this)) {
						return true;
					}

					this.makeHorseRearWithSound();
					return true;
				}

				if (!flag && this.canCarryChest() && !this.isChested() && itemstack.getItem() == Item.getItemFromBlock(Blocks.chest)) {
					this.setChested(true);
					this.playSound("mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
					flag = true;
					this.initHorseChest();
				}

				if (!flag && this.func_110253_bW() && !this.isHorseSaddled() && itemstack.getItem() == Items.saddle) {
					this.openGUI(player);
					return true;
				}

				if (flag) {
					if (!player.capabilities.isCreativeMode && --itemstack.stackSize == 0) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack) null);
					}

					return true;
				}
			}

			if (this.func_110253_bW() && this.riddenByEntity == null) {
				if (itemstack != null && itemstack.interactWithEntity(player, this)) {
					return true;
				} else {
					this.mountTo(player);
					return true;
				}
			} else {
				return super.interact(player);
			}
		}
	}

	private void mountTo(EntityPlayer player) {
		player.rotationYaw = this.rotationYaw;
		player.rotationPitch = this.rotationPitch;
		this.setEatingHaystack(false);
		this.setRearing(false);

		if (!this.worldObj.isRemote) {
			player.mountEntity(this);
		}
	}

	/**
	 * Return true if the horse entity can wear an armor
	 */
	public boolean canWearArmor() {
		return this.getHorseType() == 0;
	}

	/**
	 * Return true if the horse entity can carry a chest.
	 */
	public boolean canCarryChest() {
		int i = this.getHorseType();
		return i == 2 || i == 1;
	}

	/**
	 * Dead and sleeping entities cannot move
	 */
	protected boolean isMovementBlocked() {
		return this.riddenByEntity != null && this.isHorseSaddled() ? true : this.isEatingHaystack() || this.isRearing();
	}

	/**
	 * Used to know if the horse can be leashed, if he can mate, or if we can
	 * interact with him
	 */
	public boolean isUndead() {
		int i = this.getHorseType();
		return i == 3 || i == 4;
	}

	/**
	 * Return true if the horse entity is sterile (Undead || Mule)
	 */
	public boolean isSterile() {
		return this.isUndead() || this.getHorseType() == 2;
	}

	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it
	 * (wheat, carrots or seeds depending on the animal type)
	 */
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	private void func_110210_cH() {
		this.field_110278_bp = 1;
	}

	/**
	 * Called when the mob's health reaches 0.
	 */
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);

		if (!this.worldObj.isRemote) {
			this.dropChestItems();
		}
	}

	/**
	 * Called frequently so the entity can update its state every tick as required.
	 * For example, zombies and skeletons use this to react to sunlight and start to
	 * burn.
	 */
	public void onLivingUpdate() {
		if (this.rand.nextInt(200) == 0) {
			this.func_110210_cH();
		}

		super.onLivingUpdate();

		if (!this.worldObj.isRemote) {
			if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
				this.heal(1.0F);
			}

			if (!this.isEatingHaystack() && this.riddenByEntity == null && this.rand.nextInt(300) == 0 && this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ))).getBlock() == Blocks.grass) {
				this.setEatingHaystack(true);
			}

			if (this.isEatingHaystack() && ++this.eatingHaystackCounter > 50) {
				this.eatingHaystackCounter = 0;
				this.setEatingHaystack(false);
			}

			if (this.isBreeding() && !this.isAdultHorse() && !this.isEatingHaystack()) {
				EntityHorse entityhorse = this.getClosestHorse(this, 16.0D);

				if (entityhorse != null && this.getDistanceSqToEntity(entityhorse) > 4.0D) {
					this.navigator.getPathToEntityLiving(entityhorse);
				}
			}
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		super.onUpdate();

		if (this.worldObj.isRemote && this.dataWatcher.hasObjectChanged()) {
			this.dataWatcher.func_111144_e();
			this.resetTexturePrefix();
		}

		if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30) {
			this.openMouthCounter = 0;
			this.setHorseWatchableBoolean(128, false);
		}

		if (!this.worldObj.isRemote && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20) {
			this.jumpRearingCounter = 0;
			this.setRearing(false);
		}

		if (this.field_110278_bp > 0 && ++this.field_110278_bp > 8) {
			this.field_110278_bp = 0;
		}

		if (this.field_110279_bq > 0) {
			++this.field_110279_bq;

			if (this.field_110279_bq > 300) {
				this.field_110279_bq = 0;
			}
		}

		this.prevHeadLean = this.headLean;

		if (this.isEatingHaystack()) {
			this.headLean += (1.0F - this.headLean) * 0.4F + 0.05F;

			if (this.headLean > 1.0F) {
				this.headLean = 1.0F;
			}
		} else {
			this.headLean += (0.0F - this.headLean) * 0.4F - 0.05F;

			if (this.headLean < 0.0F) {
				this.headLean = 0.0F;
			}
		}

		this.prevRearingAmount = this.rearingAmount;

		if (this.isRearing()) {
			this.prevHeadLean = this.headLean = 0.0F;
			this.rearingAmount += (1.0F - this.rearingAmount) * 0.4F + 0.05F;

			if (this.rearingAmount > 1.0F) {
				this.rearingAmount = 1.0F;
			}
		} else {
			this.field_110294_bI = false;
			this.rearingAmount += (0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F - 0.05F;

			if (this.rearingAmount < 0.0F) {
				this.rearingAmount = 0.0F;
			}
		}

		this.prevMouthOpenness = this.mouthOpenness;

		if (this.getHorseWatchableBoolean(128)) {
			this.mouthOpenness += (1.0F - this.mouthOpenness) * 0.7F + 0.05F;

			if (this.mouthOpenness > 1.0F) {
				this.mouthOpenness = 1.0F;
			}
		} else {
			this.mouthOpenness += (0.0F - this.mouthOpenness) * 0.7F - 0.05F;

			if (this.mouthOpenness < 0.0F) {
				this.mouthOpenness = 0.0F;
			}
		}
	}

	private void openHorseMouth() {
		if (!this.worldObj.isRemote) {
			this.openMouthCounter = 1;
			this.setHorseWatchableBoolean(128, true);
		}
	}

	/**
	 * Return true if the horse entity ready to mate. (no rider, not riding, tame,
	 * adult, not steril...)
	 */
	private boolean canMate() {
		return this.riddenByEntity == null && this.ridingEntity == null && this.isTame() && this.isAdultHorse() && !this.isSterile() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
	}

	public void setEating(boolean eating) {
		this.setHorseWatchableBoolean(32, eating);
	}

	public void setEatingHaystack(boolean p_110227_1_) {
		this.setEating(p_110227_1_);
	}

	public void setRearing(boolean rearing) {
		if (rearing) {
			this.setEatingHaystack(false);
		}

		this.setHorseWatchableBoolean(64, rearing);
	}

	private void makeHorseRear() {
		if (!this.worldObj.isRemote) {
			this.jumpRearingCounter = 1;
			this.setRearing(true);
		}
	}

	public void makeHorseRearWithSound() {
		this.makeHorseRear();
		String s = this.getAngrySoundName();

		if (s != null) {
			this.playSound(s, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	public void dropChestItems() {
		this.dropItemsInChest(this, this.horseChest);
		this.dropChests();
	}

	private void dropItemsInChest(Entity entityIn, AnimalChest animalChestIn) {
		if (animalChestIn != null && !this.worldObj.isRemote) {
			for (int i = 0; i < animalChestIn.getSizeInventory(); ++i) {
				ItemStack itemstack = animalChestIn.getStackInSlot(i);

				if (itemstack != null) {
					this.entityDropItem(itemstack, 0.0F);
				}
			}
		}
	}

	public boolean setTamedBy(EntityPlayer player) {
		this.setOwnerId(player.getUniqueID().toString());
		this.setHorseTamed(true);
		return true;
	}

	/**
	 * Moves the entity based on the specified heading. Args: strafe, forward
	 */
	public void moveEntityWithHeading(float strafe, float forward) {
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase && this.isHorseSaddled()) {
			this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
			this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
			strafe = ((EntityLivingBase) this.riddenByEntity).moveStrafing * 0.5F;
			forward = ((EntityLivingBase) this.riddenByEntity).moveForward;

			if (forward <= 0.0F) {
				forward *= 0.25F;
				this.gallopTime = 0;
			}

			if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.field_110294_bI) {
				strafe = 0.0F;
				forward = 0.0F;
			}

			if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
				this.motionY = this.getHorseJumpStrength() * (double) this.jumpPower;

				if (this.isPotionActive(Potion.jump)) {
					this.motionY += (double) ((float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
				}

				this.setHorseJumping(true);
				this.isAirBorne = true;

				if (forward > 0.0F) {
					float f = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
					float f1 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
					this.motionX += (double) (-0.4F * f * this.jumpPower);
					this.motionZ += (double) (0.4F * f1 * this.jumpPower);
					this.playSound("mob.horse.jump", 0.4F, 1.0F);
				}

				this.jumpPower = 0.0F;
			}

			this.stepHeight = 1.0F;
			this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;

			if (!this.worldObj.isRemote) {
				this.setAIMoveSpeed((float) this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
				super.moveEntityWithHeading(strafe, forward);
			}

			if (this.onGround) {
				this.jumpPower = 0.0F;
				this.setHorseJumping(false);
			}

			this.prevLimbSwingAmount = this.limbSwingAmount;
			double d1 = this.posX - this.prevPosX;
			double d0 = this.posZ - this.prevPosZ;
			float f2 = MathHelper.sqrt_double(d1 * d1 + d0 * d0) * 4.0F;

			if (f2 > 1.0F) {
				f2 = 1.0F;
			}

			this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			this.stepHeight = 0.5F;
			this.jumpMovementFactor = 0.02F;
			super.moveEntityWithHeading(strafe, forward);
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		super.writeEntityToNBT(tagCompound);
		tagCompound.setBoolean("EatingHaystack", this.isEatingHaystack());
		tagCompound.setBoolean("ChestedHorse", this.isChested());
		tagCompound.setBoolean("HasReproduced", this.getHasReproduced());
		tagCompound.setBoolean("Bred", this.isBreeding());
		tagCompound.setInteger("Type", this.getHorseType());
		tagCompound.setInteger("Variant", this.getHorseVariant());
		tagCompound.setInteger("Temper", this.getTemper());
		tagCompound.setBoolean("Tame", this.isTame());
		tagCompound.setString("OwnerUUID", this.getOwnerId());

		if (this.isChested()) {
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 2; i < this.horseChest.getSizeInventory(); ++i) {
				ItemStack itemstack = this.horseChest.getStackInSlot(i);

				if (itemstack != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setByte("Slot", (byte) i);
					itemstack.writeToNBT(nbttagcompound);
					nbttaglist.appendTag(nbttagcompound);
				}
			}

			tagCompound.setTag("Items", nbttaglist);
		}

		if (this.horseChest.getStackInSlot(1) != null) {
			tagCompound.setTag("ArmorItem", this.horseChest.getStackInSlot(1).writeToNBT(new NBTTagCompound()));
		}

		if (this.horseChest.getStackInSlot(0) != null) {
			tagCompound.setTag("SaddleItem", this.horseChest.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
		}
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
		super.readEntityFromNBT(tagCompund);
		this.setEatingHaystack(tagCompund.getBoolean("EatingHaystack"));
		this.setBreeding(tagCompund.getBoolean("Bred"));
		this.setChested(tagCompund.getBoolean("ChestedHorse"));
		this.setHasReproduced(tagCompund.getBoolean("HasReproduced"));
		this.setHorseType(tagCompund.getInteger("Type"));
		this.setHorseVariant(tagCompund.getInteger("Variant"));
		this.setTemper(tagCompund.getInteger("Temper"));
		this.setHorseTamed(tagCompund.getBoolean("Tame"));
		String s = "";

		if (tagCompund.hasKey("OwnerUUID", 8)) {
			s = tagCompund.getString("OwnerUUID");
		} else {
			String s1 = tagCompund.getString("Owner");
			s = PreYggdrasilConverter.getStringUUIDFromName(s1);
		}

		if (s.length() > 0) {
			this.setOwnerId(s);
		}

		IAttributeInstance iattributeinstance = this.getAttributeMap().getAttributeInstanceByName("Speed");

		if (iattributeinstance != null) {
			this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(iattributeinstance.getBaseValue() * 0.25D);
		}

		if (this.isChested()) {
			NBTTagList nbttaglist = tagCompund.getTagList("Items", 10);
			this.initHorseChest();

			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("Slot") & 255;

				if (j >= 2 && j < this.horseChest.getSizeInventory()) {
					this.horseChest.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
				}
			}
		}

		if (tagCompund.hasKey("ArmorItem", 10)) {
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("ArmorItem"));

			if (itemstack != null && isArmorItem(itemstack.getItem())) {
				this.horseChest.setInventorySlotContents(1, itemstack);
			}
		}

		if (tagCompund.hasKey("SaddleItem", 10)) {
			ItemStack itemstack1 = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("SaddleItem"));

			if (itemstack1 != null && itemstack1.getItem() == Items.saddle) {
				this.horseChest.setInventorySlotContents(0, itemstack1);
			}
		} else if (tagCompund.getBoolean("Saddle")) {
			this.horseChest.setInventorySlotContents(0, new ItemStack(Items.saddle));
		}

		this.updateHorseSlots();
	}

	/**
	 * Returns true if the mob is currently able to mate with the specified mob.
	 */
	public boolean canMateWith(EntityAnimal otherAnimal) {
		if (otherAnimal == this) {
			return false;
		} else if (otherAnimal.getClass() != this.getClass()) {
			return false;
		} else {
			EntityHorse entityhorse = (EntityHorse) otherAnimal;

			if (this.canMate() && entityhorse.canMate()) {
				int i = this.getHorseType();
				int j = entityhorse.getHorseType();
				return i == j || i == 0 && j == 1 || i == 1 && j == 0;
			} else {
				return false;
			}
		}
	}

	public EntityAgeable createChild(EntityAgeable ageable) {
		EntityHorse entityhorse = (EntityHorse) ageable;
		EntityHorse entityhorse1 = new EntityHorse(this.worldObj);
		int i = this.getHorseType();
		int j = entityhorse.getHorseType();
		int k = 0;

		if (i == j) {
			k = i;
		} else if (i == 0 && j == 1 || i == 1 && j == 0) {
			k = 2;
		}

		if (k == 0) {
			int i1 = this.rand.nextInt(9);
			int l;

			if (i1 < 4) {
				l = this.getHorseVariant() & 255;
			} else if (i1 < 8) {
				l = entityhorse.getHorseVariant() & 255;
			} else {
				l = this.rand.nextInt(7);
			}

			int j1 = this.rand.nextInt(5);

			if (j1 < 2) {
				l = l | this.getHorseVariant() & 65280;
			} else if (j1 < 4) {
				l = l | entityhorse.getHorseVariant() & 65280;
			} else {
				l = l | this.rand.nextInt(5) << 8 & 65280;
			}

			entityhorse1.setHorseVariant(l);
		}

		entityhorse1.setHorseType(k);
		double d1 = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() + ageable.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() + (double) this.getModifiedMaxHealth();
		entityhorse1.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(d1 / 3.0D);
		double d2 = this.getEntityAttribute(horseJumpStrength).getBaseValue() + ageable.getEntityAttribute(horseJumpStrength).getBaseValue() + this.getModifiedJumpStrength();
		entityhorse1.getEntityAttribute(horseJumpStrength).setBaseValue(d2 / 3.0D);
		double d0 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() + ageable.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() + this.getModifiedMovementSpeed();
		entityhorse1.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(d0 / 3.0D);
		return entityhorse1;
	}

	/**
	 * Called only once on an entity when first time spawned, via egg, mob spawner,
	 * natural spawning etc, but not called when entity is reloaded from nbt. Mainly
	 * used for initializing attributes and inventory
	 */
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		int i = 0;
		int j = 0;

		if (livingdata instanceof EntityHorse.GroupData) {
			i = ((EntityHorse.GroupData) livingdata).horseType;
			j = ((EntityHorse.GroupData) livingdata).horseVariant & 255 | this.rand.nextInt(5) << 8;
		} else {
			if (this.rand.nextInt(10) == 0) {
				i = 1;
			} else {
				int k = this.rand.nextInt(7);
				int l = this.rand.nextInt(5);
				i = 0;
				j = k | l << 8;
			}

			livingdata = new EntityHorse.GroupData(i, j);
		}

		this.setHorseType(i);
		this.setHorseVariant(j);

		if (this.rand.nextInt(5) == 0) {
			this.setGrowingAge(-24000);
		}

		if (i != 4 && i != 3) {
			this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue((double) this.getModifiedMaxHealth());

			if (i == 0) {
				this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.getModifiedMovementSpeed());
			} else {
				this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.17499999701976776D);
			}
		} else {
			this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
			this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20000000298023224D);
		}

		if (i != 2 && i != 1) {
			this.getEntityAttribute(horseJumpStrength).setBaseValue(this.getModifiedJumpStrength());
		} else {
			this.getEntityAttribute(horseJumpStrength).setBaseValue(0.5D);
		}

		this.setHealth(this.getMaxHealth());
		return livingdata;
	}

	public float getGrassEatingAmount(float p_110258_1_) {
		return this.prevHeadLean + (this.headLean - this.prevHeadLean) * p_110258_1_;
	}

	public float getRearingAmount(float p_110223_1_) {
		return this.prevRearingAmount + (this.rearingAmount - this.prevRearingAmount) * p_110223_1_;
	}

	public float getMouthOpennessAngle(float p_110201_1_) {
		return this.prevMouthOpenness + (this.mouthOpenness - this.prevMouthOpenness) * p_110201_1_;
	}

	public void setJumpPower(int jumpPowerIn) {
		if (this.isHorseSaddled()) {
			if (jumpPowerIn < 0) {
				jumpPowerIn = 0;
			} else {
				this.field_110294_bI = true;
				this.makeHorseRear();
			}

			if (jumpPowerIn >= 90) {
				this.jumpPower = 1.0F;
			} else {
				this.jumpPower = 0.4F + 0.4F * (float) jumpPowerIn / 90.0F;
			}
		}
	}

	/**
	 * "Spawns particles for the horse entity. par1 tells whether to spawn hearts.
	 * If it is false, it spawns smoke."
	 */
	protected void spawnHorseParticles(boolean p_110216_1_) {
		EnumParticleTypes enumparticletypes = p_110216_1_ ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL;

		for (int i = 0; i < 7; ++i) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			this.worldObj.spawnParticle(enumparticletypes, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2, new int[0]);
		}
	}

	public void handleStatusUpdate(byte id) {
		if (id == 7) {
			this.spawnHorseParticles(true);
		} else if (id == 6) {
			this.spawnHorseParticles(false);
		} else {
			super.handleStatusUpdate(id);
		}
	}

	public void updateRiderPosition() {
		super.updateRiderPosition();

		if (this.prevRearingAmount > 0.0F) {
			float f = MathHelper.sin(this.renderYawOffset * (float) Math.PI / 180.0F);
			float f1 = MathHelper.cos(this.renderYawOffset * (float) Math.PI / 180.0F);
			float f2 = 0.7F * this.prevRearingAmount;
			float f3 = 0.15F * this.prevRearingAmount;
			this.riddenByEntity.setPosition(this.posX + (double) (f2 * f), this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset() + (double) f3, this.posZ - (double) (f2 * f1));

			if (this.riddenByEntity instanceof EntityLivingBase) {
				((EntityLivingBase) this.riddenByEntity).renderYawOffset = this.renderYawOffset;
			}
		}
	}

	/**
	 * Returns randomized max health
	 */
	private float getModifiedMaxHealth() {
		return 15.0F + (float) this.rand.nextInt(8) + (float) this.rand.nextInt(9);
	}

	/**
	 * Returns randomized jump strength
	 */
	private double getModifiedJumpStrength() {
		return 0.4000000059604645D + this.rand.nextDouble() * 0.2D + this.rand.nextDouble() * 0.2D + this.rand.nextDouble() * 0.2D;
	}

	/**
	 * Returns randomized movement speed
	 */
	private double getModifiedMovementSpeed() {
		return (0.44999998807907104D + this.rand.nextDouble() * 0.3D + this.rand.nextDouble() * 0.3D + this.rand.nextDouble() * 0.3D) * 0.25D;
	}

	/**
	 * Returns true if given item is horse armor
	 */
	public static boolean isArmorItem(Item p_146085_0_) {
		return p_146085_0_ == Items.iron_horse_armor || p_146085_0_ == Items.golden_horse_armor || p_146085_0_ == Items.diamond_horse_armor;
	}

	/**
	 * returns true if this entity is by a ladder, false otherwise
	 */
	public boolean isOnLadder() {
		return false;
	}

	public float getEyeHeight() {
		return this.height;
	}

	public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
		if (inventorySlot == 499 && this.canCarryChest()) {
			if (itemStackIn == null && this.isChested()) {
				this.setChested(false);
				this.initHorseChest();
				return true;
			}

			if (itemStackIn != null && itemStackIn.getItem() == Item.getItemFromBlock(Blocks.chest) && !this.isChested()) {
				this.setChested(true);
				this.initHorseChest();
				return true;
			}
		}

		int i = inventorySlot - 400;

		if (i >= 0 && i < 2 && i < this.horseChest.getSizeInventory()) {
			if (i == 0 && itemStackIn != null && itemStackIn.getItem() != Items.saddle) {
				return false;
			} else if (i != 1 || (itemStackIn == null || isArmorItem(itemStackIn.getItem())) && this.canWearArmor()) {
				this.horseChest.setInventorySlotContents(i, itemStackIn);
				this.updateHorseSlots();
				return true;
			} else {
				return false;
			}
		} else {
			int j = inventorySlot - 500 + 2;

			if (j >= 2 && j < this.horseChest.getSizeInventory()) {
				this.horseChest.setInventorySlotContents(j, itemStackIn);
				return true;
			} else {
				return false;
			}
		}
	}

	public static class GroupData implements IEntityLivingData {
		public int horseType;
		public int horseVariant;

		public GroupData(int type, int variant) {
			this.horseType = type;
			this.horseVariant = variant;
		}
	}
}
