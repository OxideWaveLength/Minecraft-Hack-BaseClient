package me.wavelength.baseclient.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager {

	private List<EventListener> eventListeners;

	private HashMap<String, Method> listenerMethods;

	public EventManager() {
		this.eventListeners = new ArrayList<EventListener>();
		this.listenerMethods = new HashMap<String, Method>();

		Method[] methods = EventListener.class.getMethods();
		for (Method method : methods) {
			this.listenerMethods.put(method.getName().substring(2), method);
		}
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
	 * NOTE: there is only one small drawback, you need to have the class's instance. Or if you are unregistering the listener from within the listener's class, you can just do {@link #unregisterEventListener()} with "this" as parameter
	 * 
	 * @param clasz the listener's class
	 */
	public void unregisterListener(EventListener eventListener) {
		if (eventListeners.contains(eventListener))
			eventListeners.remove(eventListener);
	}

	/**
	 * This way you can unregister an event listener from the class (NOTE: doing this will unregister ALL of the instances of a class)
	 * 
	 * @param clasz the listener's class
	 */
	public void unregisterListener(Class<? extends EventListener> clasz) {
		for (int i = 0; i < eventListeners.size(); i++)
			if (eventListeners.get(i).getClass().equals(clasz))
				eventListeners.remove(i);
	}

	public Event call(Event event) {
		List<EventListener> eventListeners = new ArrayList<EventListener>(this.eventListeners);

		String eventName = event.getClass().getSimpleName();
		eventName = eventName.substring(0, eventName.toLowerCase().lastIndexOf("event"));

		for (EventListener listener : eventListeners) {
			if (event instanceof CancellableEvent)
				if (((CancellableEvent) event).isCancelled())
					return event;
			try {
				if (!(listenerMethods.containsKey(eventName)))
					listenerMethods.put(eventName, EventListener.class.getMethod("on" + eventName, event.getClass()));

				Method method = listenerMethods.get(eventName);
				method.invoke(listener, event);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return event;
	}

}