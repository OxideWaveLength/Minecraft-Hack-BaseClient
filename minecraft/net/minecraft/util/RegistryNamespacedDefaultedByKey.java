package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class RegistryNamespacedDefaultedByKey<K, V> extends RegistryNamespaced<K, V> {
	/** The key of the default value. */
	private final K defaultValueKey;

	/**
	 * The default value for this registry, retrurned in the place of a null value.
	 */
	private V defaultValue;

	public RegistryNamespacedDefaultedByKey(K p_i46017_1_) {
		this.defaultValueKey = p_i46017_1_;
	}

	public void register(int id, K p_177775_2_, V p_177775_3_) {
		if (this.defaultValueKey.equals(p_177775_2_)) {
			this.defaultValue = p_177775_3_;
		}

		super.register(id, p_177775_2_, p_177775_3_);
	}

	/**
	 * validates that this registry's key is non-null
	 */
	public void validateKey() {
		Validate.notNull(this.defaultValueKey);
	}

	public V getObject(K name) {
		V v = super.getObject(name);
		return (V) (v == null ? this.defaultValue : v);
	}

	/**
	 * Gets the object identified by the given ID.
	 */
	public V getObjectById(int id) {
		V v = super.getObjectById(id);
		return (V) (v == null ? this.defaultValue : v);
	}
}
