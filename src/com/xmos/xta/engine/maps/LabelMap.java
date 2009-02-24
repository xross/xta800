package com.xmos.xta.engine.maps;

import java.util.Map;
import java.util.TreeMap;

import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Engine;

public class LabelMap {	
	private int m_nodeId;
	private int m_coreId;
	private TreeMap<String, Integer> m_labels;
	
	public LabelMap(int nodeId, int coreId) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_labels = new TreeMap<String, Integer>(); 
	}
	
	public int getNodeId() {
		return m_nodeId;
	}

	public int getCoreId() {
		return m_coreId;
	}
	
	public Map<String, Integer> getMap() {
		return m_labels;
	}
	
	public boolean load(Engine engine, XeLoader xeLoader) {
        return true;
	}	
}
