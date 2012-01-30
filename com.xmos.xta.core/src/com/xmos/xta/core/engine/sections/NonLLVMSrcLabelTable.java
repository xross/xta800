package com.xmos.xta.core.engine.sections;

import java.util.Map;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class NonLLVMSrcLabelTable extends SrcLabelTable {
	private String m_sectionName;
	private Map<Long, SrcLabelTableInfo> m_locations;

	private static final boolean Debug = false;
	
	NonLLVMSrcLabelTable(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_locations = new IteratableMap<Long, SrcLabelTableInfo>();
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());
		if(Debug) System.out.println("m_sectionName " + m_sectionName);
		if(Debug) System.out.println("data.length " + data.length);
		if (data.length == 0) {
			return true;
		}
		try {
			int byteIndex = 0;
			while (byteIndex < data.length) {
				int size = ByteArrayManipulation.readInt(data, byteIndex);
				if(Debug) System.out.println(byteIndex + "\tsize " + size);
				byteIndex += 4;
	
				int version = ByteArrayManipulation.readInt(data, byteIndex);
				if(Debug) System.out.println(byteIndex + "\tversion " + version);
				byteIndex += 4;
	
				String compilationDir = ByteArrayManipulation.readString(data, byteIndex);
		   		byteIndex += compilationDir.length() + 1;
				if(Debug) System.out.println(byteIndex + "\tcompilationDir " + compilationDir);

		       	while (byteIndex < size) {
		    		String fileName = ByteArrayManipulation.readString(data, byteIndex);
					if(Debug) System.out.println(byteIndex + "\tfileName " + fileName);
		    		byteIndex += fileName.length() + 1;

		    		int lineNumberStart = ByteArrayManipulation.readInt(data, byteIndex);
					if(Debug) System.out.println(byteIndex + "\tlineNumberStart " + lineNumberStart);
		    		byteIndex += 4;

		    		int lineNumberEnd = ByteArrayManipulation.readInt(data, byteIndex);
					if(Debug) System.out.println(byteIndex + "\tlineNumberEnd " + lineNumberEnd);
		    		byteIndex += 4;

		    		long location = ByteArrayManipulation.readInt(data, byteIndex);
					if(Debug) System.out.println(byteIndex + "\tlocation " + location);
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
}
