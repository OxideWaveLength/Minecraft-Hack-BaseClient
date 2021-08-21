package me.wavelength.baseclient.account;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AccountManager {

	private ArrayList<Account> accounts = new ArrayList<>();

	private final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

	private File altsFile;

	private String alteningKey;

	private String lastAlteningAlt;

	private Account lastAlt;

	public AccountManager(File parent) {
		this.altsFile = new File(parent.toString() + File.separator + "alts.json");
		load();
	}

	public void save() {
		if (this.altsFile == null)
			return;
		try {
			if (!this.altsFile.exists())
				this.altsFile.createNewFile();
			PrintWriter printWriter = new PrintWriter(this.altsFile);
			printWriter.write(this.gson.toJson((JsonElement) toJson()));
			printWriter.close();
		} catch (IOException iOException) {
		}
	}

	public void load() {
		if (!this.altsFile.exists()) {
			save();
			return;
		}
		try {
			JsonObject json = (new JsonParser()).parse(new FileReader(this.altsFile)).getAsJsonObject();
			fromJson(json);
		} catch (IOException iOException) {
		}
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		getAccounts().forEach(account -> jsonArray.add((JsonElement) account.toJson()));
		if (this.alteningKey != null)
			jsonObject.addProperty("altening", this.alteningKey);
		if (this.lastAlteningAlt != null)
			jsonObject.addProperty("alteningAlt", this.lastAlteningAlt);
		if (this.lastAlt != null)
			jsonObject.add("lastalt", (JsonElement) this.lastAlt.toJson());
		jsonObject.add("accounts", (JsonElement) jsonArray);
		return jsonObject;
	}

	public void fromJson(JsonObject json) {
		if (json.has("altening"))
			this.alteningKey = json.get("altening").getAsString();
		if (json.has("alteningAlt"))
			this.lastAlteningAlt = json.get("alteningAlt").getAsString();
		if (json.has("lastalt")) {
			Account account = new Account();
			account.fromJson(json.get("lastalt").getAsJsonObject());
			this.lastAlt = account;
		}
		JsonArray jsonArray = json.get("accounts").getAsJsonArray();
		jsonArray.forEach(jsonElement -> {
			JsonObject jsonObject = (JsonObject) jsonElement;
			Account account = new Account();
			account.fromJson(jsonObject);
			getAccounts().add(account);
		});
	}

	public void remove(String username) {
		for (Account account : getAccounts()) {
			if (account.getName().equalsIgnoreCase(username))
				getAccounts().remove(account);
		}
	}

	public Account getAccountByEmail(String email) {
		for (Account account : getAccounts()) {
			if (account.getEmail().equalsIgnoreCase(email))
				return account;
		}
		return null;
	}

	public String getLastAlteningAlt() {
		return this.lastAlteningAlt;
	}

	public void setLastAlteningAlt(String lastAlteningAlt) {
		this.lastAlteningAlt = lastAlteningAlt;
	}

	public String getAlteningKey() {
		return this.alteningKey;
	}

	public void setAlteningKey(String alteningKey) {
		this.alteningKey = alteningKey;
	}

	public Account getLastAlt() {
		return this.lastAlt;
	}

	public void setLastAlt(Account lastAlt) {
		this.lastAlt = lastAlt;
	}

	public ArrayList<Account> getNotBannedAccounts() {
		List<Account> accounts = new ArrayList<Account>(this.accounts);
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i).isBanned())
				accounts.remove(i);
		}
		return this.accounts;
	}

	public ArrayList<Account> getAccounts() {
		return this.accounts;
	}

}