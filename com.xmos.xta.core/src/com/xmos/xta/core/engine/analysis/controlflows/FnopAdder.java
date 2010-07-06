package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgBlockNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgFunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;

class FnopAdder {	
	private Set<FunctionContainer> m_functionContainers;

	FnopAdder(Set<FunctionContainer> functionContainers) {
		m_functionContainers = functionContainers;
	}

	void process() {
		for(FunctionContainer fc : m_functionContainers){
			Collection<CfgNode> basicBlocks = new IteratableSet<CfgNode>();
			reduceGraphToBasicBlocks(fc.getStartInstruction(), fc.getFunctionContents(), basicBlocks);
			FnopAnalyzer fa = new FnopAnalyzer();
			fa.analyze(basicBlocks);
		}
	}

	private void reduceGraphToBasicBlocks(CfgNode treeStart, 
			Collection<CfgNode> currentGraph, Collection<CfgNode> basicBlocks) {
		
		Set<CfgNode> seen = new NonIteratableSet<CfgNode>();
		for (CfgNode node : currentGraph) {
			if (!seen.contains(node)) {
				if (node instanceof CfgInstructionNode) {
					BasicBlockMatcher bbpm = new BasicBlockMatcher();
					if (bbpm.match(node, treeStart, currentGraph)) {
						CfgBlockNode basicBlock = bbpm.getBlock();
						seen.addAll(basicBlock.getInstructionList());
						basicBlocks.add(basicBlock);
						treeStart = bbpm.getTreeStart();
					}
				} else if (node instanceof CfgFunctionReferenceNode){
					seen.add(node);
					basicBlocks.add(node);
				} 
			}
		}	
	}
}
