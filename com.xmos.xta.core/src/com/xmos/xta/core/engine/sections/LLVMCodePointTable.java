package com.xmos.xta.core.engine.sections;

import java.io.File;
import java.util.Map;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class LLVMCodePointTable extends CodePointTable {
	private String m_sectionName;
	private FileTable m_fileTable;
	
	private static final boolean Debug = false;
	
	LLVMCodePointTable(CoreId coreId, String sectionName, Notifier notifier, FileTable ft) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_fileTable = ft;
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
				if(Debug) System.out.println("data.length " + data.length);
				if(Debug) System.out.println("byteIndex " + byteIndex);

				int endOfEntry = byteIndex + size - 4;
				if(Debug) System.out.println("endOfEntry " + endOfEntry);
				
					while (byteIndex < endOfEntry) {
							if(Debug) System.out.println("----------------------------");
							
							String lblName = ByteArrayManipulation.readString(data,byteIndex);
							if(Debug) System.out.println(byteIndex + "\tlblName " + lblName);
							byteIndex += lblName.length() + 1;
			
							long fileLbl = ByteArrayManipulation.readInt(data,byteIndex);
							File f = m_fileTable.getFileMap().get(fileLbl);
							if(Debug) System.out.println(byteIndex + "\tfileLbl " + fileLbl + " "+ f.getName());
							byteIndex += 4;
			
							int lineNumber = ByteArrayManipulation.readInt(data,byteIndex);
							if(Debug) System.out.println(byteIndex + "\tlineNumber " + lineNumber);
							byteIndex += 4;
			
							int fileCol = ByteArrayManipulation.readInt(data, byteIndex);
							if(Debug) System.out.println(byteIndex + "\tfileCol " + fileCol);
							byteIndex += 4;
			
							int subSize = ByteArrayManipulation.readInt(data, byteIndex);
							if(Debug) System.out.println(byteIndex + "\tsubSize " + subSize);
							byteIndex += 4;
							
							int entriesEnd = byteIndex + subSize;
							
							while (byteIndex < entriesEnd) {
								if(Debug) System.out.println("====================================");
								int locationLbl = ByteArrayManipulation.readInt(data,byteIndex);
								if(Debug) System.out.println(byteIndex + "\tlocationLbl " + locationLbl);
								byteIndex += 4;
			
								String codePoint = ByteArrayManipulation.readString(data, byteIndex);
								if(Debug) System.out.println(byteIndex + "\tcodePoint " + codePoint);
								byteIndex += codePoint.length() + 1;
								String path = m_fileTable.getCompilationDir().getAbsolutePath();
								String fileName = f.getAbsolutePath().replace(path, "");
								if(fileName.startsWith("/")){
									fileName = fileName.replaceFirst("/", "");
								}
								m_locations.put((long) locationLbl, new CodePointTableInfo(this, locationLbl, 
										path, fileName, lineNumber));
								if(Debug) System.out.println("====================================");
							}
							if(Debug) System.out.println("----------------------------");
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
