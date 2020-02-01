package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RenderManager
{
    private Map < Class <? extends Entity > , Render <? extends Entity >> entityRenderMap = Maps. < Class <? extends Entity > , Render <? extends Entity >> newHashMap();
    private Map<String, RenderPlayer> skinMap = Maps.<String, RenderPlayer>newHashMap();
    private RenderPlayer playerRenderer;

    /** Renders fonts */
    private FontRenderer textRenderer;
    private double renderPosX;
    private double renderPosY;
    private double renderPosZ;
    public TextureManager renderEngine;

    /** Reference to the World object. */
    public World worldObj;

    /** Rendermanager's variable for the player */
    public Entity livingPlayer;
    public Entity pointedEntity;
    public float playerViewY;
    public float playerViewX;

    /** Reference to the GameSettings object. */
    public GameSettings options;
    public double viewerPosX;
    public double viewerPosY;
    public double viewerPosZ;
    private boolean renderOutlines = false;
    private boolean renderShadow = true;

    /** whether bounding box should be rendered or not */
    private boolean debugBoundingBox = false;

    public RenderManager(TextureManager renderEngineIn, RenderItem itemRendererIn)
    {
        this.renderEngine = renderEngineIn;
        this.entityRenderMap.put(EntityCaveSpider.class, new RenderCaveSpider(this));
        this.entityRenderMap.put(EntitySpider.class, new RenderSpider(this));
        this.entityRenderMap.put(EntityPig.class, new RenderPig(this, new ModelPig(), 0.7F));
        this.entityRenderMap.put(EntitySheep.class, new RenderSheep(this, new ModelSheep2(), 0.7F));
        this.entityRenderMap.put(EntityCow.class, new RenderCow(this, new ModelCow(), 0.7F));
        this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(this, new ModelCow(), 0.7F));
        this.entityRenderMap.put(EntityWolf.class, new RenderWolf(this, new ModelWolf(), 0.5F));
        this.entityRenderMap.put(EntityChicken.class, new RenderChicken(this, new ModelChicken(), 0.3F));
        this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(this, new ModelOcelot(), 0.4F));
        this.entityRenderMap.put(EntityRabbit.class, new RenderRabbit(this, new ModelRabbit(), 0.3F));
        this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish(this));
        this.entityRenderMap.put(EntityEndermite.class, new RenderEndermite(this));
        this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper(this));
        this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman(this));
        this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan(this));
        this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton(this));
        this.entityRenderMap.put(EntityWitch.class, new RenderWitch(this));
        this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze(this));
        this.entityRenderMap.put(EntityPigZombie.class, new RenderPigZombie(this));
        this.entityRenderMap.put(EntityZombie.class, new RenderZombie(this));
        this.entityRenderMap.put(EntitySlime.class, new RenderSlime(this, new ModelSlime(16), 0.25F));
        this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube(this));
        this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(this, new ModelZombie(), 0.5F, 6.0F));
        this.entityRenderMap.put(EntityGhast.class, new RenderGhast(this));
        this.entityRenderMap.put(EntitySquid.class, new RenderSquid(this, new ModelSquid(), 0.7F));
        this.entityRenderMap.put(EntityVillager.class, new RenderVillager(this));
        this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem(this));
        this.entityRenderMap.put(EntityBat.class, new RenderBat(this));
        this.entityRenderMap.put(EntityGuardian.class, new RenderGuardian(this));
        this.entityRenderMap.put(EntityDragon.class, new RenderDragon(this));
        this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal(this));
        this.entityRenderMap.put(EntityWither.class, new RenderWither(this));
        this.entityRenderMap.put(Entity.class, new RenderEntity(this));
        this.entityRenderMap.put(EntityPainting.class, new RenderPainting(this));
        this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame(this, itemRendererIn));
        this.entityRenderMap.put(EntityLeashKnot.class, new RenderLeashKnot(this));
        this.entityRenderMap.put(EntityArrow.class, new RenderArrow(this));
        this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(this, Items.snowball, itemRendererIn));
        this.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(this, Items.ender_pearl, itemRendererIn));
        this.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(this, Items.ender_eye, itemRendererIn));
        this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(this, Items.egg, itemRendererIn));
        this.entityRenderMap.put(EntityPotion.class, new RenderPotion(this, itemRendererIn));
        this.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(this, Items.experience_bottle, itemRendererIn));
        this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(this, Items.fireworks, itemRendererIn));
        this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(this, 2.0F));
        this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(this, 0.5F));
        this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull(this));
        this.entityRenderMap.put(EntityItem.class, new RenderEntityItem(this, itemRendererIn));
        this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb(this));
        this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed(this));
        this.entityRenderMap.put(EntityFallingBlock.class, new RenderFallingBlock(this));
        this.entityRenderMap.put(EntityArmorStand.class, new ArmorStandRenderer(this));
        this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart(this));
        this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(this));
        this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart(this));
        this.entityRenderMap.put(EntityBoat.class, new RenderBoat(this));
        this.entityRenderMap.put(EntityFishHook.class, new RenderFish(this));
        this.entityRenderMap.put(EntityHorse.class, new RenderHorse(this, new ModelHorse(), 0.75F));
        this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt(this));
        this.playerRenderer = new RenderPlayer(this);
        this.skinMap.put("default", this.playerRenderer);
        this.skinMap.put("slim", new RenderPlayer(this, true));
    }

    public void setRenderPosition(double renderPosXIn, double renderPosYIn, double renderPosZIn)
    {
        this.renderPosX = renderPosXIn;
        this.renderPosY = renderPosYIn;
        this.renderPosZ = renderPosZIn;
    }

    public <T extends Entity> Render<T> getEntityClassRenderObject(Class <? extends Entity > p_78715_1_)
    {
        Render <? extends Entity > render = (Render)this.entityRenderMap.get(p_78715_1_);

        if (render == null && p_78715_1_ != Entity.class)
        {
            render = this.<Entity>getEntityClassRenderObject((Class <? extends Entity >)p_78715_1_.getSuperclass());
            this.entityRenderMap.put(p_78715_1_, render);
        }

        return (Render<T>)render;
    }

    public <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn)
    {
        if (entityIn instanceof AbstractClientPlayer)
        {
            String s = ((AbstractClientPlayer)entityIn).getSkinType();
            RenderPlayer renderplayer = (RenderPlayer)this.skinMap.get(s);
            return (Render<T>)(renderplayer != null ? renderplayer : this.playerRenderer);
        }
        else
        {
            return this.<T>getEntityClassRenderObject(entityIn.getClass());
        }
    }

    public void cacheActiveRenderInfo(World worldIn, FontRenderer textRendererIn, Entity livingPlayerIn, Entity pointedEntityIn, GameSettings optionsIn, float partialTicks)
    {
        this.worldObj = worldIn;
        this.options = optionsIn;
        this.livingPlayer = livingPlayerIn;
        this.pointedEntity = pointedEntityIn;
        this.textRenderer = textRendererIn;

        if (livingPlayerIn instanceof EntityLivingBase && ((EntityLivingBase)livingPlayerIn).isPlayerSleeping())
        {
            IBlockState iblockstate = worldIn.getBlockState(new BlockPos(livingPlayerIn));
            Block block = iblockstate.getBlock();

            if (block == Blocks.bed)
            {
                int i = ((EnumFacing)iblockstate.getValue(BlockBed.FACING)).getHorizontalIndex();
                this.playerViewY = (float)(i * 90 + 180);
                this.playerViewX = 0.0F;
            }
        }
        else
        {
            this.playerViewY = livingPlayerIn.prevRotationYaw + (livingPlayerIn.rotationYaw - livingPlayerIn.prevRotationYaw) * partialTicks;
            this.playerViewX = livingPlayerIn.prevRotationPitch + (livingPlayerIn.rotationPitch - livingPlayerIn.prevRotationPitch) * partialTicks;
        }

        if (optionsIn.thirdPersonView == 2)
        {
            this.playerViewY += 180.0F;
        }

        this.viewerPosX = livingPlayerIn.lastTickPosX + (livingPlayerIn.posX - livingPlayerIn.lastTickPosX) * (double)partialTicks;
        this.viewerPosY = livingPlayerIn.lastTickPosY + (livingPlayerIn.posY - livingPlayerIn.lastTickPosY) * (double)partialTicks;
        this.viewerPosZ = livingPlayerIn.lastTickPosZ + (livingPlayerIn.posZ - livingPlayerIn.lastTickPosZ) * (double)partialTicks;
    }

    public void setPlayerViewY(float playerViewYIn)
    {
        this.playerViewY = playerViewYIn;
    }

    public boolean isRenderShadow()
    {
        return this.renderShadow;
    }

    public void setRenderShadow(boolean renderShadowIn)
    {
        this.renderShadow = renderShadowIn;
    }

    public void setDebugBoundingBox(boolean debugBoundingBoxIn)
    {
        this.debugBoundingBox = debugBoundingBoxIn;
    }

    public boolean isDebugBoundingBox()
    {
        return this.debugBoundingBox;
    }

    public boolean renderEntitySimple(Entity entityIn, float partialTicks)
    {
        return this.renderEntityStatic(entityIn, partialTicks, false);
    }

    public boolean shouldRender(Entity entityIn, ICamera camera, double camX, double camY, double camZ)
    {
        Render<Entity> render = this.<Entity>getEntityRenderObject(entityIn);
        return render != null && render.shouldRender(entityIn, camera, camX, camY, camZ);
    }

    public boolean renderEntityStatic(Entity entity, float partialTicks, boolean p_147936_3_)
    {
        if (entity.ticksExisted == 0)
        {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
        }

        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        float f = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        int i = entity.getBrightnessForRender(partialTicks);

        if (entity.isBurning())
        {
            i = 15728880;
        }

        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        return this.doRenderEntity(entity, d0 - this.renderPosX, d1 - this.renderPosY, d2 - this.renderPosZ, f, partialTicks, p_147936_3_);
    }

    public void renderWitherSkull(Entity entityIn, float partialTicks)
    {
        double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        Render<Entity> render = this.<Entity>getEntityRenderObject(entityIn);

        if (render != null && this.renderEngine != null)
        {
            int i = entityIn.getBrightnessForRender(partialTicks);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            render.renderName(entityIn, d0 - this.renderPosX, d1 - this.renderPosY, d2 - this.renderPosZ);
        }
    }

    public boolean renderEntityWithPosYaw(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks)
    {
        return this.doRenderEntity(entityIn, x, y, z, entityYaw, partialTicks, false);
    }

    public boolean doRenderEntity(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean p_147939_10_)
    {
        Render<Entity> render = null;

        try
        {
            render = this.<Entity>getEntityRenderObject(entity);

            if (render != null && this.renderEngine != null)
            {
                try
                {
                    if (render instanceof RendererLivingEntity)
                    {
                        ((RendererLivingEntity)render).setRenderOutlines(this.renderOutlines);
                    }

                    render.doRender(entity, x, y, z, entityYaw, partialTicks);
                }
                catch (Throwable throwable2)
                {
                    throw new ReportedException(CrashReport.makeCrashReport(throwable2, "Rendering entity in world"));
                }

                try
                {
                    if (!this.renderOutlines)
                    {
                        render.doRenderShadowAndFire(entity, x, y, z, entityYaw, partialTicks);
                    }
                }
                catch (Throwable throwable1)
                {
                    throw new ReportedException(CrashReport.makeCrashReport(throwable1, "Post-rendering entity in world"));
                }

                if (this.debugBoundingBox && !entity.isInvisible() && !p_147939_10_)
                {
                    try
                    {
                        this.renderDebugBoundingBox(entity, x, y, z, entityYaw, partialTicks);
                    }
                    catch (Throwable throwable)
                    {
                        throw new ReportedException(CrashReport.makeCrashReport(throwable, "Rendering entity hitbox in world"));
                    }
                }
            }
            else if (this.renderEngine != null)
            {
                return false;
            }

            return true;
        }
        catch (Throwable throwable3)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
            entity.addEntityCrashInfo(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
            crashreportcategory1.addCrashSection("Assigned renderer", render);
            crashreportcategory1.addCrashSection("Location", CrashReportCategory.getCoordinateInfo(x, y, z));
            crashreportcategory1.addCrashSection("Rotation", Float.valueOf(entityYaw));
            crashreportcategory1.addCrashSection("Delta", Float.valueOf(partialTicks));
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Renders the bounding box around an entity when F3+B is pressed
     */
    private void renderDebugBoundingBox(Entity entityIn, double p_85094_2_, double p_85094_4_, double p_85094_6_, float p_85094_8_, float p_85094_9_)
    {
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        float f = entityIn.width / 2.0F;
        AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - entityIn.posX + p_85094_2_, axisalignedbb.minY - entityIn.posY + p_85094_4_, axisalignedbb.minZ - entityIn.posZ + p_85094_6_, axisalignedbb.maxX - entityIn.posX + p_85094_2_, axisalignedbb.maxY - entityIn.posY + p_85094_4_, axisalignedbb.maxZ - entityIn.posZ + p_85094_6_);
        RenderGlobal.func_181563_a(axisalignedbb1, 255, 255, 255, 255);

        if (entityIn instanceof EntityLivingBase)
        {
            float f1 = 0.01F;
            RenderGlobal.func_181563_a(new AxisAlignedBB(p_85094_2_ - (double)f, p_85094_4_ + (double)entityIn.getEyeHeight() - 0.009999999776482582D, p_85094_6_ - (double)f, p_85094_2_ + (double)f, p_85094_4_ + (double)entityIn.getEyeHeight() + 0.009999999776482582D, p_85094_6_ + (double)f), 255, 0, 0, 255);
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        Vec3 vec3 = entityIn.getLook(p_85094_9_);
        worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(p_85094_2_, p_85094_4_ + (double)entityIn.getEyeHeight(), p_85094_6_).color(0, 0, 255, 255).endVertex();
        worldrenderer.pos(p_85094_2_ + vec3.xCoord * 2.0D, p_85094_4_ + (double)entityIn.getEyeHeight() + vec3.yCoord * 2.0D, p_85094_6_ + vec3.zCoord * 2.0D).color(0, 0, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    /**
     * World sets this RenderManager's worldObj to the world provided
     */
    public void set(World worldIn)
    {
        this.worldObj = worldIn;
    }

    public double getDistanceToCamera(double p_78714_1_, double p_78714_3_, double p_78714_5_)
    {
        double d0 = p_78714_1_ - this.viewerPosX;
        double d1 = p_78714_3_ - this.viewerPosY;
        double d2 = p_78714_5_ - this.viewerPosZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Returns the font renderer
     */
    public FontRenderer getFontRenderer()
    {
        return this.textRenderer;
    }

    public void setRenderOutlines(boolean renderOutlinesIn)
    {
        this.renderOutlines = renderOutlinesIn;
    }
}
