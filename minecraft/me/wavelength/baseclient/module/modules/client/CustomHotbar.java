package me.wavelength.baseclient.module.modules.client;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class CustomHotbar extends Module {
	public CustomHotbar() {
		super("Custom Hotbar", "A custom hotbar, required for Hotbar Overlay", Keyboard.KEY_NONE, Category.CLIENT, true, false);
	}

	@Override
	public void setup() {
		this.color = me.wavelength.baseclient.module.Color.CLIENT;
	}
	
	public void onEnable() {
		BaseClient.instance.defaultHotbar = false;
	}
	
	public void onDisable() {
		BaseClient.instance.defaultHotbar = true;
	}
}