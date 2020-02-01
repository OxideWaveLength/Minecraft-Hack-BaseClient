package me.wavelength.baseclient.event;

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
import net.minecraft.client.Minecraft;

public class EventListener {

	protected Minecraft mc;
	
	public EventListener() {
		this.mc = Minecraft.getMinecraft();
	}
	
	public void onPacketSent(PacketSentEvent event) {
	}

	public void onPacketReceived(PacketReceivedEvent event) {
	}

	public void onMessageSent(MessageSentEvent event) {
	}

	public void onMessageReceived(MessageReceivedEvent event) {
	}

	public void onKeyPressed(KeyPressedEvent event) {
	}

	public void onUpdate(UpdateEvent event) {
	}

	public void onMouseScroll(MouseScrollEvent event) {
	}
	
	public void onMouseClick(MouseClickEvent event) {
	}

	public void onPreMotion(PreMotionEvent event) {
	}

	public void onPostMotion(PostMotionEvent event) {
	}
	
	public void onRender2D(Render2DEvent event) {
	}
	
	public void onRender3D(Render3DEvent event) {
	}

}