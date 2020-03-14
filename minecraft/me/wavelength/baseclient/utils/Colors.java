package me.wavelength.baseclient.utils;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;

public class Colors {

	private static Map<Character, Color> colors;

	/**
	 * https://minecraft.gamepedia.com/Formatting_codes#Color_codes
	 */
	static {
		colors = new HashMap<Character, Color>();
		colors.put('0', new Color(0, 0, 0));
		colors.put('1', new Color(0, 0, 170));
		colors.put('2', new Color(0, 170, 0));
		colors.put('3', new Color(0, 170, 170));
		colors.put('4', new Color(170, 0, 0));
		colors.put('5', new Color(170, 0, 170));
		colors.put('6', new Color(250, 170, 0));
		colors.put('7', new Color(170, 170, 170));
		colors.put('8', new Color(85, 85, 85));
		colors.put('9', new Color(85, 85, 255));
		colors.put('a', new Color(85, 255, 85));
		colors.put('b', new Color(85, 255, 255));
		colors.put('c', new Color(255, 85, 85));
		colors.put('d', new Color(255, 85, 255));
		colors.put('e', new Color(255, 255, 85));
		colors.put('f', new Color(255, 255, 255));
		colors.put('r', new Color(255, 255, 255));
	}

	public static Color getFromColorCode(char c) {
		if (colors.containsKey(c))
			return colors.get(c);

		return null;
	}

}