package com.xmos.xta.core.engine.sections;

import java.util.LinkedList;
import java.util.List;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class LLVMSrcLabelTable extends SrcLabelTable {
	private String m_sectionName;

	LLVMSrcLabelTable(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_sectionName = sectionName;
	}
		
	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());
		if (data.length == 0) {
			return true;
		}
		try {
			int byteIndex = 0;
			while (byteIndex < data.length) {
				int size = ByteArrayManipulation.readInt(data, byteIndex);
				byteIndex += 4;
				
				ByteArrayManipulation.readInt(data, byteIndex);
				byteIndex += 4;

				int endOfEntry = byteIndex + size - 4;				
                while (byteIndex < endOfEntry) {
                    
                    String labelName = ByteArrayManipulation.readString(data,byteIndex);
                    byteIndex += labelName.length() + 1;

                    String fileName = ByteArrayManipulation.readString(data,byteIndex);
                    byteIndex += fileName.length() + 1;
                    
                    String compDir = ByteArrayManipulation.readString(data,byteIndex);
                    byteIndex += compDir.length() + 1;

                    byteIndex += 4; // lineNumber
                    byteIndex += 4; // columnNumber

                    int fileRangeCount = ByteArrayManipulation.readInt(data, byteIndex);
                    byteIndex += 4;
                    
                    int entryIndex = 0;
                    List<FileLoc> FileRanges = new LinkedList<FileLoc>();
                    while (entryIndex < fileRangeCount) {
                        String Range_fileName = ByteArrayManipulation.readString(data,byteIndex);
                        byteIndex += Range_fileName.length() + 1;
                        
                        String Range_compDir = ByteArrayManipulation.readString(data,byteIndex);
                        byteIndex += Range_compDir.length() + 1;

                        int Range_fromLine = ByteArrayManipulation.readInt(data,byteIndex);
                        byteIndex += 4;

                        int Range_fromCol = ByteArrayManipulation.readInt(data, byteIndex);
                        byteIndex += 4;
                        
                        int Range_toLine = ByteArrayManipulation.readInt(data,byteIndex);
                        byteIndex += 4;

                        int Range_toCol = ByteArrayManipulation.readInt(data, byteIndex);
                        byteIndex += 4;
                        
                        if (Range_fileName.equals(fileName)) {
                            FileLoc fl = new FileLoc(Range_fileName, Range_compDir, 
                                    Range_fromLine, 
                                    Range_fromCol,
                                    Range_toLine,
                                    Range_toCol);
                            FileRanges.add(fl);                            
                        }                        
                        entryIndex++;
                    }
                    
                    int asmLabelSize = ByteArrayManipulation.readInt(data, byteIndex);
                    entryIndex = byteIndex;
                    byteIndex += 4;
                    
                    while (byteIndex < asmLabelSize + entryIndex) {
                        
                        long asmLable = (long)ByteArrayManipulation.readInt(data,byteIndex);
                        byteIndex += 4;
                        
                        String inlineInfo = ByteArrayManipulation.readString(data,byteIndex);
                        byteIndex += inlineInfo.length() + 1;

                        for (FileLoc fl : FileRanges) {
                            m_locations.add(new SrcLabelTableInfo(this, asmLable, 
                                            compDir, fileName,
                                            fl.getFromLine(), fl.getToLine()));
                        }                        
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
	
    private static class FileLoc {
        private int m_fromLine;
        private int m_toLine;
        
        public FileLoc(String file, String comp, int fromLine, 
                int fromCol, int toLine, int toCol){
            m_fromLine = fromLine;
            m_toLine = toLine;
        }
        
        public int getFromLine() {
            return m_fromLine;
        }
        
        public int getToLine() {
            return m_toLine;
        }
    }
}
