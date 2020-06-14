package me.wavelength.baseclient.event;

import me.wavelength.baseclient.event.events.BlockBrightnessRequestEvent;
import me.wavelength.baseclient.event.events.BlockRenderEvent;
import me.wavelength.baseclient.event.events.CollideEvent;
import me.wavelength.baseclient.event.events.FluidRenderEvent;
import me.wavelength.baseclient.event.events.KeyPressedEvent;
import me.wavelength.baseclient.event.events.LadderClimbEvent;
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
import me.wavelength.baseclient.event.events.SlowDownEvent;
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

	public void onServerConnecting(ServerConnectingEvent event) {
	}

	public void onServerJoin(ServerJoinEvent event) {
	}

	public void onServerLeave(ServerLeaveEvent event) {
	}

	public void onCollide(CollideEvent event) {
	}

	public void onBlockRender(BlockRenderEvent event) {
	}

	public void onBlockBrightnessRequest(BlockBrightnessRequestEvent event) {
	}

	public void onRenderLivingLabel(RenderLivingLabelEvent event) {
	}

	public void onPlayerSpawn(PlayerSpawnEvent event) {
	}

	public void onFluidRender(FluidRenderEvent event) {
	}

	public void onSlowDown(SlowDownEvent event) {
	}
	
	public void onLadderClimb(LadderClimbEvent event) {
	}

}