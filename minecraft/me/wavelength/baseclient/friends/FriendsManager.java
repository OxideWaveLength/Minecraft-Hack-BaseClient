package me.wavelength.baseclient.friends;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.utils.Config;
import me.wavelength.baseclient.utils.Files;
import me.wavelength.baseclient.utils.Lists;
import me.wavelength.baseclient.utils.Strings;

public class FriendsManager {

	private List<String> friends;
	private List<String> enemies;

	private Config friendsConfig;
	private Config enemiesConfig;

	public FriendsManager() {
		this.friends = new ArrayList<String>();
		this.enemies = new ArrayList<String>();

		String clientFolder = new File(".").getAbsolutePath();

		clientFolder = (clientFolder.contains("jars") ? new File(".").getAbsolutePath().substring(0, clientFolder.length() - 2) : new File(".").getAbsolutePath()) + Strings.getSplitter() + BaseClient.instance.getClientName();

		this.friendsConfig = new Config(clientFolder + "/friends.cfg");
		this.enemiesConfig = new Config(clientFolder + "/enemies.cfg");

		try {
			Files.create(friendsConfig.getFile());
			Files.create(enemiesConfig.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

		friendsConfig.addDefault("friends", "");
		enemiesConfig.addDefault("enemies", "");

		this.friends = friendsConfig.getStringList("friends", null);
		this.enemies = enemiesConfig.getStringList("enemies", null);
	}

	public List<String> getFriends() {
		return friends;
	}

	public List<String> getEnemies() {
		return enemies;
	}

	public void addFriend(String playerName) {
		if (!(isFriend(playerName))) {
			friends.add(playerName);
			friendsConfig.set("friends", friends);
		}
	}

	public void removeFriend(String playerName) {
		if (isFriend(playerName)) {
			friends.remove(playerName);
			friendsConfig.set("friends", friends);
		}
	}

	public void addEnemy(String playerName) {
		if (!(isEnemy(playerName))) {
			enemies.add(playerName);
			friendsConfig.set("enemies", enemies);
		}
	}

	public void removeEnemy(String playerName) {
		if (isEnemy(playerName)) {
			enemies.add(playerName);
			friendsConfig.set("enemies", enemies);
		}
	}

	public boolean isFriend(String playerName) {
		return friends.stream().anyMatch(playerName::equalsIgnoreCase);
	}

	public boolean isEnemy(String playerName) {
		return enemies.stream().anyMatch(playerName::equalsIgnoreCase);
	}

	public void clear() {
		friends.clear();
		enemies.clear();
		friendsConfig.set("friends", friends);
		friendsConfig.set("enemies", enemies);
	}

}
