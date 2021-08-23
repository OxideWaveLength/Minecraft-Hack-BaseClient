package me.wavelength.baseclient.module.modules.client;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class TabGui extends Module {

	public TabGui() {
		super("Tab Gui", "This is the TabGui", Keyboard.KEY_NONE, Category.CLIENT, true, true);
	}

	@Override
	public void setup() {
		this.color = me.wavelength.baseclient.module.Color.CLIENT;
	}
}