package me.wavelength.baseclient.utils.animation.basic.util;

public class AnimationUtil {

	public static float calculateCompensation(float target, float current, long delta, double speed) {
		float diff = current - target;
		if (delta < 1) {
			delta = 1;
		}
		if (delta > 1000) {
			delta = 16;
		}
		if (diff > speed) {
			double xD = (speed * delta / (1000 / 60) < 0.5 ? 0.5 : speed * delta / (1000 / 60));
			current -= xD;
			if (current < target) {
				current = target;
			}
		} else if (diff < -speed) {
			double xD = (speed * delta / (1000 / 60) < 0.5 ? 0.5 : speed * delta / (1000 / 60));
			current += xD;
			if (current > target) {
				current = target;
			}
		} else {
			current = target;
		}
		return current;
	}

}
