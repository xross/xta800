package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public abstract class SrcLabelTable extends Section {
	protected Map<Long, SrcLabelTableInfo> m_locations;
	
	SrcLabelTable(CoreId coreId, Notifier notifier) {
		super(coreId, notifier);
		m_locations = new IteratableMap<Long, SrcLabelTableInfo>();
	}

	Map<Long, SrcLabelTableInfo> get() {
		return m_locations;
	}
	
	abstract boolean load(CommandSource src, XeLoader xeLoader);
	
	protected void detectAndCorrectOverlap() {
		List<SrcLabelTableInfo> entries = new ArrayList<SrcLabelTableInfo>();
		entries.addAll(m_locations.values());
		Collections.sort(entries);

		int index = 0;
		while (index < entries.size()) {
			index = detectForFile(entries, index);
    	}
	}

	private int detectForFile(List<SrcLabelTableInfo> entries, int index) {
		SrcLabelTableInfo entry = entries.get(index);
		String thisFile = entry.getFileName();

		index++;
		while (index < entries.size()) {
			SrcLabelTableInfo nextEntry = entries.get(index);
			if (!nextEntry.getFileName().equals(thisFile)) {
				return index;
			} else {
				if (entry.getLineNumberEnd() > nextEntry.getLineNumberStart()) {
					entry.setLineNumberEnd(nextEntry.getLineNumberStart() - 1);
				}
			}
			entry = nextEntry;
			index++;
		}
		return index;
	}
}
