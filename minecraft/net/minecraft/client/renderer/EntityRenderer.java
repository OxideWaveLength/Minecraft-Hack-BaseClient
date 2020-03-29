package net.minecraft.client.renderer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Project;

import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.event.events.Render3DEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import optfine.Config;
import optfine.CustomColorizer;
import optfine.Lagometer;
import optfine.RandomMobs;
import optfine.Reflector;
import optfine.RenderPlayerOF;
import optfine.TextureUtils;

public class EntityRenderer implements IResourceManagerReloadListener {

	private static final Logger logger = LogManager.getLogger();
	private static final ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
	private static final ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
	public static boolean anaglyphEnable;

	/** Anaglyph field (0=R, 1=GB) */
	public static int anaglyphField;

	/** A reference to the Minecraft object. */
	private Minecraft mc;
	private final IResourceManager resourceManager;
	private Random random = new Random();
	private float farPlaneDistance;
	public ItemRenderer itemRenderer;
	private final MapItemRenderer theMapItemRenderer;

	/** Entity renderer update count */
	private int rendererUpdateCount;

	/** Pointed entity */
	private Entity pointedEntity;
	private MouseFilter mouseFilterXAxis = new MouseFilter();
	private MouseFilter mouseFilterYAxis = new MouseFilter();
	private float thirdPersonDistance = 4.0F;

	/** Third person distance temp */
	private float thirdPersonDistanceTemp = 4.0F;

	/** Smooth cam yaw */
	private float smoothCamYaw;

	/** Smooth cam pitch */
	private float smoothCamPitch;

	/** Smooth cam filter X */
	private float smoothCamFilterX;

	/** Smooth cam filter Y */
	private float smoothCamFilterY;

	/** Smooth cam partial ticks */
	private float smoothCamPartialTicks;

	/** FOV modifier hand */
	private float fovModifierHand;

	/** FOV modifier hand prev */
	private float fovModifierHandPrev;
	private float bossColorModifier;
	private float bossColorModifierPrev;

	/** Cloud fog mode */
	private boolean cloudFog;
	private boolean renderHand = true;
	private boolean drawBlockOutline = true;

	/** Previous frame time in milliseconds */
	private long prevFrameTime = Minecraft.getSystemTime();

	/** End time of last render (ns) */
	private long renderEndNanoTime;

	/**
	 * The texture id of the blocklight/skylight texture used for lighting effects
	 */
	private final DynamicTexture lightmapTexture;

	/**
	 * Colors computed in updateLightmap() and loaded into the lightmap emptyTexture
	 */
	private final int[] lightmapColors;
	private final ResourceLocation locationLightMap;

	/**
	 * Is set, updateCameraAndRender() calls updateLightmap(); set by
	 * updateTorchFlicker()
	 */
	private boolean lightmapUpdateNeeded;

	/** Torch flicker X */
	private float torchFlickerX;
	private float torchFlickerDX;

	/** Rain sound counter */
	private int rainSoundCounter;
	private float[] rainXCoords = new float[1024];
	private float[] rainYCoords = new float[1024];

	/** Fog color buffer */
	private FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
	public float fogColorRed;
	public float fogColorGreen;
	public float fogColorBlue;

	/** Fog color 2 */
	private float fogColor2;

	/** Fog color 1 */
	private float fogColor1;
	private int debugViewDirection = 0;
	private boolean debugView = false;
	private double cameraZoom = 1.0D;
	private double cameraYaw;
	private double cameraPitch;
	public ShaderGroup theShaderGroup;
	private static final ResourceLocation[] shaderResourceLocations = new ResourceLocation[] { new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json") };
	public static final int shaderCount = shaderResourceLocations.length;
	private int shaderIndex;
	private boolean useShader;
	private int frameCount;
	
	private boolean initialized = false;
	private World updatedWorld = null;
	private boolean showDebugInfo = false;
	public boolean fogStandard = false;
	private float clipDistance = 128.0F;
	private long lastServerTime = 0L;
	private int lastServerTicks = 0;
	private int serverWaitTime = 0;
	private int serverWaitTimeCurrent = 0;
	private float avgServerTimeDiff = 0.0F;
	private float avgServerTickDiff = 0.0F;
	private long lastErrorCheckTimeMs = 0L;

	public EntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
		this.shaderIndex = shaderCount;
		this.useShader = false;
		this.frameCount = 0;
		this.mc = mcIn;
		this.resourceManager = resourceManagerIn;
		this.itemRenderer = mcIn.getItemRenderer();
		this.theMapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
		this.lightmapTexture = new DynamicTexture(16, 16);
		this.locationLightMap = mcIn.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
		this.lightmapColors = this.lightmapTexture.getTextureData();
		this.theShaderGroup = null;

		for (int i = 0; i < 32; ++i) {
			for (int j = 0; j < 32; ++j) {
				float f = (float) (j - 16);
				float f1 = (float) (i - 16);
				float f2 = MathHelper.sqrt_float(f * f + f1 * f1);
				this.rainXCoords[i << 5 | j] = -f1 / f2;
				this.rainYCoords[i << 5 | j] = f / f2;
			}
		}
	}

	public boolean isShaderActive() {
		return OpenGlHelper.shadersSupported && this.theShaderGroup != null;
	}

	public void func_181022_b() {
		if (this.theShaderGroup != null) {
			this.theShaderGroup.deleteShaderGroup();
		}

		this.theShaderGroup = null;
		this.shaderIndex = shaderCount;
	}

	public void switchUseShader() {
		this.useShader = !this.useShader;
	}

	/**
	 * What shader to use when spectating this entity
	 */
	public void loadEntityShader(Entity entityIn) {
		if (OpenGlHelper.shadersSupported) {
			if (this.theShaderGroup != null) {
				this.theShaderGroup.deleteShaderGroup();
			}

			this.theShaderGroup = null;

			if (entityIn instanceof EntityCreeper) {
				this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
			} else if (entityIn instanceof EntitySpider) {
				this.loadShader(new ResourceLocation("shaders/post/spider.json"));
			} else if (entityIn instanceof EntityEnderman) {
				this.loadShader(new ResourceLocation("shaders/post/invert.json"));
			}
		}
	}

