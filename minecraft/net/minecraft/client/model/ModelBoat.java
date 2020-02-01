package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelBoat extends ModelBase
{
    public ModelRenderer[] boatSides = new ModelRenderer[5];

    public ModelBoat()
    {
        this.boatSides[0] = new ModelRenderer(this, 0, 8);
        this.boatSides[1] = new ModelRenderer(this, 0, 0);
        this.boatSides[2] = new ModelRenderer(this, 0, 0);
        this.boatSides[3] = new ModelRenderer(this, 0, 0);
        this.boatSides[4] = new ModelRenderer(this, 0, 0);
        int i = 24;
        int j = 6;
        int k = 20;
        int l = 4;
        this.boatSides[0].addBox((float)(-i / 2), (float)(-k / 2 + 2), -3.0F, i, k - 4, 4, 0.0F);
        this.boatSides[0].setRotationPoint(0.0F, (float)l, 0.0F);
        this.boatSides[1].addBox((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
        this.boatSides[1].setRotationPoint((float)(-i / 2 + 1), (float)l, 0.0F);
        this.boatSides[2].addBox((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
        this.boatSides[2].setRotationPoint((float)(i / 2 - 1), (float)l, 0.0F);
        this.boatSides[3].addBox((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
        this.boatSides[3].setRotationPoint(0.0F, (float)l, (float)(-k / 2 + 1));
        this.boatSides[4].addBox((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
        this.boatSides[4].setRotationPoint(0.0F, (float)l, (float)(k / 2 - 1));
        this.boatSides[0].rotateAngleX = ((float)Math.PI / 2F);
        this.boatSides[1].rotateAngleY = ((float)Math.PI * 3F / 2F);
        this.boatSides[2].rotateAngleY = ((float)Math.PI / 2F);
        this.boatSides[3].rotateAngleY = (float)Math.PI;
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        for (int i = 0; i < 5; ++i)
        {
            this.boatSides[i].render(scale);
        }
    }
}
