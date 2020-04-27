package me.wavelength.baseclient.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Config {

	private File file;
	private LinkedHashMap<String, Object> defaultSettings;

	public Config(File file) {
		this.file = file;
		this.defaultSettings = new LinkedHashMap<String, Object>();
	}

	public Config(File file, String dir) {
		this.file = new File(file, dir);
		this.defaultSettings = new LinkedHashMap<String, Object>();
	}

	public Config(String file, String dir) {
		this.file = new File(file, dir);
		this.defaultSettings = new LinkedHashMap<String, Object>();
	}

	public Config(String filePath) {
		this.file = new File(filePath);
		this.defaultSettings = new LinkedHashMap<String, Object>();
	}

	public void check() throws IOException {
		for (Iterator<String> settings = defaultSettings.keySet().iterator(); settings.hasNext();) {
			String setting = settings.next();
			Object value = defaultSettings.get(setting);
			boolean hasLine = false;
			List<String> lines = readLines();
			for (int b = 0; b < lines.size(); b++) {
				String[] line = lines.get(b).split(": ");
				if (line.length < 2)
					continue;
				if (line != null && getObject(setting) != null && getString(setting).equals(line[1]))
					hasLine = true;
			}
			if (!(hasLine)) {
				if (value instanceof List) {
					try {
						set(setting, (List<String>) value); // Using reflection or creating a custom class extending ArrayList isn't probably worth it, so for now I will do an unchecked cast and try/catch it and change it later on
					} catch (ClassCastException e) {
						System.out.println("Only String lists are allowed. Path: " + setting);
					}
				} else
					set(setting, value);
			}
		}
	}

	public File getFile() {
		return file;
	}

	public File getDirectory() {
		if (file.isDirectory())
			return file;
		if (Strings.getOsName().toLowerCase().contains("windows"))
			return new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\")));
		else
			return new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/")));
	}

	public boolean exists() {
		return Files.fileExists(file);
	}

	public boolean isFile() {
		return Files.isFile(file);
	}

	public boolean isDirectory() {
		return Files.isDirectory(file);
	}

	public void generateConfigs() {
		try {
			String absolutePath = file.getAbsolutePath();

			absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));

			new File(absolutePath).mkdirs();
			file.createNewFile();
			check();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		Files.delete(file);
	}

	public List<String> readLines() {
		try {
			return Files.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteLine(int line) {
		Files.deleteLine(file, line);
	}

	public void deleteLine(String line) {
		Files.deleteLine(file, line);
	}

	public void removeLine(int line) {
		Files.removeLine(file, line);
	}

	public void removeLine(String line) {
		Files.removeLine(file, line);
	}

	public int getObjectLine(String path) {
		try {
			List<String> lines = readLines();
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				if (!(line.substring(0, line.indexOf(":")).equals(path)))
					continue;
				return i;
			}
		} catch (Exception e) {
		}

		return -1;
	}

	public Object getObject(String path) {
		List<String> lines = readLines();
		int lineIndex = getObjectLine(path);

		String line = null;

		return (lineIndex == -1 ? null : (line = lines.get(lineIndex)).substring(line.indexOf(":") + 2));
	}

	public String getString(String path) {
		try {
			return getObject(path).toString();
		} catch (Exception e) {
		}
		return null;
	}

	public boolean getBoolean(String path) {
		try {
			String value = getObject(path).toString();
			if (Integers.isInteger(value))
				value = Boolean.toString((Integer.parseInt(value) == 0 ? false : true));
			return Boolean.parseBoolean(value);
		} catch (Exception e) {
		}
		return false;
	}

	public char getChar(String path) {
		try {
			return getObject(path).toString().charAt(0);
		} catch (Exception e) {

		}
		return (char) -123;
	}

	public int getInt(String path) {
		try {
			return Integer.parseInt(getString(path));
		} catch (Exception e) {

		}
		return 0;
	}

	public double getDouble(String path) {
		try {
			return Double.parseDouble(getString(path));
		} catch (Exception e) {

		}
		return 0d;
	}

	public float getFloat(String path) {
		try {
			return Float.parseFloat(getString(path));
		} catch (Exception e) {

		}
		return 0f;
	}

	public List<String> getStringList(String path, Function<String, String> actions) {
		List<String> list = new ArrayList<String>();

		String line = getString(path);

		String regex = "(?<!\\\\)" + Pattern.quote(", "); // SOURCE: https://stackoverflow.com/a/18677785. This should make it possible to escape commas

		if (line != null) {
			String[] items = line.split(regex);
			for (int i = 0; i < items.length; i++) {
				String item = items[i];

				if (actions != null)
					item = actions.apply(item);

				list.add(item);
			}
		}

		return list.isEmpty() ? null : list;
	}

	public void set(String path, Object value) {
		try {
			List<String> lines = readLines();
			delete();
//			generateConfigs();
			boolean found = false;
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				if (!(line.substring(0, line.indexOf(":")).equals(path)))
					continue;
				lines.set(i, path + ": " + value);
				found = true;
			}
			for (int i = 0; i < lines.size(); i++)
				Files.append(file, lines.get(i));
			if (!(found))
				Files.append(file, path + ": " + value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void set(String path, String value) {
		set(path, (Object) value);
	}

	public void set(String path, char value) {
		set(path, (Object) value);
	}

	public void set(String path, int value) {
		set(path, (Object) value);
	}

	public void set(String path, float value) {
		set(path, (Object) value);
	}

	public void set(String path, double value) {
		set(path, (Object) value);
	}

	public void set(String path, List<String> value) {
		String list = "";

		for (int i = 0; i < value.size(); i++) {
			list += (i == 0 ? "" : ", ") + value.get(i).replace(",", "\\,");
		}

		set(path, list);
	}

	public LinkedHashMap<String, Object> getDefaultSettings() {
		return defaultSettings;
	}

	public void addDefault(String path, String value) {
		defaultSettings.put(path, value);
		generateConfigs();
	}

	public void addDefault(String path, boolean value) {
		defaultSettings.put(path, value);
		generateConfigs();
	}

	public void addDefault(String path, char value) {
		defaultSettings.put(path, value);
		generateConfigs();
	}

	public void addDefault(String path, int value) {
		defaultSettings.put(path, value);
		generateConfigs();
	}

	public void addDefault(String path, float value) {
		defaultSettings.put(path, value);
		generateConfigs();
	}

	public void addDefault(String path, double value) {
		defaultSettings.put(path, value);
		generateConfigs();
	}

	public void addDefault(String path, List<String> value) {
		defaultSettings.put(path, value);
		generateConfigs();
	}

	public void delete(String path) throws IOException {
		List<String> lines = readLines();
		delete();
		generateConfigs();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (!(line.substring(0, line.indexOf(":")).equals(path)))
				Files.append(file, line);
		}
	}

}