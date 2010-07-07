package com.xmos.xta.core.engine.analysis.structural.patterns;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.controlflow.nodes.CfgNode;
import com.xmos.libs.util.IteratableSet;
import com.xmos.xta.core.engine.analysis.structural.CfgContainerNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InfiniteLoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.LoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class LoopPatternMatcher extends PatternMatcher {

	public boolean isMatch(CfgNode currentNode, Collection<CfgNode> workingSet, CfgNode treeStart) {
		// a loop pattern is when one or more nodes are doubly attached to a
		// single node
		Set<CfgNode> intersection = new IteratableSet<CfgNode>(currentNode.getNextNodes());
		intersection.retainAll(currentNode.getPrevNodes());
		if (intersection.size() > 0) {
			Set <CfgNode> workingIntersection = new IteratableSet<CfgNode>();

			for (CfgNode loop : intersection) {
				if (!(loop.getNextNodesSize() != 1 || !loop.getNextNodes().contains(currentNode) || loop.getPrevNodes().size() != 1)) {
					workingIntersection.add(loop);
				}
			}
			if (workingIntersection.size() > 0 && !intersection.contains(treeStart)) {
				updateGraph(workingIntersection, workingSet, currentNode, treeStart);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	protected void updateGraph(Set<CfgNode> intersection, Collection<CfgNode> workingSet,
			CfgNode header, CfgNode treeStart) {
		
		boolean noNextNode = false;
		if (header.getNextNodesSize() == intersection.size()) {
			noNextNode = true;
		}
		
		CfgNode loop = createStructuralNode(intersection, header, noNextNode);

		if (intersection.contains(treeStart) || header == treeStart){
			m_treeStart = loop;
		} else {
			m_treeStart = treeStart;
		}

		for (CfgNode prev : header.getPrevNodes()) {
			if (!intersection.contains(prev)) {
				loop.addPrevNode(prev);
				prev.removeNextNode(header);
				//prev.getNextNodes().remove(base);
				prev.addNextNode(loop);
			}
		}

		for (CfgNode next : header.getNextNodes()) {
			if (!intersection.contains(next)) {
				loop.addNextNode(next);

				next.removePrevNode(header);
				//next.getPrevNodes().remove(base);
				next.addPrevNode(loop);
			}
		}

		if (noNextNode){
			CfgNode.connect(CommonMethods.findNullNode(workingSet), loop);
		}

		workingSet.removeAll(intersection);
		workingSet.remove(header);
		workingSet.add(loop);
	}

	public boolean usesDominatorTree() {
		return false;
	}
	
	private CfgNode createStructuralNode(Set<CfgNode> intersection, 
			CfgNode header, boolean noNextNode) {

		Node strHeader = getStructuralNode(header);
		Collection<Node> strLoops = getStructuralNodes(intersection);
				
		Node structuralNode = null;
		if (noNextNode){
			structuralNode = new InfiniteLoopNode(strLoops, strHeader);
		} else {
			structuralNode = new LoopNode(strLoops, strHeader);
		}		
		return new CfgContainerNode(structuralNode);
	}	
}
