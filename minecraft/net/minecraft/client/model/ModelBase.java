package net.minecraft.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public abstract class ModelBase
{
    public float swingProgress;
    public boolean isRiding;
    public boolean isChild = true;
    public List<ModelRenderer> boxList = Lists.<ModelRenderer>newArrayList();
    private Map<String, TextureOffset> modelTextureMap = Maps.<String, TextureOffset>newHashMap();
    public int textureWidth = 64;
    public int textureHeight = 32;

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn)
    {
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime)
    {
    }

    public ModelRenderer getRandomModelBox(Random rand)
    {
        return (ModelRenderer)this.boxList.get(rand.nextInt(this.boxList.size()));
    }

    protected void setTextureOffset(String partName, int x, int y)
    {
        this.modelTextureMap.put(partName, new TextureOffset(x, y));
    }

    public TextureOffset getTextureOffset(String partName)
    {
        return (TextureOffset)this.modelTextureMap.get(partName);
    }

    /**
     * Copies the angles from one object to another. This is used when objects should stay aligned with each other, like
     * the hair over a players head.
     */
    public static void copyModelAngles(ModelRenderer source, ModelRenderer dest)
    {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
    }

    public void setModelAttributes(ModelBase model)
    {
        this.swingProgress = model.swingProgress;
        this.isRiding = model.isRiding;
        this.isChild = model.isChild;
    }
}
