package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.List;

import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.SequenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public abstract class RouteAssertionPatterns {
    abstract boolean match(Node fromBlock, Node from, Node toBlock, Node to);
    abstract void apply(Reference fromRef, Reference toRef, Route route, 
    		Node fromBlock, Node from, Node toBlock, Node to, TimeUnit time, Source source);	
    
	boolean createBlock(List<Node> blocks, List<Node> children, int fromIndex, int toIndex) {
		List<InstructionNode> instructions = new ArrayList<InstructionNode>(); 
		for (Node child : children.subList(fromIndex, toIndex)) {
			instructions.add((InstructionNode)child);
		}
		if (!instructions.isEmpty()) {
			blocks.add(new BlockNode(instructions));
			return true;
		}
		return false;
	}	
}

class SameBlockPattern extends RouteAssertionPatterns {
	public boolean match(Node fromBlock, Node from, Node toBlock, Node to) {
		// Are both the from and the to nodes in the same block?
		if (fromBlock != toBlock)
			return false;

		// Checks that the from instruction comes before the to instruction 
		if (fromBlock.getIndirectChildren().indexOf(from) >= fromBlock.getIndirectChildren().indexOf(to))
			return false;
		
		return true;
	}
	
	public void apply(Reference fromRef, Reference toRef, Route route, 
			Node fromBlock, Node from, Node toBlock, Node to, TimeUnit time, Source source) {
		
		List<Node> blocks = new ArrayList<Node>();
		List<Node> children = fromBlock.getIndirectChildren();
			
		createBlock(blocks, children, 0, children.indexOf(from));
		
		if (createBlock(blocks, children, children.indexOf(from), children.indexOf(to))) {
			Node block = blocks.get(blocks.size() - 1);
			block.getAttributes().setFixedTime(time);
			block.getAttributes().addKnown(Type.PATH_TIME, source, new Reference[] {fromRef, toRef});
		}

		createBlock(blocks, children, children.indexOf(to), children.size());				

		Node sequence = new SequenceNode(blocks);		
		if (fromBlock.getParent() != null) {
			fromBlock.getParent().replaceChild(fromBlock, sequence);

		} else {
			route.setNode(sequence);
		}				
	}	
}

class DifferentBlockPattern extends RouteAssertionPatterns {
	public boolean match(Node fromBlock, Node from, Node toBlock, Node to) {
		// Are both the from and to blocks children of the same parent?
		if (fromBlock.getParent() != toBlock.getParent())		
		    return false;

		// Checks that the from block comes before the to block 
		List<Node> children = fromBlock.getParent().getIndirectChildren();
		if (children.indexOf(fromBlock) >= children.indexOf(toBlock))
			return false;
		
		return true;		
	}
	
	public void apply(Reference fromRef, Reference toRef, Route route, 
			Node fromBlock, Node from, Node toBlock, Node to, TimeUnit time, Source source) {
		
		Node parent = fromBlock.getParent();
		List<Node> children = parent.getIndirectChildren();
		List<Node> newChildren = new ArrayList<Node>();
		
		newChildren.addAll(children.subList(0, children.indexOf(fromBlock)));
		
		List<Node> timingChildren = new ArrayList<Node>();		
		
		createBlock(newChildren, fromBlock.getIndirectChildren(), 
				0, fromBlock.getIndirectChildren().indexOf(from));		

		createBlock(timingChildren, fromBlock.getIndirectChildren(), 
				fromBlock.getIndirectChildren().indexOf(from), fromBlock.getIndirectChildren().size());
				
		timingChildren.addAll(children.subList(children.indexOf(fromBlock) + 1, children.indexOf(toBlock)));
		
		createBlock(timingChildren, toBlock.getIndirectChildren(), 
				0, toBlock.getIndirectChildren().indexOf(to));		

		Node sequence = new SequenceNode(timingChildren);
		sequence.getAttributes().setFixedTime(time);		
		sequence.getAttributes().addKnown(Type.PATH_TIME, source, new Reference[] {fromRef, toRef});		
		newChildren.add(sequence);		
		
		createBlock(newChildren, toBlock.getIndirectChildren(), 
				toBlock.getIndirectChildren().indexOf(to), toBlock.getIndirectChildren().size());
				
		newChildren.addAll(children.subList(children.indexOf(toBlock) + 1, children.size()));

		parent.replaceChildren(newChildren);			
	}		
}

