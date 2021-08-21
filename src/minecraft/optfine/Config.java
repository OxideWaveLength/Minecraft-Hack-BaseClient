package optfine;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

public class Config {

	public static final String OF_NAME = "OptiFine";
	public static final String MC_VERSION = "1.8.8";
	public static final String OF_EDITION = "HD_U";
	public static final String OF_RELEASE = "E1";
	public static final String VERSION = "OptiFine_1.8.8_HD_U_E1";
	private static String newRelease = null;
	private static boolean notify64BitJava = false;
	public static String openGlVersion = null;
	public static String openGlRenderer = null;
	public static String openGlVendor = null;
	public static boolean fancyFogAvailable = false;
	public static boolean occlusionAvailable = false;
	private static GameSettings gameSettings = null;
	private static Minecraft minecraft = null;
	private static boolean initialized = false;
	private static Thread minecraftThread = null;
	private static DisplayMode desktopDisplayMode = null;
	private static int antialiasingLevel = 0;
	private static int availableProcessors = 0;
	public static boolean zoomMode = false;
	private static int texturePackClouds = 0;
	public static boolean waterOpacityChanged = false;
	private static boolean fullscreenModeChecked = false;
	private static boolean desktopModeChecked = false;
	private static PrintStream systemOut = new PrintStream(new FileOutputStream(FileDescriptor.out));
	public static final Boolean DEF_FOG_FANCY = Boolean.valueOf(true);
	public static final Float DEF_FOG_START = Float.valueOf(0.2F);
	public static final Boolean DEF_OPTIMIZE_RENDER_DISTANCE = Boolean.valueOf(false);
	public static final Boolean DEF_OCCLUSION_ENABLED = Boolean.valueOf(false);
	public static final Integer DEF_MIPMAP_LEVEL = Integer.valueOf(0);
	public static final Integer DEF_MIPMAP_TYPE = Integer.valueOf(9984);
	public static final Float DEF_ALPHA_FUNC_LEVEL = Float.valueOf(0.1F);
	public static final Boolean DEF_LOAD_CHUNKS_FAR = Boolean.valueOf(false);
	public static final Integer DEF_PRELOADED_CHUNKS = Integer.valueOf(0);
	public static final Integer DEF_CHUNKS_LIMIT = Integer.valueOf(25);
	public static final Integer DEF_UPDATES_PER_FRAME = Integer.valueOf(3);
	public static final Boolean DEF_DYNAMIC_UPDATES = Boolean.valueOf(false);
	private static long lastActionTime = System.currentTimeMillis();

	public static String getVersion() {
		return "OptiFine_1.8.8_HD_U_E1";
	}

	public static void initGameSettings(GameSettings p_initGameSettings_0_) {
		gameSettings = p_initGameSettings_0_;
		minecraft = Minecraft.getMinecraft();
		desktopDisplayMode = Display.getDesktopDisplayMode();
		updateAvailableProcessors();
	}

	public static void initDisplay() {
		checkInitialized();
		antialiasingLevel = gameSettings.ofAaLevel;
		checkDisplaySettings();
		checkDisplayMode();
		minecraftThread = Thread.currentThread();
		updateThreadPriorities();
	}

	public static void checkInitialized() {
		if (!initialized) {
			if (Display.isCreated()) {
				initialized = true;
				checkOpenGlCaps();
				startVersionCheckThread();
			}
		}
	}

	private static void checkOpenGlCaps() {
		log("");
		log(getVersion());
		log("" + new Date());
		log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
		log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
		log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
		log("LWJGL: " + Sys.getVersion());
		openGlVersion = GL11.glGetString(GL11.GL_VERSION);
		openGlRenderer = GL11.glGetString(GL11.GL_RENDERER);
		openGlVendor = GL11.glGetString(GL11.GL_VENDOR);
		log("OpenGL: " + openGlRenderer + ", version " + openGlVersion + ", " + openGlVendor);
		log("OpenGL Version: " + getOpenGlVersionString());

		if (!GLContext.getCapabilities().OpenGL12) {
			log("OpenGL Mipmap levels: Not available (GL12.GL_TEXTURE_MAX_LEVEL)");
		}

		fancyFogAvailable = GLContext.getCapabilities().GL_NV_fog_distance;

		if (!fancyFogAvailable) {
			log("OpenGL Fancy fog: Not available (GL_NV_fog_distance)");
		}

		occlusionAvailable = GLContext.getCapabilities().GL_ARB_occlusion_query;

		if (!occlusionAvailable) {
			log("OpenGL Occlussion culling: Not available (GL_ARB_occlusion_query)");
		}

		int i = Minecraft.getGLMaximumTextureSize();
		dbg("Maximum texture size: " + i + "x" + i);
	}

	public static boolean isFancyFogAvailable() {
		return fancyFogAvailable;
	}

	public static boolean isOcclusionAvailable() {
		return occlusionAvailable;
	}

	public static String getOpenGlVersionString() {
		int i = getOpenGlVersion();
		String s = "" + i / 10 + "." + i % 10;
		return s;
	}

