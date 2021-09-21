package me.wavelength.baseclient.utils.animation.basic;

import me.wavelength.baseclient.utils.animation.basic.util.AnimationUtil;

public class Expand {

	private float x;
	private float y;

	private float expandX;
	private float expandY;
	private long lastMS;

	public Expand(float x, float y, float expandX, float expandY) {
		this.x = x;
		this.y = y;
		this.expandX = expandX;
		this.expandY = expandY;
	}

	public void hardInterpolate(float targetX, float targetY, int xSpeed, int ySpeed) {
		long currentMS = System.currentTimeMillis();
		long delta = currentMS - lastMS;// 16.66666
		lastMS = currentMS;
		expandX = AnimationUtil.calculateCompensation(targetX, expandX, delta, xSpeed);
		expandY = AnimationUtil.calculateCompensation(targetY, expandY, delta, ySpeed);
	}

	public void interpolate(float targetX, float targetY, int xSpeed, int ySpeed) {
		long currentMS = System.currentTimeMillis();
		long delta = currentMS - lastMS;// 16.66666
		if (delta > 60) {
			delta = 16;
		}
		lastMS = currentMS;
		int deltaX = (int) (Math.abs(targetX - expandX) * 0.3f);
		int deltaY = (int) (Math.abs(targetY - expandY) * 0.3f);
		expandX = AnimationUtil.calculateCompensation(targetX, expandX, delta, deltaX);
		expandY = AnimationUtil.calculateCompensation(targetY, expandY, delta, deltaY);
	}

	public float getExpandX() {
		return expandX;
	}

	public float getExpandY() {
		return expandY;
	}

	public void setExpandX(float expandX) {
		this.expandX = expandX;
	}

	public void setExpandY(float expandY) {
		this.expandY = expandY;
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
