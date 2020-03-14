package me.wavelength.baseclient.module.modules.movement;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;

public class TestModule extends Module {

	public TestModule() {
		super("TestModule", "This is a test module...", 0, Category.MOVEMENT, AntiCheat.AAC);
	}

	@Override
	public void setup() {
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUpdate(UpdateEvent event) {
	}

	@Override
	public void onPacketReceived(PacketReceivedEvent event) {
		if(!(event.getPacket() instanceof S0CPacketSpawnPlayer))
			return;
		
		S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer) event.getPacket();
		
		System.out.println(mc.theWorld.getEntityByID(packet.getEntityID()) == null);
	}

}