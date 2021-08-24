package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.collect.Lists;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import optfine.Config;
import optfine.TextureUtils;

public class TextureAtlasSprite {
	private final String iconName;
	protected List framesTextureData = Lists.newArrayList();
	protected int[][] interpolatedFrameData;
	private AnimationMetadataSection animationMetadata;
	protected boolean rotated;
	protected int originX;
	protected int originY;
	protected int width;
	protected int height;
	private float minU;
	private float maxU;
	private float minV;
	private float maxV;
	protected int frameCounter;
	protected int tickCounter;
	private static String locationNameClock = "builtin/clock";
	private static String locationNameCompass = "builtin/compass";
	
	private int indexInMap = -1;
	public float baseU;
	public float baseV;
	public int sheetWidth;
	public int sheetHeight;
	public int glSpriteTextureId = -1;
	public TextureAtlasSprite spriteSingle = null;
	public boolean isSpriteSingle = false;
	public int mipmapLevels = 0;

	private TextureAtlasSprite(TextureAtlasSprite p_i9_1_) {
		this.iconName = p_i9_1_.iconName;
		this.isSpriteSingle = true;
	}

	protected TextureAtlasSprite(String spriteName) {
		this.iconName = spriteName;

		if (Config.isMultiTexture()) {
			this.spriteSingle = new TextureAtlasSprite(this);
		}
	}

	protected static TextureAtlasSprite makeAtlasSprite(ResourceLocation spriteResourceLocation) {
		String s = spriteResourceLocation.toString();
		return (TextureAtlasSprite) (locationNameClock.equals(s) ? new TextureClock(s) : (locationNameCompass.equals(s) ? new TextureCompass(s) : new TextureAtlasSprite(s)));
	}

	public static void setLocationNameClock(String clockName) {
		locationNameClock = clockName;
	}

	public static void setLocationNameCompass(String compassName) {
		locationNameCompass = compassName;
	}

	public void initSprite(int inX, int inY, int originInX, int originInY, boolean rotatedIn) {
		this.originX = originInX;
		this.originY = originInY;
		this.rotated = rotatedIn;
		float f = (float) (0.009999999776482582D / (double) inX);
		float f1 = (float) (0.009999999776482582D / (double) inY);
		this.minU = (float) originInX / (float) ((double) inX) + f;
		this.maxU = (float) (originInX + this.width) / (float) ((double) inX) - f;
		this.minV = (float) originInY / (float) inY + f1;
		this.maxV = (float) (originInY + this.height) / (float) inY - f1;
		this.baseU = Math.min(this.minU, this.maxU);
		this.baseV = Math.min(this.minV, this.maxV);

		if (this.spriteSingle != null) {
			this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
		}
	}

	public void copyFrom(TextureAtlasSprite atlasSpirit) {
		this.originX = atlasSpirit.originX;
		this.originY = atlasSpirit.originY;
		this.width = atlasSpirit.width;
		this.height = atlasSpirit.height;
		this.rotated = atlasSpirit.rotated;
		this.minU = atlasSpirit.minU;
		this.maxU = atlasSpirit.maxU;
		this.minV = atlasSpirit.minV;
		this.maxV = atlasSpirit.maxV;

		if (this.spriteSingle != null) {
			this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
		}
	}

	/**
	 * Returns the X position of this icon on its texture sheet, in pixels.
	 */
	public int getOriginX() {
		return this.originX;
	}

	/**
	 * Returns the Y position of this icon on its texture sheet, in pixels.
	 */
	public int getOriginY() {
		return this.originY;
	}

	/**
	 * Returns the width of the icon, in pixels.
	 */
	public int getIconWidth() {
		return this.width;
	}

	/**
	 * Returns the height of the icon, in pixels.
	 */
	public int getIconHeight() {
		return this.height;
	}

	/**
	 * Returns the minimum U coordinate to use when rendering with this icon.
	 */
	public float getMinU() {
		return this.minU;
	}

	/**
	 * Returns the maximum U coordinate to use when rendering with this icon.
	 */
	public float getMaxU() {
		return this.maxU;
	}

	/**
	 * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other
	 * arguments return in-between values.
	 */
	public float getInterpolatedU(double u) {
		float f = this.maxU - this.minU;
		return this.minU + f * (float) u / 16.0F;
	}

