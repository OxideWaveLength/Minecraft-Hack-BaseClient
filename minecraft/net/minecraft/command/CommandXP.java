package net.minecraft.command;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandXP extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "xp";
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
		return "commands.xp.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new WrongUsageException("commands.xp.usage", new Object[0]);
		} else {
			String s = args[0];
			boolean flag = s.endsWith("l") || s.endsWith("L");

			if (flag && s.length() > 1) {
				s = s.substring(0, s.length() - 1);
			}

			int i = parseInt(s);
			boolean flag1 = i < 0;

			if (flag1) {
				i *= -1;
			}

			EntityPlayer entityplayer = args.length > 1 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);

			if (flag) {
				sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, entityplayer.experienceLevel);

				if (flag1) {
					entityplayer.addExperienceLevel(-i);
					notifyOperators(sender, this, "commands.xp.success.negative.levels", new Object[] { Integer.valueOf(i), entityplayer.getName() });
				} else {
					entityplayer.addExperienceLevel(i);
					notifyOperators(sender, this, "commands.xp.success.levels", new Object[] { Integer.valueOf(i), entityplayer.getName() });
				}
			} else {
				sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, entityplayer.experienceTotal);

				if (flag1) {
					throw new CommandException("commands.xp.failure.widthdrawXp", new Object[0]);
				}

				entityplayer.addExperience(i);
				notifyOperators(sender, this, "commands.xp.success", new Object[] { Integer.valueOf(i), entityplayer.getName() });
			}
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : null;
	}

	protected String[] getAllUsernames() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 1;
	}
}
