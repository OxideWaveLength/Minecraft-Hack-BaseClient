package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIRestrictOpenDoor extends EntityAIBase {
	private EntityCreature entityObj;
	private VillageDoorInfo frontDoor;

	public EntityAIRestrictOpenDoor(EntityCreature creatureIn) {
		this.entityObj = creatureIn;

		if (!(creatureIn.getNavigator() instanceof PathNavigateGround)) {
			throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
		}
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.entityObj.worldObj.isDaytime()) {
			return false;
		} else {
			BlockPos blockpos = new BlockPos(this.entityObj);
			Village village = this.entityObj.worldObj.getVillageCollection().getNearestVillage(blockpos, 16);

			if (village == null) {
				return false;
			} else {
				this.frontDoor = village.getNearestDoor(blockpos);
				return this.frontDoor == null ? false : (double) this.frontDoor.getDistanceToInsideBlockSq(blockpos) < 2.25D;
			}
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return this.entityObj.worldObj.isDaytime() ? false : !this.frontDoor.getIsDetachedFromVillageFlag() && this.frontDoor.func_179850_c(new BlockPos(this.entityObj));
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		((PathNavigateGround) this.entityObj.getNavigator()).setBreakDoors(false);
		((PathNavigateGround) this.entityObj.getNavigator()).setEnterDoors(false);
	}

	/**
	 * Resets the task
	 */
	public void resetTask() {
		((PathNavigateGround) this.entityObj.getNavigator()).setBreakDoors(true);
		((PathNavigateGround) this.entityObj.getNavigator()).setEnterDoors(true);
		this.frontDoor = null;
	}

	/**
	 * Updates the task
	 */
	public void updateTask() {
		this.frontDoor.incrementDoorOpeningRestrictionCounter();
	}
}
