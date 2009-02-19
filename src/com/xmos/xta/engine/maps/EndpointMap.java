package com.xmos.xta.engine.maps;

import java.util.HashMap;
import java.util.Map;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Engine;

public class EndpointMap {
	private static final boolean DEBUG = false;
	
	private int m_nodeId;
	private int m_coreId;
	private HashMap<String, Integer> m_endpoints;
	
	public EndpointMap(int nodeId, int coreId) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_endpoints = new HashMap<String, Integer>(); 
	}
	
	public int getNodeId() {
		return m_nodeId;
	}

	public int getCoreId() {
		return m_coreId;
	}
	
	public Map<String, Integer> getEndpoints() {
		return m_endpoints;
	}
	
	public boolean load(Engine engine, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(
				".xtaendpoint", m_nodeId, m_coreId);
				
		if (DEBUG) System.out.println("\n\n.xtaendpoint is of size " + data.length + " bytes for node " + m_nodeId + " core: " + m_coreId);
		int byteIndex = 0;
        while (byteIndex < data.length) {        	
        	int pc = ByteArrayManipulation.readInt(data, byteIndex);
        	byteIndex += 4;

        	String label = ByteArrayManipulation.readString(data, byteIndex);
        	byteIndex += label.length() + 1;
        	
    		if (DEBUG) System.out.printf("- label: %s, pc: 0x%08xz\n", label, pc);        	
    		m_endpoints.put(label, pc);        	
        }        
        return true;
	}
}
