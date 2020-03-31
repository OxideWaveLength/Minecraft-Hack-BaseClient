package me.wavelength.baseclient.event;

import java.util.ArrayList;
import java.util.List;

import me.wavelength.baseclient.event.events.BlockBrightnessRequestEvent;
import me.wavelength.baseclient.event.events.BlockRenderEvent;
import me.wavelength.baseclient.event.events.CollideEvent;
import me.wavelength.baseclient.event.events.FluidRenderEvent;
import me.wavelength.baseclient.event.events.KeyPressedEvent;
import me.wavelength.baseclient.event.events.MessageReceivedEvent;
import me.wavelength.baseclient.event.events.MessageSentEvent;
import me.wavelength.baseclient.event.events.MouseClickEvent;
import me.wavelength.baseclient.event.events.MouseScrollEvent;
import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.PacketSentEvent;
import me.wavelength.baseclient.event.events.PlayerSpawnEvent;
import me.wavelength.baseclient.event.events.PostMotionEvent;
import me.wavelength.baseclient.event.events.PreMotionEvent;
import me.wavelength.baseclient.event.events.Render2DEvent;
import me.wavelength.baseclient.event.events.Render3DEvent;
import me.wavelength.baseclient.event.events.RenderLivingLabelEvent;
import me.wavelength.baseclient.event.events.ServerConnectingEvent;
import me.wavelength.baseclient.event.events.ServerJoinEvent;
import me.wavelength.baseclient.event.events.ServerLeaveEvent;
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
		if (event instanceof CancellableEvent)
			if (((CancellableEvent) event).isCancelled())
				return event;
		
		List<EventListener> eventListeners = new ArrayList<EventListener>(this.eventListeners);
		
		for (int i = 0; i < eventListeners.size(); i++) {
			EventListener eventListener = eventListeners.get(i);
			if (event instanceof KeyPressedEvent) {
				eventListener.onKeyPressed((KeyPressedEvent) event);
				continue;
			}
			if (event instanceof MessageReceivedEvent) {
				eventListener.onMessageReceived((MessageReceivedEvent) event); // Class: GuiNewChat#printChatMessageWithOptionalDeletion()
				continue;
			}
			if (event instanceof MessageSentEvent) {
				eventListener.onMessageSent((MessageSentEvent) event);
				continue;
			}
			if (event instanceof PacketReceivedEvent) {
				eventListener.onPacketReceived((PacketReceivedEvent) event); // Class: NetworkManager#channelRead0()
				continue;
			}
			if (event instanceof PacketSentEvent) {
				eventListener.onPacketSent((PacketSentEvent) event); // Class: NetworkManager#sendPacket()
				continue;
			}
			if (event instanceof UpdateEvent) {
				eventListener.onUpdate((UpdateEvent) event);
				continue;
			}
			if (event instanceof MouseScrollEvent) {
				eventListener.onMouseScroll((MouseScrollEvent) event);
				continue;
			}
			if (event instanceof MouseClickEvent) {
				eventListener.onMouseClick((MouseClickEvent) event);
				continue;
			}
			if (event instanceof PreMotionEvent) {
				eventListener.onPreMotion((PreMotionEvent) event);
				continue;
			}
			if (event instanceof PostMotionEvent) {
				eventListener.onPostMotion((PostMotionEvent) event);
				continue;
			}
			if (event instanceof Render2DEvent) {
				eventListener.onRender2D((Render2DEvent) event);
			}
			if (event instanceof Render3DEvent) {
				eventListener.onRender3D((Render3DEvent) event);
				continue;
			}
			if (event instanceof ServerConnectingEvent) {
				eventListener.onServerConnecting((ServerConnectingEvent) event); // Class GuiConnecting#connect()
				continue;
			}
			if (event instanceof ServerJoinEvent) {
				eventListener.onServerJoin((ServerJoinEvent) event); // Class GuiConnecting#connect() - New Thread
				continue;
			}
			if (event instanceof ServerLeaveEvent) {
				eventListener.onServerLeave((ServerLeaveEvent) event); // Class GuiDisconnect - constructor
				continue;
			}
			if (event instanceof CollideEvent) {
				eventListener.onCollide((CollideEvent) event);
				continue;
			}
			if (event instanceof BlockRenderEvent) {
				eventListener.onBlockRender((BlockRenderEvent) event);
				continue;
			}
			if (event instanceof FluidRenderEvent) {
				eventListener.onFluidRender((FluidRenderEvent) event);
				continue;
			}
			if (event instanceof BlockBrightnessRequestEvent) {
				eventListener.onBlockBrightnessRequest((BlockBrightnessRequestEvent) event);
				continue;
			}
			if (event instanceof RenderLivingLabelEvent) {
				eventListener.onRenderLivingLabel((RenderLivingLabelEvent) event);
				continue;
			}
			if (event instanceof PlayerSpawnEvent) {
				eventListener.onPlayerSpawn((PlayerSpawnEvent) event);
				continue;
			}
		}

		return event;
	}

}