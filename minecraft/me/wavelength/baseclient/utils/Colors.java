package me.wavelength.baseclient.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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

	public static int getRGBWave(float seconds, float brightness, float saturation, long index) {
		float hue = ((System.currentTimeMillis() + index) % (int) (seconds * 1000)) / (seconds * 1000);
		return Color.HSBtoRGB(hue, saturation, brightness);
	}

	public static Integer[] getGradientArray(Color color1, Color color2, int steps) {
		List<Integer> colors = new ArrayList<Integer>();

		for (int i = 0; i < steps; i++) {
			float ratio = (float) i / (float) steps;
			int red = (int) (color2.getRed() * ratio + color1.getRed() * (1 - ratio));
			int green = (int) (color2.getGreen() * ratio + color1.getGreen() * (1 - ratio));
			int blue = (int) (color2.getBlue() * ratio + color1.getBlue() * (1 - ratio));
			Color stepColor = new Color(red, green, blue);
			colors.add(stepColor.getRGB());
		}

		return (Integer[]) colors.toArray();
	}

	public static long getIndex(long index) {
		return ((System.currentTimeMillis() + index));
	}
}