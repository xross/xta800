package com.xmos.xta.engine.sections;

import java.util.Collection;
import java.util.TreeSet;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;

public class ThreadDelimiters extends Section {
	private String m_sectionName;
	private Collection<Integer> m_pcs;

	ThreadDelimiters(int nodeId, int coreId, String sectionName, Notifier notifier) {
		super(nodeId, coreId, notifier);
		m_sectionName = sectionName;
		m_pcs = new TreeSet<Integer>(); 
	}
	
	boolean add(int pc) {
		if (m_pcs.add(pc)) {
			return true;	
		} else {
			getNotifier().notify(Notifier.NOTIFY_ERROR, String.format(
					"Unable to add pc: 0x%08x", pc));			
			return false;
		}
	}
	
	boolean remove(int pc) {
		if (m_pcs.remove(pc)) {
			return true;
		} else {
			getNotifier().notify(Notifier.NOTIFY_ERROR, String.format(
    				"Unable to remove pc: %s", pc));
			return false;
		}		
	}
	
	Collection<Integer> get() {
		return m_pcs;
	}
	
	boolean load(XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(m_sectionName, getNodeId(), getCoreId());
				
		if ((data.length % 4) != 0) {
			getNotifier().notify(Notifier.NOTIFY_ERROR, String.format(
					"Invalid size for %s section", m_sectionName));
			return false;
		}

		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {        	
	        	int pc = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;        	

	    		m_pcs.add(pc);
	        }        
		} catch (Exception e) {
			getNotifier().notify(Notifier.NOTIFY_ERROR, String.format(
			    	"Problem reading %s section while loading executable", m_sectionName));
    	    return false;
        }        
        return true;
	}		
}
