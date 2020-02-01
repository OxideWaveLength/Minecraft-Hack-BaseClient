package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntitySilverfish extends EntityMob
{
    private EntitySilverfish.AISummonSilverfish summonSilverfish;

    public EntitySilverfish(World worldIn)
    {
        super(worldIn);
        this.setSize(0.4F, 0.3F);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, this.summonSilverfish = new EntitySilverfish.AISummonSilverfish(this));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        this.tasks.addTask(5, new EntitySilverfish.AIHideInStone(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return 0.2D;
    }

    public float getEyeHeight()
    {
        return 0.1F;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.silverfish.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.silverfish.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.silverfish.kill";
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            if (source instanceof EntityDamageSource || source == DamageSource.magic)
            {
                this.summonSilverfish.func_179462_f();
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound("mob.silverfish.step", 0.15F, 1.0F);
    }

    protected Item getDropItem()
    {
        return null;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.renderYawOffset = this.rotationYaw;
        super.onUpdate();
    }

    public float getBlockPathWeight(BlockPos pos)
    {
        return this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.stone ? 10.0F : super.getBlockPathWeight(pos);
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        return true;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        if (super.getCanSpawnHere())
        {
            EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 5.0D);
            return entityplayer == null;
        }
        else
        {
            return false;
        }
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    static class AIHideInStone extends EntityAIWander
    {
        private final EntitySilverfish field_179485_a;
        private EnumFacing facing;
        private boolean field_179484_c;

        public AIHideInStone(EntitySilverfish p_i45827_1_)
        {
            super(p_i45827_1_, 1.0D, 10);
            this.field_179485_a = p_i45827_1_;
            this.setMutexBits(1);
        }

        public boolean shouldExecute()
        {
            if (this.field_179485_a.getAttackTarget() != null)
            {
                return false;
            }
            else if (!this.field_179485_a.getNavigator().noPath())
            {
                return false;
            }
            else
            {
                Random random = this.field_179485_a.getRNG();

                if (random.nextInt(10) == 0)
                {
                    this.facing = EnumFacing.random(random);
                    BlockPos blockpos = (new BlockPos(this.field_179485_a.posX, this.field_179485_a.posY + 0.5D, this.field_179485_a.posZ)).offset(this.facing);
                    IBlockState iblockstate = this.field_179485_a.worldObj.getBlockState(blockpos);

                    if (BlockSilverfish.canContainSilverfish(iblockstate))
                    {
                        this.field_179484_c = true;
                        return true;
                    }
                }

                this.field_179484_c = false;
                return super.shouldExecute();
            }
        }

        public boolean continueExecuting()
        {
            return this.field_179484_c ? false : super.continueExecuting();
        }

        public void startExecuting()
        {
            if (!this.field_179484_c)
            {
                super.startExecuting();
            }
            else
            {
                World world = this.field_179485_a.worldObj;
                BlockPos blockpos = (new BlockPos(this.field_179485_a.posX, this.field_179485_a.posY + 0.5D, this.field_179485_a.posZ)).offset(this.facing);
                IBlockState iblockstate = world.getBlockState(blockpos);

                if (BlockSilverfish.canContainSilverfish(iblockstate))
                {
                    world.setBlockState(blockpos, Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.forModelBlock(iblockstate)), 3);
                    this.field_179485_a.spawnExplosionParticle();
                    this.field_179485_a.setDead();
                }
            }
        }
    }

    static class AISummonSilverfish extends EntityAIBase
    {
        private EntitySilverfish silverfish;
        private int field_179463_b;

        public AISummonSilverfish(EntitySilverfish p_i45826_1_)
        {
            this.silverfish = p_i45826_1_;
        }

        public void func_179462_f()
        {
            if (this.field_179463_b == 0)
            {
                this.field_179463_b = 20;
            }
        }

        public boolean shouldExecute()
        {
            return this.field_179463_b > 0;
        }

        public void updateTask()
        {
            --this.field_179463_b;

            if (this.field_179463_b <= 0)
            {
                World world = this.silverfish.worldObj;
                Random random = this.silverfish.getRNG();
                BlockPos blockpos = new BlockPos(this.silverfish);

                for (int i = 0; i <= 5 && i >= -5; i = i <= 0 ? 1 - i : 0 - i)
                {
                    for (int j = 0; j <= 10 && j >= -10; j = j <= 0 ? 1 - j : 0 - j)
                    {
                        for (int k = 0; k <= 10 && k >= -10; k = k <= 0 ? 1 - k : 0 - k)
                        {
                            BlockPos blockpos1 = blockpos.add(j, i, k);
                            IBlockState iblockstate = world.getBlockState(blockpos1);

                            if (iblockstate.getBlock() == Blocks.monster_egg)
                            {
                                if (world.getGameRules().getBoolean("mobGriefing"))
                                {
                                    world.destroyBlock(blockpos1, true);
                                }
                                else
                                {
                                    world.setBlockState(blockpos1, ((BlockSilverfish.EnumType)iblockstate.getValue(BlockSilverfish.VARIANT)).getModelBlock(), 3);
                                }

                                if (random.nextBoolean())
                                {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
