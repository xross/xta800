package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.Map;

import com.xmos.util.ByteArrayManipulation;
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
        	ByteArrayManipulation.readInt(data, byteIndex);
        	int size = ByteArrayManipulation.readInt(data, byteIndex + 4);

        	int index = 8;
        	String compilationDir = ByteArrayManipulation.readString(data, byteIndex + index);
    		index += compilationDir.length() + 1;

        	while (index < size) {
        		String labelName = ByteArrayManipulation.readString(data, byteIndex + index);
        		index += labelName.length() + 1;
        		
        		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex + index);
        		index += 4;
        		
        		String fileName = ByteArrayManipulation.readString(data, byteIndex + index);
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
        				engine.notify(Notifier.NOTIFY_ERROR, error);
        				return false;
        			}
        	    } else {
        	    	String error = "Unable to resolve label " + labelName;
        	    	if (DEBUG)  System.out.println(error);
    				engine.notify(Notifier.NOTIFY_ERROR, error);
        	    	return false;
        	    }
        	}
        	byteIndex += size;
        }        
        return true;
	}
}
