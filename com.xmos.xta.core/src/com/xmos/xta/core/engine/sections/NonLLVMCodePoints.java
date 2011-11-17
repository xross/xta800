package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;

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
		m_codePoints = new ArrayList<CodePointInfo>();
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
				if(Debug) System.out.println(byteIndex + "\tcompilationDir " + compilationDir);
		   		byteIndex += compilationDir.length() + 1;

		       	while (byteIndex < size) {
		    		int location = ByteArrayManipulation.readInt(data, byteIndex);
					if(Debug) System.out.println(byteIndex + "\tlocation " + location);
		    		byteIndex += 4;

		    		String codePoint = ByteArrayManipulation.readString(data, byteIndex);
					if(Debug) System.out.println(byteIndex + "\tcodePoint " + codePoint);
		    		byteIndex += codePoint.length() + 1;

		    		String fileName = ByteArrayManipulation.readString(data, byteIndex);
					if(Debug) System.out.println(byteIndex + "\tfileName " + fileName);
		    		byteIndex += fileName.length() + 1;

		    		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex);
					if(Debug) System.out.println(byteIndex + "\tlineNumber " + lineNumber);
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
