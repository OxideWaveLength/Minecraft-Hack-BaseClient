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
		return getSyntax(true);
	}

	public String getSyntax(boolean trigger) {
		return (trigger ? String.format("%1$s%2$s", BaseClient.instance.getCommandManager().getTrigger(), syntax) : String.format("%1$s", syntax));
	}

	public String getUsage() {
		return usage;
	}

	public String[] getAliases() {
		return aliases;
	}

	public abstract String executeCommand(String line, String[] args);

}