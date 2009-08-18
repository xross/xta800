package com.xmos.xta.engine.sections;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map;
import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class Branches extends Section {	
	private HashMap<Long, TreeSet<Long>> m_branches;

	Branches(int nodeId, int coreId, Notifier notifier) {
		super(nodeId, coreId, notifier);

		m_branches = new HashMap<Long, TreeSet<Long>>(); 
	}
	
	boolean add(CommandSource src, long fromPc, Collection<Long> toPcs) {
		if (!m_branches.containsKey(fromPc)) {
			TreeSet<Long> pcs = new TreeSet<Long>();
			pcs.addAll(toPcs);
			m_branches.put(fromPc, pcs);
			return true;
			
		} else {
			for (long toPc : toPcs) {
				if (m_branches.get(fromPc).contains(toPc)) {
					getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
							"unable to add branch: 0x%x, 0x%x", fromPc, toPc));					
					return false;				
				}				
			}
		    m_branches.get(fromPc).addAll(toPcs);				    
	        return true;
		}	
	}
	
	boolean remove(CommandSource src, long fromPc) {
		if (m_branches.containsKey(fromPc)) {
			m_branches.remove(fromPc);
			return true;
		} else {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
					"unable to remove branch: 0x%x", fromPc));		
			return false;			
		}
	}

	
	Map<Long, TreeSet<Long>> get() {
		return m_branches;
	}
		
	boolean load(CommandSource src, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(".xtabranch", getNodeId(), getCoreId());

				
		if ((data.length % 8) != 0) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, "invalid size for .xtabranch section");
			return false;
		}

		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {        	
	        	long fromPc = XtaInstruction.makeLongPc(ByteArrayManipulation.readInt(data, byteIndex));
	        	byteIndex += 4;        	
	        	long toPc = XtaInstruction.makeLongPc(ByteArrayManipulation.readInt(data, byteIndex));
	        	byteIndex += 4;
	        	
	        	Collection<Long> toPcs = new HashSet<Long>();
	        	toPcs.add(toPc);
	    		add(src, fromPc, toPcs);
	        }        
		} catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
			    	"problem reading .xtabranch section while loading executable"));
    	    return false;
        }        
        return true;
	}	
}
