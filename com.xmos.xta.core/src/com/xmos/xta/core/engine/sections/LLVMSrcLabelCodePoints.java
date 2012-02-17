package com.xmos.xta.core.engine.sections;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class LLVMSrcLabelCodePoints extends CodePoints {
	private String m_sectionName;

	private static final boolean Debug = false;
	
	LLVMSrcLabelCodePoints(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_sectionName = sectionName;
	}

	class FileLoc {
		private String m_file;
		private String m_compDir;
		private int m_fromLine, m_fromCol, m_toLine, m_toCol;
		public FileLoc(String file, String comp, int fromLine, 
				int fromCol, int toLine, int toCol){
			m_fromLine = fromLine;
			m_fromCol = fromCol;
			m_toLine = toLine;
			m_toCol = toCol;
			m_file = file;
			m_compDir = comp;
		}
		public String getFile() {
			return m_file;
		}
		public String getCompDir() {
			return m_compDir;
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
						if(Debug) System.out.println(byteIndex + "\tlblName " + lblName);
						byteIndex += lblName.length() + 1;

						String fileName = ByteArrayManipulation.readString(data,byteIndex);
						if(Debug) System.out.println(byteIndex + "\tfileName " + fileName);
						byteIndex += fileName.length() + 1;
						
						String compDir = ByteArrayManipulation.readString(data,byteIndex);
						if(Debug) System.out.println(byteIndex + "\tcompDir " + compDir);
						byteIndex += compDir.length() + 1;

						int lineNumber = ByteArrayManipulation.readInt(data,byteIndex);
						if(Debug) System.out.println(byteIndex + "\tlineNumber " + lineNumber);
						byteIndex += 4;

						int fileCol = ByteArrayManipulation.readInt(data, byteIndex);
						if(Debug) System.out.println(byteIndex + "\tfileCol " + fileCol);
						byteIndex += 4;

						int fileRangeCount = ByteArrayManipulation.readInt(data, byteIndex);
						if(Debug) System.out.println(byteIndex + "\tfileRangeCount " + fileRangeCount);
						byteIndex += 4;
						
						FileLoc fl = new FileLoc(fileName, compDir, lineNumber, fileCol, 0, 0);
						
						
						int entryIndex = 0;
						while (entryIndex < fileRangeCount) {
							if(Debug) System.out.println("================file range entry====================");
							String Range_fileName = ByteArrayManipulation.readString(data,byteIndex);
							if(Debug) System.out.println(byteIndex + "\tRange_fileName " + Range_fileName);
							byteIndex += Range_fileName.length() + 1;
							
							String Range_compDir = ByteArrayManipulation.readString(data,byteIndex);
							if(Debug) System.out.println(byteIndex + "\tRange_compDir " + Range_compDir);
							byteIndex += Range_compDir.length() + 1;

							int Range_fromLineNumber = ByteArrayManipulation.readInt(data,byteIndex);
							if(Debug) System.out.println(byteIndex + "\tRange_fromLineNumber " + Range_fromLineNumber);
							byteIndex += 4;

							int Range_fromFileCol = ByteArrayManipulation.readInt(data, byteIndex);
							if(Debug) System.out.println(byteIndex + "\tRange_fromFileCol " + Range_fromFileCol);
							byteIndex += 4;
							
							int Range_toLineNumber = ByteArrayManipulation.readInt(data,byteIndex);
							if(Debug) System.out.println(byteIndex + "\tRange_toLineNumber " + Range_toLineNumber);
							byteIndex += 4;

							int Range_toFileCol = ByteArrayManipulation.readInt(data, byteIndex);
							if(Debug) System.out.println(byteIndex + "\tRange_toFileCol " + Range_toFileCol);
							byteIndex += 4;
							
							if(Debug) System.out.println("====================================");
							entryIndex++;
						}
						
						
						
						int asmLabelSize = ByteArrayManipulation.readInt(data, byteIndex);
						if(Debug) System.out.println(byteIndex + "\tasmLabelSize " + asmLabelSize);
						entryIndex = byteIndex;
						byteIndex += 4;
						
						
						while (byteIndex < asmLabelSize + entryIndex) {
							if(Debug) System.out.println("====================asm entry================");
							
							long asmLable = (long)ByteArrayManipulation.readInt(data,byteIndex);
							if(Debug) System.out.println(byteIndex + "\tasmLable " + asmLable);
							byteIndex += 4;
							
							String inlineInfo = ByteArrayManipulation.readString(data,byteIndex);
							if(Debug) System.out.println(byteIndex + "\tinlineInfo " + inlineInfo);
							byteIndex += inlineInfo.length() + 1;
							if(!lblName.equals(""))
								m_codePoints.add(new CodePointInfo(this, asmLable,
										lblName, compDir, fileName, fl.getFromLine()));
							
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
