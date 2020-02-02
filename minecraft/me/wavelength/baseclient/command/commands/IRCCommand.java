package me.wavelength.baseclient.command.commands;

import java.io.IOException;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.Command;
import me.wavelength.baseclient.irc.IRCClient;

public class IRCCommand extends Command {

	private IRCClient ircClient;

	public IRCCommand(String name, String syntax, String usage, String... aliases) {
		super(name, syntax, usage, aliases);

		this.ircClient = BaseClient.instance.getIRCClient();
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length < 1) {
			return String.format("&c%1$s", getSyntax());
		}
		
		switch (args[0].toLowerCase()) {
		case "connect": {
			if (ircClient.isActive())

				return String.format("%1$s&cYou are already connected to the IRC.", ircClient.getPrefix());

			try {
				ircClient.start();
				ircClient.joinChannel(ircClient.getChannel());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return String.format("%1$s&e&oConnecting to the IRC...", ircClient.getPrefix());
		}
		case "disconnect": {
			if(!(ircClient.isActive()))
				return String.format("%1$s&cYou are not connected to the IRC.", ircClient.getPrefix());
			
			try {
				ircClient.quit("Client disconnected.", false);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return String.format("%1$s&a&oSuccesfully disconnected from the IRC.", ircClient.getPrefix());
		}
		case "status": {
			return String.format("%1$s&e&oYou are %2$sconnected&e&o.", ircClient.getPrefix(), (ircClient.isActive() ? "&a&o" : "&c&onot "));
		}
		}

		return null;
	}

}