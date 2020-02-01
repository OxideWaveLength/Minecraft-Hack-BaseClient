package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public interface ISound
{
    ResourceLocation getSoundLocation();

    boolean canRepeat();

    int getRepeatDelay();

    float getVolume();

    float getPitch();

    float getXPosF();

    float getYPosF();

    float getZPosF();

    ISound.AttenuationType getAttenuationType();

    public static enum AttenuationType
    {
        NONE(0),
        LINEAR(2);

        private final int type;

        private AttenuationType(int typeIn)
        {
            this.type = typeIn;
        }

        public int getTypeInt()
        {
            return this.type;
        }
    }
}
