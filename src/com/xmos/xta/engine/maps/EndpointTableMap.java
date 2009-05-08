package com.xmos.xta.engine.maps;

import java.util.Map;
import java.util.TreeMap;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;

public class EndpointTableMap {
	private static final boolean DEBUG = false;
	
	private int m_nodeId;
	private int m_coreId;
	private String m_sectionName;
	private TreeMap<Integer, EndpointTableInfo> m_locations;
	
	public EndpointTableMap(int nodeId, int coreId, String sectionName) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_sectionName = sectionName;
		m_locations = new TreeMap<Integer, EndpointTableInfo>(); 
	}
	
	public int getNodeId() {
		return m_nodeId;
	}

	public int getCoreId() {
		return m_coreId;
	}
	
	public Map<Integer, EndpointTableInfo> getMap() {
		return m_locations;
	}
	
	public boolean load(XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(
				m_sectionName, m_nodeId, m_coreId);
				
		if (DEBUG) System.out.printf("%s: section size: %d, node: %d, core: %d\n",
				m_sectionName, data.length, m_nodeId, m_coreId);

		int byteIndex = 0;
        while (byteIndex < data.length) {
        	int base = byteIndex;
        	
        	int size = ByteArrayManipulation.readInt(data, byteIndex);
        	byteIndex += 4;

       		int version = ByteArrayManipulation.readInt(data, byteIndex);
       		byteIndex += 4;
        		        		
       		String compilationDir = ByteArrayManipulation.readString(data, byteIndex);
       		byteIndex += compilationDir.length() + 1;

    		if (DEBUG) System.out.printf("size: %d, version: %s, compilationDir: %s\n",
    				size, version, compilationDir);
       		
           	while (byteIndex < (base + size)) {        		
        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
        		byteIndex += fileName.length() + 1;

        		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex);
        		byteIndex += 4;

        		int location = ByteArrayManipulation.readInt(data, byteIndex);
        		byteIndex += 4;
        		
        		if (DEBUG) System.out.printf("- pc: 0x%08x, fileName: %s, lineNumber: %d\n", 
        				location, fileName, lineNumber);
        		
        		m_locations.put(location, new EndpointTableInfo(location, compilationDir, fileName, lineNumber));        	        		
        	}
        }
        return true;
	}
}
