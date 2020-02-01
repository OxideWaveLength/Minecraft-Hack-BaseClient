package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.CancellableEvent;

public class MouseClickEvent extends CancellableEvent {

	private int button;

	/**
	 * @formatter:off
	 * Directions:
	 * 0 = LEFT CLICK
	 * 1 = RIGHT CLICK
	 * 2 = WHEEL CLICK
	 */
	public MouseClickEvent(int button) {
		this.button = button;
	}
	
	public int getButton() {
		return button;
	}

}