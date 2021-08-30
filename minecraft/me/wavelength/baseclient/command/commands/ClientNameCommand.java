package me.wavelength.baseclient.command.commands;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.Command;

public class ClientNameCommand extends Command {
	public ClientNameCommand() {
		super("clientname", "clientname <name>", "Changes the client name");
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length < 1)
			return getSyntax("&c");

		BaseClient.instance.setClientName(args[0]);

		return String.format("&aThe client's name has been succesfully changed to &e%1$s&a.", args[0]);
	}
}
