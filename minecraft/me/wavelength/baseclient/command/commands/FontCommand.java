package me.wavelength.baseclient.command.commands;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.Command;
import me.wavelength.baseclient.utils.Integers;

public class FontCommand extends Command {

	public FontCommand() {
		super("font", "font <size>", "Set the font's size");
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (args.length == 0 || !(Integers.isInteger(args[0])))
			return getSyntax("&c");

		int size = Integers.getInteger(args[0]);
		if (size < 13 || size > 45)
			return "&cPlease, type a number within 13 and 45.";

		BaseClient.instance.getFontRenderer().setFontSizeNormal(size);

		return String.format("&aThe font size has been set to &e%1$d&a.", size);
	}

}