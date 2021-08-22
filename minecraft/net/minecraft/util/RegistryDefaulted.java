package net.minecraft.util;

public class RegistryDefaulted<K, V> extends RegistrySimple<K, V> {
	/**
	 * Default object for this registry, returned when an object is not found.
	 */
	private final V defaultObject;

	public RegistryDefaulted(V defaultObjectIn) {
		this.defaultObject = defaultObjectIn;
	}

	public V getObject(K name) {
		V v = super.getObject(name);
		return (V) (v == null ? this.defaultObject : v);
	}
}
