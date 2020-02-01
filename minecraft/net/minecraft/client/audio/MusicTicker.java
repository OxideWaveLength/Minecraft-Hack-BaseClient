package net.minecraft.client.audio;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class MusicTicker implements ITickable
{
    private final Random rand = new Random();
    private final Minecraft mc;
    private ISound currentMusic;
    private int timeUntilNextMusic = 100;

    public MusicTicker(Minecraft mcIn)
    {
        this.mc = mcIn;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        MusicTicker.MusicType musicticker$musictype = this.mc.getAmbientMusicType();

        if (this.currentMusic != null)
        {
            if (!musicticker$musictype.getMusicLocation().equals(this.currentMusic.getSoundLocation()))
            {
                this.mc.getSoundHandler().stopSound(this.currentMusic);
                this.timeUntilNextMusic = MathHelper.getRandomIntegerInRange(this.rand, 0, musicticker$musictype.getMinDelay() / 2);
            }

            if (!this.mc.getSoundHandler().isSoundPlaying(this.currentMusic))
            {
                this.currentMusic = null;
                this.timeUntilNextMusic = Math.min(MathHelper.getRandomIntegerInRange(this.rand, musicticker$musictype.getMinDelay(), musicticker$musictype.getMaxDelay()), this.timeUntilNextMusic);
            }
        }

        if (this.currentMusic == null && this.timeUntilNextMusic-- <= 0)
        {
            this.func_181558_a(musicticker$musictype);
        }
    }

    public void func_181558_a(MusicTicker.MusicType p_181558_1_)
    {
        this.currentMusic = PositionedSoundRecord.create(p_181558_1_.getMusicLocation());
        this.mc.getSoundHandler().playSound(this.currentMusic);
        this.timeUntilNextMusic = Integer.MAX_VALUE;
    }

    public void func_181557_a()
    {
        if (this.currentMusic != null)
        {
            this.mc.getSoundHandler().stopSound(this.currentMusic);
            this.currentMusic = null;
            this.timeUntilNextMusic = 0;
        }
    }

    public static enum MusicType
    {
        MENU(new ResourceLocation("minecraft:music.menu"), 20, 600),
        GAME(new ResourceLocation("minecraft:music.game"), 12000, 24000),
        CREATIVE(new ResourceLocation("minecraft:music.game.creative"), 1200, 3600),
        CREDITS(new ResourceLocation("minecraft:music.game.end.credits"), Integer.MAX_VALUE, Integer.MAX_VALUE),
        NETHER(new ResourceLocation("minecraft:music.game.nether"), 1200, 3600),
        END_BOSS(new ResourceLocation("minecraft:music.game.end.dragon"), 0, 0),
        END(new ResourceLocation("minecraft:music.game.end"), 6000, 24000);

        private final ResourceLocation musicLocation;
        private final int minDelay;
        private final int maxDelay;

        private MusicType(ResourceLocation location, int minDelayIn, int maxDelayIn)
        {
            this.musicLocation = location;
            this.minDelay = minDelayIn;
            this.maxDelay = maxDelayIn;
        }

        public ResourceLocation getMusicLocation()
        {
            return this.musicLocation;
        }

        public int getMinDelay()
        {
            return this.minDelay;
        }

        public int getMaxDelay()
        {
            return this.maxDelay;
        }
    }
}
