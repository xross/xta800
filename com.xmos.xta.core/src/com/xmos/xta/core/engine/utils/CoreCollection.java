package com.xmos.xta.core.engine.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.xmos.libs.util.IteratableMap;

public class CoreCollection<E> {
	private Map<CoreId, E> m_collectedObjects;

	public CoreCollection() {
		m_collectedObjects = new IteratableMap<CoreId, E>();
	}

	public CoreCollection(CoreCollection<E> old) {
		m_collectedObjects = new IteratableMap<CoreId, E>(old.m_collectedObjects);
	}
 
	public void add(CoreId core, E newObject) {
		m_collectedObjects.put(core, newObject);
	}

	public E get(CoreId core) {
		return m_collectedObjects.get(core);
	}

	public Collection<CoreId> getEntries() {
		Collection<CoreId> entries = new ArrayList<CoreId>();
		entries.addAll(m_collectedObjects.keySet());
		return entries;
	}
}
