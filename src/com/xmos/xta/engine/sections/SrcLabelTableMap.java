package com.xmos.xta.engine.sections;

import java.util.Map;
import java.util.TreeMap;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;

public class SrcLabelTableMap {
	private static final boolean DEBUG = false;
	
	private int m_nodeId;
	private int m_coreId;
	private String m_sectionName;
	private Notifier m_notifier;	
	private TreeMap<Integer, SrcLabelTableInfo> m_locations;
	
	SrcLabelTableMap(int nodeId, int coreId, String sectionName, Notifier notifier) {
		m_notifier = notifier;
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_sectionName = sectionName;
		
		m_locations = new TreeMap<Integer, SrcLabelTableInfo>(); 
	}
	
	int getNodeId() {
		return m_nodeId;
	}

	int getCoreId() {
		return m_coreId;
	}
	
	Map<Integer, SrcLabelTableInfo> getMap() {
		return m_locations;
	}
	
	boolean load(XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(
				m_sectionName, m_nodeId, m_coreId);
				
		if (DEBUG) System.out.printf("%s: section size: %d, node: %d, core: %d\n",
				m_sectionName, data.length, m_nodeId, m_coreId);

		try {
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

	        		int lineNumberStart = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;

	        		int lineNumberEnd = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;
	        		
	        		int location = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;
	        		
	        		if (DEBUG) System.out.printf("- pc: 0x%08x, fileName: %s, lineNumber: %d\n", 
	        				location, fileName, lineNumberStart, lineNumberEnd);
	        		
	        		m_locations.put(location, new SrcLabelTableInfo(location, compilationDir, 
	        				fileName, lineNumberStart, lineNumberEnd));        	        		
	        	}
	        }
	    } catch (Exception e) {
		    m_notifier.notify(Notifier.NOTIFY_ERROR, String.format(
					"Problem reading %s section while loading executable", m_sectionName));
	        return false;
		}
        return true;
	}
}
