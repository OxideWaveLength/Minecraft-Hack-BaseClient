package me.wavelength.baseclient.command.commands;

import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.command.Command;

public class FriendsCommand extends Command {

	private List<String> friends;

	public FriendsCommand() {
		super("friends", "friends <add|remove|list> [name]", "Manage friends", "f", "friend");
		friends = new ArrayList<String>();
	}

	@Override
	public String executeCommand(String line, String[] args) {
		switch (args[0].toLowerCase()) {
		case "add": {
			String name = args[1];

			if (name == null)
				return getSyntax();

			if (friends.contains(name))
				return String.format("&e%1$s&c is already in your friends list.", name);

			friends.add(name);
			return String.format("&e%1$s &ahas been added to your friends list.", name);
		}
		case "remove": {
			String name = args[1];

			if (name == null)
				return getSyntax();

			if (!(friends.contains(name)))
				return String.format("&e%1$s&c is NOT in your friends list.", name);

			friends.remove(name);
			return String.format("&e%1$s &ahas been removed from your friends list.", name);
		}
		case "list": {
			if (friends.size() == 0)
				return "&eYou don't have any friend :(";
			String friendList = "";
			for (int i = 0; i < friends.size(); i++) {
				friendList += (i == 0 ? "" : "&f, ") + "&e" + friends.get(i);
			}
			return String.format("&aThese are your friends: %s", friendList);
		}
		case "clear": {
			friends.clear();
			return "&aYour friends list has been cleared.";
		}
		default: {
			return getSyntax();
		}
		}
	}

	public String getName(String[] args) {
		if (args.length < 2)
			return null;

		return args[1];
	}

	public List<String> getFriends() {
		return friends;
	}

	public boolean isFriend(String name) {
		return friends.contains(name);
	}

}