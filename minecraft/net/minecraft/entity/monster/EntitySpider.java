package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntitySpider extends EntityMob
{
    public EntitySpider(World worldIn)
    {
        super(worldIn);
        this.setSize(1.4F, 0.9F);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new EntitySpider.AISpiderAttack(this, EntityPlayer.class));
        this.tasks.addTask(4, new EntitySpider.AISpiderAttack(this, EntityIronGolem.class));
        this.tasks.addTask(5, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntitySpider.AISpiderTarget(this, EntityPlayer.class));
        this.targetTasks.addTask(3, new EntitySpider.AISpiderTarget(this, EntityIronGolem.class));
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)(this.height * 0.5F);
    }

    /**
     * Returns new PathNavigateGround instance
     */
    protected PathNavigate getNewNavigator(World worldIn)
    {
        return new PathNavigateClimber(this, worldIn);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote)
        {
            this.setBesideClimbableBlock(this.isCollidedHorizontally);
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.spider.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.spider.say";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.spider.death";
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound("mob.spider.step", 0.15F, 1.0F);
    }

    protected Item getDropItem()
    {
        return Items.string;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
        super.dropFewItems(p_70628_1_, p_70628_2_);

        if (p_70628_1_ && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + p_70628_2_) > 0))
        {
            this.dropItem(Items.spider_eye, 1);
        }
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        return this.isBesideClimbableBlock();
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn)
    {
        return potioneffectIn.getPotionID() == Potion.poison.id ? false : super.isPotionApplicable(potioneffectIn);
    }

    /**
     * Returns true if the WatchableObject (Byte) is 0x01 otherwise returns false. The WatchableObject is updated using
     * setBesideClimableBlock.
     */
    public boolean isBesideClimbableBlock()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    /**
     * Updates the WatchableObject (Byte) created in entityInit(), setting it to 0x01 if par1 is true or 0x00 if it is
     * false.
     */
    public void setBesideClimbableBlock(boolean p_70839_1_)
    {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);

        if (p_70839_1_)
        {
            b0 = (byte)(b0 | 1);
        }
        else
        {
            b0 = (byte)(b0 & -2);
        }

        this.dataWatcher.updateObject(16, Byte.valueOf(b0));
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        livingdata = super.onInitialSpawn(difficulty, livingdata);

        if (this.worldObj.rand.nextInt(100) == 0)
        {
            EntitySkeleton entityskeleton = new EntitySkeleton(this.worldObj);
            entityskeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            entityskeleton.onInitialSpawn(difficulty, (IEntityLivingData)null);
            this.worldObj.spawnEntityInWorld(entityskeleton);
            entityskeleton.mountEntity(this);
        }

        if (livingdata == null)
        {
            livingdata = new EntitySpider.GroupData();

            if (this.worldObj.getDifficulty() == EnumDifficulty.HARD && this.worldObj.rand.nextFloat() < 0.1F * difficulty.getClampedAdditionalDifficulty())
            {
                ((EntitySpider.GroupData)livingdata).func_111104_a(this.worldObj.rand);
            }
        }

        if (livingdata instanceof EntitySpider.GroupData)
        {
            int i = ((EntitySpider.GroupData)livingdata).potionEffectId;

            if (i > 0 && Potion.potionTypes[i] != null)
            {
                this.addPotionEffect(new PotionEffect(i, Integer.MAX_VALUE));
            }
        }

        return livingdata;
    }

    public float getEyeHeight()
    {
        return 0.65F;
    }

    static class AISpiderAttack extends EntityAIAttackOnCollide
    {
        public AISpiderAttack(EntitySpider p_i45819_1_, Class <? extends Entity > targetClass)
        {
            super(p_i45819_1_, targetClass, 1.0D, true);
        }

        public boolean continueExecuting()
        {
            float f = this.attacker.getBrightness(1.0F);

            if (f >= 0.5F && this.attacker.getRNG().nextInt(100) == 0)
            {
                this.attacker.setAttackTarget((EntityLivingBase)null);
                return false;
            }
            else
            {
                return super.continueExecuting();
            }
        }

        protected double func_179512_a(EntityLivingBase attackTarget)
        {
            return (double)(4.0F + attackTarget.width);
        }
    }

    static class AISpiderTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget
    {
        public AISpiderTarget(EntitySpider p_i45818_1_, Class<T> classTarget)
        {
            super(p_i45818_1_, classTarget, true);
        }

        public boolean shouldExecute()
        {
            float f = this.taskOwner.getBrightness(1.0F);
            return f >= 0.5F ? false : super.shouldExecute();
        }
    }

    public static class GroupData implements IEntityLivingData
    {
        public int potionEffectId;

        public void func_111104_a(Random rand)
        {
            int i = rand.nextInt(5);

            if (i <= 1)
            {
                this.potionEffectId = Potion.moveSpeed.id;
            }
            else if (i <= 2)
            {
                this.potionEffectId = Potion.damageBoost.id;
            }
            else if (i <= 3)
            {
                this.potionEffectId = Potion.regeneration.id;
            }
            else if (i <= 4)
            {
                this.potionEffectId = Potion.invisibility.id;
            }
        }
    }
}
