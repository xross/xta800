package com.xmos.xta.core.engine.sections;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class LLVMSrcLabelTable extends Section {
	private String m_sectionName;
	private Map<String, LLVMSrcLabelTableInfo> m_locations;
	private Map<Long, File> m_fileMap;
	
	LLVMSrcLabelTable(CoreId coreId, String sectionName, Notifier notifier, FileTable ft) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_locations = new IteratableMap<String, LLVMSrcLabelTableInfo>();
		m_fileMap = ft.getFileMap();
	}

	Map<String, LLVMSrcLabelTableInfo> get() {
		return m_locations;
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());
		
    	
		try {
			int byteIndex = 0;
			int size = ByteArrayManipulation.readInt(data, byteIndex);
        	byteIndex += 4;
        	int version = ByteArrayManipulation.readInt(data, byteIndex);
        	byteIndex += 4;
        	
	        while (byteIndex < size) {

	        	String label = ByteArrayManipulation.readString(data, byteIndex);
	       		byteIndex += label.length() + 1;
	       		
	        	int count = ByteArrayManipulation.readInt(data, byteIndex);
				byteIndex += 4;
	        	int locCount = 0;
	           	while (locCount < count) {
	        		long fileLbl = ByteArrayManipulation.readInt(data, byteIndex);
		        	byteIndex += 4;
		        	
		        	File file = m_fileMap.get(fileLbl);
		        	
	        		int fromLine = ByteArrayManipulation.readInt(data, byteIndex);
		        	byteIndex += 4;
	        		int fromCol = ByteArrayManipulation.readInt(data, byteIndex);
		        	byteIndex += 4;
	        		int toLine = ByteArrayManipulation.readInt(data, byteIndex);
		        	byteIndex += 4;
	        		int toCol = ByteArrayManipulation.readInt(data, byteIndex);
		        	byteIndex += 4;
		        	LLVMSrcLabelTableInfo slti  = 
		        		new LLVMSrcLabelTableInfo(file.getAbsolutePath(), file.getName(), 
		        				fromLine, toLine);
		        	
		        	int commaIndex = label.lastIndexOf(",", 0);
		        	String xtaLabel;
		        	if(commaIndex == -1){
		        		xtaLabel = label;
		        	} else {
		        		xtaLabel = label.substring(commaIndex+1);
		        	}

		        	m_locations.put(xtaLabel, slti);
		        	locCount++;
	        	}
	           	int locSize = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;
	        }
	    } catch (Exception e) {
		    getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"problem reading %s section while loading executable", m_sectionName));
	        return false;
		}

	    // BUG 6471: A compiler bug means that basic blocks overlap when they shouldn't
	    // Here we ensure this overlapping is removed as the rest of the tool assumes the
	    // blocks don't overlap.
	    detectAndCorrectOverlap();

        return true;
	}
	
	
	private void detectAndCorrectOverlap() {
		List<LLVMSrcLabelTableInfo> entries = new ArrayList<LLVMSrcLabelTableInfo>();
		entries.addAll(m_locations.values());
		Collections.sort(entries);

		int index = 0;
		while (index < entries.size()) {
			index = detectForFile(entries, index);
    	}
	}

	private int detectForFile(List<LLVMSrcLabelTableInfo> entries, int index) {
		LLVMSrcLabelTableInfo entry = entries.get(index);
		String thisFile = entry.getFileName();

		index++;
		while (index < entries.size()) {
			LLVMSrcLabelTableInfo nextEntry = entries.get(index);
			if (!nextEntry.getFileName().equals(thisFile)) {
				return index;
			} else {
				if (entry.getLineNumberEnd() > nextEntry.getLineNumberStart()) {
					entry.setLineNumberEnd(nextEntry.getLineNumberStart() - 1);
				}
			}
			entry = nextEntry;
			index++;
		}
		return index;
	}
}
