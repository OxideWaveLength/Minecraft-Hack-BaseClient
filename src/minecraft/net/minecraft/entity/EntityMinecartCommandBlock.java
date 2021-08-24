package net.minecraft.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityMinecartCommandBlock extends EntityMinecart {
	private final CommandBlockLogic commandBlockLogic = new CommandBlockLogic() {
		public void updateCommand() {
			EntityMinecartCommandBlock.this.getDataWatcher().updateObject(23, this.getCommand());
			EntityMinecartCommandBlock.this.getDataWatcher().updateObject(24, IChatComponent.Serializer.componentToJson(this.getLastOutput()));
		}

		public int func_145751_f() {
			return 1;
		}

		public void func_145757_a(ByteBuf p_145757_1_) {
			p_145757_1_.writeInt(EntityMinecartCommandBlock.this.getEntityId());
		}

		public BlockPos getPosition() {
			return new BlockPos(EntityMinecartCommandBlock.this.posX, EntityMinecartCommandBlock.this.posY + 0.5D, EntityMinecartCommandBlock.this.posZ);
		}

		public Vec3 getPositionVector() {
			return new Vec3(EntityMinecartCommandBlock.this.posX, EntityMinecartCommandBlock.this.posY, EntityMinecartCommandBlock.this.posZ);
		}

		public World getEntityWorld() {
			return EntityMinecartCommandBlock.this.worldObj;
		}

		public Entity getCommandSenderEntity() {
			return EntityMinecartCommandBlock.this;
		}
	};

	/** Cooldown before command block logic runs again in ticks */
	private int activatorRailCooldown = 0;

	public EntityMinecartCommandBlock(World worldIn) {
		super(worldIn);
	}

	public EntityMinecartCommandBlock(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	protected void entityInit() {
		super.entityInit();
		this.getDataWatcher().addObject(23, "");
		this.getDataWatcher().addObject(24, "");
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {
		super.readEntityFromNBT(tagCompund);
		this.commandBlockLogic.readDataFromNBT(tagCompund);
		this.getDataWatcher().updateObject(23, this.getCommandBlockLogic().getCommand());
		this.getDataWatcher().updateObject(24, IChatComponent.Serializer.componentToJson(this.getCommandBlockLogic().getLastOutput()));
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {
		super.writeEntityToNBT(tagCompound);
		this.commandBlockLogic.writeDataToNBT(tagCompound);
	}

	public EntityMinecart.EnumMinecartType getMinecartType() {
		return EntityMinecart.EnumMinecartType.COMMAND_BLOCK;
	}

	public IBlockState getDefaultDisplayTile() {
		return Blocks.command_block.getDefaultState();
	}

	public CommandBlockLogic getCommandBlockLogic() {
		return this.commandBlockLogic;
	}

	/**
	 * Called every tick the minecart is on an activator rail. Args: x, y, z, is the
	 * rail receiving power
	 */
	public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
		if (receivingPower && this.ticksExisted - this.activatorRailCooldown >= 4) {
			this.getCommandBlockLogic().trigger(this.worldObj);
			this.activatorRailCooldown = this.ticksExisted;
		}
	}

	/**
	 * First layer of player interaction
	 */
	public boolean interactFirst(EntityPlayer playerIn) {
		this.commandBlockLogic.tryOpenEditCommandBlock(playerIn);
		return false;
	}

	public void onDataWatcherUpdate(int dataID) {
		super.onDataWatcherUpdate(dataID);

		if (dataID == 24) {
			try {
				this.commandBlockLogic.setLastOutput(IChatComponent.Serializer.jsonToComponent(this.getDataWatcher().getWatchableObjectString(24)));
			} catch (Throwable var3) {
				;
			}
		} else if (dataID == 23) {
			this.commandBlockLogic.setCommand(this.getDataWatcher().getWatchableObjectString(23));
		}
	}
}
