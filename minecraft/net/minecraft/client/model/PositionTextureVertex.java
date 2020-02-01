package net.minecraft.client.model;

import net.minecraft.util.Vec3;

public class PositionTextureVertex
{
    public Vec3 vector3D;
    public float texturePositionX;
    public float texturePositionY;

    public PositionTextureVertex(float p_i1158_1_, float p_i1158_2_, float p_i1158_3_, float p_i1158_4_, float p_i1158_5_)
    {
        this(new Vec3((double)p_i1158_1_, (double)p_i1158_2_, (double)p_i1158_3_), p_i1158_4_, p_i1158_5_);
    }

    public PositionTextureVertex setTexturePosition(float p_78240_1_, float p_78240_2_)
    {
        return new PositionTextureVertex(this, p_78240_1_, p_78240_2_);
    }

    public PositionTextureVertex(PositionTextureVertex textureVertex, float texturePositionXIn, float texturePositionYIn)
    {
        this.vector3D = textureVertex.vector3D;
        this.texturePositionX = texturePositionXIn;
        this.texturePositionY = texturePositionYIn;
    }

    public PositionTextureVertex(Vec3 vector3DIn, float texturePositionXIn, float texturePositionYIn)
    {
        this.vector3D = vector3DIn;
        this.texturePositionX = texturePositionXIn;
        this.texturePositionY = texturePositionYIn;
    }
}
