package me.wavelength.baseclient.module.modules.semi_hidden;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class TabGui extends Module {

	public TabGui() {
		super("TabGUI", "This is the TabGUI", Keyboard.KEY_RSHIFT, Category.SEMI_HIDDEN, false, true);
	}

}