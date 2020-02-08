package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelMinecart extends ModelBase {
	public ModelRenderer[] sideModels = new ModelRenderer[7];

	public ModelMinecart() {
		this.sideModels[0] = new ModelRenderer(this, 0, 10);
		this.sideModels[1] = new ModelRenderer(this, 0, 0);
		this.sideModels[2] = new ModelRenderer(this, 0, 0);
		this.sideModels[3] = new ModelRenderer(this, 0, 0);
		this.sideModels[4] = new ModelRenderer(this, 0, 0);
		this.sideModels[5] = new ModelRenderer(this, 44, 10);
		int i = 20;
		int j = 8;
		int k = 16;
		int l = 4;
		this.sideModels[0].addBox((float) (-i / 2), (float) (-k / 2), -1.0F, i, k, 2, 0.0F);
		this.sideModels[0].setRotationPoint(0.0F, (float) l, 0.0F);
		this.sideModels[5].addBox((float) (-i / 2 + 1), (float) (-k / 2 + 1), -1.0F, i - 2, k - 2, 1, 0.0F);
		this.sideModels[5].setRotationPoint(0.0F, (float) l, 0.0F);
		this.sideModels[1].addBox((float) (-i / 2 + 2), (float) (-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.sideModels[1].setRotationPoint((float) (-i / 2 + 1), (float) l, 0.0F);
		this.sideModels[2].addBox((float) (-i / 2 + 2), (float) (-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.sideModels[2].setRotationPoint((float) (i / 2 - 1), (float) l, 0.0F);
		this.sideModels[3].addBox((float) (-i / 2 + 2), (float) (-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.sideModels[3].setRotationPoint(0.0F, (float) l, (float) (-k / 2 + 1));
		this.sideModels[4].addBox((float) (-i / 2 + 2), (float) (-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.sideModels[4].setRotationPoint(0.0F, (float) l, (float) (k / 2 - 1));
		this.sideModels[0].rotateAngleX = ((float) Math.PI / 2F);
		this.sideModels[1].rotateAngleY = ((float) Math.PI * 3F / 2F);
		this.sideModels[2].rotateAngleY = ((float) Math.PI / 2F);
		this.sideModels[3].rotateAngleY = (float) Math.PI;
		this.sideModels[5].rotateAngleX = -((float) Math.PI / 2F);
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
		this.sideModels[5].rotationPointY = 4.0F - p_78088_4_;

		for (int i = 0; i < 6; ++i) {
			this.sideModels[i].render(scale);
		}
	}
}
