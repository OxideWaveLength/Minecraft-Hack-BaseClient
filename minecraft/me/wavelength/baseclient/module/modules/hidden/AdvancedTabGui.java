package me.wavelength.baseclient.module.modules.hidden;

import org.lwjgl.input.Mouse;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.MouseScrollEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class AdvancedTabGui extends Module {

	/**
	 * -98 is the CUSTOM mouse wheel click code
	 */
	public AdvancedTabGui() {
		super("AdvancedTabGui", "Interact with the TabGui in an advanced way", -3, Category.SEMI_HIDDEN);
	}

	@Override
	public void onUpdate(UpdateEvent event) {
	}

}