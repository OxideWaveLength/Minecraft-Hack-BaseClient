package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

public class ModelArmorStand extends ModelArmorStandArmor {
	public ModelRenderer standRightSide;
	public ModelRenderer standLeftSide;
	public ModelRenderer standWaist;
	public ModelRenderer standBase;

	public ModelArmorStand() {
		this(0.0F);
	}

	public ModelArmorStand(float p_i46306_1_) {
		super(p_i46306_1_, 64, 64);
		this.bipedHead = new ModelRenderer(this, 0, 0);
		this.bipedHead.addBox(-1.0F, -7.0F, -1.0F, 2, 7, 2, p_i46306_1_);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedBody = new ModelRenderer(this, 0, 26);
		this.bipedBody.addBox(-6.0F, 0.0F, -1.5F, 12, 3, 3, p_i46306_1_);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedRightArm = new ModelRenderer(this, 24, 0);
		this.bipedRightArm.addBox(-2.0F, -2.0F, -1.0F, 2, 12, 2, p_i46306_1_);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.bipedLeftArm = new ModelRenderer(this, 32, 16);
		this.bipedLeftArm.mirror = true;
		this.bipedLeftArm.addBox(0.0F, -2.0F, -1.0F, 2, 12, 2, p_i46306_1_);
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.bipedRightLeg = new ModelRenderer(this, 8, 0);
		this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, p_i46306_1_);
		this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.bipedLeftLeg = new ModelRenderer(this, 40, 16);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, p_i46306_1_);
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.standRightSide = new ModelRenderer(this, 16, 0);
		this.standRightSide.addBox(-3.0F, 3.0F, -1.0F, 2, 7, 2, p_i46306_1_);
		this.standRightSide.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.standRightSide.showModel = true;
		this.standLeftSide = new ModelRenderer(this, 48, 16);
		this.standLeftSide.addBox(1.0F, 3.0F, -1.0F, 2, 7, 2, p_i46306_1_);
		this.standLeftSide.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.standWaist = new ModelRenderer(this, 0, 48);
		this.standWaist.addBox(-4.0F, 10.0F, -1.0F, 8, 2, 2, p_i46306_1_);
		this.standWaist.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.standBase = new ModelRenderer(this, 0, 32);
		this.standBase.addBox(-6.0F, 11.0F, -6.0F, 12, 1, 12, p_i46306_1_);
		this.standBase.setRotationPoint(0.0F, 12.0F, 0.0F);
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used
	 * for animating the movement of arms and legs, where par1 represents the
	 * time(so that arms and legs swing back and forth) and par2 represents how
	 * "far" arms and legs can swing at most.
	 */
	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn) {
		super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entityIn);

		if (entityIn instanceof EntityArmorStand) {
			EntityArmorStand entityarmorstand = (EntityArmorStand) entityIn;
			this.bipedLeftArm.showModel = entityarmorstand.getShowArms();
			this.bipedRightArm.showModel = entityarmorstand.getShowArms();
			this.standBase.showModel = !entityarmorstand.hasNoBasePlate();
			this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
			this.standRightSide.rotateAngleX = 0.017453292F * entityarmorstand.getBodyRotation().getX();
			this.standRightSide.rotateAngleY = 0.017453292F * entityarmorstand.getBodyRotation().getY();
			this.standRightSide.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyRotation().getZ();
			this.standLeftSide.rotateAngleX = 0.017453292F * entityarmorstand.getBodyRotation().getX();
			this.standLeftSide.rotateAngleY = 0.017453292F * entityarmorstand.getBodyRotation().getY();
			this.standLeftSide.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyRotation().getZ();
			this.standWaist.rotateAngleX = 0.017453292F * entityarmorstand.getBodyRotation().getX();
			this.standWaist.rotateAngleY = 0.017453292F * entityarmorstand.getBodyRotation().getY();
			this.standWaist.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyRotation().getZ();
			float f = (entityarmorstand.getLeftLegRotation().getX() + entityarmorstand.getRightLegRotation().getX()) / 2.0F;
			float f1 = (entityarmorstand.getLeftLegRotation().getY() + entityarmorstand.getRightLegRotation().getY()) / 2.0F;
			float f2 = (entityarmorstand.getLeftLegRotation().getZ() + entityarmorstand.getRightLegRotation().getZ()) / 2.0F;
			this.standBase.rotateAngleX = 0.0F;
			this.standBase.rotateAngleY = 0.017453292F * -entityIn.rotationYaw;
			this.standBase.rotateAngleZ = 0.0F;
		}
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
		super.render(entityIn, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale);
		GlStateManager.pushMatrix();

		if (this.isChild) {
			float f = 2.0F;
			GlStateManager.scale(1.0F / f, 1.0F / f, 1.0F / f);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.standRightSide.render(scale);
			this.standLeftSide.render(scale);
			this.standWaist.render(scale);
			this.standBase.render(scale);
		} else {
			if (entityIn.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}

			this.standRightSide.render(scale);
			this.standLeftSide.render(scale);
			this.standWaist.render(scale);
			this.standBase.render(scale);
		}

		GlStateManager.popMatrix();
	}

	public void postRenderArm(float scale) {
		boolean flag = this.bipedRightArm.showModel;
		this.bipedRightArm.showModel = true;
		super.postRenderArm(scale);
		this.bipedRightArm.showModel = flag;
	}
}
