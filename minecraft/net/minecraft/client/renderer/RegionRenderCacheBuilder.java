package net.minecraft.client.renderer;

import net.minecraft.util.EnumWorldBlockLayer;

public class RegionRenderCacheBuilder
{
    private final WorldRenderer[] worldRenderers = new WorldRenderer[EnumWorldBlockLayer.values().length];

    public RegionRenderCacheBuilder()
    {
        this.worldRenderers[EnumWorldBlockLayer.SOLID.ordinal()] = new WorldRenderer(2097152);
        this.worldRenderers[EnumWorldBlockLayer.CUTOUT.ordinal()] = new WorldRenderer(131072);
        this.worldRenderers[EnumWorldBlockLayer.CUTOUT_MIPPED.ordinal()] = new WorldRenderer(131072);
        this.worldRenderers[EnumWorldBlockLayer.TRANSLUCENT.ordinal()] = new WorldRenderer(262144);
    }

    public WorldRenderer getWorldRendererByLayer(EnumWorldBlockLayer layer)
    {
        return this.worldRenderers[layer.ordinal()];
    }

    public WorldRenderer getWorldRendererByLayerId(int id)
    {
        return this.worldRenderers[id];
    }
}
