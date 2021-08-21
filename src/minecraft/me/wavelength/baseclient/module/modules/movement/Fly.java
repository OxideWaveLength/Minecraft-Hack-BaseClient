package me.wavelength.baseclient.module.modules.movement;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class Fly extends Module {

	public Fly() {
		super("Fly", "Reach the outer skies!", Keyboard.KEY_F, Category.MOVEMENT, AntiCheat.VANILLA, AntiCheat.AAC);
	}

	private boolean isFlying;
	private boolean allowFlying;

	@Override
	public void setup() {
		moduleSettings.addDefault("speed", 1.0D);
	}

	@Override
	public void onEnable() {
		this.isFlying = mc.thePlayer.capabilities.isFlying;
		this.allowFlying = mc.thePlayer.capabilities.allowFlying;

		mc.thePlayer.capabilities.allowFlying = true;
	}

	@Override
	public void onDisable() {
		mc.thePlayer.capabilities.allowFlying = allowFlying;
		mc.thePlayer.capabilities.isFlying = isFlying;
	}

	@Override
	public void onUpdate(UpdateEvent event) {
		double speed = moduleSettings.getDouble("speed");

		speed = speed * 2;

		mc.thePlayer.capabilities.isFlying = true;
	}

}