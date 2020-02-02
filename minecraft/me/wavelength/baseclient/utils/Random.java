package me.wavelength.baseclient.utils;

import java.awt.Color;
import java.security.SecureRandom;

public class Random {

	public static SecureRandom random = new SecureRandom();

	public static Color getRandomColor() {
		float r = randomFloat(0, 1);
		float g = randomFloat(0, 1);
		float b = randomFloat(0, 1);
		return new Color(r, g, b);
	}

	public static Color getRandomLightColor() {
		float r = (randomFloat(0f, 0.5f) + randomFloat(0f, 0.5f)) / 2f + 0.5f;
		float g = (randomFloat(0f, 0.5f) + randomFloat(0f, 0.5f)) / 2f + 0.5f;
		float b = (randomFloat(0f, 0.5f) + randomFloat(0f, 0.5f)) / 2f + 0.5f;
		return new Color(r, g, b);
	}

	public static int randomInt(int min, int max) {
		return randomInts(min, max, 1)[0];
	}

	public static int[] randomInts(int min, int max, int amount) {
		int[] results = new int[amount];
		for (int i = 0; i < amount; i++)
			results[i] = random.ints(min, (max + 1)).limit(1).findFirst().getAsInt();

		return results;
	}

	public static double randomDouble(double min, double max) {
		return randomDoubles(min, max, 1)[0];
	}

	public static double[] randomDoubles(double min, double max, int amount) {
		double[] results = new double[amount];
		for (int i = 0; i < amount; i++)
			results[i] = min + (max - min) * random.nextDouble();

		return results;
	}

	public static float randomFloat(float min, float max) {
		return randomFloats(min, max, 1)[0];
	}

	public static float[] randomFloats(float min, float max, int amount) {
		float[] results = new float[amount];
		for (int i = 0; i < amount; i++)
			results[i] = min + (max - min) * random.nextFloat();

		return results;
	}

	public static boolean randomBoolean() {
		return random.nextBoolean();
	}

}