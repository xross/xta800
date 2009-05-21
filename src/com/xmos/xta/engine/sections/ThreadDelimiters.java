package com.xmos.xta.engine.sections;

import java.util.Collection;
import java.util.TreeSet;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;

public class ThreadDelimiters {
	private static final boolean DEBUG = false;
	
	private int m_nodeId;
	private int m_coreId;
	private String m_sectionName;
	private Notifier m_notifier;	
	private Collection<Integer> m_pcs;

	ThreadDelimiters(int nodeId, int coreId, String sectionName, Notifier notifier) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_sectionName = sectionName;
		m_notifier = notifier;
		m_pcs = new TreeSet<Integer>(); 
	}
	
	int getNodeId() {
		return m_nodeId;
	}

	int getCoreId() {
		return m_coreId;
	}
	
	Collection<Integer> getPcs() {
		return m_pcs;
	}
	
	boolean load(XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(
				m_sectionName, m_nodeId, m_coreId);
				
		if (DEBUG) System.out.printf("%s is of size %d bytes for node: %d, core: %d\n",
		    m_sectionName, data.length, m_nodeId, m_coreId);
		
		if ((data.length % 4) != 0) {
			m_notifier.notify(Notifier.NOTIFY_ERROR, String.format(
					"Invalid size for %s section", m_sectionName));
			return false;
		}

		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {        	
	        	int pc = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;        	

	    		if (DEBUG) System.out.printf("- pc: 0x%08x\n", pc);        	
	    		m_pcs.add(pc);
	        }        
		} catch (Exception e) {
		    m_notifier.notify(Notifier.NOTIFY_ERROR, String.format(
			    	"Problem reading %s section while loading executable", m_sectionName));
    	    return false;
        }        
        return true;
	}		
}
