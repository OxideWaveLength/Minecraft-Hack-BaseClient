package me.wavelength.baseclient.module.modules.client;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class TabGui extends Module {
	public TabGui() {
		super("TabGui", "This is the TabGui", Keyboard.KEY_NONE, Category.CLIENT, true);
	}

	@Override
	public void setup() {
		this.color = me.wavelength.baseclient.module.Color.CLIENT;
		moduleSettings.addDefault("rainbow", true);
		moduleSettings.addDefault("speed", 20);
		moduleSettings.addDefault("offset", 1);
		moduleSettings.addDefault("gradient", true);
	}
}