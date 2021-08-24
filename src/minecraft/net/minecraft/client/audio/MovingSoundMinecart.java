package net.minecraft.client.audio;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class MovingSoundMinecart extends MovingSound {
	private final EntityMinecart minecart;
	private float distance = 0.0F;

	public MovingSoundMinecart(EntityMinecart minecartIn) {
		super(new ResourceLocation("minecraft:minecart.base"));
		this.minecart = minecartIn;
		this.repeat = true;
		this.repeatDelay = 0;
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update() {
		if (this.minecart.isDead) {
			this.donePlaying = true;
		} else {
			this.xPosF = (float) this.minecart.posX;
			this.yPosF = (float) this.minecart.posY;
			this.zPosF = (float) this.minecart.posZ;
			float f = MathHelper.sqrt_double(this.minecart.motionX * this.minecart.motionX + this.minecart.motionZ * this.minecart.motionZ);

			if ((double) f >= 0.01D) {
				this.distance = MathHelper.clamp_float(this.distance + 0.0025F, 0.0F, 1.0F);
				this.volume = 0.0F + MathHelper.clamp_float(f, 0.0F, 0.5F) * 0.7F;
			} else {
				this.distance = 0.0F;
				this.volume = 0.0F;
			}
		}
	}
}
