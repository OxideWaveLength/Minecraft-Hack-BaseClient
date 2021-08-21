package me.wavelength.baseclient.command.commands;

import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.command.Command;
import me.wavelength.baseclient.friends.FriendsManager;

public class FriendsCommand extends Command {

	private FriendsManager friendsManager;

	public FriendsCommand(FriendsManager friendsManager) {
		super("friends", "friends <add|remove|list> [name]", "Manage friends", "f", "friend");

		this.friendsManager = friendsManager;
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length < 1)
			return getSyntax("&c");

		switch (args[0].toLowerCase()) {
		case "add": {
			String name = getName(args);

			if (name == null)
				return getSyntax("&c");

			if (friendsManager.isFriend(name))
				return String.format("&e%1$s&c is already in your friends list.", name);

			friendsManager.addFriend(name);
			return String.format("&e%1$s &ahas been added to your friends list.", name);
		}
		case "remove": {
			String name = getName(args);

			if (name == null)
				return getSyntax("&c");

			if (!(friendsManager.isFriend(name)))
				return String.format("&e%1$s&c is NOT in your friends list.", name);

			friendsManager.removeFriend(name);
			return String.format("&e%1$s &ahas been removed from your friends list.", name);
		}
		case "list": {
			List<String> friends = new ArrayList<String>(friendsManager.getFriends());

			if (friends.size() == 0)
				return "&eYou don't have any friend :(";

			String friendList = "";
			for (int i = 0; i < friends.size(); i++) {
				friendList += (i == 0 ? "" : "&f, ") + "&e" + friends.get(i);
			}
			return String.format("&aThese are your friends: %s", friendList);
		}
		case "clear": {
			friendsManager.clear();
			return "&aYour friends list has been cleared.";
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

}