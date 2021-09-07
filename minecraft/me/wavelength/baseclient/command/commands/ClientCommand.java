package me.wavelength.baseclient.command.commands;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.Command;

public class ClientCommand extends Command {
	public ClientCommand() {
		super("client", "client <version|name> <value>", "Changes the client version/name");
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length < 2)
			return getSyntax("&c");

		switch (args[0].toLowerCase()) {
		case ("name"):
			BaseClient.instance.setClientName(args[1]);
			return String.format("&aThe client's name has been succesfully changed to &e%1$s&a.", args[1]);
		case ("version"):
			BaseClient.instance.setClientVersion(args[1]);
			return String.format("&aThe client's version has been succesfully changed to &e%1$s&a.", args[1]);
		default:
			return getSyntax("&c");
		}
	}
}
