package me.wavelength.baseclient.command;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import net.minecraft.client.Minecraft;

public abstract class Command extends EventListener {

	private String name;
	private String usage;
	private String description;
	private String[] aliases;

	protected Minecraft mc;

	public Command(String name, String usage, String description, String... aliases) {
		this.name = name;
		this.usage = usage;
		this.description = description;
		this.aliases = aliases;

		this.mc = Minecraft.getMinecraft();
	}

	public String getName() {
		return name;
	}

	public String getUsage() {
		return getUsage(true);
	}

	public String getUsage(boolean trigger) {
		return (trigger ? String.format("%1$s%2$s", BaseClient.instance.getCommandManager().getTrigger(), usage) : String.format("%1$s", usage));
	}

	public String getDescription() {
		return description;
	}

	public String[] getAliases() {
		return aliases;
	}

	public abstract String executeCommand(String line, String[] args);

}