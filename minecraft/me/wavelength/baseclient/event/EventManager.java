package me.wavelength.baseclient.event;

import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.event.events.KeyPressedEvent;
import me.wavelength.baseclient.event.events.MessageReceivedEvent;
import me.wavelength.baseclient.event.events.MessageSentEvent;
import me.wavelength.baseclient.event.events.MouseClickEvent;
import me.wavelength.baseclient.event.events.MouseScrollEvent;
import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.event.events.PostMotionEvent;
import me.wavelength.baseclient.event.events.PreMotionEvent;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.event.events.Render3DEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;

public class EventManager {

	private List<EventListener> eventListeners;

	public EventManager() {
		this.eventListeners = new ArrayList<EventListener>();
	}

	public void registerEvent(EventListener eventListener) {
		this.eventListeners.add(eventListener);
	}

	public void unregisterEvent(EventListener eventListener) {
		if (eventListeners.contains(eventListener))
			eventListeners.remove(eventListener);
	}

	public Event call(Event event) {
		if (event instanceof CancellableEvent)
			if (((CancellableEvent) event).isCancelled())
				return event;
		for (int i = 0; i < eventListeners.size(); i++) {
			if (event instanceof KeyPressedEvent) {
				eventListeners.get(i).onKeyPressed((KeyPressedEvent) event);
			}
			if (event instanceof MessageReceivedEvent) {
				eventListeners.get(i).onMessageReceived((MessageReceivedEvent) event); // TODO: Implement
			}
			if (event instanceof MessageSentEvent) {
				eventListeners.get(i).onMessageSent((MessageSentEvent) event);
			}
			if (event instanceof PacketReceivedEvent) {
				eventListeners.get(i).onPacketReceived((PacketReceivedEvent) event); // TODO: Implement
			}
			if (event instanceof PacketSentEvent) {
				eventListeners.get(i).onPacketSent((PacketSentEvent) event); // TODO: Implement
			}
			if (event instanceof UpdateEvent) {
				eventListeners.get(i).onUpdate((UpdateEvent) event);
			}
			if (event instanceof MouseScrollEvent) {
				eventListeners.get(i).onMouseScroll((MouseScrollEvent) event); // TODO: Implement
			}
			if (event instanceof MouseClickEvent) {
				eventListeners.get(i).onMouseClick((MouseClickEvent) event); // TODO: Implement
			}
			if (event instanceof PreMotionEvent) {
				eventListeners.get(i).onPreMotion((PreMotionEvent) event);
			}
			if (event instanceof PostMotionEvent) {
				eventListeners.get(i).onPostMotion((PostMotionEvent) event);
			}
			if (event instanceof Render2DEvent) {
				eventListeners.get(i).onRender2D((Render2DEvent) event);
			}
			if (event instanceof Render3DEvent) {
				eventListeners.get(i).onRender3D((Render3DEvent) event);
			}
		}

		return event;
	}

}