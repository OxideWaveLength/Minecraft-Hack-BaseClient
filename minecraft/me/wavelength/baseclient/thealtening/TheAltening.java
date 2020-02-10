package me.wavelength.baseclient.thealtening;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class TheAltening {

	private final String apiKey;

	private final String website = "http://api.thealtening.com/v1/";

	private final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

	public TheAltening(String apiKey) {
		this.apiKey = apiKey;
	}

	public User getUser() throws IOException {
		getClass();
		URLConnection licenseEndpoint = (new URL(attach(website + "license"))).openConnection();
		String userInfo = new String(Utilities.getInstance().readAllBytes(licenseEndpoint.getInputStream()));
		return (User) this.gson.fromJson(userInfo, User.class);
	}

	public AlteningAlt generateAccount(User user) throws IOException {
		getClass();
		URLConnection generateEndpoint = (new URL(attach(website + "generate"))).openConnection();
		String accountInfo = new String(Utilities.getInstance().readAllBytes(generateEndpoint.getInputStream()));
		if (user.isPremium())
			return (AlteningAlt) this.gson.fromJson(accountInfo, AlteningAlt.class);
		return null;
	}

	public boolean favoriteAccount(AlteningAlt account) throws IOException {
		getClass();
		URLConnection favoriteAccount = (new URL(attachAccount(website + "favorite", account))).openConnection();
		String info = new String(Utilities.getInstance().readAllBytes(favoriteAccount.getInputStream()));
		return info.isEmpty();
	}

	public boolean privateAccount(AlteningAlt account) throws IOException {
		getClass();
		URLConnection privateAccount = (new URL(attachAccount(website + "private", account))).openConnection();
		String info = new String(Utilities.getInstance().readAllBytes(privateAccount.getInputStream()));
		return info.isEmpty();
	}

	private String attach(String website) {
		return website + "?token=" + this.apiKey;
	}

	private String attachAccount(String website, AlteningAlt account) {
		return website + "?token=" + this.apiKey + "&acctoken=" + account.getToken();
	}

}