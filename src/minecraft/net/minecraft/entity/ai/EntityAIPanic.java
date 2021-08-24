package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Vec3;

public class EntityAIPanic extends EntityAIBase {
	private EntityCreature theEntityCreature;
	protected double speed;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIPanic(EntityCreature creature, double speedIn) {
		this.theEntityCreature = creature;
		this.speed = speedIn;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.theEntityCreature.getAITarget() == null && !this.theEntityCreature.isBurning()) {
			return false;
		} else {
			Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.theEntityCreature, 5, 4);

			if (vec3 == null) {
				return false;
			} else {
				this.randPosX = vec3.xCoord;
				this.randPosY = vec3.yCoord;
				this.randPosZ = vec3.zCoord;
				return true;
			}
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.theEntityCreature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return !this.theEntityCreature.getNavigator().noPath();
	}
}
