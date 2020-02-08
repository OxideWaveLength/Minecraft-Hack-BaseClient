package net.minecraft.village;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData {
	private World worldObj;
	private final List<BlockPos> villagerPositionsList = Lists.<BlockPos>newArrayList();
	private final List<VillageDoorInfo> newDoors = Lists.<VillageDoorInfo>newArrayList();
	private final List<Village> villageList = Lists.<Village>newArrayList();
	private int tickCounter;

	public VillageCollection(String name) {
		super(name);
	}

	public VillageCollection(World worldIn) {
		super(fileNameForProvider(worldIn.provider));
		this.worldObj = worldIn;
		this.markDirty();
	}

	public void setWorldsForAll(World worldIn) {
		this.worldObj = worldIn;

		for (Village village : this.villageList) {
			village.setWorld(worldIn);
		}
	}

	public void addToVillagerPositionList(BlockPos pos) {
		if (this.villagerPositionsList.size() <= 64) {
			if (!this.positionInList(pos)) {
				this.villagerPositionsList.add(pos);
			}
		}
	}

	/**
	 * Runs a single tick for the village collection
	 */
	public void tick() {
		++this.tickCounter;

		for (Village village : this.villageList) {
			village.tick(this.tickCounter);
		}

		this.removeAnnihilatedVillages();
		this.dropOldestVillagerPosition();
		this.addNewDoorsToVillageOrCreateVillage();

		if (this.tickCounter % 400 == 0) {
			this.markDirty();
		}
	}

	private void removeAnnihilatedVillages() {
		Iterator<Village> iterator = this.villageList.iterator();

		while (iterator.hasNext()) {
			Village village = (Village) iterator.next();

			if (village.isAnnihilated()) {
				iterator.remove();
				this.markDirty();
			}
		}
	}

	public List<Village> getVillageList() {
		return this.villageList;
	}

	public Village getNearestVillage(BlockPos doorBlock, int radius) {
		Village village = null;
		double d0 = 3.4028234663852886E38D;

		for (Village village1 : this.villageList) {
			double d1 = village1.getCenter().distanceSq(doorBlock);

			if (d1 < d0) {
				float f = (float) (radius + village1.getVillageRadius());

				if (d1 <= (double) (f * f)) {
					village = village1;
					d0 = d1;
				}
			}
		}

		return village;
	}

	private void dropOldestVillagerPosition() {
		if (!this.villagerPositionsList.isEmpty()) {
			this.addDoorsAround((BlockPos) this.villagerPositionsList.remove(0));
		}
	}

	private void addNewDoorsToVillageOrCreateVillage() {
		for (int i = 0; i < this.newDoors.size(); ++i) {
			VillageDoorInfo villagedoorinfo = (VillageDoorInfo) this.newDoors.get(i);
			Village village = this.getNearestVillage(villagedoorinfo.getDoorBlockPos(), 32);

			if (village == null) {
				village = new Village(this.worldObj);
				this.villageList.add(village);
				this.markDirty();
			}

			village.addVillageDoorInfo(villagedoorinfo);
		}

		this.newDoors.clear();
	}

	private void addDoorsAround(BlockPos central) {
		int i = 16;
		int j = 4;
		int k = 16;

		for (int l = -i; l < i; ++l) {
			for (int i1 = -j; i1 < j; ++i1) {
				for (int j1 = -k; j1 < k; ++j1) {
					BlockPos blockpos = central.add(l, i1, j1);

					if (this.isWoodDoor(blockpos)) {
						VillageDoorInfo villagedoorinfo = this.checkDoorExistence(blockpos);

						if (villagedoorinfo == null) {
							this.addToNewDoorsList(blockpos);
						} else {
							villagedoorinfo.func_179849_a(this.tickCounter);
						}
					}
				}
			}
		}
	}

	/**
	 * returns the VillageDoorInfo if it exists in any village or in the newDoor
	 * list, otherwise returns null
	 */
	private VillageDoorInfo checkDoorExistence(BlockPos doorBlock) {
		for (VillageDoorInfo villagedoorinfo : this.newDoors) {
			if (villagedoorinfo.getDoorBlockPos().getX() == doorBlock.getX() && villagedoorinfo.getDoorBlockPos().getZ() == doorBlock.getZ() && Math.abs(villagedoorinfo.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
				return villagedoorinfo;
			}
		}

		for (Village village : this.villageList) {
			VillageDoorInfo villagedoorinfo1 = village.getExistedDoor(doorBlock);

			if (villagedoorinfo1 != null) {
				return villagedoorinfo1;
			}
		}

		return null;
	}

	private void addToNewDoorsList(BlockPos doorBlock) {
		EnumFacing enumfacing = BlockDoor.getFacing(this.worldObj, doorBlock);
		EnumFacing enumfacing1 = enumfacing.getOpposite();
		int i = this.countBlocksCanSeeSky(doorBlock, enumfacing, 5);
		int j = this.countBlocksCanSeeSky(doorBlock, enumfacing1, i + 1);

		if (i != j) {
			this.newDoors.add(new VillageDoorInfo(doorBlock, i < j ? enumfacing : enumfacing1, this.tickCounter));
		}
	}

	/**
	 * Check five blocks in the direction. The centerPos will not be checked.
	 */
	private int countBlocksCanSeeSky(BlockPos centerPos, EnumFacing direction, int limitation) {
		int i = 0;

		for (int j = 1; j <= 5; ++j) {
			if (this.worldObj.canSeeSky(centerPos.offset(direction, j))) {
				++i;

				if (i >= limitation) {
					return i;
				}
			}
		}

		return i;
	}

	private boolean positionInList(BlockPos pos) {
		for (BlockPos blockpos : this.villagerPositionsList) {
			if (blockpos.equals(pos)) {
				return true;
			}
		}

		return false;
	}

	private boolean isWoodDoor(BlockPos doorPos) {
		Block block = this.worldObj.getBlockState(doorPos).getBlock();
		return block instanceof BlockDoor ? block.getMaterial() == Material.wood : false;
	}

	/**
	 * reads in data from the NBTTagCompound into this MapDataBase
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		this.tickCounter = nbt.getInteger("Tick");
		NBTTagList nbttaglist = nbt.getTagList("Villages", 10);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			Village village = new Village();
			village.readVillageDataFromNBT(nbttagcompound);
			this.villageList.add(village);
		}
	}

	/**
	 * write data to NBTTagCompound from this MapDataBase, similar to Entities and
	 * TileEntities
	 */
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("Tick", this.tickCounter);
		NBTTagList nbttaglist = new NBTTagList();

		for (Village village : this.villageList) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			village.writeVillageDataToNBT(nbttagcompound);
			nbttaglist.appendTag(nbttagcompound);
		}

		nbt.setTag("Villages", nbttaglist);
	}

	public static String fileNameForProvider(WorldProvider provider) {
		return "villages" + provider.getInternalNameSuffix();
	}
}
