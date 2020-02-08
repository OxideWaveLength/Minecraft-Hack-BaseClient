package me.wavelength.baseclient.irc;

import me.wavelength.baseclient.utils.Player;

public class IRCClient extends me.wavelength.baseclient.utils.IRCClient {

	private String channel;

	private String prefix;

	public IRCClient(String host, int port, String username, String channel) {
		super(host, port, username);

		this.channel = channel;
		this.prefix = "&dIRC&7> ";
	}

	public String getChannel() {
		return channel;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public void listener(String line) {
		if (!(isActive()))
			return;

		if (!(line.contains("!")) || !(line.contains(":")))
			return;

		String name = line.substring(1, line.indexOf("!"));
		String message = line.substring(line.lastIndexOf(":") + 1, line.length());

		Player.sendMessage(String.format("%1$s&b%2$s&7: &e%3$s", prefix, name, message), false);
	}

	@Override
	public void onConnect() {
	}

	@Override
	public void exception(Exception e) {
	}

}