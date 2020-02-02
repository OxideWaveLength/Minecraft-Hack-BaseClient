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
	
	/**
	 * @deprecated this method will be removed soon. Use the new {@link #registerListener(EventListener)}
	 */
	@Deprecated
	public void registerEvent(EventListener eventListener) {
		registerListener(eventListener);
	}
	
	/**
	 * @deprecated this method will be removed soon. Use the new {@link #unregisterListener(EventListener)}
	 */
	@Deprecated
	public void unregisterEvent(EventListener eventListener) {
		unregisterListener(eventListener);
	}

	public void registerListener(EventListener eventListener) {
		this.eventListeners.add(eventListener);
	}
	
	/**
	 * This way you can unregister an event listener from the INSTANCE.
	 * 
	 * NOTE: there is only one small drawback, you need to have the class's instance.
	 * Or if you are unregistering the listener from within the listener's class, you can just do {@link #unregisterEventListener()} with "this" as parameter
	 * 
	 * @param clasz the listener's class
	 */
	public void unregisterListener(EventListener eventListener) {
		if (eventListeners.contains(eventListener))
			eventListeners.remove(eventListener);
	}
	
	/**
	 * This way you can unregister an event listener from the class (NOTE: doing this will unregister ALL of the instances of a class)
	 * @param clasz the listener's class
	 */
	public void unregisterListener(Class<? extends EventListener> clasz) {
		for(int i = 0; i < eventListeners.size(); i++)
			if(eventListeners.get(i).getClass().equals(clasz))
				eventListeners.remove(i);
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
				eventListeners.get(i).onMessageReceived((MessageReceivedEvent) event); // Class: GuiNewChat#printChatMessageWithOptionalDeletion()
			}
			if (event instanceof MessageSentEvent) {
				eventListeners.get(i).onMessageSent((MessageSentEvent) event);
			}
			if (event instanceof PacketReceivedEvent) {
				eventListeners.get(i).onPacketReceived((PacketReceivedEvent) event); // TODO: Implement
			}
			if (event instanceof PacketSentEvent) {
				eventListeners.get(i).onPacketSent((PacketSentEvent) event); // Class: NetworkManager#sendPacket()
			}
			if (event instanceof UpdateEvent) {
				eventListeners.get(i).onUpdate((UpdateEvent) event);
			}
			if (event instanceof MouseScrollEvent) {
				eventListeners.get(i).onMouseScroll((MouseScrollEvent) event);
			}
			if (event instanceof MouseClickEvent) {
				eventListeners.get(i).onMouseClick((MouseClickEvent) event);
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