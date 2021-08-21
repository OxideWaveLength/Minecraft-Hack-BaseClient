package net.minecraft.util;

import java.util.Set;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class JsonSerializableSet extends ForwardingSet<String> implements IJsonSerializable {
	private final Set<String> underlyingSet = Sets.<String>newHashSet();

	public void fromJson(JsonElement json) {
		if (json.isJsonArray()) {
			for (JsonElement jsonelement : json.getAsJsonArray()) {
				this.add(jsonelement.getAsString());
			}
		}
	}

	/**
	 * Gets the JsonElement that can be serialized.
	 */
	public JsonElement getSerializableElement() {
		JsonArray jsonarray = new JsonArray();

		for (String s : this) {
			jsonarray.add(new JsonPrimitive(s));
		}

		return jsonarray;
	}

	protected Set<String> delegate() {
		return this.underlyingSet;
	}
}
