package me.wavelength.baseclient.account;

import com.google.gson.JsonObject;

public class Account {

	private String email;

	private String password;

	private String name;

	private boolean banned;

	public Account(String email, String password, String name) {
		this.email = email;
		this.password = password;
		this.name = name;
	}

	public Account() {
	}

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return this.password;
	}

	public String getName() {
		return this.name;
	}

	public boolean isBanned() {
		return this.banned;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("email", this.email);
		jsonObject.addProperty("password", this.password);
		jsonObject.addProperty("name", this.name);
		jsonObject.addProperty("banned", Boolean.valueOf(this.banned));
		return jsonObject;
	}

	public void fromJson(JsonObject json) {
		this.email = json.get("email").getAsString();
		this.password = json.get("password").getAsString();
		this.name = json.get("name").getAsString();
		this.banned = json.get("banned").getAsBoolean();
	}

}