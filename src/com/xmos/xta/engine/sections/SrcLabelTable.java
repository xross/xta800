package com.xmos.xta.engine.sections;

import java.util.Map;
import java.util.TreeMap;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Notifier;

public class SrcLabelTable extends Section {
	private String m_sectionName;
	private TreeMap<Integer, SrcLabelTableInfo> m_locations;
	
	SrcLabelTable(int nodeId, int coreId, String sectionName, Notifier notifier) {
		super(nodeId, coreId, notifier);
		m_sectionName = sectionName;		
		m_locations = new TreeMap<Integer, SrcLabelTableInfo>(); 
	}
	
	Map<Integer, SrcLabelTableInfo> get() {
		return m_locations;
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getNodeId(), getCoreId());
				
		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {
	        	int base = byteIndex;
	        	
	        	int size = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;

	        	// Ignores the version field.
	       		ByteArrayManipulation.readInt(data, byteIndex);
	       		byteIndex += 4;
	        		        		
	       		String compilationDir = ByteArrayManipulation.readString(data, byteIndex);
	       		byteIndex += compilationDir.length() + 1;

	           	while (byteIndex < (base + size)) {        		
	        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += fileName.length() + 1;

	        		int lineNumberStart = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;

	        		int lineNumberEnd = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;
	        		
	        		int location = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;
	        		
	        		m_locations.put(location, new SrcLabelTableInfo(location, compilationDir, 
	        				fileName, lineNumberStart, lineNumberEnd));        	        		
	        	}
	        }
	    } catch (Exception e) {
		    getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"problem reading %s section while loading executable", m_sectionName));
	        return false;
		}
        return true;
	}
}
