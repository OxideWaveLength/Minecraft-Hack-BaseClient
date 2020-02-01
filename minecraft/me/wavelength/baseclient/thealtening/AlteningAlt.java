package me.wavelength.baseclient.thealtening;

import com.google.gson.annotations.SerializedName;

public class AlteningAlt {

	@SerializedName("token")
	private String token;

	@SerializedName("username")
	private String username;

	@SerializedName("expires")
	private String expiryDate;

	@SerializedName("limit")
	private boolean isLimitReached;

	@SerializedName("skin")
	private String skinHash;

	public String getToken() {
		return this.token;
	}

	public String getUsername() {
		return this.username;
	}

	public String getExpiryDate() {
		return this.expiryDate;
	}

	public boolean isLimitReached() {
		return this.isLimitReached;
	}

	public String getSkinHash() {
		return this.skinHash;
	}

}