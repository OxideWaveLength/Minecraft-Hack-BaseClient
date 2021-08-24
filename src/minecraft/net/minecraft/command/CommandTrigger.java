package net.minecraft.command;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandTrigger extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "trigger";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 0;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.trigger.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 3) {
			throw new WrongUsageException("commands.trigger.usage", new Object[0]);
		} else {
			EntityPlayerMP entityplayermp;

			if (sender instanceof EntityPlayerMP) {
				entityplayermp = (EntityPlayerMP) sender;
			} else {
				Entity entity = sender.getCommandSenderEntity();

				if (!(entity instanceof EntityPlayerMP)) {
					throw new CommandException("commands.trigger.invalidPlayer", new Object[0]);
				}

				entityplayermp = (EntityPlayerMP) entity;
			}

			Scoreboard scoreboard = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
			ScoreObjective scoreobjective = scoreboard.getObjective(args[0]);

			if (scoreobjective != null && scoreobjective.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
				int i = parseInt(args[2]);

				if (!scoreboard.entityHasObjective(entityplayermp.getName(), scoreobjective)) {
					throw new CommandException("commands.trigger.invalidObjective", new Object[] { args[0] });
				} else {
					Score score = scoreboard.getValueFromObjective(entityplayermp.getName(), scoreobjective);

					if (score.isLocked()) {
						throw new CommandException("commands.trigger.disabled", new Object[] { args[0] });
					} else {
						if ("set".equals(args[1])) {
							score.setScorePoints(i);
						} else {
							if (!"add".equals(args[1])) {
								throw new CommandException("commands.trigger.invalidMode", new Object[] { args[1] });
							}

							score.increseScore(i);
						}

						score.setLocked(true);

						if (entityplayermp.theItemInWorldManager.isCreative()) {
							notifyOperators(sender, this, "commands.trigger.success", new Object[] { args[0], args[1], args[2] });
						}
					}
				}
			} else {
				throw new CommandException("commands.trigger.invalidObjective", new Object[] { args[0] });
			}
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			Scoreboard scoreboard = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
			List<String> list = Lists.<String>newArrayList();

			for (ScoreObjective scoreobjective : scoreboard.getScoreObjectives()) {
				if (scoreobjective.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
					list.add(scoreobjective.getName());
				}
			}

			return getListOfStringsMatchingLastWord(args, (String[]) list.toArray(new String[list.size()]));
		} else {
			return args.length == 2 ? getListOfStringsMatchingLastWord(args, new String[] { "add", "set" }) : null;
		}
	}
}
