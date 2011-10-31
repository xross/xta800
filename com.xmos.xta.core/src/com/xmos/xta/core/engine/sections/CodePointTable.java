package com.xmos.xta.core.engine.sections;

import java.io.File;
import java.util.Map;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class CodePointTable extends Section {
	private String m_sectionName;
	private Map<Long, CodePointTableInfo> m_locations;
	private Map<Long, File> m_fileMap;
	
	CodePointTable(CoreId coreId, String sectionName, Notifier notifier, FileTable ft) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_locations = new IteratableMap<Long, CodePointTableInfo>();
		m_fileMap = ft.getFileMap();
	}

	Map<Long, CodePointTableInfo> get() {
		return m_locations;
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());
		int byteIndex = 0;
		
		ByteArrayManipulation.readInt(data, byteIndex);
    	byteIndex += 4;

    	// Ignores the version field.
   		int version = ByteArrayManipulation.readInt(data, byteIndex);
   		byteIndex += 4;
		
		if(version == 5){
			return loadLLVM(src, xeLoader);
		} else {
			return loadNonLLVM(src, xeLoader);
		}
	}
	boolean loadLLVM(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());

		try {
			
			int byteIndex = 0;
			while (byteIndex < data.length) {
			int entriesSize = ByteArrayManipulation.readInt(data, byteIndex);
    		byteIndex += 4;
        	// Ignores the version field.
        	ByteArrayManipulation.readInt(data, byteIndex);
        	byteIndex += 4;
			
	        while (byteIndex < entriesSize) {
	        	
	        	String lblName = ByteArrayManipulation.readString(data, byteIndex);
        		byteIndex += lblName.length() + 1;
        		
        		long fileLbl = ByteArrayManipulation.readInt(data, byteIndex);
        		byteIndex += 4;
        		
        		File f = m_fileMap.get(fileLbl);
        		
        		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex);
        		byteIndex += 4;
        		
        		//int fileCol = 
        		ByteArrayManipulation.readInt(data, byteIndex);
        		byteIndex += 4;
        		
        		int subSize = ByteArrayManipulation.readInt(data, byteIndex);
        		byteIndex += 4;
        		int entriesEnd = byteIndex + subSize;
	           	while (byteIndex < entriesEnd) {
	           		
	           		int locationLbl = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;
	        		
	        		String codePoint = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += codePoint.length() + 1;
	        		m_locations.put((long) locationLbl, new CodePointTableInfo(this, locationLbl, f.getAbsolutePath(), f.getName(), lineNumber));
       
	        	}
		    }
			 }
        } catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"problem reading %s section while loading executable", m_sectionName));
        	return false;
        }
        return true;
	}
	boolean loadNonLLVM(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());

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

	        		m_locations.put(location, new CodePointTableInfo(this, location, compilationDir, fileName, lineNumber));
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
