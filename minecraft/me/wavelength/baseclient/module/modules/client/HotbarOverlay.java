package me.wavelength.baseclient.module.modules.client;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Color;

public class HotbarOverlay extends me.wavelength.baseclient.module.Module {
	public HotbarOverlay() {
		super("HotbarOverlay", "Custom hotbar", Keyboard.KEY_NONE, Category.CLIENT, true, true);
	}
	
	@Override
	public void setup() {
		this.color = Color.CLIENT;
		
		moduleSettings.addDefault("fps", true);
		moduleSettings.addDefault("time", true);
	}
}
