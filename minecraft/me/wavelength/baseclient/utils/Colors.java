package me.wavelength.baseclient.utils;

import java.awt.Color;

import net.minecraft.client.Minecraft;

public class Colors {

	private static int[] colorCode;

	static {
		colorCode = new int[32];
		for (int i = 0; i < 32; ++i) {
			int j = (i >> 3 & 1) * 85;
			int k = (i >> 2 & 1) * 170 + j;
			int l = (i >> 1 & 1) * 170 + j;
			int i1 = (i >> 0 & 1) * 170 + j;

			if (i == 6) {
				k += 85;
			}

			if (Minecraft.getMinecraft().gameSettings.anaglyph) {
				int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
				int k1 = (k * 30 + l * 70) / 100;
				int l1 = (k * 30 + i1 * 70) / 100;
				k = j1;
				l = k1;
				i1 = l1;
			}

			if (i >= 16) {
				k /= 4;
				l /= 4;
				i1 /= 4;
			}

			colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
		}
	}

	public static org.newdawn.slick.Color getSlickColor(char c) {
		return new org.newdawn.slick.Color(getFromColorCode(c));
	}

	public static Color getFromColorCode(char c) {
		int i1 = "0123456789abcdefklmnor".indexOf(c);

		int j1 = colorCode[i1];

		float red = (j1 >> 16) / 255.0F;
		float green = (j1 >> 8 & 255) / 255.0F;
		float blue = (j1 & 255) / 255.0F;

		return new Color(red, green, blue);
	}

}