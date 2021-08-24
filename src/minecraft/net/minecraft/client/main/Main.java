package net.minecraft.client.main;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.wavelength.baseclient.BaseClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {

	private static BaseClient baseClient;

	public static void main(String[] p_main_0_) {
		baseClient = new BaseClient();
		System.setProperty("java.net.preferIPv4Stack", "true");
		OptionParser optionparser = new OptionParser();
		optionparser.allowsUnrecognizedOptions();
		optionparser.accepts("demo");
		optionparser.accepts("fullscreen");
		optionparser.accepts("checkGlErrors");
		OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
		OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(25565), new Integer[0]);
		OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
		OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().<File>ofType(File.class);
		OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().<File>ofType(File.class);
		OptionSpec<String> optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
		OptionSpec<Integer> optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).<Integer>ofType(Integer.class);
		OptionSpec<String> optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
		OptionSpec<String> optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
		OptionSpec<String> optionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo(baseClient.getDefaultUsername());
		OptionSpec<String> optionspec10 = optionparser.accepts("uuid").withRequiredArg();
		OptionSpec<String> optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
		OptionSpec<String> optionspec12 = optionparser.accepts("version").withRequiredArg().required();
		OptionSpec<Integer> optionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(854), new Integer[0]);
		OptionSpec<Integer> optionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(480), new Integer[0]);
		OptionSpec<String> optionspec15 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
		OptionSpec<String> optionspec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
		OptionSpec<String> optionspec17 = optionparser.accepts("assetIndex").withRequiredArg();
		OptionSpec<String> optionspec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
		OptionSpec<String> optionspec19 = optionparser.nonOptions();
		OptionSet optionset = optionparser.parse(p_main_0_);
		List<String> list = optionset.valuesOf(optionspec19);

		if (!list.isEmpty()) {
			System.out.println("Completely ignored arguments: " + list);
		}

		String s = (String) optionset.valueOf(optionspec5);
		Proxy proxy = Proxy.NO_PROXY;

		if (s != null) {
			try {
				proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, ((Integer) optionset.valueOf(optionspec6)).intValue()));
			} catch (Exception var46) {
				;
			}
		}

		final String s1 = (String) optionset.valueOf(optionspec7);
		final String s2 = (String) optionset.valueOf(optionspec8);

		if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(s1, s2.toCharArray());
				}
			});
		}

		int i = ((Integer) optionset.valueOf(optionspec13)).intValue();
		int j = ((Integer) optionset.valueOf(optionspec14)).intValue();
		boolean flag = optionset.has("fullscreen");
		boolean flag1 = optionset.has("checkGlErrors");
		boolean flag2 = optionset.has("demo");
		String s3 = (String) optionset.valueOf(optionspec12);
		Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
		PropertyMap propertymap = (PropertyMap) gson.fromJson((String) optionset.valueOf(optionspec15), PropertyMap.class);
		PropertyMap propertymap1 = (PropertyMap) gson.fromJson((String) optionset.valueOf(optionspec16), PropertyMap.class);
		File file1 = (File) optionset.valueOf(optionspec2);
		File file2 = optionset.has(optionspec3) ? (File) optionset.valueOf(optionspec3) : new File(file1, "assets/");
		File file3 = optionset.has(optionspec4) ? (File) optionset.valueOf(optionspec4) : new File(file1, "resourcepacks/");
		String s4 = optionset.has(optionspec10) ? (String) optionspec10.value(optionset) : (String) optionspec9.value(optionset);
		String s5 = optionset.has(optionspec17) ? (String) optionspec17.value(optionset) : null;
		String s6 = (String) optionset.valueOf(optionspec);
		Integer integer = (Integer) optionset.valueOf(optionspec1);
		Session session = new Session((String) optionspec9.value(optionset), s4, (String) optionspec11.value(optionset), (String) optionspec18.value(optionset));
		GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag1), new GameConfiguration.FolderInformation(file1, file3, file2, s5), new GameConfiguration.GameInformation(flag2, s3), new GameConfiguration.ServerInformation(s6, integer.intValue()));
		Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
			public void run() {
				Minecraft.stopIntegratedServer();
			}
		});
		Thread.currentThread().setName("Client thread");
		(new Minecraft(gameconfiguration)).run();
	}

	private static boolean isNullOrEmpty(String str) {
		return str != null && !str.isEmpty();
	}

}