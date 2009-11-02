package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.PatternSyntaxException;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class CodePoints extends Section  {
	private String m_sectionName;
	private TreeMap<String, CodePointInfo> m_codePoints;
	private String m_type;
	
	CodePoints(int nodeId, int coreId, String sectionName, Notifier notifier, String type) {
		super(nodeId, coreId, notifier);
		m_sectionName = sectionName;
		m_codePoints = new TreeMap<String, CodePointInfo>();
		m_type = type;
	}
	
	boolean add(CommandSource src, String label, int pc) {
		if (!m_codePoints.containsKey(label)) {										
			m_codePoints.put(label, new CodePointInfo(pc, label, "", "", 0));
			return true;
		} else {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"label already exists: %s", label));
			return false;
		}		
	}
	
	boolean remove(CommandSource src, String label) {
		List<String> matches = new ArrayList<String>();
		for (String mapEntry : m_codePoints.keySet()) {
			try {
				if (mapEntry.matches(label)) {
					matches.add(mapEntry);
				}
			} catch (PatternSyntaxException e) {
			}
		}

		if (matches.size() > 0) {
			for (String toRemove : matches) {
				m_codePoints.remove(toRemove);
			}
			return true;
		} else {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"unable to remove label: %s", label));
			return false;
		}		
	}
	
	Map<String, CodePointInfo> get() {
		return m_codePoints;
	}
	
	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getNodeId(), getCoreId());
				
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
	        		        		
	        		if (validateCodePoint(src, codePoint, location)) {
	            		m_codePoints.put(codePoint, new CodePointInfo(location, codePoint, compilationDir, fileName, lineNumber));        	        		
	        		} else {
	        			m_codePoints.clear();
	        			return false;
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
	
	private boolean validateCodePoint(CommandSource src, String codePoint, int location) {
		if (m_codePoints.containsKey(codePoint)) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, 
					String.format("duplicate %s: %s at pc: 0x%x", m_type, codePoint, location));
			return false; 				
		}
		return true;
	}
}
