package net.minecraft.client.resources.model;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public interface IBakedModel {
	List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_);

	List<BakedQuad> getGeneralQuads();

	boolean isAmbientOcclusion();

	boolean isGui3d();

	boolean isBuiltInRenderer();

	TextureAtlasSprite getParticleTexture();

	ItemCameraTransforms getItemCameraTransforms();
}
