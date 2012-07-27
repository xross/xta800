package com.xmos.xta.core.engine.sections;

import com.xmos.libs.core.utils.ByteArrayManipulation;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class CodePointTable extends Section {
	protected IteratableSet<CodePointTableInfo> m_locations;
	private String m_sectionName;
	private static final boolean Debug = false;

	CodePointTable(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_locations = new IteratableSet<CodePointTableInfo>();
		m_sectionName = sectionName;
	}

	IteratableSet< CodePointTableInfo> get() {
		return m_locations;
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
			if(Debug) System.out.println(byteIndex + "\tsize " + size);
			byteIndex += 4;

			int version = ByteArrayManipulation.readInt(data, byteIndex);
			if(Debug) System.out.println(byteIndex + "\tversion " + version);
			byteIndex += 4;
			if(Debug) System.out.println("data.length " + data.length);
			if(Debug) System.out.println("byteIndex " + byteIndex);


       		String compilationDir = ByteArrayManipulation.readString(data, byteIndex);
			if(Debug) System.out.println(byteIndex + "\tcompilationDir " + compilationDir);
       		byteIndex += compilationDir.length() + 1;

           	while (byteIndex < (base + size)) {
        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
    			if(Debug) System.out.println(byteIndex + "\tfileName " + fileName);
        		byteIndex += fileName.length() + 1;

        		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex);
    			if(Debug) System.out.println(byteIndex + "\tlineNumber " + lineNumber);
        		byteIndex += 4;

        		long location = ByteArrayManipulation.readInt(data, byteIndex);
    			if(Debug) System.out.println(byteIndex + "\tlocation " + location);
        		byteIndex += 4;

        		m_locations.add( new CodePointTableInfo(this, location, compilationDir, fileName, lineNumber));
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
