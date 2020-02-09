package me.wavelength.baseclient.module.modules.hidden;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class TabGui extends Module {

	public TabGui() {
		super("TabGUI", "This is the TabGUI", Keyboard.KEY_RSHIFT, Category.HIDDEN);
	}

	@Override
	public void setup() {
		if (isToggled())
			BaseClient.instance.getEventManager().registerListener(BaseClient.instance.getTabGui());
	}

	@Override
	public void onEnable() {
		BaseClient.instance.getEventManager().registerListener(BaseClient.instance.getTabGui());
	}

	@Override
	public void onDisable() {
		BaseClient.instance.getEventManager().unregisterListener(BaseClient.instance.getTabGui());
	}

}