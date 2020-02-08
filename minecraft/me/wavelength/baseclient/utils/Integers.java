package me.wavelength.baseclient.utils;

import java.util.regex.Pattern;

public class Integers {

	private static final Pattern DOUBLE_PATTERN = Pattern.compile("[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" + "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" + "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" + "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");

	public static boolean isPositive(int i) {
		return i >= 0;
	}

	public static boolean isNegative(int i) {
		return !isPositive(i);
	}

	public static boolean isInteger(String string) {
		return string.matches("-?\\d+");
	}

	public static boolean isInteger(char c) {
		return String.valueOf(c).matches("-?\\d+");
	}

	public static int getInteger(String string) throws NumberFormatException {
		return Integer.parseInt(string);
	}

	public static boolean isDouble(char c) {
		return isDouble(String.valueOf(c));
	}

	public static boolean isDouble(String s) {
		return isDouble(s, false);
	}

	public static boolean isDouble(String s, boolean strict) {
		if (strict)
			if (!(s.contains(".")))
				return false;

		return DOUBLE_PATTERN.matcher(s).matches();
	}

}