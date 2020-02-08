package net.minecraft.command.server;

import java.util.Date;
import java.util.List;

import com.mojang.authlib.GameProfile;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.BlockPos;

public class CommandBanPlayer extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "ban";
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
		return "commands.ban.usage";
	}

	/**
	 * Returns true if the given command sender is allowed to use this command.
	 */
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer() && super.canCommandSenderUseCommand(sender);
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 1 && args[0].length() > 0) {
			MinecraftServer minecraftserver = MinecraftServer.getServer();
			GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[0]);

			if (gameprofile == null) {
				throw new CommandException("commands.ban.failed", new Object[] { args[0] });
			} else {
				String s = null;

				if (args.length >= 2) {
					s = getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
				}

				UserListBansEntry userlistbansentry = new UserListBansEntry(gameprofile, (Date) null, sender.getName(), (Date) null, s);
				minecraftserver.getConfigurationManager().getBannedPlayers().addEntry(userlistbansentry);
				EntityPlayerMP entityplayermp = minecraftserver.getConfigurationManager().getPlayerByUsername(args[0]);

				if (entityplayermp != null) {
					entityplayermp.playerNetServerHandler.kickPlayerFromServer("You are banned from this server.");
				}

				notifyOperators(sender, this, "commands.ban.success", new Object[] { args[0] });
			}
		} else {
			throw new WrongUsageException("commands.ban.usage", new Object[0]);
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length >= 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
	}
}
