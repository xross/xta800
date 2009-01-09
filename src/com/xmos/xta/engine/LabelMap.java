package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.Map;

public class LabelMap {
	private int m_nodeId;
	private int m_coreId;
	private HashMap<String, Integer> m_labels;
	
	LabelMap(int nodeId, int coreId) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_labels = new HashMap<String, Integer>(); 
	}
	
	int getNodeId() {
		return m_nodeId;
	}

	int getCoreId() {
		return m_coreId;
	}
	
	Map<String, Integer> getLabels() {
		return m_labels;
	}
}
