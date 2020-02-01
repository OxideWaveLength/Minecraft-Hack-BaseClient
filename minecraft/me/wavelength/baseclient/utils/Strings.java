package me.wavelength.baseclient.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import me.wavelength.baseclient.BaseClient;
import net.minecraft.client.Minecraft;

public class Strings {

	public static String translateColors(String string) {
		return string.replace("&", "\u00A7");
	}
	
	public static String stripColors(String string) {
		return translateColors(string).replaceAll("\u00A7[a-z-A-z-0-9]", "");
	}
	
	public static String capitalizeOnlyFirstLetter(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1, string.length()).toLowerCase();
	}
	
	public static int getStringWidth(String text) {
		return Minecraft.getMinecraft().fontRendererObj.getStringWidth(stripColors(text));
	}
	
	/**
	 * @formatter:off
	 * CFR stands for CustomFontRenderer, that means that it will get the String Width from the custom font renderer, and not Minecraft's one.
	 * @formatter:on
	 */
	public static int getStringWidthCFR(String text) {
		return (int) BaseClient.instance.getFontRenderer().getStringWidth(stripColors(text));
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
	 * @param args - The first parameter is the String, after that the parameters,
	 *             and with a line break (<br>
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

	public static String generateCreditCard(String bin, int length) {
		Random random = new Random(System.currentTimeMillis());
		int randomNumberLength = length - (bin.length() + 1);

		StringBuilder builder = new StringBuilder(bin);
		for (int i = 0; i < randomNumberLength; i++) {
			int digit = random.nextInt(10);
			builder.append(digit);
		}

		int checkDigit = getCreditCardCheckDigit(builder.toString());
		builder.append(checkDigit);

		return builder.toString();
	}

	public static int getCreditCardCheckDigit(String number) {
		int sum = 0;
		for (int i = 0; i < number.length(); i++) {
			int digit = Integer.parseInt(number.substring(i, (i + 1)));

			if ((i % 2) == 0) {
				digit = digit * 2;
				if (digit > 9) {
					digit = (digit / 10) + (digit % 10);
				}
			}
			sum += digit;
		}
		int mod = sum % 10;
		return ((mod == 0) ? 0 : 10 - mod);
	}

}