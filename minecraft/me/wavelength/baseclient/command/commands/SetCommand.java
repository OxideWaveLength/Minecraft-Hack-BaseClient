package me.wavelength.baseclient.command.commands;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.Command;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.ModuleSettings;
import me.wavelength.baseclient.utils.Files;
import me.wavelength.baseclient.utils.Integers;
import me.wavelength.baseclient.utils.Player;
import me.wavelength.baseclient.utils.Strings;

public class SetCommand extends Command {

	public SetCommand() {
		super("set", "set <module> <key> <value>", "Sets something for the module.");
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length < 1)
			return getSyntax("&c");

		Module module = BaseClient.instance.getModuleManager().getModule(args[0]);
		if (module == null)
			return String.format("&cThe module &e%1$s&c does not exist.", args[0]);

		if (module.getCategory().equals(Category.HIDDEN))
			return String.format("&cThe module &e%1$s&c is hidden.", args[0]);

		ModuleSettings moduleSettings = module.getModuleSettings();

		if (args.length == 1) {
			try {
				StringBuilder values = new StringBuilder("&e");
				for (int i = 0; i < Files.read(moduleSettings.getConfig().getFile()).size(); i++) {
					String value = Files.read(moduleSettings.getConfig().getFile()).get(i);
					values.append("&e" + (i == 0 ? "" : "\n&e") + value);
				}
				Player.sendMessage(values.toString());
			} catch (Exception e) {
				e.printStackTrace();
				Player.sendMessage("&cUnexpected error.");
			}
		}

		if (args.length == 2) {
			if (module.getModuleSettings().getString(args[1]) != null)
				return String.format("&e%1$s&a's &e%2$s&a is set to &e%3$s", module.getName(), args[1], moduleSettings.getString(args[1]));
			else
				return String.format("&cThe key &e%1$s&c does not exist.", args[1]);
		}

		if (args.length > 2) {
			Object setting = moduleSettings.getObject(args[1]);
			if (setting == null) {
				return String.format("&cThe key &e%1$s&c does not exist.", args[1]);
			}
			if (setting instanceof Boolean) {
				if (!(Strings.isBoolean(args[2]))) {
					return String.format("&cThe key &e%1$s&c &c expects a boolean value (true/false or 1/0).", args[1]);
				}
				moduleSettings.set(args[1], Strings.getBooleanValue(args[2]));
			} else if (setting instanceof String) {
				moduleSettings.set(args[1], args[2]);
			} else if (setting instanceof Integer || setting instanceof Float || setting instanceof Double) {
				if (!(Integers.isInteger(args[2])) && !(Integers.isDouble(args[2]))) {
					return String.format("&cThe key &e%1$s&c expects an integer/double value (a number).", args[1]);
				}

				moduleSettings.set(args[1], Double.parseDouble(args[2]));
			}
			return String.format("&aSet &e%1$s&a to &e%2$s&a for the module &e" + module.getName(), args[1], args[2]);
		}

		return getSyntax("&c");
	}

}