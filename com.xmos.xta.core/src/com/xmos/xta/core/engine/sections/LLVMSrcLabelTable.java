package com.xmos.xta.core.engine.sections;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class LLVMSrcLabelTable extends SrcLabelTable {
	private String m_sectionName;

	private static final boolean Debug = true;
	LinkedList<Integer> lineCover;
	LLVMSrcLabelTable(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		lineCover = new LinkedList<Integer>();
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
						if(Debug) System.out.println("----------------------------");
						
						String lblName = ByteArrayManipulation.readString(data,byteIndex);
						byteIndex += lblName.length() + 1;

						String fileName = ByteArrayManipulation.readString(data,byteIndex);
						byteIndex += fileName.length() + 1;
						
						String compDir = ByteArrayManipulation.readString(data,byteIndex);
						byteIndex += compDir.length() + 1;

						int lineNumber = ByteArrayManipulation.readInt(data,byteIndex);
						byteIndex += 4;

						int fileCol = ByteArrayManipulation.readInt(data, byteIndex);
						byteIndex += 4;

						
						if(Debug) {
							if(lblName.equals("")){
								System.out.println(fileName + "\tline:" +lineNumber + "\tcol" +fileCol + "\tEMPTY ");
							} else {
								System.out.println(fileName + "\tline:" +lineNumber + "\tcol" +fileCol + "\t" + lblName + " ");
							}
						}
						
						
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
							
							System.out.println("\t" + Range_fileName + "\tfrom line:" +Range_fromLine + "\tto line" +Range_toLine);

							if(Range_fileName.equals(fileName)){
								FileLoc fl = new FileLoc(Range_fileName, Range_compDir, 
										Range_fromLine, 
										Range_fromCol,
										Range_toLine,
										Range_toCol);
								FileRanges.add(fl);
							} else {
								System.out.println("bad");
								
								if(lblName.length() == 0){
									//compiler generated
								} else {
									//put out a warning that the file names don't match
								}
							}
							
							entryIndex++;
						}
						
						
						
						int asmLabelSize = ByteArrayManipulation.readInt(data, byteIndex);
						entryIndex = byteIndex;
						byteIndex += 4;
						
						boolean hasAsmEntry = false;
						while (byteIndex < asmLabelSize + entryIndex) {
							
							long asmLable = (long)ByteArrayManipulation.readInt(data,byteIndex);
							byteIndex += 4;
							
							String inlineInfo = ByteArrayManipulation.readString(data,byteIndex);
							byteIndex += inlineInfo.length() + 1;

							//for each fileRange add an entry
							for(FileLoc fl : FileRanges){
								m_locations.put(asmLable, 
										new SrcLabelTableInfo(this, asmLable, 
												compDir, fileName,
												fl.getFromLine(), fl.getToLine()));
								hasAsmEntry = true;
								if(fileName.equals("UartReceive.xc")){
									for(int l=fl.getFromLine(); l<=fl.getFromLine(); l++){
										if(!lineCover.contains(l))
											lineCover.add(l);
									}
								}
							}
							
						}
						if(Debug) System.out.println("hasAsmEntry: " + hasAsmEntry);
						if(Debug) System.out.println("----------------------------");
					}
				
			}
		} catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"problem reading %s section while loading executable", m_sectionName));
	    	return false;
	    }
		Collections.sort(lineCover);
		for(int l:lineCover){
			System.out.println(l);
		}
		
		return true;
	}

	

}
