package me.wavelength.baseclient.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.commands.*;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.KeyPressedEvent;
import me.wavelength.baseclient.event.events.MessageSentEvent;
import me.wavelength.baseclient.irc.IRCClient;
import me.wavelength.baseclient.utils.Lists;
import me.wavelength.baseclient.utils.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

public class CommandManager extends EventListener {

	private List<Command> commands;

	private String trigger;

	private Minecraft mc;

	public CommandManager(String trigger) {
		this.commands = new ArrayList<Command>();

		this.trigger = trigger;

		this.mc = Minecraft.getMinecraft();

		BaseClient.instance.getEventManager().registerEvent(this);
		registerCommands();
	}

	public String getTrigger() {
		return trigger;
	}

	public void registerCommand(Command command) {
		commands.add(command);
	}

	public void registerCommands() {
		registerCommand(new IRCCommand("irc", "irc <connect|disconnect|status>", "Connects to the IRC Server."));
		registerCommand(new SetCommand("set", "set <module> <key> <value>", "Sets something for the module."));
	}

	@Override
	public void onMessageSent(MessageSentEvent event) {
		String[] args = event.getMessage().split(" ");

		if (event.isCancelled())
			return;

		String line = Lists.stringArrayToString(" ", args);
		String commandLine = Lists.stringArrayToString(" ", args);
		String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

		if (!(args[0].startsWith(trigger))) {
			if (args[0].startsWith("@")) {
				IRCClient ircClient = BaseClient.instance.getIRCClient();
				if (ircClient != null && ircClient.isActive()) {
					try {
						ircClient.sendMessage(ircClient.getChannel(), commandLine.substring(1));
						Player.sendMessage(String.format("%1$s&6YOU &7(&e&o%2$s&7)&7: &e%3$s", ircClient.getPrefix(), ircClient.getUsername(), commandLine.substring(1)));
					} catch (IOException e) {
						e.printStackTrace();
						Player.sendMessage(String.format("%1$sCan't send the chat message. (%2$s)", ircClient.getPrefix(), e.getMessage()));
					}
					event.setCancelled(true);
				}
			}
			return;
		}

		String name = args[0].substring(1, args[0].length());

		event.setCancelled(true);

		for (int i = 0; i < commands.size(); i++) {
			Command command = commands.get(i);
			if (!(command.getName().equalsIgnoreCase(name)) && !(Arrays.stream(command.getAliases()).anyMatch(name::equalsIgnoreCase)))
				continue;

			String result = command.executeCommand(line, commandArgs);

			if (result != null && !(result.trim().isEmpty()))
				Player.sendMessage(result);
		}

	}

	/** This will open the chat with the trigger when the period key is pressed. */
	@Override
	public void onKeyPressed(KeyPressedEvent event) {
		if (event.getKey() == Keyboard.KEY_PERIOD) {
			mc.displayGuiScreen(new GuiChat(trigger));
		} else if (event.getKey() == Keyboard.KEY_AT) {
			mc.displayGuiScreen(new GuiChat("@"));
		}
	}

}