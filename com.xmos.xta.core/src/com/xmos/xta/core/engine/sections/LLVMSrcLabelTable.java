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

public class LLVMSrcLabelTable extends SrcLabelTable {
	private String m_sectionName;
	private FileTable m_fileTable;

	private static final boolean Debug = false;
	
	LLVMSrcLabelTable(CoreId coreId, String sectionName, Notifier notifier, FileTable ft) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_fileTable = ft;
	}
	
	class FileLoc {
		private File m_file;
		private int m_fromLine, m_fromCol, m_toLine, m_toCol;
		public FileLoc(File file, int fromLine, 
				int fromCol, int toLine, int toCol){
			m_fromLine = fromLine;
			m_fromCol = fromCol;
			m_toLine = toLine;
			m_toCol = toCol;
			m_file = file;
		}
		public File getFile() {
			return m_file;
		}
		public int getFromLine() {
			return m_fromLine;
		}
		public int getFromCol() {
			return m_fromCol;
		}
		public int getToLine() {
			return m_toLine;
		}
		public int getToCol() {
			return m_toCol;
		}
		
		
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
					if(Debug) System.out.println(byteIndex + "\tlblName " + lblName + "|");
					byteIndex += lblName.length() + 1;

					int fileLocs = ByteArrayManipulation.readInt(data,byteIndex);
					if(Debug) System.out.println(byteIndex + "\tfileLocs " + fileLocs);
					byteIndex += 4;

					List<FileLoc> fileLocList = new ArrayList<FileLoc>();
					
					for(int fl=0;fl < fileLocs;fl++){
						if(Debug) System.out.println("================= file ===================");
						long fileLbl =  ByteArrayManipulation.readInt(data,byteIndex);
						File f = m_fileTable.getFileMap().get(fileLbl);
						if(Debug) System.out.println(byteIndex + "\tfileLbl " + fileLbl + " "+ f.getName());
						byteIndex += 4;
						
						int FromLine = ByteArrayManipulation.readInt(data,byteIndex);
						if(Debug) System.out.println(byteIndex + "\tFromLine " + FromLine);
						byteIndex += 4;
						int ToLine = ByteArrayManipulation.readInt(data,byteIndex);
						if(Debug) System.out.println(byteIndex + "\tToLine " + ToLine);
						byteIndex += 4;
						int FromCol = ByteArrayManipulation.readInt(data,byteIndex);
						if(Debug) System.out.println(byteIndex + "\tFromCol " + FromCol);
						byteIndex += 4;
						int ToCol = ByteArrayManipulation.readInt(data,byteIndex);
						if(Debug) System.out.println(byteIndex + "\tToCol " + ToCol);
						byteIndex += 4;
						
						FileLoc fileLoc = new FileLoc(f, FromLine, FromCol, ToLine, ToCol);
						fileLocList.add(fileLoc);
						if(Debug) System.out.println("================= eof  ===================");
					}
					int subSize = ByteArrayManipulation.readInt(data, byteIndex);
					if(Debug) System.out.println(byteIndex + "\tsubSize " + subSize);
					byteIndex += 4;
					
					int entriesEnd = byteIndex + subSize;
					
					while (byteIndex < entriesEnd) {
						if(Debug) System.out.println("================== label ==================");
						long locationLbl = ByteArrayManipulation.readInt(data,byteIndex);
						if(Debug) System.out.println(byteIndex + "\tlocationLbl " + locationLbl);
						byteIndex += 4;

						String InlineInfo = ByteArrayManipulation.readString(data, byteIndex);
						if(Debug) System.out.println(byteIndex + "\tInlineInfo " + InlineInfo);
						byteIndex += InlineInfo.length() + 1;
						
						
						for(FileLoc fl : fileLocList){
							
							String path = m_fileTable.getCompilationDir().getAbsolutePath();
							String fileName = fl.getFile().getAbsolutePath().replace(path, "");
							if(fileName.startsWith("/")){
								fileName = fileName.replaceFirst("/", "");
							}
							
							m_locations.put(locationLbl, 
									new SrcLabelTableInfo(this, locationLbl, 
											path, fileName,
											fl.getFromLine(), fl.getToLine()));
						}
						if(Debug) System.out.println("================ eol ====================");
					}

					if(Debug) System.out.println("----------------------------");
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
