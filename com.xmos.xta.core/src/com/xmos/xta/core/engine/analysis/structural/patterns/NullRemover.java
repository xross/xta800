package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.controlflow.nodes.CfgNullNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;

public class NullRemover extends PatternMatcher {

    public NullRemover(MemoryMonitor memoryMonitor) {
        super(memoryMonitor);
    }

	public boolean isMatch(CfgNode currentNode, 
	        Collection<CfgNode> workingSet, CfgNode treeStart) throws MemoryMonitorException {
	    
		m_treeStart = treeStart;
		
		if (!(currentNode instanceof CfgNullNode)) {
			return false;
		}
		
		assert(currentNode.getNextNodes().size() < 2);
		
		for (CfgNode next : currentNode.getNextNodes()) {
			if (next instanceof CfgNullNode) {
				workingSet.remove(currentNode);
				for (CfgNode prev : currentNode.getPrevNodes()) {
					prev.removeNextNode(currentNode);
					prev.addNextNode(next);
					next.addPrevNode(prev);
				}
				next.removePrevNode(currentNode);
				return true;
			}
		}
		return false;
	}

	public boolean usesDominatorTree() {
		return false;
	}
}