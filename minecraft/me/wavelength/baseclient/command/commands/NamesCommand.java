package me.wavelength.baseclient.command.commands;

import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.command.Command;

public class NamesCommand extends Command {

	private List<String> exceptions;

	public NamesCommand() {
		super("names", "names <add|remove|list> [name]", "Manage the name protect exceptions list");
		exceptions = new ArrayList<String>();
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length < 1)
			return getSyntax("&c");
		
		switch (args[0].toLowerCase()) {
		case "add": {
			String name = args[1];

			if (name == null)
				return getSyntax("&c");

			if (exceptions.contains(name))
				return String.format("&e%1$s&c is already in the exceptions.", name);

			exceptions.add(name);
			return String.format("&e%1$s &ahas been added to the exceptions.", name);
		}
		case "remove": {
			String name = args[1];

			if (name == null)
				return getSyntax("&c");

			if (!(exceptions.contains(name)))
				return String.format("&e%1$s&c is NOT in the exceptions.", name);

			exceptions.remove(name);
			return String.format("&e%1$s &ahas been removed from the exceptions.", name);
		}
		case "list": {
			if (exceptions.size() == 0)
				return "&eYou don't have any friend :(";
			String friendList = "";
			for (int i = 0; i < exceptions.size(); i++) {
				friendList += (i == 0 ? "" : "&f, ") + "&e" + exceptions.get(i);
			}
			return String.format("&aThese are the exceptions: %s", friendList);
		}
		case "clear": {
			exceptions.clear();
			return "&aThe exceptions have been cleared.";
		}
		default: {
			return getSyntax("&c");
		}
		}
	}

	public String getName(String[] args) {
		if (args.length < 2)
			return null;

		return args[1];
	}

	public List<String> getExceptions() {
		return exceptions;
	}

	public boolean isInExceptions(String name) {
		return exceptions.contains(name);
	}

}