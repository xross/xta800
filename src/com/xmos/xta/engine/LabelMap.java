package com.xmos.xta.engine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.xmos.xeloader.XeLoader;

public class LabelMap {
	
	private static final boolean DEBUG = false;
	
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
	
	void loadLabels(Engine engine, XeLoader xeLoader) {		
		byte[] data = xeLoader.getSectionData(
				".xtainfo", m_nodeId, m_coreId);
				
		if (DEBUG) System.out.println(".xtainfo is of size " + data.length + " bytes");
		int byteIndex = 0;
        while (byteIndex < data.length) {
        	// Skip version
        	readInt(data, byteIndex);
        	int size = readInt(data, byteIndex + 4);

        	int index = 8;
        	String basePath = readString(data, byteIndex + index);
    		index += basePath.length() + 1;

        	while (index < size) {
        		String labelName = readString(data, byteIndex + index);
        		index += labelName.length() + 1;
        		
        		int lineNumber = readInt(data, byteIndex + index);
        		index += 4;
        		
        		String fileName = readString(data, byteIndex + index);
        		index += fileName.length() + 1;
        		fileName = basePath + File.separator + fileName;
        		
        		CodeReference ref = new CodeReference(fileName, lineNumber);
        		
        		if (DEBUG) System.out.println(fileName + " " + lineNumber);
        		
        		if (ref.resolve(engine, m_nodeId, m_coreId)) {
        			m_labels.put(labelName, ref.getPc());
        	    } else {
        	    	System.out.println("WARNING: unable to resolve label " +
        	    			fileName + ":" + lineNumber);
        	    }
        	}
        	byteIndex += size;
        }
	}
	
    final int readInt(byte[] data, int offset) {
        int l = 0;
        if ((offset + 4) >= data.length)
        	return l;
        
        for (int i = 0; i < 4; i++) {
            l = l | (data[offset + i]&0xff) << (i*8);
        }
        return l;
    }
    
    final String readString(byte[] data, int offset) {
        String s = "";
        int index = offset;
        while (data[index] != 0) {
        	s += (char)data[index];
        	++index;
        }
        return s;
    }
}
