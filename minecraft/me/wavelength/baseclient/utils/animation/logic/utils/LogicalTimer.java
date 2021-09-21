package me.wavelength.baseclient.utils.animation.logic.utils;

import net.minecraft.client.Minecraft;

public class LogicalTimer {

	public long lastMS = System.currentTimeMillis();

	public void reset() {
		lastMS = System.currentTimeMillis();
	}

	public boolean hasTimeElapsed(long time, boolean reset) {
		if (System.currentTimeMillis() - lastMS > time) {
			if (reset)
				reset();

			return true;
		}

		return false;
	}

	public long getTime() {
		return System.currentTimeMillis() - lastMS;
	}

	public void setTime(long time) {
		lastMS = time;
	}

	public void mcTimeSpeed(double speed) {
		Minecraft.getMinecraft().timer.timerSpeed = (float) speed;
	}
}
