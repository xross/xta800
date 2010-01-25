package com.xmos.xta.core.engine.analysis.structural;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class FnopAnalyser {
	public static int totalFnops;
	public FnopAnalyser() {
	}

	public void analyse(Node startNode) {
		//simulateBasicBlock((IBufferNode)startNode, null);
	}
/*
	public void addFnopsToBasicBlocks(Node startNode){
		
		Set<IBufferNode> nodes = new TreeSet<IBufferNode>();
		explore((IBufferNode)startNode, nodes);
		
		for(IBufferNode node : nodes){
			node.insertFnops();
			totalFnops += node.getFnopCount();
		}
		
	}
	
	private void explore(IBufferNode startNode, Set<IBufferNode> seen) {
		seen.add(startNode);
		for (IBufferNode next : startNode.getNextIbufferNodes()){
			if (!seen.contains(next)){
				explore(next, seen);
			}
		}
	}

	private void simulateBasicBlock(IBufferNode block, IBufferNode parent) {
		boolean statesUpdated = false;
		if (parent == null || parent.isFunctionNode()) {
			InstructionBuffer ib = new InstructionBuffer(block.getStartPc());
			
			////if (isFunction) {
			//	ib.fetch();
			//	statesUpdated = true;
			//}
			
			statesUpdated |= iterateOverBasicBlocks(block, ib, parent);
		} else {
			for (InstructionBuffer ib : parent.getEndInstructionBuffers()) {
				statesUpdated |= iterateOverBasicBlocks(block, ib, parent);
			}
		}
		if (statesUpdated) {
			for (IBufferNode next : block.getNextIbufferNodes()) {
				simulateBasicBlock(next, block);
			}
		}

	}

	private boolean iterateOverBasicBlocks(IBufferNode block, InstructionBuffer pretransitionIB, IBufferNode parent) {

		List<Long> fnopPcs = new LinkedList<Long>();
		InstructionBuffer endInstBuffer = new InstructionBuffer(pretransitionIB);
		
		
		if (parent == null) {
			
		} else {
			if (parent.branchesTo(block)) {
				if (parent.isFunctionNode()) {
					// FIXME - this wont always be a load store but it is for now
					endInstBuffer.branchTo(block.getStartPc(), true);
				} else {
					XtaInstruction prevInst = parent.getInstructions().getLast();
					endInstBuffer.branchTo(block.getStartPc(), prevInst.isLoadStore());
				}
			} else {
				if(!parent.getInstructions().isEmpty()){
					XtaInstruction prevInst = parent.getInstructions().getLast();
					endInstBuffer.branchTo(block.getStartPc(), prevInst.isLoadStore());
				}
			}
		}

		int numStartBytes = endInstBuffer.getNumBytes();
		
		for (XtaInstruction inst : block.getInstructions()) {
			
			if (inst.isBranch()) {
				update(endInstBuffer, fnopPcs, inst);
				endInstBuffer.branchTo(inst.getBranchPcRelativeTargetPc(), inst.isLoadStore());
			} else {
				update(endInstBuffer, fnopPcs, inst);
				endInstBuffer.setPc(inst.getPc() + inst.getNumBytes());
			}
		}

		endInstBuffer = new InstructionBuffer(pretransitionIB.getPc(), 1);
		return block.addEndInstructionBuffer(numStartBytes, endInstBuffer, fnopPcs);
	}

	private void update(InstructionBuffer ib, List<Long> fnopPcs, XtaInstruction inst) {
		while (!ib.canIssue(inst.getNumBytes())) {
			ib.fetch();
			fnopPcs.add(inst.getPc());
		}
		if (!inst.isLoadStore()) {
			ib.fetch();
		}
		ib.issue(inst.getNumBytes());
	}
*/
}
