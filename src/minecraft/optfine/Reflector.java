package optfine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public class Reflector {
	public static ReflectorClass ModLoader = new ReflectorClass("ModLoader");
	public static ReflectorMethod ModLoader_renderWorldBlock = new ReflectorMethod(ModLoader, "renderWorldBlock");
	public static ReflectorMethod ModLoader_renderInvBlock = new ReflectorMethod(ModLoader, "renderInvBlock");
	public static ReflectorMethod ModLoader_renderBlockIsItemFull3D = new ReflectorMethod(ModLoader, "renderBlockIsItemFull3D");
	public static ReflectorMethod ModLoader_registerServer = new ReflectorMethod(ModLoader, "registerServer");
	public static ReflectorMethod ModLoader_getCustomAnimationLogic = new ReflectorMethod(ModLoader, "getCustomAnimationLogic");
	public static ReflectorClass FMLRenderAccessLibrary = new ReflectorClass("net.minecraft.src.FMLRenderAccessLibrary");
	public static ReflectorMethod FMLRenderAccessLibrary_renderWorldBlock = new ReflectorMethod(FMLRenderAccessLibrary, "renderWorldBlock");
	public static ReflectorMethod FMLRenderAccessLibrary_renderInventoryBlock = new ReflectorMethod(FMLRenderAccessLibrary, "renderInventoryBlock");
	public static ReflectorMethod FMLRenderAccessLibrary_renderItemAsFull3DBlock = new ReflectorMethod(FMLRenderAccessLibrary, "renderItemAsFull3DBlock");
	public static ReflectorClass LightCache = new ReflectorClass("LightCache");
	public static ReflectorField LightCache_cache = new ReflectorField(LightCache, "cache");
	public static ReflectorMethod LightCache_clear = new ReflectorMethod(LightCache, "clear");
	public static ReflectorClass BlockCoord = new ReflectorClass("BlockCoord");
	public static ReflectorMethod BlockCoord_resetPool = new ReflectorMethod(BlockCoord, "resetPool");
	public static ReflectorClass MinecraftForge = new ReflectorClass("net.minecraftforge.common.MinecraftForge");
	public static ReflectorField MinecraftForge_EVENT_BUS = new ReflectorField(MinecraftForge, "EVENT_BUS");
	public static ReflectorClass ForgeHooks = new ReflectorClass("net.minecraftforge.common.ForgeHooks");
	public static ReflectorMethod ForgeHooks_onLivingSetAttackTarget = new ReflectorMethod(ForgeHooks, "onLivingSetAttackTarget");
	public static ReflectorMethod ForgeHooks_onLivingUpdate = new ReflectorMethod(ForgeHooks, "onLivingUpdate");
	public static ReflectorMethod ForgeHooks_onLivingAttack = new ReflectorMethod(ForgeHooks, "onLivingAttack");
	public static ReflectorMethod ForgeHooks_onLivingHurt = new ReflectorMethod(ForgeHooks, "onLivingHurt");
	public static ReflectorMethod ForgeHooks_onLivingDeath = new ReflectorMethod(ForgeHooks, "onLivingDeath");
	public static ReflectorMethod ForgeHooks_onLivingDrops = new ReflectorMethod(ForgeHooks, "onLivingDrops");
	public static ReflectorMethod ForgeHooks_onLivingFall = new ReflectorMethod(ForgeHooks, "onLivingFall");
	public static ReflectorMethod ForgeHooks_onLivingJump = new ReflectorMethod(ForgeHooks, "onLivingJump");
	public static ReflectorClass MinecraftForgeClient = new ReflectorClass("net.minecraftforge.client.MinecraftForgeClient");
	public static ReflectorMethod MinecraftForgeClient_getRenderPass = new ReflectorMethod(MinecraftForgeClient, "getRenderPass");
	public static ReflectorMethod MinecraftForgeClient_getItemRenderer = new ReflectorMethod(MinecraftForgeClient, "getItemRenderer");
	public static ReflectorClass ForgeHooksClient = new ReflectorClass("net.minecraftforge.client.ForgeHooksClient");
	public static ReflectorMethod ForgeHooksClient_onDrawBlockHighlight = new ReflectorMethod(ForgeHooksClient, "onDrawBlockHighlight");
	public static ReflectorMethod ForgeHooksClient_orientBedCamera = new ReflectorMethod(ForgeHooksClient, "orientBedCamera");
	public static ReflectorMethod ForgeHooksClient_dispatchRenderLast = new ReflectorMethod(ForgeHooksClient, "dispatchRenderLast");
	public static ReflectorMethod ForgeHooksClient_setRenderPass = new ReflectorMethod(ForgeHooksClient, "setRenderPass");
	public static ReflectorMethod ForgeHooksClient_onTextureStitchedPre = new ReflectorMethod(ForgeHooksClient, "onTextureStitchedPre");
	public static ReflectorMethod ForgeHooksClient_onTextureStitchedPost = new ReflectorMethod(ForgeHooksClient, "onTextureStitchedPost");
	public static ReflectorMethod ForgeHooksClient_renderFirstPersonHand = new ReflectorMethod(ForgeHooksClient, "renderFirstPersonHand");
	public static ReflectorMethod ForgeHooksClient_getOffsetFOV = new ReflectorMethod(ForgeHooksClient, "getOffsetFOV");
	public static ReflectorMethod ForgeHooksClient_drawScreen = new ReflectorMethod(ForgeHooksClient, "drawScreen");
	public static ReflectorMethod ForgeHooksClient_onFogRender = new ReflectorMethod(ForgeHooksClient, "onFogRender");
	public static ReflectorMethod ForgeHooksClient_setRenderLayer = new ReflectorMethod(ForgeHooksClient, "setRenderLayer");
	public static ReflectorClass FMLCommonHandler = new ReflectorClass("net.minecraftforge.fml.common.FMLCommonHandler");
	public static ReflectorMethod FMLCommonHandler_instance = new ReflectorMethod(FMLCommonHandler, "instance");
	public static ReflectorMethod FMLCommonHandler_handleServerStarting = new ReflectorMethod(FMLCommonHandler, "handleServerStarting");
	public static ReflectorMethod FMLCommonHandler_handleServerAboutToStart = new ReflectorMethod(FMLCommonHandler, "handleServerAboutToStart");
	public static ReflectorMethod FMLCommonHandler_enhanceCrashReport = new ReflectorMethod(FMLCommonHandler, "enhanceCrashReport");
	public static ReflectorMethod FMLCommonHandler_getBrandings = new ReflectorMethod(FMLCommonHandler, "getBrandings");
	public static ReflectorClass FMLClientHandler = new ReflectorClass("net.minecraftforge.fml.client.FMLClientHandler");
	public static ReflectorMethod FMLClientHandler_instance = new ReflectorMethod(FMLClientHandler, "instance");
	public static ReflectorMethod FMLClientHandler_isLoading = new ReflectorMethod(FMLClientHandler, "isLoading");
	public static ReflectorClass ItemRenderType = new ReflectorClass("net.minecraftforge.client.IItemRenderer$ItemRenderType");
	public static ReflectorField ItemRenderType_EQUIPPED = new ReflectorField(ItemRenderType, "EQUIPPED");
	public static ReflectorClass ForgeWorldProvider = new ReflectorClass(WorldProvider.class);
	public static ReflectorMethod ForgeWorldProvider_getSkyRenderer = new ReflectorMethod(ForgeWorldProvider, "getSkyRenderer");
	public static ReflectorMethod ForgeWorldProvider_getCloudRenderer = new ReflectorMethod(ForgeWorldProvider, "getCloudRenderer");
	public static ReflectorMethod ForgeWorldProvider_getWeatherRenderer = new ReflectorMethod(ForgeWorldProvider, "getWeatherRenderer");
	public static ReflectorClass ForgeWorld = new ReflectorClass(World.class);
	public static ReflectorMethod ForgeWorld_countEntities = new ReflectorMethod(ForgeWorld, "countEntities", new Class[] { EnumCreatureType.class, Boolean.TYPE });
	public static ReflectorMethod ForgeWorld_getPerWorldStorage = new ReflectorMethod(ForgeWorld, "getPerWorldStorage");
	public static ReflectorClass IRenderHandler = new ReflectorClass("net.minecraftforge.client.IRenderHandler");
	public static ReflectorMethod IRenderHandler_render = new ReflectorMethod(IRenderHandler, "render");
	public static ReflectorClass DimensionManager = new ReflectorClass("net.minecraftforge.common.DimensionManager");
	public static ReflectorMethod DimensionManager_getStaticDimensionIDs = new ReflectorMethod(DimensionManager, "getStaticDimensionIDs");
	public static ReflectorClass WorldEvent_Load = new ReflectorClass("net.minecraftforge.event.world.WorldEvent$Load");
	public static ReflectorConstructor WorldEvent_Load_Constructor = new ReflectorConstructor(WorldEvent_Load, new Class[] { World.class });
	public static ReflectorClass DrawScreenEvent_Pre = new ReflectorClass("net.minecraftforge.client.event.GuiScreenEvent$DrawScreenEvent$Pre");
	public static ReflectorConstructor DrawScreenEvent_Pre_Constructor = new ReflectorConstructor(DrawScreenEvent_Pre, new Class[] { GuiScreen.class, Integer.TYPE, Integer.TYPE, Float.TYPE });
	public static ReflectorClass DrawScreenEvent_Post = new ReflectorClass("net.minecraftforge.client.event.GuiScreenEvent$DrawScreenEvent$Post");
	public static ReflectorConstructor DrawScreenEvent_Post_Constructor = new ReflectorConstructor(DrawScreenEvent_Post, new Class[] { GuiScreen.class, Integer.TYPE, Integer.TYPE, Float.TYPE });
	public static ReflectorClass EntityViewRenderEvent_FogColors = new ReflectorClass("net.minecraftforge.client.event.EntityViewRenderEvent$FogColors");
	public static ReflectorConstructor EntityViewRenderEvent_FogColors_Constructor = new ReflectorConstructor(EntityViewRenderEvent_FogColors, new Class[] { EntityRenderer.class, Entity.class, Block.class, Double.TYPE, Float.TYPE, Float.TYPE, Float.TYPE });
	public static ReflectorField EntityViewRenderEvent_FogColors_red = new ReflectorField(EntityViewRenderEvent_FogColors, "red");
	public static ReflectorField EntityViewRenderEvent_FogColors_green = new ReflectorField(EntityViewRenderEvent_FogColors, "green");
	public static ReflectorField EntityViewRenderEvent_FogColors_blue = new ReflectorField(EntityViewRenderEvent_FogColors, "blue");
	public static ReflectorClass EntityViewRenderEvent_FogDensity = new ReflectorClass("net.minecraftforge.client.event.EntityViewRenderEvent$FogDensity");
	public static ReflectorConstructor EntityViewRenderEvent_FogDensity_Constructor = new ReflectorConstructor(EntityViewRenderEvent_FogDensity, new Class[] { EntityRenderer.class, Entity.class, Block.class, Double.TYPE, Float.TYPE });
	public static ReflectorField EntityViewRenderEvent_FogDensity_density = new ReflectorField(EntityViewRenderEvent_FogDensity, "density");
	public static ReflectorClass EntityViewRenderEvent_RenderFogEvent = new ReflectorClass("net.minecraftforge.client.event.EntityViewRenderEvent$RenderFogEvent");
	public static ReflectorConstructor EntityViewRenderEvent_RenderFogEvent_Constructor = new ReflectorConstructor(EntityViewRenderEvent_RenderFogEvent, new Class[] { EntityRenderer.class, Entity.class, Block.class, Double.TYPE, Integer.TYPE, Float.TYPE });
	public static ReflectorClass EventBus = new ReflectorClass("net.minecraftforge.fml.common.eventhandler.EventBus");
	public static ReflectorMethod EventBus_post = new ReflectorMethod(EventBus, "post");
	public static ReflectorClass Event_Result = new ReflectorClass("net.minecraftforge.fml.common.eventhandler.Event$Result");
	public static ReflectorField Event_Result_DENY = new ReflectorField(Event_Result, "DENY");
	public static ReflectorField Event_Result_ALLOW = new ReflectorField(Event_Result, "ALLOW");
	public static ReflectorField Event_Result_DEFAULT = new ReflectorField(Event_Result, "DEFAULT");
	public static ReflectorClass ForgeEventFactory = new ReflectorClass("net.minecraftforge.event.ForgeEventFactory");
	public static ReflectorMethod ForgeEventFactory_canEntitySpawn = new ReflectorMethod(ForgeEventFactory, "canEntitySpawn");
	public static ReflectorMethod ForgeEventFactory_canEntityDespawn = new ReflectorMethod(ForgeEventFactory, "canEntityDespawn");
	public static ReflectorClass ChunkWatchEvent_UnWatch = new ReflectorClass("net.minecraftforge.event.world.ChunkWatchEvent$UnWatch");
	public static ReflectorConstructor ChunkWatchEvent_UnWatch_Constructor = new ReflectorConstructor(ChunkWatchEvent_UnWatch, new Class[] { ChunkCoordIntPair.class, EntityPlayerMP.class });
	public static ReflectorClass ForgeBlock = new ReflectorClass(Block.class);
	public static ReflectorMethod ForgeBlock_getBedDirection = new ReflectorMethod(ForgeBlock, "getBedDirection");
	public static ReflectorMethod ForgeBlock_isBedFoot = new ReflectorMethod(ForgeBlock, "isBedFoot");
	public static ReflectorMethod ForgeBlock_hasTileEntity = new ReflectorMethod(ForgeBlock, "hasTileEntity", new Class[] { IBlockState.class });
	public static ReflectorMethod ForgeBlock_canCreatureSpawn = new ReflectorMethod(ForgeBlock, "canCreatureSpawn");
	public static ReflectorMethod ForgeBlock_addHitEffects = new ReflectorMethod(ForgeBlock, "addHitEffects");
	public static ReflectorMethod ForgeBlock_addDestroyEffects = new ReflectorMethod(ForgeBlock, "addDestroyEffects");
	public static ReflectorMethod ForgeBlock_isAir = new ReflectorMethod(ForgeBlock, "isAir");
	public static ReflectorMethod ForgeBlock_canRenderInLayer = new ReflectorMethod(ForgeBlock, "canRenderInLayer");
	public static ReflectorClass ForgeEntity = new ReflectorClass(Entity.class);
	public static ReflectorField ForgeEntity_captureDrops = new ReflectorField(ForgeEntity, "captureDrops");
	public static ReflectorField ForgeEntity_capturedDrops = new ReflectorField(ForgeEntity, "capturedDrops");
	public static ReflectorMethod ForgeEntity_shouldRenderInPass = new ReflectorMethod(ForgeEntity, "shouldRenderInPass");
	public static ReflectorMethod ForgeEntity_canRiderInteract = new ReflectorMethod(ForgeEntity, "canRiderInteract");
	public static ReflectorClass ForgeTileEntity = new ReflectorClass(TileEntity.class);
	public static ReflectorMethod ForgeTileEntity_shouldRenderInPass = new ReflectorMethod(ForgeTileEntity, "shouldRenderInPass");
	public static ReflectorMethod ForgeTileEntity_getRenderBoundingBox = new ReflectorMethod(ForgeTileEntity, "getRenderBoundingBox");
	public static ReflectorMethod ForgeTileEntity_canRenderBreaking = new ReflectorMethod(ForgeTileEntity, "canRenderBreaking");
	public static ReflectorClass ForgeItem = new ReflectorClass(Item.class);
	public static ReflectorMethod ForgeItem_onEntitySwing = new ReflectorMethod(ForgeItem, "onEntitySwing");
	public static ReflectorClass ForgePotionEffect = new ReflectorClass(PotionEffect.class);
	public static ReflectorMethod ForgePotionEffect_isCurativeItem = new ReflectorMethod(ForgePotionEffect, "isCurativeItem");
	public static ReflectorClass ForgeItemRecord = new ReflectorClass(ItemRecord.class);
	public static ReflectorMethod ForgeItemRecord_getRecordResource = new ReflectorMethod(ForgeItemRecord, "getRecordResource", new Class[] { String.class });
	public static ReflectorClass ForgeVertexFormatElementEnumUseage = new ReflectorClass(VertexFormatElement.EnumUsage.class);
	public static ReflectorMethod ForgeVertexFormatElementEnumUseage_preDraw = new ReflectorMethod(ForgeVertexFormatElementEnumUseage, "preDraw");
	public static ReflectorMethod ForgeVertexFormatElementEnumUseage_postDraw = new ReflectorMethod(ForgeVertexFormatElementEnumUseage, "postDraw");

	public static void callVoid(ReflectorMethod p_callVoid_0_, Object... p_callVoid_1_) {
		try {
			Method method = p_callVoid_0_.getTargetMethod();

			if (method == null) {
				return;
			}

			method.invoke((Object) null, p_callVoid_1_);
		} catch (Throwable throwable) {
			handleException(throwable, (Object) null, p_callVoid_0_, p_callVoid_1_);
		}
	}

	public static boolean callBoolean(ReflectorMethod p_callBoolean_0_, Object... p_callBoolean_1_) {
		try {
			Method method = p_callBoolean_0_.getTargetMethod();

			if (method == null) {
				return false;
			} else {
				Boolean obool = (Boolean) method.invoke((Object) null, p_callBoolean_1_);
				return obool.booleanValue();
			}
		} catch (Throwable throwable) {
			handleException(throwable, (Object) null, p_callBoolean_0_, p_callBoolean_1_);
			return false;
		}
	}

	public static int callInt(ReflectorMethod p_callInt_0_, Object... p_callInt_1_) {
		try {
			Method method = p_callInt_0_.getTargetMethod();

			if (method == null) {
				return 0;
			} else {
				Integer integer = (Integer) method.invoke((Object) null, p_callInt_1_);
				return integer.intValue();
			}
		} catch (Throwable throwable) {
			handleException(throwable, (Object) null, p_callInt_0_, p_callInt_1_);
			return 0;
		}
	}

	public static float callFloat(ReflectorMethod p_callFloat_0_, Object... p_callFloat_1_) {
		try {
			Method method = p_callFloat_0_.getTargetMethod();

			if (method == null) {
				return 0.0F;
			} else {
				Float f = (Float) method.invoke((Object) null, p_callFloat_1_);
				return f.floatValue();
			}
		} catch (Throwable throwable) {
			handleException(throwable, (Object) null, p_callFloat_0_, p_callFloat_1_);
			return 0.0F;
		}
	}

	public static String callString(ReflectorMethod p_callString_0_, Object... p_callString_1_) {
		try {
			Method method = p_callString_0_.getTargetMethod();

			if (method == null) {
				return null;
			} else {
				String s = (String) method.invoke((Object) null, p_callString_1_);
				return s;
			}
		} catch (Throwable throwable) {
			handleException(throwable, (Object) null, p_callString_0_, p_callString_1_);
			return null;
		}
	}

	public static Object call(ReflectorMethod p_call_0_, Object... p_call_1_) {
		try {
			Method method = p_call_0_.getTargetMethod();

			if (method == null) {
				return null;
			} else {
				Object object = method.invoke((Object) null, p_call_1_);
				return object;
			}
		} catch (Throwable throwable) {
			handleException(throwable, (Object) null, p_call_0_, p_call_1_);
			return null;
		}
	}

	public static void callVoid(Object p_callVoid_0_, ReflectorMethod p_callVoid_1_, Object... p_callVoid_2_) {
		try {
			if (p_callVoid_0_ == null) {
				return;
			}

			Method method = p_callVoid_1_.getTargetMethod();

			if (method == null) {
				return;
			}

			method.invoke(p_callVoid_0_, p_callVoid_2_);
		} catch (Throwable throwable) {
			handleException(throwable, p_callVoid_0_, p_callVoid_1_, p_callVoid_2_);
		}
	}

	public static boolean callBoolean(Object p_callBoolean_0_, ReflectorMethod p_callBoolean_1_, Object... p_callBoolean_2_) {
		try {
			Method method = p_callBoolean_1_.getTargetMethod();

			if (method == null) {
				return false;
			} else {
				Boolean obool = (Boolean) method.invoke(p_callBoolean_0_, p_callBoolean_2_);
				return obool.booleanValue();
			}
		} catch (Throwable throwable) {
			handleException(throwable, p_callBoolean_0_, p_callBoolean_1_, p_callBoolean_2_);
			return false;
		}
	}

	public static int callInt(Object p_callInt_0_, ReflectorMethod p_callInt_1_, Object... p_callInt_2_) {
		try {
			Method method = p_callInt_1_.getTargetMethod();

			if (method == null) {
				return 0;
			} else {
				Integer integer = (Integer) method.invoke(p_callInt_0_, p_callInt_2_);
				return integer.intValue();
			}
		} catch (Throwable throwable) {
			handleException(throwable, p_callInt_0_, p_callInt_1_, p_callInt_2_);
			return 0;
		}
	}

	public static float callFloat(Object p_callFloat_0_, ReflectorMethod p_callFloat_1_, Object... p_callFloat_2_) {
		try {
			Method method = p_callFloat_1_.getTargetMethod();

			if (method == null) {
				return 0.0F;
			} else {
				Float f = (Float) method.invoke(p_callFloat_0_, p_callFloat_2_);
				return f.floatValue();
			}
		} catch (Throwable throwable) {
			handleException(throwable, p_callFloat_0_, p_callFloat_1_, p_callFloat_2_);
			return 0.0F;
		}
	}

	public static String callString(Object p_callString_0_, ReflectorMethod p_callString_1_, Object... p_callString_2_) {
		try {
			Method method = p_callString_1_.getTargetMethod();

			if (method == null) {
				return null;
			} else {
				String s = (String) method.invoke(p_callString_0_, p_callString_2_);
				return s;
			}
		} catch (Throwable throwable) {
			handleException(throwable, p_callString_0_, p_callString_1_, p_callString_2_);
			return null;
		}
	}

	public static Object call(Object p_call_0_, ReflectorMethod p_call_1_, Object... p_call_2_) {
		try {
			Method method = p_call_1_.getTargetMethod();

			if (method == null) {
				return null;
			} else {
				Object object = method.invoke(p_call_0_, p_call_2_);
				return object;
			}
		} catch (Throwable throwable) {
			handleException(throwable, p_call_0_, p_call_1_, p_call_2_);
			return null;
		}
	}

	public static Object getFieldValue(ReflectorField p_getFieldValue_0_) {
		return getFieldValue((Object) null, p_getFieldValue_0_);
	}

	public static Object getFieldValue(Object p_getFieldValue_0_, ReflectorField p_getFieldValue_1_) {
		try {
			Field field = p_getFieldValue_1_.getTargetField();

			if (field == null) {
				return null;
			} else {
				Object object = field.get(p_getFieldValue_0_);
				return object;
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return null;
		}
	}

	public static float getFieldValueFloat(Object p_getFieldValueFloat_0_, ReflectorField p_getFieldValueFloat_1_, float p_getFieldValueFloat_2_) {
		Object object = getFieldValue(p_getFieldValueFloat_0_, p_getFieldValueFloat_1_);

		if (!(object instanceof Float)) {
			return p_getFieldValueFloat_2_;
		} else {
			Float f = (Float) object;
			return f.floatValue();
		}
	}

	public static void setFieldValue(ReflectorField p_setFieldValue_0_, Object p_setFieldValue_1_) {
		setFieldValue((Object) null, p_setFieldValue_0_, p_setFieldValue_1_);
	}

	public static void setFieldValue(Object p_setFieldValue_0_, ReflectorField p_setFieldValue_1_, Object p_setFieldValue_2_) {
		try {
			Field field = p_setFieldValue_1_.getTargetField();

			if (field == null) {
				return;
			}

			field.set(p_setFieldValue_0_, p_setFieldValue_2_);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	public static boolean postForgeBusEvent(ReflectorConstructor p_postForgeBusEvent_0_, Object... p_postForgeBusEvent_1_) {
		Object object = newInstance(p_postForgeBusEvent_0_, p_postForgeBusEvent_1_);
		return object == null ? false : postForgeBusEvent(object);
	}

	public static boolean postForgeBusEvent(Object p_postForgeBusEvent_0_) {
		if (p_postForgeBusEvent_0_ == null) {
			return false;
		} else {
			Object object = getFieldValue(MinecraftForge_EVENT_BUS);

			if (object == null) {
				return false;
			} else {
				Object object1 = call(object, EventBus_post, new Object[] { p_postForgeBusEvent_0_ });

				if (!(object1 instanceof Boolean)) {
					return false;
				} else {
					Boolean obool = (Boolean) object1;
					return obool.booleanValue();
				}
			}
		}
	}

	public static Object newInstance(ReflectorConstructor p_newInstance_0_, Object... p_newInstance_1_) {
		Constructor constructor = p_newInstance_0_.getTargetConstructor();

		if (constructor == null) {
			return null;
		} else {
			try {
				Object object = constructor.newInstance(p_newInstance_1_);
				return object;
			} catch (Throwable throwable) {
				handleException(throwable, p_newInstance_0_, p_newInstance_1_);
				return null;
			}
		}
	}

	public static boolean matchesTypes(Class[] p_matchesTypes_0_, Class[] p_matchesTypes_1_) {
		if (p_matchesTypes_0_.length != p_matchesTypes_1_.length) {
			return false;
		} else {
			for (int i = 0; i < p_matchesTypes_1_.length; ++i) {
				Class oclass = p_matchesTypes_0_[i];
				Class oclass1 = p_matchesTypes_1_[i];

				if (oclass != oclass1) {
					return false;
				}
			}

			return true;
		}
	}

	private static void dbgCall(boolean p_dbgCall_0_, String p_dbgCall_1_, ReflectorMethod p_dbgCall_2_, Object[] p_dbgCall_3_, Object p_dbgCall_4_) {
		String s = p_dbgCall_2_.getTargetMethod().getDeclaringClass().getName();
		String s1 = p_dbgCall_2_.getTargetMethod().getName();
		String s2 = "";

		if (p_dbgCall_0_) {
			s2 = " static";
		}

		Config.dbg(p_dbgCall_1_ + s2 + " " + s + "." + s1 + "(" + Config.arrayToString(p_dbgCall_3_) + ") => " + p_dbgCall_4_);
	}

	private static void dbgCallVoid(boolean p_dbgCallVoid_0_, String p_dbgCallVoid_1_, ReflectorMethod p_dbgCallVoid_2_, Object[] p_dbgCallVoid_3_) {
		String s = p_dbgCallVoid_2_.getTargetMethod().getDeclaringClass().getName();
		String s1 = p_dbgCallVoid_2_.getTargetMethod().getName();
		String s2 = "";

		if (p_dbgCallVoid_0_) {
			s2 = " static";
		}

		Config.dbg(p_dbgCallVoid_1_ + s2 + " " + s + "." + s1 + "(" + Config.arrayToString(p_dbgCallVoid_3_) + ")");
	}

	private static void dbgFieldValue(boolean p_dbgFieldValue_0_, String p_dbgFieldValue_1_, ReflectorField p_dbgFieldValue_2_, Object p_dbgFieldValue_3_) {
		String s = p_dbgFieldValue_2_.getTargetField().getDeclaringClass().getName();
		String s1 = p_dbgFieldValue_2_.getTargetField().getName();
		String s2 = "";

		if (p_dbgFieldValue_0_) {
			s2 = " static";
		}

		Config.dbg(p_dbgFieldValue_1_ + s2 + " " + s + "." + s1 + " => " + p_dbgFieldValue_3_);
	}

	private static void handleException(Throwable p_handleException_0_, Object p_handleException_1_, ReflectorMethod p_handleException_2_, Object[] p_handleException_3_) {
		if (p_handleException_0_ instanceof InvocationTargetException) {
			p_handleException_0_.printStackTrace();
		} else {
			if (p_handleException_0_ instanceof IllegalArgumentException) {
				Config.warn("*** IllegalArgumentException ***");
				Config.warn("Method: " + p_handleException_2_.getTargetMethod());
				Config.warn("Object: " + p_handleException_1_);
				Config.warn("Parameter classes: " + Config.arrayToString(getClasses(p_handleException_3_)));
				Config.warn("Parameters: " + Config.arrayToString(p_handleException_3_));
			}

			Config.warn("*** Exception outside of method ***");
			Config.warn("Method deactivated: " + p_handleException_2_.getTargetMethod());
			p_handleException_2_.deactivate();
			p_handleException_0_.printStackTrace();
		}
	}

	private static void handleException(Throwable p_handleException_0_, ReflectorConstructor p_handleException_1_, Object[] p_handleException_2_) {
		if (p_handleException_0_ instanceof InvocationTargetException) {
			p_handleException_0_.printStackTrace();
		} else {
			if (p_handleException_0_ instanceof IllegalArgumentException) {
				Config.warn("*** IllegalArgumentException ***");
				Config.warn("Constructor: " + p_handleException_1_.getTargetConstructor());
				Config.warn("Parameter classes: " + Config.arrayToString(getClasses(p_handleException_2_)));
				Config.warn("Parameters: " + Config.arrayToString(p_handleException_2_));
			}

			Config.warn("*** Exception outside of constructor ***");
			Config.warn("Constructor deactivated: " + p_handleException_1_.getTargetConstructor());
			p_handleException_1_.deactivate();
			p_handleException_0_.printStackTrace();
		}
	}

	private static Object[] getClasses(Object[] p_getClasses_0_) {
		if (p_getClasses_0_ == null) {
			return new Class[0];
		} else {
			Class[] aclass = new Class[p_getClasses_0_.length];

			for (int i = 0; i < aclass.length; ++i) {
				Object object = p_getClasses_0_[i];

				if (object != null) {
					aclass[i] = object.getClass();
				}
			}

			return aclass;
		}
	}

	public static Field getField(Class p_getField_0_, Class p_getField_1_) {
		try {
			Field[] afield = p_getField_0_.getDeclaredFields();

			for (int i = 0; i < afield.length; ++i) {
				Field field = afield[i];

				if (field.getType() == p_getField_1_) {
					field.setAccessible(true);
					return field;
				}
			}

			return null;
		} catch (Exception var5) {
			return null;
		}
	}

	public static Field[] getFields(Class p_getFields_0_, Class p_getFields_1_) {
		List list = new ArrayList();

		try {
			Field[] afield = p_getFields_0_.getDeclaredFields();

			for (int i = 0; i < afield.length; ++i) {
				Field field = afield[i];

				if (field.getType() == p_getFields_1_) {
					field.setAccessible(true);
					list.add(field);
				}
			}

			Field[] afield1 = (Field[]) ((Field[]) list.toArray(new Field[list.size()]));
			return afield1;
		} catch (Exception var6) {
			return null;
		}
	}
}
