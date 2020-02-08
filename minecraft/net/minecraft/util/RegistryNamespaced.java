package net.minecraft.util;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class RegistryNamespaced<K, V> extends RegistrySimple<K, V> implements IObjectIntIterable<V> {
	protected final ObjectIntIdentityMap underlyingIntegerMap = new ObjectIntIdentityMap();
	protected final Map<V, K> inverseObjectRegistry;

	public RegistryNamespaced() {
		this.inverseObjectRegistry = ((BiMap) this.registryObjects).inverse();
	}

	public void register(int id, K p_177775_2_, V p_177775_3_) {
		this.underlyingIntegerMap.put(p_177775_3_, id);
		this.putObject(p_177775_2_, p_177775_3_);
	}

	protected Map<K, V> createUnderlyingMap() {
		return HashBiMap.<K, V>create();
	}

	public V getObject(K name) {
		return super.getObject(name);
	}

	/**
	 * Gets the name we use to identify the given object.
	 */
	public K getNameForObject(V p_177774_1_) {
		return (K) this.inverseObjectRegistry.get(p_177774_1_);
	}

	/**
	 * Does this registry contain an entry for the given key?
	 */
	public boolean containsKey(K p_148741_1_) {
		return super.containsKey(p_148741_1_);
	}

	/**
	 * Gets the integer ID we use to identify the given object.
	 */
	public int getIDForObject(V p_148757_1_) {
		return this.underlyingIntegerMap.get(p_148757_1_);
	}

	/**
	 * Gets the object identified by the given ID.
	 */
	public V getObjectById(int id) {
		return (V) this.underlyingIntegerMap.getByValue(id);
	}

	public Iterator<V> iterator() {
		return this.underlyingIntegerMap.iterator();
	}
}
