package me.wavelength.baseclient.utils.animation.basic;

import me.wavelength.baseclient.utils.animation.basic.util.AnimationUtil;

/**
 * Created by cool1 on 4/9/2017.
 */
public class Opacity {

	private float opacity;
	private long lastMS;

	public Opacity(int opacity) {
		this.opacity = opacity;
		lastMS = System.currentTimeMillis();
	}

	public void interpolate(int targetOpacity) {
		opacity = (int) AnimationUtil.calculateCompensation(targetOpacity, opacity, 16, 5);
	}

	public void interp(float targetOpacity, double speed) {
		long currentMS = System.currentTimeMillis();
		long delta = currentMS - lastMS;// 16.66666
		lastMS = currentMS;
		opacity = (AnimationUtil.calculateCompensation(targetOpacity, opacity, delta, speed));
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

}