	private static int getOpenGlVersion() {
		return !GLContext.getCapabilities().OpenGL11 ? 10 : (!GLContext.getCapabilities().OpenGL12 ? 11 : (!GLContext.getCapabilities().OpenGL13 ? 12 : (!GLContext.getCapabilities().OpenGL14 ? 13 : (!GLContext.getCapabilities().OpenGL15 ? 14 : (!GLContext.getCapabilities().OpenGL20 ? 15 : (!GLContext.getCapabilities().OpenGL21 ? 20 : (!GLContext.getCapabilities().OpenGL30 ? 21 : (!GLContext.getCapabilities().OpenGL31 ? 30 : (!GLContext.getCapabilities().OpenGL32 ? 31 : (!GLContext.getCapabilities().OpenGL33 ? 32 : (!GLContext.getCapabilities().OpenGL40 ? 33 : 40)))))))))));
	}

	public static void updateThreadPriorities() {
		updateAvailableProcessors();
		int i = 8;

		if (isSingleProcessor()) {
			if (isSmoothWorld()) {
				minecraftThread.setPriority(10);
				setThreadPriority("Server thread", 1);
			} else {
				minecraftThread.setPriority(5);
				setThreadPriority("Server thread", 5);
			}
		} else {
			minecraftThread.setPriority(10);
			setThreadPriority("Server thread", 5);
		}
	}

	private static void setThreadPriority(String p_setThreadPriority_0_, int p_setThreadPriority_1_) {
		try {
			ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();

			if (threadgroup == null) {
				return;
			}

			int i = (threadgroup.activeCount() + 10) * 2;
			Thread[] athread = new Thread[i];
			threadgroup.enumerate(athread, false);

			for (int j = 0; j < athread.length; ++j) {
				Thread thread = athread[j];

				if (thread != null && thread.getName().startsWith(p_setThreadPriority_0_)) {
					thread.setPriority(p_setThreadPriority_1_);
				}
			}
		} catch (Throwable throwable) {
			dbg(throwable.getClass().getName() + ": " + throwable.getMessage());
		}
	}

	public static boolean isMinecraftThread() {
		return Thread.currentThread() == minecraftThread;
	}

	private static void startVersionCheckThread() {
		VersionCheckThread versioncheckthread = new VersionCheckThread();
		versioncheckthread.start();
	}

	public static int getMipmapType() {
		if (gameSettings == null) {
			return DEF_MIPMAP_TYPE.intValue();
		} else {
			switch (gameSettings.ofMipmapType) {
			case 0:
				return 9986;

			case 1:
				return 9986;

			case 2:
				if (isMultiTexture()) {
					return 9985;
				}

				return 9986;

			case 3:
				if (isMultiTexture()) {
					return 9987;
				}

				return 9986;

			default:
				return 9986;
			}
		}
	}

	public static boolean isUseAlphaFunc() {
		float f = getAlphaFuncLevel();
		return f > DEF_ALPHA_FUNC_LEVEL.floatValue() + 1.0E-5F;
	}

	public static float getAlphaFuncLevel() {
		return DEF_ALPHA_FUNC_LEVEL.floatValue();
	}

	public static boolean isFogFancy() {
		return !isFancyFogAvailable() ? false : gameSettings.ofFogType == 2;
	}

	public static boolean isFogFast() {
		return gameSettings.ofFogType == 1;
	}

	public static boolean isFogOff() {
		return gameSettings.ofFogType == 3;
	}

	public static float getFogStart() {
		return gameSettings.ofFogStart;
	}

	public static void dbg(String p_dbg_0_) {
		systemOut.print("[OptiFine] ");
		systemOut.println(p_dbg_0_);
	}

	public static void warn(String p_warn_0_) {
		systemOut.print("[OptiFine] [WARN] ");
		systemOut.println(p_warn_0_);
	}

	public static void error(String p_error_0_) {
		systemOut.print("[OptiFine] [ERROR] ");
		systemOut.println(p_error_0_);
	}

	public static void log(String p_log_0_) {
		dbg(p_log_0_);
	}

	public static int getUpdatesPerFrame() {
		return gameSettings.ofChunkUpdates;
	}

	public static boolean isDynamicUpdates() {
		return gameSettings.ofChunkUpdatesDynamic;
	}

	public static boolean isRainFancy() {
		return gameSettings.ofRain == 0 ? gameSettings.fancyGraphics : gameSettings.ofRain == 2;
	}

	public static boolean isRainOff() {
		return gameSettings.ofRain == 3;
	}

	public static boolean isCloudsFancy() {
		return gameSettings.ofClouds != 0 ? gameSettings.ofClouds == 2 : (texturePackClouds != 0 ? texturePackClouds == 2 : gameSettings.fancyGraphics);
	}

	public static boolean isCloudsOff() {
		return gameSettings.ofClouds == 3;
	}

	public static void updateTexturePackClouds() {
		texturePackClouds = 0;
		IResourceManager iresourcemanager = getResourceManager();

		if (iresourcemanager != null) {
			try {
				InputStream inputstream = iresourcemanager.getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();

				if (inputstream == null) {
					return;
				}

				Properties properties = new Properties();
				properties.load(inputstream);
				inputstream.close();
				String s = properties.getProperty("clouds");

				if (s == null) {
					return;
				}

				dbg("Texture pack clouds: " + s);
				s = s.toLowerCase();

				if (s.equals("fast")) {
					texturePackClouds = 1;
				}

				if (s.equals("fancy")) {
					texturePackClouds = 2;
				}
			} catch (Exception var4) {
				;
			}
		}
	}

