package net.minecraft.client.renderer;

import net.minecraft.client.renderer.texture.Stitcher;

public class StitcherException extends RuntimeException
{
    private final Stitcher.Holder holder;

    public StitcherException(Stitcher.Holder p_i2344_1_, String p_i2344_2_)
    {
        super(p_i2344_2_);
        this.holder = p_i2344_1_;
    }
}
