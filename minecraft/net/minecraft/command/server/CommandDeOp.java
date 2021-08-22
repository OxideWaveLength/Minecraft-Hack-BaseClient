package net.minecraft.command.server;

import java.util.List;

import com.mojang.authlib.GameProfile;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandDeOp extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "deop";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 3;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.deop.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 1 && args[0].length() > 0) {
			MinecraftServer minecraftserver = MinecraftServer.getServer();
			GameProfile gameprofile = minecraftserver.getConfigurationManager().getOppedPlayers().getGameProfileFromName(args[0]);

			if (gameprofile == null) {
				throw new CommandException("commands.deop.failed", new Object[] { args[0] });
			} else {
				minecraftserver.getConfigurationManager().removeOp(gameprofile);
				notifyOperators(sender, this, "commands.deop.success", new Object[] { args[0] });
			}
		} else {
			throw new WrongUsageException("commands.deop.usage", new Object[0]);
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getOppedPlayerNames()) : null;
	}
}
