package net.minecraft.client.audio;

import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.ResourceLocation;

public class GuardianSound extends MovingSound
{
    private final EntityGuardian guardian;

    public GuardianSound(EntityGuardian guardian)
    {
        super(new ResourceLocation("minecraft:mob.guardian.attack"));
        this.guardian = guardian;
        this.attenuationType = ISound.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        if (!this.guardian.isDead && this.guardian.hasTargetedEntity())
        {
            this.xPosF = (float)this.guardian.posX;
            this.yPosF = (float)this.guardian.posY;
            this.zPosF = (float)this.guardian.posZ;
            float f = this.guardian.func_175477_p(0.0F);
            this.volume = 0.0F + 1.0F * f * f;
            this.pitch = 0.7F + 0.5F * f;
        }
        else
        {
            this.donePlaying = true;
        }
    }
}
