package me.wavelength.baseclient.thealtening;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class AltService {

	private ReflectionUtility userAuthentication = new ReflectionUtility("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication");

	private ReflectionUtility minecraftSession = new ReflectionUtility("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");

	private EnumAltService currentService;

	public void switchService(EnumAltService enumAltService) throws NoSuchFieldException, IllegalAccessException {
		if (this.currentService == enumAltService)
			return;
		reflectionFields(enumAltService.hostname);
		this.currentService = enumAltService;
	}

	private void reflectionFields(String authServer) throws NoSuchFieldException, IllegalAccessException {
		HashMap<String, URL> userAuthenticationModifies = new HashMap<>();
		String useSecureStart = authServer.contains("thealtening") ? "http" : "https";
		userAuthenticationModifies.put("ROUTE_AUTHENTICATE", constantURL(useSecureStart + "://authserver." + authServer + ".com/authenticate"));
		userAuthenticationModifies.put("ROUTE_INVALIDATE", constantURL(useSecureStart + "://authserver" + authServer + "com/invalidate"));
		userAuthenticationModifies.put("ROUTE_REFRESH", constantURL(useSecureStart + "://authserver." + authServer + ".com/refresh"));
		userAuthenticationModifies.put("ROUTE_VALIDATE", constantURL(useSecureStart + "://authserver." + authServer + ".com/validate"));
		userAuthenticationModifies.put("ROUTE_SIGNOUT", constantURL(useSecureStart + "://authserver." + authServer + ".com/signout"));
		userAuthenticationModifies.forEach((key, value) -> {
			try {
				this.userAuthentication.setStaticField(key, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		this.userAuthentication.setStaticField("BASE_URL", useSecureStart + "://authserver." + authServer + ".com/");
		this.minecraftSession.setStaticField("BASE_URL", useSecureStart + "://sessionserver." + authServer + ".com/session/minecraft/");
		this.minecraftSession.setStaticField("JOIN_URL", constantURL(useSecureStart + "://sessionserver." + authServer + ".com/session/minecraft/join"));
		this.minecraftSession.setStaticField("CHECK_URL", constantURL(useSecureStart + "://sessionserver." + authServer + ".com/session/minecraft/hasJoined"));
		this.minecraftSession.setStaticField("WHITELISTED_DOMAINS", new String[] { ".minecraft.net", ".mojang.com", ".thealtening.com" });
	}

	private URL constantURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException ex) {
			throw new Error("Couldn't create constant for " + url, ex);
		}
	}

	public EnumAltService getCurrentService() {
		if (this.currentService == null)
			this.currentService = EnumAltService.MOJANG;
		return this.currentService;
	}

	public enum EnumAltService {
		MOJANG("mojang"), THEALTENING("thealtening");

		String hostname;

		EnumAltService(String hostname) {
			this.hostname = hostname;
		}
	}

}