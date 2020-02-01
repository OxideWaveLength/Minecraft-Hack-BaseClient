package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAIFindEntityNearestPlayer extends EntityAIBase
{
    private static final Logger field_179436_a = LogManager.getLogger();
    private EntityLiving field_179434_b;
    private final Predicate<Entity> field_179435_c;
    private final EntityAINearestAttackableTarget.Sorter field_179432_d;
    private EntityLivingBase field_179433_e;

    public EntityAIFindEntityNearestPlayer(EntityLiving p_i45882_1_)
    {
        this.field_179434_b = p_i45882_1_;

        if (p_i45882_1_ instanceof EntityCreature)
        {
            field_179436_a.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
        }

        this.field_179435_c = new Predicate<Entity>()
        {
            public boolean apply(Entity p_apply_1_)
            {
                if (!(p_apply_1_ instanceof EntityPlayer))
                {
                    return false;
                }
                else if (((EntityPlayer)p_apply_1_).capabilities.disableDamage)
                {
                    return false;
                }
                else
                {
                    double d0 = EntityAIFindEntityNearestPlayer.this.func_179431_f();

                    if (p_apply_1_.isSneaking())
                    {
                        d0 *= 0.800000011920929D;
                    }

                    if (p_apply_1_.isInvisible())
                    {
                        float f = ((EntityPlayer)p_apply_1_).getArmorVisibility();

                        if (f < 0.1F)
                        {
                            f = 0.1F;
                        }

                        d0 *= (double)(0.7F * f);
                    }

                    return (double)p_apply_1_.getDistanceToEntity(EntityAIFindEntityNearestPlayer.this.field_179434_b) > d0 ? false : EntityAITarget.isSuitableTarget(EntityAIFindEntityNearestPlayer.this.field_179434_b, (EntityLivingBase)p_apply_1_, false, true);
                }
            }
        };
        this.field_179432_d = new EntityAINearestAttackableTarget.Sorter(p_i45882_1_);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        double d0 = this.func_179431_f();
        List<EntityPlayer> list = this.field_179434_b.worldObj.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, this.field_179434_b.getEntityBoundingBox().expand(d0, 4.0D, d0), this.field_179435_c);
        Collections.sort(list, this.field_179432_d);

        if (list.isEmpty())
        {
            return false;
        }
        else
        {
            this.field_179433_e = (EntityLivingBase)list.get(0);
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        EntityLivingBase entitylivingbase = this.field_179434_b.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).capabilities.disableDamage)
        {
            return false;
        }
        else
        {
            Team team = this.field_179434_b.getTeam();
            Team team1 = entitylivingbase.getTeam();

            if (team != null && team1 == team)
            {
                return false;
            }
            else
            {
                double d0 = this.func_179431_f();
                return this.field_179434_b.getDistanceSqToEntity(entitylivingbase) > d0 * d0 ? false : !(entitylivingbase instanceof EntityPlayerMP) || !((EntityPlayerMP)entitylivingbase).theItemInWorldManager.isCreative();
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_179434_b.setAttackTarget(this.field_179433_e);
        super.startExecuting();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.field_179434_b.setAttackTarget((EntityLivingBase)null);
        super.startExecuting();
    }

    protected double func_179431_f()
    {
        IAttributeInstance iattributeinstance = this.field_179434_b.getEntityAttribute(SharedMonsterAttributes.followRange);
        return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
    }
}
