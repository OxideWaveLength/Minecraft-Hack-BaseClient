package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

public class LayerSheepWool implements LayerRenderer<EntitySheep> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
	private final RenderSheep sheepRenderer;
	private final ModelSheep1 sheepModel = new ModelSheep1();

	public LayerSheepWool(RenderSheep sheepRendererIn) {
		this.sheepRenderer = sheepRendererIn;
	}

	public void doRenderLayer(EntitySheep entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if (!entitylivingbaseIn.getSheared() && !entitylivingbaseIn.isInvisible()) {
			this.sheepRenderer.bindTexture(TEXTURE);

			if (entitylivingbaseIn.hasCustomName() && "jeb_".equals(entitylivingbaseIn.getCustomNameTag())) {
				int i1 = 25;
				int i = entitylivingbaseIn.ticksExisted / 25 + entitylivingbaseIn.getEntityId();
				int j = EnumDyeColor.values().length;
				int k = i % j;
				int l = (i + 1) % j;
				float f = ((float) (entitylivingbaseIn.ticksExisted % 25) + partialTicks) / 25.0F;
				float[] afloat1 = EntitySheep.func_175513_a(EnumDyeColor.byMetadata(k));
				float[] afloat2 = EntitySheep.func_175513_a(EnumDyeColor.byMetadata(l));
				GlStateManager.color(afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f);
			} else {
				float[] afloat = EntitySheep.func_175513_a(entitylivingbaseIn.getFleeceColor());
				GlStateManager.color(afloat[0], afloat[1], afloat[2]);
			}

			this.sheepModel.setModelAttributes(this.sheepRenderer.getMainModel());
			this.sheepModel.setLivingAnimations(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks);
			this.sheepModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
		}
	}

	public boolean shouldCombineTextures() {
		return true;
	}
}
