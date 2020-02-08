package net.minecraft.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart {
	/** Mob spawner logic for this spawner minecart. */
	private final MobSpawnerBaseLogic mobSpawnerLogic = new MobSpawnerBaseLogic() {
		public void func_98267_a(int id) {
			EntityMinecartMobSpawner.this.worldObj.setEntityState(EntityMinecartMobSpawner.this, (byte) id);
		}

		public World getSpawnerWorld() {
			return EntityMinecartMobSpawner.this.worldObj;
		}

		public BlockPos getSpawnerPosition() {
			return new BlockPos(EntityMinecartMobSpawner.this);
		}
	};

	public EntityMinecartMobSpawner(World worldIn) {
		super(worldIn);
	}

	public EntityMinecartMobSpawner(World worldIn, double p_i1726_2_, double p_i1726_4_, double p_i1726_6_) {
		super(worldIn, p_i1726_2_, p_i1726_4_, p_i1726_6_);
	}

	public EntityMinecart.EnumMinecartType getMinecartType() {
		return EntityMinecart.EnumMinecartType.SPAWNER;
	}

	public IBlockState getDefaultDisplayTile() {
		return Blocks.mob_spawner.getDefaultState();
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {
		super.readEntityFromNBT(tagCompund);
		this.mobSpawnerLogic.readFromNBT(tagCompund);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {
		super.writeEntityToNBT(tagCompound);
		this.mobSpawnerLogic.writeToNBT(tagCompound);
	}

	public void handleStatusUpdate(byte id) {
		this.mobSpawnerLogic.setDelayToMin(id);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		super.onUpdate();
		this.mobSpawnerLogic.updateSpawner();
	}

	public MobSpawnerBaseLogic func_98039_d() {
		return this.mobSpawnerLogic;
	}
}
