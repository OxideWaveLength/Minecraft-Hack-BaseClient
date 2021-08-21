package me.wavelength.baseclient.module;

import java.awt.Color;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.EventListener;
import me.wavelength.baseclient.utils.Random;
import me.wavelength.baseclient.utils.Strings;
import me.wavelength.baseclient.utils.Timer;

public class Module extends EventListener {

	protected String name;
	protected String description;
	protected int key;
	protected Category category;
	protected AntiCheat[] allowedAntiCheats;

	protected AntiCheat antiCheat;

	protected ModuleSettings moduleSettings;

	private boolean toggled;

	private boolean showInModuleArraylist;

	protected Color color;

	protected Timer timer;

	protected ExecutorService singleExecutorService;
	protected ExecutorService executorService;

	public Module(String name, String description, int key, Category category, AntiCheat... allowedAntiCheats) {
		initializeModule(name, description, key, category, (category.equals(Category.HIDDEN) ? false : true), false, allowedAntiCheats);
	}

	public Module(String name, String description, int key, Category category, boolean showInModuleArrayList, AntiCheat... allowedAntiCheats) {
		initializeModule(name, description, key, category, showInModuleArrayList, false, allowedAntiCheats);
	}

	public Module(String name, String description, int key, Category category, boolean showInModuleArrayList, boolean toggled, AntiCheat... allowedAntiCheats) {
		initializeModule(name, description, key, category, showInModuleArrayList, toggled, allowedAntiCheats);
	}

	private void initializeModule(String name, String description, int key, Category category, boolean showInModuleArrayList, boolean toggled, AntiCheat... allowedAntiCheats) {
		this.name = name;
		this.description = description;
		this.key = key;
		this.category = category;
		allowedAntiCheats = (allowedAntiCheats == null || allowedAntiCheats.length == 0 ? new AntiCheat[] { AntiCheat.VANILLA } : allowedAntiCheats);

		this.allowedAntiCheats = allowedAntiCheats;
		this.antiCheat = allowedAntiCheats[0];

		this.showInModuleArraylist = showInModuleArrayList;

		this.moduleSettings = new ModuleSettings(this);

		if (!(moduleSettings.wasGenerated()))
			this.toggled = toggled;

		this.timer = new Timer(true);
		this.singleExecutorService = Executors.newFixedThreadPool(1);
		this.executorService = Executors.newCachedThreadPool();

		loadFromSettings();
		setup();
		randomColor();
	}

	private void loadFromSettings() {
		if (moduleSettings.getBoolean("toggled")) {
			toggled = true;
			BaseClient.instance.getEventManager().registerListener(this);
		}

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

	public void setKey(int key) {
		this.key = key;
		moduleSettings.set("key", key);
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

	public boolean isShownInModuleArrayList() {
		return showInModuleArraylist;
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

	private void randomColor() {
		this.color = Random.getRandomLightColor();
	}

	public Timer getTimer() {
		return timer;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		if (toggled) {
			randomColor();
			moduleSettings.set("toggled", true);
			BaseClient.instance.getEventManager().registerListener(this);
			onEnable();
		} else {
			moduleSettings.set("toggled", false);
			BaseClient.instance.getEventManager().unregisterListener(this);
			onDisable();
		}
	}

	public void setAntiCheat(AntiCheat antiCheat) {
		if (!(Arrays.stream(allowedAntiCheats).anyMatch(antiCheat::equals)))
			return;

		this.antiCheat = antiCheat;
		moduleSettings.set("anticheat", antiCheat.name().toLowerCase());
	}

}