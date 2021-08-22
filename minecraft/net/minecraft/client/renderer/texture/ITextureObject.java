package net.minecraft.client.renderer.texture;

import java.io.IOException;

import net.minecraft.client.resources.IResourceManager;

public interface ITextureObject {
	void setBlurMipmap(boolean p_174936_1_, boolean p_174936_2_);

	void restoreLastBlurMipmap();

	void loadTexture(IResourceManager resourceManager) throws IOException;

	int getGlTextureId();
}
