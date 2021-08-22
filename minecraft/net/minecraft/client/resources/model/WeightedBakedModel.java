package net.minecraft.client.resources.model;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;

public class WeightedBakedModel implements IBakedModel {
	private final int totalWeight;
	private final List<WeightedBakedModel.MyWeighedRandomItem> models;
	private final IBakedModel baseModel;

	public WeightedBakedModel(List<WeightedBakedModel.MyWeighedRandomItem> p_i46073_1_) {
		this.models = p_i46073_1_;
		this.totalWeight = WeightedRandom.getTotalWeight(p_i46073_1_);
		this.baseModel = ((WeightedBakedModel.MyWeighedRandomItem) p_i46073_1_.get(0)).model;
	}

	public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
		return this.baseModel.getFaceQuads(p_177551_1_);
	}

	public List<BakedQuad> getGeneralQuads() {
		return this.baseModel.getGeneralQuads();
	}

	public boolean isAmbientOcclusion() {
		return this.baseModel.isAmbientOcclusion();
	}

	public boolean isGui3d() {
		return this.baseModel.isGui3d();
	}

	public boolean isBuiltInRenderer() {
		return this.baseModel.isBuiltInRenderer();
	}

	public TextureAtlasSprite getParticleTexture() {
		return this.baseModel.getParticleTexture();
	}

	public ItemCameraTransforms getItemCameraTransforms() {
		return this.baseModel.getItemCameraTransforms();
	}

	public IBakedModel getAlternativeModel(long p_177564_1_) {
		return ((WeightedBakedModel.MyWeighedRandomItem) WeightedRandom.getRandomItem(this.models, Math.abs((int) p_177564_1_ >> 16) % this.totalWeight)).model;
	}

	public static class Builder {
		private List<WeightedBakedModel.MyWeighedRandomItem> listItems = Lists.<WeightedBakedModel.MyWeighedRandomItem>newArrayList();

		public WeightedBakedModel.Builder add(IBakedModel p_177677_1_, int p_177677_2_) {
			this.listItems.add(new WeightedBakedModel.MyWeighedRandomItem(p_177677_1_, p_177677_2_));
			return this;
		}

		public WeightedBakedModel build() {
			Collections.sort(this.listItems);
			return new WeightedBakedModel(this.listItems);
		}

		public IBakedModel first() {
			return ((WeightedBakedModel.MyWeighedRandomItem) this.listItems.get(0)).model;
		}
	}

	static class MyWeighedRandomItem extends WeightedRandom.Item implements Comparable<WeightedBakedModel.MyWeighedRandomItem> {
		protected final IBakedModel model;

		public MyWeighedRandomItem(IBakedModel p_i46072_1_, int p_i46072_2_) {
			super(p_i46072_2_);
			this.model = p_i46072_1_;
		}

		public int compareTo(WeightedBakedModel.MyWeighedRandomItem p_compareTo_1_) {
			return ComparisonChain.start().compare(p_compareTo_1_.itemWeight, this.itemWeight).compare(this.getCountQuads(), p_compareTo_1_.getCountQuads()).result();
		}

		protected int getCountQuads() {
			int i = this.model.getGeneralQuads().size();

			for (EnumFacing enumfacing : EnumFacing.values()) {
				i += this.model.getFaceQuads(enumfacing).size();
			}

			return i;
		}

		public String toString() {
			return "MyWeighedRandomItem{weight=" + this.itemWeight + ", model=" + this.model + '}';
		}
	}
}
