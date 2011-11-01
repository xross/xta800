package com.xmos.xta.core.engine.sections;

import java.io.File;
import java.util.Map;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class FileTable extends Section {
	private String m_sectionName;
	private Map<Long, File> m_files;

	FileTable(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_files = new NonIteratableMap<Long, File>();
	}

	Map<Long, File> get() {
		return m_files;
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
		
		byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());
		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {
	        	
	        	int size = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;
	        	
	        	//int version = 
	        	ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;
	        	int tableEnd = byteIndex + size;
	           	while (byteIndex < tableEnd-4) {
	           		long address = byteIndex;
	        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += fileName.length() + 1;
	        		m_files.put(address, new File(fileName));
	        	}
		    }
        } catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"problem reading %s section while loading executable", m_sectionName));
        	return false;
        }
        return true;
	}

	public Map<Long, File> getFileMap() {
		return m_files;
	}
	
}
