package com.xmos.xta.core.engine.sections;

import java.util.Map;
import java.util.TreeMap;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class CodePointTable extends Section {
	private String m_sectionName;
	private TreeMap<Long, CodePointTableInfo> m_locations;
	
	CodePointTable(int nodeId, int coreId, String sectionName, Notifier notifier) {
		super(nodeId, coreId, notifier);
		m_sectionName = sectionName;
		m_locations = new TreeMap<Long, CodePointTableInfo>(); 
	}
	
	Map<Long, CodePointTableInfo> get() {
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

	        		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;

	        		long location = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;
	        		
	        		m_locations.put(location, new CodePointTableInfo(location, compilationDir, fileName, lineNumber));        	        		
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
