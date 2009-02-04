package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;

public class BranchMap {	
	private static final boolean DEBUG = false;
	
	private int m_nodeId;
	private int m_coreId;
	private HashMap<Integer, TreeSet<Integer>> m_branches;
	
	BranchMap(int nodeId, int coreId) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_branches = new HashMap<Integer, TreeSet<Integer>>(); 
	}
	
	int getNodeId() {
		return m_nodeId;
	}

	int getCoreId() {
		return m_coreId;
	}
	
	Map<Integer, TreeSet<Integer>> getBranches() {
		return m_branches;
	}
	
	void add(int fromPc, int toPc) {
		if (!m_branches.containsKey(fromPc)) {
			TreeSet<Integer> toPcs = new TreeSet<Integer>();
			toPcs.add(toPc);
			m_branches.put(fromPc, toPcs);
		} else {
			m_branches.get(fromPc).add(toPc);				
		}		
	}
	
	void remove(int fromPc) {
		if (m_branches.containsKey(fromPc)) {
			m_branches.remove(fromPc);
		}		
	}
	
	boolean loadBranches(Engine engine, XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(
				".xtabranch", m_nodeId, m_coreId);
				
		if (DEBUG) System.out.println(".xtabranch is of size " + data.length + " bytes for node " + m_nodeId + " core: " + m_coreId);
		
		if ((data.length % 8) != 0) {
			engine.notify(Notifier.NOTIFY_ERROR, "Invalid size for .xtabranch section");
			return false;
		}
		
		int byteIndex = 0;
        while (byteIndex < data.length) {        	
        	int fromPc = ByteArrayManipulation.readInt(data, byteIndex);
        	int toPc = ByteArrayManipulation.readInt(data, byteIndex + 4);
        	byteIndex += 8;

    		if (DEBUG) System.out.printf("- fromPc: 0x%08x, toPc: 0x%08xz\n", fromPc, toPc);        	
    		add(fromPc, toPc);
        }        
        return true;
	}	
}
