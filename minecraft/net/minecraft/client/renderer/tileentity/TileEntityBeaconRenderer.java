package net.minecraft.client.renderer.tileentity;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer<TileEntityBeacon> {
	private static final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");

	public void renderTileEntityAt(TileEntityBeacon te, double x, double y, double z, float partialTicks, int destroyStage) {
		float f = te.shouldBeamRender();
		GlStateManager.alphaFunc(516, 0.1F);

		if (f > 0.0F) {
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			GlStateManager.disableFog();
			List<TileEntityBeacon.BeamSegment> list = te.getBeamSegments();
			int i = 0;

			for (int j = 0; j < list.size(); ++j) {
				TileEntityBeacon.BeamSegment tileentitybeacon$beamsegment = (TileEntityBeacon.BeamSegment) list.get(j);
				int k = i + tileentitybeacon$beamsegment.getHeight();
				this.bindTexture(beaconBeam);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
				GlStateManager.disableLighting();
				GlStateManager.disableCull();
				GlStateManager.disableBlend();
				GlStateManager.depthMask(true);
				GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
				double d0 = (double) te.getWorld().getTotalWorldTime() + (double) partialTicks;
				double d1 = MathHelper.func_181162_h(-d0 * 0.2D - (double) MathHelper.floor_double(-d0 * 0.1D));
				float f1 = tileentitybeacon$beamsegment.getColors()[0];
				float f2 = tileentitybeacon$beamsegment.getColors()[1];
				float f3 = tileentitybeacon$beamsegment.getColors()[2];
				double d2 = d0 * 0.025D * -1.5D;
				double d3 = 0.2D;
				double d4 = 0.5D + Math.cos(d2 + 2.356194490192345D) * 0.2D;
				double d5 = 0.5D + Math.sin(d2 + 2.356194490192345D) * 0.2D;
				double d6 = 0.5D + Math.cos(d2 + (Math.PI / 4D)) * 0.2D;
				double d7 = 0.5D + Math.sin(d2 + (Math.PI / 4D)) * 0.2D;
				double d8 = 0.5D + Math.cos(d2 + 3.9269908169872414D) * 0.2D;
				double d9 = 0.5D + Math.sin(d2 + 3.9269908169872414D) * 0.2D;
				double d10 = 0.5D + Math.cos(d2 + 5.497787143782138D) * 0.2D;
				double d11 = 0.5D + Math.sin(d2 + 5.497787143782138D) * 0.2D;
				double d12 = 0.0D;
				double d13 = 1.0D;
				double d14 = -1.0D + d1;
				double d15 = (double) ((float) tileentitybeacon$beamsegment.getHeight() * f) * 2.5D + d14;
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				worldrenderer.pos(x + d4, y + (double) k, z + d5).tex(1.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d4, y + (double) i, z + d5).tex(1.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d6, y + (double) i, z + d7).tex(0.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d6, y + (double) k, z + d7).tex(0.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d10, y + (double) k, z + d11).tex(1.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d10, y + (double) i, z + d11).tex(1.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d8, y + (double) i, z + d9).tex(0.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d8, y + (double) k, z + d9).tex(0.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d6, y + (double) k, z + d7).tex(1.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d6, y + (double) i, z + d7).tex(1.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d10, y + (double) i, z + d11).tex(0.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d10, y + (double) k, z + d11).tex(0.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d8, y + (double) k, z + d9).tex(1.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d8, y + (double) i, z + d9).tex(1.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d4, y + (double) i, z + d5).tex(0.0D, d14).color(f1, f2, f3, 1.0F).endVertex();
				worldrenderer.pos(x + d4, y + (double) k, z + d5).tex(0.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
				tessellator.draw();
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.depthMask(false);
				d2 = 0.2D;
				d3 = 0.2D;
				d4 = 0.8D;
				d5 = 0.2D;
				d6 = 0.2D;
				d7 = 0.8D;
				d8 = 0.8D;
				d9 = 0.8D;
				d10 = 0.0D;
				d11 = 1.0D;
				d12 = -1.0D + d1;
				d13 = (double) ((float) tileentitybeacon$beamsegment.getHeight() * f) + d12;
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				worldrenderer.pos(x + 0.2D, y + (double) k, z + 0.2D).tex(1.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.2D, y + (double) i, z + 0.2D).tex(1.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.8D, y + (double) i, z + 0.2D).tex(0.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.8D, y + (double) k, z + 0.2D).tex(0.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.8D, y + (double) k, z + 0.8D).tex(1.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.8D, y + (double) i, z + 0.8D).tex(1.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.2D, y + (double) i, z + 0.8D).tex(0.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.2D, y + (double) k, z + 0.8D).tex(0.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.8D, y + (double) k, z + 0.2D).tex(1.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.8D, y + (double) i, z + 0.2D).tex(1.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.8D, y + (double) i, z + 0.8D).tex(0.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.8D, y + (double) k, z + 0.8D).tex(0.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.2D, y + (double) k, z + 0.8D).tex(1.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.2D, y + (double) i, z + 0.8D).tex(1.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.2D, y + (double) i, z + 0.2D).tex(0.0D, d12).color(f1, f2, f3, 0.125F).endVertex();
				worldrenderer.pos(x + 0.2D, y + (double) k, z + 0.2D).tex(0.0D, d13).color(f1, f2, f3, 0.125F).endVertex();
				tessellator.draw();
				GlStateManager.enableLighting();
				GlStateManager.enableTexture2D();
				GlStateManager.depthMask(true);
				i = k;
			}

			GlStateManager.enableFog();
		}
	}

	public boolean func_181055_a() {
		return true;
	}
}
