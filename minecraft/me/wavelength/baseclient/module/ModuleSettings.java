package me.wavelength.baseclient.module;

import java.io.File;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.utils.Config;
import me.wavelength.baseclient.utils.Files;

public class ModuleSettings {

	private Module module;
	private Config config;

	public ModuleSettings(Module module) {
		this.module = module;

		String path = new File(".").getAbsolutePath();

		path = (path.contains("jars") ? new File(".").getAbsolutePath().substring(0, path.length() - 2) : new File(".").getAbsolutePath());

		String clientName = BaseClient.instance.getClientName();

		if (!(Files.fileExists(path, clientName + "\\")))
			Files.createDirectory(path, clientName);
		if (!(Files.fileExists(path, clientName + "\\modules")))
			Files.createDirectory(path, clientName + "\\modules");
		if (!(Files.fileExists(path, clientName + "\\modules\\" + module.getCategory().toString().toLowerCase())))
			Files.createDirectory(path, clientName + "\\modules\\" + module.getCategory().toString().toLowerCase());

		this.config = new Config(path, clientName + "\\modules\\" + module.getCategory().toString().toLowerCase() + "\\" + module.getName() + ".cfg");
		config.addDefault("toggled", false);
		config.addDefault("key", module.getKey());
		config.addDefault("anticheat", module.getAntiCheat().name().toLowerCase());
		config.generateConfigs();
	}

	public Module getModule() {
		return module;
	}

	public Config getConfig() {
		return config;
	}

	public void generateConfigs() {
		config.generateConfigs();
	}

	public void addDefault(String key, String value) {
		config.addDefault(key.toLowerCase(), value);
	}

	public void addDefault(String key, boolean value) {
		config.addDefault(key.toLowerCase(), value);
	}

	public void addDefault(String key, int value) {
		config.addDefault(key.toLowerCase(), value);
	}

	public void addDefault(String key, char value) {
		config.addDefault(key.toLowerCase(), value);
	}

	public void addDefault(String key, double value) {
		config.addDefault(key.toLowerCase(), value);
	}

	public void addDefault(String key, float value) {
		config.addDefault(key.toLowerCase(), value);
	}

	public void set(String key, Object value) {
		config.set(key.toLowerCase(), value);
	}

	public String getString(String key) {
		return config.getString(key.toLowerCase());
	}

	public boolean getBoolean(String key) {
		return config.getBoolean(key.toLowerCase());
	}

	public double getDouble(String key) {
		return config.getDouble(key.toLowerCase());
	}

	public float getFloat(String key) {
		return config.getFloat(key.toLowerCase());
	}

	public Object getObject(String key) {
		return config.getObject(key.toLowerCase());
	}

	public int getInt(String key) {
		return config.getInt(key.toLowerCase());
	}
	
}