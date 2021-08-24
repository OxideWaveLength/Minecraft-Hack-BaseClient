package net.minecraft.client.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import optfine.Config;
import optfine.PlayerConfigurations;
import optfine.Reflector;

public abstract class AbstractClientPlayer extends EntityPlayer {

	private NetworkPlayerInfo playerInfo;
	private ResourceLocation ofLocationCape = null;
	

	public AbstractClientPlayer(World worldIn, GameProfile playerProfile) {
		super(worldIn, playerProfile);
		String s = playerProfile.getName();
		this.downloadCape(s);
		PlayerConfigurations.getPlayerConfiguration(this);
	}

	/**
	 * Returns true if the player is in spectator mode.
	 */
	public boolean isSpectator() {
		NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
		return networkplayerinfo != null && networkplayerinfo.getGameType() == WorldSettings.GameType.SPECTATOR;
	}

	/**
	 * Checks if this instance of AbstractClientPlayer has any associated player
	 * data.
	 */
	public boolean hasPlayerInfo() {
		return this.getPlayerInfo() != null;
	}

	protected NetworkPlayerInfo getPlayerInfo() {
		if (this.playerInfo == null) {
			this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
		}

		return this.playerInfo;
	}

	/**
	 * Returns true if the player has an associated skin.
	 */
	public boolean hasSkin() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
	}

	/**
	 * Returns true if the player instance has an associated skin.
	 */
	public ResourceLocation getLocationSkin() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
	}

	public ResourceLocation getLocationCape() {
		if (!Config.isShowCapes()) {
			return null;
		} else if (this.ofLocationCape != null) {
			return this.ofLocationCape;
		} else {
			NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
			return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
		}
	}

	public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		Object object = texturemanager.getTexture(resourceLocationIn);

		if (object == null) {
			object = new ThreadDownloadImageData((File) null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtils.stripControlCodes(username) }), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)), new ImageBufferDownload());
			texturemanager.loadTexture(resourceLocationIn, (ITextureObject) object);
		}

		return (ThreadDownloadImageData) object;
	}

	/**
	 * Returns true if the username has an associated skin.
	 */
	public static ResourceLocation getLocationSkin(String username) {
		return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
	}

	public String getSkinType() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
	}

	public float getFovModifier() {
		float f = 1.0F;

		if (this.capabilities.isFlying) {
			f *= 1.1F;
		}

		IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
		f = (float) ((double) f * ((iattributeinstance.getAttributeValue() / (double) this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

		if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
			f = 1.0F;
		}

		if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
			int i = this.getItemInUseDuration();
			float f1 = (float) i / 20.0F;

			if (f1 > 1.0F) {
				f1 = 1.0F;
			} else {
				f1 = f1 * f1;
			}

			f *= 1.0F - f1 * 0.15F;
		}

		return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, new Object[] { this, Float.valueOf(f) }) : f;
	}

	private void downloadCape(String p_downloadCape_1_) {
		if (p_downloadCape_1_ != null && !p_downloadCape_1_.isEmpty()) {
			p_downloadCape_1_ = StringUtils.stripControlCodes(p_downloadCape_1_);
			String s = "http://s.optifine.net/capes/" + p_downloadCape_1_ + ".png";
			String s1 = FilenameUtils.getBaseName(s);
			final ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s1);
			TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
			ITextureObject itextureobject = texturemanager.getTexture(resourcelocation);

			if (itextureobject != null && itextureobject instanceof ThreadDownloadImageData) {
				ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData) itextureobject;

				if (threaddownloadimagedata.imageFound != null) {
					if (threaddownloadimagedata.imageFound.booleanValue()) {
						this.ofLocationCape = resourcelocation;
					}

					return;
				}
			}

			IImageBuffer iimagebuffer = new IImageBuffer() {
				ImageBufferDownload ibd = new ImageBufferDownload();

				public BufferedImage parseUserSkin(BufferedImage image) {
					return AbstractClientPlayer.this.parseCape(image);
				}

				public void skinAvailable() {
					AbstractClientPlayer.this.ofLocationCape = resourcelocation;
				}
			};
			ThreadDownloadImageData threaddownloadimagedata1 = new ThreadDownloadImageData((File) null, s, (ResourceLocation) null, iimagebuffer);
			texturemanager.loadTexture(resourcelocation, threaddownloadimagedata1);
		}
	}

	private BufferedImage parseCape(BufferedImage p_parseCape_1_) {
		int i = 64;
		int j = 32;
		int k = p_parseCape_1_.getWidth();

		for (int l = p_parseCape_1_.getHeight(); i < k || j < l; j *= 2) {
			i *= 2;
		}

		BufferedImage bufferedimage = new BufferedImage(i, j, 2);
		Graphics graphics = bufferedimage.getGraphics();
		graphics.drawImage(p_parseCape_1_, 0, 0, (ImageObserver) null);
		graphics.dispose();
		return bufferedimage;
	}

}