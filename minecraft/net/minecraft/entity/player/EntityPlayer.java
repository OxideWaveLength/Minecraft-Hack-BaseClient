package net.minecraft.entity.player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

@SuppressWarnings("incomplete-switch")
public abstract class EntityPlayer extends EntityLivingBase {

	/** Inventory of the player */
	public InventoryPlayer inventory = new InventoryPlayer(this);
	private InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();

	/**
	 * The Container for the player's inventory (which opens when they press E)
	 */
	public Container inventoryContainer;

	/** The Container the player has open. */
	public Container openContainer;

	/** The food object of the player, the general hunger logic. */
	protected FoodStats foodStats = new FoodStats();

	/**
	 * Used to tell if the player pressed jump twice. If this is at 0 and it's
	 * pressed (And they are allowed to fly, as defined in the player's
	 * movementInput) it sets this to 7. If it's pressed and it's greater than 0
	 * enable fly.
	 */
	protected int flyToggleTimer;
	public float prevCameraYaw;
	public float cameraYaw;

	/**
	 * Used by EntityPlayer to prevent too many xp orbs from getting absorbed at
	 * once.
	 */
	public int xpCooldown;
	public double prevChasingPosX;
	public double prevChasingPosY;
	public double prevChasingPosZ;
	public double chasingPosX;
	public double chasingPosY;
	public double chasingPosZ;

	/** Boolean value indicating weather a player is sleeping or not */
	protected boolean sleeping;

	/** the current location of the player */
	public BlockPos playerLocation;
	private int sleepTimer;
	public float renderOffsetX;
	public float renderOffsetY;
	public float renderOffsetZ;

	/** holds the spawn chunk of the player */
	private BlockPos spawnChunk;

	/**
	 * Whether this player's spawn point is forced, preventing execution of bed
	 * checks.
	 */
	private boolean spawnForced;

	/** Holds the coordinate of the player when enter a minecraft to ride. */
	private BlockPos startMinecartRidingCoordinate;

	/** The player's capabilities. (See class PlayerCapabilities) */
	public PlayerCapabilities capabilities = new PlayerCapabilities();

	/** The current experience level the player is on. */
	public int experienceLevel;

	/**
	 * The total amount of experience the player has. This also includes the amount
	 * of experience within their Experience Bar.
	 */
	public int experienceTotal;

	/**
	 * The current amount of experience the player has within their Experience Bar.
	 */
	public float experience;
	private int xpSeed;

	/**
	 * This is the item that is in use when the player is holding down the
	 * useItemButton (e.g., bow, food, sword)
	 */
	private ItemStack itemInUse;

	/**
	 * This field starts off equal to getMaxItemUseDuration and is decremented on
	 * each tick
	 */
	private int itemInUseCount;
	protected float speedOnGround = 0.1F;
	protected float speedInAir = 0.02F;
	private int lastXPSound;

	/** The player's unique game profile */
	private final GameProfile gameProfile;
	private boolean hasReducedDebug = false;

	/**
	 * An instance of a fishing rod's hook. If this isn't null, the icon image of
	 * the fishing rod is slightly different
	 */
	public EntityFishHook fishEntity;

