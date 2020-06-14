package me.wavelength.baseclient.command.commands;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.Command;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class ToggleCommand extends Command {

	public ToggleCommand() {
		super("toggle", "toggle <module>", "Toggle a module.", "t");
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length < 1)
			return getSyntax("&c");

		Module module = BaseClient.instance.getModuleManager().getModule(args[0]);

		if (module == null)
			return String.format("&cThe module &e%s&c does not exist.", args[0]);

		if (module.getCategory().equals(Category.HIDDEN))
			return String.format("&cThe module &e%s&c is hidden, and can't be toggled.", module.getName());

		module.toggle();

		return String.format("&aThe module &e%s&a has been toggled to &e%s&a.", module.getName(), Boolean.toString(module.isToggled()));
	}

}