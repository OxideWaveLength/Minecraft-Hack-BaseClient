package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class EntityAINearestAttackableTarget<T extends EntityLivingBase> extends EntityAITarget
{
    protected final Class<T> targetClass;
    private final int targetChance;

    /** Instance of EntityAINearestAttackableTargetSorter. */
    protected final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    protected Predicate <? super T > targetEntitySelector;
    protected EntityLivingBase targetEntity;

    public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight)
    {
        this(creature, classTarget, checkSight, false);
    }

    public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby)
    {
        this(creature, classTarget, 10, checkSight, onlyNearby, (Predicate <? super T >)null);
    }

    public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, final Predicate <? super T > targetSelector)
    {
        super(creature, checkSight, onlyNearby);
        this.targetClass = classTarget;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(creature);
        this.setMutexBits(1);
        this.targetEntitySelector = new Predicate<T>()
        {
            public boolean apply(T p_apply_1_)
            {
                if (targetSelector != null && !targetSelector.apply(p_apply_1_))
                {
                    return false;
                }
                else
                {
                    if (p_apply_1_ instanceof EntityPlayer)
                    {
                        double d0 = EntityAINearestAttackableTarget.this.getTargetDistance();

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

                        if ((double)p_apply_1_.getDistanceToEntity(EntityAINearestAttackableTarget.this.taskOwner) > d0)
                        {
                            return false;
                        }
                    }

                    return EntityAINearestAttackableTarget.this.isSuitableTarget(p_apply_1_, false);
                }
            }
        };
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0)
        {
            return false;
        }
        else
        {
            double d0 = this.getTargetDistance();
            List<T> list = this.taskOwner.worldObj.<T>getEntitiesWithinAABB(this.targetClass, this.taskOwner.getEntityBoundingBox().expand(d0, 4.0D, d0), Predicates.<T> and (this.targetEntitySelector, EntitySelectors.NOT_SPECTATING));
            Collections.sort(list, this.theNearestAttackableTargetSorter);

            if (list.isEmpty())
            {
                return false;
            }
            else
            {
                this.targetEntity = (EntityLivingBase)list.get(0);
                return true;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }

    public static class Sorter implements Comparator<Entity>
    {
        private final Entity theEntity;

        public Sorter(Entity theEntityIn)
        {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_)
        {
            double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
            double d1 = this.theEntity.getDistanceSqToEntity(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }
}
