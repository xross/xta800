package com.xmos.xta.core.engine.sections;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import com.xmos.libs.util.ByteArrayManipulation;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;

public class Branches extends Section {	
	private HashMap<Long, Set<BranchInfo>> m_branches;

	Branches(int nodeId, int coreId, Notifier notifier) {
		super(nodeId, coreId, notifier);

		m_branches = new HashMap<Long, Set<BranchInfo>>(); 
	}
	
	boolean add(CommandSource src, long fromPc, Collection<Long> toPcs, boolean isUser) {
		Set<BranchInfo> pcs = null;
		if (!m_branches.containsKey(fromPc)) {
			pcs = new TreeSet<BranchInfo>();
			m_branches.put(fromPc, pcs);

		} else {
		    pcs = m_branches.get(fromPc);
		}	

		for (long newPc : toPcs) {
			for (BranchInfo info : pcs) {
				if (info.getPc() == newPc) {
					getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
							"unable to add duplicate branch: 0x%x, 0x%x", fromPc, newPc));					
					return false;				
				}
			}
		}
		for (long newPc : toPcs) {
			pcs.add(new BranchInfo(newPc, isUser));
		}
		return true;
	}
	
	boolean remove(CommandSource src, Reference fromRef, Collection<Reference> toRefs) {
		boolean hit = false;
		for (Long fromPc : m_branches.keySet()) {
			if (fromRef.matches(fromPc)) {
				Set<BranchInfo> toRemove = new HashSet<BranchInfo>();
				for (BranchInfo info : m_branches.get(fromPc)) {
					if (info.isUser()) {
						for (Reference toRef : toRefs) {
							if (toRef.matches(info.getPc())) {
								toRemove.add(info);
								hit = true;
							}
						}
					}
				}
				remove(fromPc, toRemove);
			}
		}
		if (!hit) {
			String message = String.format("unable to remove branch: %s", fromRef.toString());
			for (Reference toRef : toRefs) {
				message += String.format(" %s", toRef.toString());
			}
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, message);
			return false;			
		}
		return hit;
	}

	private void remove(long fromPc, Set<BranchInfo> toRemove) {
		assert(m_branches.containsKey(fromPc));
		Set<BranchInfo> toPcs = m_branches.get(fromPc);
		toPcs.removeAll(toRemove);
		if (toPcs.isEmpty()) {
			m_branches.remove(toPcs);
		}
	}

	Map<Long, Set<BranchInfo>> get() {
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
	    		add(src, fromPc, toPcs, false);
	        }        
		} catch (Exception e) {
			getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
			    	"problem reading .xtabranch section while loading executable"));
			e.printStackTrace(System.out);
    	    return false;
        }        
        return true;
	}	
}
