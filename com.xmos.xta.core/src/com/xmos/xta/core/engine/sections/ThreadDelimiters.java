package com.xmos.xta.core.engine.sections;

import java.util.Collection;

import com.xmos.libs.instructiondecoder.Instruction;
import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public class ThreadDelimiters extends Section {
	private String m_sectionName;
	private Collection<Long> m_pcs;

	ThreadDelimiters(CoreId coreId, String sectionName, Notifier notifier) {
		super(coreId, notifier);
		m_sectionName = sectionName;
		m_pcs = new IteratableSet<Long>();
	}

	boolean add(CommandSource src, long pc) {
		if (m_pcs.add(pc)) {
			return true;
		} else {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"unable to add pc: 0x%08x", pc));
			return false;
		}
	}
	/*
	boolean remove(CommandSource src, int pc) {
		if (m_pcs.remove(pc)) {
			return true;
		} else {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
    				"unable to remove pc: %s", pc));
			return false;
		}
	}
	*/
	Collection<Long> get() {
		return m_pcs;
	}

	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getCoreId());

		if ((data.length % 4) != 0) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"invalid size for %s section", m_sectionName));
			return false;
		}

		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {
	        	long pc = Instruction.makeLongPc(ByteArrayManipulation.readInt(data, byteIndex));
	        	byteIndex += 4;

	    		m_pcs.add(pc);
	        }
		} catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
			    	"problem reading %s section while loading executable", m_sectionName));
    	    return false;
        }
        return true;
	}
}
