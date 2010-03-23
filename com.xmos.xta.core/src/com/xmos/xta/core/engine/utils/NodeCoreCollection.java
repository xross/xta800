package com.xmos.xta.core.engine.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.Pair;

public class NodeCoreCollection<E> {
	private Map<String, E> m_collectedObjects;

	public NodeCoreCollection() {
		m_collectedObjects = new IteratableMap<String, E>();
	}

	public NodeCoreCollection(NodeCoreCollection<E> old) {
		m_collectedObjects = new IteratableMap<String, E>(old.m_collectedObjects);
	}
 
	public void add(int nodeNumber, int coreNumber, E newObject) {
		m_collectedObjects.put(nodeCoreToString(nodeNumber, coreNumber), newObject);
	}

	public E get(int nodeNumber, int coreNumber) {
		String s = nodeCoreToString(nodeNumber, coreNumber);
		return m_collectedObjects.get(s);
	}

	public Collection<Pair<Integer, Integer>> getEntries() {
		Collection<Pair<Integer, Integer>> entries = new ArrayList<Pair<Integer, Integer>>();
		for (String key : m_collectedObjects.keySet()) {
			entries.add(stringToNodeCore(key));
		}
		return entries;
	}

	private String nodeCoreToString(int nodeNumber, int coreNumber) {
		return nodeNumber + ":" + coreNumber;
	}

	private Pair<Integer, Integer> stringToNodeCore(String str) {
		int nodeNumber = Integer.parseInt(str.split(":")[0]);
		int coreNumber = Integer.parseInt(str.split(":")[1]);
		return new Pair<Integer, Integer>(nodeNumber, coreNumber);
	}
}
