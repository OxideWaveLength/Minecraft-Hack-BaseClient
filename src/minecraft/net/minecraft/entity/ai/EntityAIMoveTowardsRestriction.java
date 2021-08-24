package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsRestriction extends EntityAIBase {
	private EntityCreature theEntity;
	private double movePosX;
	private double movePosY;
	private double movePosZ;
	private double movementSpeed;

	public EntityAIMoveTowardsRestriction(EntityCreature creatureIn, double speedIn) {
		this.theEntity = creatureIn;
		this.movementSpeed = speedIn;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.theEntity.isWithinHomeDistanceCurrentPosition()) {
			return false;
		} else {
			BlockPos blockpos = this.theEntity.getHomePosition();
			Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, new Vec3((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ()));

			if (vec3 == null) {
				return false;
			} else {
				this.movePosX = vec3.xCoord;
				this.movePosY = vec3.yCoord;
				this.movePosZ = vec3.zCoord;
				return true;
			}
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return !this.theEntity.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.movementSpeed);
	}
}
