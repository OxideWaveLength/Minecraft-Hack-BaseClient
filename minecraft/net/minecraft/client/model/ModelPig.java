package net.minecraft.client.model;

public class ModelPig extends ModelQuadruped
{
    public ModelPig()
    {
        this(0.0F);
    }

    public ModelPig(float p_i1151_1_)
    {
        super(6, p_i1151_1_);
        this.head.setTextureOffset(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1, p_i1151_1_);
        this.childYOffset = 4.0F;
    }
}
