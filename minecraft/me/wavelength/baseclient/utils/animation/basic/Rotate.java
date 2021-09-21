package me.wavelength.baseclient.utils.animation.basic;

import me.wavelength.baseclient.utils.animation.basic.util.AnimationUtil;

public class Rotate {

	private float angle;
	private long lastMS;

	public Rotate(float angle) {
		this.angle = angle;
	}

	public void interpolate(float targetAngle, int speed) {
		long currentMS = System.currentTimeMillis();
		long delta = currentMS - lastMS;// 16.66666
		lastMS = currentMS;
		angle = AnimationUtil.calculateCompensation(targetAngle, angle, delta, speed);
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}
}
