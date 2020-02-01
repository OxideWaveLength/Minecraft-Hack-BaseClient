package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelEnderMite extends ModelBase
{
    private static final int[][] field_178716_a = new int[][] {{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
    private static final int[][] field_178714_b = new int[][] {{0, 0}, {0, 5}, {0, 14}, {0, 18}};
    private static final int field_178715_c = field_178716_a.length;
    private final ModelRenderer[] field_178713_d;

    public ModelEnderMite()
    {
        this.field_178713_d = new ModelRenderer[field_178715_c];
        float f = -3.5F;

        for (int i = 0; i < this.field_178713_d.length; ++i)
        {
            this.field_178713_d[i] = new ModelRenderer(this, field_178714_b[i][0], field_178714_b[i][1]);
            this.field_178713_d[i].addBox((float)field_178716_a[i][0] * -0.5F, 0.0F, (float)field_178716_a[i][2] * -0.5F, field_178716_a[i][0], field_178716_a[i][1], field_178716_a[i][2]);
            this.field_178713_d[i].setRotationPoint(0.0F, (float)(24 - field_178716_a[i][1]), f);

            if (i < this.field_178713_d.length - 1)
            {
                f += (float)(field_178716_a[i][2] + field_178716_a[i + 1][2]) * 0.5F;
            }
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);

        for (int i = 0; i < this.field_178713_d.length; ++i)
        {
            this.field_178713_d[i].render(scale);
        }
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn)
    {
        for (int i = 0; i < this.field_178713_d.length; ++i)
        {
            this.field_178713_d[i].rotateAngleY = MathHelper.cos(p_78087_3_ * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.01F * (float)(1 + Math.abs(i - 2));
            this.field_178713_d[i].rotationPointX = MathHelper.sin(p_78087_3_ * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.1F * (float)Math.abs(i - 2);
        }
    }
}
