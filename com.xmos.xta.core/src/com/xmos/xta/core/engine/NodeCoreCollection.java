package com.xmos.xta.core.engine;

import java.util.HashMap;
import java.util.Map;

public class NodeCoreCollection<E> {
	private Map<String, E> m_collectedObjects;

	public NodeCoreCollection() {
		m_collectedObjects = new HashMap<String, E>();
	}
	
	public NodeCoreCollection(NodeCoreCollection <E> old) {
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

	private String nodeCoreToString(int nodeId, int coreId) {
		return nodeId + ":" + coreId;
	}
}