	/**
	 * Returns the minimum V coordinate to use when rendering with this icon.
	 */
	public float getMinV() {
		return this.minV;
	}

	/**
	 * Returns the maximum V coordinate to use when rendering with this icon.
	 */
	public float getMaxV() {
		return this.maxV;
	}

	/**
	 * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other
	 * arguments return in-between values.
	 */
	public float getInterpolatedV(double v) {
		float f = this.maxV - this.minV;
		return this.minV + f * ((float) v / 16.0F);
	}

	public String getIconName() {
		return this.iconName;
	}

	public void updateAnimation() {
		++this.tickCounter;

		if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
			int i = this.animationMetadata.getFrameIndex(this.frameCounter);
			int j = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
			this.frameCounter = (this.frameCounter + 1) % j;
			this.tickCounter = 0;
			int k = this.animationMetadata.getFrameIndex(this.frameCounter);
			boolean flag = false;
			boolean flag1 = this.isSpriteSingle;

			if (i != k && k >= 0 && k < this.framesTextureData.size()) {
				TextureUtil.uploadTextureMipmap((int[][]) ((int[][]) this.framesTextureData.get(k)), this.width, this.height, this.originX, this.originY, flag, flag1);
			}
		} else if (this.animationMetadata.isInterpolate()) {
			this.updateAnimationInterpolated();
		}
	}

	private void updateAnimationInterpolated() {
		double d0 = 1.0D - (double) this.tickCounter / (double) this.animationMetadata.getFrameTimeSingle(this.frameCounter);
		int i = this.animationMetadata.getFrameIndex(this.frameCounter);
		int j = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
		int k = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % j);

		if (i != k && k >= 0 && k < this.framesTextureData.size()) {
			int[][] aint = (int[][]) ((int[][]) this.framesTextureData.get(i));
			int[][] aint1 = (int[][]) ((int[][]) this.framesTextureData.get(k));

			if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != aint.length) {
				this.interpolatedFrameData = new int[aint.length][];
			}

			for (int l = 0; l < aint.length; ++l) {
				if (this.interpolatedFrameData[l] == null) {
					this.interpolatedFrameData[l] = new int[aint[l].length];
				}

				if (l < aint1.length && aint1[l].length == aint[l].length) {
					for (int i1 = 0; i1 < aint[l].length; ++i1) {
						int j1 = aint[l][i1];
						int k1 = aint1[l][i1];
						int l1 = (int) ((double) ((j1 & 16711680) >> 16) * d0 + (double) ((k1 & 16711680) >> 16) * (1.0D - d0));
						int i2 = (int) ((double) ((j1 & 65280) >> 8) * d0 + (double) ((k1 & 65280) >> 8) * (1.0D - d0));
						int j2 = (int) ((double) (j1 & 255) * d0 + (double) (k1 & 255) * (1.0D - d0));
						this.interpolatedFrameData[l][i1] = j1 & -16777216 | l1 << 16 | i2 << 8 | j2;
					}
				}
			}

			TextureUtil.uploadTextureMipmap(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false);
		}
	}

	public int[][] getFrameTextureData(int index) {
		return (int[][]) ((int[][]) this.framesTextureData.get(index));
	}

	public int getFrameCount() {
		return this.framesTextureData.size();
	}

	public void setIconWidth(int newWidth) {
		this.width = newWidth;

		if (this.spriteSingle != null) {
			this.spriteSingle.setIconWidth(this.width);
		}
	}

	public void setIconHeight(int newHeight) {
		this.height = newHeight;

		if (this.spriteSingle != null) {
			this.spriteSingle.setIconHeight(this.height);
		}
	}

	public void loadSprite(BufferedImage[] images, AnimationMetadataSection meta) throws IOException {
		this.resetSprite();
		int i = images[0].getWidth();
		int j = images[0].getHeight();
		this.width = i;
		this.height = j;
		int[][] aint = new int[images.length][];

		for (int k = 0; k < images.length; ++k) {
			BufferedImage bufferedimage = images[k];

			if (bufferedimage != null) {
				if (k > 0 && (bufferedimage.getWidth() != i >> k || bufferedimage.getHeight() != j >> k)) {
					throw new RuntimeException(String.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", new Object[] { Integer.valueOf(k), Integer.valueOf(bufferedimage.getWidth()), Integer.valueOf(bufferedimage.getHeight()), Integer.valueOf(i >> k), Integer.valueOf(j >> k) }));
				}

				aint[k] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
				bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[k], 0, bufferedimage.getWidth());
			}
		}

		if (meta == null) {
			if (j != i) {
				throw new RuntimeException("broken aspect ratio and not an animation");
			}

			this.framesTextureData.add(aint);
		} else {
			int j1 = j / i;
			int k1 = i;
			int l = i;
			this.height = this.width;

			if (meta.getFrameCount() > 0) {
				Iterator iterator = meta.getFrameIndexSet().iterator();

				while (iterator.hasNext()) {
					int i1 = ((Integer) iterator.next()).intValue();

					if (i1 >= j1) {
						throw new RuntimeException("invalid frameindex " + i1);
					}

					this.allocateFrameTextureData(i1);
					this.framesTextureData.set(i1, getFrameTextureData(aint, k1, l, i1));
				}

				this.animationMetadata = meta;
			} else {
				ArrayList arraylist = Lists.newArrayList();

				for (int i2 = 0; i2 < j1; ++i2) {
					this.framesTextureData.add(getFrameTextureData(aint, k1, l, i2));
					arraylist.add(new AnimationFrame(i2, -1));
				}

				this.animationMetadata = new AnimationMetadataSection(arraylist, this.width, this.height, meta.getFrameTime(), meta.isInterpolate());
			}
		}

		for (int l1 = 0; l1 < this.framesTextureData.size(); ++l1) {
			int[][] aint1 = (int[][]) ((int[][]) this.framesTextureData.get(l1));

			if (aint1 != null && !this.iconName.startsWith("minecraft:blocks/leaves_")) {
				for (int j2 = 0; j2 < aint1.length; ++j2) {
					int[] aint2 = aint1[j2];
					this.fixTransparentColor(aint2);
				}
			}
		}

		if (this.spriteSingle != null) {
			this.spriteSingle.loadSprite(images, meta);
		}
	}

	public void generateMipmaps(int level) {
		ArrayList arraylist = Lists.newArrayList();

		for (int i = 0; i < this.framesTextureData.size(); ++i) {
			final int[][] aint = (int[][]) ((int[][]) this.framesTextureData.get(i));

			if (aint != null) {
				try {
					arraylist.add(TextureUtil.generateMipmapData(level, this.width, aint));
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
					crashreportcategory.addCrashSection("Frame index", Integer.valueOf(i));
					crashreportcategory.addCrashSectionCallable("Frame sizes", new Callable() {
						

						public String call() throws Exception {
							StringBuilder stringbuilder = new StringBuilder();

							for (int[] aint1 : aint) {
								if (stringbuilder.length() > 0) {
									stringbuilder.append(", ");
								}

								stringbuilder.append(aint1 == null ? "null" : Integer.valueOf(aint1.length));
							}

							return stringbuilder.toString();
						}
					});
					throw new ReportedException(crashreport);
				}
			}
		}

		this.setFramesTextureData(arraylist);

		if (this.spriteSingle != null) {
			this.spriteSingle.generateMipmaps(level);
		}
	}

	private void allocateFrameTextureData(int index) {
		if (this.framesTextureData.size() <= index) {
			for (int i = this.framesTextureData.size(); i <= index; ++i) {
				this.framesTextureData.add((Object) null);
			}
		}

		if (this.spriteSingle != null) {
			this.spriteSingle.allocateFrameTextureData(index);
		}
	}

	private static int[][] getFrameTextureData(int[][] data, int rows, int columns, int p_147962_3_) {
		int[][] aint = new int[data.length][];

		for (int i = 0; i < data.length; ++i) {
			int[] aint1 = data[i];

			if (aint1 != null) {
				aint[i] = new int[(rows >> i) * (columns >> i)];
				System.arraycopy(aint1, p_147962_3_ * aint[i].length, aint[i], 0, aint[i].length);
			}
		}

		return aint;
	}

	public void clearFramesTextureData() {
		this.framesTextureData.clear();

		if (this.spriteSingle != null) {
			this.spriteSingle.clearFramesTextureData();
		}
	}

	public boolean hasAnimationMetadata() {
		return this.animationMetadata != null;
	}

	public void setFramesTextureData(List newFramesTextureData) {
		this.framesTextureData = newFramesTextureData;

		if (this.spriteSingle != null) {
			this.spriteSingle.setFramesTextureData(newFramesTextureData);
		}
	}

	private void resetSprite() {
		this.animationMetadata = null;
		this.setFramesTextureData(Lists.newArrayList());
		this.frameCounter = 0;
		this.tickCounter = 0;

		if (this.spriteSingle != null) {
			this.spriteSingle.resetSprite();
		}
	}

	public String toString() {
		return "TextureAtlasSprite{name=\'" + this.iconName + '\'' + ", frameCount=" + this.framesTextureData.size() + ", rotated=" + this.rotated + ", x=" + this.originX + ", y=" + this.originY + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
	}

	public boolean hasCustomLoader(IResourceManager p_hasCustomLoader_1_, ResourceLocation p_hasCustomLoader_2_) {
		return false;
	}

	public boolean load(IResourceManager p_load_1_, ResourceLocation p_load_2_) {
		return true;
	}

	public int getIndexInMap() {
		return this.indexInMap;
	}

	public void setIndexInMap(int p_setIndexInMap_1_) {
		this.indexInMap = p_setIndexInMap_1_;
	}

	private void fixTransparentColor(int[] p_fixTransparentColor_1_) {
		if (p_fixTransparentColor_1_ != null) {
			long i = 0L;
			long j = 0L;
			long k = 0L;
			long l = 0L;

			for (int i1 = 0; i1 < p_fixTransparentColor_1_.length; ++i1) {
				int j1 = p_fixTransparentColor_1_[i1];
				int k1 = j1 >> 24 & 255;

				if (k1 >= 16) {
					int l1 = j1 >> 16 & 255;
					int i2 = j1 >> 8 & 255;
					int j2 = j1 & 255;
					i += (long) l1;
					j += (long) i2;
					k += (long) j2;
					++l;
				}
			}

			if (l > 0L) {
				int l2 = (int) (i / l);
				int i3 = (int) (j / l);
				int j3 = (int) (k / l);
				int k3 = l2 << 16 | i3 << 8 | j3;

				for (int l3 = 0; l3 < p_fixTransparentColor_1_.length; ++l3) {
					int i4 = p_fixTransparentColor_1_[l3];
					int k2 = i4 >> 24 & 255;

					if (k2 <= 16) {
						p_fixTransparentColor_1_[l3] = k3;
					}
				}
			}
		}
	}

	public double getSpriteU16(float p_getSpriteU16_1_) {
		float f = this.maxU - this.minU;
		return (double) ((p_getSpriteU16_1_ - this.minU) / f * 16.0F);
	}

	public double getSpriteV16(float p_getSpriteV16_1_) {
		float f = this.maxV - this.minV;
		return (double) ((p_getSpriteV16_1_ - this.minV) / f * 16.0F);
	}

	public void bindSpriteTexture() {
		if (this.glSpriteTextureId < 0) {
			this.glSpriteTextureId = TextureUtil.glGenTextures();
			TextureUtil.allocateTextureImpl(this.glSpriteTextureId, this.mipmapLevels, this.width, this.height);
			TextureUtils.applyAnisotropicLevel();
		}

		TextureUtils.bindTexture(this.glSpriteTextureId);
	}

	public void deleteSpriteTexture() {
		if (this.glSpriteTextureId >= 0) {
			TextureUtil.deleteTexture(this.glSpriteTextureId);
			this.glSpriteTextureId = -1;
		}
	}

	public float toSingleU(float p_toSingleU_1_) {
		p_toSingleU_1_ = p_toSingleU_1_ - this.baseU;
		float f = (float) this.sheetWidth / (float) this.width;
		p_toSingleU_1_ = p_toSingleU_1_ * f;
		return p_toSingleU_1_;
	}

	public float toSingleV(float p_toSingleV_1_) {
		p_toSingleV_1_ = p_toSingleV_1_ - this.baseV;
		float f = (float) this.sheetHeight / (float) this.height;
		p_toSingleV_1_ = p_toSingleV_1_ * f;
		return p_toSingleV_1_;
	}
}
