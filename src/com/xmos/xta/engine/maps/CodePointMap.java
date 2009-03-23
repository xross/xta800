package com.xmos.xta.engine.maps;

import java.util.Map;
import java.util.TreeMap;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;

public class CodePointMap {
	private static final boolean DEBUG = false;
	
	private Engine m_engine;
	private int m_nodeId;
	private int m_coreId;
	private String m_sectionName;
	private TreeMap<String, CodePointInfo> m_codePoints;
	private String m_type;
	
	public CodePointMap(int nodeId, int coreId, String sectionName, Engine engine, String type) {
		m_engine = engine;
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_sectionName = sectionName;
		m_codePoints = new TreeMap<String, CodePointInfo>();
		m_type = type;
	}
	
	public int getNodeId() {
		return m_nodeId;
	}

	public int getCoreId() {
		return m_coreId;
	}
	
	public Map<String, CodePointInfo> getMap() {
		return m_codePoints;
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
        		
        		String codePoint = ByteArrayManipulation.readString(data, byteIndex);
        		byteIndex += codePoint.length() + 1;

        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
        		byteIndex += fileName.length() + 1;
        		
        		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex);
        		byteIndex += 4;
        		        		
        		if (DEBUG) System.out.printf("- label: %s, pc: 0x%08x, fileName: %s, lineNumber: %d\n", 
        				codePoint, location, fileName, lineNumber);
        		
        		if (validateCodePoint(codePoint, location)) {
            		m_codePoints.put(codePoint, new CodePointInfo(location, codePoint, compilationDir, fileName, lineNumber));        	        		
        		} else {
        			m_codePoints.clear();
        			return false;
        		}
        	}
        }
        return true;
	}
	
	private boolean validateCodePoint(String codePoint, int location) {
		if (m_codePoints.containsKey(codePoint)) {
			CodePointInfo info = m_codePoints.get(codePoint);
			
			if (info.getPc() == location) {
				m_engine.notify(Notifier.NOTIFY_WARNING, 
						String.format("Duplicate %s: %s at pc: 0x%x", m_type, codePoint, location));
				return true; 
			} else {
				m_engine.notify(Notifier.NOTIFY_ERROR, 
						String.format("Duplicate %s: %s", m_type, codePoint, location));
				return false; 				
			}				
		}
		return true;
	}
}
