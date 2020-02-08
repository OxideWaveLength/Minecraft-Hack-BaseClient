package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public abstract class PositionedSound implements ISound {
	protected final ResourceLocation positionedSoundLocation;
	protected float volume = 1.0F;
	protected float pitch = 1.0F;
	protected float xPosF;
	protected float yPosF;
	protected float zPosF;
	protected boolean repeat = false;

	/** The number of ticks between repeating the sound */
	protected int repeatDelay = 0;
	protected ISound.AttenuationType attenuationType = ISound.AttenuationType.LINEAR;

	protected PositionedSound(ResourceLocation soundResource) {
		this.positionedSoundLocation = soundResource;
	}

	public ResourceLocation getSoundLocation() {
		return this.positionedSoundLocation;
	}

	public boolean canRepeat() {
		return this.repeat;
	}

	public int getRepeatDelay() {
		return this.repeatDelay;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getXPosF() {
		return this.xPosF;
	}

	public float getYPosF() {
		return this.yPosF;
	}

	public float getZPosF() {
		return this.zPosF;
	}

	public ISound.AttenuationType getAttenuationType() {
		return this.attenuationType;
	}
}
