package com.xmos.xta.engine.maps;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;

public class BranchMap {	
	private static final boolean DEBUG = false;
	
	private int m_nodeId;
	private int m_coreId;
	private Notifier m_notifier;
	private TreeMap<Integer, TreeSet<Integer>> m_branches;
	
	public BranchMap(int nodeId, int coreId, Notifier notifier) {
		m_notifier = notifier;
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_branches = new TreeMap<Integer, TreeSet<Integer>>(); 
	}
	
	public int getNodeId() {
		return m_nodeId;
	}

	public int getCoreId() {
		return m_coreId;
	}
	
	public Map<Integer, TreeSet<Integer>> getMap() {
		return m_branches;
	}
	
	public void add(int fromPc, int toPc) {
		if (!m_branches.containsKey(fromPc)) {
			TreeSet<Integer> toPcs = new TreeSet<Integer>();
			toPcs.add(toPc);
			m_branches.put(fromPc, toPcs);
		} else {
			m_branches.get(fromPc).add(toPc);				
		}		
	}
	
	public void remove(int fromPc) {
		if (m_branches.containsKey(fromPc)) {
			m_branches.remove(fromPc);
		}		
	}
	
	public boolean load(XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(
				".xtabranch", m_nodeId, m_coreId);
				
		if (DEBUG) System.out.println(".xtabranch is of size " + data.length + " bytes for node " + m_nodeId + " core: " + m_coreId);
		
		if ((data.length % 8) != 0) {
			m_notifier.notify(Notifier.NOTIFY_ERROR, "Invalid size for .xtabranch section");
			return false;
		}
		
		int byteIndex = 0;
        while (byteIndex < data.length) {        	
        	int fromPc = ByteArrayManipulation.readInt(data, byteIndex);
        	byteIndex += 4;        	
        	int toPc = ByteArrayManipulation.readInt(data, byteIndex);
        	byteIndex += 4;

    		if (DEBUG) System.out.printf("- fromPc: 0x%08x, toPc: 0x%08xz\n", fromPc, toPc);        	
    		add(fromPc, toPc);
        }        
        return true;
	}	
}
