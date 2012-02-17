package com.xmos.xta.core.engine.sections;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class NonLLVMCodePoints extends CodePoints  {
	private String m_sectionName;
	private static final boolean Debug = false;

	NonLLVMCodePoints(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_sectionName = sectionName;
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
	
	
	
	
}
