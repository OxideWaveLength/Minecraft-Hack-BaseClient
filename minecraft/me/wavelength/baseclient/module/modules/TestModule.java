package me.wavelength.baseclient.module.modules;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.Value;

public class TestModule extends Module {

	//Show you everthing
	
	public Value<Boolean> booleanValue = new Value("BooleanTest", true);
	public Value<Integer> intValue = new Value("IntTest", 1);
	public Value<Double> doubleValue = new Value("DoubleTest", 1.2D);
	public Value<Float> floatValue = new Value("FloatTest", 1.2F);
	
	public TestModule() {
		super("TestModule", "This is a test module...", 0, Category.Movement, AntiCheat.AAC);
	}

	@Override
	public void setup() {
	}

	@Override
	public void onEnable() {
		//Get values;
		this.getValue("BooleanTest");
		this.getValue("IntTest");
		this.getValue("DoubleTest");
		this.getValue("FloatTest");
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUpdate(UpdateEvent event) {
	}

	@Override
	public void onPacketReceived(PacketReceivedEvent event) {
	}

}