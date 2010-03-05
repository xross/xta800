package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class CodePoints extends Section  {
	private String m_sectionName;
	private List<CodePointInfo> m_codePoints;

	CodePoints(int nodeNumber, int coreNumber, String sectionName, Notifier notifier) {
		super(nodeNumber, coreNumber, notifier);
		m_sectionName = sectionName;
		m_codePoints = new ArrayList<CodePointInfo>();
	}

	boolean add(CommandSource src, String label, int pc) {
		m_codePoints.add(new CodePointInfo(this, pc, label, "", "", 0));
		return true;
	}

	boolean remove(CommandSource src, String label) {
		boolean found = false;
		Iterator<CodePointInfo> iter = m_codePoints.iterator();
		while (iter.hasNext()) {
			CodePointInfo codePointInfo = iter.next();
			try {
				if (codePointInfo.getLabel().matches(label)) {
					iter.remove();
					found = true;
				}
			} catch (PatternSyntaxException e) {
			}
		}

		if (!found) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"unable to remove label: %s", label));
			return false;
		}
		return true;
	}

	List<CodePointInfo> get() {
		return m_codePoints;
	}

	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getNodeNumber(), getCoreNumber());

		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {
	        	int base = byteIndex;

	        	int size = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;

	        	// Ignores the version field.
	       		ByteArrayManipulation.readInt(data, byteIndex);
	       		byteIndex += 4;

	       		String compilationDir = ByteArrayManipulation.readString(data, byteIndex);
	       		byteIndex += compilationDir.length() + 1;

	           	while (byteIndex < (base + size)) {
	        		int location = ByteArrayManipulation.readInt(data, byteIndex);
	        		byteIndex += 4;

	        		String codePoint = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += codePoint.length() + 1;

	        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += fileName.length() + 1;

	        		int lineNumber = ByteArrayManipulation.readInt(data, byteIndex);
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