	public static boolean isTreesFancy() {
		return gameSettings.ofTrees == 0 ? gameSettings.fancyGraphics : gameSettings.ofTrees == 2;
	}

	public static boolean isDroppedItemsFancy() {
		return gameSettings.ofDroppedItems == 0 ? gameSettings.fancyGraphics : gameSettings.ofDroppedItems == 2;
	}

	public static int limit(int p_limit_0_, int p_limit_1_, int p_limit_2_) {
		return p_limit_0_ < p_limit_1_ ? p_limit_1_ : (p_limit_0_ > p_limit_2_ ? p_limit_2_ : p_limit_0_);
	}

	public static float limit(float p_limit_0_, float p_limit_1_, float p_limit_2_) {
		return p_limit_0_ < p_limit_1_ ? p_limit_1_ : (p_limit_0_ > p_limit_2_ ? p_limit_2_ : p_limit_0_);
	}

	public static float limitTo1(float p_limitTo1_0_) {
		return p_limitTo1_0_ < 0.0F ? 0.0F : (p_limitTo1_0_ > 1.0F ? 1.0F : p_limitTo1_0_);
	}

	public static boolean isAnimatedWater() {
		return gameSettings.ofAnimatedWater != 2;
	}

	public static boolean isGeneratedWater() {
		return gameSettings.ofAnimatedWater == 1;
	}

	public static boolean isAnimatedPortal() {
		return gameSettings.ofAnimatedPortal;
	}

	public static boolean isAnimatedLava() {
		return gameSettings.ofAnimatedLava != 2;
	}

	public static boolean isGeneratedLava() {
		return gameSettings.ofAnimatedLava == 1;
	}

	public static boolean isAnimatedFire() {
		return gameSettings.ofAnimatedFire;
	}

	public static boolean isAnimatedRedstone() {
		return gameSettings.ofAnimatedRedstone;
	}

	public static boolean isAnimatedExplosion() {
		return gameSettings.ofAnimatedExplosion;
	}

	public static boolean isAnimatedFlame() {
		return gameSettings.ofAnimatedFlame;
	}

	public static boolean isAnimatedSmoke() {
		return gameSettings.ofAnimatedSmoke;
	}

	public static boolean isVoidParticles() {
		return gameSettings.ofVoidParticles;
	}

	public static boolean isWaterParticles() {
		return gameSettings.ofWaterParticles;
	}

	public static boolean isRainSplash() {
		return gameSettings.ofRainSplash;
	}

	public static boolean isPortalParticles() {
		return gameSettings.ofPortalParticles;
	}

	public static boolean isPotionParticles() {
		return gameSettings.ofPotionParticles;
	}

	public static boolean isFireworkParticles() {
		return gameSettings.ofFireworkParticles;
	}

	public static float getAmbientOcclusionLevel() {
		return gameSettings.ofAoLevel;
	}

	private static Method getMethod(Class p_getMethod_0_, String p_getMethod_1_, Object[] p_getMethod_2_) {
		Method[] amethod = p_getMethod_0_.getMethods();

		for (int i = 0; i < amethod.length; ++i) {
			Method method = amethod[i];

			if (method.getName().equals(p_getMethod_1_) && method.getParameterTypes().length == p_getMethod_2_.length) {
				return method;
			}
		}

		warn("No method found for: " + p_getMethod_0_.getName() + "." + p_getMethod_1_ + "(" + arrayToString(p_getMethod_2_) + ")");
		return null;
	}

	public static String arrayToString(Object[] p_arrayToString_0_) {
		if (p_arrayToString_0_ == null) {
			return "";
		} else {
			StringBuffer stringbuffer = new StringBuffer(p_arrayToString_0_.length * 5);

			for (int i = 0; i < p_arrayToString_0_.length; ++i) {
				Object object = p_arrayToString_0_[i];

				if (i > 0) {
					stringbuffer.append(", ");
				}

				stringbuffer.append(String.valueOf(object));
			}

			return stringbuffer.toString();
		}
	}

	public static String arrayToString(int[] p_arrayToString_0_) {
		if (p_arrayToString_0_ == null) {
			return "";
		} else {
			StringBuffer stringbuffer = new StringBuffer(p_arrayToString_0_.length * 5);

			for (int i = 0; i < p_arrayToString_0_.length; ++i) {
				int j = p_arrayToString_0_[i];

				if (i > 0) {
					stringbuffer.append(", ");
				}

				stringbuffer.append(String.valueOf(j));
			}

			return stringbuffer.toString();
		}
	}

	public static Minecraft getMinecraft() {
		return minecraft;
	}

	public static TextureManager getTextureManager() {
		return minecraft.getTextureManager();
	}

	public static IResourceManager getResourceManager() {
		return minecraft.getResourceManager();
	}

