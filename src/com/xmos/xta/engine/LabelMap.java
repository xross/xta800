package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.Map;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;

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
	
	boolean loadLabels(Engine engine, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(
				".xtainfo", m_nodeId, m_coreId);
				
		if (DEBUG) System.out.println("\n\n.xtainfo is of size " + data.length + " bytes for node " + m_nodeId + " core: " + m_coreId);
		int byteIndex = 0;
        while (byteIndex < data.length) {
        	// Skip version
        	readInt(data, byteIndex);
        	int size = readInt(data, byteIndex + 4);

        	int index = 8;
        	String compilationDir = readString(data, byteIndex + index);
    		index += compilationDir.length() + 1;

        	while (index < size) {
        		String labelName = readString(data, byteIndex + index);
        		index += labelName.length() + 1;
        		
        		int lineNumber = readInt(data, byteIndex + index);
        		index += 4;
        		
        		String fileName = readString(data, byteIndex + index);
        		index += fileName.length() + 1;
        		
        		CodeReference ref = CodeReferenceFactory.createCodeReference(
        				String.format("%s:%d", fileName, lineNumber));
        		
        		if (ref.resolve(engine, m_nodeId, m_coreId)) {
        			if (!m_labels.containsKey(labelName)) {
        				if (DEBUG)  System.out.println("found");
        				m_labels.put(labelName, ref.getPc());
        			} else {
        				String error = "Duplicate label '" +labelName + "' found.";
        				if (DEBUG)  System.out.println(error);
        				//engine.callDisplayers(Displayer.NOTIFY_ERROR, error);
            	    	//plough on regardless
        				//FIXME (BUG: 4437)
        				//return false;
        			}
        	    } else {
        	    	String error = "Unable to resolve label " + labelName;
        	    	if (DEBUG)  System.out.println(error);
        	    	//engine.callDisplayers(Displayer.NOTIFY_ERROR, error);
        	    	//plough on regardless
        	    	//FIXME (BUG: 4437)
        	    	//return false;
        	    }
        	}
        	byteIndex += size;
        }        
        return true;
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
        StringBuffer sb = new StringBuffer();
        int index = offset;
        while (data[index] != 0) {
        	sb.append((char)data[index]);
        	++index;
        }
        return sb.toString();
    }
}
