package me.wavelength.baseclient.module;

import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.event.events.KeyPressedEvent;
import me.wavelength.baseclient.event.events.MouseClickEvent;
import me.wavelength.baseclient.module.modules.hidden.AdvancedTabGui;
import me.wavelength.baseclient.module.modules.movement.Fly;

public class ModuleManager extends EventListener {

	/** If you are wondering "why not lambda", it's pretty easy: thread safety. */

	private List<Module> modules;

	public ModuleManager() {
		this.modules = new ArrayList<Module>();
		BaseClient.instance.getEventManager().registerListener(this);

		registerModules();
	}

	public void registerModule(Module module) {
		modules.add(module);
	}

	public void registerModules() {
		registerModule(new Fly());
		registerModule(new AdvancedTabGui());
	}

	public Module getModule(Class<? extends Module> clasz) {
		for (int i = 0; i < modules.size(); i++) {
			if (modules.get(i).getClass().equals(clasz))
				return modules.get(i);
		}

		return null;
	}

	public Module getModule(String name) {
		for (int i = 0; i < modules.size(); i++) {
			if (modules.get(i).getName().equalsIgnoreCase(name))
				return modules.get(i);
		}

		return null;
	}

	public List<Module> getModules() {
		return modules;
	}

	public List<Module> getToggledModules() {
		List<Module> modules = new ArrayList<Module>();
		for (int i = 0; i < this.modules.size(); i++) {
			Module module = this.modules.get(i);
			if (module.isToggled())
				modules.add(module);
		}

		return modules;
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		for (int i = 0; i < modules.size(); i++) {
			Module module = modules.get(i);
			if (module.getKey() == -100 + event.getButton())
				module.toggle();
		}
	}

	@Override
	public void onKeyPressed(KeyPressedEvent event) {
		for (int i = 0; i < modules.size(); i++) {
			Module module = modules.get(i);
			if (module.getKey() == event.getKey())
				module.toggle();
		}
	}

	public List<Module> getModules(Category category) {
		List<Module> modules = new ArrayList<Module>();
		for (int i = 0; i < this.modules.size(); i++) {
			Module module = this.modules.get(i);
			if (module.getCategory().equals(category))
				modules.add(module);
		}

		return modules;
	}

}