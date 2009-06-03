package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.Map;

public class NodeCoreCollection<E> {
	private Map<String, E> m_collectedObjects;
	
	public NodeCoreCollection(){
		m_collectedObjects = new HashMap<String, E>();
	}
	
	public void add(int nodeId, int coreId, E newObject){
		m_collectedObjects.put(nodeCoreToString(nodeId, coreId), newObject);
	}
	
	public E get(int nodeId, int coreId){
		return m_collectedObjects.get(nodeCoreToString(nodeId, coreId));
	}
	
	private String nodeCoreToString(int nodeId, int coreId){
		return nodeId + ":" + coreId;
	}	
}
