package me.wavelength.baseclient.utils.animation.basic;

import me.wavelength.baseclient.utils.animation.basic.util.AnimationUtil;

public class Translate {

	private float x;
	private float y;
	private long lastMS;

	public Translate(float x, float y) {
		this.x = x;
		this.y = y;
		this.lastMS = System.currentTimeMillis();
	}

	public void interpolate(float targetX, float targetY, int xSpeed, int ySpeed) {
		long currentMS = System.currentTimeMillis();
		long delta = currentMS - lastMS;// 16.66666
		lastMS = currentMS;
		int deltaX = (int) (Math.abs(targetX - x) * 0.51f);
		int deltaY = (int) (Math.abs(targetY - y) * 0.51f);
		x = AnimationUtil.calculateCompensation(targetX, x, delta, deltaX);
		y = AnimationUtil.calculateCompensation(targetY, y, delta, deltaY);
	}

	public void interpolate(float targetX, float targetY, double speed) {
		long currentMS = System.currentTimeMillis();
		long delta = currentMS - lastMS;// 16.66666
		lastMS = currentMS;
		double deltaX = 0;
		double deltaY = 0;
		if (speed != 0) {
			deltaX = (Math.abs(targetX - x) * 0.35f) / (10 / speed);
			deltaY = (Math.abs(targetY - y) * 0.35f) / (10 / speed);
		}
		x = AnimationUtil.calculateCompensation(targetX, x, delta, deltaX);
		y = AnimationUtil.calculateCompensation(targetY, y, delta, deltaY);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

}
