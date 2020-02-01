package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.entity.layers.LayerEndermanEyes;
import net.minecraft.client.renderer.entity.layers.LayerHeldBlock;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;

public class RenderEnderman extends RenderLiving<EntityEnderman>
{
    private static final ResourceLocation endermanTextures = new ResourceLocation("textures/entity/enderman/enderman.png");

    /** The model of the enderman */
    private ModelEnderman endermanModel;
    private Random rnd = new Random();

    public RenderEnderman(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelEnderman(0.0F), 0.5F);
        this.endermanModel = (ModelEnderman)super.mainModel;
        this.addLayer(new LayerEndermanEyes(this));
        this.addLayer(new LayerHeldBlock(this));
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    public void doRender(EntityEnderman entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.endermanModel.isCarrying = entity.getHeldBlockState().getBlock().getMaterial() != Material.air;
        this.endermanModel.isAttacking = entity.isScreaming();

        if (entity.isScreaming())
        {
            double d0 = 0.02D;
            x += this.rnd.nextGaussian() * d0;
            z += this.rnd.nextGaussian() * d0;
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityEnderman entity)
    {
        return endermanTextures;
    }
}
