package me.wavelength.baseclient.command.commands;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.command.Command;
import me.wavelength.baseclient.utils.Integers;

public class VClipCommand extends Command {

	public VClipCommand() {
		super("vclip", "vclip <distance>", "Set the font's size", "vc");
	}

	@Override
	public String executeCommand(String line, String[] args) {
		if (!(Integers.isInteger(args[0])))
			return getSyntax("&c");

		int distance = Integers.getInteger(args[0]);

		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + distance, mc.thePlayer.posZ);
		
		return String.format("&aYou have VClipped &e%1$d blocks&a.", distance);
		
	}

}