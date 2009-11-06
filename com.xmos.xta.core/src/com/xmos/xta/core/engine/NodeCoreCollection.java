package com.xmos.xta.core.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.xmos.libs.util.Pair;

public class NodeCoreCollection<E> {
	private Map<String, E> m_collectedObjects;

	public NodeCoreCollection() {
		m_collectedObjects = new HashMap<String, E>();
	}
	
	public NodeCoreCollection(NodeCoreCollection<E> old) {
		m_collectedObjects = new HashMap<String, E>(old.m_collectedObjects);
	}

	public void add(int nodeId, int coreId, E newObject) {
		m_collectedObjects.put(nodeCoreToString(nodeId, coreId), newObject);
	}

	public E get(int nodeId, int coreId) {
		return m_collectedObjects.get(nodeCoreToString(nodeId, coreId));
	}

	public void clear() {
		m_collectedObjects.clear();
	}

	public Collection<Pair<Integer, Integer>> getEntries() {
		Collection<Pair<Integer, Integer>> entries = new ArrayList<Pair<Integer, Integer>>();
		for (String key : m_collectedObjects.keySet()) {
			entries.add(stringToNodeCore(key));
		}
		return entries;
	}

	private String nodeCoreToString(int nodeId, int coreId) {
		return nodeId + ":" + coreId;
	}
	
	private Pair<Integer, Integer> stringToNodeCore(String str) {
		int nodeId = Integer.parseInt(str.split(":")[0]);
		int coreId = Integer.parseInt(str.split(":")[1]);
		return new Pair<Integer, Integer>(nodeId, coreId);
	}
}