	public EntityPlayer(World worldIn, GameProfile gameProfileIn) {
		super(worldIn);
		this.entityUniqueID = getUUID(gameProfileIn);
		this.gameProfile = gameProfileIn;
		this.inventoryContainer = new ContainerPlayer(this.inventory, !worldIn.isRemote, this);
		this.openContainer = this.inventoryContainer;
		BlockPos blockpos = worldIn.getSpawnPoint();
		this.setLocationAndAngles((double) blockpos.getX() + 0.5D, (double) (blockpos.getY() + 1), (double) blockpos.getZ() + 0.5D, 0.0F, 0.0F);
		this.field_70741_aB = 180.0F;
		this.fireResistance = 20;
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.10000000149011612D);
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
		this.dataWatcher.addObject(17, Float.valueOf(0.0F));
		this.dataWatcher.addObject(18, Integer.valueOf(0));
		this.dataWatcher.addObject(10, Byte.valueOf((byte) 0));
	}

	/**
	 * returns the ItemStack containing the itemInUse
	 */
	public ItemStack getItemInUse() {
		return this.itemInUse;
	}

	/**
	 * Returns the item in use count
	 */
	public int getItemInUseCount() {
		return this.itemInUseCount;
	}

	/**
	 * Checks if the entity is currently using an item (e.g., bow, food, sword) by
	 * holding down the useItemButton
	 */
	public boolean isUsingItem() {
		return this.itemInUse != null;
	}

	/**
	 * gets the duration for how long the current itemInUse has been in use
	 */
	public int getItemInUseDuration() {
		return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
	}

	public void stopUsingItem() {
		if (this.itemInUse != null) {
			this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
		}

		this.clearItemInUse();
	}

	public void clearItemInUse() {
		this.itemInUse = null;
		this.itemInUseCount = 0;

		if (!this.worldObj.isRemote) {
			this.setEating(false);
		}
	}

	public boolean isBlocking() {
		return this.isUsingItem() && this.itemInUse.getItem().getItemUseAction(this.itemInUse) == EnumAction.BLOCK;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		this.noClip = this.isSpectator();

		if (this.isSpectator()) {
			this.onGround = false;
		}

		if (this.itemInUse != null) {
			ItemStack itemstack = this.inventory.getCurrentItem();

			if (itemstack == this.itemInUse) {
				if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0) {
					this.updateItemUse(itemstack, 5);
				}

				if (--this.itemInUseCount == 0 && !this.worldObj.isRemote) {
					this.onItemUseFinish();
				}
			} else {
				this.clearItemInUse();
			}
		}

		if (this.xpCooldown > 0) {
			--this.xpCooldown;
		}

		if (this.isPlayerSleeping()) {
			++this.sleepTimer;

			if (this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}

			if (!this.worldObj.isRemote) {
				if (!this.isInBed()) {
					this.wakeUpPlayer(true, true, false);
				} else if (this.worldObj.isDaytime()) {
					this.wakeUpPlayer(false, true, true);
				}
			}
		} else if (this.sleepTimer > 0) {
			++this.sleepTimer;

			if (this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}

		super.onUpdate();

		if (!this.worldObj.isRemote && this.openContainer != null && !this.openContainer.canInteractWith(this)) {
			this.closeScreen();
			this.openContainer = this.inventoryContainer;
		}

		if (this.isBurning() && this.capabilities.disableDamage) {
			this.extinguish();
		}

		this.prevChasingPosX = this.chasingPosX;
		this.prevChasingPosY = this.chasingPosY;
		this.prevChasingPosZ = this.chasingPosZ;
		double d5 = this.posX - this.chasingPosX;
		double d0 = this.posY - this.chasingPosY;
		double d1 = this.posZ - this.chasingPosZ;
		double d2 = 10.0D;

		if (d5 > d2) {
			this.prevChasingPosX = this.chasingPosX = this.posX;
		}

		if (d1 > d2) {
			this.prevChasingPosZ = this.chasingPosZ = this.posZ;
		}

		if (d0 > d2) {
			this.prevChasingPosY = this.chasingPosY = this.posY;
		}

		if (d5 < -d2) {
			this.prevChasingPosX = this.chasingPosX = this.posX;
		}

		if (d1 < -d2) {
			this.prevChasingPosZ = this.chasingPosZ = this.posZ;
		}

		if (d0 < -d2) {
			this.prevChasingPosY = this.chasingPosY = this.posY;
		}

		this.chasingPosX += d5 * 0.25D;
		this.chasingPosZ += d1 * 0.25D;
		this.chasingPosY += d0 * 0.25D;

		if (this.ridingEntity == null) {
			this.startMinecartRidingCoordinate = null;
		}

		if (!this.worldObj.isRemote) {
			this.foodStats.onUpdate(this);
			this.triggerAchievement(StatList.minutesPlayedStat);

			if (this.isEntityAlive()) {
				this.triggerAchievement(StatList.timeSinceDeathStat);
			}
		}

		int i = 29999999;
		double d3 = MathHelper.clamp_double(this.posX, -2.9999999E7D, 2.9999999E7D);
		double d4 = MathHelper.clamp_double(this.posZ, -2.9999999E7D, 2.9999999E7D);

		if (d3 != this.posX || d4 != this.posZ) {
			this.setPosition(d3, this.posY, d4);
		}
	}

	/**
	 * Return the amount of time this entity should stay in a portal before being
	 * transported.
	 */
	public int getMaxInPortalTime() {
		return this.capabilities.disableDamage ? 0 : 80;
	}

	protected String getSwimSound() {
		return "game.player.swim";
	}

	protected String getSplashSound() {
		return "game.player.swim.splash";
	}

	/**
	 * Return the amount of cooldown before this entity can use a portal again.
	 */
	public int getPortalCooldown() {
		return 10;
	}

	public void playSound(String name, float volume, float pitch) {
		this.worldObj.playSoundToNearExcept(this, name, volume, pitch);
	}

	/**
	 * Plays sounds and makes particles for item in use state
	 */
	protected void updateItemUse(ItemStack itemStackIn, int p_71010_2_) {
		if (itemStackIn.getItemUseAction() == EnumAction.DRINK) {
			this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (itemStackIn.getItemUseAction() == EnumAction.EAT) {
			for (int i = 0; i < p_71010_2_; ++i) {
				Vec3 vec3 = new Vec3(((double) this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
				vec3 = vec3.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
				vec3 = vec3.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
				double d0 = (double) (-this.rand.nextFloat()) * 0.6D - 0.3D;
				Vec3 vec31 = new Vec3(((double) this.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
				vec31 = vec31.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
				vec31 = vec31.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
				vec31 = vec31.addVector(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ);

				if (itemStackIn.getHasSubtypes()) {
					this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord, new int[] { Item.getIdFromItem(itemStackIn.getItem()), itemStackIn.getMetadata() });
				} else {
					this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord, new int[] { Item.getIdFromItem(itemStackIn.getItem()) });
				}
			}

			this.playSound("random.eat", 0.5F + 0.5F * (float) this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		}
	}

	/**
	 * Used for when item use count runs out, ie: eating completed
	 */
	protected void onItemUseFinish() {
		if (this.itemInUse != null) {
			this.updateItemUse(this.itemInUse, 16);
			int i = this.itemInUse.stackSize;
			ItemStack itemstack = this.itemInUse.onItemUseFinish(this.worldObj, this);

			if (itemstack != this.itemInUse || itemstack != null && itemstack.stackSize != i) {
				this.inventory.mainInventory[this.inventory.currentItem] = itemstack;

				if (itemstack.stackSize == 0) {
					this.inventory.mainInventory[this.inventory.currentItem] = null;
				}
			}

			this.clearItemInUse();
		}
	}

	public void handleStatusUpdate(byte id) {
		if (id == 9) {
			this.onItemUseFinish();
		} else if (id == 23) {
			this.hasReducedDebug = false;
		} else if (id == 22) {
			this.hasReducedDebug = true;
		} else {
			super.handleStatusUpdate(id);
		}
	}

	/**
	 * Dead and sleeping entities cannot move
	 */
	protected boolean isMovementBlocked() {
		return this.getHealth() <= 0.0F || this.isPlayerSleeping();
	}

	/**
	 * set current crafting inventory back to the 2x2 square
	 */
	protected void closeScreen() {
		this.openContainer = this.inventoryContainer;
	}

	/**
	 * Handles updating while being ridden by an entity
	 */
	public void updateRidden() {
		if (!this.worldObj.isRemote && this.isSneaking()) {
			this.mountEntity((Entity) null);
			this.setSneaking(false);
		} else {
			double d0 = this.posX;
			double d1 = this.posY;
			double d2 = this.posZ;
			float f = this.rotationYaw;
			float f1 = this.rotationPitch;
			super.updateRidden();
			this.prevCameraYaw = this.cameraYaw;
			this.cameraYaw = 0.0F;
			this.addMountedMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);

			if (this.ridingEntity instanceof EntityPig) {
				this.rotationPitch = f1;
				this.rotationYaw = f;
				this.renderYawOffset = ((EntityPig) this.ridingEntity).renderYawOffset;
			}
		}
	}

	/**
	 * Keeps moving the entity up so it isn't colliding with blocks and other
	 * requirements for this entity to be spawned (only actually used on players
	 * though its also on Entity)
	 */
	public void preparePlayerToSpawn() {
		this.setSize(0.6F, 1.8F);
		super.preparePlayerToSpawn();
		this.setHealth(this.getMaxHealth());
		this.deathTime = 0;
	}

	protected void updateEntityActionState() {
		super.updateEntityActionState();
		this.updateArmSwingProgress();
		this.rotationYawHead = this.rotationYaw;
	}

	/**
	 * Called frequently so the entity can update its state every tick as required.
	 * For example, zombies and skeletons use this to react to sunlight and start to
	 * burn.
	 */
	public void onLivingUpdate() {
		if (this.flyToggleTimer > 0) {
			--this.flyToggleTimer;
		}

		if (this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL && this.worldObj.getGameRules().getBoolean("naturalRegeneration")) {
			if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
				this.heal(1.0F);
			}

			if (this.foodStats.needFood() && this.ticksExisted % 10 == 0) {
				this.foodStats.setFoodLevel(this.foodStats.getFoodLevel() + 1);
			}
		}

		this.inventory.decrementAnimations();
		this.prevCameraYaw = this.cameraYaw;
		super.onLivingUpdate();
		IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

		if (!this.worldObj.isRemote) {
			iattributeinstance.setBaseValue((double) this.capabilities.getWalkSpeed());
		}

		this.jumpMovementFactor = this.speedInAir;

		if (this.isSprinting()) {
			this.jumpMovementFactor = (float) ((double) this.jumpMovementFactor + (double) this.speedInAir * 0.3D);
		}

		this.setAIMoveSpeed((float) iattributeinstance.getAttributeValue());
		float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float f1 = (float) (Math.atan(-this.motionY * 0.20000000298023224D) * 15.0D);

		if (f > 0.1F) {
			f = 0.1F;
		}

		if (!this.onGround || this.getHealth() <= 0.0F) {
			f = 0.0F;
		}

		if (this.onGround || this.getHealth() <= 0.0F) {
			f1 = 0.0F;
		}

		this.cameraYaw += (f - this.cameraYaw) * 0.4F;
		this.cameraPitch += (f1 - this.cameraPitch) * 0.8F;

		if (this.getHealth() > 0.0F && !this.isSpectator()) {
			AxisAlignedBB axisalignedbb = null;

			if (this.ridingEntity != null && !this.ridingEntity.isDead) {
				axisalignedbb = this.getEntityBoundingBox().union(this.ridingEntity.getEntityBoundingBox()).expand(1.0D, 0.0D, 1.0D);
			} else {
				axisalignedbb = this.getEntityBoundingBox().expand(1.0D, 0.5D, 1.0D);
			}

			List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb);

			for (int i = 0; i < list.size(); ++i) {
				Entity entity = (Entity) list.get(i);

				if (!entity.isDead) {
					this.collideWithPlayer(entity);
				}
			}
		}
	}

	private void collideWithPlayer(Entity p_71044_1_) {
		p_71044_1_.onCollideWithPlayer(this);
	}

	public int getScore() {
		return this.dataWatcher.getWatchableObjectInt(18);
	}

	/**
	 * Set player's score
	 */
	public void setScore(int p_85040_1_) {
		this.dataWatcher.updateObject(18, Integer.valueOf(p_85040_1_));
	}

	/**
	 * Add to player's score
	 */
	public void addScore(int p_85039_1_) {
		int i = this.getScore();
		this.dataWatcher.updateObject(18, Integer.valueOf(i + p_85039_1_));
	}

	/**
	 * Called when the mob's health reaches 0.
	 */
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		this.setSize(0.2F, 0.2F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.motionY = 0.10000000149011612D;

		if (this.getName().equals("Notch")) {
			this.dropItem(new ItemStack(Items.apple, 1), true, false);
		}

		if (!this.worldObj.getGameRules().getBoolean("keepInventory")) {
			this.inventory.dropAllItems();
		}

		if (cause != null) {
			this.motionX = (double) (-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F);
			this.motionZ = (double) (-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F);
		} else {
			this.motionX = this.motionZ = 0.0D;
		}

		this.triggerAchievement(StatList.deathsStat);
		this.func_175145_a(StatList.timeSinceDeathStat);
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		return "game.player.hurt";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound() {
		return "game.player.die";
	}

	/**
	 * Adds a value to the player score. Currently not actually used and the entity
	 * passed in does nothing. Args: entity, scoreToAdd
	 */
	public void addToPlayerScore(Entity entityIn, int amount) {
		this.addScore(amount);
		Collection<ScoreObjective> collection = this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.totalKillCount);

		if (entityIn instanceof EntityPlayer) {
			this.triggerAchievement(StatList.playerKillsStat);
			collection.addAll(this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.playerKillCount));
			collection.addAll(this.func_175137_e(entityIn));
		} else {
			this.triggerAchievement(StatList.mobKillsStat);
		}

		for (ScoreObjective scoreobjective : collection) {
			Score score = this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreobjective);
			score.func_96648_a();
		}
	}

	private Collection<ScoreObjective> func_175137_e(Entity p_175137_1_) {
		ScorePlayerTeam scoreplayerteam = this.getWorldScoreboard().getPlayersTeam(this.getName());

		if (scoreplayerteam != null) {
			int i = scoreplayerteam.getChatFormat().getColorIndex();

			if (i >= 0 && i < IScoreObjectiveCriteria.field_178793_i.length) {
				for (ScoreObjective scoreobjective : this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.field_178793_i[i])) {
					Score score = this.getWorldScoreboard().getValueFromObjective(p_175137_1_.getName(), scoreobjective);
					score.func_96648_a();
				}
			}
		}

		ScorePlayerTeam scoreplayerteam1 = this.getWorldScoreboard().getPlayersTeam(p_175137_1_.getName());

		if (scoreplayerteam1 != null) {
			int j = scoreplayerteam1.getChatFormat().getColorIndex();

			if (j >= 0 && j < IScoreObjectiveCriteria.field_178792_h.length) {
				return this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.field_178792_h[j]);
			}
		}

		return Lists.<ScoreObjective>newArrayList();
	}

	/**
	 * Called when player presses the drop item key
	 */
	public EntityItem dropOneItem(boolean dropAll) {
		return this.dropItem(this.inventory.decrStackSize(this.inventory.currentItem, dropAll && this.inventory.getCurrentItem() != null ? this.inventory.getCurrentItem().stackSize : 1), false, true);
	}

	/**
	 * Args: itemstack, flag
	 */
	public EntityItem dropPlayerItemWithRandomChoice(ItemStack itemStackIn, boolean unused) {
		return this.dropItem(itemStackIn, false, false);
	}

	public EntityItem dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem) {
		if (droppedItem == null) {
			return null;
		} else if (droppedItem.stackSize == 0) {
			return null;
		} else {
			double d0 = this.posY - 0.30000001192092896D + (double) this.getEyeHeight();
			EntityItem entityitem = new EntityItem(this.worldObj, this.posX, d0, this.posZ, droppedItem);
			entityitem.setPickupDelay(40);

			if (traceItem) {
				entityitem.setThrower(this.getName());
			}

			if (dropAround) {
				float f = this.rand.nextFloat() * 0.5F;
				float f1 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
				entityitem.motionX = (double) (-MathHelper.sin(f1) * f);
				entityitem.motionZ = (double) (MathHelper.cos(f1) * f);
				entityitem.motionY = 0.20000000298023224D;
			} else {
				float f2 = 0.3F;
				entityitem.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f2);
				entityitem.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f2);
				entityitem.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f2 + 0.1F);
				float f3 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
				f2 = 0.02F * this.rand.nextFloat();
				entityitem.motionX += Math.cos((double) f3) * (double) f2;
				entityitem.motionY += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
				entityitem.motionZ += Math.sin((double) f3) * (double) f2;
			}

			this.joinEntityItemWithWorld(entityitem);

			if (traceItem) {
				this.triggerAchievement(StatList.dropStat);
			}

			return entityitem;
		}
	}

	/**
	 * Joins the passed in entity item with the world. Args: entityItem
	 */
	protected void joinEntityItemWithWorld(EntityItem itemIn) {
		this.worldObj.spawnEntityInWorld(itemIn);
	}

	/**
	 * Block hardness will be further counted in
	 * net/minecraft/block/Block.getPlayerRelativeBlockHardness
	 */
	public float getToolDigEfficiency(Block p_180471_1_) {
		float f = this.inventory.getStrVsBlock(p_180471_1_);

		if (f > 1.0F) {
			int i = EnchantmentHelper.getEfficiencyModifier(this);
			ItemStack itemstack = this.inventory.getCurrentItem();

			if (i > 0 && itemstack != null) {
				f += (float) (i * i + 1);
			}
		}

		if (this.isPotionActive(Potion.digSpeed)) {
			f *= 1.0F + (float) (this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
		}

		if (this.isPotionActive(Potion.digSlowdown)) {
			float f1 = 1.0F;

			switch (this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
			case 0:
				f1 = 0.3F;
				break;

			case 1:
				f1 = 0.09F;
				break;

			case 2:
				f1 = 0.0027F;
				break;

			case 3:
			default:
				f1 = 8.1E-4F;
			}

			f *= f1;
		}

		if (this.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
			f /= 5.0F;
		}

		if (!this.onGround) {
			f /= 5.0F;
		}

		return f;
	}

	/**
	 * Checks if the player has the ability to harvest a block (checks current
	 * inventory item for a tool if necessary)
	 */
	public boolean canHarvestBlock(Block blockToHarvest) {
		return this.inventory.canHeldItemHarvest(blockToHarvest);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
		super.readEntityFromNBT(tagCompund);
		this.entityUniqueID = getUUID(this.gameProfile);
		NBTTagList nbttaglist = tagCompund.getTagList("Inventory", 10);
		this.inventory.readFromNBT(nbttaglist);
		this.inventory.currentItem = tagCompund.getInteger("SelectedItemSlot");
		this.sleeping = tagCompund.getBoolean("Sleeping");
		this.sleepTimer = tagCompund.getShort("SleepTimer");
		this.experience = tagCompund.getFloat("XpP");
		this.experienceLevel = tagCompund.getInteger("XpLevel");
		this.experienceTotal = tagCompund.getInteger("XpTotal");
		this.xpSeed = tagCompund.getInteger("XpSeed");

		if (this.xpSeed == 0) {
			this.xpSeed = this.rand.nextInt();
		}

		this.setScore(tagCompund.getInteger("Score"));

		if (this.sleeping) {
			this.playerLocation = new BlockPos(this);
			this.wakeUpPlayer(true, true, false);
		}

		if (tagCompund.hasKey("SpawnX", 99) && tagCompund.hasKey("SpawnY", 99) && tagCompund.hasKey("SpawnZ", 99)) {
			this.spawnChunk = new BlockPos(tagCompund.getInteger("SpawnX"), tagCompund.getInteger("SpawnY"), tagCompund.getInteger("SpawnZ"));
			this.spawnForced = tagCompund.getBoolean("SpawnForced");
		}

		this.foodStats.readNBT(tagCompund);
		this.capabilities.readCapabilitiesFromNBT(tagCompund);

		if (tagCompund.hasKey("EnderItems", 9)) {
			NBTTagList nbttaglist1 = tagCompund.getTagList("EnderItems", 10);
			this.theInventoryEnderChest.loadInventoryFromNBT(nbttaglist1);
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		super.writeEntityToNBT(tagCompound);
		tagCompound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
		tagCompound.setInteger("SelectedItemSlot", this.inventory.currentItem);
		tagCompound.setBoolean("Sleeping", this.sleeping);
		tagCompound.setShort("SleepTimer", (short) this.sleepTimer);
		tagCompound.setFloat("XpP", this.experience);
		tagCompound.setInteger("XpLevel", this.experienceLevel);
		tagCompound.setInteger("XpTotal", this.experienceTotal);
		tagCompound.setInteger("XpSeed", this.xpSeed);
		tagCompound.setInteger("Score", this.getScore());

		if (this.spawnChunk != null) {
			tagCompound.setInteger("SpawnX", this.spawnChunk.getX());
			tagCompound.setInteger("SpawnY", this.spawnChunk.getY());
			tagCompound.setInteger("SpawnZ", this.spawnChunk.getZ());
			tagCompound.setBoolean("SpawnForced", this.spawnForced);
		}

		this.foodStats.writeNBT(tagCompound);
		this.capabilities.writeCapabilitiesToNBT(tagCompound);
		tagCompound.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
		ItemStack itemstack = this.inventory.getCurrentItem();

		if (itemstack != null && itemstack.getItem() != null) {
			tagCompound.setTag("SelectedItem", itemstack.writeToNBT(new NBTTagCompound()));
		}
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else if (this.capabilities.disableDamage && !source.canHarmInCreative()) {
			return false;
		} else {
			this.entityAge = 0;

			if (this.getHealth() <= 0.0F) {
				return false;
			} else {
				if (this.isPlayerSleeping() && !this.worldObj.isRemote) {
					this.wakeUpPlayer(true, true, false);
				}

				if (source.isDifficultyScaled()) {
					if (this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
						amount = 0.0F;
					}

					if (this.worldObj.getDifficulty() == EnumDifficulty.EASY) {
						amount = amount / 2.0F + 1.0F;
					}

					if (this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
						amount = amount * 3.0F / 2.0F;
					}
				}

				if (amount == 0.0F) {
					return false;
				} else {
					Entity entity = source.getEntity();

					if (entity instanceof EntityArrow && ((EntityArrow) entity).shootingEntity != null) {
						entity = ((EntityArrow) entity).shootingEntity;
					}

					return super.attackEntityFrom(source, amount);
				}
			}
		}
	}

	public boolean canAttackPlayer(EntityPlayer other) {
		Team team = this.getTeam();
		Team team1 = other.getTeam();
		return team == null ? true : (!team.isSameTeam(team1) ? true : team.getAllowFriendlyFire());
	}

	protected void damageArmor(float p_70675_1_) {
		this.inventory.damageArmor(p_70675_1_);
	}

	/**
	 * Returns the current armor value as determined by a call to
	 * InventoryPlayer.getTotalArmorValue
	 */
	public int getTotalArmorValue() {
		return this.inventory.getTotalArmorValue();
	}

	/**
	 * When searching for vulnerable players, if a player is invisible, the return
	 * value of this is the chance of seeing them anyway.
	 */
	public float getArmorVisibility() {
		int i = 0;

		for (ItemStack itemstack : this.inventory.armorInventory) {
			if (itemstack != null) {
				++i;
			}
		}

		return (float) i / (float) this.inventory.armorInventory.length;
	}

	/**
	 * Deals damage to the entity. If its a EntityPlayer then will take damage from
	 * the armor first and then health second with the reduced value. Args:
	 * damageAmount
	 */
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		if (!this.isEntityInvulnerable(damageSrc)) {
			if (!damageSrc.isUnblockable() && this.isBlocking() && damageAmount > 0.0F) {
				damageAmount = (1.0F + damageAmount) * 0.5F;
			}

			damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
			damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
			float f = damageAmount;
			damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - damageAmount));

			if (damageAmount != 0.0F) {
				this.addExhaustion(damageSrc.getHungerDamage());
				float f1 = this.getHealth();
				this.setHealth(this.getHealth() - damageAmount);
				this.getCombatTracker().trackDamage(damageSrc, f1, damageAmount);

				if (damageAmount < 3.4028235E37F) {
					this.addStat(StatList.damageTakenStat, Math.round(damageAmount * 10.0F));
				}
			}
		}
	}

	public void openEditSign(TileEntitySign signTile) {
	}

	public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic) {
	}

	public void displayVillagerTradeGui(IMerchant villager) {
	}

	/**
	 * Displays the GUI for interacting with a chest inventory. Args: chestInventory
	 */
	public void displayGUIChest(IInventory chestInventory) {
	}

	public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
	}

	public void displayGui(IInteractionObject guiOwner) {
	}

	/**
	 * Displays the GUI for interacting with a book.
	 */
	public void displayGUIBook(ItemStack bookStack) {
	}

	public boolean interactWith(Entity p_70998_1_) {
		if (this.isSpectator()) {
			if (p_70998_1_ instanceof IInventory) {
				this.displayGUIChest((IInventory) p_70998_1_);
			}

			return false;
		} else {
			ItemStack itemstack = this.getCurrentEquippedItem();
			ItemStack itemstack1 = itemstack != null ? itemstack.copy() : null;

			if (!p_70998_1_.interactFirst(this)) {
				if (itemstack != null && p_70998_1_ instanceof EntityLivingBase) {
					if (this.capabilities.isCreativeMode) {
						itemstack = itemstack1;
					}

					if (itemstack.interactWithEntity(this, (EntityLivingBase) p_70998_1_)) {
						if (itemstack.stackSize <= 0 && !this.capabilities.isCreativeMode) {
							this.destroyCurrentEquippedItem();
						}

						return true;
					}
				}

				return false;
			} else {
				if (itemstack != null && itemstack == this.getCurrentEquippedItem()) {
					if (itemstack.stackSize <= 0 && !this.capabilities.isCreativeMode) {
						this.destroyCurrentEquippedItem();
					} else if (itemstack.stackSize < itemstack1.stackSize && this.capabilities.isCreativeMode) {
						itemstack.stackSize = itemstack1.stackSize;
					}
				}

				return true;
			}
		}
	}

	/**
	 * Returns the currently being used item by the player.
	 */
	public ItemStack getCurrentEquippedItem() {
		return this.inventory.getCurrentItem();
	}

	/**
	 * Destroys the currently equipped item from the player's inventory.
	 */
	public void destroyCurrentEquippedItem() {
		this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack) null);
	}

	/**
	 * Returns the Y Offset of this entity.
	 */
	public double getYOffset() {
		return -0.35D;
	}

	/**
	 * Attacks for the player the targeted entity with the currently equipped item.
	 * The equipped item has hitEntity called on it. Args: targetEntity
	 */
	public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
		if (targetEntity.canAttackWithItem()) {
			if (!targetEntity.hitByEntity(this)) {
				float f = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
				int i = 0;
				float f1 = 0.0F;

				if (targetEntity instanceof EntityLivingBase) {
					f1 = EnchantmentHelper.func_152377_a(this.getHeldItem(), ((EntityLivingBase) targetEntity).getCreatureAttribute());
				} else {
					f1 = EnchantmentHelper.func_152377_a(this.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
				}

				i = i + EnchantmentHelper.getKnockbackModifier(this);

				if (this.isSprinting()) {
					++i;
				}

				if (f > 0.0F || f1 > 0.0F) {
					boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null && targetEntity instanceof EntityLivingBase;

					if (flag && f > 0.0F) {
						f *= 1.5F;
					}

					f = f + f1;
					boolean flag1 = false;
					int j = EnchantmentHelper.getFireAspectModifier(this);

					if (targetEntity instanceof EntityLivingBase && j > 0 && !targetEntity.isBurning()) {
						flag1 = true;
						targetEntity.setFire(1);
					}

					double d0 = targetEntity.motionX;
					double d1 = targetEntity.motionY;
					double d2 = targetEntity.motionZ;
					boolean flag2 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(this), f);

					if (flag2) {
						if (i > 0) {
							targetEntity.addVelocity((double) (-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F));
							this.motionX *= 0.6D;
							this.motionZ *= 0.6D;
							this.setSprinting(false);
						}

						if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
							((EntityPlayerMP) targetEntity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(targetEntity));
							targetEntity.velocityChanged = false;
							targetEntity.motionX = d0;
							targetEntity.motionY = d1;
							targetEntity.motionZ = d2;
						}

						if (flag) {
							this.onCriticalHit(targetEntity);
						}

						if (f1 > 0.0F) {
							this.onEnchantmentCritical(targetEntity);
						}

						if (f >= 18.0F) {
							this.triggerAchievement(AchievementList.overkill);
						}

						this.setLastAttacker(targetEntity);

						if (targetEntity instanceof EntityLivingBase) {
							EnchantmentHelper.applyThornEnchantments((EntityLivingBase) targetEntity, this);
						}

						EnchantmentHelper.applyArthropodEnchantments(this, targetEntity);
						ItemStack itemstack = this.getCurrentEquippedItem();
						Entity entity = targetEntity;

						if (targetEntity instanceof EntityDragonPart) {
							IEntityMultiPart ientitymultipart = ((EntityDragonPart) targetEntity).entityDragonObj;

							if (ientitymultipart instanceof EntityLivingBase) {
								entity = (EntityLivingBase) ientitymultipart;
							}
						}

						if (itemstack != null && entity instanceof EntityLivingBase) {
							itemstack.hitEntity((EntityLivingBase) entity, this);

							if (itemstack.stackSize <= 0) {
								this.destroyCurrentEquippedItem();
							}
						}

						if (targetEntity instanceof EntityLivingBase) {
							this.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));

							if (j > 0) {
								targetEntity.setFire(j * 4);
							}
						}

						this.addExhaustion(0.3F);
					} else if (flag1) {
						targetEntity.extinguish();
					}
				}
			}
		}
	}

	/**
	 * Called when the player performs a critical hit on the Entity. Args: entity
	 * that was hit critically
	 */
	public void onCriticalHit(Entity entityHit) {
	}

	public void onEnchantmentCritical(Entity entityHit) {
	}

	public void respawnPlayer() {
	}

	/**
	 * Will get destroyed next tick.
	 */
	public void setDead() {
		super.setDead();
		this.inventoryContainer.onContainerClosed(this);

		if (this.openContainer != null) {
			this.openContainer.onContainerClosed(this);
		}
	}

	/**
	 * Checks if this entity is inside of an opaque block
	 */
	public boolean isEntityInsideOpaqueBlock() {
		return !this.sleeping && super.isEntityInsideOpaqueBlock();
	}

	/**
	 * returns true if this is an EntityPlayerSP, or the logged in player.
	 */
	public boolean isUser() {
		return false;
	}

	/**
	 * Returns the GameProfile for this player
	 */
	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public EntityPlayer.EnumStatus trySleep(BlockPos bedLocation) {
		if (!this.worldObj.isRemote) {
			if (this.isPlayerSleeping() || !this.isEntityAlive()) {
				return EntityPlayer.EnumStatus.OTHER_PROBLEM;
			}

			if (!this.worldObj.provider.isSurfaceWorld()) {
				return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
			}

			if (this.worldObj.isDaytime()) {
				return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
			}

			if (Math.abs(this.posX - (double) bedLocation.getX()) > 3.0D || Math.abs(this.posY - (double) bedLocation.getY()) > 2.0D || Math.abs(this.posZ - (double) bedLocation.getZ()) > 3.0D) {
				return EntityPlayer.EnumStatus.TOO_FAR_AWAY;
			}

			double d0 = 8.0D;
			double d1 = 5.0D;
			List<EntityMob> list = this.worldObj.<EntityMob>getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB((double) bedLocation.getX() - d0, (double) bedLocation.getY() - d1, (double) bedLocation.getZ() - d0, (double) bedLocation.getX() + d0, (double) bedLocation.getY() + d1, (double) bedLocation.getZ() + d0));

			if (!list.isEmpty()) {
				return EntityPlayer.EnumStatus.NOT_SAFE;
			}
		}

		if (this.isRiding()) {
			this.mountEntity((Entity) null);
		}

		this.setSize(0.2F, 0.2F);

		if (this.worldObj.isBlockLoaded(bedLocation)) {
			EnumFacing enumfacing = (EnumFacing) this.worldObj.getBlockState(bedLocation).getValue(BlockDirectional.FACING);
			float f = 0.5F;
			float f1 = 0.5F;

			switch (enumfacing) {
			case SOUTH:
				f1 = 0.9F;
				break;

			case NORTH:
				f1 = 0.1F;
				break;

			case WEST:
				f = 0.1F;
				break;

			case EAST:
				f = 0.9F;
			}

			this.func_175139_a(enumfacing);
			this.setPosition((double) ((float) bedLocation.getX() + f), (double) ((float) bedLocation.getY() + 0.6875F), (double) ((float) bedLocation.getZ() + f1));
		} else {
			this.setPosition((double) ((float) bedLocation.getX() + 0.5F), (double) ((float) bedLocation.getY() + 0.6875F), (double) ((float) bedLocation.getZ() + 0.5F));
		}

		this.sleeping = true;
		this.sleepTimer = 0;
		this.playerLocation = bedLocation;
		this.motionX = this.motionZ = this.motionY = 0.0D;

		if (!this.worldObj.isRemote) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}

		return EntityPlayer.EnumStatus.OK;
	}

	private void func_175139_a(EnumFacing p_175139_1_) {
		this.renderOffsetX = 0.0F;
		this.renderOffsetZ = 0.0F;

		switch (p_175139_1_) {
		case SOUTH:
			this.renderOffsetZ = -1.8F;
			break;

		case NORTH:
			this.renderOffsetZ = 1.8F;
			break;

		case WEST:
			this.renderOffsetX = 1.8F;
			break;

		case EAST:
			this.renderOffsetX = -1.8F;
		}
	}

	/**
	 * Wake up the player if they're sleeping.
	 */
	public void wakeUpPlayer(boolean p_70999_1_, boolean updateWorldFlag, boolean setSpawn) {
		this.setSize(0.6F, 1.8F);
		IBlockState iblockstate = this.worldObj.getBlockState(this.playerLocation);

		if (this.playerLocation != null && iblockstate.getBlock() == Blocks.bed) {
			this.worldObj.setBlockState(this.playerLocation, iblockstate.withProperty(BlockBed.OCCUPIED, Boolean.valueOf(false)), 4);
			BlockPos blockpos = BlockBed.getSafeExitLocation(this.worldObj, this.playerLocation, 0);

			if (blockpos == null) {
				blockpos = this.playerLocation.up();
			}

			this.setPosition((double) ((float) blockpos.getX() + 0.5F), (double) ((float) blockpos.getY() + 0.1F), (double) ((float) blockpos.getZ() + 0.5F));
		}

		this.sleeping = false;

		if (!this.worldObj.isRemote && updateWorldFlag) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}

		this.sleepTimer = p_70999_1_ ? 0 : 100;

		if (setSpawn) {
			this.setSpawnPoint(this.playerLocation, false);
		}
	}

	private boolean isInBed() {
		return this.worldObj.getBlockState(this.playerLocation).getBlock() == Blocks.bed;
	}

	/**
	 * Return null if bed is invalid
	 */
	public static BlockPos getBedSpawnLocation(World worldIn, BlockPos bedLocation, boolean forceSpawn) {
		Block block = worldIn.getBlockState(bedLocation).getBlock();

		if (block != Blocks.bed) {
			if (!forceSpawn) {
				return null;
			} else {
				boolean flag = block.func_181623_g();
				boolean flag1 = worldIn.getBlockState(bedLocation.up()).getBlock().func_181623_g();
				return flag && flag1 ? bedLocation : null;
			}
		} else {
			return BlockBed.getSafeExitLocation(worldIn, bedLocation, 0);
		}
	}

	/**
	 * Returns the orientation of the bed in degrees.
	 */
	public float getBedOrientationInDegrees() {
		if (this.playerLocation != null) {
			EnumFacing enumfacing = (EnumFacing) this.worldObj.getBlockState(this.playerLocation).getValue(BlockDirectional.FACING);

			switch (enumfacing) {
			case SOUTH:
				return 90.0F;

			case NORTH:
				return 270.0F;

			case WEST:
				return 0.0F;

			case EAST:
				return 180.0F;
			}
		}

		return 0.0F;
	}

	/**
	 * Returns whether player is sleeping or not
	 */
	public boolean isPlayerSleeping() {
		return this.sleeping;
	}

	/**
	 * Returns whether or not the player is asleep and the screen has fully faded.
	 */
	public boolean isPlayerFullyAsleep() {
		return this.sleeping && this.sleepTimer >= 100;
	}

	public int getSleepTimer() {
		return this.sleepTimer;
	}

	public void addChatComponentMessage(IChatComponent chatComponent) {
	}

	public BlockPos getBedLocation() {
		return this.spawnChunk;
	}

	public boolean isSpawnForced() {
		return this.spawnForced;
	}

	public void setSpawnPoint(BlockPos pos, boolean forced) {
		if (pos != null) {
			this.spawnChunk = pos;
			this.spawnForced = forced;
		} else {
			this.spawnChunk = null;
			this.spawnForced = false;
		}
	}

	/**
	 * Will trigger the specified trigger.
	 */
	public void triggerAchievement(StatBase achievementIn) {
		this.addStat(achievementIn, 1);
	}

	/**
	 * Adds a value to a statistic field.
	 */
	public void addStat(StatBase stat, int amount) {
	}

	public void func_175145_a(StatBase p_175145_1_) {
	}

	/**
	 * Causes this entity to do an upwards motion (jumping).
	 */
	public void jump() {
		super.jump();
		this.triggerAchievement(StatList.jumpStat);

		if (this.isSprinting()) {
			this.addExhaustion(0.8F);
		} else {
			this.addExhaustion(0.2F);
		}
	}

	/**
	 * Moves the entity based on the specified heading. Args: strafe, forward
	 */
	public void moveEntityWithHeading(float strafe, float forward) {
		double d0 = this.posX;
		double d1 = this.posY;
		double d2 = this.posZ;

		if (this.capabilities.isFlying && this.ridingEntity == null) {
			double d3 = this.motionY;
			float f = this.jumpMovementFactor;
			this.jumpMovementFactor = this.capabilities.getFlySpeed() * (float) (this.isSprinting() ? 2 : 1);
			super.moveEntityWithHeading(strafe, forward);
			this.motionY = d3 * 0.6D;
			this.jumpMovementFactor = f;
		} else {
			super.moveEntityWithHeading(strafe, forward);
		}

		this.addMovementStat(this.posX - d0, this.posY - d1, this.posZ - d2);
	}

	/**
	 * the movespeed used for the new AI system
	 */
	public float getAIMoveSpeed() {
		return (float) this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
	}

	/**
	 * Adds a value to a movement statistic field - like run, walk, swin or climb.
	 */
	public void addMovementStat(double p_71000_1_, double p_71000_3_, double p_71000_5_) {
		if (this.ridingEntity == null) {
			if (this.isInsideOfMaterial(Material.water)) {
				int i = Math.round(MathHelper.sqrt_double(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);

				if (i > 0) {
					this.addStat(StatList.distanceDoveStat, i);
					this.addExhaustion(0.015F * (float) i * 0.01F);
				}
			} else if (this.isInWater()) {
				int j = Math.round(MathHelper.sqrt_double(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);

				if (j > 0) {
					this.addStat(StatList.distanceSwumStat, j);
					this.addExhaustion(0.015F * (float) j * 0.01F);
				}
			} else if (this.isOnLadder()) {
				if (p_71000_3_ > 0.0D) {
					this.addStat(StatList.distanceClimbedStat, (int) Math.round(p_71000_3_ * 100.0D));
				}
			} else if (this.onGround) {
				int k = Math.round(MathHelper.sqrt_double(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);

				if (k > 0) {
					this.addStat(StatList.distanceWalkedStat, k);

					if (this.isSprinting()) {
						this.addStat(StatList.distanceSprintedStat, k);
						this.addExhaustion(0.099999994F * (float) k * 0.01F);
					} else {
						if (this.isSneaking()) {
							this.addStat(StatList.distanceCrouchedStat, k);
						}

						this.addExhaustion(0.01F * (float) k * 0.01F);
					}
				}
			} else {
				int l = Math.round(MathHelper.sqrt_double(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);

				if (l > 25) {
					this.addStat(StatList.distanceFlownStat, l);
				}
			}
		}
	}

	/**
	 * Adds a value to a mounted movement statistic field - by minecart, boat, or
	 * pig.
	 */
	private void addMountedMovementStat(double p_71015_1_, double p_71015_3_, double p_71015_5_) {
		if (this.ridingEntity != null) {
			int i = Math.round(MathHelper.sqrt_double(p_71015_1_ * p_71015_1_ + p_71015_3_ * p_71015_3_ + p_71015_5_ * p_71015_5_) * 100.0F);

			if (i > 0) {
				if (this.ridingEntity instanceof EntityMinecart) {
					this.addStat(StatList.distanceByMinecartStat, i);

					if (this.startMinecartRidingCoordinate == null) {
						this.startMinecartRidingCoordinate = new BlockPos(this);
					} else if (this.startMinecartRidingCoordinate.distanceSq((double) MathHelper.floor_double(this.posX), (double) MathHelper.floor_double(this.posY), (double) MathHelper.floor_double(this.posZ)) >= 1000000.0D) {
						this.triggerAchievement(AchievementList.onARail);
					}
				} else if (this.ridingEntity instanceof EntityBoat) {
					this.addStat(StatList.distanceByBoatStat, i);
				} else if (this.ridingEntity instanceof EntityPig) {
					this.addStat(StatList.distanceByPigStat, i);
				} else if (this.ridingEntity instanceof EntityHorse) {
					this.addStat(StatList.distanceByHorseStat, i);
				}
			}
		}
	}

	public void fall(float distance, float damageMultiplier) {
		if (!this.capabilities.allowFlying) {
			if (distance >= 2.0F) {
				this.addStat(StatList.distanceFallenStat, (int) Math.round((double) distance * 100.0D));
			}

			super.fall(distance, damageMultiplier);
		}
	}

	/**
	 * sets the players height back to normal after doing things like sleeping and
	 * dieing
	 */
	protected void resetHeight() {
		if (!this.isSpectator()) {
			super.resetHeight();
		}
	}

	protected String getFallSoundString(int damageValue) {
		return damageValue > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
	}

	/**
	 * This method gets called when the entity kills another one.
	 */
	public void onKillEntity(EntityLivingBase entityLivingIn) {
		if (entityLivingIn instanceof IMob) {
			this.triggerAchievement(AchievementList.killEnemy);
		}

		EntityList.EntityEggInfo entitylist$entityegginfo = (EntityList.EntityEggInfo) EntityList.entityEggs.get(Integer.valueOf(EntityList.getEntityID(entityLivingIn)));

		if (entitylist$entityegginfo != null) {
			this.triggerAchievement(entitylist$entityegginfo.field_151512_d);
		}
	}

	/**
	 * Sets the Entity inside a web block.
	 */
	public void setInWeb() {
		if (!this.capabilities.isFlying) {
			super.setInWeb();
		}
	}

	public ItemStack getCurrentArmor(int slotIn) {
		return this.inventory.armorItemInSlot(slotIn);
	}

	/**
	 * Add experience points to player.
	 */
	public void addExperience(int amount) {
		this.addScore(amount);
		int i = Integer.MAX_VALUE - this.experienceTotal;

		if (amount > i) {
			amount = i;
		}

		this.experience += (float) amount / (float) this.xpBarCap();

		for (this.experienceTotal += amount; this.experience >= 1.0F; this.experience /= (float) this.xpBarCap()) {
			this.experience = (this.experience - 1.0F) * (float) this.xpBarCap();
			this.addExperienceLevel(1);
		}
	}

	public int getXPSeed() {
		return this.xpSeed;
	}

	public void removeExperienceLevel(int levels) {
		this.experienceLevel -= levels;

		if (this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experience = 0.0F;
			this.experienceTotal = 0;
		}

		this.xpSeed = this.rand.nextInt();
	}

	/**
	 * Add experience levels to this player.
	 */
	public void addExperienceLevel(int levels) {
		this.experienceLevel += levels;

		if (this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experience = 0.0F;
			this.experienceTotal = 0;
		}

		if (levels > 0 && this.experienceLevel % 5 == 0 && (float) this.lastXPSound < (float) this.ticksExisted - 100.0F) {
			float f = this.experienceLevel > 30 ? 1.0F : (float) this.experienceLevel / 30.0F;
			this.worldObj.playSoundAtEntity(this, "random.levelup", f * 0.75F, 1.0F);
			this.lastXPSound = this.ticksExisted;
		}
	}

	/**
	 * This method returns the cap amount of experience that the experience bar can
	 * hold. With each level, the experience cap on the player's experience bar is
	 * raised by 10.
	 */
	public int xpBarCap() {
		return this.experienceLevel >= 30 ? 112 + (this.experienceLevel - 30) * 9 : (this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2);
	}

	/**
	 * increases exhaustion level by supplied amount
	 */
	public void addExhaustion(float p_71020_1_) {
		if (!this.capabilities.disableDamage) {
			if (!this.worldObj.isRemote) {
				this.foodStats.addExhaustion(p_71020_1_);
			}
		}
	}

	/**
	 * Returns the player's FoodStats object.
	 */
	public FoodStats getFoodStats() {
		return this.foodStats;
	}

	public boolean canEat(boolean ignoreHunger) {
		return (ignoreHunger || this.foodStats.needFood()) && !this.capabilities.disableDamage;
	}

	/**
	 * Checks if the player's health is not full and not zero.
	 */
	public boolean shouldHeal() {
		return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
	}

	/**
	 * sets the itemInUse when the use item button is clicked. Args: itemstack, int
	 * maxItemUseDuration
	 */
	public void setItemInUse(ItemStack stack, int duration) {
		if (stack != this.itemInUse) {
			this.itemInUse = stack;
			this.itemInUseCount = duration;

			if (!this.worldObj.isRemote) {
				this.setEating(true);
			}
		}
	}

	public boolean isAllowEdit() {
		return this.capabilities.allowEdit;
	}

	public boolean canPlayerEdit(BlockPos p_175151_1_, EnumFacing p_175151_2_, ItemStack p_175151_3_) {
		if (this.capabilities.allowEdit) {
			return true;
		} else if (p_175151_3_ == null) {
			return false;
		} else {
			BlockPos blockpos = p_175151_1_.offset(p_175151_2_.getOpposite());
			Block block = this.worldObj.getBlockState(blockpos).getBlock();
			return p_175151_3_.canPlaceOn(block) || p_175151_3_.canEditBlocks();
		}
	}

	/**
	 * Get the experience points the entity currently has.
	 */
	protected int getExperiencePoints(EntityPlayer player) {
		if (this.worldObj.getGameRules().getBoolean("keepInventory")) {
			return 0;
		} else {
			int i = this.experienceLevel * 7;
			return i > 100 ? 100 : i;
		}
	}

	/**
	 * Only use is to identify if class is an instance of player for experience
	 * dropping
	 */
	protected boolean isPlayer() {
		return true;
	}

	public boolean getAlwaysRenderNameTagForRender() {
		return true;
	}

	/**
	 * Copies the values from the given player into this player if boolean par2 is
	 * true. Always clones Ender Chest Inventory.
	 */
	public void clonePlayer(EntityPlayer oldPlayer, boolean respawnFromEnd) {
		if (respawnFromEnd) {
			this.inventory.copyInventory(oldPlayer.inventory);
			this.setHealth(oldPlayer.getHealth());
			this.foodStats = oldPlayer.foodStats;
			this.experienceLevel = oldPlayer.experienceLevel;
			this.experienceTotal = oldPlayer.experienceTotal;
			this.experience = oldPlayer.experience;
			this.setScore(oldPlayer.getScore());
			this.field_181016_an = oldPlayer.field_181016_an;
			this.field_181017_ao = oldPlayer.field_181017_ao;
			this.field_181018_ap = oldPlayer.field_181018_ap;
		} else if (this.worldObj.getGameRules().getBoolean("keepInventory")) {
			this.inventory.copyInventory(oldPlayer.inventory);
			this.experienceLevel = oldPlayer.experienceLevel;
			this.experienceTotal = oldPlayer.experienceTotal;
			this.experience = oldPlayer.experience;
			this.setScore(oldPlayer.getScore());
		}

		this.xpSeed = oldPlayer.xpSeed;
		this.theInventoryEnderChest = oldPlayer.theInventoryEnderChest;
		this.getDataWatcher().updateObject(10, Byte.valueOf(oldPlayer.getDataWatcher().getWatchableObjectByte(10)));
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk
	 * on. used for spiders and wolves to prevent them from trampling crops
	 */
	protected boolean canTriggerWalking() {
		return !this.capabilities.isFlying;
	}

	/**
	 * Sends the player's abilities to the server (if there is one).
	 */
	public void sendPlayerAbilities() {
	}

	/**
	 * Sets the player's game mode and sends it to them.
	 */
	public void setGameType(WorldSettings.GameType gameType) {
	}

	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	public String getName() {
		return this.gameProfile.getName();
	}

	/**
	 * Returns the InventoryEnderChest of this player.
	 */
	public InventoryEnderChest getInventoryEnderChest() {
		return this.theInventoryEnderChest;
	}

	/**
	 * 0: Tool in Hand; 1-4: Armor
	 */
	public ItemStack getEquipmentInSlot(int slotIn) {
		return slotIn == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[slotIn - 1];
	}

	/**
	 * Returns the item that this EntityLiving is holding, if any.
	 */
	public ItemStack getHeldItem() {
		return this.inventory.getCurrentItem();
	}

	/**
	 * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor.
	 * Params: Item, slot
	 */
	public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
		this.inventory.armorInventory[slotIn] = stack;
	}

	/**
	 * Only used by renderer in EntityLivingBase subclasses. Determines if an entity
	 * is visible or not to a specfic player, if the entity is normally invisible.
	 * For EntityLivingBase subclasses, returning false when invisible will render
	 * the entity semitransparent.
	 */
	public boolean isInvisibleToPlayer(EntityPlayer player) {
		if (!this.isInvisible()) {
			return false;
		} else if (player.isSpectator()) {
			return false;
		} else {
			Team team = this.getTeam();
			return team == null || player == null || player.getTeam() != team || !team.getSeeFriendlyInvisiblesEnabled();
		}
	}

	/**
	 * Returns true if the player is in spectator mode.
	 */
	public abstract boolean isSpectator();

	/**
	 * returns the inventory of this entity (only used in EntityPlayerMP it seems)
	 */
	public ItemStack[] getInventory() {
		return this.inventory.armorInventory;
	}

	public boolean isPushedByWater() {
		return !this.capabilities.isFlying;
	}

	public Scoreboard getWorldScoreboard() {
		return this.worldObj.getScoreboard();
	}

	public Team getTeam() {
		return this.getWorldScoreboard().getPlayersTeam(this.getName());
	}

	/**
	 * Get the formatted ChatComponent that will be used for the sender's username
	 * in chat
	 */
	public IChatComponent getDisplayName() {
		IChatComponent ichatcomponent = new ChatComponentText(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getName()));
		ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.getName() + " "));
		ichatcomponent.getChatStyle().setChatHoverEvent(this.getHoverEvent());
		ichatcomponent.getChatStyle().setInsertion(this.getName());
		return ichatcomponent;
	}

	public float getEyeHeight() {
		float f = 1.62F;

		if (this.isPlayerSleeping()) {
			f = 0.2F;
		}

		if (this.isSneaking()) {
			f -= 0.08F;
		}

		return f;
	}

	public void setAbsorptionAmount(float amount) {
		if (amount < 0.0F) {
			amount = 0.0F;
		}

		this.getDataWatcher().updateObject(17, Float.valueOf(amount));
	}

	public float getAbsorptionAmount() {
		return this.getDataWatcher().getWatchableObjectFloat(17);
	}

	/**
	 * Gets a players UUID given their GameProfie
	 */
	public static UUID getUUID(GameProfile profile) {
		UUID uuid = profile.getId();

		if (uuid == null) {
			uuid = getOfflineUUID(profile.getName());
		}

		return uuid;
	}

	public static UUID getOfflineUUID(String username) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));
	}

	/**
	 * Check whether this player can open an inventory locked with the given
	 * LockCode.
	 */
	public boolean canOpen(LockCode code) {
		if (code.isEmpty()) {
			return true;
		} else {
			ItemStack itemstack = this.getCurrentEquippedItem();
			return itemstack != null && itemstack.hasDisplayName() ? itemstack.getDisplayName().equals(code.getLock()) : false;
		}
	}

	public boolean isWearing(EnumPlayerModelParts p_175148_1_) {
		return (this.getDataWatcher().getWatchableObjectByte(10) & p_175148_1_.getPartMask()) == p_175148_1_.getPartMask();
	}

	/**
	 * Returns true if the command sender should be sent feedback about executed
	 * commands
	 */
	public boolean sendCommandFeedback() {
		return MinecraftServer.getServer().worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
	}

	public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
		if (inventorySlot >= 0 && inventorySlot < this.inventory.mainInventory.length) {
			this.inventory.setInventorySlotContents(inventorySlot, itemStackIn);
			return true;
		} else {
			int i = inventorySlot - 100;

			if (i >= 0 && i < this.inventory.armorInventory.length) {
				int k = i + 1;

				if (itemStackIn != null && itemStackIn.getItem() != null) {
					if (itemStackIn.getItem() instanceof ItemArmor) {
						if (EntityLiving.getArmorPosition(itemStackIn) != k) {
							return false;
						}
					} else if (k != 4 || itemStackIn.getItem() != Items.skull && !(itemStackIn.getItem() instanceof ItemBlock)) {
						return false;
					}
				}

				this.inventory.setInventorySlotContents(i + this.inventory.mainInventory.length, itemStackIn);
				return true;
			} else {
				int j = inventorySlot - 200;

				if (j >= 0 && j < this.theInventoryEnderChest.getSizeInventory()) {
					this.theInventoryEnderChest.setInventorySlotContents(j, itemStackIn);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	/**
	 * Whether the "reducedDebugInfo" option is active for this player.
	 */
	public boolean hasReducedDebug() {
		return this.hasReducedDebug;
	}

	public void setReducedDebug(boolean reducedDebug) {
		this.hasReducedDebug = reducedDebug;
	}

	public static enum EnumChatVisibility {
		FULL(0, "options.chat.visibility.full"), SYSTEM(1, "options.chat.visibility.system"), HIDDEN(2, "options.chat.visibility.hidden");

		private static final EntityPlayer.EnumChatVisibility[] ID_LOOKUP = new EntityPlayer.EnumChatVisibility[values().length];
		private final int chatVisibility;
		private final String resourceKey;

		private EnumChatVisibility(int id, String resourceKey) {
			this.chatVisibility = id;
			this.resourceKey = resourceKey;
		}

		public int getChatVisibility() {
			return this.chatVisibility;
		}

		public static EntityPlayer.EnumChatVisibility getEnumChatVisibility(int id) {
			return ID_LOOKUP[id % ID_LOOKUP.length];
		}

		public String getResourceKey() {
			return this.resourceKey;
		}

		static {
			for (EntityPlayer.EnumChatVisibility entityplayer$enumchatvisibility : values()) {
				ID_LOOKUP[entityplayer$enumchatvisibility.chatVisibility] = entityplayer$enumchatvisibility;
			}
		}
	}

	public static enum EnumStatus {
		OK, NOT_POSSIBLE_HERE, NOT_POSSIBLE_NOW, TOO_FAR_AWAY, OTHER_PROBLEM, NOT_SAFE;
	}

}