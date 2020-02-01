package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGuardian extends EntityMob
{
    private float field_175482_b;
    private float field_175484_c;
    private float field_175483_bk;
    private float field_175485_bl;
    private float field_175486_bm;
    private EntityLivingBase targetedEntity;
    private int field_175479_bo;
    private boolean field_175480_bp;
    private EntityAIWander wander;

    public EntityGuardian(World worldIn)
    {
        super(worldIn);
        this.experienceValue = 10;
        this.setSize(0.85F, 0.85F);
        this.tasks.addTask(4, new EntityGuardian.AIGuardianAttack(this));
        EntityAIMoveTowardsRestriction entityaimovetowardsrestriction;
        this.tasks.addTask(5, entityaimovetowardsrestriction = new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, this.wander = new EntityAIWander(this, 1.0D, 80));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityGuardian.class, 12.0F, 0.01F));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.wander.setMutexBits(3);
        entityaimovetowardsrestriction.setMutexBits(3);
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 10, true, false, new EntityGuardian.GuardianTargetSelector(this)));
        this.moveHelper = new EntityGuardian.GuardianMoveHelper(this);
        this.field_175484_c = this.field_175482_b = this.rand.nextFloat();
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);
        this.setElder(tagCompund.getBoolean("Elder"));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("Elder", this.isElder());
    }

    /**
     * Returns new PathNavigateGround instance
     */
    protected PathNavigate getNewNavigator(World worldIn)
    {
        return new PathNavigateSwimmer(this, worldIn);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Integer.valueOf(0));
        this.dataWatcher.addObject(17, Integer.valueOf(0));
    }

    /**
     * Returns true if given flag is set
     */
    private boolean isSyncedFlagSet(int flagId)
    {
        return (this.dataWatcher.getWatchableObjectInt(16) & flagId) != 0;
    }

    /**
     * Sets a flag state "on/off" on both sides (client/server) by using DataWatcher
     */
    private void setSyncedFlag(int flagId, boolean state)
    {
        int i = this.dataWatcher.getWatchableObjectInt(16);

        if (state)
        {
            this.dataWatcher.updateObject(16, Integer.valueOf(i | flagId));
        }
        else
        {
            this.dataWatcher.updateObject(16, Integer.valueOf(i & ~flagId));
        }
    }

    public boolean func_175472_n()
    {
        return this.isSyncedFlagSet(2);
    }

    private void func_175476_l(boolean p_175476_1_)
    {
        this.setSyncedFlag(2, p_175476_1_);
    }

    public int func_175464_ck()
    {
        return this.isElder() ? 60 : 80;
    }

    public boolean isElder()
    {
        return this.isSyncedFlagSet(4);
    }

    /**
     * Sets this Guardian to be an elder or not.
     */
    public void setElder(boolean elder)
    {
        this.setSyncedFlag(4, elder);

        if (elder)
        {
            this.setSize(1.9975F, 1.9975F);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0D);
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(80.0D);
            this.enablePersistence();
            this.wander.setExecutionChance(400);
        }
    }

    public void setElder()
    {
        this.setElder(true);
        this.field_175486_bm = this.field_175485_bl = 1.0F;
    }

    private void setTargetedEntity(int entityId)
    {
        this.dataWatcher.updateObject(17, Integer.valueOf(entityId));
    }

    public boolean hasTargetedEntity()
    {
        return this.dataWatcher.getWatchableObjectInt(17) != 0;
    }

    public EntityLivingBase getTargetedEntity()
    {
        if (!this.hasTargetedEntity())
        {
            return null;
        }
        else if (this.worldObj.isRemote)
        {
            if (this.targetedEntity != null)
            {
                return this.targetedEntity;
            }
            else
            {
                Entity entity = this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(17));

                if (entity instanceof EntityLivingBase)
                {
                    this.targetedEntity = (EntityLivingBase)entity;
                    return this.targetedEntity;
                }
                else
                {
                    return null;
                }
            }
        }
        else
        {
            return this.getAttackTarget();
        }
    }

    public void onDataWatcherUpdate(int dataID)
    {
        super.onDataWatcherUpdate(dataID);

        if (dataID == 16)
        {
            if (this.isElder() && this.width < 1.0F)
            {
                this.setSize(1.9975F, 1.9975F);
            }
        }
        else if (dataID == 17)
        {
            this.field_175479_bo = 0;
            this.targetedEntity = null;
        }
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval()
    {
        return 160;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return !this.isInWater() ? "mob.guardian.land.idle" : (this.isElder() ? "mob.guardian.elder.idle" : "mob.guardian.idle");
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return !this.isInWater() ? "mob.guardian.land.hit" : (this.isElder() ? "mob.guardian.elder.hit" : "mob.guardian.hit");
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return !this.isInWater() ? "mob.guardian.land.death" : (this.isElder() ? "mob.guardian.elder.death" : "mob.guardian.death");
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    public float getEyeHeight()
    {
        return this.height * 0.5F;
    }

    public float getBlockPathWeight(BlockPos pos)
    {
        return this.worldObj.getBlockState(pos).getBlock().getMaterial() == Material.water ? 10.0F + this.worldObj.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.worldObj.isRemote)
        {
            this.field_175484_c = this.field_175482_b;

            if (!this.isInWater())
            {
                this.field_175483_bk = 2.0F;

                if (this.motionY > 0.0D && this.field_175480_bp && !this.isSilent())
                {
                    this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.guardian.flop", 1.0F, 1.0F, false);
                }

                this.field_175480_bp = this.motionY < 0.0D && this.worldObj.isBlockNormalCube((new BlockPos(this)).down(), false);
            }
            else if (this.func_175472_n())
            {
                if (this.field_175483_bk < 0.5F)
                {
                    this.field_175483_bk = 4.0F;
                }
                else
                {
                    this.field_175483_bk += (0.5F - this.field_175483_bk) * 0.1F;
                }
            }
            else
            {
                this.field_175483_bk += (0.125F - this.field_175483_bk) * 0.2F;
            }

            this.field_175482_b += this.field_175483_bk;
            this.field_175486_bm = this.field_175485_bl;

            if (!this.isInWater())
            {
                this.field_175485_bl = this.rand.nextFloat();
            }
            else if (this.func_175472_n())
            {
                this.field_175485_bl += (0.0F - this.field_175485_bl) * 0.25F;
            }
            else
            {
                this.field_175485_bl += (1.0F - this.field_175485_bl) * 0.06F;
            }

            if (this.func_175472_n() && this.isInWater())
            {
                Vec3 vec3 = this.getLook(0.0F);

                for (int i = 0; i < 2; ++i)
                {
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width - vec3.xCoord * 1.5D, this.posY + this.rand.nextDouble() * (double)this.height - vec3.yCoord * 1.5D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width - vec3.zCoord * 1.5D, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }

            if (this.hasTargetedEntity())
            {
                if (this.field_175479_bo < this.func_175464_ck())
                {
                    ++this.field_175479_bo;
                }

                EntityLivingBase entitylivingbase = this.getTargetedEntity();

                if (entitylivingbase != null)
                {
                    this.getLookHelper().setLookPositionWithEntity(entitylivingbase, 90.0F, 90.0F);
                    this.getLookHelper().onUpdateLook();
                    double d5 = (double)this.func_175477_p(0.0F);
                    double d0 = entitylivingbase.posX - this.posX;
                    double d1 = entitylivingbase.posY + (double)(entitylivingbase.height * 0.5F) - (this.posY + (double)this.getEyeHeight());
                    double d2 = entitylivingbase.posZ - this.posZ;
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    d0 = d0 / d3;
                    d1 = d1 / d3;
                    d2 = d2 / d3;
                    double d4 = this.rand.nextDouble();

                    while (d4 < d3)
                    {
                        d4 += 1.8D - d5 + this.rand.nextDouble() * (1.7D - d5);
                        this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + d0 * d4, this.posY + d1 * d4 + (double)this.getEyeHeight(), this.posZ + d2 * d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                }
            }
        }

        if (this.inWater)
        {
            this.setAir(300);
        }
        else if (this.onGround)
        {
            this.motionY += 0.5D;
            this.motionX += (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
            this.motionZ += (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
            this.rotationYaw = this.rand.nextFloat() * 360.0F;
            this.onGround = false;
            this.isAirBorne = true;
        }

        if (this.hasTargetedEntity())
        {
            this.rotationYaw = this.rotationYawHead;
        }

        super.onLivingUpdate();
    }

    public float func_175471_a(float p_175471_1_)
    {
        return this.field_175484_c + (this.field_175482_b - this.field_175484_c) * p_175471_1_;
    }

    public float func_175469_o(float p_175469_1_)
    {
        return this.field_175486_bm + (this.field_175485_bl - this.field_175486_bm) * p_175469_1_;
    }

    public float func_175477_p(float p_175477_1_)
    {
        return ((float)this.field_175479_bo + p_175477_1_) / (float)this.func_175464_ck();
    }

    protected void updateAITasks()
    {
        super.updateAITasks();

        if (this.isElder())
        {
            int i = 1200;
            int j = 1200;
            int k = 6000;
            int l = 2;

            if ((this.ticksExisted + this.getEntityId()) % 1200 == 0)
            {
                Potion potion = Potion.digSlowdown;

                for (EntityPlayerMP entityplayermp : this.worldObj.getPlayers(EntityPlayerMP.class, new Predicate<EntityPlayerMP>()
            {
                public boolean apply(EntityPlayerMP p_apply_1_)
                    {
                        return EntityGuardian.this.getDistanceSqToEntity(p_apply_1_) < 2500.0D && p_apply_1_.theItemInWorldManager.survivalOrAdventure();
                    }
                }))
                {
                    if (!entityplayermp.isPotionActive(potion) || entityplayermp.getActivePotionEffect(potion).getAmplifier() < 2 || entityplayermp.getActivePotionEffect(potion).getDuration() < 1200)
                    {
                        entityplayermp.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(10, 0.0F));
                        entityplayermp.addPotionEffect(new PotionEffect(potion.id, 6000, 2));
                    }
                }
            }

            if (!this.hasHome())
            {
                this.setHomePosAndDistance(new BlockPos(this), 16);
            }
        }
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
        int i = this.rand.nextInt(3) + this.rand.nextInt(p_70628_2_ + 1);

        if (i > 0)
        {
            this.entityDropItem(new ItemStack(Items.prismarine_shard, i, 0), 1.0F);
        }

        if (this.rand.nextInt(3 + p_70628_2_) > 1)
        {
            this.entityDropItem(new ItemStack(Items.fish, 1, ItemFishFood.FishType.COD.getMetadata()), 1.0F);
        }
        else if (this.rand.nextInt(3 + p_70628_2_) > 1)
        {
            this.entityDropItem(new ItemStack(Items.prismarine_crystals, 1, 0), 1.0F);
        }

        if (p_70628_1_ && this.isElder())
        {
            this.entityDropItem(new ItemStack(Blocks.sponge, 1, 1), 1.0F);
        }
    }

    /**
     * Causes this Entity to drop a random item.
     */
    protected void addRandomDrop()
    {
        ItemStack itemstack = ((WeightedRandomFishable)WeightedRandom.getRandomItem(this.rand, EntityFishHook.func_174855_j())).getItemStack(this.rand);
        this.entityDropItem(itemstack, 1.0F);
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        return true;
    }

    /**
     * Checks that the entity is not colliding with any blocks / liquids
     */
    public boolean isNotColliding()
    {
        return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty();
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return (this.rand.nextInt(20) == 0 || !this.worldObj.canBlockSeeSky(new BlockPos(this))) && super.getCanSpawnHere();
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!this.func_175472_n() && !source.isMagicDamage() && source.getSourceOfDamage() instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)source.getSourceOfDamage();

            if (!source.isExplosion())
            {
                entitylivingbase.attackEntityFrom(DamageSource.causeThornsDamage(this), 2.0F);
                entitylivingbase.playSound("damage.thorns", 0.5F, 1.0F);
            }
        }

        this.wander.makeUpdate();
        return super.attackEntityFrom(source, amount);
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    public int getVerticalFaceSpeed()
    {
        return 180;
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    public void moveEntityWithHeading(float strafe, float forward)
    {
        if (this.isServerWorld())
        {
            if (this.isInWater())
            {
                this.moveFlying(strafe, forward, 0.1F);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.8999999761581421D;
                this.motionY *= 0.8999999761581421D;
                this.motionZ *= 0.8999999761581421D;

                if (!this.func_175472_n() && this.getAttackTarget() == null)
                {
                    this.motionY -= 0.005D;
                }
            }
            else
            {
                super.moveEntityWithHeading(strafe, forward);
            }
        }
        else
        {
            super.moveEntityWithHeading(strafe, forward);
        }
    }

    static class AIGuardianAttack extends EntityAIBase
    {
        private EntityGuardian theEntity;
        private int tickCounter;

        public AIGuardianAttack(EntityGuardian p_i45833_1_)
        {
            this.theEntity = p_i45833_1_;
            this.setMutexBits(3);
        }

        public boolean shouldExecute()
        {
            EntityLivingBase entitylivingbase = this.theEntity.getAttackTarget();
            return entitylivingbase != null && entitylivingbase.isEntityAlive();
        }

        public boolean continueExecuting()
        {
            return super.continueExecuting() && (this.theEntity.isElder() || this.theEntity.getDistanceSqToEntity(this.theEntity.getAttackTarget()) > 9.0D);
        }

        public void startExecuting()
        {
            this.tickCounter = -10;
            this.theEntity.getNavigator().clearPathEntity();
            this.theEntity.getLookHelper().setLookPositionWithEntity(this.theEntity.getAttackTarget(), 90.0F, 90.0F);
            this.theEntity.isAirBorne = true;
        }

        public void resetTask()
        {
            this.theEntity.setTargetedEntity(0);
            this.theEntity.setAttackTarget((EntityLivingBase)null);
            this.theEntity.wander.makeUpdate();
        }

        public void updateTask()
        {
            EntityLivingBase entitylivingbase = this.theEntity.getAttackTarget();
            this.theEntity.getNavigator().clearPathEntity();
            this.theEntity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 90.0F, 90.0F);

            if (!this.theEntity.canEntityBeSeen(entitylivingbase))
            {
                this.theEntity.setAttackTarget((EntityLivingBase)null);
            }
            else
            {
                ++this.tickCounter;

                if (this.tickCounter == 0)
                {
                    this.theEntity.setTargetedEntity(this.theEntity.getAttackTarget().getEntityId());
                    this.theEntity.worldObj.setEntityState(this.theEntity, (byte)21);
                }
                else if (this.tickCounter >= this.theEntity.func_175464_ck())
                {
                    float f = 1.0F;

                    if (this.theEntity.worldObj.getDifficulty() == EnumDifficulty.HARD)
                    {
                        f += 2.0F;
                    }

                    if (this.theEntity.isElder())
                    {
                        f += 2.0F;
                    }

                    entitylivingbase.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.theEntity, this.theEntity), f);
                    entitylivingbase.attackEntityFrom(DamageSource.causeMobDamage(this.theEntity), (float)this.theEntity.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
                    this.theEntity.setAttackTarget((EntityLivingBase)null);
                }
                else if (this.tickCounter >= 60 && this.tickCounter % 20 == 0)
                {
                    ;
                }

                super.updateTask();
            }
        }
    }

    static class GuardianMoveHelper extends EntityMoveHelper
    {
        private EntityGuardian entityGuardian;

        public GuardianMoveHelper(EntityGuardian p_i45831_1_)
        {
            super(p_i45831_1_);
            this.entityGuardian = p_i45831_1_;
        }

        public void onUpdateMoveHelper()
        {
            if (this.update && !this.entityGuardian.getNavigator().noPath())
            {
                double d0 = this.posX - this.entityGuardian.posX;
                double d1 = this.posY - this.entityGuardian.posY;
                double d2 = this.posZ - this.entityGuardian.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = (double)MathHelper.sqrt_double(d3);
                d1 = d1 / d3;
                float f = (float)(MathHelper.func_181159_b(d2, d0) * 180.0D / Math.PI) - 90.0F;
                this.entityGuardian.rotationYaw = this.limitAngle(this.entityGuardian.rotationYaw, f, 30.0F);
                this.entityGuardian.renderYawOffset = this.entityGuardian.rotationYaw;
                float f1 = (float)(this.speed * this.entityGuardian.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
                this.entityGuardian.setAIMoveSpeed(this.entityGuardian.getAIMoveSpeed() + (f1 - this.entityGuardian.getAIMoveSpeed()) * 0.125F);
                double d4 = Math.sin((double)(this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.5D) * 0.05D;
                double d5 = Math.cos((double)(this.entityGuardian.rotationYaw * (float)Math.PI / 180.0F));
                double d6 = Math.sin((double)(this.entityGuardian.rotationYaw * (float)Math.PI / 180.0F));
                this.entityGuardian.motionX += d4 * d5;
                this.entityGuardian.motionZ += d4 * d6;
                d4 = Math.sin((double)(this.entityGuardian.ticksExisted + this.entityGuardian.getEntityId()) * 0.75D) * 0.05D;
                this.entityGuardian.motionY += d4 * (d6 + d5) * 0.25D;
                this.entityGuardian.motionY += (double)this.entityGuardian.getAIMoveSpeed() * d1 * 0.1D;
                EntityLookHelper entitylookhelper = this.entityGuardian.getLookHelper();
                double d7 = this.entityGuardian.posX + d0 / d3 * 2.0D;
                double d8 = (double)this.entityGuardian.getEyeHeight() + this.entityGuardian.posY + d1 / d3 * 1.0D;
                double d9 = this.entityGuardian.posZ + d2 / d3 * 2.0D;
                double d10 = entitylookhelper.getLookPosX();
                double d11 = entitylookhelper.getLookPosY();
                double d12 = entitylookhelper.getLookPosZ();

                if (!entitylookhelper.getIsLooking())
                {
                    d10 = d7;
                    d11 = d8;
                    d12 = d9;
                }

                this.entityGuardian.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
                this.entityGuardian.func_175476_l(true);
            }
            else
            {
                this.entityGuardian.setAIMoveSpeed(0.0F);
                this.entityGuardian.func_175476_l(false);
            }
        }
    }

    static class GuardianTargetSelector implements Predicate<EntityLivingBase>
    {
        private EntityGuardian parentEntity;

        public GuardianTargetSelector(EntityGuardian p_i45832_1_)
        {
            this.parentEntity = p_i45832_1_;
        }

        public boolean apply(EntityLivingBase p_apply_1_)
        {
            return (p_apply_1_ instanceof EntityPlayer || p_apply_1_ instanceof EntitySquid) && p_apply_1_.getDistanceSqToEntity(this.parentEntity) > 9.0D;
        }
    }
}
