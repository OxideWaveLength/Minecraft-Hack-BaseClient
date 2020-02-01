package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;

public class AnimationMetadataSection implements IMetadataSection
{
    private final List<AnimationFrame> animationFrames;
    private final int frameWidth;
    private final int frameHeight;
    private final int frameTime;
    private final boolean interpolate;

    public AnimationMetadataSection(List<AnimationFrame> p_i46088_1_, int p_i46088_2_, int p_i46088_3_, int p_i46088_4_, boolean p_i46088_5_)
    {
        this.animationFrames = p_i46088_1_;
        this.frameWidth = p_i46088_2_;
        this.frameHeight = p_i46088_3_;
        this.frameTime = p_i46088_4_;
        this.interpolate = p_i46088_5_;
    }

    public int getFrameHeight()
    {
        return this.frameHeight;
    }

    public int getFrameWidth()
    {
        return this.frameWidth;
    }

    public int getFrameCount()
    {
        return this.animationFrames.size();
    }

    public int getFrameTime()
    {
        return this.frameTime;
    }

    public boolean isInterpolate()
    {
        return this.interpolate;
    }

    private AnimationFrame getAnimationFrame(int p_130072_1_)
    {
        return (AnimationFrame)this.animationFrames.get(p_130072_1_);
    }

    public int getFrameTimeSingle(int p_110472_1_)
    {
        AnimationFrame animationframe = this.getAnimationFrame(p_110472_1_);
        return animationframe.hasNoTime() ? this.frameTime : animationframe.getFrameTime();
    }

    public boolean frameHasTime(int p_110470_1_)
    {
        return !((AnimationFrame)this.animationFrames.get(p_110470_1_)).hasNoTime();
    }

    public int getFrameIndex(int p_110468_1_)
    {
        return ((AnimationFrame)this.animationFrames.get(p_110468_1_)).getFrameIndex();
    }

    public Set<Integer> getFrameIndexSet()
    {
        Set<Integer> set = Sets.<Integer>newHashSet();

        for (AnimationFrame animationframe : this.animationFrames)
        {
            set.add(Integer.valueOf(animationframe.getFrameIndex()));
        }

        return set;
    }
}
