package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelEnderman extends ModelBiped
{
    /** Is the enderman carrying a block? */
    public boolean isCarrying;

    /** Is the enderman attacking an entity? */
    public boolean isAttacking;

    public ModelEnderman(float p_i46305_1_)
    {
        super(0.0F, -14.0F, 64, 32);
        float f = -14.0F;
        this.bipedHeadwear = new ModelRenderer(this, 0, 16);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i46305_1_ - 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + f, 0.0F);
        this.bipedBody = new ModelRenderer(this, 32, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, p_i46305_1_);
        this.bipedBody.setRotationPoint(0.0F, 0.0F + f, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 56, 0);
        this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, p_i46305_1_);
        this.bipedRightArm.setRotationPoint(-3.0F, 2.0F + f, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 56, 0);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, p_i46305_1_);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + f, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 56, 0);
        this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, p_i46305_1_);
        this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F + f, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 56, 0);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, p_i46305_1_);
        this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F + f, 0.0F);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn)
    {
        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entityIn);
        this.bipedHead.showModel = true;
        float f = -14.0F;
        this.bipedBody.rotateAngleX = 0.0F;
        this.bipedBody.rotationPointY = f;
        this.bipedBody.rotationPointZ = -0.0F;
        this.bipedRightLeg.rotateAngleX -= 0.0F;
        this.bipedLeftLeg.rotateAngleX -= 0.0F;
        this.bipedRightArm.rotateAngleX = (float)((double)this.bipedRightArm.rotateAngleX * 0.5D);
        this.bipedLeftArm.rotateAngleX = (float)((double)this.bipedLeftArm.rotateAngleX * 0.5D);
        this.bipedRightLeg.rotateAngleX = (float)((double)this.bipedRightLeg.rotateAngleX * 0.5D);
        this.bipedLeftLeg.rotateAngleX = (float)((double)this.bipedLeftLeg.rotateAngleX * 0.5D);
        float f1 = 0.4F;

        if (this.bipedRightArm.rotateAngleX > f1)
        {
            this.bipedRightArm.rotateAngleX = f1;
        }

        if (this.bipedLeftArm.rotateAngleX > f1)
        {
            this.bipedLeftArm.rotateAngleX = f1;
        }

        if (this.bipedRightArm.rotateAngleX < -f1)
        {
            this.bipedRightArm.rotateAngleX = -f1;
        }

        if (this.bipedLeftArm.rotateAngleX < -f1)
        {
            this.bipedLeftArm.rotateAngleX = -f1;
        }

        if (this.bipedRightLeg.rotateAngleX > f1)
        {
            this.bipedRightLeg.rotateAngleX = f1;
        }

        if (this.bipedLeftLeg.rotateAngleX > f1)
        {
            this.bipedLeftLeg.rotateAngleX = f1;
        }

        if (this.bipedRightLeg.rotateAngleX < -f1)
        {
            this.bipedRightLeg.rotateAngleX = -f1;
        }

        if (this.bipedLeftLeg.rotateAngleX < -f1)
        {
            this.bipedLeftLeg.rotateAngleX = -f1;
        }

        if (this.isCarrying)
        {
            this.bipedRightArm.rotateAngleX = -0.5F;
            this.bipedLeftArm.rotateAngleX = -0.5F;
            this.bipedRightArm.rotateAngleZ = 0.05F;
            this.bipedLeftArm.rotateAngleZ = -0.05F;
        }

        this.bipedRightArm.rotationPointZ = 0.0F;
        this.bipedLeftArm.rotationPointZ = 0.0F;
        this.bipedRightLeg.rotationPointZ = 0.0F;
        this.bipedLeftLeg.rotationPointZ = 0.0F;
        this.bipedRightLeg.rotationPointY = 9.0F + f;
        this.bipedLeftLeg.rotationPointY = 9.0F + f;
        this.bipedHead.rotationPointZ = -0.0F;
        this.bipedHead.rotationPointY = f + 1.0F;
        this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX;
        this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY;
        this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ;
        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ;

        if (this.isAttacking)
        {
            float f2 = 1.0F;
            this.bipedHead.rotationPointY -= f2 * 5.0F;
        }
    }
}
