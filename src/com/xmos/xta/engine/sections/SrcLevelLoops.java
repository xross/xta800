package com.xmos.xta.engine.sections;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.util.ByteArrayManipulation;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;

public class SrcLevelLoops extends Section {
	private Collection<SrcLevelLoopInfo> m_loops;

	SrcLevelLoops(int nodeId, int coreId, Notifier notifier) {
		super(nodeId, coreId, notifier);
		m_loops = new ArrayList<SrcLevelLoopInfo>(); 
	}
	
	Collection<SrcLevelLoopInfo> get() {
		return m_loops;
	}
	
	boolean load(XeLoader xeLoader) {
		byte[] data = xeLoader.getSectionData(".xtaloop", getNodeId(), getCoreId());
		try {
			int byteIndex = 0;
	        while (byteIndex < data.length) {        
	        	int pc = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;        	
	        	
	        	int numIterations = ByteArrayManipulation.readInt(data, byteIndex);
	        	byteIndex += 4;
	        	
	        	m_loops.add(new SrcLevelLoopInfo(pc, numIterations));
	        }        
		} catch (Exception e) {
		    getNotifier().notify(Notifier.NOTIFY_ERROR,
			    	"Problem reading .xtaloop section while loading executable");
    	    return false;
        }
        return true;
	}		
}
