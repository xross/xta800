package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Collection;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.structural.BasicBlockMatcher;
import com.xmos.xta.core.engine.analysis.structural.FnopAnalyzer;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class FnopAdder {
	
	private Set<FunctionContainer> m_functionContainers;

	public FnopAdder(Set<FunctionContainer> functionContainers) {
		m_functionContainers = functionContainers;
	}

	public void process() {
		for(FunctionContainer fc : m_functionContainers){
			Collection<Node> basicBlocks = new IteratableSet<Node>();
			reduceGraphToBasicBlocks(fc.getStartInstruction(), fc.getFunctionContents(), basicBlocks);
			FnopAnalyzer fa = new FnopAnalyzer();
			fa.analyze(basicBlocks);
		}
	}

	private void reduceGraphToBasicBlocks(Node treeStart, Collection<Node> currentGraph, Collection<Node> basicBlocks) {
		Set<Node> seen = new NonIteratableSet<Node>();
		for (Node node : currentGraph) {
			if (!seen.contains(node)) {
				if (node instanceof InstructionNode) {
					BasicBlockMatcher bbpm = new BasicBlockMatcher();
					if (bbpm.match(node, treeStart, currentGraph)) {
						BlockNode basicBlock = bbpm.getBlock();
						seen.addAll(basicBlock.getInstructionList());
						basicBlocks.add(basicBlock);
						treeStart = bbpm.getTreeStart();
					}
				} else if(node instanceof FunctionReferenceNode){
					seen.add(node);
					basicBlocks.add(node);
				} 
			}
		}	
	}
}
