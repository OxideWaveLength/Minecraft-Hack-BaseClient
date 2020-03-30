package me.wavelength.baseclient.command;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import net.minecraft.client.Minecraft;

public abstract class Command extends EventListener {

	protected final String name;
	protected final String syntax;
	protected final String usage;
	protected final String[] aliases;

	protected final Minecraft mc;

	protected final CommandManager commandManager;

	public Command(String name, String syntax, String usage, String... aliases) {
		this.name = name;
		this.syntax = syntax;
		this.usage = usage;
		this.aliases = aliases;

		this.mc = Minecraft.getMinecraft();
		this.commandManager = BaseClient.instance.getCommandManager();
	}

	public String getName() {
		return name;
	}

	public String getSyntax() {
		return getSyntax("");
	}

	public String getSyntax(boolean trigger) {
		return getSyntax("", trigger);
	}

	public String getSyntax(String colored) {
		return getSyntax(colored, true);
	}

	public String getSyntax(String color, boolean trigger) {
		return color + (trigger ? commandManager.getTrigger() : "") + syntax;
	}

	public String getUsage() {
		return usage;
	}

	public String[] getAliases() {
		return aliases;
	}

	public abstract String executeCommand(String line, String[] args);

}