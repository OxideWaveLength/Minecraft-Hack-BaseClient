package me.wavelength.baseclient.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Files {

	public static void createRecursiveFolder(String path) {
		createRecursiveFolder("", path);
	}

	public static void createRecursiveFolder(String base, String path) {
		String p = base;

		String[] dirs = path.split("\\" + Strings.getSplitter());
		for (int i = 0; i < dirs.length; i++) {
			String dir = dirs[i];
			p = (p == null || p.equals("") ? "" : p + (p.endsWith(Strings.getSplitter()) ? "" : Strings.getSplitter())) + dir;

			File file = new File(p);
			if (!(file.exists()))
				file.mkdir();
		}
	}

	public static boolean fileExists(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static boolean fileExists(String dir, String file) {
		return new File(dir, file).exists();
	}

	public static boolean fileExists(File dir, String file) {
		return new File(dir, file).exists();
	}

	public static boolean fileExists(File file) {
		return file.exists();
	}

	public static boolean isFile(String path) {
		File file = new File(path);
		return file.isFile();
	}

	public static boolean isFile(File file) {
		return file.isFile();
	}

	public static boolean isDirectory(String path) {
		File file = new File(path);
		return file.isDirectory();
	}

	public static boolean isDirectory(File file) {
		return file.isDirectory();
	}

	public static void create(String path) {
		try {
			new File(path).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void create(String dir, String file) {
		try {
			new File(dir, file).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void create(File file, String dir) {
		try {
			new File(file, dir).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void create(File file) throws IOException {
		file.createNewFile();
	}

	public static void createDirectory(String path) {
		File dir = new File(path);
		dir.mkdirs();
	}

	public static void createDirectory(File dir, String file) {
		new File(dir, file).mkdirs();
	}

	public static void createDirectory(String dir, String file) {
		new File(dir, file).mkdirs();
	}

	public static void delete(String path) {
		File file = new File(path);
		file.delete();
	}

	public static void delete(File file) {
		file.delete();
	}

	public static void rename(String path, String newPath) {
		File file = new File(path);
		File newFile = new File(newPath);
		file.renameTo(newFile);
	}

	public static void write(String path, String text) throws IOException {
		write(new File(path), text);
	}

	public static void write(File file, String text) throws IOException {
		delete(file);
		append(file, text);
	}

	public static void write(File file, BufferedReader reader) throws IOException {
		delete(file);
		while (reader.ready())
			append(file, reader.readLine());
	}

	public static void write(File file, List<String> lines) throws IOException {
		delete(file);
		append(file, lines);
	}

	public static void append(String path, String text) throws IOException {
		append(new File(path), text);
	}

	public static void append(String path, List<String> lines) throws IOException {
		append(new File(path), lines);
	}

	public static void append(File file, List<String> lines) throws IOException {
		for (int i = 0; i < lines.size(); i++) {
			append(file, lines.get(i));
		}
	}

	public static void append(File file, String text) throws IOException {
		FileWriter fr;
		fr = new FileWriter(file, true);
		fr.write(text + System.getProperty("line.separator"));
		fr.close();
	}

	public static List<String> read(String path) throws IOException {
		return read(new File(path));
	}

	public static List<String> read(File file) throws IOException {
		List<String> line = new ArrayList<String>();
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(file));
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			line.add(currentLine);
		}
		reader.close();
		return line;
	}

	public static File getExecutionDirectory(Class<?> clasz) {
		return new File(System.getProperty("user.dir"));
	}

	public static void setLine(String path, int line, String newContent) {
		setLine(new File(path), line, newContent);
	}

	public static void setLine(File file, int line, String newContent) {
		try {
			List<String> lines = read(file);
			if (newContent == null) {
				lines.remove(line);
			} else {
				lines.set(line, newContent);
			}
			String finalLines = Lists.listStringToString(lines, "\n");
			write(file, (finalLines == null ? "" : finalLines));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setLine(File file, String line, String newContent) {
		try {
			List<String> lines = read(file);
			for (int i = 0; i < lines.size(); i++) {
				if (!(lines.get(i).equals(line)))
					continue;
				deleteLine(file, i);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeLine(String path, int line) {
		deleteLine(new File(path), line);
	}

	public static void removeLine(File file, int line) {
		deleteLine(file, line);
	}

	public static void deleteLine(String path, int line) {
		deleteLine(new File(path), line);
	}

	public static void deleteLine(File file, int line) {
		setLine(file, line, null);
	}

	public static void removeLine(String path, String line) {
		deleteLine(new File(path), line);
	}

	public static void removeLine(File file, String line) {
		deleteLine(file, line);
	}

	public static void deleteLine(String path, String line) {
		deleteLine(new File(path), line);
	}

	public static void deleteLine(File file, String line) {
		setLine(file, line, null);
	}

}