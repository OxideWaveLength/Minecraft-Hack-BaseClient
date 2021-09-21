package me.wavelength.baseclient.utils.animation.basic;

public class Scale {

	private float centerX;
	private float centerY;
	private float width;
	private float height;
	private long lastMS;

	public Scale(float centerX, float centerY, float width, float height) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.height = height;
		this.width = width;
		this.lastMS = System.currentTimeMillis();
	}

	public void interpolate(float tWidth, float tHeight, int speed) {
		long currentMS = System.currentTimeMillis();
		long delta = currentMS - lastMS;// 16.66666
		lastMS = currentMS;
		float diffW = (width - tWidth);
		if (diffW > speed) {
			width -= (speed * delta / (1000 / 60));
		} else if (diffW < -speed) {
			width += (speed * delta / (1000 / 60));
		} else {
			width = tWidth;
		}
		float diffH = (height - tHeight);
		if (diffH > speed) {
			height -= (speed * delta / (1000 / 60));
		} else if (diffH < -speed) {
			height += (speed * delta / (1000 / 60));
		} else {
			height = tHeight;
		}
	}

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

}
