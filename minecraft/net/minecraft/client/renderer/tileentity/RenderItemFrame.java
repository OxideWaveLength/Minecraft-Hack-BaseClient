package net.minecraft.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;

public class RenderItemFrame extends Render<EntityItemFrame> {
	private static final ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
	private final Minecraft mc = Minecraft.getMinecraft();
	private final ModelResourceLocation itemFrameModel = new ModelResourceLocation("item_frame", "normal");
	private final ModelResourceLocation mapModel = new ModelResourceLocation("item_frame", "map");
	private RenderItem itemRenderer;

	public RenderItemFrame(RenderManager renderManagerIn, RenderItem itemRendererIn) {
		super(renderManagerIn);
		this.itemRenderer = itemRendererIn;
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker function
	 * which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity>) and this method has signature public void
	 * doRender(T entity, double d, double d1, double d2, float f, float f1). But
	 * JAD is pre 1.5 so doe
	 */
	public void doRender(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		BlockPos blockpos = entity.getHangingPosition();
		double d0 = (double) blockpos.getX() - entity.posX + x;
		double d1 = (double) blockpos.getY() - entity.posY + y;
		double d2 = (double) blockpos.getZ() - entity.posZ + z;
		GlStateManager.translate(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D);
		GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
		this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
		ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
		IBakedModel ibakedmodel;

		if (entity.getDisplayedItem() != null && entity.getDisplayedItem().getItem() == Items.filled_map) {
			ibakedmodel = modelmanager.getModel(this.mapModel);
		} else {
			ibakedmodel = modelmanager.getModel(this.itemFrameModel);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
		GlStateManager.translate(0.0F, 0.0F, 0.4375F);
		this.renderItem(entity);
		GlStateManager.popMatrix();
		this.renderName(entity, x + (double) ((float) entity.facingDirection.getFrontOffsetX() * 0.3F), y - 0.25D, z + (double) ((float) entity.facingDirection.getFrontOffsetZ() * 0.3F));
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless
	 * you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityItemFrame entity) {
		return null;
	}

	private void renderItem(EntityItemFrame itemFrame) {
		ItemStack itemstack = itemFrame.getDisplayedItem();

		if (itemstack != null) {
			EntityItem entityitem = new EntityItem(itemFrame.worldObj, 0.0D, 0.0D, 0.0D, itemstack);
			Item item = entityitem.getEntityItem().getItem();
			entityitem.getEntityItem().stackSize = 1;
			entityitem.hoverStart = 0.0F;
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			int i = itemFrame.getRotation();

			if (item == Items.filled_map) {
				i = i % 4 * 2;
			}

			GlStateManager.rotate((float) i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);

			if (item == Items.filled_map) {
				this.renderManager.renderEngine.bindTexture(mapBackgroundTextures);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				float f = 0.0078125F;
				GlStateManager.scale(f, f, f);
				GlStateManager.translate(-64.0F, -64.0F, 0.0F);
				MapData mapdata = Items.filled_map.getMapData(entityitem.getEntityItem(), itemFrame.worldObj);
				GlStateManager.translate(0.0F, 0.0F, -1.0F);

				if (mapdata != null) {
					this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
				}
			} else {
				TextureAtlasSprite textureatlassprite = null;

				if (item == Items.compass) {
					textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite(TextureCompass.field_176608_l);
					this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

					if (textureatlassprite instanceof TextureCompass) {
						TextureCompass texturecompass = (TextureCompass) textureatlassprite;
						double d0 = texturecompass.currentAngle;
						double d1 = texturecompass.angleDelta;
						texturecompass.currentAngle = 0.0D;
						texturecompass.angleDelta = 0.0D;
						texturecompass.updateCompass(itemFrame.worldObj, itemFrame.posX, itemFrame.posZ, (double) MathHelper.wrapAngleTo180_float((float) (180 + itemFrame.facingDirection.getHorizontalIndex() * 90)), false, true);
						texturecompass.currentAngle = d0;
						texturecompass.angleDelta = d1;
					} else {
						textureatlassprite = null;
					}
				}

				GlStateManager.scale(0.5F, 0.5F, 0.5F);

				if (!this.itemRenderer.shouldRenderItemIn3D(entityitem.getEntityItem()) || item instanceof ItemSkull) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.pushAttrib();
				RenderHelper.enableStandardItemLighting();
				this.itemRenderer.func_181564_a(entityitem.getEntityItem(), ItemCameraTransforms.TransformType.FIXED);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.popAttrib();

				if (textureatlassprite != null && textureatlassprite.getFrameCount() > 0) {
					textureatlassprite.updateAnimation();
				}
			}

			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
	}

	protected void renderName(EntityItemFrame entity, double x, double y, double z) {
		if (Minecraft.isGuiEnabled() && entity.getDisplayedItem() != null && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity) {
			float f = 1.6F;
			float f1 = 0.016666668F * f;
			double d0 = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
			float f2 = entity.isSneaking() ? 32.0F : 64.0F;

			if (d0 < (double) (f2 * f2)) {
				String s = entity.getDisplayedItem().getDisplayName();

				if (entity.isSneaking()) {
					FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
					GlStateManager.pushMatrix();
					GlStateManager.translate((float) x + 0.0F, (float) y + entity.height + 0.5F, (float) z);
					GL11.glNormal3f(0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
					GlStateManager.scale(-f1, -f1, f1);
					GlStateManager.disableLighting();
					GlStateManager.translate(0.0F, 0.25F / f1, 0.0F);
					GlStateManager.depthMask(false);
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(770, 771);
					Tessellator tessellator = Tessellator.getInstance();
					WorldRenderer worldrenderer = tessellator.getWorldRenderer();
					int i = fontrenderer.getStringWidth(s) / 2;
					GlStateManager.disableTexture2D();
					worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
					worldrenderer.pos((double) (-i - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
					worldrenderer.pos((double) (-i - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
					worldrenderer.pos((double) (i + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
					worldrenderer.pos((double) (i + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
					tessellator.draw();
					GlStateManager.enableTexture2D();
					GlStateManager.depthMask(true);
					fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 553648127);
					GlStateManager.enableLighting();
					GlStateManager.disableBlend();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.popMatrix();
				} else {
					this.renderLivingLabel(entity, s, x, y, z, 64);
				}
			}
		}
	}
}
