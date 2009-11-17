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
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class Branches extends Section {	
	private HashMap<Reference, Set<BranchInfo>> m_branches;

	Branches(int nodeId, int coreId, Notifier notifier) {
		super(nodeId, coreId, notifier);

		m_branches = new HashMap<Reference, Set<BranchInfo>>(); 
	}
	
	boolean add(CommandSource src, Reference from, Collection<Reference> to, boolean isUser) {
		Set<BranchInfo> pcs = null;
		if (!m_branches.containsKey(from)) {
			pcs = new TreeSet<BranchInfo>();
			m_branches.put(from, pcs);

		} else {
		    pcs = m_branches.get(from);
		}	

		for (Reference ref : to) {
			for (BranchInfo info : pcs) {
				if (info.getRef().equals(ref)) {
					getNotifier().notify(src, Notifier.NOTIFY_ERROR, String.format(
							"unable to add duplicate branch: %s, %s", from.toString(), ref.toString()));					
					return false;
				}
			}
		}
		for (Reference ref : to) {
			pcs.add(new BranchInfo(ref, isUser));
		}
		return true;
	}
	
	boolean remove(CommandSource src, Reference fromRef, Collection<Reference> toRefs) {
		boolean hit = false;
		for (Reference ref : m_branches.keySet()) {
			if (ref.equals(fromRef)) {
				hit |= removeFrom(m_branches.get(ref), toRefs);
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

	private boolean removeFrom(Set<BranchInfo> set, Collection<Reference> toRefs) {
		boolean hit = false;
		Set<BranchInfo> toRemove = new HashSet<BranchInfo>();
		for (BranchInfo info : set) {
			if (info.isUser()) {
				for (Reference toRef : toRefs) {
					if (info.getRef().equals(toRef)) {
						toRemove.add(info);
						hit = true;
					}
				}
			}
		}
		set.removeAll(toRemove);
		return hit;
	}

	Map<Reference, Set<BranchInfo>> get() {
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
	        	
	        	Collection<Reference> toRefs = new HashSet<Reference>();
	    		Reference toRef = ReferenceFactory.createReference(String.format("0x%x", toPc), ReferenceClass.PC_REF);
	        	toRefs.add(toRef);
	    		Reference fromRef = ReferenceFactory.createReference(String.format("0x%x", fromPc), ReferenceClass.PC_REF);
	    		add(src, fromRef, toRefs, false);
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
