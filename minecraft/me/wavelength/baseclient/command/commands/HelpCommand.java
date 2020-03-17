package me.wavelength.baseclient.command.commands;

import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.command.Command;
import me.wavelength.baseclient.utils.Strings;

public class HelpCommand extends Command {

	public HelpCommand() {
		super("help", "help [module]", "Returns all the commands or a command description.");
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length < 1) {
			List<Command> commands = new ArrayList<Command>(commandManager.getCommands());

			String help = "";
			for (int i = 0; i < commands.size(); i++) {
				Command command = commands.get(i);
				help += String.format("%1$s&d%2$s &7-&e %3$s", (i == 0 ? "" : "\n"), command.getName(), command.getUsage());
			}

			int maxWidth = Strings.getMaxChars(help.split("\n"));

			String spaces = String.format("&7%1$s&f", Strings.multiplyString(" ", maxWidth / 4));
			String dashes = String.format("&f&7&m%1$s&f", Strings.multiplyString("-", maxWidth / 6));
			help = (spaces + dashes + " &5Help " + dashes) + "\n" + help;

			return help;
		}

		Command command = commandManager.getCommand(args[0]);

		if (command == null)
			return commandManager.getHelpMessage(args[0]);

		return String.format("&d%1$s &7-&e %2$s", command.getName(), command.getSyntax(false));
	}

}