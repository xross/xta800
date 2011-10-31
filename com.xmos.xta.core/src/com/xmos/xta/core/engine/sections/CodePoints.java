package com.xmos.xta.core.engine.sections;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class CodePoints extends Section  {
	private String m_sectionName;
	private List<CodePointInfo> m_codePoints;
	private Map<Long, File> m_fileMap;
	
	CodePoints(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_codePoints = new ArrayList<CodePointInfo>();
	}
	
	CodePoints(CoreId coreId, String sectionName, Notifier notifier, FileTable ft) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_codePoints = new ArrayList<CodePointInfo>();
		m_fileMap = ft.getFileMap();
	}

	List<CodePointInfo> get() {
		return m_codePoints;
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
	        		int location = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;

	        		String codePoint = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += codePoint.length() + 1;

	        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += fileName.length() + 1;

	        		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;

            		m_codePoints.add(new CodePointInfo(this, location, codePoint, compilationDir, fileName, lineNumber));
	        	}
		    }
        } catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"problem reading %s section while loading executable", m_sectionName));
        	return false;
        }
        return true;
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
	        		
            		m_codePoints.add(new CodePointInfo(this, locationLbl, codePoint + lblName, 
            				f.getAbsolutePath(), f.getName(), lineNumber));
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
}
