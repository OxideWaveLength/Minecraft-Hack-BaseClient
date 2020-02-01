package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;

public class ModelMagmaCube extends ModelBase
{
    ModelRenderer[] segments = new ModelRenderer[8];
    ModelRenderer core;

    public ModelMagmaCube()
    {
        for (int i = 0; i < this.segments.length; ++i)
        {
            int j = 0;
            int k = i;

            if (i == 2)
            {
                j = 24;
                k = 10;
            }
            else if (i == 3)
            {
                j = 24;
                k = 19;
            }

            this.segments[i] = new ModelRenderer(this, j, k);
            this.segments[i].addBox(-4.0F, (float)(16 + i), -4.0F, 8, 1, 8);
        }

        this.core = new ModelRenderer(this, 0, 16);
        this.core.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime)
    {
        EntityMagmaCube entitymagmacube = (EntityMagmaCube)entitylivingbaseIn;
        float f = entitymagmacube.prevSquishFactor + (entitymagmacube.squishFactor - entitymagmacube.prevSquishFactor) * partialTickTime;

        if (f < 0.0F)
        {
            f = 0.0F;
        }

        for (int i = 0; i < this.segments.length; ++i)
        {
            this.segments[i].rotationPointY = (float)(-(4 - i)) * f * 1.7F;
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.core.render(scale);

        for (int i = 0; i < this.segments.length; ++i)
        {
            this.segments[i].render(scale);
        }
    }
}
