package me.wavelength.baseclient.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import me.wavelength.baseclient.BaseClient;
import net.minecraft.client.Minecraft;

public class Strings {

	public static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

	public static Color getColor(String name) {
		try {
			return (Color) Color.class.getField(name.toUpperCase()).get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
		}

		return null;
	}

	/**
	 * This is the advanced translateColors, it will keep the last color used and apply it to the next word. This fixes the issue #3
	 */
	public static String translateColors(String string) {
		String[] raw = string.split(" ");

		String result = "";

		List<String> styles = new ArrayList<String>();
		String lastColor = "";
		for (int wordIndex = 0; wordIndex < raw.length; wordIndex++) {
			String word = raw[wordIndex];
			char[] chars = word.toCharArray();
			for (int charIndex = 0; charIndex < chars.length; charIndex++) {
				char c = chars[charIndex];
				if (!(isColorSign(c)))
					continue;

				if (charIndex + 1 >= chars.length)
					continue;

				char nextChar = chars[charIndex + 1];
				String color = String.valueOf(c) + String.valueOf(nextChar);
				if (!(isColor(color)))
					continue;

				if ((nextChar == 'l' || nextChar == 'o' || nextChar == 'm' || nextChar == 'n')) {
					if (!(styles.contains(String.valueOf(nextChar))))
						styles.add(String.valueOf(nextChar));
				} else {
					lastColor = color;
					styles.clear();
				}
			}

			String styleCodes = "";

			for (int i = 0; i < styles.size(); i++)
				styleCodes += "&" + styles.get(i);

			result += (wordIndex == 0 ? "" : " ") + word + lastColor + styleCodes;
		}

		return simpleTranslateColors(result);
	}

	/**
	 * This is the simple translate colors, this will NOT keep track of the last color used, the Issue #3 will still be present if used Although it should have less impact on performances
	 */
	public static String simpleTranslateColors(String string) {
		return string.replace("&", "\u00A7");
	}

	/**
	 * This removes colors from a string
	 */
	public static String stripColors(String string) {
		return COLOR_CODE_PATTERN.matcher(simpleTranslateColors(string)).replaceAll("");
	}

	/**
	 * @return if @param string starts with a color or not
	 */
	public static boolean startsWithColor(String string) {
		string = simpleTranslateColors(string);

		if (string.length() < 2)
			return false;

		return COLOR_CODE_PATTERN.matcher(string.substring(0, 2)).matches();
	}

	/**
	 * @return if @param c is the color sign
	 */
	public static boolean isColorSign(char c) {
		return simpleTranslateColors(String.valueOf(c)).equals("\u00A7");
	}

	/**
	 * @return if @param text IS a color (&f for example)
	 */
	public static boolean isColor(String text) {
		return COLOR_CODE_PATTERN.matcher(simpleTranslateColors(text)).matches();
	}

