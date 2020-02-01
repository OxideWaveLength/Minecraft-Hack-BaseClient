package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;

public class ModelSheep2 extends ModelQuadruped
{
    private float headRotationAngleX;

    public ModelSheep2()
    {
        super(12, 0.0F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);
        this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
        this.body = new ModelRenderer(this, 28, 8);
        this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F);
        this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime)
    {
        super.setLivingAnimations(entitylivingbaseIn, p_78086_2_, p_78086_3_, partialTickTime);
        this.head.rotationPointY = 6.0F + ((EntitySheep)entitylivingbaseIn).getHeadRotationPointY(partialTickTime) * 9.0F;
        this.headRotationAngleX = ((EntitySheep)entitylivingbaseIn).getHeadRotationAngleX(partialTickTime);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn)
    {
        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entityIn);
        this.head.rotateAngleX = this.headRotationAngleX;
    }
}
