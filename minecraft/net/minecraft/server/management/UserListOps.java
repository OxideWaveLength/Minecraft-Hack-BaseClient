package net.minecraft.server.management;

import java.io.File;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry> {
	public UserListOps(File saveFile) {
		super(saveFile);
	}

	protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
		return new UserListOpsEntry(entryData);
	}

	public String[] getKeys() {
		String[] astring = new String[this.getValues().size()];
		int i = 0;

		for (UserListOpsEntry userlistopsentry : this.getValues().values()) {
			astring[i++] = ((GameProfile) userlistopsentry.getValue()).getName();
		}

		return astring;
	}

	public boolean func_183026_b(GameProfile p_183026_1_) {
		UserListOpsEntry userlistopsentry = (UserListOpsEntry) this.getEntry(p_183026_1_);
		return userlistopsentry != null ? userlistopsentry.func_183024_b() : false;
	}

	/**
	 * Gets the key value for the given object
	 */
	protected String getObjectKey(GameProfile obj) {
		return obj.getId().toString();
	}

	/**
	 * Gets the GameProfile of based on the provided username.
	 */
	public GameProfile getGameProfileFromName(String username) {
		for (UserListOpsEntry userlistopsentry : this.getValues().values()) {
			if (username.equalsIgnoreCase(((GameProfile) userlistopsentry.getValue()).getName())) {
				return (GameProfile) userlistopsentry.getValue();
			}
		}

		return null;
	}
}
