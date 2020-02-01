package net.minecraft.client.audio;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class MovingSoundMinecartRiding extends MovingSound
{
    private final EntityPlayer player;
    private final EntityMinecart minecart;

    public MovingSoundMinecartRiding(EntityPlayer playerRiding, EntityMinecart minecart)
    {
        super(new ResourceLocation("minecraft:minecart.inside"));
        this.player = playerRiding;
        this.minecart = minecart;
        this.attenuationType = ISound.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        if (!this.minecart.isDead && this.player.isRiding() && this.player.ridingEntity == this.minecart)
        {
            float f = MathHelper.sqrt_double(this.minecart.motionX * this.minecart.motionX + this.minecart.motionZ * this.minecart.motionZ);

            if ((double)f >= 0.01D)
            {
                this.volume = 0.0F + MathHelper.clamp_float(f, 0.0F, 1.0F) * 0.75F;
            }
            else
            {
                this.volume = 0.0F;
            }
        }
        else
        {
            this.donePlaying = true;
        }
    }
}
