package net.minecraft.client.renderer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import optfine.ChunkUtils;
import optfine.CloudRenderer;
import optfine.Config;
import optfine.CustomColorizer;
import optfine.CustomSky;
import optfine.Lagometer;
import optfine.RandomMobs;
import optfine.Reflector;

public class RenderGlobal implements IWorldAccess, IResourceManagerReloadListener {
	private static final Logger logger = LogManager.getLogger();
	private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
	private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
	private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
	private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation locationForcefieldPng = new ResourceLocation("textures/misc/forcefield.png");

	/** A reference to the Minecraft object. */
	public final Minecraft mc;

	/** The RenderEngine instance used by RenderGlobal */
	private final TextureManager renderEngine;
	private final RenderManager renderManager;
	private WorldClient theWorld;
	private Set chunksToUpdate = Sets.newLinkedHashSet();

	/** List of OpenGL lists for the current render pass */
	private List renderInfos = Lists.newArrayListWithCapacity(69696);
	private final Set field_181024_n = Sets.newHashSet();
	private ViewFrustum viewFrustum;

	/** The star GL Call list */
	private int starGLCallList = -1;

	/** OpenGL sky list */
	private int glSkyList = -1;

	/** OpenGL sky list 2 */
	private int glSkyList2 = -1;
	private VertexFormat vertexBufferFormat;
	private VertexBuffer starVBO;
	private VertexBuffer skyVBO;
	private VertexBuffer sky2VBO;

	/**
	 * counts the cloud render updates. Used with mod to stagger some updates
	 */
	private int cloudTickCounter;

	/**
	 * Stores blocks currently being broken. Key is entity ID of the thing doing the
	 * breaking. Value is a DestroyBlockProgress
	 */
	public final Map damagedBlocks = Maps.newHashMap();

	/** Currently playing sounds. Type: HashMap<ChunkCoordinates, ISound> */
	private final Map mapSoundPositions = Maps.newHashMap();
	private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
	private Framebuffer entityOutlineFramebuffer;

	/** Stores the shader group for the entity_outline shader */
	private ShaderGroup entityOutlineShader;
	private double frustumUpdatePosX = Double.MIN_VALUE;
	private double frustumUpdatePosY = Double.MIN_VALUE;
	private double frustumUpdatePosZ = Double.MIN_VALUE;
	private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
	private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
	private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
	private double lastViewEntityX = Double.MIN_VALUE;
	private double lastViewEntityY = Double.MIN_VALUE;
	private double lastViewEntityZ = Double.MIN_VALUE;
	private double lastViewEntityPitch = Double.MIN_VALUE;
	private double lastViewEntityYaw = Double.MIN_VALUE;
	private final ChunkRenderDispatcher renderDispatcher = new ChunkRenderDispatcher();
	private ChunkRenderContainer renderContainer;
	private int renderDistanceChunks = -1;

	/** Render entities startup counter (init value=2) */
	private int renderEntitiesStartupCounter = 2;

	/** Count entities total */
	private int countEntitiesTotal;

	/** Count entities rendered */
	private int countEntitiesRendered;

	/** Count entities hidden */
	private int countEntitiesHidden;
	private boolean debugFixTerrainFrustum = false;
	private ClippingHelper debugFixedClippingHelper;
	private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
	private final Vector3d debugTerrainFrustumPosition = new Vector3d();
	private boolean vboEnabled = false;
	IRenderChunkFactory renderChunkFactory;
	private double prevRenderSortX;
	private double prevRenderSortY;
	private double prevRenderSortZ;
	public boolean displayListEntitiesDirty = true;
	
	private CloudRenderer cloudRenderer;
	public Entity renderedEntity;
	public Set chunksToResortTransparency = new LinkedHashSet();
	public Set chunksToUpdateForced = new LinkedHashSet();
	private Deque visibilityDeque = new ArrayDeque();
	private List renderInfosEntities = new ArrayList(1024);
	private List renderInfosTileEntities = new ArrayList(1024);
	private int renderDistance = 0;
	private int renderDistanceSq = 0;
	private static final Set SET_ALL_FACINGS = Collections.unmodifiableSet(new HashSet(Arrays.asList(EnumFacing.VALUES)));
	private int countTileEntitiesRendered;

	public RenderGlobal(Minecraft mcIn) {
		this.cloudRenderer = new CloudRenderer(mcIn);
		this.mc = mcIn;
		this.renderManager = mcIn.getRenderManager();
		this.renderEngine = mcIn.getTextureManager();
		this.renderEngine.bindTexture(locationForcefieldPng);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GlStateManager.bindTexture(0);
		this.updateDestroyBlockIcons();
		this.vboEnabled = OpenGlHelper.useVbo();

		if (this.vboEnabled) {
			this.renderContainer = new VboRenderList();
			this.renderChunkFactory = new VboChunkFactory();
		} else {
			this.renderContainer = new RenderList();
			this.renderChunkFactory = new ListChunkFactory();
		}

		this.vertexBufferFormat = new VertexFormat();
		this.vertexBufferFormat.func_181721_a(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
		this.generateStars();
		this.generateSky();
		this.generateSky2();
	}

	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.updateDestroyBlockIcons();
	}

	private void updateDestroyBlockIcons() {
		TextureMap texturemap = this.mc.getTextureMapBlocks();

		for (int i = 0; i < this.destroyBlockIcons.length; ++i) {
			this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
		}
	}

