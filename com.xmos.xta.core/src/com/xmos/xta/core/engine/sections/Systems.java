package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.libs.core.utils.ByteArrayManipulation;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class Systems extends Section {
	private Collection<String> m_functions;

	Systems(CoreId coreId, Notifier notifier) {
		super(coreId, notifier);
		m_functions = new ArrayList<String>();
	}

	Collection<String> get() {
		return m_functions;
	}

	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(".xtasystem", getCoreId());

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
	        		String functionName = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += functionName.length() + 1;

	        		String fileName = ByteArrayManipulation.readString(data, byteIndex);
	        		byteIndex += fileName.length() + 1;

	        		ByteArrayManipulation.readInt(data, byteIndex); // Skip line number
	        		byteIndex += 4;

	        		m_functions.add(functionName);
	        	}
		    }
        } catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"problem reading .xtasystem while loading executable"));
        	return false;
        }
        return true;
	}
}
