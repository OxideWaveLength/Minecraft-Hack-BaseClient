package net.minecraft.command;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "gamerule";
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
		return "commands.gamerule.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		GameRules gamerules = this.getGameRules();
		String s = args.length > 0 ? args[0] : "";
		String s1 = args.length > 1 ? buildString(args, 1) : "";

		switch (args.length) {
		case 0:
			sender.addChatMessage(new ChatComponentText(joinNiceString(gamerules.getRules())));
			break;

		case 1:
			if (!gamerules.hasRule(s)) {
				throw new CommandException("commands.gamerule.norule", new Object[] { s });
			}

			String s2 = gamerules.getString(s);
			sender.addChatMessage((new ChatComponentText(s)).appendText(" = ").appendText(s2));
			sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, gamerules.getInt(s));
			break;

		default:
			if (gamerules.areSameType(s, GameRules.ValueType.BOOLEAN_VALUE) && !"true".equals(s1) && !"false".equals(s1)) {
				throw new CommandException("commands.generic.boolean.invalid", new Object[] { s1 });
			}

			gamerules.setOrCreateGameRule(s, s1);
			func_175773_a(gamerules, s);
			notifyOperators(sender, this, "commands.gamerule.success", new Object[0]);
		}
	}

	public static void func_175773_a(GameRules p_175773_0_, String p_175773_1_) {
		if ("reducedDebugInfo".equals(p_175773_1_)) {
			byte b0 = (byte) (p_175773_0_.getBoolean(p_175773_1_) ? 22 : 23);

			for (EntityPlayerMP entityplayermp : MinecraftServer.getServer().getConfigurationManager().func_181057_v()) {
				entityplayermp.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(entityplayermp, b0));
			}
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, this.getGameRules().getRules());
		} else {
			if (args.length == 2) {
				GameRules gamerules = this.getGameRules();

				if (gamerules.areSameType(args[0], GameRules.ValueType.BOOLEAN_VALUE)) {
					return getListOfStringsMatchingLastWord(args, new String[] { "true", "false" });
				}
			}

			return null;
		}
	}

	/**
	 * Return the game rule set this command should be able to manipulate.
	 */
	private GameRules getGameRules() {
		return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
	}
}
