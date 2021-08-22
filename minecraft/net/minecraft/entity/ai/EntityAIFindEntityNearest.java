package net.minecraft.entity.ai;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;

public class EntityAIFindEntityNearest extends EntityAIBase {
	private static final Logger field_179444_a = LogManager.getLogger();
	private EntityLiving field_179442_b;
	private final Predicate<EntityLivingBase> field_179443_c;
	private final EntityAINearestAttackableTarget.Sorter field_179440_d;
	private EntityLivingBase field_179441_e;
	private Class<? extends EntityLivingBase> field_179439_f;

	public EntityAIFindEntityNearest(EntityLiving p_i45884_1_, Class<? extends EntityLivingBase> p_i45884_2_) {
		this.field_179442_b = p_i45884_1_;
		this.field_179439_f = p_i45884_2_;

		if (p_i45884_1_ instanceof EntityCreature) {
			field_179444_a.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
		}

		this.field_179443_c = new Predicate<EntityLivingBase>() {
			public boolean apply(EntityLivingBase p_apply_1_) {
				double d0 = EntityAIFindEntityNearest.this.func_179438_f();

				if (p_apply_1_.isSneaking()) {
					d0 *= 0.800000011920929D;
				}

				return p_apply_1_.isInvisible() ? false : ((double) p_apply_1_.getDistanceToEntity(EntityAIFindEntityNearest.this.field_179442_b) > d0 ? false : EntityAITarget.isSuitableTarget(EntityAIFindEntityNearest.this.field_179442_b, p_apply_1_, false, true));
			}
		};
		this.field_179440_d = new EntityAINearestAttackableTarget.Sorter(p_i45884_1_);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		double d0 = this.func_179438_f();
		List<EntityLivingBase> list = this.field_179442_b.worldObj.<EntityLivingBase>getEntitiesWithinAABB(this.field_179439_f, this.field_179442_b.getEntityBoundingBox().expand(d0, 4.0D, d0), this.field_179443_c);
		Collections.sort(list, this.field_179440_d);

		if (list.isEmpty()) {
			return false;
		} else {
			this.field_179441_e = (EntityLivingBase) list.get(0);
			return true;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		EntityLivingBase entitylivingbase = this.field_179442_b.getAttackTarget();

		if (entitylivingbase == null) {
			return false;
		} else if (!entitylivingbase.isEntityAlive()) {
			return false;
		} else {
			double d0 = this.func_179438_f();
			return this.field_179442_b.getDistanceSqToEntity(entitylivingbase) > d0 * d0 ? false : !(entitylivingbase instanceof EntityPlayerMP) || !((EntityPlayerMP) entitylivingbase).theItemInWorldManager.isCreative();
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.field_179442_b.setAttackTarget(this.field_179441_e);
		super.startExecuting();
	}

	/**
	 * Resets the task
	 */
	public void resetTask() {
		this.field_179442_b.setAttackTarget((EntityLivingBase) null);
		super.startExecuting();
	}

	protected double func_179438_f() {
		IAttributeInstance iattributeinstance = this.field_179442_b.getEntityAttribute(SharedMonsterAttributes.followRange);
		return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
	}
}
