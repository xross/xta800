package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.List;

import com.xmos.libs.core.utils.ByteArrayManipulation;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class CodePoints extends Section  {

    private static final boolean DEBUG = false;

    private List<CodePointInfo> m_codePoints;
	private String m_sectionName;
	private boolean m_addRemovedEntries;
	private int m_byteIndex;
	private byte[] m_data;

	CodePoints(CoreId coreId, String sectionName, Notifier notifier, boolean addRemovedEntries) {
		super(coreId, notifier);
		m_codePoints = new ArrayList<CodePointInfo>();
		m_sectionName = sectionName; 
		m_addRemovedEntries = addRemovedEntries;
	}

	List<CodePointInfo> get() {
		return m_codePoints;
	}

	private void loadCompilationUnitVersion0(int base, int size) throws Exception {
   		String compilationDir = ByteArrayManipulation.readString(m_data, m_byteIndex);
   		m_byteIndex += compilationDir.length() + 1;

       	while (m_byteIndex < (base + size)) {
    		int location = ByteArrayManipulation.readInt(m_data, m_byteIndex);
    		m_byteIndex += 4;

    		String codePoint = ByteArrayManipulation.readString(m_data, m_byteIndex);
    		m_byteIndex += codePoint.length() + 1;

    		String fileName = ByteArrayManipulation.readString(m_data, m_byteIndex);
    		m_byteIndex += fileName.length() + 1;

    		int lineNumber = ByteArrayManipulation.readInt(m_data, m_byteIndex);
    		m_byteIndex += 4;

    		m_codePoints.add(new CodePointInfo(this, location, codePoint, compilationDir, fileName, lineNumber, true));
    	}
	}

	private void loadCompilationUnitVersion1(int base, int size) throws Exception {
		int endOfEntry = base + size;
		if(DEBUG) System.out.println("endOfEntry " + endOfEntry);

   		String compDir = ByteArrayManipulation.readString(m_data, m_byteIndex);
   		m_byteIndex += compDir.length() + 1;

		while (m_byteIndex < endOfEntry) {
			if(DEBUG) System.out.println("----------------------------");
			
			String labelName = ByteArrayManipulation.readString(m_data,m_byteIndex);
			if(DEBUG) System.out.println(m_byteIndex + "\tlblName " + labelName);
			m_byteIndex += labelName.length() + 1;

			String fileName = ByteArrayManipulation.readString(m_data,m_byteIndex);
			if(DEBUG) System.out.println(m_byteIndex + "\tfileName " + fileName);
			m_byteIndex += fileName.length() + 1;

			int lineNumber = ByteArrayManipulation.readInt(m_data,m_byteIndex);
			if(DEBUG) System.out.println(m_byteIndex + "\tlineNumber " + lineNumber);
			m_byteIndex += 4;

			int asmLabelSize = ByteArrayManipulation.readInt(m_data, m_byteIndex);
			if(DEBUG) System.out.println(m_byteIndex + "\tasmLabelSize " + asmLabelSize);
			m_byteIndex += 4;

			int entryIndex = m_byteIndex;
			if (m_byteIndex < entryIndex + asmLabelSize) {
				do {
					if(DEBUG) System.out.println("====================asm entry================");
					
					long asmLabel = (long)ByteArrayManipulation.readInt(m_data,m_byteIndex);
					if(DEBUG) System.out.println(m_byteIndex + "\tasmLable " + asmLabel);
					m_byteIndex += 4;
					
					String inlineInfo = ByteArrayManipulation.readString(m_data,m_byteIndex);
					if(DEBUG) System.out.println(m_byteIndex + "\tinlineInfo " + inlineInfo);
					m_byteIndex += inlineInfo.length() + 1;

					if(!labelName.equals(""))
						m_codePoints.add(new CodePointInfo(this, asmLabel,
								inlineInfo + labelName, compDir, fileName, lineNumber, true));

					if(DEBUG) System.out.println("====================================");
				} while (m_byteIndex < asmLabelSize + entryIndex);
			} else if (m_addRemovedEntries && !labelName.equals("")) {
                // No asm entries associated with this label. Therefore we can 
                // assume that the compiler / mapper has optimised this away.
                m_codePoints.add(new CodePointInfo(this, 0,
                        labelName, compDir, fileName, lineNumber, false));
			}
			if(DEBUG) System.out.println("----------------------------");
		}
	}

	private void loadCompilationUnit() throws Exception {
    	int base = m_byteIndex;
		int size = ByteArrayManipulation.readInt(m_data, m_byteIndex);
		if(DEBUG) System.out.println(m_byteIndex + "\tsize " + size);
		m_byteIndex += 4;
		int version = ByteArrayManipulation.readInt(m_data, m_byteIndex);
		if(DEBUG) System.out.println(m_byteIndex + "\tversion " + version);
		m_byteIndex += 4;
		switch (version) {
		case 0:
			loadCompilationUnitVersion0(base, size);
			return;
		case 1:
			loadCompilationUnitVersion1(base, size);
			return;
		default:
			throw new Exception();
		}
	}

	boolean load(CommandSource src, XeLoader xeLoader) {
		m_data = xeLoader.getSectionData(m_sectionName, getCoreId());
		if(DEBUG) System.out.println("m_sectionName " + m_sectionName);
		
		if(DEBUG) System.out.println("data.length " + m_data.length);
		if (m_data.length == 0) {
			return true;
		}
		try {
			m_byteIndex = 0;
			while (m_byteIndex < m_data.length) {
				loadCompilationUnit();
			}
			if (m_byteIndex != m_data.length) {
				throw new Exception();
			}
		} catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"problem reading %s section while loading executable", m_sectionName));
	    	return false;
	    }
		return true;
	}
}
