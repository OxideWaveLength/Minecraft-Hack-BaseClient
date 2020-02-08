package optfine;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TextureUtils {
	public static final String texGrassTop = "grass_top";
	public static final String texStone = "stone";
	public static final String texDirt = "dirt";
	public static final String texCoarseDirt = "coarse_dirt";
	public static final String texGrassSide = "grass_side";
	public static final String texStoneslabSide = "stone_slab_side";
	public static final String texStoneslabTop = "stone_slab_top";
	public static final String texBedrock = "bedrock";
	public static final String texSand = "sand";
	public static final String texGravel = "gravel";
	public static final String texLogOak = "log_oak";
	public static final String texLogBigOak = "log_big_oak";
	public static final String texLogAcacia = "log_acacia";
	public static final String texLogSpruce = "log_spruce";
	public static final String texLogBirch = "log_birch";
	public static final String texLogJungle = "log_jungle";
	public static final String texLogOakTop = "log_oak_top";
	public static final String texLogBigOakTop = "log_big_oak_top";
	public static final String texLogAcaciaTop = "log_acacia_top";
	public static final String texLogSpruceTop = "log_spruce_top";
	public static final String texLogBirchTop = "log_birch_top";
	public static final String texLogJungleTop = "log_jungle_top";
	public static final String texLeavesOak = "leaves_oak";
	public static final String texLeavesBigOak = "leaves_big_oak";
	public static final String texLeavesAcacia = "leaves_acacia";
	public static final String texLeavesBirch = "leaves_birch";
	public static final String texLeavesSpuce = "leaves_spruce";
	public static final String texLeavesJungle = "leaves_jungle";
	public static final String texGoldOre = "gold_ore";
	public static final String texIronOre = "iron_ore";
	public static final String texCoalOre = "coal_ore";
	public static final String texObsidian = "obsidian";
	public static final String texGrassSideOverlay = "grass_side_overlay";
	public static final String texSnow = "snow";
	public static final String texGrassSideSnowed = "grass_side_snowed";
	public static final String texMyceliumSide = "mycelium_side";
	public static final String texMyceliumTop = "mycelium_top";
	public static final String texDiamondOre = "diamond_ore";
	public static final String texRedstoneOre = "redstone_ore";
	public static final String texLapisOre = "lapis_ore";
	public static final String texCactusSide = "cactus_side";
	public static final String texClay = "clay";
	public static final String texFarmlandWet = "farmland_wet";
	public static final String texFarmlandDry = "farmland_dry";
	public static final String texNetherrack = "netherrack";
	public static final String texSoulSand = "soul_sand";
	public static final String texGlowstone = "glowstone";
	public static final String texLeavesSpruce = "leaves_spruce";
	public static final String texLeavesSpruceOpaque = "leaves_spruce_opaque";
	public static final String texEndStone = "end_stone";
	public static final String texSandstoneTop = "sandstone_top";
	public static final String texSandstoneBottom = "sandstone_bottom";
	public static final String texRedstoneLampOff = "redstone_lamp_off";
	public static final String texRedstoneLampOn = "redstone_lamp_on";
	public static final String texWaterStill = "water_still";
	public static final String texWaterFlow = "water_flow";
	public static final String texLavaStill = "lava_still";
	public static final String texLavaFlow = "lava_flow";
	public static final String texFireLayer0 = "fire_layer_0";
	public static final String texFireLayer1 = "fire_layer_1";
	public static final String texPortal = "portal";
	public static final String texGlass = "glass";
	public static final String texGlassPaneTop = "glass_pane_top";
	public static TextureAtlasSprite iconGrassTop;
	public static TextureAtlasSprite iconGrassSide;
	public static TextureAtlasSprite iconGrassSideOverlay;
	public static TextureAtlasSprite iconSnow;
	public static TextureAtlasSprite iconGrassSideSnowed;
	public static TextureAtlasSprite iconMyceliumSide;
	public static TextureAtlasSprite iconMyceliumTop;
	public static TextureAtlasSprite iconWaterStill;
	public static TextureAtlasSprite iconWaterFlow;
	public static TextureAtlasSprite iconLavaStill;
	public static TextureAtlasSprite iconLavaFlow;
	public static TextureAtlasSprite iconPortal;
	public static TextureAtlasSprite iconFireLayer0;
	public static TextureAtlasSprite iconFireLayer1;
	public static TextureAtlasSprite iconGlass;
	public static TextureAtlasSprite iconGlassPaneTop;
	public static final String SPRITE_LOCATION_PREFIX = "minecraft:blocks/";
	private static IntBuffer staticBuffer = GLAllocation.createDirectIntBuffer(256);

	public static void update() {
		TextureMap texturemap = getTextureMapBlocks();

		if (texturemap != null) {
			String s = "minecraft:blocks/";
			iconGrassTop = texturemap.getSpriteSafe(s + "grass_top");
			iconGrassSide = texturemap.getSpriteSafe(s + "grass_side");
			iconGrassSideOverlay = texturemap.getSpriteSafe(s + "grass_side_overlay");
			iconSnow = texturemap.getSpriteSafe(s + "snow");
			iconGrassSideSnowed = texturemap.getSpriteSafe(s + "grass_side_snowed");
			iconMyceliumSide = texturemap.getSpriteSafe(s + "mycelium_side");
			iconMyceliumTop = texturemap.getSpriteSafe(s + "mycelium_top");
			iconWaterStill = texturemap.getSpriteSafe(s + "water_still");
			iconWaterFlow = texturemap.getSpriteSafe(s + "water_flow");
			iconLavaStill = texturemap.getSpriteSafe(s + "lava_still");
			iconLavaFlow = texturemap.getSpriteSafe(s + "lava_flow");
			iconFireLayer0 = texturemap.getSpriteSafe(s + "fire_layer_0");
			iconFireLayer1 = texturemap.getSpriteSafe(s + "fire_layer_1");
			iconPortal = texturemap.getSpriteSafe(s + "portal");
			iconGlass = texturemap.getSpriteSafe(s + "glass");
			iconGlassPaneTop = texturemap.getSpriteSafe(s + "glass_pane_top");
		}
	}

	public static BufferedImage fixTextureDimensions(String p_fixTextureDimensions_0_, BufferedImage p_fixTextureDimensions_1_) {
		if (p_fixTextureDimensions_0_.startsWith("/mob/zombie") || p_fixTextureDimensions_0_.startsWith("/mob/pigzombie")) {
			int i = p_fixTextureDimensions_1_.getWidth();
			int j = p_fixTextureDimensions_1_.getHeight();

			if (i == j * 2) {
				BufferedImage bufferedimage = new BufferedImage(i, j * 2, 2);
				Graphics2D graphics2d = bufferedimage.createGraphics();
				graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				graphics2d.drawImage(p_fixTextureDimensions_1_, 0, 0, i, j, (ImageObserver) null);
				return bufferedimage;
			}
		}

		return p_fixTextureDimensions_1_;
	}

	public static int ceilPowerOfTwo(int p_ceilPowerOfTwo_0_) {
		int i;

		for (i = 1; i < p_ceilPowerOfTwo_0_; i *= 2) {
			;
		}

		return i;
	}

	public static int getPowerOfTwo(int p_getPowerOfTwo_0_) {
		int i = 1;
		int j;

		for (j = 0; i < p_getPowerOfTwo_0_; ++j) {
			i *= 2;
		}

		return j;
	}

	public static int twoToPower(int p_twoToPower_0_) {
		int i = 1;

		for (int j = 0; j < p_twoToPower_0_; ++j) {
			i *= 2;
		}

		return i;
	}

	public static void refreshBlockTextures() {
	}

	public static ITextureObject getTexture(String p_getTexture_0_) {
		return getTexture(new ResourceLocation(p_getTexture_0_));
	}

	public static ITextureObject getTexture(ResourceLocation p_getTexture_0_) {
		ITextureObject itextureobject = Config.getTextureManager().getTexture(p_getTexture_0_);

		if (itextureobject != null) {
			return itextureobject;
		} else if (!Config.hasResource(p_getTexture_0_)) {
			return null;
		} else {
			SimpleTexture simpletexture = new SimpleTexture(p_getTexture_0_);
			Config.getTextureManager().loadTexture(p_getTexture_0_, simpletexture);
			return simpletexture;
		}
	}

	public static void resourcesReloaded(IResourceManager p_resourcesReloaded_0_) {
		if (getTextureMapBlocks() != null) {
			Config.dbg("*** Reloading custom textures ***");
			CustomSky.reset();
			TextureAnimations.reset();
			update();
			NaturalTextures.update();
			BetterGrass.update();
			BetterSnow.update();
			TextureAnimations.update();
			CustomColorizer.update();
			CustomSky.update();
			RandomMobs.resetTextures();
			Config.updateTexturePackClouds();
			Config.getTextureManager().tick();
		}
	}

	public static TextureMap getTextureMapBlocks() {
		return Minecraft.getMinecraft().getTextureMapBlocks();
	}

	public static void registerResourceListener() {
		IResourceManager iresourcemanager = Config.getResourceManager();

		if (iresourcemanager instanceof IReloadableResourceManager) {
			IReloadableResourceManager ireloadableresourcemanager = (IReloadableResourceManager) iresourcemanager;
			IResourceManagerReloadListener iresourcemanagerreloadlistener = new IResourceManagerReloadListener() {
				public void onResourceManagerReload(IResourceManager resourceManager) {
					TextureUtils.resourcesReloaded(resourceManager);
				}
			};
			ireloadableresourcemanager.registerReloadListener(iresourcemanagerreloadlistener);
		}

		ITickableTextureObject itickabletextureobject = new ITickableTextureObject() {
			public void tick() {
				TextureAnimations.updateCustomAnimations();
			}

			public void loadTexture(IResourceManager resourceManager) throws IOException {
			}

			public int getGlTextureId() {
				return 0;
			}

			public void setBlurMipmap(boolean p_174936_1_, boolean p_174936_2_) {
			}

			public void restoreLastBlurMipmap() {
			}
		};
		ResourceLocation resourcelocation = new ResourceLocation("optifine/TickableTextures");
		Config.getTextureManager().loadTickableTexture(resourcelocation, itickabletextureobject);
	}

	public static String fixResourcePath(String p_fixResourcePath_0_, String p_fixResourcePath_1_) {
		String s = "assets/minecraft/";

		if (p_fixResourcePath_0_.startsWith(s)) {
			p_fixResourcePath_0_ = p_fixResourcePath_0_.substring(s.length());
			return p_fixResourcePath_0_;
		} else if (p_fixResourcePath_0_.startsWith("./")) {
			p_fixResourcePath_0_ = p_fixResourcePath_0_.substring(2);

			if (!p_fixResourcePath_1_.endsWith("/")) {
				p_fixResourcePath_1_ = p_fixResourcePath_1_ + "/";
			}

			p_fixResourcePath_0_ = p_fixResourcePath_1_ + p_fixResourcePath_0_;
			return p_fixResourcePath_0_;
		} else {
			String s1 = "mcpatcher/";

			if (p_fixResourcePath_0_.startsWith("~/")) {
				p_fixResourcePath_0_ = p_fixResourcePath_0_.substring(2);
				p_fixResourcePath_0_ = s1 + p_fixResourcePath_0_;
				return p_fixResourcePath_0_;
			} else if (p_fixResourcePath_0_.startsWith("/")) {
				p_fixResourcePath_0_ = s1 + p_fixResourcePath_0_.substring(1);
				return p_fixResourcePath_0_;
			} else {
				return p_fixResourcePath_0_;
			}
		}
	}

	public static String getBasePath(String p_getBasePath_0_) {
		int i = p_getBasePath_0_.lastIndexOf(47);
		return i < 0 ? "" : p_getBasePath_0_.substring(0, i);
	}

	public static void applyAnisotropicLevel() {
		if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
			float f = GL11.glGetFloat(34047);
			float f1 = (float) Config.getAnisotropicFilterLevel();
			f1 = Math.min(f1, f);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 34046, f1);
		}
	}

	public static void bindTexture(int p_bindTexture_0_) {
		GlStateManager.bindTexture(p_bindTexture_0_);
	}

	public static boolean isPowerOfTwo(int p_isPowerOfTwo_0_) {
		int i = MathHelper.roundUpToPowerOfTwo(p_isPowerOfTwo_0_);
		return i == p_isPowerOfTwo_0_;
	}

	public static BufferedImage scaleToPowerOfTwo(BufferedImage p_scaleToPowerOfTwo_0_, int p_scaleToPowerOfTwo_1_) {
		if (p_scaleToPowerOfTwo_0_ == null) {
			return p_scaleToPowerOfTwo_0_;
		} else {
			int i = p_scaleToPowerOfTwo_0_.getWidth();
			int j = p_scaleToPowerOfTwo_0_.getHeight();
			int k = Math.max(i, p_scaleToPowerOfTwo_1_);
			k = MathHelper.roundUpToPowerOfTwo(k);

			if (k == i) {
				return p_scaleToPowerOfTwo_0_;
			} else {
				int l = j * k / i;
				BufferedImage bufferedimage = new BufferedImage(k, l, 2);
				Graphics2D graphics2d = bufferedimage.createGraphics();
				Object object = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

				if (k % i != 0) {
					object = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
				}

				graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, object);
				graphics2d.drawImage(p_scaleToPowerOfTwo_0_, 0, 0, k, l, (ImageObserver) null);
				return bufferedimage;
			}
		}
	}

	public static Dimension getImageSize(InputStream p_getImageSize_0_, String p_getImageSize_1_) {
		Iterator iterator = ImageIO.getImageReadersBySuffix(p_getImageSize_1_);

		while (true) {
			if (iterator.hasNext()) {
				ImageReader imagereader = (ImageReader) iterator.next();
				Dimension dimension;

				try {
					ImageInputStream imageinputstream = ImageIO.createImageInputStream(p_getImageSize_0_);
					imagereader.setInput(imageinputstream);
					int i = imagereader.getWidth(imagereader.getMinIndex());
					int j = imagereader.getHeight(imagereader.getMinIndex());
					dimension = new Dimension(i, j);
				} catch (IOException var11) {
					continue;
				} finally {
					imagereader.dispose();
				}

				return dimension;
			}

			return null;
		}
	}
}