	public static InputStream getResourceStream(ResourceLocation p_getResourceStream_0_) throws IOException {
		return getResourceStream(minecraft.getResourceManager(), p_getResourceStream_0_);
	}

	public static InputStream getResourceStream(IResourceManager p_getResourceStream_0_, ResourceLocation p_getResourceStream_1_) throws IOException {
		IResource iresource = p_getResourceStream_0_.getResource(p_getResourceStream_1_);
		return iresource == null ? null : iresource.getInputStream();
	}

	public static IResource getResource(ResourceLocation p_getResource_0_) throws IOException {
		return minecraft.getResourceManager().getResource(p_getResource_0_);
	}

	public static boolean hasResource(ResourceLocation p_hasResource_0_) {
		try {
			IResource iresource = getResource(p_hasResource_0_);
			return iresource != null;
		} catch (IOException var2) {
			return false;
		}
	}

	public static boolean hasResource(IResourceManager p_hasResource_0_, ResourceLocation p_hasResource_1_) {
		try {
			IResource iresource = p_hasResource_0_.getResource(p_hasResource_1_);
			return iresource != null;
		} catch (IOException var3) {
			return false;
		}
	}

	public static IResourcePack[] getResourcePacks() {
		ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
		List list = resourcepackrepository.getRepositoryEntries();
		List list1 = new ArrayList();

		for (Object resourcepackrepository$entry : list) {
			list1.add(((ResourcePackRepository.Entry) resourcepackrepository$entry).getResourcePack());
		}

		IResourcePack[] airesourcepack = (IResourcePack[]) ((IResourcePack[]) list1.toArray(new IResourcePack[list1.size()]));
		return airesourcepack;
	}

	public static String getResourcePackNames() {
		if (minecraft == null) {
			return "";
		} else if (minecraft.getResourcePackRepository() == null) {
			return "";
		} else {
			IResourcePack[] airesourcepack = getResourcePacks();

			if (airesourcepack.length <= 0) {
				return getDefaultResourcePack().getPackName();
			} else {
				String[] astring = new String[airesourcepack.length];

				for (int i = 0; i < airesourcepack.length; ++i) {
					astring[i] = airesourcepack[i].getPackName();
				}

				String s = arrayToString((Object[]) astring);
				return s;
			}
		}
	}

	public static IResourcePack getDefaultResourcePack() {
		return minecraft.getResourcePackRepository().rprDefaultResourcePack;
	}

	public static boolean isFromDefaultResourcePack(ResourceLocation p_isFromDefaultResourcePack_0_) {
		IResourcePack iresourcepack = getDefiningResourcePack(p_isFromDefaultResourcePack_0_);
		return iresourcepack == getDefaultResourcePack();
	}

	public static IResourcePack getDefiningResourcePack(ResourceLocation p_getDefiningResourcePack_0_) {
		IResourcePack[] airesourcepack = getResourcePacks();

		for (int i = airesourcepack.length - 1; i >= 0; --i) {
			IResourcePack iresourcepack = airesourcepack[i];

			if (iresourcepack.resourceExists(p_getDefiningResourcePack_0_)) {
				return iresourcepack;
			}
		}

		if (getDefaultResourcePack().resourceExists(p_getDefiningResourcePack_0_)) {
			return getDefaultResourcePack();
		} else {
			return null;
		}
	}

	public static RenderGlobal getRenderGlobal() {
		return minecraft == null ? null : minecraft.renderGlobal;
	}

	public static int getMaxDynamicTileWidth() {
		return 64;
	}

	public static boolean isBetterGrass() {
		return gameSettings.ofBetterGrass != 3;
	}

	public static boolean isBetterGrassFancy() {
		return gameSettings.ofBetterGrass == 2;
	}

	public static boolean isWeatherEnabled() {
		return gameSettings.ofWeather;
	}

	public static boolean isSkyEnabled() {
		return gameSettings.ofSky;
	}

	public static boolean isSunMoonEnabled() {
		return gameSettings.ofSunMoon;
	}

	public static boolean isVignetteEnabled() {
		return gameSettings.ofVignette == 0 ? gameSettings.fancyGraphics : gameSettings.ofVignette == 2;
	}

	public static boolean isStarsEnabled() {
		return gameSettings.ofStars;
	}

	public static void sleep(long p_sleep_0_) {
		try {
			Thread.currentThread();
			Thread.sleep(p_sleep_0_);
		} catch (InterruptedException interruptedexception) {
			interruptedexception.printStackTrace();
		}
	}

	public static boolean isTimeDayOnly() {
		return gameSettings.ofTime == 1;
	}

	public static boolean isTimeDefault() {
		return gameSettings.ofTime == 0 || gameSettings.ofTime == 2;
	}

	public static boolean isTimeNightOnly() {
		return gameSettings.ofTime == 3;
	}

	public static boolean isClearWater() {
		return gameSettings.ofClearWater;
	}

	public static int getAnisotropicFilterLevel() {
		return gameSettings.ofAfLevel;
	}

	public static int getAntialiasingLevel() {
		return antialiasingLevel;
	}

