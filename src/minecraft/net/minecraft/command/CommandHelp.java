package net.minecraft.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class CommandHelp extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "help";
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
		return "commands.help.usage";
	}

	public List<String> getCommandAliases() {
		return Arrays.<String>asList(new String[] { "?" });
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		List<ICommand> list = this.getSortedPossibleCommands(sender);
		int i = 7;
		int j = (list.size() - 1) / 7;
		int k = 0;

		try {
			k = args.length == 0 ? 0 : parseInt(args[0], 1, j + 1) - 1;
		} catch (NumberInvalidException numberinvalidexception) {
			Map<String, ICommand> map = this.getCommands();
			ICommand icommand = (ICommand) map.get(args[0]);

			if (icommand != null) {
				throw new WrongUsageException(icommand.getCommandUsage(sender), new Object[0]);
			}

			if (MathHelper.parseIntWithDefault(args[0], -1) != -1) {
				throw numberinvalidexception;
			}

			throw new CommandNotFoundException();
		}

		int l = Math.min((k + 1) * 7, list.size());
		ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.help.header", new Object[] { Integer.valueOf(k + 1), Integer.valueOf(j + 1) });
		chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
		sender.addChatMessage(chatcomponenttranslation1);

		for (int i1 = k * 7; i1 < l; ++i1) {
			ICommand icommand1 = (ICommand) list.get(i1);
			ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation(icommand1.getCommandUsage(sender), new Object[0]);
			chatcomponenttranslation.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + icommand1.getCommandName() + " "));
			sender.addChatMessage(chatcomponenttranslation);
		}

		if (k == 0 && sender instanceof EntityPlayer) {
			ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.help.footer", new Object[0]);
			chatcomponenttranslation2.getChatStyle().setColor(EnumChatFormatting.GREEN);
			sender.addChatMessage(chatcomponenttranslation2);
		}
	}

	protected List<ICommand> getSortedPossibleCommands(ICommandSender p_71534_1_) {
		List<ICommand> list = MinecraftServer.getServer().getCommandManager().getPossibleCommands(p_71534_1_);
		Collections.sort(list);
		return list;
	}

	protected Map<String, ICommand> getCommands() {
		return MinecraftServer.getServer().getCommandManager().getCommands();
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			Set<String> set = this.getCommands().keySet();
			return getListOfStringsMatchingLastWord(args, (String[]) set.toArray(new String[set.size()]));
		} else {
			return null;
		}
	}
}
