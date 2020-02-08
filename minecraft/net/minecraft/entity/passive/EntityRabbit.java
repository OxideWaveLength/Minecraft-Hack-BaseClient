package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityRabbit extends EntityAnimal {
	private EntityRabbit.AIAvoidEntity<EntityWolf> aiAvoidWolves;
	private int field_175540_bm = 0;
	private int field_175535_bn = 0;
	private boolean field_175536_bo = false;
	private boolean field_175537_bp = false;
	private int currentMoveTypeDuration = 0;
	private EntityRabbit.EnumMoveType moveType = EntityRabbit.EnumMoveType.HOP;
	private int carrotTicks = 0;
	private EntityPlayer field_175543_bt = null;

	public EntityRabbit(World worldIn) {
		super(worldIn);
		this.setSize(0.6F, 0.7F);
		this.jumpHelper = new EntityRabbit.RabbitJumpHelper(this);
		this.moveHelper = new EntityRabbit.RabbitMoveHelper(this);
		((PathNavigateGround) this.getNavigator()).setAvoidsWater(true);
		this.navigator.setHeightRequirement(2.5F);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityRabbit.AIPanic(this, 1.33D));
		this.tasks.addTask(2, new EntityAITempt(this, 1.0D, Items.carrot, false));
		this.tasks.addTask(2, new EntityAITempt(this, 1.0D, Items.golden_carrot, false));
		this.tasks.addTask(2, new EntityAITempt(this, 1.0D, Item.getItemFromBlock(Blocks.yellow_flower), false));
		this.tasks.addTask(3, new EntityAIMate(this, 0.8D));
		this.tasks.addTask(5, new EntityRabbit.AIRaidFarm(this));
		this.tasks.addTask(5, new EntityAIWander(this, 0.6D));
		this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
		this.aiAvoidWolves = new EntityRabbit.AIAvoidEntity(this, EntityWolf.class, 16.0F, 1.33D, 1.33D);
		this.tasks.addTask(4, this.aiAvoidWolves);
		this.setMovementSpeed(0.0D);
	}

	protected float getJumpUpwardsMotion() {
		return this.moveHelper.isUpdating() && this.moveHelper.getY() > this.posY + 0.5D ? 0.5F : this.moveType.func_180074_b();
	}

	public void setMoveType(EntityRabbit.EnumMoveType type) {
		this.moveType = type;
	}

	public float func_175521_o(float p_175521_1_) {
		return this.field_175535_bn == 0 ? 0.0F : ((float) this.field_175540_bm + p_175521_1_) / (float) this.field_175535_bn;
	}

	public void setMovementSpeed(double newSpeed) {
		this.getNavigator().setSpeed(newSpeed);
		this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), newSpeed);
	}

	public void setJumping(boolean jump, EntityRabbit.EnumMoveType moveTypeIn) {
		super.setJumping(jump);

		if (!jump) {
			if (this.moveType == EntityRabbit.EnumMoveType.ATTACK) {
				this.moveType = EntityRabbit.EnumMoveType.HOP;
			}
		} else {
			this.setMovementSpeed(1.5D * (double) moveTypeIn.getSpeed());
			this.playSound(this.getJumpingSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}

		this.field_175536_bo = jump;
	}

	public void doMovementAction(EntityRabbit.EnumMoveType movetype) {
		this.setJumping(true, movetype);
		this.field_175535_bn = movetype.func_180073_d();
		this.field_175540_bm = 0;
	}

	public boolean func_175523_cj() {
		return this.field_175536_bo;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(18, Byte.valueOf((byte) 0));
	}

	public void updateAITasks() {
		if (this.moveHelper.getSpeed() > 0.8D) {
			this.setMoveType(EntityRabbit.EnumMoveType.SPRINT);
		} else if (this.moveType != EntityRabbit.EnumMoveType.ATTACK) {
			this.setMoveType(EntityRabbit.EnumMoveType.HOP);
		}

		if (this.currentMoveTypeDuration > 0) {
			--this.currentMoveTypeDuration;
		}

		if (this.carrotTicks > 0) {
			this.carrotTicks -= this.rand.nextInt(3);

			if (this.carrotTicks < 0) {
				this.carrotTicks = 0;
			}
		}

		if (this.onGround) {
			if (!this.field_175537_bp) {
				this.setJumping(false, EntityRabbit.EnumMoveType.NONE);
				this.func_175517_cu();
			}

			if (this.getRabbitType() == 99 && this.currentMoveTypeDuration == 0) {
				EntityLivingBase entitylivingbase = this.getAttackTarget();

				if (entitylivingbase != null && this.getDistanceSqToEntity(entitylivingbase) < 16.0D) {
					this.calculateRotationYaw(entitylivingbase.posX, entitylivingbase.posZ);
					this.moveHelper.setMoveTo(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, this.moveHelper.getSpeed());
					this.doMovementAction(EntityRabbit.EnumMoveType.ATTACK);
					this.field_175537_bp = true;
				}
			}

			EntityRabbit.RabbitJumpHelper entityrabbit$rabbitjumphelper = (EntityRabbit.RabbitJumpHelper) this.jumpHelper;

			if (!entityrabbit$rabbitjumphelper.getIsJumping()) {
				if (this.moveHelper.isUpdating() && this.currentMoveTypeDuration == 0) {
					PathEntity pathentity = this.navigator.getPath();
					Vec3 vec3 = new Vec3(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ());

					if (pathentity != null && pathentity.getCurrentPathIndex() < pathentity.getCurrentPathLength()) {
						vec3 = pathentity.getPosition(this);
					}

					this.calculateRotationYaw(vec3.xCoord, vec3.zCoord);
					this.doMovementAction(this.moveType);
				}
			} else if (!entityrabbit$rabbitjumphelper.func_180065_d()) {
				this.func_175518_cr();
			}
		}

		this.field_175537_bp = this.onGround;
	}

	/**
	 * Attempts to create sprinting particles if the entity is sprinting and not in
	 * water.
	 */
	public void spawnRunningParticles() {
	}

	private void calculateRotationYaw(double x, double z) {
		this.rotationYaw = (float) (MathHelper.func_181159_b(z - this.posZ, x - this.posX) * 180.0D / Math.PI) - 90.0F;
	}

	private void func_175518_cr() {
		((EntityRabbit.RabbitJumpHelper) this.jumpHelper).func_180066_a(true);
	}

	private void func_175520_cs() {
		((EntityRabbit.RabbitJumpHelper) this.jumpHelper).func_180066_a(false);
	}

	private void updateMoveTypeDuration() {
		this.currentMoveTypeDuration = this.getMoveTypeDuration();
	}

	private void func_175517_cu() {
		this.updateMoveTypeDuration();
		this.func_175520_cs();
	}

	/**
	 * Called frequently so the entity can update its state every tick as required.
	 * For example, zombies and skeletons use this to react to sunlight and start to
	 * burn.
	 */
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (this.field_175540_bm != this.field_175535_bn) {
			if (this.field_175540_bm == 0 && !this.worldObj.isRemote) {
				this.worldObj.setEntityState(this, (byte) 1);
			}

			++this.field_175540_bm;
		} else if (this.field_175535_bn != 0) {
			this.field_175540_bm = 0;
			this.field_175535_bn = 0;
		}
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		super.writeEntityToNBT(tagCompound);
		tagCompound.setInteger("RabbitType", this.getRabbitType());
		tagCompound.setInteger("MoreCarrotTicks", this.carrotTicks);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
		super.readEntityFromNBT(tagCompund);
		this.setRabbitType(tagCompund.getInteger("RabbitType"));
		this.carrotTicks = tagCompund.getInteger("MoreCarrotTicks");
	}

	protected String getJumpingSound() {
		return "mob.rabbit.hop";
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound() {
		return "mob.rabbit.idle";
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound() {
		return "mob.rabbit.hurt";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound() {
		return "mob.rabbit.death";
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		if (this.getRabbitType() == 99) {
			this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
		} else {
			return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
		}
	}

	/**
	 * Returns the current armor value as determined by a call to
	 * InventoryPlayer.getTotalArmorValue
	 */
	public int getTotalArmorValue() {
		return this.getRabbitType() == 99 ? 8 : super.getTotalArmorValue();
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return this.isEntityInvulnerable(source) ? false : super.attackEntityFrom(source, amount);
	}

	/**
	 * Causes this Entity to drop a random item.
	 */
	protected void addRandomDrop() {
		this.entityDropItem(new ItemStack(Items.rabbit_foot, 1), 0.0F);
	}

	/**
	 * Drop 0-2 items of this living's type
	 */
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
		int i = this.rand.nextInt(2) + this.rand.nextInt(1 + p_70628_2_);

		for (int j = 0; j < i; ++j) {
			this.dropItem(Items.rabbit_hide, 1);
		}

		i = this.rand.nextInt(2);

		for (int k = 0; k < i; ++k) {
			if (this.isBurning()) {
				this.dropItem(Items.cooked_rabbit, 1);
			} else {
				this.dropItem(Items.rabbit, 1);
			}
		}
	}

	private boolean isRabbitBreedingItem(Item itemIn) {
		return itemIn == Items.carrot || itemIn == Items.golden_carrot || itemIn == Item.getItemFromBlock(Blocks.yellow_flower);
	}

	public EntityRabbit createChild(EntityAgeable ageable) {
		EntityRabbit entityrabbit = new EntityRabbit(this.worldObj);

		if (ageable instanceof EntityRabbit) {
			entityrabbit.setRabbitType(this.rand.nextBoolean() ? this.getRabbitType() : ((EntityRabbit) ageable).getRabbitType());
		}

		return entityrabbit;
	}

	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it
	 * (wheat, carrots or seeds depending on the animal type)
	 */
	public boolean isBreedingItem(ItemStack stack) {
		return stack != null && this.isRabbitBreedingItem(stack.getItem());
	}

	public int getRabbitType() {
		return this.dataWatcher.getWatchableObjectByte(18);
	}

	public void setRabbitType(int rabbitTypeId) {
		if (rabbitTypeId == 99) {
			this.tasks.removeTask(this.aiAvoidWolves);
			this.tasks.addTask(4, new EntityRabbit.AIEvilAttack(this));
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityWolf.class, true));

			if (!this.hasCustomName()) {
				this.setCustomNameTag(StatCollector.translateToLocal("entity.KillerBunny.name"));
			}
		}

		this.dataWatcher.updateObject(18, Byte.valueOf((byte) rabbitTypeId));
	}

	/**
	 * Called only once on an entity when first time spawned, via egg, mob spawner,
	 * natural spawning etc, but not called when entity is reloaded from nbt. Mainly
	 * used for initializing attributes and inventory
	 */
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		int i = this.rand.nextInt(6);
		boolean flag = false;

		if (livingdata instanceof EntityRabbit.RabbitTypeData) {
			i = ((EntityRabbit.RabbitTypeData) livingdata).typeData;
			flag = true;
		} else {
			livingdata = new EntityRabbit.RabbitTypeData(i);
		}

		this.setRabbitType(i);

		if (flag) {
			this.setGrowingAge(-24000);
		}

		return livingdata;
	}

	/**
	 * Returns true if {@link net.minecraft.entity.passive.EntityRabbit#carrotTicks
	 * carrotTicks} has reached zero
	 */
	private boolean isCarrotEaten() {
		return this.carrotTicks == 0;
	}

	/**
	 * Returns duration of the current
	 * {@link net.minecraft.entity.passive.EntityRabbit.EnumMoveType move type}
	 */
	protected int getMoveTypeDuration() {
		return this.moveType.getDuration();
	}

	protected void createEatingParticles() {
		this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, 0.0D, 0.0D, 0.0D, new int[] { Block.getStateId(Blocks.carrots.getStateFromMeta(7)) });
		this.carrotTicks = 100;
	}

	public void handleStatusUpdate(byte id) {
		if (id == 1) {
			this.createRunningParticles();
			this.field_175535_bn = 10;
			this.field_175540_bm = 0;
		} else {
			super.handleStatusUpdate(id);
		}
	}

	static class AIAvoidEntity<T extends Entity> extends EntityAIAvoidEntity<T> {
		private EntityRabbit entityInstance;

		public AIAvoidEntity(EntityRabbit p_i46403_1_, Class<T> p_i46403_2_, float p_i46403_3_, double p_i46403_4_, double p_i46403_6_) {
			super(p_i46403_1_, p_i46403_2_, p_i46403_3_, p_i46403_4_, p_i46403_6_);
			this.entityInstance = p_i46403_1_;
		}

		public void updateTask() {
			super.updateTask();
		}
	}

	static class AIEvilAttack extends EntityAIAttackOnCollide {
		public AIEvilAttack(EntityRabbit p_i45867_1_) {
			super(p_i45867_1_, EntityLivingBase.class, 1.4D, true);
		}

		protected double func_179512_a(EntityLivingBase attackTarget) {
			return (double) (4.0F + attackTarget.width);
		}
	}

	static class AIPanic extends EntityAIPanic {
		private EntityRabbit theEntity;

		public AIPanic(EntityRabbit p_i45861_1_, double speedIn) {
			super(p_i45861_1_, speedIn);
			this.theEntity = p_i45861_1_;
		}

		public void updateTask() {
			super.updateTask();
			this.theEntity.setMovementSpeed(this.speed);
		}
	}

	static class AIRaidFarm extends EntityAIMoveToBlock {
		private final EntityRabbit field_179500_c;
		private boolean field_179498_d;
		private boolean field_179499_e = false;

		public AIRaidFarm(EntityRabbit p_i45860_1_) {
			super(p_i45860_1_, 0.699999988079071D, 16);
			this.field_179500_c = p_i45860_1_;
		}

		public boolean shouldExecute() {
			if (this.runDelay <= 0) {
				if (!this.field_179500_c.worldObj.getGameRules().getBoolean("mobGriefing")) {
					return false;
				}

				this.field_179499_e = false;
				this.field_179498_d = this.field_179500_c.isCarrotEaten();
			}

			return super.shouldExecute();
		}

		public boolean continueExecuting() {
			return this.field_179499_e && super.continueExecuting();
		}

		public void startExecuting() {
			super.startExecuting();
		}

		public void resetTask() {
			super.resetTask();
		}

		public void updateTask() {
			super.updateTask();
			this.field_179500_c.getLookHelper().setLookPosition((double) this.destinationBlock.getX() + 0.5D, (double) (this.destinationBlock.getY() + 1), (double) this.destinationBlock.getZ() + 0.5D, 10.0F, (float) this.field_179500_c.getVerticalFaceSpeed());

			if (this.getIsAboveDestination()) {
				World world = this.field_179500_c.worldObj;
				BlockPos blockpos = this.destinationBlock.up();
				IBlockState iblockstate = world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();

				if (this.field_179499_e && block instanceof BlockCarrot && ((Integer) iblockstate.getValue(BlockCarrot.AGE)).intValue() == 7) {
					world.setBlockState(blockpos, Blocks.air.getDefaultState(), 2);
					world.destroyBlock(blockpos, true);
					this.field_179500_c.createEatingParticles();
				}

				this.field_179499_e = false;
				this.runDelay = 10;
			}
		}

		protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
			Block block = worldIn.getBlockState(pos).getBlock();

			if (block == Blocks.farmland) {
				pos = pos.up();
				IBlockState iblockstate = worldIn.getBlockState(pos);
				block = iblockstate.getBlock();

				if (block instanceof BlockCarrot && ((Integer) iblockstate.getValue(BlockCarrot.AGE)).intValue() == 7 && this.field_179498_d && !this.field_179499_e) {
					this.field_179499_e = true;
					return true;
				}
			}

			return false;
		}
	}

	static enum EnumMoveType {
		NONE(0.0F, 0.0F, 30, 1), HOP(0.8F, 0.2F, 20, 10), STEP(1.0F, 0.45F, 14, 14), SPRINT(1.75F, 0.4F, 1, 8), ATTACK(2.0F, 0.7F, 7, 8);

		private final float speed;
		private final float field_180077_g;
		private final int duration;
		private final int field_180085_i;

		private EnumMoveType(float typeSpeed, float p_i45866_4_, int typeDuration, int p_i45866_6_) {
			this.speed = typeSpeed;
			this.field_180077_g = p_i45866_4_;
			this.duration = typeDuration;
			this.field_180085_i = p_i45866_6_;
		}

		public float getSpeed() {
			return this.speed;
		}

		public float func_180074_b() {
			return this.field_180077_g;
		}

		public int getDuration() {
			return this.duration;
		}

		public int func_180073_d() {
			return this.field_180085_i;
		}
	}

	public class RabbitJumpHelper extends EntityJumpHelper {
		private EntityRabbit theEntity;
		private boolean field_180068_d = false;

		public RabbitJumpHelper(EntityRabbit rabbit) {
			super(rabbit);
			this.theEntity = rabbit;
		}

		public boolean getIsJumping() {
			return this.isJumping;
		}

		public boolean func_180065_d() {
			return this.field_180068_d;
		}

		public void func_180066_a(boolean p_180066_1_) {
			this.field_180068_d = p_180066_1_;
		}

		public void doJump() {
			if (this.isJumping) {
				this.theEntity.doMovementAction(EntityRabbit.EnumMoveType.STEP);
				this.isJumping = false;
			}
		}
	}

	static class RabbitMoveHelper extends EntityMoveHelper {
		private EntityRabbit theEntity;

		public RabbitMoveHelper(EntityRabbit p_i45862_1_) {
			super(p_i45862_1_);
			this.theEntity = p_i45862_1_;
		}

		public void onUpdateMoveHelper() {
			if (this.theEntity.onGround && !this.theEntity.func_175523_cj()) {
				this.theEntity.setMovementSpeed(0.0D);
			}

			super.onUpdateMoveHelper();
		}
	}

	public static class RabbitTypeData implements IEntityLivingData {
		public int typeData;

		public RabbitTypeData(int type) {
			this.typeData = type;
		}
	}
}
