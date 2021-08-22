package net.minecraft.util;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class ObjectIntIdentityMap implements IObjectIntIterable {
	private final IdentityHashMap identityMap = new IdentityHashMap(512);
	private final List objectList = Lists.newArrayList();
	

	public void put(Object key, int value) {
		this.identityMap.put(key, Integer.valueOf(value));

		while (this.objectList.size() <= value) {
			this.objectList.add((Object) null);
		}

		this.objectList.set(value, key);
	}

	public int get(Object key) {
		Integer integer = (Integer) this.identityMap.get(key);
		return integer == null ? -1 : integer.intValue();
	}

	public final Object getByValue(int value) {
		return value >= 0 && value < this.objectList.size() ? this.objectList.get(value) : null;
	}

	public Iterator iterator() {
		return Iterators.filter(this.objectList.iterator(), Predicates.notNull());
	}

	public List getObjectList() {
		return this.objectList;
	}
}
