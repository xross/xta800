package com.xmos.xta.core.engine.sections;

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

public class SrcLabelTable extends Section {
	private String m_sectionName;
	private Map<Long, SrcLabelTableInfo> m_locations;
	private LLVMSrcLabelTable m_llvmSrcLabelTable;
	
	SrcLabelTable(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_locations = new IteratableMap<Long, SrcLabelTableInfo>();
	}

	SrcLabelTable(CoreId coreId, String sectionName, Notifier notifier, LLVMSrcLabelTable slt) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_locations = new IteratableMap<Long, SrcLabelTableInfo>();
		m_llvmSrcLabelTable = slt;
	}

	Map<Long, SrcLabelTableInfo> get() {
		return m_locations;
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
		
		
		//if(isUsingLLVM){
		//	return loadLLVM(src, xeLoader);
		//} else {
			return loadNonLLVM(src, xeLoader);
		//}
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

	        		int lineNumberStart = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;

	        		int lineNumberEnd = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;

	        		long location = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;

	        		m_locations.put(location, new SrcLabelTableInfo(this, location, compilationDir,
	        				fileName, lineNumberStart, lineNumberEnd));
	        	}
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

	boolean loadLLVM(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());

		try {
			int byteIndex = 0;
			while (byteIndex < data.length) {
				int version = ByteArrayManipulation.readInt(data, byteIndex);
				byteIndex += 4;
				int size = ByteArrayManipulation.readInt(data, byteIndex);
				byteIndex += 4;
				int base = byteIndex;

				while (byteIndex < (base + size)) {
					String s = ByteArrayManipulation
							.readString(data, byteIndex);
					byteIndex += s.length() + 1;
					long location = ByteArrayManipulation.readInt(data, byteIndex);
					byteIndex += 4;
					
					Map<String, LLVMSrcLabelTableInfo> map = m_llvmSrcLabelTable.get();
					LLVMSrcLabelTableInfo entry = map.get(s);
					 m_locations.put(location, new SrcLabelTableInfo(this,
					 location, entry.getCompilationDir(),
					 entry.getFileName(), entry.getLineNumberStart(), entry.getLineNumberEnd()));
				}
			}
		} catch (Exception e) {
			getNotifier()
					.notify(src,
							Notifier.NOTIFY_ERROR,
							String.format(
									"problem reading %s section while loading executable",
									m_sectionName));
			return false;
		}

		// BUG 6471: A compiler bug means that basic blocks overlap when they
		// shouldn't
		// Here we ensure this overlapping is removed as the rest of the tool
		// assumes the
		// blocks don't overlap.
		detectAndCorrectOverlap();

		return true;}
	
	
	private void detectAndCorrectOverlap() {
		List<SrcLabelTableInfo> entries = new ArrayList<SrcLabelTableInfo>();
		entries.addAll(m_locations.values());
		Collections.sort(entries);

		int index = 0;
		while (index < entries.size()) {
			index = detectForFile(entries, index);
    	}
	}

	private int detectForFile(List<SrcLabelTableInfo> entries, int index) {
		SrcLabelTableInfo entry = entries.get(index);
		String thisFile = entry.getFileName();

		index++;
		while (index < entries.size()) {
			SrcLabelTableInfo nextEntry = entries.get(index);
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
