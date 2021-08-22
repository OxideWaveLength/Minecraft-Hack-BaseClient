package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.Event;

public class Render2DEvent extends Event {

	private int width;
	private int height;

	public Render2DEvent(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}