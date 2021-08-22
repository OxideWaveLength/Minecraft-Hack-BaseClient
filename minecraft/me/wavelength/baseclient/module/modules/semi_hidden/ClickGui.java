package me.wavelength.baseclient.module.modules.semi_hidden;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class ClickGui extends Module {

	public ClickGui() {
		super("ClickGui", "This is the ClickGui", Keyboard.KEY_RSHIFT, Category.SEMI_HIDDEN, false);
	}

	@Override
	public void onEnable() {
		mc.displayGuiScreen(BaseClient.instance.getClickGui());
		super.toggle();
	}

}