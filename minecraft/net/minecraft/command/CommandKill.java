package net.minecraft.command;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandKill extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "kill";
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
		return "commands.kill.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			EntityPlayer entityplayer = getCommandSenderAsPlayer(sender);
			entityplayer.onKillCommand();
			notifyOperators(sender, this, "commands.kill.successful", new Object[] { entityplayer.getDisplayName() });
		} else {
			Entity entity = func_175768_b(sender, args[0]);
			entity.onKillCommand();
			notifyOperators(sender, this, "commands.kill.successful", new Object[] { entity.getDisplayName() });
		}
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
	}
}