	/**
	 * Creates the entity outline shader to be stored in
	 * RenderGlobal.entityOutlineShader
	 */
	public void makeEntityOutlineShader() {
		if (OpenGlHelper.shadersSupported) {
			if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
				ShaderLinkHelper.setNewStaticShaderLinkHelper();
			}

			ResourceLocation resourcelocation = new ResourceLocation("shaders/post/entity_outline.json");

			try {
				this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocation);
				this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
				this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
			} catch (IOException ioexception) {
				logger.warn((String) ("Failed to load shader: " + resourcelocation), (Throwable) ioexception);
				this.entityOutlineShader = null;
				this.entityOutlineFramebuffer = null;
			} catch (JsonSyntaxException jsonsyntaxexception) {
				logger.warn((String) ("Failed to load shader: " + resourcelocation), (Throwable) jsonsyntaxexception);
				this.entityOutlineShader = null;
				this.entityOutlineFramebuffer = null;
			}
		} else {
			this.entityOutlineShader = null;
			this.entityOutlineFramebuffer = null;
		}
	}

	public void renderEntityOutlineFramebuffer() {
		if (this.isRenderEntityOutlines()) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
			this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
			GlStateManager.disableBlend();
		}
	}

	protected boolean isRenderEntityOutlines() {
		return this.entityOutlineFramebuffer != null && this.entityOutlineShader != null && this.mc.thePlayer != null && this.mc.thePlayer.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown();
	}

	private void generateSky2() {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		if (this.sky2VBO != null) {
			this.sky2VBO.deleteGlBuffers();
		}

		if (this.glSkyList2 >= 0) {
			GLAllocation.deleteDisplayLists(this.glSkyList2);
			this.glSkyList2 = -1;
		}

		if (this.vboEnabled) {
			this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
			this.renderSky(worldrenderer, -16.0F, true);
			worldrenderer.finishDrawing();
			worldrenderer.reset();
			this.sky2VBO.func_181722_a(worldrenderer.getByteBuffer());
		} else {
			this.glSkyList2 = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
			this.renderSky(worldrenderer, -16.0F, true);
			tessellator.draw();
			GL11.glEndList();
		}
	}

	private void generateSky() {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		if (this.skyVBO != null) {
			this.skyVBO.deleteGlBuffers();
		}

		if (this.glSkyList >= 0) {
			GLAllocation.deleteDisplayLists(this.glSkyList);
			this.glSkyList = -1;
		}

		if (this.vboEnabled) {
			this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
			this.renderSky(worldrenderer, 16.0F, false);
			worldrenderer.finishDrawing();
			worldrenderer.reset();
			this.skyVBO.func_181722_a(worldrenderer.getByteBuffer());
		} else {
			this.glSkyList = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
			this.renderSky(worldrenderer, 16.0F, false);
			tessellator.draw();
			GL11.glEndList();
		}
	}

	private void renderSky(WorldRenderer worldRendererIn, float p_174968_2_, boolean p_174968_3_) {
		boolean flag = true;
		boolean flag1 = true;
		worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

		for (int i = -384; i <= 384; i += 64) {
			for (int j = -384; j <= 384; j += 64) {
				float f = (float) i;
				float f1 = (float) (i + 64);

				if (p_174968_3_) {
					f1 = (float) i;
					f = (float) (i + 64);
				}

				worldRendererIn.pos((double) f, (double) p_174968_2_, (double) j).endVertex();
				worldRendererIn.pos((double) f1, (double) p_174968_2_, (double) j).endVertex();
				worldRendererIn.pos((double) f1, (double) p_174968_2_, (double) (j + 64)).endVertex();
				worldRendererIn.pos((double) f, (double) p_174968_2_, (double) (j + 64)).endVertex();
			}
		}
	}

	private void generateStars() {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		if (this.starVBO != null) {
			this.starVBO.deleteGlBuffers();
		}

		if (this.starGLCallList >= 0) {
			GLAllocation.deleteDisplayLists(this.starGLCallList);
			this.starGLCallList = -1;
		}

		if (this.vboEnabled) {
			this.starVBO = new VertexBuffer(this.vertexBufferFormat);
			this.renderStars(worldrenderer);
			worldrenderer.finishDrawing();
			worldrenderer.reset();
			this.starVBO.func_181722_a(worldrenderer.getByteBuffer());
		} else {
			this.starGLCallList = GLAllocation.generateDisplayLists(1);
			GlStateManager.pushMatrix();
			GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
			this.renderStars(worldrenderer);
			tessellator.draw();
			GL11.glEndList();
			GlStateManager.popMatrix();
		}
	}

	private void renderStars(WorldRenderer worldRendererIn) {
		Random random = new Random(10842L);
		worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

		for (int i = 0; i < 1500; ++i) {
			double d0 = (double) (random.nextFloat() * 2.0F - 1.0F);
			double d1 = (double) (random.nextFloat() * 2.0F - 1.0F);
			double d2 = (double) (random.nextFloat() * 2.0F - 1.0F);
			double d3 = (double) (0.15F + random.nextFloat() * 0.1F);
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d4 < 1.0D && d4 > 0.01D) {
				d4 = 1.0D / Math.sqrt(d4);
				d0 = d0 * d4;
				d1 = d1 * d4;
				d2 = d2 * d4;
				double d5 = d0 * 100.0D;
				double d6 = d1 * 100.0D;
				double d7 = d2 * 100.0D;
				double d8 = Math.atan2(d0, d2);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double d15 = Math.sin(d14);
				double d16 = Math.cos(d14);

				for (int j = 0; j < 4; ++j) {
					double d17 = 0.0D;
					double d18 = (double) ((j & 2) - 1) * d3;
					double d19 = (double) ((j + 1 & 2) - 1) * d3;
					double d20 = 0.0D;
					double d21 = d18 * d16 - d19 * d15;
					double d22 = d19 * d16 + d18 * d15;
					double d23 = d21 * d12 + 0.0D * d13;
					double d24 = 0.0D * d12 - d21 * d13;
					double d25 = d24 * d9 - d22 * d10;
					double d26 = d22 * d9 + d24 * d10;
					worldRendererIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
				}
			}
		}
	}

	/**
	 * set null to clear
	 */
	public void setWorldAndLoadRenderers(WorldClient worldClientIn) {
		if (this.theWorld != null) {
			this.theWorld.removeWorldAccess(this);
		}

		this.frustumUpdatePosX = Double.MIN_VALUE;
		this.frustumUpdatePosY = Double.MIN_VALUE;
		this.frustumUpdatePosZ = Double.MIN_VALUE;
		this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
		this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
		this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
		this.renderManager.set(worldClientIn);
		this.theWorld = worldClientIn;

		if (worldClientIn != null) {
			worldClientIn.addWorldAccess(this);
			this.loadRenderers();
		}
	}

	/**
	 * Loads all the renderers and sets up the basic settings usage
	 */
	public void loadRenderers() {
		if (this.theWorld != null) {
			this.displayListEntitiesDirty = true;
			Blocks.leaves.setGraphicsLevel(Config.isTreesFancy());
			Blocks.leaves2.setGraphicsLevel(Config.isTreesFancy());
			BlockModelRenderer.updateAoLightValue();
			this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
			this.renderDistance = this.renderDistanceChunks * 16;
			this.renderDistanceSq = this.renderDistance * this.renderDistance;
			boolean flag = this.vboEnabled;
			this.vboEnabled = OpenGlHelper.useVbo();

			if (flag && !this.vboEnabled) {
				this.renderContainer = new RenderList();
				this.renderChunkFactory = new ListChunkFactory();
			} else if (!flag && this.vboEnabled) {
				this.renderContainer = new VboRenderList();
				this.renderChunkFactory = new VboChunkFactory();
			}

			if (flag != this.vboEnabled) {
				this.generateStars();
				this.generateSky();
				this.generateSky2();
			}

			if (this.viewFrustum != null) {
				this.viewFrustum.deleteGlResources();
			}

			this.stopChunkUpdates();
			Set var5 = this.field_181024_n;

			synchronized (this.field_181024_n) {
				this.field_181024_n.clear();
			}

			this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);

			if (this.theWorld != null) {
				Entity entity = this.mc.getRenderViewEntity();

				if (entity != null) {
					this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
				}
			}

			this.renderEntitiesStartupCounter = 2;
		}
	}

	protected void stopChunkUpdates() {
		this.chunksToUpdate.clear();
		this.renderDispatcher.stopChunkUpdates();
	}

	public void createBindEntityOutlineFbs(int p_72720_1_, int p_72720_2_) {
		if (OpenGlHelper.shadersSupported && this.entityOutlineShader != null) {
			this.entityOutlineShader.createBindFramebuffers(p_72720_1_, p_72720_2_);
		}
	}

	public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
		int i = 0;

		if (Reflector.MinecraftForgeClient_getRenderPass.exists()) {
			i = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass, new Object[0]);
		}

		if (this.renderEntitiesStartupCounter > 0) {
			if (i > 0) {
				return;
			}

			--this.renderEntitiesStartupCounter;
		} else {
			double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double) partialTicks;
			double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double) partialTicks;
			double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double) partialTicks;
			this.theWorld.theProfiler.startSection("prepare");
			TileEntityRendererDispatcher.instance.cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), partialTicks);
			this.renderManager.cacheActiveRenderInfo(this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);

			if (i == 0) {
				this.countEntitiesTotal = 0;
				this.countEntitiesRendered = 0;
				this.countEntitiesHidden = 0;
				this.countTileEntitiesRendered = 0;
			}

			Entity entity = this.mc.getRenderViewEntity();
			double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
			double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
			double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
			TileEntityRendererDispatcher.staticPlayerX = d3;
			TileEntityRendererDispatcher.staticPlayerY = d4;
			TileEntityRendererDispatcher.staticPlayerZ = d5;
			this.renderManager.setRenderPosition(d3, d4, d5);
			this.mc.entityRenderer.enableLightmap();
			this.theWorld.theProfiler.endStartSection("global");
			List list = this.theWorld.getLoadedEntityList();

			if (i == 0) {
				this.countEntitiesTotal = list.size();
			}

			if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
				GlStateManager.disableFog();
			}

			boolean flag = Reflector.ForgeEntity_shouldRenderInPass.exists();
			boolean flag1 = Reflector.ForgeTileEntity_shouldRenderInPass.exists();

			for (int j = 0; j < this.theWorld.weatherEffects.size(); ++j) {
				Entity entity1 = (Entity) this.theWorld.weatherEffects.get(j);

				if (!flag || Reflector.callBoolean(entity1, Reflector.ForgeEntity_shouldRenderInPass, new Object[] { Integer.valueOf(i) })) {
					++this.countEntitiesRendered;

					if (entity1.isInRangeToRender3d(d0, d1, d2)) {
						this.renderManager.renderEntitySimple(entity1, partialTicks);
					}
				}
			}

			if (this.isRenderEntityOutlines()) {
				GlStateManager.depthFunc(519);
				GlStateManager.disableFog();
				this.entityOutlineFramebuffer.framebufferClear();
				this.entityOutlineFramebuffer.bindFramebuffer(false);
				this.theWorld.theProfiler.endStartSection("entityOutlines");
				RenderHelper.disableStandardItemLighting();
				this.renderManager.setRenderOutlines(true);

				for (int k = 0; k < list.size(); ++k) {
					Entity entity3 = (Entity) list.get(k);

					if (!flag || Reflector.callBoolean(entity3, Reflector.ForgeEntity_shouldRenderInPass, new Object[] { Integer.valueOf(i) })) {
						boolean flag2 = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) this.mc.getRenderViewEntity()).isPlayerSleeping();
						boolean flag3 = entity3.isInRangeToRender3d(d0, d1, d2) && (entity3.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entity3.getEntityBoundingBox()) || entity3.riddenByEntity == this.mc.thePlayer) && entity3 instanceof EntityPlayer;

						if ((entity3 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || flag2) && flag3) {
							this.renderManager.renderEntitySimple(entity3, partialTicks);
						}
					}
				}

				this.renderManager.setRenderOutlines(false);
				RenderHelper.enableStandardItemLighting();
				GlStateManager.depthMask(false);
				this.entityOutlineShader.loadShaderGroup(partialTicks);
				GlStateManager.enableLighting();
				GlStateManager.depthMask(true);
				this.mc.getFramebuffer().bindFramebuffer(false);
				GlStateManager.enableFog();
				GlStateManager.enableBlend();
				GlStateManager.enableColorMaterial();
				GlStateManager.depthFunc(515);
				GlStateManager.enableDepth();
				GlStateManager.enableAlpha();
			}

			this.theWorld.theProfiler.endStartSection("entities");
			Iterator iterator1 = this.renderInfosEntities.iterator();
			boolean flag4 = this.mc.gameSettings.fancyGraphics;
			this.mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();
			label907:

			while (iterator1.hasNext()) {
				RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation = (RenderGlobal.ContainerLocalRenderInformation) iterator1.next();
				Chunk chunk = this.theWorld.getChunkFromBlockCoords(renderglobal$containerlocalrenderinformation.renderChunk.getPosition());
				ClassInheritanceMultiMap classinheritancemultimap = chunk.getEntityLists()[renderglobal$containerlocalrenderinformation.renderChunk.getPosition().getY() / 16];

				if (!classinheritancemultimap.isEmpty()) {
					Iterator iterator = classinheritancemultimap.iterator();

					while (true) {
						Entity entity2;
						boolean flag5;

						while (true) {
							if (!iterator.hasNext()) {
								continue label907;
							}

							entity2 = (Entity) iterator.next();

							if (!flag || Reflector.callBoolean(entity2, Reflector.ForgeEntity_shouldRenderInPass, new Object[] { Integer.valueOf(i) })) {
								flag5 = this.renderManager.shouldRender(entity2, camera, d0, d1, d2) || entity2.riddenByEntity == this.mc.thePlayer;

								if (!flag5) {
									break;
								}

								boolean flag6 = this.mc.getRenderViewEntity() instanceof EntityLivingBase ? ((EntityLivingBase) this.mc.getRenderViewEntity()).isPlayerSleeping() : false;

								if ((entity2 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || flag6) && (entity2.posY < 0.0D || entity2.posY >= 256.0D || this.theWorld.isBlockLoaded(new BlockPos(entity2)))) {
									++this.countEntitiesRendered;

									if (entity2.getClass() == EntityItemFrame.class) {
										entity2.renderDistanceWeight = 0.06D;
									}

									this.renderedEntity = entity2;
									this.renderManager.renderEntitySimple(entity2, partialTicks);
									this.renderedEntity = null;
									break;
								}
							}
						}

						if (!flag5 && entity2 instanceof EntityWitherSkull) {
							this.mc.getRenderManager().renderWitherSkull(entity2, partialTicks);
						}
					}
				}
			}

			this.mc.gameSettings.fancyGraphics = flag4;
			FontRenderer fontrenderer = TileEntityRendererDispatcher.instance.getFontRenderer();
			this.theWorld.theProfiler.endStartSection("blockentities");
			RenderHelper.enableStandardItemLighting();
			label1318:

			for (Object renderglobal$containerlocalrenderinformation10 : this.renderInfosTileEntities) {
				RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 = (RenderGlobal.ContainerLocalRenderInformation) renderglobal$containerlocalrenderinformation10;
				List list1 = renderglobal$containerlocalrenderinformation1.renderChunk.getCompiledChunk().getTileEntities();

				if (!list1.isEmpty()) {
					Iterator iterator2 = list1.iterator();

					while (true) {
						TileEntity tileentity;

						while (true) {
							if (!iterator2.hasNext()) {
								continue label1318;
							}

							tileentity = (TileEntity) iterator2.next();

							if (!flag1) {
								break;
							}

							if (Reflector.callBoolean(tileentity, Reflector.ForgeTileEntity_shouldRenderInPass, new Object[] { Integer.valueOf(i) })) {
								AxisAlignedBB axisalignedbb = (AxisAlignedBB) Reflector.call(tileentity, Reflector.ForgeTileEntity_getRenderBoundingBox, new Object[0]);

								if (axisalignedbb == null || camera.isBoundingBoxInFrustum(axisalignedbb)) {
									break;
								}
							}
						}

						Class oclass = tileentity.getClass();

						if (oclass == TileEntitySign.class && !Config.zoomMode) {
							EntityPlayer entityplayer = this.mc.thePlayer;
							double d6 = tileentity.getDistanceSq(entityplayer.posX, entityplayer.posY, entityplayer.posZ);

							if (d6 > 256.0D) {
								fontrenderer.enabled = false;
							}
						}

						TileEntityRendererDispatcher.instance.renderTileEntity(tileentity, partialTicks, -1);
						++this.countTileEntitiesRendered;
						fontrenderer.enabled = true;
					}
				}
			}

			Set var32 = this.field_181024_n;

			synchronized (this.field_181024_n) {
				for (Object tileentity1 : this.field_181024_n) {
					if (flag1) {
						if (!Reflector.callBoolean(tileentity1, Reflector.ForgeTileEntity_shouldRenderInPass, new Object[] { Integer.valueOf(i) })) {
							continue;
						}
						AxisAlignedBB axisalignedbb1 = (AxisAlignedBB) Reflector.call(tileentity1, Reflector.ForgeTileEntity_getRenderBoundingBox, new Object[0]);

						if (axisalignedbb1 != null && !camera.isBoundingBoxInFrustum(axisalignedbb1)) {
							continue;
						}
					}

					Class oclass1 = tileentity1.getClass();

					if (oclass1 == TileEntitySign.class && !Config.zoomMode) {
						EntityPlayer entityplayer1 = this.mc.thePlayer;
						double d7 = ((TileEntity) tileentity1).getDistanceSq(entityplayer1.posX, entityplayer1.posY, entityplayer1.posZ);

						if (d7 > 256.0D) {
							fontrenderer.enabled = false;
						}
					}

					TileEntityRendererDispatcher.instance.renderTileEntity((TileEntity) tileentity1, partialTicks, -1);
					fontrenderer.enabled = true;
				}
			}

			this.preRenderDamagedBlocks();

			for (Object destroyblockprogress : this.damagedBlocks.values()) {
				BlockPos blockpos = ((DestroyBlockProgress) destroyblockprogress).getPosition();
				TileEntity tileentity2 = this.theWorld.getTileEntity(blockpos);

				if (tileentity2 instanceof TileEntityChest) {
					TileEntityChest tileentitychest = (TileEntityChest) tileentity2;

					if (tileentitychest.adjacentChestXNeg != null) {
						blockpos = blockpos.offset(EnumFacing.WEST);
						tileentity2 = this.theWorld.getTileEntity(blockpos);
					} else if (tileentitychest.adjacentChestZNeg != null) {
						blockpos = blockpos.offset(EnumFacing.NORTH);
						tileentity2 = this.theWorld.getTileEntity(blockpos);
					}
				}

				Block block = this.theWorld.getBlockState(blockpos).getBlock();
				boolean flag7;

				if (flag1) {
					flag7 = false;

					if (tileentity2 != null && Reflector.callBoolean(tileentity2, Reflector.ForgeTileEntity_shouldRenderInPass, new Object[] { Integer.valueOf(i) }) && Reflector.callBoolean(tileentity2, Reflector.ForgeTileEntity_canRenderBreaking, new Object[0])) {
						AxisAlignedBB axisalignedbb2 = (AxisAlignedBB) Reflector.call(tileentity2, Reflector.ForgeTileEntity_getRenderBoundingBox, new Object[0]);

						if (axisalignedbb2 != null) {
							flag7 = camera.isBoundingBoxInFrustum(axisalignedbb2);
						}
					}
				} else {
					flag7 = tileentity2 != null && (block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull);
				}

				if (flag7) {
					TileEntityRendererDispatcher.instance.renderTileEntity(tileentity2, partialTicks, ((DestroyBlockProgress) destroyblockprogress).getPartialBlockDamage());
				}
			}

			this.postRenderDamagedBlocks();
			this.mc.entityRenderer.disableLightmap();
			this.mc.mcProfiler.endSection();
		}
	}

	/**
	 * Gets the render info for use on the Debug screen
	 */
	public String getDebugInfoRenders() {
		int i = this.viewFrustum.renderChunks.length;
		int j = 0;

		for (Object renderglobal$containerlocalrenderinformation0 : this.renderInfos) {
			RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation = (RenderGlobal.ContainerLocalRenderInformation) renderglobal$containerlocalrenderinformation0;
			CompiledChunk compiledchunk = renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk;

			if (compiledchunk != CompiledChunk.DUMMY && !compiledchunk.isEmpty()) {
				++j;
			}
		}

		return String.format("C: %d/%d %sD: %d, %s", new Object[] { Integer.valueOf(j), Integer.valueOf(i), this.mc.renderChunksMany ? "(s) " : "", Integer.valueOf(this.renderDistanceChunks), this.renderDispatcher.getDebugInfo() });
	}

	/**
	 * Gets the entities info for use on the Debug screen
	 */
	public String getDebugInfoEntities() {
		return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ", B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered) + ", " + Config.getVersion();
	}

	public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
		if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks) {
			this.loadRenderers();
		}

		this.theWorld.theProfiler.startSection("camera");
		double d0 = viewEntity.posX - this.frustumUpdatePosX;
		double d1 = viewEntity.posY - this.frustumUpdatePosY;
		double d2 = viewEntity.posZ - this.frustumUpdatePosZ;

		if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0D) {
			this.frustumUpdatePosX = viewEntity.posX;
			this.frustumUpdatePosY = viewEntity.posY;
			this.frustumUpdatePosZ = viewEntity.posZ;
			this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
			this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
			this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
			this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
		}

		this.theWorld.theProfiler.endStartSection("renderlistcamera");
		double d3 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
		double d4 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
		double d5 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
		this.renderContainer.initialize(d3, d4, d5);
		this.theWorld.theProfiler.endStartSection("cull");

		if (this.debugFixedClippingHelper != null) {
			Frustum frustum = new Frustum(this.debugFixedClippingHelper);
			frustum.setPosition(this.debugTerrainFrustumPosition.field_181059_a, this.debugTerrainFrustumPosition.field_181060_b, this.debugTerrainFrustumPosition.field_181061_c);
			camera = frustum;
		}

		this.mc.mcProfiler.endStartSection("culling");
		BlockPos blockpos1 = new BlockPos(d3, d4 + (double) viewEntity.getEyeHeight(), d5);
		RenderChunk renderchunk = this.viewFrustum.getRenderChunk(blockpos1);
		BlockPos blockpos = new BlockPos(MathHelper.floor_double(d3 / 16.0D) * 16, MathHelper.floor_double(d4 / 16.0D) * 16, MathHelper.floor_double(d5 / 16.0D) * 16);
		this.displayListEntitiesDirty = this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || viewEntity.posX != this.lastViewEntityX || viewEntity.posY != this.lastViewEntityY || viewEntity.posZ != this.lastViewEntityZ || (double) viewEntity.rotationPitch != this.lastViewEntityPitch || (double) viewEntity.rotationYaw != this.lastViewEntityYaw;
		this.lastViewEntityX = viewEntity.posX;
		this.lastViewEntityY = viewEntity.posY;
		this.lastViewEntityZ = viewEntity.posZ;
		this.lastViewEntityPitch = (double) viewEntity.rotationPitch;
		this.lastViewEntityYaw = (double) viewEntity.rotationYaw;
		boolean flag = this.debugFixedClippingHelper != null;
		Lagometer.timerVisibility.start();

		if (!flag && this.displayListEntitiesDirty) {
			this.displayListEntitiesDirty = false;
			this.renderInfos.clear();
			this.renderInfosEntities.clear();
			this.renderInfosTileEntities.clear();
			this.visibilityDeque.clear();
			Deque deque = this.visibilityDeque;
			boolean flag1 = this.mc.renderChunksMany;

			if (renderchunk != null) {
				boolean flag2 = false;
				RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation3 = new RenderGlobal.ContainerLocalRenderInformation(renderchunk, (EnumFacing) null, 0, (Object) null);
				Set set1 = SET_ALL_FACINGS;

				if (set1.size() == 1) {
					Vector3f vector3f = this.getViewVector(viewEntity, partialTicks);
					EnumFacing enumfacing = EnumFacing.getFacingFromVector(vector3f.x, vector3f.y, vector3f.z).getOpposite();
					set1.remove(enumfacing);
				}

				if (set1.isEmpty()) {
					flag2 = true;
				}

				if (flag2 && !playerSpectator) {
					this.renderInfos.add(renderglobal$containerlocalrenderinformation3);
				} else {
					if (playerSpectator && this.theWorld.getBlockState(blockpos1).getBlock().isOpaqueCube()) {
						flag1 = false;
					}

					renderchunk.setFrameIndex(frameCount);
					deque.add(renderglobal$containerlocalrenderinformation3);
				}
			} else {
				int i = blockpos1.getY() > 0 ? 248 : 8;

				for (int j = -this.renderDistanceChunks; j <= this.renderDistanceChunks; ++j) {
					for (int k = -this.renderDistanceChunks; k <= this.renderDistanceChunks; ++k) {
						RenderChunk renderchunk2 = this.viewFrustum.getRenderChunk(new BlockPos((j << 4) + 8, i, (k << 4) + 8));

						if (renderchunk2 != null && ((ICamera) camera).isBoundingBoxInFrustum(renderchunk2.boundingBox)) {
							renderchunk2.setFrameIndex(frameCount);
							deque.add(new RenderGlobal.ContainerLocalRenderInformation(renderchunk2, (EnumFacing) null, 0, (Object) null));
						}
					}
				}
			}

			EnumFacing[] aenumfacing = EnumFacing.VALUES;
			int l = aenumfacing.length;

			while (!deque.isEmpty()) {
				RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation = (RenderGlobal.ContainerLocalRenderInformation) deque.poll();
				RenderChunk renderchunk1 = renderglobal$containerlocalrenderinformation.renderChunk;
				EnumFacing enumfacing2 = renderglobal$containerlocalrenderinformation.facing;
				BlockPos blockpos2 = renderchunk1.getPosition();

				if (!renderchunk1.compiledChunk.isEmpty() || renderchunk1.isNeedsUpdate()) {
					this.renderInfos.add(renderglobal$containerlocalrenderinformation);
				}

				if (ChunkUtils.hasEntities(this.theWorld.getChunkFromBlockCoords(blockpos2))) {
					this.renderInfosEntities.add(renderglobal$containerlocalrenderinformation);
				}

				if (renderchunk1.getCompiledChunk().getTileEntities().size() > 0) {
					this.renderInfosTileEntities.add(renderglobal$containerlocalrenderinformation);
				}

				for (int i1 = 0; i1 < l; ++i1) {
					EnumFacing enumfacing1 = aenumfacing[i1];

					if ((!flag1 || !renderglobal$containerlocalrenderinformation.setFacing.contains(enumfacing1.getOpposite())) && (!flag1 || enumfacing2 == null || renderchunk1.getCompiledChunk().isVisible(enumfacing2.getOpposite(), enumfacing1))) {
						RenderChunk renderchunk3 = this.func_181562_a(blockpos1, renderchunk1, enumfacing1);

						if (renderchunk3 != null && renderchunk3.setFrameIndex(frameCount) && ((ICamera) camera).isBoundingBoxInFrustum(renderchunk3.boundingBox)) {
							RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 = new RenderGlobal.ContainerLocalRenderInformation(renderchunk3, enumfacing1, renderglobal$containerlocalrenderinformation.counter + 1, (Object) null);
							renderglobal$containerlocalrenderinformation1.setFacing.addAll(renderglobal$containerlocalrenderinformation.setFacing);
							renderglobal$containerlocalrenderinformation1.setFacing.add(enumfacing1);
							deque.add(renderglobal$containerlocalrenderinformation1);
						}
					}
				}
			}
		}

		if (this.debugFixTerrainFrustum) {
			this.fixTerrainFrustum(d3, d4, d5);
			this.debugFixTerrainFrustum = false;
		}

		Lagometer.timerVisibility.end();
		this.renderDispatcher.clearChunkUpdates();
		Set set = this.chunksToUpdate;
		this.chunksToUpdate = Sets.newLinkedHashSet();
		Iterator iterator = this.renderInfos.iterator();
		Lagometer.timerChunkUpdate.start();

		while (iterator.hasNext()) {
			RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation2 = (RenderGlobal.ContainerLocalRenderInformation) iterator.next();
			RenderChunk renderchunk4 = renderglobal$containerlocalrenderinformation2.renderChunk;

			if (renderchunk4.isNeedsUpdate() || set.contains(renderchunk4)) {
				this.displayListEntitiesDirty = true;

				if (this.isPositionInRenderChunk(blockpos, renderglobal$containerlocalrenderinformation2.renderChunk)) {
					if (!Config.isActing()) {
						this.chunksToUpdateForced.add(renderchunk4);
					} else {
						this.mc.mcProfiler.startSection("build near");
						this.renderDispatcher.updateChunkNow(renderchunk4);
						renderchunk4.setNeedsUpdate(false);
						this.mc.mcProfiler.endSection();
					}
				} else {
					this.chunksToUpdate.add(renderchunk4);
				}
			}
		}

		Lagometer.timerChunkUpdate.end();
		this.chunksToUpdate.addAll(set);
		this.mc.mcProfiler.endSection();
	}

	private boolean isPositionInRenderChunk(BlockPos pos, RenderChunk renderChunkIn) {
		BlockPos blockpos = renderChunkIn.getPosition();
		return MathHelper.abs_int(pos.getX() - blockpos.getX()) > 16 ? false : (MathHelper.abs_int(pos.getY() - blockpos.getY()) > 16 ? false : MathHelper.abs_int(pos.getZ() - blockpos.getZ()) <= 16);
	}

	private Set getVisibleFacings(BlockPos pos) {
		VisGraph visgraph = new VisGraph();
		BlockPos blockpos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
		Chunk chunk = this.theWorld.getChunkFromBlockCoords(blockpos);

		for (BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos.add(15, 15, 15))) {
			if (chunk.getBlock(blockpos$mutableblockpos).isOpaqueCube()) {
				visgraph.func_178606_a(blockpos$mutableblockpos);
			}
		}
		return visgraph.func_178609_b(pos);
	}

	private RenderChunk func_181562_a(BlockPos p_181562_1_, RenderChunk p_181562_2_, EnumFacing p_181562_3_) {
		BlockPos blockpos = p_181562_2_.getPositionOffset16(p_181562_3_);

		if (blockpos.getY() >= 0 && blockpos.getY() < 256) {
			int i = MathHelper.abs_int(p_181562_1_.getX() - blockpos.getX());
			int j = MathHelper.abs_int(p_181562_1_.getZ() - blockpos.getZ());

			if (Config.isFogOff()) {
				if (i > this.renderDistance || j > this.renderDistance) {
					return null;
				}
			} else {
				int k = i * i + j * j;

				if (k > this.renderDistanceSq) {
					return null;
				}
			}

			return this.viewFrustum.getRenderChunk(blockpos);
		} else {
			return null;
		}
	}

	private void fixTerrainFrustum(double x, double y, double z) {
		this.debugFixedClippingHelper = new ClippingHelperImpl();
		((ClippingHelperImpl) this.debugFixedClippingHelper).init();
		Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
		matrix4f.transpose();
		Matrix4f matrix4f1 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
		matrix4f1.transpose();
		Matrix4f matrix4f2 = new Matrix4f();
		Matrix4f.mul(matrix4f1, matrix4f, matrix4f2);
		matrix4f2.invert();
		this.debugTerrainFrustumPosition.field_181059_a = x;
		this.debugTerrainFrustumPosition.field_181060_b = y;
		this.debugTerrainFrustumPosition.field_181061_c = z;
		this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
		this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
		this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
		this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
		this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
		this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
		this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

		for (int i = 0; i < 8; ++i) {
			Matrix4f.transform(matrix4f2, this.debugTerrainMatrix[i], this.debugTerrainMatrix[i]);
			this.debugTerrainMatrix[i].x /= this.debugTerrainMatrix[i].w;
			this.debugTerrainMatrix[i].y /= this.debugTerrainMatrix[i].w;
			this.debugTerrainMatrix[i].z /= this.debugTerrainMatrix[i].w;
			this.debugTerrainMatrix[i].w = 1.0F;
		}
	}

	protected Vector3f getViewVector(Entity entityIn, double partialTicks) {
		float f = (float) ((double) entityIn.prevRotationPitch + (double) (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
		float f1 = (float) ((double) entityIn.prevRotationYaw + (double) (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);

		if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
			f += 180.0F;
		}

		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		return new Vector3f(f3 * f4, f5, f2 * f4);
	}

	public int renderBlockLayer(EnumWorldBlockLayer blockLayerIn, double partialTicks, int pass, Entity entityIn) {
		RenderHelper.disableStandardItemLighting();

		if (blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT) {
			this.mc.mcProfiler.startSection("translucent_sort");
			double d0 = entityIn.posX - this.prevRenderSortX;
			double d1 = entityIn.posY - this.prevRenderSortY;
			double d2 = entityIn.posZ - this.prevRenderSortZ;

			if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D) {
				this.prevRenderSortX = entityIn.posX;
				this.prevRenderSortY = entityIn.posY;
				this.prevRenderSortZ = entityIn.posZ;
				int k = 0;
				Iterator iterator = this.renderInfos.iterator();
				this.chunksToResortTransparency.clear();

				while (iterator.hasNext()) {
					RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation = (RenderGlobal.ContainerLocalRenderInformation) iterator.next();

					if (renderglobal$containerlocalrenderinformation.renderChunk.compiledChunk.isLayerStarted(blockLayerIn) && k++ < 15) {
						this.chunksToResortTransparency.add(renderglobal$containerlocalrenderinformation.renderChunk);
					}
				}
			}

			this.mc.mcProfiler.endSection();
		}

		this.mc.mcProfiler.startSection("filterempty");
		int l = 0;
		boolean flag = blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT;
		int i1 = flag ? this.renderInfos.size() - 1 : 0;
		int i = flag ? -1 : this.renderInfos.size();
		int j1 = flag ? -1 : 1;

		for (int j = i1; j != i; j += j1) {
			RenderChunk renderchunk = ((RenderGlobal.ContainerLocalRenderInformation) this.renderInfos.get(j)).renderChunk;

			if (!renderchunk.getCompiledChunk().isLayerEmpty(blockLayerIn)) {
				++l;
				this.renderContainer.addRenderChunk(renderchunk, blockLayerIn);
			}
		}

		if (l == 0) {
			return l;
		} else {
			if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
				GlStateManager.disableFog();
			}

			this.mc.mcProfiler.endStartSection("render_" + blockLayerIn);
			this.renderBlockLayer(blockLayerIn);
			this.mc.mcProfiler.endSection();
			return l;
		}
	}

	private void renderBlockLayer(EnumWorldBlockLayer blockLayerIn) {
		this.mc.entityRenderer.enableLightmap();

		if (OpenGlHelper.useVbo()) {
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
			GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		}

		this.renderContainer.renderChunkLayer(blockLayerIn);

		if (OpenGlHelper.useVbo()) {
			for (VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements()) {
				VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
				int i = vertexformatelement.getIndex();

				switch (RenderGlobal.RenderGlobal$2.field_178037_a[vertexformatelement$enumusage.ordinal()]) {
				case 1:
					GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
					break;

				case 2:
					OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + i);
					GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
					OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
					break;

				case 3:
					GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
					GlStateManager.resetColor();
				}
			}
		}

		this.mc.entityRenderer.disableLightmap();
	}

	private void cleanupDamagedBlocks(Iterator iteratorIn) {
		while (iteratorIn.hasNext()) {
			DestroyBlockProgress destroyblockprogress = (DestroyBlockProgress) iteratorIn.next();
			int i = destroyblockprogress.getCreationCloudUpdateTick();

			if (this.cloudTickCounter - i > 400) {
				iteratorIn.remove();
			}
		}
	}

	public void updateClouds() {
		++this.cloudTickCounter;

		if (this.cloudTickCounter % 20 == 0) {
			this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
		}
	}

	private void renderSkyEnd() {
		if (Config.isSkyEnabled()) {
			GlStateManager.disableFog();
			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.depthMask(false);
			this.renderEngine.bindTexture(locationEndSkyPng);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();

			for (int i = 0; i < 6; ++i) {
				GlStateManager.pushMatrix();

				if (i == 1) {
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (i == 2) {
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (i == 3) {
					GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				}

				if (i == 4) {
					GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
				}

				if (i == 5) {
					GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
				}

				worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				worldrenderer.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
				worldrenderer.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
				worldrenderer.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
				worldrenderer.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.enableAlpha();
		}
	}

	public void renderSky(float partialTicks, int pass) {
		if (Reflector.ForgeWorldProvider_getSkyRenderer.exists()) {
			WorldProvider worldprovider = this.mc.theWorld.provider;
			Object object = Reflector.call(worldprovider, Reflector.ForgeWorldProvider_getSkyRenderer, new Object[0]);

			if (object != null) {
				Reflector.callVoid(object, Reflector.IRenderHandler_render, new Object[] { Float.valueOf(partialTicks), this.theWorld, this.mc });
				return;
			}
		}

		if (this.mc.theWorld.provider.getDimensionId() == 1) {
			this.renderSkyEnd();
		} else if (this.mc.theWorld.provider.isSurfaceWorld()) {
			GlStateManager.disableTexture2D();
			Vec3 vec3 = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
			vec3 = CustomColorizer.getSkyColor(vec3, this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0D, this.mc.getRenderViewEntity().posZ);
			float f14 = (float) vec3.xCoord;
			float f = (float) vec3.yCoord;
			float f1 = (float) vec3.zCoord;

			if (pass != 2) {
				float f2 = (f14 * 30.0F + f * 59.0F + f1 * 11.0F) / 100.0F;
				float f3 = (f14 * 30.0F + f * 70.0F) / 100.0F;
				float f4 = (f14 * 30.0F + f1 * 70.0F) / 100.0F;
				f14 = f2;
				f = f3;
				f1 = f4;
			}

			GlStateManager.color(f14, f, f1);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			GlStateManager.depthMask(false);
			GlStateManager.enableFog();
			GlStateManager.color(f14, f, f1);

			if (Config.isSkyEnabled()) {
				if (this.vboEnabled) {
					this.skyVBO.bindBuffer();
					GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
					GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
					this.skyVBO.drawArrays(7);
					this.skyVBO.unbindBuffer();
					GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
				} else {
					GlStateManager.callList(this.glSkyList);
				}
			}

			GlStateManager.disableFog();
			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			RenderHelper.disableStandardItemLighting();
			float[] afloat = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(partialTicks), partialTicks);

			if (afloat != null && Config.isSunMoonEnabled()) {
				GlStateManager.disableTexture2D();
				GlStateManager.shadeModel(7425);
				GlStateManager.pushMatrix();
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(MathHelper.sin(this.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
				float f5 = afloat[0];
				float f6 = afloat[1];
				float f7 = afloat[2];

				if (pass != 2) {
					float f8 = (f5 * 30.0F + f6 * 59.0F + f7 * 11.0F) / 100.0F;
					float f9 = (f5 * 30.0F + f6 * 70.0F) / 100.0F;
					float f10 = (f5 * 30.0F + f7 * 70.0F) / 100.0F;
					f5 = f8;
					f6 = f9;
					f7 = f10;
				}

				worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
				worldrenderer.pos(0.0D, 100.0D, 0.0D).color(f5, f6, f7, afloat[3]).endVertex();
				boolean flag = true;

				for (int i = 0; i <= 16; ++i) {
					float f20 = (float) i * (float) Math.PI * 2.0F / 16.0F;
					float f11 = MathHelper.sin(f20);
					float f12 = MathHelper.cos(f20);
					worldrenderer.pos((double) (f11 * 120.0F), (double) (f12 * 120.0F), (double) (-f12 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
				}

				tessellator.draw();
				GlStateManager.popMatrix();
				GlStateManager.shadeModel(7424);
			}

			GlStateManager.enableTexture2D();
			GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
			GlStateManager.pushMatrix();
			float f15 = 1.0F - this.theWorld.getRainStrength(partialTicks);
			GlStateManager.color(1.0F, 1.0F, 1.0F, f15);
			GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
			CustomSky.renderSky(this.theWorld, this.renderEngine, this.theWorld.getCelestialAngle(partialTicks), f15);
			GlStateManager.rotate(this.theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);

			if (Config.isSunMoonEnabled()) {
				float f16 = 30.0F;
				this.renderEngine.bindTexture(locationSunPng);
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				worldrenderer.pos((double) (-f16), 100.0D, (double) (-f16)).tex(0.0D, 0.0D).endVertex();
				worldrenderer.pos((double) f16, 100.0D, (double) (-f16)).tex(1.0D, 0.0D).endVertex();
				worldrenderer.pos((double) f16, 100.0D, (double) f16).tex(1.0D, 1.0D).endVertex();
				worldrenderer.pos((double) (-f16), 100.0D, (double) f16).tex(0.0D, 1.0D).endVertex();
				tessellator.draw();
				f16 = 20.0F;
				this.renderEngine.bindTexture(locationMoonPhasesPng);
				int l = this.theWorld.getMoonPhase();
				int j = l % 4;
				int k = l / 4 % 2;
				float f21 = (float) (j + 0) / 4.0F;
				float f22 = (float) (k + 0) / 2.0F;
				float f23 = (float) (j + 1) / 4.0F;
				float f13 = (float) (k + 1) / 2.0F;
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				worldrenderer.pos((double) (-f16), -100.0D, (double) f16).tex((double) f23, (double) f13).endVertex();
				worldrenderer.pos((double) f16, -100.0D, (double) f16).tex((double) f21, (double) f13).endVertex();
				worldrenderer.pos((double) f16, -100.0D, (double) (-f16)).tex((double) f21, (double) f22).endVertex();
				worldrenderer.pos((double) (-f16), -100.0D, (double) (-f16)).tex((double) f23, (double) f22).endVertex();
				tessellator.draw();
			}

			GlStateManager.disableTexture2D();
			float f24 = this.theWorld.getStarBrightness(partialTicks) * f15;

			if (f24 > 0.0F && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.theWorld)) {
				GlStateManager.color(f24, f24, f24, f24);

				if (this.vboEnabled) {
					this.starVBO.bindBuffer();
					GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
					GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
					this.starVBO.drawArrays(7);
					this.starVBO.unbindBuffer();
					GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
				} else {
					GlStateManager.callList(this.starGLCallList);
				}
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.enableFog();
			GlStateManager.popMatrix();
			GlStateManager.disableTexture2D();
			GlStateManager.color(0.0F, 0.0F, 0.0F);
			double d0 = this.mc.thePlayer.getPositionEyes(partialTicks).yCoord - this.theWorld.getHorizon();

			if (d0 < 0.0D) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 12.0F, 0.0F);

				if (this.vboEnabled) {
					this.sky2VBO.bindBuffer();
					GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
					GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
					this.sky2VBO.drawArrays(7);
					this.sky2VBO.unbindBuffer();
					GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
				} else {
					GlStateManager.callList(this.glSkyList2);
				}

				GlStateManager.popMatrix();
				float f17 = 1.0F;
				float f18 = -((float) (d0 + 65.0D));
				float f19 = -1.0F;
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
				worldrenderer.pos(-1.0D, (double) f18, 1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(1.0D, (double) f18, 1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(1.0D, (double) f18, -1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(-1.0D, (double) f18, -1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(1.0D, (double) f18, 1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(1.0D, (double) f18, -1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(-1.0D, (double) f18, -1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(-1.0D, (double) f18, 1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
				worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
				tessellator.draw();
			}

			if (this.theWorld.provider.isSkyColored()) {
				GlStateManager.color(f14 * 0.2F + 0.04F, f * 0.2F + 0.04F, f1 * 0.6F + 0.1F);
			} else {
				GlStateManager.color(f14, f, f1);
			}

			if (this.mc.gameSettings.renderDistanceChunks <= 4) {
				GlStateManager.color(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, -((float) (d0 - 16.0D)), 0.0F);

			if (Config.isSkyEnabled()) {
				GlStateManager.callList(this.glSkyList2);
			}

			GlStateManager.popMatrix();
			GlStateManager.enableTexture2D();
			GlStateManager.depthMask(true);
		}
	}

	public void renderClouds(float partialTicks, int pass) {
		if (!Config.isCloudsOff()) {
			if (Reflector.ForgeWorldProvider_getCloudRenderer.exists()) {
				WorldProvider worldprovider = this.mc.theWorld.provider;
				Object object = Reflector.call(worldprovider, Reflector.ForgeWorldProvider_getCloudRenderer, new Object[0]);

				if (object != null) {
					Reflector.callVoid(object, Reflector.IRenderHandler_render, new Object[] { Float.valueOf(partialTicks), this.theWorld, this.mc });
					return;
				}
			}

			if (this.mc.theWorld.provider.isSurfaceWorld()) {
				if (Config.isCloudsFancy()) {
					this.renderCloudsFancy(partialTicks, pass);
				} else {
					this.cloudRenderer.prepareToRender(false, this.cloudTickCounter, partialTicks);
					partialTicks = 0.0F;
					GlStateManager.disableCull();
					float f9 = (float) (this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * (double) partialTicks);
					boolean flag = true;
					boolean flag1 = true;
					Tessellator tessellator = Tessellator.getInstance();
					WorldRenderer worldrenderer = tessellator.getWorldRenderer();
					this.renderEngine.bindTexture(locationCloudsPng);
					GlStateManager.enableBlend();
					GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

					if (this.cloudRenderer.shouldUpdateGlList()) {
						this.cloudRenderer.startUpdateGlList();
						Vec3 vec3 = this.theWorld.getCloudColour(partialTicks);
						float f = (float) vec3.xCoord;
						float f1 = (float) vec3.yCoord;
						float f2 = (float) vec3.zCoord;

						if (pass != 2) {
							float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
							float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
							float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
							f = f3;
							f1 = f4;
							f2 = f5;
						}

						float f10 = 4.8828125E-4F;
						double d2 = (double) ((float) this.cloudTickCounter + partialTicks);
						double d0 = this.mc.getRenderViewEntity().prevPosX + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * (double) partialTicks + d2 * 0.029999999329447746D;
						double d1 = this.mc.getRenderViewEntity().prevPosZ + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * (double) partialTicks;
						int i = MathHelper.floor_double(d0 / 2048.0D);
						int j = MathHelper.floor_double(d1 / 2048.0D);
						d0 = d0 - (double) (i * 2048);
						d1 = d1 - (double) (j * 2048);
						float f6 = this.theWorld.provider.getCloudHeight() - f9 + 0.33F;
						f6 = f6 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
						float f7 = (float) (d0 * 4.8828125E-4D);
						float f8 = (float) (d1 * 4.8828125E-4D);
						worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

						for (int k = -256; k < 256; k += 32) {
							for (int l = -256; l < 256; l += 32) {
								worldrenderer.pos((double) (k + 0), (double) f6, (double) (l + 32)).tex((double) ((float) (k + 0) * 4.8828125E-4F + f7), (double) ((float) (l + 32) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
								worldrenderer.pos((double) (k + 32), (double) f6, (double) (l + 32)).tex((double) ((float) (k + 32) * 4.8828125E-4F + f7), (double) ((float) (l + 32) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
								worldrenderer.pos((double) (k + 32), (double) f6, (double) (l + 0)).tex((double) ((float) (k + 32) * 4.8828125E-4F + f7), (double) ((float) (l + 0) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
								worldrenderer.pos((double) (k + 0), (double) f6, (double) (l + 0)).tex((double) ((float) (k + 0) * 4.8828125E-4F + f7), (double) ((float) (l + 0) * 4.8828125E-4F + f8)).color(f, f1, f2, 0.8F).endVertex();
							}
						}

						tessellator.draw();
						this.cloudRenderer.endUpdateGlList();
					}

					this.cloudRenderer.renderGlList();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.disableBlend();
					GlStateManager.enableCull();
				}
			}
		}
	}

	/**
	 * Checks if the given position is to be rendered with cloud fog
	 */
	public boolean hasCloudFog(double x, double y, double z, float partialTicks) {
		return false;
	}

	private void renderCloudsFancy(float partialTicks, int pass) {
		this.cloudRenderer.prepareToRender(true, this.cloudTickCounter, partialTicks);
		partialTicks = 0.0F;
		GlStateManager.disableCull();
		float f = (float) (this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * (double) partialTicks);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		float f1 = 12.0F;
		float f2 = 4.0F;
		double d0 = (double) ((float) this.cloudTickCounter + partialTicks);
		double d1 = (this.mc.getRenderViewEntity().prevPosX + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * (double) partialTicks + d0 * 0.029999999329447746D) / 12.0D;
		double d2 = (this.mc.getRenderViewEntity().prevPosZ + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * (double) partialTicks) / 12.0D + 0.33000001311302185D;
		float f3 = this.theWorld.provider.getCloudHeight() - f + 0.33F;
		f3 = f3 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
		int i = MathHelper.floor_double(d1 / 2048.0D);
		int j = MathHelper.floor_double(d2 / 2048.0D);
		d1 = d1 - (double) (i * 2048);
		d2 = d2 - (double) (j * 2048);
		this.renderEngine.bindTexture(locationCloudsPng);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		Vec3 vec3 = this.theWorld.getCloudColour(partialTicks);
		float f4 = (float) vec3.xCoord;
		float f5 = (float) vec3.yCoord;
		float f6 = (float) vec3.zCoord;

		if (pass != 2) {
			float f7 = (f4 * 30.0F + f5 * 59.0F + f6 * 11.0F) / 100.0F;
			float f8 = (f4 * 30.0F + f5 * 70.0F) / 100.0F;
			float f9 = (f4 * 30.0F + f6 * 70.0F) / 100.0F;
			f4 = f7;
			f5 = f8;
			f6 = f9;
		}

		float f26 = f4 * 0.9F;
		float f27 = f5 * 0.9F;
		float f28 = f6 * 0.9F;
		float f10 = f4 * 0.7F;
		float f11 = f5 * 0.7F;
		float f12 = f6 * 0.7F;
		float f13 = f4 * 0.8F;
		float f14 = f5 * 0.8F;
		float f15 = f6 * 0.8F;
		float f16 = 0.00390625F;
		float f17 = (float) MathHelper.floor_double(d1) * 0.00390625F;
		float f18 = (float) MathHelper.floor_double(d2) * 0.00390625F;
		float f19 = (float) (d1 - (double) MathHelper.floor_double(d1));
		float f20 = (float) (d2 - (double) MathHelper.floor_double(d2));
		boolean flag = true;
		boolean flag1 = true;
		float f21 = 9.765625E-4F;
		GlStateManager.scale(12.0F, 1.0F, 12.0F);

		for (int k = 0; k < 2; ++k) {
			if (k == 0) {
				GlStateManager.colorMask(false, false, false, false);
			} else {
				switch (pass) {
				case 0:
					GlStateManager.colorMask(false, true, true, true);
					break;

				case 1:
					GlStateManager.colorMask(true, false, false, true);
					break;

				case 2:
					GlStateManager.colorMask(true, true, true, true);
				}
			}

			this.cloudRenderer.renderGlList();
		}

		if (this.cloudRenderer.shouldUpdateGlList()) {
			this.cloudRenderer.startUpdateGlList();

			for (int j1 = -3; j1 <= 4; ++j1) {
				for (int l = -3; l <= 4; ++l) {
					worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
					float f22 = (float) (j1 * 8);
					float f23 = (float) (l * 8);
					float f24 = f22 - f19;
					float f25 = f23 - f20;

					if (f3 > -5.0F) {
						worldrenderer.pos((double) (f24 + 0.0F), (double) (f3 + 0.0F), (double) (f25 + 8.0F)).tex((double) ((f22 + 0.0F) * 0.00390625F + f17), (double) ((f23 + 8.0F) * 0.00390625F + f18)).color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
						worldrenderer.pos((double) (f24 + 8.0F), (double) (f3 + 0.0F), (double) (f25 + 8.0F)).tex((double) ((f22 + 8.0F) * 0.00390625F + f17), (double) ((f23 + 8.0F) * 0.00390625F + f18)).color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
						worldrenderer.pos((double) (f24 + 8.0F), (double) (f3 + 0.0F), (double) (f25 + 0.0F)).tex((double) ((f22 + 8.0F) * 0.00390625F + f17), (double) ((f23 + 0.0F) * 0.00390625F + f18)).color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
						worldrenderer.pos((double) (f24 + 0.0F), (double) (f3 + 0.0F), (double) (f25 + 0.0F)).tex((double) ((f22 + 0.0F) * 0.00390625F + f17), (double) ((f23 + 0.0F) * 0.00390625F + f18)).color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
					}

					if (f3 <= 5.0F) {
						worldrenderer.pos((double) (f24 + 0.0F), (double) (f3 + 4.0F - 9.765625E-4F), (double) (f25 + 8.0F)).tex((double) ((f22 + 0.0F) * 0.00390625F + f17), (double) ((f23 + 8.0F) * 0.00390625F + f18)).color(f4, f5, f6, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
						worldrenderer.pos((double) (f24 + 8.0F), (double) (f3 + 4.0F - 9.765625E-4F), (double) (f25 + 8.0F)).tex((double) ((f22 + 8.0F) * 0.00390625F + f17), (double) ((f23 + 8.0F) * 0.00390625F + f18)).color(f4, f5, f6, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
						worldrenderer.pos((double) (f24 + 8.0F), (double) (f3 + 4.0F - 9.765625E-4F), (double) (f25 + 0.0F)).tex((double) ((f22 + 8.0F) * 0.00390625F + f17), (double) ((f23 + 0.0F) * 0.00390625F + f18)).color(f4, f5, f6, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
						worldrenderer.pos((double) (f24 + 0.0F), (double) (f3 + 4.0F - 9.765625E-4F), (double) (f25 + 0.0F)).tex((double) ((f22 + 0.0F) * 0.00390625F + f17), (double) ((f23 + 0.0F) * 0.00390625F + f18)).color(f4, f5, f6, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
					}

					if (j1 > -1) {
						for (int i1 = 0; i1 < 8; ++i1) {
							worldrenderer.pos((double) (f24 + (float) i1 + 0.0F), (double) (f3 + 0.0F), (double) (f25 + 8.0F)).tex((double) ((f22 + (float) i1 + 0.5F) * 0.00390625F + f17), (double) ((f23 + 8.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer.pos((double) (f24 + (float) i1 + 0.0F), (double) (f3 + 4.0F), (double) (f25 + 8.0F)).tex((double) ((f22 + (float) i1 + 0.5F) * 0.00390625F + f17), (double) ((f23 + 8.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer.pos((double) (f24 + (float) i1 + 0.0F), (double) (f3 + 4.0F), (double) (f25 + 0.0F)).tex((double) ((f22 + (float) i1 + 0.5F) * 0.00390625F + f17), (double) ((f23 + 0.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer.pos((double) (f24 + (float) i1 + 0.0F), (double) (f3 + 0.0F), (double) (f25 + 0.0F)).tex((double) ((f22 + (float) i1 + 0.5F) * 0.00390625F + f17), (double) ((f23 + 0.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
						}
					}

					if (j1 <= 1) {
						for (int k1 = 0; k1 < 8; ++k1) {
							worldrenderer.pos((double) (f24 + (float) k1 + 1.0F - 9.765625E-4F), (double) (f3 + 0.0F), (double) (f25 + 8.0F)).tex((double) ((f22 + (float) k1 + 0.5F) * 0.00390625F + f17), (double) ((f23 + 8.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer.pos((double) (f24 + (float) k1 + 1.0F - 9.765625E-4F), (double) (f3 + 4.0F), (double) (f25 + 8.0F)).tex((double) ((f22 + (float) k1 + 0.5F) * 0.00390625F + f17), (double) ((f23 + 8.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer.pos((double) (f24 + (float) k1 + 1.0F - 9.765625E-4F), (double) (f3 + 4.0F), (double) (f25 + 0.0F)).tex((double) ((f22 + (float) k1 + 0.5F) * 0.00390625F + f17), (double) ((f23 + 0.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer.pos((double) (f24 + (float) k1 + 1.0F - 9.765625E-4F), (double) (f3 + 0.0F), (double) (f25 + 0.0F)).tex((double) ((f22 + (float) k1 + 0.5F) * 0.00390625F + f17), (double) ((f23 + 0.0F) * 0.00390625F + f18)).color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
						}
					}

					if (l > -1) {
						for (int l1 = 0; l1 < 8; ++l1) {
							worldrenderer.pos((double) (f24 + 0.0F), (double) (f3 + 4.0F), (double) (f25 + (float) l1 + 0.0F)).tex((double) ((f22 + 0.0F) * 0.00390625F + f17), (double) ((f23 + (float) l1 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
							worldrenderer.pos((double) (f24 + 8.0F), (double) (f3 + 4.0F), (double) (f25 + (float) l1 + 0.0F)).tex((double) ((f22 + 8.0F) * 0.00390625F + f17), (double) ((f23 + (float) l1 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
							worldrenderer.pos((double) (f24 + 8.0F), (double) (f3 + 0.0F), (double) (f25 + (float) l1 + 0.0F)).tex((double) ((f22 + 8.0F) * 0.00390625F + f17), (double) ((f23 + (float) l1 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
							worldrenderer.pos((double) (f24 + 0.0F), (double) (f3 + 0.0F), (double) (f25 + (float) l1 + 0.0F)).tex((double) ((f22 + 0.0F) * 0.00390625F + f17), (double) ((f23 + (float) l1 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
						}
					}

					if (l <= 1) {
						for (int i2 = 0; i2 < 8; ++i2) {
							worldrenderer.pos((double) (f24 + 0.0F), (double) (f3 + 4.0F), (double) (f25 + (float) i2 + 1.0F - 9.765625E-4F)).tex((double) ((f22 + 0.0F) * 0.00390625F + f17), (double) ((f23 + (float) i2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
							worldrenderer.pos((double) (f24 + 8.0F), (double) (f3 + 4.0F), (double) (f25 + (float) i2 + 1.0F - 9.765625E-4F)).tex((double) ((f22 + 8.0F) * 0.00390625F + f17), (double) ((f23 + (float) i2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
							worldrenderer.pos((double) (f24 + 8.0F), (double) (f3 + 0.0F), (double) (f25 + (float) i2 + 1.0F - 9.765625E-4F)).tex((double) ((f22 + 8.0F) * 0.00390625F + f17), (double) ((f23 + (float) i2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
							worldrenderer.pos((double) (f24 + 0.0F), (double) (f3 + 0.0F), (double) (f25 + (float) i2 + 1.0F - 9.765625E-4F)).tex((double) ((f22 + 0.0F) * 0.00390625F + f17), (double) ((f23 + (float) i2 + 0.5F) * 0.00390625F + f18)).color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
						}
					}

					tessellator.draw();
				}
			}

			this.cloudRenderer.endUpdateGlList();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
	}

	public void updateChunks(long finishTimeNano) {
		this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);

		if (this.chunksToUpdateForced.size() > 0) {
			Iterator iterator = this.chunksToUpdateForced.iterator();

			while (iterator.hasNext()) {
				RenderChunk renderchunk = (RenderChunk) iterator.next();

				if (!this.renderDispatcher.updateChunkLater(renderchunk)) {
					break;
				}

				renderchunk.setNeedsUpdate(false);
				iterator.remove();
				this.chunksToUpdate.remove(renderchunk);
				this.chunksToResortTransparency.remove(renderchunk);
			}
		}

		if (this.chunksToResortTransparency.size() > 0) {
			Iterator iterator2 = this.chunksToResortTransparency.iterator();

			if (iterator2.hasNext()) {
				RenderChunk renderchunk2 = (RenderChunk) iterator2.next();

				if (this.renderDispatcher.updateTransparencyLater(renderchunk2)) {
					iterator2.remove();
				}
			}
		}

		int j = 0;
		int k = Config.getUpdatesPerFrame();
		int i = k * 2;
		Iterator iterator1 = this.chunksToUpdate.iterator();

		while (iterator1.hasNext()) {
			RenderChunk renderchunk1 = (RenderChunk) iterator1.next();

			if (!this.renderDispatcher.updateChunkLater(renderchunk1)) {
				break;
			}

			renderchunk1.setNeedsUpdate(false);
			iterator1.remove();

			if (renderchunk1.getCompiledChunk().isEmpty() && k < i) {
				++k;
			}

			++j;

			if (j >= k) {
				break;
			}
		}
	}

	public void renderWorldBorder(Entity p_180449_1_, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		WorldBorder worldborder = this.theWorld.getWorldBorder();
		double d0 = (double) (this.mc.gameSettings.renderDistanceChunks * 16);

		if (p_180449_1_.posX >= worldborder.maxX() - d0 || p_180449_1_.posX <= worldborder.minX() + d0 || p_180449_1_.posZ >= worldborder.maxZ() - d0 || p_180449_1_.posZ <= worldborder.minZ() + d0) {
			double d1 = 1.0D - worldborder.getClosestDistance(p_180449_1_) / d0;
			d1 = Math.pow(d1, 4.0D);
			double d2 = p_180449_1_.lastTickPosX + (p_180449_1_.posX - p_180449_1_.lastTickPosX) * (double) partialTicks;
			double d3 = p_180449_1_.lastTickPosY + (p_180449_1_.posY - p_180449_1_.lastTickPosY) * (double) partialTicks;
			double d4 = p_180449_1_.lastTickPosZ + (p_180449_1_.posZ - p_180449_1_.lastTickPosZ) * (double) partialTicks;
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
			this.renderEngine.bindTexture(locationForcefieldPng);
			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			int i = worldborder.getStatus().getID();
			float f = (float) (i >> 16 & 255) / 255.0F;
			float f1 = (float) (i >> 8 & 255) / 255.0F;
			float f2 = (float) (i & 255) / 255.0F;
			GlStateManager.color(f, f1, f2, (float) d1);
			GlStateManager.doPolygonOffset(-3.0F, -3.0F);
			GlStateManager.enablePolygonOffset();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableAlpha();
			GlStateManager.disableCull();
			float f3 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F;
			float f4 = 0.0F;
			float f5 = 0.0F;
			float f6 = 128.0F;
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			worldrenderer.setTranslation(-d2, -d3, -d4);
			double d5 = Math.max((double) MathHelper.floor_double(d4 - d0), worldborder.minZ());
			double d6 = Math.min((double) MathHelper.ceiling_double_int(d4 + d0), worldborder.maxZ());

			if (d2 > worldborder.maxX() - d0) {
				float f7 = 0.0F;

				for (double d7 = d5; d7 < d6; f7 += 0.5F) {
					double d8 = Math.min(1.0D, d6 - d7);
					float f8 = (float) d8 * 0.5F;
					worldrenderer.pos(worldborder.maxX(), 256.0D, d7).tex((double) (f3 + f7), (double) (f3 + 0.0F)).endVertex();
					worldrenderer.pos(worldborder.maxX(), 256.0D, d7 + d8).tex((double) (f3 + f8 + f7), (double) (f3 + 0.0F)).endVertex();
					worldrenderer.pos(worldborder.maxX(), 0.0D, d7 + d8).tex((double) (f3 + f8 + f7), (double) (f3 + 128.0F)).endVertex();
					worldrenderer.pos(worldborder.maxX(), 0.0D, d7).tex((double) (f3 + f7), (double) (f3 + 128.0F)).endVertex();
					++d7;
				}
			}

			if (d2 < worldborder.minX() + d0) {
				float f9 = 0.0F;

				for (double d9 = d5; d9 < d6; f9 += 0.5F) {
					double d12 = Math.min(1.0D, d6 - d9);
					float f12 = (float) d12 * 0.5F;
					worldrenderer.pos(worldborder.minX(), 256.0D, d9).tex((double) (f3 + f9), (double) (f3 + 0.0F)).endVertex();
					worldrenderer.pos(worldborder.minX(), 256.0D, d9 + d12).tex((double) (f3 + f12 + f9), (double) (f3 + 0.0F)).endVertex();
					worldrenderer.pos(worldborder.minX(), 0.0D, d9 + d12).tex((double) (f3 + f12 + f9), (double) (f3 + 128.0F)).endVertex();
					worldrenderer.pos(worldborder.minX(), 0.0D, d9).tex((double) (f3 + f9), (double) (f3 + 128.0F)).endVertex();
					++d9;
				}
			}

			d5 = Math.max((double) MathHelper.floor_double(d2 - d0), worldborder.minX());
			d6 = Math.min((double) MathHelper.ceiling_double_int(d2 + d0), worldborder.maxX());

			if (d4 > worldborder.maxZ() - d0) {
				float f10 = 0.0F;

				for (double d10 = d5; d10 < d6; f10 += 0.5F) {
					double d13 = Math.min(1.0D, d6 - d10);
					float f13 = (float) d13 * 0.5F;
					worldrenderer.pos(d10, 256.0D, worldborder.maxZ()).tex((double) (f3 + f10), (double) (f3 + 0.0F)).endVertex();
					worldrenderer.pos(d10 + d13, 256.0D, worldborder.maxZ()).tex((double) (f3 + f13 + f10), (double) (f3 + 0.0F)).endVertex();
					worldrenderer.pos(d10 + d13, 0.0D, worldborder.maxZ()).tex((double) (f3 + f13 + f10), (double) (f3 + 128.0F)).endVertex();
					worldrenderer.pos(d10, 0.0D, worldborder.maxZ()).tex((double) (f3 + f10), (double) (f3 + 128.0F)).endVertex();
					++d10;
				}
			}

			if (d4 < worldborder.minZ() + d0) {
				float f11 = 0.0F;

				for (double d11 = d5; d11 < d6; f11 += 0.5F) {
					double d14 = Math.min(1.0D, d6 - d11);
					float f14 = (float) d14 * 0.5F;
					worldrenderer.pos(d11, 256.0D, worldborder.minZ()).tex((double) (f3 + f11), (double) (f3 + 0.0F)).endVertex();
					worldrenderer.pos(d11 + d14, 256.0D, worldborder.minZ()).tex((double) (f3 + f14 + f11), (double) (f3 + 0.0F)).endVertex();
					worldrenderer.pos(d11 + d14, 0.0D, worldborder.minZ()).tex((double) (f3 + f14 + f11), (double) (f3 + 128.0F)).endVertex();
					worldrenderer.pos(d11, 0.0D, worldborder.minZ()).tex((double) (f3 + f11), (double) (f3 + 128.0F)).endVertex();
					++d11;
				}
			}

			tessellator.draw();
			worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
			GlStateManager.enableCull();
			GlStateManager.disableAlpha();
			GlStateManager.doPolygonOffset(0.0F, 0.0F);
			GlStateManager.disablePolygonOffset();
			GlStateManager.enableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
		}
	}

	private void preRenderDamagedBlocks() {
		GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.doPolygonOffset(-3.0F, -3.0F);
		GlStateManager.enablePolygonOffset();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableAlpha();
		GlStateManager.pushMatrix();
	}

	private void postRenderDamagedBlocks() {
		GlStateManager.disableAlpha();
		GlStateManager.doPolygonOffset(0.0F, 0.0F);
		GlStateManager.disablePolygonOffset();
		GlStateManager.enableAlpha();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}

	public void drawBlockDamageTexture(Tessellator tessellatorIn, WorldRenderer worldRendererIn, Entity entityIn, float partialTicks) {
		double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
		double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
		double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;

		if (!this.damagedBlocks.isEmpty()) {
			this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			this.preRenderDamagedBlocks();
			worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
			worldRendererIn.setTranslation(-d0, -d1, -d2);
			worldRendererIn.markDirty();
			Iterator iterator = this.damagedBlocks.values().iterator();

			while (iterator.hasNext()) {
				DestroyBlockProgress destroyblockprogress = (DestroyBlockProgress) iterator.next();
				BlockPos blockpos = destroyblockprogress.getPosition();
				double d3 = (double) blockpos.getX() - d0;
				double d4 = (double) blockpos.getY() - d1;
				double d5 = (double) blockpos.getZ() - d2;
				Block block = this.theWorld.getBlockState(blockpos).getBlock();
				boolean flag;

				if (Reflector.ForgeTileEntity_canRenderBreaking.exists()) {
					boolean flag1 = block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull;

					if (!flag1) {
						TileEntity tileentity = this.theWorld.getTileEntity(blockpos);

						if (tileentity != null) {
							flag1 = Reflector.callBoolean(tileentity, Reflector.ForgeTileEntity_canRenderBreaking, new Object[0]);
						}
					}

					flag = !flag1;
				} else {
					flag = !(block instanceof BlockChest) && !(block instanceof BlockEnderChest) && !(block instanceof BlockSign) && !(block instanceof BlockSkull);
				}

				if (flag) {
					if (d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D) {
						iterator.remove();
					} else {
						IBlockState iblockstate = this.theWorld.getBlockState(blockpos);

						if (iblockstate.getBlock().getMaterial() != Material.air) {
							int i = destroyblockprogress.getPartialBlockDamage();
							TextureAtlasSprite textureatlassprite = this.destroyBlockIcons[i];
							BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
							blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, this.theWorld);
						}
					}
				}
			}

			tessellatorIn.draw();
			worldRendererIn.setTranslation(0.0D, 0.0D, 0.0D);
			this.postRenderDamagedBlocks();
		}
	}

	/**
	 * Draws the selection box for the player. Args: entityPlayer, rayTraceHit, i,
	 * itemStack, partialTickTime
	 */
	public void drawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int p_72731_3_, float partialTicks) {
		if (p_72731_3_ == 0 && movingObjectPositionIn.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			float f = 0.002F;
			BlockPos blockpos = movingObjectPositionIn.getBlockPos();
			Block block = this.theWorld.getBlockState(blockpos).getBlock();

			if (block.getMaterial() != Material.air && this.theWorld.getWorldBorder().contains(blockpos)) {
				block.setBlockBoundsBasedOnState(this.theWorld, blockpos);
				double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
				double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
				double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
				func_181561_a(block.getSelectedBoundingBox(this.theWorld, blockpos).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2));
			}

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
		}
	}

	public static void func_181561_a(AxisAlignedBB p_181561_0_) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(3, DefaultVertexFormats.POSITION);
		worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
		worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
		worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
		worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
		worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
		tessellator.draw();
		worldrenderer.begin(3, DefaultVertexFormats.POSITION);
		worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
		worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
		worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
		worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
		worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
		tessellator.draw();
		worldrenderer.begin(1, DefaultVertexFormats.POSITION);
		worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
		worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
		worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
		worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
		worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
		worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
		worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
		worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
		tessellator.draw();
	}

	public static void func_181563_a(AxisAlignedBB p_181563_0_, int p_181563_1_, int p_181563_2_, int p_181563_3_, int p_181563_4_) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(p_181563_0_.minX, p_181563_0_.minY, p_181563_0_.minZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.maxX, p_181563_0_.minY, p_181563_0_.minZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.maxX, p_181563_0_.minY, p_181563_0_.maxZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.minX, p_181563_0_.minY, p_181563_0_.maxZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.minX, p_181563_0_.minY, p_181563_0_.minZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		tessellator.draw();
		worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(p_181563_0_.minX, p_181563_0_.maxY, p_181563_0_.minZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.maxX, p_181563_0_.maxY, p_181563_0_.minZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.maxX, p_181563_0_.maxY, p_181563_0_.maxZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.minX, p_181563_0_.maxY, p_181563_0_.maxZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.minX, p_181563_0_.maxY, p_181563_0_.minZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		tessellator.draw();
		worldrenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(p_181563_0_.minX, p_181563_0_.minY, p_181563_0_.minZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.minX, p_181563_0_.maxY, p_181563_0_.minZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.maxX, p_181563_0_.minY, p_181563_0_.minZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.maxX, p_181563_0_.maxY, p_181563_0_.minZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.maxX, p_181563_0_.minY, p_181563_0_.maxZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.maxX, p_181563_0_.maxY, p_181563_0_.maxZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.minX, p_181563_0_.minY, p_181563_0_.maxZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		worldrenderer.pos(p_181563_0_.minX, p_181563_0_.maxY, p_181563_0_.maxZ).color(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).endVertex();
		tessellator.draw();
	}

	/**
	 * Marks the blocks in the given range for update
	 */
	private void markBlocksForUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.viewFrustum.markBlocksForUpdate(x1, y1, z1, x2, y2, z2);
	}

	public void markBlockForUpdate(BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		this.markBlocksForUpdate(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1);
	}

	public void notifyLightSet(BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		this.markBlocksForUpdate(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1);
	}

	/**
	 * On the client, re-renders all blocks in this range, inclusive. On the server,
	 * does nothing. Args: min x, min y, min z, max x, max y, max z
	 */
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1);
	}

	public void playRecord(String recordName, BlockPos blockPosIn) {
		ISound isound = (ISound) this.mapSoundPositions.get(blockPosIn);

		if (isound != null) {
			this.mc.getSoundHandler().stopSound(isound);
			this.mapSoundPositions.remove(blockPosIn);
		}

		if (recordName != null) {
			ItemRecord itemrecord = ItemRecord.getRecord(recordName);

			if (itemrecord != null) {
				this.mc.ingameGUI.setRecordPlayingMessage(itemrecord.getRecordNameLocal());
			}

			ResourceLocation resourcelocation = null;

			if (Reflector.ForgeItemRecord_getRecordResource.exists() && itemrecord != null) {
				resourcelocation = (ResourceLocation) Reflector.call(itemrecord, Reflector.ForgeItemRecord_getRecordResource, new Object[] { recordName });
			}

			if (resourcelocation == null) {
				resourcelocation = new ResourceLocation(recordName);
			}

			PositionedSoundRecord positionedsoundrecord = PositionedSoundRecord.create(resourcelocation, (float) blockPosIn.getX(), (float) blockPosIn.getY(), (float) blockPosIn.getZ());
			this.mapSoundPositions.put(blockPosIn, positionedsoundrecord);
			this.mc.getSoundHandler().playSound(positionedsoundrecord);
		}
	}

	/**
	 * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
	 */
	public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {
	}

	/**
	 * Plays sound to all near players except the player reference given
	 */
	public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch) {
	}

	public void spawnParticle(int particleID, boolean ignoreRange, final double xCoord, final double yCoord, final double zCoord, double xOffset, double yOffset, double zOffset, int... p_180442_15_) {
		try {
			this.spawnEntityFX(particleID, ignoreRange, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_180442_15_);
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
			crashreportcategory.addCrashSection("ID", Integer.valueOf(particleID));

			if (p_180442_15_ != null) {
				crashreportcategory.addCrashSection("Parameters", p_180442_15_);
			}

			crashreportcategory.addCrashSectionCallable("Position", new Callable() {
				

				public String call() throws Exception {
					return CrashReportCategory.getCoordinateInfo(xCoord, yCoord, zCoord);
				}
			});
			throw new ReportedException(crashreport);
		}
	}

	private void spawnParticle(EnumParticleTypes particleIn, double p_174972_2_, double p_174972_4_, double p_174972_6_, double p_174972_8_, double p_174972_10_, double p_174972_12_, int... p_174972_14_) {
		this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), p_174972_2_, p_174972_4_, p_174972_6_, p_174972_8_, p_174972_10_, p_174972_12_, p_174972_14_);
	}

	private EntityFX spawnEntityFX(int p_174974_1_, boolean ignoreRange, double p_174974_3_, double p_174974_5_, double p_174974_7_, double p_174974_9_, double p_174974_11_, double p_174974_13_, int... p_174974_15_) {
		if (this.mc != null && this.mc.getRenderViewEntity() != null && this.mc.effectRenderer != null) {
			int i = this.mc.gameSettings.particleSetting;

			if (i == 1 && this.theWorld.rand.nextInt(3) == 0) {
				i = 2;
			}

			double d0 = this.mc.getRenderViewEntity().posX - p_174974_3_;
			double d1 = this.mc.getRenderViewEntity().posY - p_174974_5_;
			double d2 = this.mc.getRenderViewEntity().posZ - p_174974_7_;

			if (p_174974_1_ == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() && !Config.isAnimatedExplosion()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() && !Config.isAnimatedExplosion()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID() && !Config.isAnimatedExplosion()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.SUSPENDED.getParticleID() && !Config.isWaterParticles()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.SUSPENDED_DEPTH.getParticleID() && !Config.isVoidParticles()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.SMOKE_NORMAL.getParticleID() && !Config.isAnimatedSmoke()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.SMOKE_LARGE.getParticleID() && !Config.isAnimatedSmoke()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.SPELL_MOB.getParticleID() && !Config.isPotionParticles()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID() && !Config.isPotionParticles()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.SPELL.getParticleID() && !Config.isPotionParticles()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.SPELL_INSTANT.getParticleID() && !Config.isPotionParticles()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.SPELL_WITCH.getParticleID() && !Config.isPotionParticles()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.PORTAL.getParticleID() && !Config.isAnimatedPortal()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.FLAME.getParticleID() && !Config.isAnimatedFlame()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.REDSTONE.getParticleID() && !Config.isAnimatedRedstone()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.DRIP_WATER.getParticleID() && !Config.isDrippingWaterLava()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.DRIP_LAVA.getParticleID() && !Config.isDrippingWaterLava()) {
				return null;
			} else if (p_174974_1_ == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() && !Config.isFireworkParticles()) {
				return null;
			} else if (ignoreRange) {
				return this.mc.effectRenderer.spawnEffectParticle(p_174974_1_, p_174974_3_, p_174974_5_, p_174974_7_, p_174974_9_, p_174974_11_, p_174974_13_, p_174974_15_);
			} else {
				double d3 = 16.0D;
				double d4 = 256.0D;

				if (p_174974_1_ == EnumParticleTypes.CRIT.getParticleID()) {
					d4 = 38416.0D;
				}

				if (d0 * d0 + d1 * d1 + d2 * d2 > d4) {
					return null;
				} else if (i > 1) {
					return null;
				} else {
					EntityFX entityfx = this.mc.effectRenderer.spawnEffectParticle(p_174974_1_, p_174974_3_, p_174974_5_, p_174974_7_, p_174974_9_, p_174974_11_, p_174974_13_, p_174974_15_);

					if (p_174974_1_ == EnumParticleTypes.WATER_BUBBLE.getParticleID()) {
						CustomColorizer.updateWaterFX(entityfx, this.theWorld, p_174974_3_, p_174974_5_, p_174974_7_);
					}

					if (p_174974_1_ == EnumParticleTypes.WATER_SPLASH.getParticleID()) {
						CustomColorizer.updateWaterFX(entityfx, this.theWorld, p_174974_3_, p_174974_5_, p_174974_7_);
					}

					if (p_174974_1_ == EnumParticleTypes.WATER_DROP.getParticleID()) {
						CustomColorizer.updateWaterFX(entityfx, this.theWorld, p_174974_3_, p_174974_5_, p_174974_7_);
					}

					if (p_174974_1_ == EnumParticleTypes.TOWN_AURA.getParticleID()) {
						CustomColorizer.updateMyceliumFX(entityfx);
					}

					if (p_174974_1_ == EnumParticleTypes.PORTAL.getParticleID()) {
						CustomColorizer.updatePortalFX(entityfx);
					}

					if (p_174974_1_ == EnumParticleTypes.REDSTONE.getParticleID()) {
						CustomColorizer.updateReddustFX(entityfx, this.theWorld, p_174974_3_, p_174974_5_, p_174974_7_);
					}

					return entityfx;
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * Called on all IWorldAccesses when an entity is created or loaded. On client
	 * worlds, starts downloading any necessary textures. On server worlds, adds the
	 * entity to the entity tracker.
	 */
	public void onEntityAdded(Entity entityIn) {
		RandomMobs.entityLoaded(entityIn);
	}

	/**
	 * Called on all IWorldAccesses when an entity is unloaded or destroyed. On
	 * client worlds, releases any downloaded textures. On server worlds, removes
	 * the entity from the entity tracker.
	 */
	public void onEntityRemoved(Entity entityIn) {
	}

	/**
	 * Deletes all display lists
	 */
	public void deleteAllDisplayLists() {
	}

	public void broadcastSound(int p_180440_1_, BlockPos p_180440_2_, int p_180440_3_) {
		switch (p_180440_1_) {
		case 1013:
		case 1018:
			if (this.mc.getRenderViewEntity() != null) {
				double d0 = (double) p_180440_2_.getX() - this.mc.getRenderViewEntity().posX;
				double d1 = (double) p_180440_2_.getY() - this.mc.getRenderViewEntity().posY;
				double d2 = (double) p_180440_2_.getZ() - this.mc.getRenderViewEntity().posZ;
				double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
				double d4 = this.mc.getRenderViewEntity().posX;
				double d5 = this.mc.getRenderViewEntity().posY;
				double d6 = this.mc.getRenderViewEntity().posZ;

				if (d3 > 0.0D) {
					d4 += d0 / d3 * 2.0D;
					d5 += d1 / d3 * 2.0D;
					d6 += d2 / d3 * 2.0D;
				}

				if (p_180440_1_ == 1013) {
					this.theWorld.playSound(d4, d5, d6, "mob.wither.spawn", 1.0F, 1.0F, false);
				} else {
					this.theWorld.playSound(d4, d5, d6, "mob.enderdragon.end", 5.0F, 1.0F, false);
				}
			}

		default:
		}
	}

	public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int p_180439_4_) {
		Random random = this.theWorld.rand;

		switch (sfxType) {
		case 1000:
			this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.0F, false);
			break;

		case 1001:
			this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.2F, false);
			break;

		case 1002:
			this.theWorld.playSoundAtPos(blockPosIn, "random.bow", 1.0F, 1.2F, false);
			break;

		case 1003:
			this.theWorld.playSoundAtPos(blockPosIn, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			break;

		case 1004:
			this.theWorld.playSoundAtPos(blockPosIn, "random.fizz", 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
			break;

		case 1005:
			if (Item.getItemById(p_180439_4_) instanceof ItemRecord) {
				this.theWorld.playRecord(blockPosIn, "records." + ((ItemRecord) Item.getItemById(p_180439_4_)).recordName);
			} else {
				this.theWorld.playRecord(blockPosIn, (String) null);
			}

			break;

		case 1006:
			this.theWorld.playSoundAtPos(blockPosIn, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			break;

		case 1007:
			this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.charge", 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;

		case 1008:
			this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;

		case 1009:
			this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;

		case 1010:
			this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.wood", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;

		case 1011:
			this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.metal", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;

		case 1012:
			this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.woodbreak", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;

		case 1014:
			this.theWorld.playSoundAtPos(blockPosIn, "mob.wither.shoot", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;

		case 1015:
			this.theWorld.playSoundAtPos(blockPosIn, "mob.bat.takeoff", 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;

		case 1016:
			this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.infect", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;

		case 1017:
			this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.unfect", 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
			break;

		case 1020:
			this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			break;

		case 1021:
			this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			break;

		case 1022:
			this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			break;

		case 2000:
			int k = p_180439_4_ % 3 - 1;
			int l = p_180439_4_ / 3 % 3 - 1;
			double d13 = (double) blockPosIn.getX() + (double) k * 0.6D + 0.5D;
			double d15 = (double) blockPosIn.getY() + 0.5D;
			double d19 = (double) blockPosIn.getZ() + (double) l * 0.6D + 0.5D;

			for (int l1 = 0; l1 < 10; ++l1) {
				double d20 = random.nextDouble() * 0.2D + 0.01D;
				double d21 = d13 + (double) k * 0.01D + (random.nextDouble() - 0.5D) * (double) l * 0.5D;
				double d22 = d15 + (random.nextDouble() - 0.5D) * 0.5D;
				double d23 = d19 + (double) l * 0.01D + (random.nextDouble() - 0.5D) * (double) k * 0.5D;
				double d24 = (double) k * d20 + random.nextGaussian() * 0.01D;
				double d9 = -0.03D + random.nextGaussian() * 0.01D;
				double d10 = (double) l * d20 + random.nextGaussian() * 0.01D;
				this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d21, d22, d23, d24, d9, d10, new int[0]);
			}

			return;

		case 2001:
			Block block = Block.getBlockById(p_180439_4_ & 4095);

			if (block.getMaterial() != Material.air) {
				this.mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(block.stepSound.getBreakSound()), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getFrequency() * 0.8F, (float) blockPosIn.getX() + 0.5F, (float) blockPosIn.getY() + 0.5F, (float) blockPosIn.getZ() + 0.5F));
			}

			this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, block.getStateFromMeta(p_180439_4_ >> 12 & 255));
			break;

		case 2002:
			double d11 = (double) blockPosIn.getX();
			double d12 = (double) blockPosIn.getY();
			double d14 = (double) blockPosIn.getZ();

			for (int i1 = 0; i1 < 8; ++i1) {
				this.spawnParticle(EnumParticleTypes.ITEM_CRACK, d11, d12, d14, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, new int[] { Item.getIdFromItem(Items.potionitem), p_180439_4_ });
			}

			int j1 = Items.potionitem.getColorFromDamage(p_180439_4_);
			float f = (float) (j1 >> 16 & 255) / 255.0F;
			float f1 = (float) (j1 >> 8 & 255) / 255.0F;
			float f2 = (float) (j1 >> 0 & 255) / 255.0F;
			EnumParticleTypes enumparticletypes = EnumParticleTypes.SPELL;

			if (Items.potionitem.isEffectInstant(p_180439_4_)) {
				enumparticletypes = EnumParticleTypes.SPELL_INSTANT;
			}

			for (int k1 = 0; k1 < 100; ++k1) {
				double d16 = random.nextDouble() * 4.0D;
				double d17 = random.nextDouble() * Math.PI * 2.0D;
				double d18 = Math.cos(d17) * d16;
				double d7 = 0.01D + random.nextDouble() * 0.5D;
				double d8 = Math.sin(d17) * d16;
				EntityFX entityfx = this.spawnEntityFX(enumparticletypes.getParticleID(), enumparticletypes.getShouldIgnoreRange(), d11 + d18 * 0.1D, d12 + 0.3D, d14 + d8 * 0.1D, d18, d7, d8, new int[0]);

				if (entityfx != null) {
					float f3 = 0.75F + random.nextFloat() * 0.25F;
					entityfx.setRBGColorF(f * f3, f1 * f3, f2 * f3);
					entityfx.multiplyVelocity((float) d16);
				}
			}

			this.theWorld.playSoundAtPos(blockPosIn, "game.potion.smash", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
			break;

		case 2003:
			double var7 = (double) blockPosIn.getX() + 0.5D;
			double var9 = (double) blockPosIn.getY();
			double var11 = (double) blockPosIn.getZ() + 0.5D;

			for (int var13 = 0; var13 < 8; ++var13) {
				this.spawnParticle(EnumParticleTypes.ITEM_CRACK, var7, var9, var11, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D, new int[] { Item.getIdFromItem(Items.ender_eye) });
			}

			for (double var32 = 0.0D; var32 < (Math.PI * 2D); var32 += 0.15707963267948966D) {
				this.spawnParticle(EnumParticleTypes.PORTAL, var7 + Math.cos(var32) * 5.0D, var9 - 0.4D, var11 + Math.sin(var32) * 5.0D, Math.cos(var32) * -5.0D, 0.0D, Math.sin(var32) * -5.0D, new int[0]);
				this.spawnParticle(EnumParticleTypes.PORTAL, var7 + Math.cos(var32) * 5.0D, var9 - 0.4D, var11 + Math.sin(var32) * 5.0D, Math.cos(var32) * -7.0D, 0.0D, Math.sin(var32) * -7.0D, new int[0]);
			}

			return;

		case 2004:
			for (int var18 = 0; var18 < 20; ++var18) {
				double d3 = (double) blockPosIn.getX() + 0.5D + ((double) this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
				double d4 = (double) blockPosIn.getY() + 0.5D + ((double) this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
				double d5 = (double) blockPosIn.getZ() + 0.5D + ((double) this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
				this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0.0D, 0.0D, 0.0D, new int[0]);
				this.theWorld.spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D, new int[0]);
			}

			return;

		case 2005:
			ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, p_180439_4_);
		}
	}

	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
		if (progress >= 0 && progress < 10) {
			DestroyBlockProgress destroyblockprogress = (DestroyBlockProgress) this.damagedBlocks.get(Integer.valueOf(breakerId));

			if (destroyblockprogress == null || destroyblockprogress.getPosition().getX() != pos.getX() || destroyblockprogress.getPosition().getY() != pos.getY() || destroyblockprogress.getPosition().getZ() != pos.getZ()) {
				destroyblockprogress = new DestroyBlockProgress(breakerId, pos);
				this.damagedBlocks.put(Integer.valueOf(breakerId), destroyblockprogress);
			}

			destroyblockprogress.setPartialBlockDamage(progress);
			destroyblockprogress.setCloudUpdateTick(this.cloudTickCounter);
		} else {
			this.damagedBlocks.remove(Integer.valueOf(breakerId));
		}
	}

	public void setDisplayListEntitiesDirty() {
		this.displayListEntitiesDirty = true;
	}

	public void resetClouds() {
		this.cloudRenderer.reset();
	}

	public int getCountRenderers() {
		return this.viewFrustum.renderChunks.length;
	}

	public int getCountActiveRenderers() {
		return this.renderInfos.size();
	}

	public int getCountEntitiesRendered() {
		return this.countEntitiesRendered;
	}

	public int getCountTileEntitiesRendered() {
		return this.countTileEntitiesRendered;
	}

	public void func_181023_a(Collection p_181023_1_, Collection p_181023_2_) {
		Set set = this.field_181024_n;

		synchronized (this.field_181024_n) {
			this.field_181024_n.removeAll(p_181023_1_);
			this.field_181024_n.addAll(p_181023_2_);
		}
	}

	static final class RenderGlobal$2 {
		static final int[] field_178037_a = new int[VertexFormatElement.EnumUsage.values().length];
		

		static {
			try {
				field_178037_a[VertexFormatElement.EnumUsage.POSITION.ordinal()] = 1;
			} catch (NoSuchFieldError var3) {
				;
			}

			try {
				field_178037_a[VertexFormatElement.EnumUsage.UV.ordinal()] = 2;
			} catch (NoSuchFieldError var2) {
				;
			}

			try {
				field_178037_a[VertexFormatElement.EnumUsage.COLOR.ordinal()] = 3;
			} catch (NoSuchFieldError var1) {
				;
			}
		}
	}

	class ContainerLocalRenderInformation {
		final RenderChunk renderChunk;
		final EnumFacing facing;
		final Set setFacing;
		final int counter;
		

		private ContainerLocalRenderInformation(RenderChunk renderChunkIn, EnumFacing facingIn, int counterIn) {
			this.setFacing = EnumSet.noneOf(EnumFacing.class);
			this.renderChunk = renderChunkIn;
			this.facing = facingIn;
			this.counter = counterIn;
		}

		ContainerLocalRenderInformation(RenderChunk p_i4_2_, EnumFacing p_i4_3_, int p_i4_4_, Object p_i4_5_) {
			this(p_i4_2_, p_i4_3_, p_i4_4_);
		}
	}
}
