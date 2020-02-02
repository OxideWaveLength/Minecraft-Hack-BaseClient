package me.wavelength.baseclient.module;

import java.awt.Color;
import java.util.Arrays;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.utils.Random;
import me.wavelength.baseclient.utils.Strings;

public class Module extends EventListener {

	protected String name;
	protected String description;
	protected int key;
	protected Category category;
	protected AntiCheat[] allowedAntiCheats;

	protected AntiCheat antiCheat;

	protected ModuleSettings moduleSettings;

	protected boolean toggled;

	private Color color;

	public Module(String name, String description, int key, Category category, AntiCheat... allowedAntiCheats) {
		this.name = name;
		this.description = description;
		this.key = key;
		this.category = category;
		allowedAntiCheats = (allowedAntiCheats == null || allowedAntiCheats.length == 0 ? new AntiCheat[] { AntiCheat.VANILLA } : allowedAntiCheats);

		this.allowedAntiCheats = allowedAntiCheats;
		this.antiCheat = allowedAntiCheats[0];

		this.moduleSettings = new ModuleSettings(this);
		
		setup();
		loadFromSettings();
	}

	private void loadFromSettings() {
		this.toggled = moduleSettings.getBoolean("toggled");
		this.key = moduleSettings.getInt("key");
		this.antiCheat = AntiCheat.valueOf(moduleSettings.getString("anticheat").toUpperCase());
		this.antiCheat = (Arrays.stream(allowedAntiCheats).anyMatch(antiCheat::equals) ? antiCheat : allowedAntiCheats[0]);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getKey() {
		return key;
	}

	public Category getCategory() {
		return category;
	}

	public AntiCheat getAntiCheat() {
		return antiCheat;
	}

	public AntiCheat[] getAllowedAntiCheats() {
		return allowedAntiCheats;
	}

	public ModuleSettings getModuleSettings() {
		return moduleSettings;
	}

	public boolean isToggled() {
		return toggled;
	}

	public Color getColor() {
		return color;
	}

	public String getNameWithAntiCheat() {
		return name + (antiCheat.equals(AntiCheat.VANILLA) ? "" : " &7-&f " + (antiCheat.isCapital() ? antiCheat.name() : Strings.capitalizeOnlyFirstLetter(antiCheat.name())));
	}

	public void setup() {

	}

	public void onEnable() {

	}

	public void onDisable() {

	}

	public void toggle() {
		setToggled(!(toggled));
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		if (toggled) {
			this.color = Random.getRandomLightColor();
			moduleSettings.set("toggled", true);
			BaseClient.instance.getEventManager().registerListener(this);
			onEnable();
		} else {
			moduleSettings.set("toggled", false);
			BaseClient.instance.getEventManager().unregisterListener(this);
			onDisable();
		}
	}

}