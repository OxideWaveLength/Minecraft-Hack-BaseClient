package me.wavelength.baseclient.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.Command;
import me.wavelength.baseclient.irc.IRCClient;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.ModuleManager;
import me.wavelength.baseclient.utils.Integers;

public class BindCommand extends Command {

	private IRCClient ircClient;

	public BindCommand() {
		super("bind", "bind <module> <key>", "Bind a module to a key");

		this.ircClient = BaseClient.instance.getIRCClient();
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length < 2) {
			return String.format("&c%1$s", getSyntax());
		}

		ModuleManager moduleManager = BaseClient.instance.getModuleManager();
		Module module = moduleManager.getModule(args[0]);

		if (module == null)
			return String.format("&cThe module &e%1$s&c does not exist.", args[0]);

		if (module.getCategory().equals(Category.HIDDEN))
			return String.format("&cThe module &e%1$s&c is hidden.", args[0]);

		int key = (Integers.isInteger(args[1]) ? Integers.getInteger(args[1]) : Keyboard.getKeyIndex(args[1]));

		List<Module> boundModules = new ArrayList<Module>(moduleManager.getModules(key));
		boundModules.forEach(mod -> mod.setKey(0));

		module.setKey(key);

		String keyName = args[1];
		
		try {
			keyName = Keyboard.getKeyName(key);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		
		return String.format("&aThe module &e%1$s&a has been bound to &d%2$s", module.getName(), keyName);
	}

}