	/**
	 * @formatter:off
	 * @return returns a version of @param string 
	 * where only the first letter is uppsercase and the rest is be lowercase
	 * @formatter:on
	 */
	public static String capitalizeOnlyFirstLetter(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1, string.length()).toLowerCase();
	}

	/**
	 * @formatter:off
	 * @return returns a version of @param string 
	 * where the first letter is uppsercase and the rest will stay untouched
	 * @formatter:on
	 */
	public static String capitalizeFirstLetter(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
	}

	public static int getStringWidth(String text) {
		return Minecraft.getMinecraft().fontRendererObj.getStringWidth(stripColors(text));
	}

	public static int getStringWidthCFR(String text) {
		return getStringWidthCFR(text, BaseClient.instance.getFontRenderer().getFontSize());
	}

	/**
	 * @formatter:off
	 * CFR stands for CustomFontRenderer
	 * That means that it will get the String Width from the custom font renderer, and not Minecraft's one.
	 * @formatter:on
	 */
	public static int getStringWidthCFR(String text, int fontSize) {
		return (int) BaseClient.instance.getFontRenderer().getFont(fontSize).getStringWidth(stripColors(text));
	}

	public static int getStringHeightCFR(String text) {
		return getStringHeightCFR(text, BaseClient.instance.getFontRenderer().getFontSize());
	}

	public static int getStringHeightCFR(String text, int fontSize) {
		return (int) BaseClient.instance.getFontRenderer().getFont(fontSize).getStringHeight(stripColors(text));
	}

	public static int getMaxWidth(String[] lines) {
		int maxWidth = 0;
		for (int i = 0; i < lines.length; i++) {
			int width = getStringWidth(lines[i]);

			if (maxWidth < width)
				maxWidth = width;
		}

		return maxWidth;
	}

	public static int getMaxChars(String[] lines) {
		int maxChars = 0;
		for (int i = 0; i < lines.length; i++) {
			int chars = lines[i].toCharArray().length;

			if (maxChars < chars)
				maxChars = chars;
		}

		return maxChars;
	}

	public static String multiplyString(String string, int amount) {
		return multiplyString(string, null, amount);
	}

	public static String multiplyString(String string, String glue, int amount) {
		String result = string;

		if (glue == null)
			glue = "";

		for (int i = 0; i < amount; i++) {
			result += (i == 0 ? "" : glue) + string;
		}

		return result;
	}

	public static boolean isEmpty(String string) {
		if (string.isEmpty())
			return true;
		string = string.trim();
		if (string.isEmpty())
			return true;
		if (string == "" || string == null)
			return true;
		return false;
	}

	public static boolean isEmpty(char[] chars) {
		if (chars.length == 0)
			return true;
		return isEmpty(chars.toString());
	}

	public static String getOsName() {
		return System.getProperty("os.name").toLowerCase();
	}

	public static String convertToUnix(String windows, String unix) {
		if (!getOsName().contains("windows")) {
			return unix;
		}
		return windows;
	}

	public static String getSplitter() {
		if (getOsName().contains("windows"))
			return "\\";
		return "/";
	}

	public static String getSplittedPath(String path) {
		path = path.replace("/", getSplitter());
		path = path.replace("\\", getSplitter());
		return path;
	}

	public static String[] replaceFromStringArray(String[] string, String[] parameters, String[] values) {
		String[] result = string;
		for (int i = 0; i < parameters.length; i++) {
			if (result[i] == null)
				continue;
			if (parameters[i] == null)
				continue;
			if (!result[i].contains(parameters[i]))
				continue;
			if (values[i] == null)
				result[i] = result[i].replace(parameters[i], "");
			result[i] = result[i].replace(parameters[i], values[i]);
		}
		return result;
	}

	/**
	 * 
	 * @param args - The first parameter is the String, after that the parameters, and with a line break (<br>
	 *             ) you define the values.
	 * @return
	 */
	public static String stringFormatter(String... args) {
		String[] parameters = Lists.objectArrayToString(Lists.removeElementFromArray(args, 0)).split("<br>");
		return stringFormatter(args[0], parameters[0].split(", "), parameters[1].split(", "));
	}

	public static String stringFormatter(String string, String[] parameters, String[] values) {
		String result = string;
		for (int i = 0; i < parameters.length; i++) {
			result = result.replace(parameters[i], values[i]);
		}
		return result;
	}

	public static String randomString(int length, boolean letters, boolean numbers, boolean uppercases) {
		String SALTCHARS = "";
		if (letters && uppercases)
			SALTCHARS += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		if (letters)
			SALTCHARS += "abcdefghijklmnopqrstuvwxyz";
		if (numbers)
			SALTCHARS += "1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < length) {
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;
	}

	public static String removeFirstChar(String text) {
		return text.substring(1, text.length());
	}

	/**
	 * @deprecated use the new {@link Integers#isInteger(string)}
	 */
	@Deprecated
	public static boolean isInteger(String string) {
		return string.matches("-?\\d+");
	}

	/**
	 * @deprecated use the new {@link Integers#isInteger(char)}
	 */
	@Deprecated
	public static boolean isInteger(char c) {
		return String.valueOf(c).matches("-?\\d+");
	}

	public static String intToRoman(int integer) {
		int[] bases = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, "I");
		map.put(4, "IV");
		map.put(5, "V");
		map.put(9, "IX");
		map.put(10, "X");
		map.put(40, "XL");
		map.put(50, "L");
		map.put(90, "XC");
		map.put(100, "C");
		map.put(400, "CD");
		map.put(500, "D");
		map.put(900, "CM");
		map.put(1000, "M");

		String result = new String();
		for (int i : bases) {
			while (integer >= i) {
				result += map.get(i);
				integer -= i;
			}
		}
		return result;
	}

	public static boolean isBoolean(String string) {
		if (string.equalsIgnoreCase("y") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true") || string.equalsIgnoreCase("on") || Integers.isInteger(string) || string.equalsIgnoreCase("n") || string.equalsIgnoreCase("no") || string.equalsIgnoreCase("false") || string.equalsIgnoreCase("off"))
			return true;
		return false;
	}

	public static boolean getBooleanValue(String string) {
		if (string.equalsIgnoreCase("y") || string.equalsIgnoreCase("yes") || string.equalsIgnoreCase("true") || string.equalsIgnoreCase("on") || (Integers.isInteger(string) && Integer.parseInt(string) > 0))
			return true;
		return false;
	}

}
