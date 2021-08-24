package net.minecraft.command;

import java.util.List;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandBlockData extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "blockdata";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 2;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.blockdata.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 4) {
			throw new WrongUsageException("commands.blockdata.usage", new Object[0]);
		} else {
			sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
			BlockPos blockpos = parseBlockPos(sender, args, 0, false);
			World world = sender.getEntityWorld();

			if (!world.isBlockLoaded(blockpos)) {
				throw new CommandException("commands.blockdata.outOfWorld", new Object[0]);
			} else {
				TileEntity tileentity = world.getTileEntity(blockpos);

				if (tileentity == null) {
					throw new CommandException("commands.blockdata.notValid", new Object[0]);
				} else {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					tileentity.writeToNBT(nbttagcompound);
					NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttagcompound.copy();
					NBTTagCompound nbttagcompound2;

					try {
						nbttagcompound2 = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(sender, args, 3).getUnformattedText());
					} catch (NBTException nbtexception) {
						throw new CommandException("commands.blockdata.tagError", new Object[] { nbtexception.getMessage() });
					}

					nbttagcompound.merge(nbttagcompound2);
					nbttagcompound.setInteger("x", blockpos.getX());
					nbttagcompound.setInteger("y", blockpos.getY());
					nbttagcompound.setInteger("z", blockpos.getZ());

					if (nbttagcompound.equals(nbttagcompound1)) {
						throw new CommandException("commands.blockdata.failed", new Object[] { nbttagcompound.toString() });
					} else {
						tileentity.readFromNBT(nbttagcompound);
						tileentity.markDirty();
						world.markBlockForUpdate(blockpos);
						sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
						notifyOperators(sender, this, "commands.blockdata.success", new Object[] { nbttagcompound.toString() });
					}
				}
			}
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : null;
	}
}
