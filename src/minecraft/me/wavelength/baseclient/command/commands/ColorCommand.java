package me.wavelength.baseclient.command.commands;

import java.awt.Color;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.Command;
import me.wavelength.baseclient.utils.Config;
import me.wavelength.baseclient.utils.Integers;
import me.wavelength.baseclient.utils.Lists;
import me.wavelength.baseclient.utils.Random;
import me.wavelength.baseclient.utils.Strings;

public class ColorCommand extends Command {

	public ColorCommand() {
		super("color", "color <color|colors|reset>", "Set the TabGUI color.");
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length < 1)
			return getSyntax("&c");

		Config genericConfig = BaseClient.instance.getGenericConfig();

		Color color = null;

		switch (args[0].toLowerCase()) {
		case "colors": {
			String colors = Lists.stringArrayToString("&f, ", "&cRED", "&1BLUE", "&3CYAN", "&2GREEN", "&dMAGENTA", "&6ORAGNE", "&eYELLOW", "&5PINK", "&k|&fRANDOM&k|");

			return String.format("&eThis is the color list:\n%1$s", colors);
		}
		case "random": {
			color = Random.getRandomLightColor();
			break;
		}
		case "reset": {
			break;
		}
		default: {
			if (Integers.isInteger(args[0])) {
				color = new Color(Integers.getInteger(args[0]));
			} else {
				color = Strings.getColor(args[0]);
			}
			break;
		}
		}

		if (color == null)
			return String.format("&cThe color &e%1$s&c does not exist.", args[0].toUpperCase());

		genericConfig.set("tabguicolor", color.getRGB());

		return String.format("&aThe TabGUI color has been succesfully changed to &e%1$s&a.", args[0].toUpperCase());
	}

}