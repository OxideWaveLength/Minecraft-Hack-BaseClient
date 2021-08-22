package me.wavelength.baseclient.command.commands;

import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.Command;
import me.wavelength.baseclient.module.modules.render.XRay;
import me.wavelength.baseclient.utils.Lists;

public class XRayCommand extends Command {

	public XRayCommand() {
		super("xray", "xray <add|remove|clear|list> [name|id]", "Add or remove a block from the xray list");
	}

	@Override
	public String executeCommand(String line, String[] args) {
		XRay xray = (XRay) BaseClient.instance.getModuleManager().getModule(XRay.class);

		List<String> exceptions = new ArrayList<String>(xray.getExceptions());

		if (args.length < 2) {
			if (args.length < 1)
				return getSyntax("&c");

			switch (args[0].toLowerCase()) {
			case "list": {
				String blocks = Lists.listStringToString(exceptions, "&f, &e");
				return String.format("&aBlocks: &e%s", (blocks == null || blocks.isEmpty() ? "&cNone" : blocks));
			}
			case "clear": {
				exceptions.clear();
				xray.setExceptions(exceptions);
				return "&aThe exceptions list has been cleared";
			}
			default: {
				return getSyntax("&c");
			}
			}
		}

		String block = args[1].toUpperCase();

		switch (args[0].toLowerCase()) {
		case "add": {
			if (exceptions.contains(block))
				return String.format("&cThe block &e%s&c is already in the list", block);

			exceptions.add(block);
			xray.setExceptions(exceptions);
			return String.format("&aThe block &e%s&a has been added to the list", block);
		}
		case "remove": {
			if (!(exceptions.contains(block))) {
				return String.format("&cThe block &e%s&c is not in the list", block);
			}
			exceptions.remove(block);
			xray.setExceptions(exceptions);
			return String.format("&aThe block &e%s&a has been removed from the list", block);
		}
		default: {
			return getSyntax("&c");
		}
		}
	}

}