	public static boolean between(int p_between_0_, int p_between_1_, int p_between_2_) {
		return p_between_0_ >= p_between_1_ && p_between_0_ <= p_between_2_;
	}

	public static boolean isMultiTexture() {
		return getAnisotropicFilterLevel() > 1 ? true : getAntialiasingLevel() > 0;
	}

	public static boolean isDrippingWaterLava() {
		return gameSettings.ofDrippingWaterLava;
	}

	public static boolean isBetterSnow() {
		return gameSettings.ofBetterSnow;
	}

	public static Dimension getFullscreenDimension() {
		if (desktopDisplayMode == null) {
			return null;
		} else if (gameSettings == null) {
			return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
		} else {
			String s = gameSettings.ofFullscreenMode;

			if (s.equals("Default")) {
				return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
			} else {
				String[] astring = tokenize(s, " x");
				return astring.length < 2 ? new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight()) : new Dimension(parseInt(astring[0], -1), parseInt(astring[1], -1));
			}
		}
	}

	public static int parseInt(String p_parseInt_0_, int p_parseInt_1_) {
		try {
			return p_parseInt_0_ == null ? p_parseInt_1_ : Integer.parseInt(p_parseInt_0_);
		} catch (NumberFormatException var3) {
			return p_parseInt_1_;
		}
	}

	public static float parseFloat(String p_parseFloat_0_, float p_parseFloat_1_) {
		try {
			return p_parseFloat_0_ == null ? p_parseFloat_1_ : Float.parseFloat(p_parseFloat_0_);
		} catch (NumberFormatException var3) {
			return p_parseFloat_1_;
		}
	}

	public static String[] tokenize(String p_tokenize_0_, String p_tokenize_1_) {
		StringTokenizer stringtokenizer = new StringTokenizer(p_tokenize_0_, p_tokenize_1_);
		List list = new ArrayList();

		while (stringtokenizer.hasMoreTokens()) {
			String s = stringtokenizer.nextToken();
			list.add(s);
		}

		String[] astring = (String[]) ((String[]) list.toArray(new String[list.size()]));
		return astring;
	}

	public static DisplayMode getDesktopDisplayMode() {
		return desktopDisplayMode;
	}

	public static DisplayMode[] getFullscreenDisplayModes() {
		try {
			DisplayMode[] adisplaymode = Display.getAvailableDisplayModes();
			List list = new ArrayList();

			for (int i = 0; i < adisplaymode.length; ++i) {
				DisplayMode displaymode = adisplaymode[i];

				if (desktopDisplayMode == null || displaymode.getBitsPerPixel() == desktopDisplayMode.getBitsPerPixel() && displaymode.getFrequency() == desktopDisplayMode.getFrequency()) {
					list.add(displaymode);
				}
			}

			DisplayMode[] adisplaymode1 = (DisplayMode[]) ((DisplayMode[]) list.toArray(new DisplayMode[list.size()]));
			Comparator comparator = new Comparator() {
				public int compare(Object p_compare_1_, Object p_compare_2_) {
					DisplayMode displaymode1 = (DisplayMode) p_compare_1_;
					DisplayMode displaymode2 = (DisplayMode) p_compare_2_;
					return displaymode1.getWidth() != displaymode2.getWidth() ? displaymode2.getWidth() - displaymode1.getWidth() : (displaymode1.getHeight() != displaymode2.getHeight() ? displaymode2.getHeight() - displaymode1.getHeight() : 0);
				}
			};
			Arrays.sort(adisplaymode1, comparator);
			return adisplaymode1;
		} catch (Exception exception) {
			exception.printStackTrace();
			return new DisplayMode[] { desktopDisplayMode };
		}
	}

	public static String[] getFullscreenModes() {
		DisplayMode[] adisplaymode = getFullscreenDisplayModes();
		String[] astring = new String[adisplaymode.length];

		for (int i = 0; i < adisplaymode.length; ++i) {
			DisplayMode displaymode = adisplaymode[i];
			String s = "" + displaymode.getWidth() + "x" + displaymode.getHeight();
			astring[i] = s;
		}

		return astring;
	}

	public static DisplayMode getDisplayMode(Dimension p_getDisplayMode_0_) throws LWJGLException {
		DisplayMode[] adisplaymode = Display.getAvailableDisplayModes();

		for (int i = 0; i < adisplaymode.length; ++i) {
			DisplayMode displaymode = adisplaymode[i];

			if (displaymode.getWidth() == p_getDisplayMode_0_.width && displaymode.getHeight() == p_getDisplayMode_0_.height && (desktopDisplayMode == null || displaymode.getBitsPerPixel() == desktopDisplayMode.getBitsPerPixel() && displaymode.getFrequency() == desktopDisplayMode.getFrequency())) {
				return displaymode;
			}
		}

		return desktopDisplayMode;
	}

	public static boolean isAnimatedTerrain() {
		return gameSettings.ofAnimatedTerrain;
	}

	public static boolean isAnimatedTextures() {
		return gameSettings.ofAnimatedTextures;
	}

	public static boolean isSwampColors() {
		return gameSettings.ofSwampColors;
	}

	public static boolean isRandomMobs() {
		return gameSettings.ofRandomMobs;
	}

	public static void checkGlError(String p_checkGlError_0_) {
		int i = GL11.glGetError();

		if (i != 0) {
			String s = GLU.gluErrorString(i);
			error("OpenGlError: " + i + " (" + s + "), at: " + p_checkGlError_0_);
		}
	}

	public static boolean isSmoothBiomes() {
		return gameSettings.ofSmoothBiomes;
	}

	public static boolean isCustomColors() {
		return gameSettings.ofCustomColors;
	}

	public static boolean isCustomSky() {
		return gameSettings.ofCustomSky;
	}

	public static boolean isCustomFonts() {
		return gameSettings.ofCustomFonts;
	}

	public static boolean isShowCapes() {
		return gameSettings.ofShowCapes;
	}

	public static boolean isConnectedTextures() {
		return gameSettings.ofConnectedTextures != 3;
	}

	public static boolean isNaturalTextures() {
		return gameSettings.ofNaturalTextures;
	}

	public static boolean isConnectedTexturesFancy() {
		return gameSettings.ofConnectedTextures == 2;
	}

	public static boolean isFastRender() {
		return gameSettings.ofFastRender;
	}

	public static boolean isTranslucentBlocksFancy() {
		return gameSettings.ofTranslucentBlocks == 0 ? gameSettings.fancyGraphics : gameSettings.ofTranslucentBlocks == 2;
	}

	public static String[] readLines(File p_readLines_0_) throws IOException {
		List list = new ArrayList();
		FileInputStream fileinputstream = new FileInputStream(p_readLines_0_);
		InputStreamReader inputstreamreader = new InputStreamReader(fileinputstream, "ASCII");
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

		String[] astring = null;
		while (astring == null) {
			String s = bufferedreader.readLine();

			if (s == null) {
				astring = (String[]) ((String[]) list.toArray(new String[list.size()]));
			} else
				list.add(s);
		}
		bufferedreader.close();
		return astring;
	}

	public static String readFile(File p_readFile_0_) throws IOException {
		FileInputStream fileinputstream = new FileInputStream(p_readFile_0_);
		return readInputStream(fileinputstream, "ASCII");
	}

	public static String readInputStream(InputStream p_readInputStream_0_) throws IOException {
		return readInputStream(p_readInputStream_0_, "ASCII");
	}

	public static String readInputStream(InputStream p_readInputStream_0_, String p_readInputStream_1_) throws IOException {
		InputStreamReader inputstreamreader = new InputStreamReader(p_readInputStream_0_, p_readInputStream_1_);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
		StringBuffer stringbuffer = new StringBuffer();

		while (true) {
			String s = bufferedreader.readLine();

			if (s == null) {
				return stringbuffer.toString();
			}

			stringbuffer.append(s);
			stringbuffer.append("\n");
		}
	}

	public static GameSettings getGameSettings() {
		return gameSettings;
	}

	public static String getNewRelease() {
		return newRelease;
	}

	public static void setNewRelease(String p_setNewRelease_0_) {
		newRelease = p_setNewRelease_0_;
	}

	public static int compareRelease(String p_compareRelease_0_, String p_compareRelease_1_) {
		String[] astring = splitRelease(p_compareRelease_0_);
		String[] astring1 = splitRelease(p_compareRelease_1_);
		String s = astring[0];
		String s1 = astring1[0];

		if (!s.equals(s1)) {
			return s.compareTo(s1);
		} else {
			int i = parseInt(astring[1], -1);
			int j = parseInt(astring1[1], -1);

			if (i != j) {
				return i - j;
			} else {
				String s2 = astring[2];
				String s3 = astring1[2];
				return s2.compareTo(s3);
			}
		}
	}

	private static String[] splitRelease(String p_splitRelease_0_) {
		if (p_splitRelease_0_ != null && p_splitRelease_0_.length() > 0) {
			String s = p_splitRelease_0_.substring(0, 1);

			if (p_splitRelease_0_.length() <= 1) {
				return new String[] { s, "", "" };
			} else {
				int i;

				for (i = 1; i < p_splitRelease_0_.length() && Character.isDigit(p_splitRelease_0_.charAt(i)); ++i) {
					;
				}

				String s1 = p_splitRelease_0_.substring(1, i);

				if (i >= p_splitRelease_0_.length()) {
					return new String[] { s, s1, "" };
				} else {
					String s2 = p_splitRelease_0_.substring(i);
					return new String[] { s, s1, s2 };
				}
			}
		} else {
			return new String[] { "", "", "" };
		}
	}

	public static int intHash(int p_intHash_0_) {
		p_intHash_0_ = p_intHash_0_ ^ 61 ^ p_intHash_0_ >> 16;
		p_intHash_0_ = p_intHash_0_ + (p_intHash_0_ << 3);
		p_intHash_0_ = p_intHash_0_ ^ p_intHash_0_ >> 4;
		p_intHash_0_ = p_intHash_0_ * 668265261;
		p_intHash_0_ = p_intHash_0_ ^ p_intHash_0_ >> 15;
		return p_intHash_0_;
	}

	public static int getRandom(BlockPos p_getRandom_0_, int p_getRandom_1_) {
		int i = intHash(p_getRandom_1_ + 37);
		i = intHash(i + p_getRandom_0_.getX());
		i = intHash(i + p_getRandom_0_.getZ());
		i = intHash(i + p_getRandom_0_.getY());
		return i;
	}

	public static WorldServer getWorldServer() {
		if (minecraft == null) {
			return null;
		} else {
			World world = minecraft.theWorld;

			if (world == null) {
				return null;
			} else if (!minecraft.isIntegratedServerRunning()) {
				return null;
			} else {
				IntegratedServer integratedserver = minecraft.getIntegratedServer();

				if (integratedserver == null) {
					return null;
				} else {
					WorldProvider worldprovider = world.provider;

					if (worldprovider == null) {
						return null;
					} else {
						int i = worldprovider.getDimensionId();

						try {
							WorldServer worldserver = integratedserver.worldServerForDimension(i);
							return worldserver;
						} catch (NullPointerException var5) {
							return null;
						}
					}
				}
			}
		}
	}

	public static int getAvailableProcessors() {
		return availableProcessors;
	}

	public static void updateAvailableProcessors() {
		availableProcessors = Runtime.getRuntime().availableProcessors();
	}

	public static boolean isSingleProcessor() {
		return getAvailableProcessors() <= 1;
	}

	public static boolean isSmoothWorld() {
		return gameSettings.ofSmoothWorld;
	}

	public static boolean isLazyChunkLoading() {
		return !isSingleProcessor() ? false : gameSettings.ofLazyChunkLoading;
	}

	public static int getChunkViewDistance() {
		if (gameSettings == null) {
			return 10;
		} else {
			int i = gameSettings.renderDistanceChunks;
			return i;
		}
	}

	public static boolean equals(Object p_equals_0_, Object p_equals_1_) {
		return p_equals_0_ == p_equals_1_ ? true : (p_equals_0_ == null ? false : p_equals_0_.equals(p_equals_1_));
	}

	public static void checkDisplaySettings() {
		if (getAntialiasingLevel() > 0) {
			int i = getAntialiasingLevel();
			DisplayMode displaymode = Display.getDisplayMode();
			dbg("FSAA Samples: " + i);

			try {
				Display.destroy();
				Display.setDisplayMode(displaymode);
				Display.create((new PixelFormat()).withDepthBits(24).withSamples(i));
				Display.setResizable(false);
				Display.setResizable(true);
			} catch (LWJGLException lwjglexception2) {
				warn("Error setting FSAA: " + i + "x");
				lwjglexception2.printStackTrace();

				try {
					Display.setDisplayMode(displaymode);
					Display.create((new PixelFormat()).withDepthBits(24));
					Display.setResizable(false);
					Display.setResizable(true);
				} catch (LWJGLException lwjglexception1) {
					lwjglexception1.printStackTrace();

					try {
						Display.setDisplayMode(displaymode);
						Display.create();
						Display.setResizable(false);
						Display.setResizable(true);
					} catch (LWJGLException lwjglexception) {
						lwjglexception.printStackTrace();
					}
				}
			}
		}
	}

	private static ByteBuffer readIconImage(File p_readIconImage_0_) throws IOException {
		BufferedImage bufferedimage = ImageIO.read(p_readIconImage_0_);
		int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), (int[]) null, 0, bufferedimage.getWidth());
		ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

		for (int i : aint) {
			bytebuffer.putInt(i << 8 | i >> 24 & 255);
		}

		bytebuffer.flip();
		return bytebuffer;
	}

	public static void checkDisplayMode() {
		try {
			if (minecraft.isFullScreen()) {
				if (fullscreenModeChecked) {
					return;
				}

				fullscreenModeChecked = true;
				desktopModeChecked = false;
				DisplayMode displaymode = Display.getDisplayMode();
				Dimension dimension = getFullscreenDimension();

				if (dimension == null) {
					return;
				}

				if (displaymode.getWidth() == dimension.width && displaymode.getHeight() == dimension.height) {
					return;
				}

				DisplayMode displaymode1 = getDisplayMode(dimension);

				if (displaymode1 == null) {
					return;
				}

				Display.setDisplayMode(displaymode1);
				minecraft.displayWidth = Display.getDisplayMode().getWidth();
				minecraft.displayHeight = Display.getDisplayMode().getHeight();

				if (minecraft.displayWidth <= 0) {
					minecraft.displayWidth = 1;
				}

				if (minecraft.displayHeight <= 0) {
					minecraft.displayHeight = 1;
				}

				if (minecraft.currentScreen != null) {
					ScaledResolution scaledresolution = new ScaledResolution(minecraft);
					int i = scaledresolution.getScaledWidth();
					int j = scaledresolution.getScaledHeight();
					minecraft.currentScreen.setWorldAndResolution(minecraft, i, j);
				}

				minecraft.loadingScreen = new LoadingScreenRenderer(minecraft);
				updateFramebufferSize();
				Display.setFullscreen(true);
				minecraft.gameSettings.updateVSync();
				GlStateManager.enableTexture2D();
			} else {
				if (desktopModeChecked) {
					return;
				}

				desktopModeChecked = true;
				fullscreenModeChecked = false;
				minecraft.gameSettings.updateVSync();
				Display.update();
				GlStateManager.enableTexture2D();
				Display.setResizable(false);
				Display.setResizable(true);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void updateFramebufferSize() {
		minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);

		if (minecraft.entityRenderer != null) {
			minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
		}
	}

	public static Object[] addObjectToArray(Object[] p_addObjectToArray_0_, Object p_addObjectToArray_1_) {
		if (p_addObjectToArray_0_ == null) {
			throw new NullPointerException("The given array is NULL");
		} else {
			int i = p_addObjectToArray_0_.length;
			int j = i + 1;
			Object[] aobject = (Object[]) ((Object[]) Array.newInstance(p_addObjectToArray_0_.getClass().getComponentType(), j));
			System.arraycopy(p_addObjectToArray_0_, 0, aobject, 0, i);
			aobject[i] = p_addObjectToArray_1_;
			return aobject;
		}
	}

	public static Object[] addObjectsToArray(Object[] p_addObjectsToArray_0_, Object[] p_addObjectsToArray_1_) {
		if (p_addObjectsToArray_0_ == null) {
			throw new NullPointerException("The given array is NULL");
		} else if (p_addObjectsToArray_1_.length == 0) {
			return p_addObjectsToArray_0_;
		} else {
			int i = p_addObjectsToArray_0_.length;
			int j = i + p_addObjectsToArray_1_.length;
			Object[] aobject = (Object[]) ((Object[]) Array.newInstance(p_addObjectsToArray_0_.getClass().getComponentType(), j));
			System.arraycopy(p_addObjectsToArray_0_, 0, aobject, 0, i);
			System.arraycopy(p_addObjectsToArray_1_, 0, aobject, i, p_addObjectsToArray_1_.length);
			return aobject;
		}
	}

	public static boolean isCustomItems() {
		return false;
	}

	public static boolean isActing() {
		boolean flag = isActingNow();
		long i = System.currentTimeMillis();

		if (flag) {
			lastActionTime = i;
			return true;
		} else {
			return i - lastActionTime < 100L;
		}
	}

	private static boolean isActingNow() {
		return Mouse.isButtonDown(0) ? true : Mouse.isButtonDown(1);
	}

	public static void drawFps() {
		Minecraft minecraftx = minecraft;
		int i = Minecraft.getDebugFPS();
		String s = getUpdates(minecraft.debug);
		int j = minecraft.renderGlobal.getCountActiveRenderers();
		int k = minecraft.renderGlobal.getCountEntitiesRendered();
		int l = minecraft.renderGlobal.getCountTileEntitiesRendered();
		String s1 = "" + i + " fps, C: " + j + ", E: " + k + "+" + l + ", U: " + s;
		minecraft.fontRendererObj.drawString(s1, 2, 2, -2039584);
	}

	private static String getUpdates(String p_getUpdates_0_) {
		int i = p_getUpdates_0_.indexOf(40);

		if (i < 0) {
			return "";
		} else {
			int j = p_getUpdates_0_.indexOf(32, i);
			return j < 0 ? "" : p_getUpdates_0_.substring(i + 1, j);
		}
	}

	public static int getBitsOs() {
		String s = System.getenv("ProgramFiles(X86)");
		return s != null ? 64 : 32;
	}

	public static int getBitsJre() {
		String[] astring = new String[] { "sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch" };

		for (int i = 0; i < astring.length; ++i) {
			String s = astring[i];
			String s1 = System.getProperty(s);

			if (s1 != null && s1.contains("64")) {
				return 64;
			}
		}

		return 32;
	}

	public static boolean isNotify64BitJava() {
		return notify64BitJava;
	}

	public static void setNotify64BitJava(boolean p_setNotify64BitJava_0_) {
		notify64BitJava = p_setNotify64BitJava_0_;
	}

	public static boolean isConnectedModels() {
		return false;
	}

	public static String fillLeft(String p_fillLeft_0_, int p_fillLeft_1_, char p_fillLeft_2_) {
		if (p_fillLeft_0_ == null) {
			p_fillLeft_0_ = "";
		}

		if (p_fillLeft_0_.length() >= p_fillLeft_1_) {
			return p_fillLeft_0_;
		} else {
			StringBuffer stringbuffer = new StringBuffer(p_fillLeft_0_);

			while (stringbuffer.length() < p_fillLeft_1_ - p_fillLeft_0_.length()) {
				stringbuffer.append(p_fillLeft_2_);
			}

			return stringbuffer.toString() + p_fillLeft_0_;
		}
	}

	public static String fillRight(String p_fillRight_0_, int p_fillRight_1_, char p_fillRight_2_) {
		if (p_fillRight_0_ == null) {
			p_fillRight_0_ = "";
		}

		if (p_fillRight_0_.length() >= p_fillRight_1_) {
			return p_fillRight_0_;
		} else {
			StringBuffer stringbuffer = new StringBuffer(p_fillRight_0_);

			while (stringbuffer.length() < p_fillRight_1_) {
				stringbuffer.append(p_fillRight_2_);
			}

			return stringbuffer.toString();
		}
	}
}
