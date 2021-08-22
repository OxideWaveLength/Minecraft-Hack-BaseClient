package net.minecraft.client.audio;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;

public class SoundRegistry extends RegistrySimple<ResourceLocation, SoundEventAccessorComposite> {
	private Map<ResourceLocation, SoundEventAccessorComposite> soundRegistry;

	protected Map<ResourceLocation, SoundEventAccessorComposite> createUnderlyingMap() {
		this.soundRegistry = Maps.<ResourceLocation, SoundEventAccessorComposite>newHashMap();
		return this.soundRegistry;
	}

	public void registerSound(SoundEventAccessorComposite p_148762_1_) {
		this.putObject(p_148762_1_.getSoundEventLocation(), p_148762_1_);
	}

	/**
	 * Reset the underlying sound map (Called on resource manager reload)
	 */
	public void clearMap() {
		this.soundRegistry.clear();
	}
}
