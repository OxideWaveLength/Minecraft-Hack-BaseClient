package net.minecraft.client.renderer.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;

public class VertexBuffer {
	private int glBufferId;
	private final VertexFormat vertexFormat;
	private int count;

	public VertexBuffer(VertexFormat vertexFormatIn) {
		this.vertexFormat = vertexFormatIn;
		this.glBufferId = OpenGlHelper.glGenBuffers();
	}

	public void bindBuffer() {
		OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
	}

	public void func_181722_a(ByteBuffer p_181722_1_) {
		this.bindBuffer();
		OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, p_181722_1_, 35044);
		this.unbindBuffer();
		this.count = p_181722_1_.limit() / this.vertexFormat.getNextOffset();
	}

	public void drawArrays(int mode) {
		GL11.glDrawArrays(mode, 0, this.count);
	}

	public void unbindBuffer() {
		OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
	}

	public void deleteGlBuffers() {
		if (this.glBufferId >= 0) {
			OpenGlHelper.glDeleteBuffers(this.glBufferId);
			this.glBufferId = -1;
		}
	}
}
