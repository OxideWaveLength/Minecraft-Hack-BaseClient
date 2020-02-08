package net.minecraft.realms;

import java.net.Proxy;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.world.WorldSettings;

public class Realms {
	public static boolean isTouchScreen() {
		return Minecraft.getMinecraft().gameSettings.touchscreen;
	}

	public static Proxy getProxy() {
		return Minecraft.getMinecraft().getProxy();
	}

	public static String sessionId() {
		Session session = Minecraft.getMinecraft().getSession();
		return session == null ? null : session.getSessionID();
	}

	public static String userName() {
		Session session = Minecraft.getMinecraft().getSession();
		return session == null ? null : session.getUsername();
	}

	public static long currentTimeMillis() {
		return Minecraft.getSystemTime();
	}

	public static String getSessionId() {
		return Minecraft.getMinecraft().getSession().getSessionID();
	}

	public static String getUUID() {
		return Minecraft.getMinecraft().getSession().getPlayerID();
	}

	public static String getName() {
		return Minecraft.getMinecraft().getSession().getUsername();
	}

	public static String uuidToName(String p_uuidToName_0_) {
		return Minecraft.getMinecraft().getSessionService().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(p_uuidToName_0_), (String) null), false).getName();
	}

	public static void setScreen(RealmsScreen p_setScreen_0_) {
		Minecraft.getMinecraft().displayGuiScreen(p_setScreen_0_.getProxy());
	}

	public static String getGameDirectoryPath() {
		return Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
	}

	public static int survivalId() {
		return WorldSettings.GameType.SURVIVAL.getID();
	}

	public static int creativeId() {
		return WorldSettings.GameType.CREATIVE.getID();
	}

	public static int adventureId() {
		return WorldSettings.GameType.ADVENTURE.getID();
	}

	public static int spectatorId() {
		return WorldSettings.GameType.SPECTATOR.getID();
	}

	public static void setConnectedToRealms(boolean p_setConnectedToRealms_0_) {
		Minecraft.getMinecraft().func_181537_a(p_setConnectedToRealms_0_);
	}

	public static ListenableFuture<Object> downloadResourcePack(String p_downloadResourcePack_0_, String p_downloadResourcePack_1_) {
		ListenableFuture<Object> listenablefuture = Minecraft.getMinecraft().getResourcePackRepository().downloadResourcePack(p_downloadResourcePack_0_, p_downloadResourcePack_1_);
		return listenablefuture;
	}

	public static void clearResourcePack() {
		Minecraft.getMinecraft().getResourcePackRepository().func_148529_f();
	}
}
