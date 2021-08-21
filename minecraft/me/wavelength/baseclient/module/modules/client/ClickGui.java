package me.wavelength.baseclient.module.modules.client;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class ClickGui extends Module {

	public ClickGui() {
		super("ClickGui", "This is the ClickGui", Keyboard.KEY_RSHIFT, Category.CLIENT, true);
	}

	@Override
	public void setup() {
		this.color = me.wavelength.baseclient.module.Color.CLIENT;
		moduleSettings.addDefault("rainbow", true);
		moduleSettings.addDefault("offset", 1);
		moduleSettings.addDefault("speed", 20);
		moduleSettings.addDefault("spacing", 5);
		moduleSettings.addDefault("scroll", 5);
	}

	@Override
	public void onEnable() {
		mc.displayGuiScreen(BaseClient.instance.getClickGui());
		super.toggle();
	}

}