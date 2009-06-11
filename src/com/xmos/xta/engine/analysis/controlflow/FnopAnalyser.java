package com.xmos.xta.engine.analysis.controlflow;

import java.util.LinkedList;
import java.util.List;

import com.xmos.xta.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.engine.analysis.structural.nodes.IbufferNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class FnopAnalyser {

	public FnopAnalyser() {
	}

	public void analyse(Node startNode, boolean isFunction) {
		assert (!startNode.isFunctionNode());
		assert(startNode instanceof IbufferNode);
		simulateBasicBlock((IbufferNode)startNode, null, isFunction);

	}

	private void simulateBasicBlock(IbufferNode block, IbufferNode parent, boolean isFunction) {
		boolean statesUpdated = false;
		if (parent == null || parent.isFunctionNode()) {
			InstructionBuffer ib = new InstructionBuffer(block.getStartPc());
			if (isFunction) {
				ib.fetch();
				statesUpdated = true;
			}
				
			statesUpdated |= iterateOverBasicBlocks(block, ib, parent);
		} else {
			
			for (InstructionBuffer ib : parent.getEndInstructionBuffers()) {
				statesUpdated |= iterateOverBasicBlocks(block, ib, parent);
			}
		}
		if (statesUpdated) {
			for (IbufferNode next : block.getNextIbufferNodes()) {
				simulateBasicBlock(next, block, isFunction);
			}
		}

	}

	private boolean iterateOverBasicBlocks(IbufferNode block, InstructionBuffer pretransitionIB, IbufferNode parent) {

		List<Integer> fnopPcs = new LinkedList<Integer>();
		
		
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
				XtaInstruction prevInst = parent.getInstructions().getLast();
				endInstBuffer.branchTo(block.getStartPc(), prevInst.isLoadStore());
			}
		}
		
		
		
		int numStartBytes = endInstBuffer.getNumBytes();

		for (XtaInstruction inst : block.getInstructions()) {

			if (inst.isBranch()) {
				endInstBuffer.branchTo(inst.getBranchPcRelativeTargetPc(), inst.isLoadStore());
			} else {
				update(endInstBuffer, fnopPcs, inst);
				endInstBuffer.setPc(inst.getPc() + inst.getNumBytes());
			}

		}

		return block.addEndInstructionBuffer(numStartBytes, endInstBuffer, fnopPcs);
	}

	private void update(InstructionBuffer ib, List<Integer> fnopPcs, XtaInstruction inst) {
		while (!ib.canIssue(inst.getNumBytes())) {
			ib.fetch();

			// make a record of the fnops
			fnopPcs.add(inst.getPc());
		}
		if (!inst.isLoadStore()) {
			ib.fetch();
		}
		ib.issue(inst.getNumBytes());
	}

}
