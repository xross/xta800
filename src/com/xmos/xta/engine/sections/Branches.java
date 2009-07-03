package com.xmos.xta.engine.sections;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map;
import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;

public class Branches extends Section {	
	private HashMap<Integer, TreeSet<Integer>> m_branches;

	Branches(int nodeId, int coreId, Notifier notifier) {
		super(nodeId, coreId, notifier);

		m_branches = new HashMap<Integer, TreeSet<Integer>>(); 
	}
	
	boolean add(int fromPc, int toPc) {
		if (!m_branches.containsKey(fromPc)) {
			TreeSet<Integer> toPcs = new TreeSet<Integer>();
			toPcs.add(toPc);
			m_branches.put(fromPc, toPcs);
			return true;
			
		} else {
			if (!m_branches.get(fromPc).contains(toPc)) {
			    m_branches.get(fromPc).add(toPc);
			    return true;
			} else {
				getNotifier().notify(Notifier.NOTIFY_ERROR, String.format(
						"Unable to add branch: 0x%x, 0x%08x", fromPc, toPc));					
				return false;				
			}
		}	
	}
	
	boolean remove(int fromPc) {
		if (m_branches.containsKey(fromPc)) {
			m_branches.remove(fromPc);
			return true;
		} else {
			getNotifier().notify(Notifier.NOTIFY_ERROR, String.format(
					"Unable to remove branch: 0x%x", fromPc));		
			return false;			
		}
	}

	
	Map<Integer, TreeSet<Integer>> get() {
		return m_branches;
	}
		
	boolean load(XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(".xtabranch", getNodeId(), getCoreId());

				
		if ((data.length % 8) != 0) {
			getNotifier().notify(Notifier.NOTIFY_ERROR, "Invalid size for .xtabranch section");
			return false;
		}

		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {        	
	        	int fromPc = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;        	
	        	int toPc = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;
	        	
	    		add(fromPc, toPc);
	        }        
		} catch (Exception e) {
			getNotifier().notify(Notifier.NOTIFY_ERROR, String.format(
			    	"Problem reading .xtabranch section while loading executable"));
    	    return false;
        }        
        return true;
	}	
}
