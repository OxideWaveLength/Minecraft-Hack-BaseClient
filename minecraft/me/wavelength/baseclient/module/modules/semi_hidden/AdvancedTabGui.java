package me.wavelength.baseclient.module.modules.semi_hidden;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.modules.client.TabGui;

public class AdvancedTabGui extends Module {

	/**
	 * -98 is the CUSTOM mouse wheel click code
	 */
	public AdvancedTabGui() {
		super("AdvancedTabGui", "Interact with the TabGui in an advanced way", Keyboard.KEY_NONE, Category.SEMI_HIDDEN);
	}

	@Override
	public void onUpdate(UpdateEvent event) {
		if (!(BaseClient.instance.getModuleManager().getModule(TabGui.class).isToggled()))
			toggle();
	}

}