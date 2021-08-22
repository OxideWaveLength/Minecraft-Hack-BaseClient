package me.wavelength.baseclient.thealtening;

import com.google.gson.annotations.SerializedName;

public class User {

	@SerializedName("username")
	private String username;

	@SerializedName("premium")
	private boolean premium;

	@SerializedName("premium_name")
	private String premiumName;

	@SerializedName("expires")
	private String expiryDate;

	public String getUsername() {
		return this.username;
	}

	public boolean isPremium() {
		return this.premium;
	}

	public String getPremiumName() {
		return this.premiumName;
	}

	public String getExpiryDate() {
		return this.expiryDate;
	}

}