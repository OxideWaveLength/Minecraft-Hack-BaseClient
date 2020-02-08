package net.minecraft.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.EnumWorldBlockLayer;

public class VboRenderList extends ChunkRenderContainer {
	public void renderChunkLayer(EnumWorldBlockLayer layer) {
		if (this.initialized) {
			for (RenderChunk renderchunk : this.renderChunks) {
				VertexBuffer vertexbuffer = renderchunk.getVertexBufferByLayer(layer.ordinal());
				GlStateManager.pushMatrix();
				this.preRenderChunk(renderchunk);
				renderchunk.multModelviewMatrix();
				vertexbuffer.bindBuffer();
				this.setupArrayPointers();
				vertexbuffer.drawArrays(7);
				GlStateManager.popMatrix();
			}

			OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
			GlStateManager.resetColor();
			this.renderChunks.clear();
		}
	}

	private void setupArrayPointers() {
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 28, 0L);
		GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 28, 12L);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 28, 16L);
		OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glTexCoordPointer(2, GL11.GL_SHORT, 28, 24L);
		OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
	}
}