	public void activateNextShader() {
		if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
			if (this.theShaderGroup != null) {
				this.theShaderGroup.deleteShaderGroup();
			}

			this.shaderIndex = (this.shaderIndex + 1) % (shaderResourceLocations.length + 1);

			if (this.shaderIndex != shaderCount) {
				this.loadShader(shaderResourceLocations[this.shaderIndex]);
			} else {
				this.theShaderGroup = null;
			}
		}
	}

	public void loadShader(ResourceLocation resourceLocationIn) {
		try {
			this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn);
			this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
			this.useShader = true;
		} catch (IOException ioexception) {
			logger.warn((String) ("Failed to load shader: " + resourceLocationIn), (Throwable) ioexception);
			this.shaderIndex = shaderCount;
			this.useShader = false;
		} catch (JsonSyntaxException jsonsyntaxexception) {
			logger.warn((String) ("Failed to load shader: " + resourceLocationIn), (Throwable) jsonsyntaxexception);
			this.shaderIndex = shaderCount;
			this.useShader = false;
		}
	}

	public void onResourceManagerReload(IResourceManager resourceManager) {
		if (this.theShaderGroup != null) {
			this.theShaderGroup.deleteShaderGroup();
		}

		this.theShaderGroup = null;

		if (this.shaderIndex != shaderCount) {
			this.loadShader(shaderResourceLocations[this.shaderIndex]);
		} else {
			this.loadEntityShader(this.mc.getRenderViewEntity());
		}
	}

	/**
	 * Updates the entity renderer
	 */
	public void updateRenderer() {
		if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
			ShaderLinkHelper.setNewStaticShaderLinkHelper();
		}

		this.updateFovModifierHand();
		this.updateTorchFlicker();
		this.fogColor2 = this.fogColor1;
		this.thirdPersonDistanceTemp = this.thirdPersonDistance;

		if (this.mc.gameSettings.smoothCamera) {
			float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f1 = f * f * f * 8.0F;
			this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f1);
			this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f1);
			this.smoothCamPartialTicks = 0.0F;
			this.smoothCamYaw = 0.0F;
			this.smoothCamPitch = 0.0F;
		} else {
			this.smoothCamFilterX = 0.0F;
			this.smoothCamFilterY = 0.0F;
			this.mouseFilterXAxis.reset();
			this.mouseFilterYAxis.reset();
		}

		if (this.mc.getRenderViewEntity() == null) {
			this.mc.setRenderViewEntity(this.mc.thePlayer);
		}

		float f3 = this.mc.theWorld.getLightBrightness(new BlockPos(this.mc.getRenderViewEntity()));
		float f4 = (float) this.mc.gameSettings.renderDistanceChunks / 32.0F;
		float f2 = f3 * (1.0F - f4) + f4;
		this.fogColor1 += (f2 - this.fogColor1) * 0.1F;
		++this.rendererUpdateCount;
		this.itemRenderer.updateEquippedItem();
		this.addRainParticles();
		this.bossColorModifierPrev = this.bossColorModifier;

		if (BossStatus.hasColorModifier) {
			this.bossColorModifier += 0.05F;

			if (this.bossColorModifier > 1.0F) {
				this.bossColorModifier = 1.0F;
			}

			BossStatus.hasColorModifier = false;
		} else if (this.bossColorModifier > 0.0F) {
			this.bossColorModifier -= 0.0125F;
		}
	}

	public ShaderGroup getShaderGroup() {
		return this.theShaderGroup;
	}

	public void updateShaderGroupSize(int width, int height) {
		if (OpenGlHelper.shadersSupported) {
			if (this.theShaderGroup != null) {
				this.theShaderGroup.createBindFramebuffers(width, height);
			}

			this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
		}
	}

	/**
	 * Finds what block or object the mouse is over at the specified partial tick
	 * time. Args: partialTickTime
	 */
	public void getMouseOver(float partialTicks) {
		Entity entity = this.mc.getRenderViewEntity();

		if (entity != null && this.mc.theWorld != null) {
			this.mc.mcProfiler.startSection("pick");
			this.mc.pointedEntity = null;
			double d0 = (double) this.mc.playerController.getBlockReachDistance();
			this.mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
			double d1 = d0;
			Vec3 vec3 = entity.getPositionEyes(partialTicks);
			boolean flag = false;
			boolean flag1 = true;

			if (this.mc.playerController.extendedReach()) {
				d0 = 6.0D;
				d1 = 6.0D;
			} else {
				if (d0 > 3.0D) {
					flag = true;
				}

//				d0 = d0;
			}

			if (this.mc.objectMouseOver != null) {
				d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
			}

			Vec3 vec31 = entity.getLook(partialTicks);
			Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
			this.pointedEntity = null;
			Vec3 vec33 = null;
			float f = 1.0F;
			List list = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double) f, (double) f, (double) f), Predicates.and(EntitySelectors.NOT_SPECTATING, new EntityRenderer1(this)));
			double d2 = d1;

			for (int i = 0; i < list.size(); ++i) {
				Entity entity1 = (Entity) list.get(i);
				float f1 = entity1.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double) f1, (double) f1, (double) f1);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

				if (axisalignedbb.isVecInside(vec3)) {
					if (d2 >= 0.0D) {
						this.pointedEntity = entity1;
						vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
						d2 = 0.0D;
					}
				} else if (movingobjectposition != null) {
					double d3 = vec3.distanceTo(movingobjectposition.hitVec);

					if (d3 < d2 || d2 == 0.0D) {
						boolean flag2 = false;

						if (Reflector.ForgeEntity_canRiderInteract.exists()) {
							flag2 = Reflector.callBoolean(entity1, Reflector.ForgeEntity_canRiderInteract, new Object[0]);
						}

						if (entity1 == entity.ridingEntity && !flag2) {
							if (d2 == 0.0D) {
								this.pointedEntity = entity1;
								vec33 = movingobjectposition.hitVec;
							}
						} else {
							this.pointedEntity = entity1;
							vec33 = movingobjectposition.hitVec;
							d2 = d3;
						}
					}
				}
			}

			if (this.pointedEntity != null && flag && vec3.distanceTo(vec33) > 3.0D) {
				this.pointedEntity = null;
				this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, (EnumFacing) null, new BlockPos(vec33));
			}

			if (this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null)) {
				this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);

				if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
					this.mc.pointedEntity = this.pointedEntity;
				}
			}

			this.mc.mcProfiler.endSection();
		}
	}

	/**
	 * Update FOV modifier hand
	 */
	private void updateFovModifierHand() {
		float f = 1.0F;

		if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
			AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) this.mc.getRenderViewEntity();
			f = abstractclientplayer.getFovModifier();
		}

		this.fovModifierHandPrev = this.fovModifierHand;
		this.fovModifierHand += (f - this.fovModifierHand) * 0.5F;

		if (this.fovModifierHand > 1.5F) {
			this.fovModifierHand = 1.5F;
		}

		if (this.fovModifierHand < 0.1F) {
			this.fovModifierHand = 0.1F;
		}
	}

	/**
	 * Changes the field of view of the player depending on if they are underwater
	 * or not
	 */
	private float getFOVModifier(float partialTicks, boolean p_78481_2_) {
		if (this.debugView) {
			return 90.0F;
		} else {
			Entity entity = this.mc.getRenderViewEntity();
			float f = 70.0F;

			if (p_78481_2_) {
				f = this.mc.gameSettings.fovSetting;
				f = f * (this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks);
			}

			boolean flag = false;

			if (this.mc.currentScreen == null) {
				GameSettings gamesettings = this.mc.gameSettings;
				flag = GameSettings.isKeyDown(this.mc.gameSettings.ofKeyBindZoom);
			}

			if (flag) {
				if (!Config.zoomMode) {
					Config.zoomMode = true;
					this.mc.gameSettings.smoothCamera = true;
				}

				if (Config.zoomMode) {
					f /= 4.0F;
				}
			} else if (Config.zoomMode) {
				Config.zoomMode = false;
				this.mc.gameSettings.smoothCamera = false;
				this.mouseFilterXAxis = new MouseFilter();
				this.mouseFilterYAxis = new MouseFilter();
				this.mc.renderGlobal.displayListEntitiesDirty = true;
			}

			if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0.0F) {
				float f1 = (float) ((EntityLivingBase) entity).deathTime + partialTicks;
				f /= (1.0F - 500.0F / (f1 + 500.0F)) * 2.0F + 1.0F;
			}

			Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);

			if (block.getMaterial() == Material.water) {
				f = f * 60.0F / 70.0F;
			}

			return f;
		}
	}

	private void hurtCameraEffect(float partialTicks) {
		if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) this.mc.getRenderViewEntity();
			float f = (float) entitylivingbase.hurtTime - partialTicks;

			if (entitylivingbase.getHealth() <= 0.0F) {
				float f1 = (float) entitylivingbase.deathTime + partialTicks;
				GlStateManager.rotate(40.0F - 8000.0F / (f1 + 200.0F), 0.0F, 0.0F, 1.0F);
			}

			if (f < 0.0F) {
				return;
			}

			f = f / (float) entitylivingbase.maxHurtTime;
			f = MathHelper.sin(f * f * f * f * (float) Math.PI);
			float f2 = entitylivingbase.attackedAtYaw;
			GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-f * 14.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
		}
	}

	/**
	 * Setups all the GL settings for view bobbing. Args: partialTickTime
	 */
	private void setupViewBobbing(float partialTicks) {
		if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
			float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
			float f1 = -(entityplayer.distanceWalkedModified + f * partialTicks);
			float f2 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTicks;
			float f3 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTicks;
			GlStateManager.translate(MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F, -Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2), 0.0F);
			GlStateManager.rotate(MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
		}
	}

	/**
	 * sets up player's eye (or camera in third person mode)
	 */
	private void orientCamera(float partialTicks) {
		Entity entity = this.mc.getRenderViewEntity();
		float f = entity.getEyeHeight();
		double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
		double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
		double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;

		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPlayerSleeping()) {
			f = (float) ((double) f + 1.0D);
			GlStateManager.translate(0.0F, 0.3F, 0.0F);

			if (!this.mc.gameSettings.debugCamEnable) {
				BlockPos blockpos = new BlockPos(entity);
				IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);
				Block block = iblockstate.getBlock();

				if (Reflector.ForgeHooksClient_orientBedCamera.exists()) {
					Reflector.callVoid(Reflector.ForgeHooksClient_orientBedCamera, new Object[] { this.mc.theWorld, blockpos, iblockstate, entity });
				} else if (block == Blocks.bed) {
					int j = ((EnumFacing) iblockstate.getValue(BlockBed.FACING)).getHorizontalIndex();
					GlStateManager.rotate((float) (j * 90), 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
			}
		} else if (this.mc.gameSettings.thirdPersonView > 0) {
			double d3 = (double) (this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks);

			if (this.mc.gameSettings.debugCamEnable) {
				GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
			} else {
				float f1 = entity.rotationYaw;
				float f2 = entity.rotationPitch;

				if (this.mc.gameSettings.thirdPersonView == 2) {
					f2 += 180.0F;
				}

				double d4 = (double) (-MathHelper.sin(f1 / 180.0F * (float) Math.PI) * MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d3;
				double d5 = (double) (MathHelper.cos(f1 / 180.0F * (float) Math.PI) * MathHelper.cos(f2 / 180.0F * (float) Math.PI)) * d3;
				double d6 = (double) (-MathHelper.sin(f2 / 180.0F * (float) Math.PI)) * d3;

				for (int i = 0; i < 8; ++i) {
					float f3 = (float) ((i & 1) * 2 - 1);
					float f4 = (float) ((i >> 1 & 1) * 2 - 1);
					float f5 = (float) ((i >> 2 & 1) * 2 - 1);
					f3 = f3 * 0.1F;
					f4 = f4 * 0.1F;
					f5 = f5 * 0.1F;
					MovingObjectPosition movingobjectposition = this.mc.theWorld.rayTraceBlocks(new Vec3(d0 + (double) f3, d1 + (double) f4, d2 + (double) f5), new Vec3(d0 - d4 + (double) f3 + (double) f5, d1 - d6 + (double) f4, d2 - d5 + (double) f5));

					if (movingobjectposition != null) {
						double d7 = movingobjectposition.hitVec.distanceTo(new Vec3(d0, d1, d2));

						if (d7 < d3) {
							d3 = d7;
						}
					}
				}

				if (this.mc.gameSettings.thirdPersonView == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
				GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
			}
		} else {
			GlStateManager.translate(0.0F, 0.0F, -0.1F);
		}

		if (!this.mc.gameSettings.debugCamEnable) {
			GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);

			if (entity instanceof EntityAnimal) {
				EntityAnimal entityanimal = (EntityAnimal) entity;
				GlStateManager.rotate(entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
			} else {
				GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
			}
		}

		GlStateManager.translate(0.0F, -f, 0.0F);
		d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
		d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
		d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
		this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
	}

	/**
	 * sets up projection, view effects, camera position/rotation
	 */
	private void setupCameraTransform(float partialTicks, int pass) {
		this.farPlaneDistance = (float) (this.mc.gameSettings.renderDistanceChunks * 16);

		if (Config.isFogFancy()) {
			this.farPlaneDistance *= 0.95F;
		}

		if (Config.isFogFast()) {
			this.farPlaneDistance *= 0.83F;
		}

		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		float f = 0.07F;

		if (this.mc.gameSettings.anaglyph) {
			GlStateManager.translate((float) (-(pass * 2 - 1)) * f, 0.0F, 0.0F);
		}

		this.clipDistance = this.farPlaneDistance * 2.0F;

		if (this.clipDistance < 173.0F) {
			this.clipDistance = 173.0F;
		}

		if (this.mc.theWorld.provider.getDimensionId() == 1) {
			this.clipDistance = 256.0F;
		}

		if (this.cameraZoom != 1.0D) {
			GlStateManager.translate((float) this.cameraYaw, (float) (-this.cameraPitch), 0.0F);
			GlStateManager.scale(this.cameraZoom, this.cameraZoom, 1.0D);
		}

		Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.clipDistance);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();

		if (this.mc.gameSettings.anaglyph) {
			GlStateManager.translate((float) (pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
		}

		this.hurtCameraEffect(partialTicks);

		if (this.mc.gameSettings.viewBobbing) {
			this.setupViewBobbing(partialTicks);
		}

		float f1 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;

		if (f1 > 0.0F) {
			byte b0 = 20;

			if (this.mc.thePlayer.isPotionActive(Potion.confusion)) {
				b0 = 7;
			}

			float f2 = 5.0F / (f1 * f1 + 5.0F) - f1 * 0.04F;
			f2 = f2 * f2;
			GlStateManager.rotate(((float) this.rendererUpdateCount + partialTicks) * (float) b0, 0.0F, 1.0F, 1.0F);
			GlStateManager.scale(1.0F / f2, 1.0F, 1.0F);
			GlStateManager.rotate(-((float) this.rendererUpdateCount + partialTicks) * (float) b0, 0.0F, 1.0F, 1.0F);
		}

		this.orientCamera(partialTicks);

		if (this.debugView) {
			switch (this.debugViewDirection) {
			case 0:
				GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				break;

			case 1:
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				break;

			case 2:
				GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
				break;

			case 3:
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				break;

			case 4:
				GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			}
		}
	}

	/**
	 * Render player hand
	 */
	private void renderHand(float partialTicks, int xOffset) {
		if (!this.debugView) {
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			float f = 0.07F;

			if (this.mc.gameSettings.anaglyph) {
				GlStateManager.translate((float) (-(xOffset * 2 - 1)) * f, 0.0F, 0.0F);
			}

			Project.gluPerspective(this.getFOVModifier(partialTicks, false), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();

			if (this.mc.gameSettings.anaglyph) {
				GlStateManager.translate((float) (xOffset * 2 - 1) * 0.1F, 0.0F, 0.0F);
			}

			GlStateManager.pushMatrix();
			this.hurtCameraEffect(partialTicks);

			if (this.mc.gameSettings.viewBobbing) {
				this.setupViewBobbing(partialTicks);
			}

			boolean flag = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) this.mc.getRenderViewEntity()).isPlayerSleeping();

			if (this.mc.gameSettings.thirdPersonView == 0 && !flag && !this.mc.gameSettings.hideGUI && !this.mc.playerController.isSpectator()) {
				this.enableLightmap();
				this.itemRenderer.renderItemInFirstPerson(partialTicks);
				this.disableLightmap();
			}

			GlStateManager.popMatrix();

			if (this.mc.gameSettings.thirdPersonView == 0 && !flag) {
				this.itemRenderer.renderOverlays(partialTicks);
				this.hurtCameraEffect(partialTicks);
			}

			if (this.mc.gameSettings.viewBobbing) {
				this.setupViewBobbing(partialTicks);
			}
		}
	}

	public void disableLightmap() {
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void enableLightmap() {
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		float f = 0.00390625F;
		GlStateManager.scale(f, f, f);
		GlStateManager.translate(8.0F, 8.0F, 8.0F);
		GlStateManager.matrixMode(5888);
		this.mc.getTextureManager().bindTexture(this.locationLightMap);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	/**
	 * Recompute a random value that is applied to block color in updateLightmap()
	 */
	private void updateTorchFlicker() {
		this.torchFlickerDX = (float) ((double) this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
		this.torchFlickerDX = (float) ((double) this.torchFlickerDX * 0.9D);
		this.torchFlickerX += (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
		this.lightmapUpdateNeeded = true;
	}

	private void updateLightmap(float partialTicks) {
		if (this.lightmapUpdateNeeded) {
			this.mc.mcProfiler.startSection("lightTex");
			WorldClient worldclient = this.mc.theWorld;

			if (worldclient != null) {
				if (CustomColorizer.updateLightmap(worldclient, this.torchFlickerX, this.lightmapColors, this.mc.thePlayer.isPotionActive(Potion.nightVision))) {
					this.lightmapTexture.updateDynamicTexture();
					this.lightmapUpdateNeeded = false;
					this.mc.mcProfiler.endSection();
					return;
				}

				float f = worldclient.getSunBrightness(1.0F);
				float f1 = f * 0.95F + 0.05F;

				for (int i = 0; i < 256; ++i) {
					float f2 = worldclient.provider.getLightBrightnessTable()[i / 16] * f1;
					float f3 = worldclient.provider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);

					if (worldclient.getLastLightningBolt() > 0) {
						f2 = worldclient.provider.getLightBrightnessTable()[i / 16];
					}

					float f4 = f2 * (f * 0.65F + 0.35F);
					float f5 = f2 * (f * 0.65F + 0.35F);
					float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
					float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
					float f8 = f4 + f3;
					float f9 = f5 + f6;
					float f10 = f2 + f7;
					f8 = f8 * 0.96F + 0.03F;
					f9 = f9 * 0.96F + 0.03F;
					f10 = f10 * 0.96F + 0.03F;

					if (this.bossColorModifier > 0.0F) {
						float f11 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
						f8 = f8 * (1.0F - f11) + f8 * 0.7F * f11;
						f9 = f9 * (1.0F - f11) + f9 * 0.6F * f11;
						f10 = f10 * (1.0F - f11) + f10 * 0.6F * f11;
					}

					if (worldclient.provider.getDimensionId() == 1) {
						f8 = 0.22F + f3 * 0.75F;
						f9 = 0.28F + f6 * 0.75F;
						f10 = 0.25F + f7 * 0.75F;
					}

					if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
						float f15 = this.getNightVisionBrightness(this.mc.thePlayer, partialTicks);
						float f12 = 1.0F / f8;

						if (f12 > 1.0F / f9) {
							f12 = 1.0F / f9;
						}

						if (f12 > 1.0F / f10) {
							f12 = 1.0F / f10;
						}

						f8 = f8 * (1.0F - f15) + f8 * f12 * f15;
						f9 = f9 * (1.0F - f15) + f9 * f12 * f15;
						f10 = f10 * (1.0F - f15) + f10 * f12 * f15;
					}

					if (f8 > 1.0F) {
						f8 = 1.0F;
					}

					if (f9 > 1.0F) {
						f9 = 1.0F;
					}

					if (f10 > 1.0F) {
						f10 = 1.0F;
					}

					float f16 = this.mc.gameSettings.gammaSetting;
					float f17 = 1.0F - f8;
					float f13 = 1.0F - f9;
					float f14 = 1.0F - f10;
					f17 = 1.0F - f17 * f17 * f17 * f17;
					f13 = 1.0F - f13 * f13 * f13 * f13;
					f14 = 1.0F - f14 * f14 * f14 * f14;
					f8 = f8 * (1.0F - f16) + f17 * f16;
					f9 = f9 * (1.0F - f16) + f13 * f16;
					f10 = f10 * (1.0F - f16) + f14 * f16;
					f8 = f8 * 0.96F + 0.03F;
					f9 = f9 * 0.96F + 0.03F;
					f10 = f10 * 0.96F + 0.03F;

					if (f8 > 1.0F) {
						f8 = 1.0F;
					}

					if (f9 > 1.0F) {
						f9 = 1.0F;
					}

					if (f10 > 1.0F) {
						f10 = 1.0F;
					}

					if (f8 < 0.0F) {
						f8 = 0.0F;
					}

					if (f9 < 0.0F) {
						f9 = 0.0F;
					}

					if (f10 < 0.0F) {
						f10 = 0.0F;
					}

					short short1 = 255;
					int j = (int) (f8 * 255.0F);
					int k = (int) (f9 * 255.0F);
					int l = (int) (f10 * 255.0F);
					this.lightmapColors[i] = short1 << 24 | j << 16 | k << 8 | l;
				}

				this.lightmapTexture.updateDynamicTexture();
				this.lightmapUpdateNeeded = false;
				this.mc.mcProfiler.endSection();
			}
		}
	}

	private float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks) {
		int i = entitylivingbaseIn.getActivePotionEffect(Potion.nightVision).getDuration();
		return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float) i - partialTicks) * (float) Math.PI * 0.2F) * 0.3F;
	}

	public void func_181560_a(float p_181560_1_, long p_181560_2_) {
		this.frameInit();
		boolean flag = Display.isActive();

		if (!flag && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
			if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
				this.mc.displayInGameMenu();
			}
		} else {
			this.prevFrameTime = Minecraft.getSystemTime();
		}

		this.mc.mcProfiler.startSection("mouse");

		if (flag && Minecraft.isRunningOnMac && this.mc.inGameHasFocus && !Mouse.isInsideWindow()) {
			Mouse.setGrabbed(false);
			Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
			Mouse.setGrabbed(true);
		}

		if (this.mc.inGameHasFocus && flag) {
			this.mc.mouseHelper.mouseXYChange();
			float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f1 = f * f * f * 8.0F;
			float f2 = (float) this.mc.mouseHelper.deltaX * f1;
			float f3 = (float) this.mc.mouseHelper.deltaY * f1;
			byte b0 = 1;

			if (this.mc.gameSettings.invertMouse) {
				b0 = -1;
			}

			if (this.mc.gameSettings.smoothCamera) {
				this.smoothCamYaw += f2;
				this.smoothCamPitch += f3;
				float f4 = p_181560_1_ - this.smoothCamPartialTicks;
				this.smoothCamPartialTicks = p_181560_1_;
				f2 = this.smoothCamFilterX * f4;
				f3 = this.smoothCamFilterY * f4;
				this.mc.thePlayer.setAngles(f2, f3 * (float) b0);
			} else {
				this.smoothCamYaw = 0.0F;
				this.smoothCamPitch = 0.0F;
				this.mc.thePlayer.setAngles(f2, f3 * (float) b0);
			}
		}

		this.mc.mcProfiler.endSection();

		if (!this.mc.skipRenderWorld) {
			anaglyphEnable = this.mc.gameSettings.anaglyph;
			final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
			int l = scaledresolution.getScaledWidth();
			int i1 = scaledresolution.getScaledHeight();
			final int j1 = Mouse.getX() * l / this.mc.displayWidth;
			final int k1 = i1 - Mouse.getY() * i1 / this.mc.displayHeight - 1;
			int l1 = this.mc.gameSettings.limitFramerate;

			if (this.mc.theWorld != null) {
				this.mc.mcProfiler.startSection("level");
				int i = Math.min(Minecraft.getDebugFPS(), l1);
				i = Math.max(i, 60);
				long j = System.nanoTime() - p_181560_2_;
				long k = Math.max((long) (1000000000 / i / 4) - j, 0L);
				this.renderWorld(p_181560_1_, System.nanoTime() + k);

				if (OpenGlHelper.shadersSupported) {
					this.mc.renderGlobal.renderEntityOutlineFramebuffer();

					if (this.theShaderGroup != null && this.useShader) {
						GlStateManager.matrixMode(5890);
						GlStateManager.pushMatrix();
						GlStateManager.loadIdentity();
						this.theShaderGroup.loadShaderGroup(p_181560_1_);
						GlStateManager.popMatrix();
					}

					this.mc.getFramebuffer().bindFramebuffer(true);
				}

				this.renderEndNanoTime = System.nanoTime();
				this.mc.mcProfiler.endStartSection("gui");

				if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
					GlStateManager.alphaFunc(516, 0.1F);
					this.mc.ingameGUI.renderGameOverlay(p_181560_1_);

					if (this.mc.gameSettings.ofShowFps && !this.mc.gameSettings.showDebugInfo) {
						Config.drawFps();
					}

					if (this.mc.gameSettings.showDebugInfo) {
						Lagometer.showLagometer(scaledresolution);
					}
				}

				this.mc.mcProfiler.endSection();
			} else {
				GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				this.setupOverlayRendering();
				this.renderEndNanoTime = System.nanoTime();
			}

			if (this.mc.currentScreen != null) {
				GlStateManager.clear(256);

				try {
					if (Reflector.ForgeHooksClient_drawScreen.exists()) {
						Reflector.callVoid(Reflector.ForgeHooksClient_drawScreen, new Object[] { this.mc.currentScreen, Integer.valueOf(j1), Integer.valueOf(k1), Float.valueOf(p_181560_1_) });
					} else {
						this.mc.currentScreen.drawScreen(j1, k1, p_181560_1_);
					}
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
					crashreportcategory.addCrashSectionCallable("Screen name", new EntityRenderer2(this));
					crashreportcategory.addCrashSectionCallable("Mouse location", new Callable() {
						

						public String call() throws Exception {
							return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", new Object[] { Integer.valueOf(j1), Integer.valueOf(k1), Integer.valueOf(Mouse.getX()), Integer.valueOf(Mouse.getY()) });
						}
					});
					crashreportcategory.addCrashSectionCallable("Screen size", new Callable() {
						

						public String call() throws Exception {
							return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", new Object[] { Integer.valueOf(scaledresolution.getScaledWidth()), Integer.valueOf(scaledresolution.getScaledHeight()), Integer.valueOf(EntityRenderer.this.mc.displayWidth), Integer.valueOf(EntityRenderer.this.mc.displayHeight), Integer.valueOf(scaledresolution.getScaleFactor()) });
						}
					});
					throw new ReportedException(crashreport);
				}
			}
		}

		this.frameFinish();
		this.waitForServerThread();
		Lagometer.updateLagometer();

		if (this.mc.gameSettings.ofProfiler) {
			this.mc.gameSettings.showDebugProfilerChart = true;
		}
	}

	public void renderStreamIndicator(float partialTicks) {
		this.setupOverlayRendering();
		this.mc.ingameGUI.renderStreamIndicator(new ScaledResolution(this.mc));
	}

	private boolean isDrawBlockOutline() {
		if (!this.drawBlockOutline) {
			return false;
		} else {
			Entity entity = this.mc.getRenderViewEntity();
			boolean flag = entity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;

			if (flag && !((EntityPlayer) entity).capabilities.allowEdit) {
				ItemStack itemstack = ((EntityPlayer) entity).getCurrentEquippedItem();

				if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
					BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
					Block block = this.mc.theWorld.getBlockState(blockpos).getBlock();

					if (this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
						boolean flag1;

						if (Reflector.ForgeBlock_hasTileEntity.exists()) {
							IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);
							flag1 = Reflector.callBoolean(block, Reflector.ForgeBlock_hasTileEntity, new Object[] { iblockstate });
						} else {
							flag1 = block.hasTileEntity();
						}

						flag = flag1 && this.mc.theWorld.getTileEntity(blockpos) instanceof IInventory;
					} else {
						flag = itemstack != null && (itemstack.canDestroy(block) || itemstack.canPlaceOn(block));
					}
				}
			}

			return flag;
		}
	}

	private void renderWorldDirections(float partialTicks) {
		if (this.mc.gameSettings.showDebugInfo && !this.mc.gameSettings.hideGUI && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo) {
			Entity entity = this.mc.getRenderViewEntity();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GL11.glLineWidth(1.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			this.orientCamera(partialTicks);
			GlStateManager.translate(0.0F, entity.getEyeHeight(), 0.0F);
			RenderGlobal.func_181563_a(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.005D, 1.0E-4D, 1.0E-4D), 255, 0, 0, 255);
			RenderGlobal.func_181563_a(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0E-4D, 1.0E-4D, 0.005D), 0, 0, 255, 255);
			RenderGlobal.func_181563_a(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0E-4D, 0.0033D, 1.0E-4D), 0, 255, 0, 255);
			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
		}
	}

	public void renderWorld(float partialTicks, long finishTimeNano) {
		this.updateLightmap(partialTicks);

		if (this.mc.getRenderViewEntity() == null) {
			this.mc.setRenderViewEntity(this.mc.thePlayer);
		}

		this.getMouseOver(partialTicks);
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		this.mc.mcProfiler.startSection("center");

		if (this.mc.gameSettings.anaglyph) {
			anaglyphField = 0;
			GlStateManager.colorMask(false, true, true, false);
			this.renderWorldPass(0, partialTicks, finishTimeNano);
			anaglyphField = 1;
			GlStateManager.colorMask(true, false, false, false);
			this.renderWorldPass(1, partialTicks, finishTimeNano);
			GlStateManager.colorMask(true, true, true, false);
		} else {
			this.renderWorldPass(2, partialTicks, finishTimeNano);
		}

		this.mc.mcProfiler.endSection();
	}

	private void renderWorldPass(int pass, float partialTicks, long finishTimeNano) {
		RenderGlobal renderglobal = this.mc.renderGlobal;
		EffectRenderer effectrenderer = this.mc.effectRenderer;
		boolean flag = this.isDrawBlockOutline();
		GlStateManager.enableCull();
		this.mc.mcProfiler.endStartSection("clear");
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		this.updateFogColor(partialTicks);
		GlStateManager.clear(16640);
		this.mc.mcProfiler.endStartSection("camera");
		this.setupCameraTransform(partialTicks, pass);
		ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
		this.mc.mcProfiler.endStartSection("frustum");
		ClippingHelperImpl.getInstance();
		this.mc.mcProfiler.endStartSection("culling");
		Frustum frustum = new Frustum();
		Entity entity = this.mc.getRenderViewEntity();
		double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
		frustum.setPosition(d0, d1, d2);

		if (!Config.isSkyEnabled() && !Config.isSunMoonEnabled() && !Config.isStarsEnabled()) {
			GlStateManager.disableBlend();
		} else {
			this.setupFog(-1, partialTicks);
			this.mc.mcProfiler.endStartSection("sky");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.clipDistance);
			GlStateManager.matrixMode(5888);
			renderglobal.renderSky(partialTicks, pass);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.clipDistance);
			GlStateManager.matrixMode(5888);
		}

		this.setupFog(0, partialTicks);
		GlStateManager.shadeModel(7425);

		if (entity.posY + (double) entity.getEyeHeight() < 128.0D + (double) (this.mc.gameSettings.ofCloudsHeight * 128.0F)) {
			this.renderCloudsCheck(renderglobal, partialTicks, pass);
		}

		this.mc.mcProfiler.endStartSection("prepareterrain");
		this.setupFog(0, partialTicks);
		this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		RenderHelper.disableStandardItemLighting();
		this.mc.mcProfiler.endStartSection("terrain_setup");
		renderglobal.setupTerrain(entity, (double) partialTicks, frustum, this.frameCount++, this.mc.thePlayer.isSpectator());

		if (pass == 0 || pass == 2) {
			this.mc.mcProfiler.endStartSection("updatechunks");
			Lagometer.timerChunkUpload.start();
			this.mc.renderGlobal.updateChunks(finishTimeNano);
			Lagometer.timerChunkUpload.end();
		}

		this.mc.mcProfiler.endStartSection("terrain");
		Lagometer.timerTerrain.start();

		if (this.mc.gameSettings.ofSmoothFps && pass > 0) {
			GL11.glFinish();
		}

		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlpha();
		renderglobal.renderBlockLayer(EnumWorldBlockLayer.SOLID, (double) partialTicks, pass, entity);
		GlStateManager.enableAlpha();
		renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, (double) partialTicks, pass, entity);
		this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
		renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, (double) partialTicks, pass, entity);
		this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
		Lagometer.timerTerrain.end();
		GlStateManager.shadeModel(7424);
		GlStateManager.alphaFunc(516, 0.1F);

		if (!this.debugView) {
			GlStateManager.matrixMode(5888);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			RenderHelper.enableStandardItemLighting();
			this.mc.mcProfiler.endStartSection("entities");

			if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
				Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, new Object[] { Integer.valueOf(0) });
			}

			renderglobal.renderEntities(entity, frustum, partialTicks);

			if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
				Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, new Object[] { Integer.valueOf(-1) });
			}

			RenderHelper.disableStandardItemLighting();
			this.disableLightmap();
			GlStateManager.matrixMode(5888);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();

			if (this.mc.objectMouseOver != null && entity.isInsideOfMaterial(Material.water) && flag) {
				EntityPlayer entityplayer = (EntityPlayer) entity;
				GlStateManager.disableAlpha();
				this.mc.mcProfiler.endStartSection("outline");
				boolean flag1 = Reflector.ForgeHooksClient_onDrawBlockHighlight.exists();

				if ((!flag1 || !Reflector.callBoolean(Reflector.ForgeHooksClient_onDrawBlockHighlight, new Object[] { renderglobal, entityplayer, this.mc.objectMouseOver, Integer.valueOf(0), entityplayer.getHeldItem(), Float.valueOf(partialTicks) })) && !this.mc.gameSettings.hideGUI) {
					renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, partialTicks);
				}
				GlStateManager.enableAlpha();
			}
		}

		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();

		if (flag && this.mc.objectMouseOver != null && !entity.isInsideOfMaterial(Material.water)) {
			EntityPlayer entityplayer1 = (EntityPlayer) entity;
			GlStateManager.disableAlpha();
			this.mc.mcProfiler.endStartSection("outline");
			boolean flag2 = Reflector.ForgeHooksClient_onDrawBlockHighlight.exists();

			if ((!flag2 || !Reflector.callBoolean(Reflector.ForgeHooksClient_onDrawBlockHighlight, new Object[] { renderglobal, entityplayer1, this.mc.objectMouseOver, Integer.valueOf(0), entityplayer1.getHeldItem(), Float.valueOf(partialTicks) })) && !this.mc.gameSettings.hideGUI) {
				renderglobal.drawSelectionBox(entityplayer1, this.mc.objectMouseOver, 0, partialTicks);
			}
			GlStateManager.enableAlpha();
		}

		this.mc.mcProfiler.endStartSection("destroyProgress");
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
		this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
		renderglobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getWorldRenderer(), entity, partialTicks);
		this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
		GlStateManager.disableBlend();

		if (!this.debugView) {
			this.enableLightmap();
			this.mc.mcProfiler.endStartSection("litParticles");
			effectrenderer.renderLitParticles(entity, partialTicks);
			RenderHelper.disableStandardItemLighting();
			this.setupFog(0, partialTicks);
			this.mc.mcProfiler.endStartSection("particles");
			effectrenderer.renderParticles(entity, partialTicks);
			this.disableLightmap();
		}

		GlStateManager.depthMask(false);
		GlStateManager.enableCull();
		this.mc.mcProfiler.endStartSection("weather");
		this.renderRainSnow(partialTicks);
		GlStateManager.depthMask(true);
		renderglobal.renderWorldBorder(entity, partialTicks);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.alphaFunc(516, 0.1F);
		this.setupFog(0, partialTicks);
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		GlStateManager.shadeModel(7425);
		this.mc.mcProfiler.endStartSection("translucent");
		renderglobal.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, (double) partialTicks, pass, entity);

		if (Reflector.ForgeHooksClient_setRenderPass.exists() && !this.debugView) {
			RenderHelper.enableStandardItemLighting();
			this.mc.mcProfiler.endStartSection("entities");
			Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, new Object[] { Integer.valueOf(1) });
			this.mc.renderGlobal.renderEntities(entity, frustum, partialTicks);
			Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, new Object[] { Integer.valueOf(-1) });
			RenderHelper.disableStandardItemLighting();
		}

		GlStateManager.shadeModel(7424);
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableFog();

		if (entity.posY + (double) entity.getEyeHeight() >= 128.0D + (double) (this.mc.gameSettings.ofCloudsHeight * 128.0F)) {
			this.mc.mcProfiler.endStartSection("aboveClouds");
			this.renderCloudsCheck(renderglobal, partialTicks, pass);
		}

		if (Reflector.ForgeHooksClient_dispatchRenderLast.exists()) {
			this.mc.mcProfiler.endStartSection("forge_render_last");
			Reflector.callVoid(Reflector.ForgeHooksClient_dispatchRenderLast, new Object[] { renderglobal, Float.valueOf(partialTicks) });
		}

		BaseClient.instance.getEventManager().call(new Render3DEvent(partialTicks));

		this.mc.mcProfiler.endStartSection("hand");
		boolean flag3 = Reflector.callBoolean(Reflector.ForgeHooksClient_renderFirstPersonHand, new Object[] { this.mc.renderGlobal, Float.valueOf(partialTicks), Integer.valueOf(pass) });

		if (!flag3 && this.renderHand) {
			GlStateManager.clear(256);
			this.renderHand(partialTicks, pass);
			this.renderWorldDirections(partialTicks);
		}
	}

	private void renderCloudsCheck(RenderGlobal renderGlobalIn, float partialTicks, int pass) {
		if (this.mc.gameSettings.renderDistanceChunks >= 4 && !Config.isCloudsOff()) {
			this.mc.mcProfiler.endStartSection("clouds");
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.clipDistance * 4.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.pushMatrix();
			this.setupFog(0, partialTicks);
			renderGlobalIn.renderClouds(partialTicks, pass);
			GlStateManager.disableFog();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.clipDistance);
			GlStateManager.matrixMode(5888);
		}
	}

	private void addRainParticles() {
		float f = this.mc.theWorld.getRainStrength(1.0F);

		if (!Config.isRainFancy()) {
			f /= 2.0F;
		}

		if (f != 0.0F && Config.isRainSplash()) {
			this.random.setSeed((long) this.rendererUpdateCount * 312987231L);
			Entity entity = this.mc.getRenderViewEntity();
			WorldClient worldclient = this.mc.theWorld;
			BlockPos blockpos = new BlockPos(entity);
			byte b0 = 10;
			double d0 = 0.0D;
			double d1 = 0.0D;
			double d2 = 0.0D;
			int i = 0;
			int j = (int) (100.0F * f * f);

			if (this.mc.gameSettings.particleSetting == 1) {
				j >>= 1;
			} else if (this.mc.gameSettings.particleSetting == 2) {
				j = 0;
			}

			for (int k = 0; k < j; ++k) {
				BlockPos blockpos1 = worldclient.getPrecipitationHeight(blockpos.add(this.random.nextInt(b0) - this.random.nextInt(b0), 0, this.random.nextInt(b0) - this.random.nextInt(b0)));
				BiomeGenBase biomegenbase = worldclient.getBiomeGenForCoords(blockpos1);
				BlockPos blockpos2 = blockpos1.down();
				Block block = worldclient.getBlockState(blockpos2).getBlock();

				if (blockpos1.getY() <= blockpos.getY() + b0 && blockpos1.getY() >= blockpos.getY() - b0 && biomegenbase.canSpawnLightningBolt() && biomegenbase.getFloatTemperature(blockpos1) >= 0.15F) {
					double d3 = this.random.nextDouble();
					double d4 = this.random.nextDouble();

					if (block.getMaterial() == Material.lava) {
						this.mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double) blockpos1.getX() + d3, (double) ((float) blockpos1.getY() + 0.1F) - block.getBlockBoundsMinY(), (double) blockpos1.getZ() + d4, 0.0D, 0.0D, 0.0D, new int[0]);
					} else if (block.getMaterial() != Material.air) {
						block.setBlockBoundsBasedOnState(worldclient, blockpos2);
						++i;

						if (this.random.nextInt(i) == 0) {
							d0 = (double) blockpos2.getX() + d3;
							d1 = (double) ((float) blockpos2.getY() + 0.1F) + block.getBlockBoundsMaxY() - 1.0D;
							d2 = (double) blockpos2.getZ() + d4;
						}

						this.mc.theWorld.spawnParticle(EnumParticleTypes.WATER_DROP, (double) blockpos2.getX() + d3, (double) ((float) blockpos2.getY() + 0.1F) + block.getBlockBoundsMaxY(), (double) blockpos2.getZ() + d4, 0.0D, 0.0D, 0.0D, new int[0]);
					}
				}
			}

			if (i > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
				this.rainSoundCounter = 0;

				if (d1 > (double) (blockpos.getY() + 1) && worldclient.getPrecipitationHeight(blockpos).getY() > MathHelper.floor_float((float) blockpos.getY())) {
					this.mc.theWorld.playSound(d0, d1, d2, "ambient.weather.rain", 0.1F, 0.5F, false);
				} else {
					this.mc.theWorld.playSound(d0, d1, d2, "ambient.weather.rain", 0.2F, 1.0F, false);
				}
			}
		}
	}

	/**
	 * Render rain and snow
	 */
	protected void renderRainSnow(float partialTicks) {
		if (Reflector.ForgeWorldProvider_getWeatherRenderer.exists()) {
			WorldProvider worldprovider = this.mc.theWorld.provider;
			Object object = Reflector.call(worldprovider, Reflector.ForgeWorldProvider_getWeatherRenderer, new Object[0]);

			if (object != null) {
				Reflector.callVoid(object, Reflector.IRenderHandler_render, new Object[] { Float.valueOf(partialTicks), this.mc.theWorld, this.mc });
				return;
			}
		}

		float f5 = this.mc.theWorld.getRainStrength(partialTicks);

		if (f5 > 0.0F) {
			if (Config.isRainOff()) {
				return;
			}

			this.enableLightmap();
			Entity entity = this.mc.getRenderViewEntity();
			WorldClient worldclient = this.mc.theWorld;
			int i = MathHelper.floor_double(entity.posX);
			int j = MathHelper.floor_double(entity.posY);
			int k = MathHelper.floor_double(entity.posZ);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			GlStateManager.disableCull();
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.alphaFunc(516, 0.1F);
			double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
			double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
			double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
			int l = MathHelper.floor_double(d1);
			byte b0 = 5;

			if (Config.isRainFancy()) {
				b0 = 10;
			}

			byte b1 = -1;
			float f = (float) this.rendererUpdateCount + partialTicks;
			worldrenderer.setTranslation(-d0, -d1, -d2);

			if (Config.isRainFancy()) {
				b0 = 10;
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

			for (int i1 = k - b0; i1 <= k + b0; ++i1) {
				for (int j1 = i - b0; j1 <= i + b0; ++j1) {
					int k1 = (i1 - k + 16) * 32 + j1 - i + 16;
					double d3 = (double) this.rainXCoords[k1] * 0.5D;
					double d4 = (double) this.rainYCoords[k1] * 0.5D;
					blockpos$mutableblockpos.func_181079_c(j1, 0, i1);
					BiomeGenBase biomegenbase = worldclient.getBiomeGenForCoords(blockpos$mutableblockpos);

					if (biomegenbase.canSpawnLightningBolt() || biomegenbase.getEnableSnow()) {
						int l1 = worldclient.getPrecipitationHeight(blockpos$mutableblockpos).getY();
						int i2 = j - b0;
						int j2 = j + b0;

						if (i2 < l1) {
							i2 = l1;
						}

						if (j2 < l1) {
							j2 = l1;
						}

						int k2 = l1;

						if (l1 < l) {
							k2 = l;
						}

						if (i2 != j2) {
							this.random.setSeed((long) (j1 * j1 * 3121 + j1 * 45238971 ^ i1 * i1 * 418711 + i1 * 13761));
							blockpos$mutableblockpos.func_181079_c(j1, i2, i1);
							float f1 = biomegenbase.getFloatTemperature(blockpos$mutableblockpos);

							if (worldclient.getWorldChunkManager().getTemperatureAtHeight(f1, l1) >= 0.15F) {
								if (b1 != 0) {
									if (b1 >= 0) {
										tessellator.draw();
									}

									b1 = 0;
									this.mc.getTextureManager().bindTexture(locationRainPng);
									worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
								}

								double d5 = ((double) (this.rendererUpdateCount + j1 * j1 * 3121 + j1 * 45238971 + i1 * i1 * 418711 + i1 * 13761 & 31) + (double) partialTicks) / 32.0D * (3.0D + this.random.nextDouble());
								double d6 = (double) ((float) j1 + 0.5F) - entity.posX;
								double d7 = (double) ((float) i1 + 0.5F) - entity.posZ;
								float f2 = MathHelper.sqrt_double(d6 * d6 + d7 * d7) / (float) b0;
								float f3 = ((1.0F - f2 * f2) * 0.5F + 0.5F) * f5;
								blockpos$mutableblockpos.func_181079_c(j1, k2, i1);
								int l2 = worldclient.getCombinedLight(blockpos$mutableblockpos, 0);
								int i3 = l2 >> 16 & 65535;
								int j3 = l2 & 65535;
								worldrenderer.pos((double) j1 - d3 + 0.5D, (double) i2, (double) i1 - d4 + 0.5D).tex(0.0D, (double) i2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f3).lightmap(i3, j3).endVertex();
								worldrenderer.pos((double) j1 + d3 + 0.5D, (double) i2, (double) i1 + d4 + 0.5D).tex(1.0D, (double) i2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f3).lightmap(i3, j3).endVertex();
								worldrenderer.pos((double) j1 + d3 + 0.5D, (double) j2, (double) i1 + d4 + 0.5D).tex(1.0D, (double) j2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f3).lightmap(i3, j3).endVertex();
								worldrenderer.pos((double) j1 - d3 + 0.5D, (double) j2, (double) i1 - d4 + 0.5D).tex(0.0D, (double) j2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f3).lightmap(i3, j3).endVertex();
							} else {
								if (b1 != 1) {
									if (b1 >= 0) {
										tessellator.draw();
									}

									b1 = 1;
									this.mc.getTextureManager().bindTexture(locationSnowPng);
									worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
								}

								double d8 = (double) (((float) (this.rendererUpdateCount & 511) + partialTicks) / 512.0F);
								double d9 = this.random.nextDouble() + (double) f * 0.01D * (double) ((float) this.random.nextGaussian());
								double d10 = this.random.nextDouble() + (double) (f * (float) this.random.nextGaussian()) * 0.001D;
								double d11 = (double) ((float) j1 + 0.5F) - entity.posX;
								double d12 = (double) ((float) i1 + 0.5F) - entity.posZ;
								float f6 = MathHelper.sqrt_double(d11 * d11 + d12 * d12) / (float) b0;
								float f4 = ((1.0F - f6 * f6) * 0.3F + 0.5F) * f5;
								blockpos$mutableblockpos.func_181079_c(j1, k2, i1);
								int k3 = (worldclient.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 15728880) / 4;
								int l3 = k3 >> 16 & 65535;
								int i4 = k3 & 65535;
								worldrenderer.pos((double) j1 - d3 + 0.5D, (double) i2, (double) i1 - d4 + 0.5D).tex(0.0D + d9, (double) i2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f4).lightmap(l3, i4).endVertex();
								worldrenderer.pos((double) j1 + d3 + 0.5D, (double) i2, (double) i1 + d4 + 0.5D).tex(1.0D + d9, (double) i2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f4).lightmap(l3, i4).endVertex();
								worldrenderer.pos((double) j1 + d3 + 0.5D, (double) j2, (double) i1 + d4 + 0.5D).tex(1.0D + d9, (double) j2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f4).lightmap(l3, i4).endVertex();
								worldrenderer.pos((double) j1 - d3 + 0.5D, (double) j2, (double) i1 - d4 + 0.5D).tex(0.0D + d9, (double) j2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f4).lightmap(l3, i4).endVertex();
							}
						}
					}
				}
			}

			if (b1 >= 0) {
				tessellator.draw();
			}

			worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(516, 0.1F);
			this.disableLightmap();
		}
	}

	/**
	 * Setup orthogonal projection for rendering GUI screen overlays
	 */
	public void setupOverlayRendering() {
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		GlStateManager.clear(256);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
	}

	/**
	 * calculates fog and calls glClearColor
	 */
	private void updateFogColor(float partialTicks) {
		WorldClient worldclient = this.mc.theWorld;
		Entity entity = this.mc.getRenderViewEntity();
		float f = 0.25F + 0.75F * (float) this.mc.gameSettings.renderDistanceChunks / 32.0F;
		f = 1.0F - (float) Math.pow((double) f, 0.25D);
		Vec3 vec3 = worldclient.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
		vec3 = CustomColorizer.getWorldSkyColor(vec3, worldclient, this.mc.getRenderViewEntity(), partialTicks);
		float f1 = (float) vec3.xCoord;
		float f2 = (float) vec3.yCoord;
		float f3 = (float) vec3.zCoord;
		Vec3 vec31 = worldclient.getFogColor(partialTicks);
		vec31 = CustomColorizer.getWorldFogColor(vec31, worldclient, this.mc.getRenderViewEntity(), partialTicks);
		this.fogColorRed = (float) vec31.xCoord;
		this.fogColorGreen = (float) vec31.yCoord;
		this.fogColorBlue = (float) vec31.zCoord;

		if (this.mc.gameSettings.renderDistanceChunks >= 4) {
			double d0 = -1.0D;
			Vec3 vec32 = MathHelper.sin(worldclient.getCelestialAngleRadians(partialTicks)) > 0.0F ? new Vec3(d0, 0.0D, 0.0D) : new Vec3(1.0D, 0.0D, 0.0D);
			float f4 = (float) entity.getLook(partialTicks).dotProduct(vec32);

			if (f4 < 0.0F) {
				f4 = 0.0F;
			}

			if (f4 > 0.0F) {
				float[] afloat = worldclient.provider.calcSunriseSunsetColors(worldclient.getCelestialAngle(partialTicks), partialTicks);

				if (afloat != null) {
					f4 = f4 * afloat[3];
					this.fogColorRed = this.fogColorRed * (1.0F - f4) + afloat[0] * f4;
					this.fogColorGreen = this.fogColorGreen * (1.0F - f4) + afloat[1] * f4;
					this.fogColorBlue = this.fogColorBlue * (1.0F - f4) + afloat[2] * f4;
				}
			}
		}

		this.fogColorRed += (f1 - this.fogColorRed) * f;
		this.fogColorGreen += (f2 - this.fogColorGreen) * f;
		this.fogColorBlue += (f3 - this.fogColorBlue) * f;
		float f10 = worldclient.getRainStrength(partialTicks);

		if (f10 > 0.0F) {
			float f5 = 1.0F - f10 * 0.5F;
			float f12 = 1.0F - f10 * 0.4F;
			this.fogColorRed *= f5;
			this.fogColorGreen *= f5;
			this.fogColorBlue *= f12;
		}

		float f11 = worldclient.getThunderStrength(partialTicks);

		if (f11 > 0.0F) {
			float f13 = 1.0F - f11 * 0.5F;
			this.fogColorRed *= f13;
			this.fogColorGreen *= f13;
			this.fogColorBlue *= f13;
		}

		Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);

		if (this.cloudFog) {
			Vec3 vec33 = worldclient.getCloudColour(partialTicks);
			this.fogColorRed = (float) vec33.xCoord;
			this.fogColorGreen = (float) vec33.yCoord;
			this.fogColorBlue = (float) vec33.zCoord;
		} else if (block.getMaterial() == Material.water) {
			float f8 = (float) EnchantmentHelper.getRespiration(entity) * 0.2F;

			if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(Potion.waterBreathing)) {
				f8 = f8 * 0.3F + 0.6F;
			}

			this.fogColorRed = 0.02F + f8;
			this.fogColorGreen = 0.02F + f8;
			this.fogColorBlue = 0.2F + f8;
			Vec3 vec34 = CustomColorizer.getUnderwaterColor(this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0D, this.mc.getRenderViewEntity().posZ);

			if (vec34 != null) {
				this.fogColorRed = (float) vec34.xCoord;
				this.fogColorGreen = (float) vec34.yCoord;
				this.fogColorBlue = (float) vec34.zCoord;
			}
		} else if (block.getMaterial() == Material.lava) {
			this.fogColorRed = 0.6F;
			this.fogColorGreen = 0.1F;
			this.fogColorBlue = 0.0F;
		}

		float f9 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks;
		this.fogColorRed *= f9;
		this.fogColorGreen *= f9;
		this.fogColorBlue *= f9;
		double d2 = worldclient.provider.getVoidFogYFactor();
		double d1 = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks) * d2;

		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(Potion.blindness)) {
			int i = ((EntityLivingBase) entity).getActivePotionEffect(Potion.blindness).getDuration();

			if (i < 20) {
				d1 *= (double) (1.0F - (float) i / 20.0F);
			} else {
				d1 = 0.0D;
			}
		}

		if (d1 < 1.0D) {
			if (d1 < 0.0D) {
				d1 = 0.0D;
			}

			d1 = d1 * d1;
			this.fogColorRed = (float) ((double) this.fogColorRed * d1);
			this.fogColorGreen = (float) ((double) this.fogColorGreen * d1);
			this.fogColorBlue = (float) ((double) this.fogColorBlue * d1);
		}

		if (this.bossColorModifier > 0.0F) {
			float f14 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
			this.fogColorRed = this.fogColorRed * (1.0F - f14) + this.fogColorRed * 0.7F * f14;
			this.fogColorGreen = this.fogColorGreen * (1.0F - f14) + this.fogColorGreen * 0.6F * f14;
			this.fogColorBlue = this.fogColorBlue * (1.0F - f14) + this.fogColorBlue * 0.6F * f14;
		}

		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(Potion.nightVision)) {
			float f15 = this.getNightVisionBrightness((EntityLivingBase) entity, partialTicks);
			float f6 = 1.0F / this.fogColorRed;

			if (f6 > 1.0F / this.fogColorGreen) {
				f6 = 1.0F / this.fogColorGreen;
			}

			if (f6 > 1.0F / this.fogColorBlue) {
				f6 = 1.0F / this.fogColorBlue;
			}

			this.fogColorRed = this.fogColorRed * (1.0F - f15) + this.fogColorRed * f6 * f15;
			this.fogColorGreen = this.fogColorGreen * (1.0F - f15) + this.fogColorGreen * f6 * f15;
			this.fogColorBlue = this.fogColorBlue * (1.0F - f15) + this.fogColorBlue * f6 * f15;
		}

		if (this.mc.gameSettings.anaglyph) {
			float f16 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
			float f17 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
			float f7 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
			this.fogColorRed = f16;
			this.fogColorGreen = f17;
			this.fogColorBlue = f7;
		}

		if (Reflector.EntityViewRenderEvent_FogColors_Constructor.exists()) {
			Object object = Reflector.newInstance(Reflector.EntityViewRenderEvent_FogColors_Constructor, new Object[] { this, entity, block, Float.valueOf(partialTicks), Float.valueOf(this.fogColorRed), Float.valueOf(this.fogColorGreen), Float.valueOf(this.fogColorBlue) });
			Reflector.postForgeBusEvent(object);
			this.fogColorRed = Reflector.getFieldValueFloat(object, Reflector.EntityViewRenderEvent_FogColors_red, this.fogColorRed);
			this.fogColorGreen = Reflector.getFieldValueFloat(object, Reflector.EntityViewRenderEvent_FogColors_green, this.fogColorGreen);
			this.fogColorBlue = Reflector.getFieldValueFloat(object, Reflector.EntityViewRenderEvent_FogColors_blue, this.fogColorBlue);
		}

		GlStateManager.clearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
	}

	/**
	 * Sets up the fog to be rendered. If the arg passed in is -1 the fog starts at
	 * 0 and goes to 80% of far plane distance and is used for sky rendering.
	 */
	private void setupFog(int p_78468_1_, float partialTicks) {
		Entity entity = this.mc.getRenderViewEntity();
		boolean flag = false;
		this.fogStandard = false;

		if (entity instanceof EntityPlayer) {
			flag = ((EntityPlayer) entity).capabilities.isCreativeMode;
		}

		GL11.glFog(GL11.GL_FOG_COLOR, (FloatBuffer) this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
		GL11.glNormal3f(0.0F, -1.0F, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, entity, partialTicks);
		Object object = Reflector.newInstance(Reflector.EntityViewRenderEvent_FogDensity_Constructor, new Object[] { this, entity, block, Float.valueOf(partialTicks), Float.valueOf(0.1F) });

		if (Reflector.postForgeBusEvent(object)) {
			float f1 = Reflector.getFieldValueFloat(object, Reflector.EntityViewRenderEvent_FogDensity_density, 0.0F);
			GL11.glFogf(GL11.GL_FOG_DENSITY, f1);
		} else if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(Potion.blindness)) {
			float f2 = 5.0F;
			int i = ((EntityLivingBase) entity).getActivePotionEffect(Potion.blindness).getDuration();

			if (i < 20) {
				f2 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float) i / 20.0F);
			}

			GlStateManager.setFog(9729);

			if (p_78468_1_ == -1) {
				GlStateManager.setFogStart(0.0F);
				GlStateManager.setFogEnd(f2 * 0.8F);
			} else {
				GlStateManager.setFogStart(f2 * 0.25F);
				GlStateManager.setFogEnd(f2);
			}

			if (GLContext.getCapabilities().GL_NV_fog_distance && Config.isFogFancy()) {
				GL11.glFogi(34138, 34139);
			}
		} else if (this.cloudFog) {
			GlStateManager.setFog(2048);
			GlStateManager.setFogDensity(0.1F);
		} else if (block.getMaterial() == Material.water) {
			GlStateManager.setFog(2048);

			if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(Potion.waterBreathing)) {
				GlStateManager.setFogDensity(0.01F);
			} else {
				GlStateManager.setFogDensity(0.1F - (float) EnchantmentHelper.getRespiration(entity) * 0.03F);
			}

			if (Config.isClearWater()) {
				GL11.glFogf(GL11.GL_FOG_DENSITY, 0.02F);
			}
		} else if (block.getMaterial() == Material.lava) {
			GlStateManager.setFog(2048);
			GlStateManager.setFogDensity(2.0F);
		} else {
			float f = this.farPlaneDistance;
			this.fogStandard = true;
			GlStateManager.setFog(9729);

			if (p_78468_1_ == -1) {
				GlStateManager.setFogStart(0.0F);
				GlStateManager.setFogEnd(f);
			} else {
				GlStateManager.setFogStart(f * Config.getFogStart());
				GlStateManager.setFogEnd(f);
			}

			if (GLContext.getCapabilities().GL_NV_fog_distance) {
				if (Config.isFogFancy()) {
					GL11.glFogi(34138, 34139);
				}

				if (Config.isFogFast()) {
					GL11.glFogi(34138, 34140);
				}
			}

			if (this.mc.theWorld.provider.doesXZShowFog((int) entity.posX, (int) entity.posZ)) {
				GlStateManager.setFogStart(f * 0.05F);
				GlStateManager.setFogEnd(f);
			}

			if (Reflector.ForgeHooksClient_onFogRender.exists()) {
				Reflector.callVoid(Reflector.ForgeHooksClient_onFogRender, new Object[] { this, entity, block, Float.valueOf(partialTicks), Integer.valueOf(p_78468_1_), Float.valueOf(f) });
			}
		}

		GlStateManager.enableColorMaterial();
		GlStateManager.enableFog();
		GlStateManager.colorMaterial(1028, 4608);
	}

	/**
	 * Update and return fogColorBuffer with the RGBA values passed as arguments
	 */
	private FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha) {
		this.fogColorBuffer.clear();
		this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
		this.fogColorBuffer.flip();
		return this.fogColorBuffer;
	}

	public MapItemRenderer getMapItemRenderer() {
		return this.theMapItemRenderer;
	}

	private void waitForServerThread() {
		this.serverWaitTimeCurrent = 0;

		if (Config.isSmoothWorld() && Config.isSingleProcessor()) {
			if (this.mc.isIntegratedServerRunning()) {
				IntegratedServer integratedserver = this.mc.getIntegratedServer();

				if (integratedserver != null) {
					boolean flag = this.mc.isGamePaused();

					if (!flag && !(this.mc.currentScreen instanceof GuiDownloadTerrain)) {
						if (this.serverWaitTime > 0) {
							Lagometer.timerServer.start();
							Config.sleep((long) this.serverWaitTime);
							Lagometer.timerServer.end();
							this.serverWaitTimeCurrent = this.serverWaitTime;
						}

						long i = System.nanoTime() / 1000000L;

						if (this.lastServerTime != 0L && this.lastServerTicks != 0) {
							long j = i - this.lastServerTime;

							if (j < 0L) {
								this.lastServerTime = i;
								j = 0L;
							}

							if (j >= 50L) {
								this.lastServerTime = i;
								int k = integratedserver.getTickCounter();
								int l = k - this.lastServerTicks;

								if (l < 0) {
									this.lastServerTicks = k;
									l = 0;
								}

								if (l < 1 && this.serverWaitTime < 100) {
									this.serverWaitTime += 2;
								}

								if (l > 1 && this.serverWaitTime > 0) {
									--this.serverWaitTime;
								}

								this.lastServerTicks = k;
							}
						} else {
							this.lastServerTime = i;
							this.lastServerTicks = integratedserver.getTickCounter();
							this.avgServerTickDiff = 1.0F;
							this.avgServerTimeDiff = 50.0F;
						}
					} else {
						if (this.mc.currentScreen instanceof GuiDownloadTerrain) {
							Config.sleep(20L);
						}

						this.lastServerTime = 0L;
						this.lastServerTicks = 0;
					}
				}
			}
		} else {
			this.lastServerTime = 0L;
			this.lastServerTicks = 0;
		}
	}

	private void frameInit() {
		if (!this.initialized) {
			TextureUtils.registerResourceListener();
			RenderPlayerOF.register();

			if (Config.getBitsOs() == 64 && Config.getBitsJre() == 32) {
				Config.setNotify64BitJava(true);
			}

			this.initialized = true;
		}

		Config.isActing();
		Config.checkDisplayMode();
		World world = this.mc.theWorld;

		if (world != null) {
			if (Config.getNewRelease() != null) {
				String s = "HD_U".replace("HD_U", "HD Ultra").replace("L", "Light");
				String s1 = s + " " + Config.getNewRelease();
				ChatComponentText chatcomponenttext = new ChatComponentText("A new \u00a7eOptiFine\u00a7f version is available: \u00a7e" + s1 + "\u00a7f");
				this.mc.ingameGUI.getChatGUI().printChatMessage(chatcomponenttext);
				Config.setNewRelease((String) null);
			}

			if (Config.isNotify64BitJava()) {
				Config.setNotify64BitJava(false);
				ChatComponentText chatcomponenttext1 = new ChatComponentText("You can install \u00a7e64-bit Java\u00a7f to increase performance");
				this.mc.ingameGUI.getChatGUI().printChatMessage(chatcomponenttext1);
			}
		}

		if (this.mc.currentScreen instanceof GuiMainMenu) {
			this.updateMainMenu((GuiMainMenu) this.mc.currentScreen);
		}

		if (this.updatedWorld != world) {
			RandomMobs.worldChanged(this.updatedWorld, world);
			Config.updateThreadPriorities();
			this.lastServerTime = 0L;
			this.lastServerTicks = 0;
			this.updatedWorld = world;
		}
	}

	private void frameFinish() {
		if (this.mc.theWorld != null) {
			long i = System.currentTimeMillis();

			if (i > this.lastErrorCheckTimeMs + 10000L) {
				this.lastErrorCheckTimeMs = i;
				int j = GL11.glGetError();

				if (j != 0) {
					String s = GLU.gluErrorString(j);
					ChatComponentText chatcomponenttext = new ChatComponentText("\u00a7eOpenGL Error\u00a7f: " + j + " (" + s + ")");
					this.mc.ingameGUI.getChatGUI().printChatMessage(chatcomponenttext);
				}
			}
		}
	}

	private void updateMainMenu(GuiMainMenu p_updateMainMenu_1_) {
		try {
			String s = null;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			int i = calendar.get(5);
			int j = calendar.get(2) + 1;

			if (i == 8 && j == 4) {
				s = "Happy birthday, OptiFine!";
			}

			if (i == 14 && j == 8) {
				s = "Happy birthday, sp614x!";
			}

			if (s == null) {
				return;
			}

			Field[] afield = GuiMainMenu.class.getDeclaredFields();

			for (int k = 0; k < afield.length; ++k) {
				if (afield[k].getType() == String.class) {
					afield[k].setAccessible(true);
					afield[k].set(p_updateMainMenu_1_, s);
					break;
				}
			}
		} catch (Throwable var8) {
			;
		}
	}
}
