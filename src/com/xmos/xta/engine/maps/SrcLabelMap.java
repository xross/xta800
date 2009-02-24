package com.xmos.xta.engine.maps;

import java.util.Map;
import java.util.TreeMap;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Engine;

public class SrcLabelMap {
	private static final boolean DEBUG = false;
	
	private int m_nodeId;
	private int m_coreId;
	private String m_sectionName;
	private TreeMap<String, SrcLabelInfo> m_labels;
	
	public SrcLabelMap(int nodeId, int coreId, String sectionName) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_sectionName = sectionName;
		m_labels = new TreeMap<String, SrcLabelInfo>(); 
	}
	
	public int getNodeId() {
		return m_nodeId;
	}

	public int getCoreId() {
		return m_coreId;
	}
	
	public Map<String, SrcLabelInfo> getMap() {
		return m_labels;
	}
	
	public boolean load(Engine engine, XeLoader xeLoader) {
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
        		int location = ByteArrayManipulation.readInt(data, byteIndex);
        		byteIndex += 4;
        		
        		String label = ByteArrayManipulation.readString(data, byteIndex);
        		byteIndex += label.length() + 1;

        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
        		byteIndex += fileName.length() + 1;
        		
        		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex);
        		byteIndex += 4;
        		        		
        		if (DEBUG) System.out.printf("- label: %s, pc: 0x%08x, fileName: %s, lineNumber: %d\n", 
        				label, location, fileName, lineNumber);
        		
        		m_labels.put(label, new SrcLabelInfo(location, label, compilationDir, fileName, lineNumber));        	        		
        	}
        }
        return true;
	}
}
