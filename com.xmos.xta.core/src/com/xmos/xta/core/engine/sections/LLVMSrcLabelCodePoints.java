package com.xmos.xta.core.engine.sections;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class LLVMSrcLabelCodePoints extends CodePoints {
	private String m_sectionName;

	LLVMSrcLabelCodePoints(CoreId coreId, String sectionName, Notifier notifier) {
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

                    int lineNumber = ByteArrayManipulation.readInt(data,byteIndex);
                    byteIndex += 4;
                    
                    byteIndex += 4; // columnNumber
                    
                    int fileRangeCount = ByteArrayManipulation.readInt(data, byteIndex);
                    byteIndex += 4;
                    
                    int entryIndex = 0;
                    while (entryIndex < fileRangeCount) {
                        String rangeFileName = ByteArrayManipulation.readString(data,byteIndex);
                        byteIndex += rangeFileName.length() + 1;
                        
                        String rangeCompDir = ByteArrayManipulation.readString(data,byteIndex);
                        byteIndex += rangeCompDir.length() + 1;
                        
                        byteIndex += 4; // fromLine
                        byteIndex += 4; // fromCol
                        byteIndex += 4; // toLine
                        byteIndex += 4; // toCol

                        entryIndex++;
                    }
                    
                    int asmLabelSize = ByteArrayManipulation.readInt(data, byteIndex);
                    entryIndex = byteIndex;
                    byteIndex += 4;
                    
                    if (byteIndex < asmLabelSize + entryIndex) {
                        while (byteIndex < asmLabelSize + entryIndex) {     
                            
                            long address = (long)ByteArrayManipulation.readInt(data,byteIndex);
                            byteIndex += 4;
                            
                            String inlineInfo = ByteArrayManipulation.readString(data,byteIndex);                        
                            byteIndex += inlineInfo.length() + 1;

                            if (!labelName.equals("")) {
                                m_codePoints.add(new CodePointInfo(this, address,
                                        labelName, compDir, fileName, lineNumber, true));
                            }
                        }

                    } else {
                        // No asm entries associated with this label. Therefore we can 
                        // assume that the compiler has optimised this away.
                        if (!labelName.equals("")) {
                            m_codePoints.add(new CodePointInfo(this, 0,
                                    labelName, compDir, fileName, lineNumber, false));
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
}
