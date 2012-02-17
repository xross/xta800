package com.xmos.xta.core.engine.sections;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class LLVMCodePoints extends CodePoints  {
	private String m_sectionName;
	private static final boolean Debug = false;
	
	LLVMCodePoints(CoreId coreId, String sectionName, 
			Notifier notifier) {
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

					int asmLabelSize = ByteArrayManipulation.readInt(data, byteIndex);
					if(Debug) System.out.println(byteIndex + "\tasmLabelSize " + asmLabelSize);
					int entryIndex = byteIndex;
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
									inlineInfo + lblName, compDir, fileName, lineNumber));
						
						
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
