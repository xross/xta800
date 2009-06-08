package com.xmos.xta.engine.analysis.controlflow;

import java.util.LinkedList;
import java.util.List;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class FnopAnalyser {

	public FnopAnalyser() {
	}

	public void analyse(Node startNode, boolean isFunction) {
		assert (!startNode.isFunctionNode());
		simulateBasicBlock(startNode, null, isFunction);

	}

	private void simulateBasicBlock(Node block, Node parent, boolean isFunction) {
		boolean statesUpdated = false;
		if (parent == null || parent.isFunctionNode()) {
			InstructionBuffer ib = new InstructionBuffer(block.getStartPc());
			if (isFunction)
				ib.fetch();
			statesUpdated |= iterateOverInstructions(block, ib, parent);
		} else {
			for (InstructionBuffer ib : parent.getEndInstructionBuffers()) {
				statesUpdated |= iterateOverInstructions(block, ib, parent);
			}
		}
		if (statesUpdated) {
			for (Node next : block.getNextNodes()) {
				simulateBasicBlock(next, block, isFunction);
			}
		}

	}

	private boolean iterateOverInstructions(Node block, InstructionBuffer pretransitionIB, Node parent) {
		if (block.isFunctionNode()) {
			return true;
		}

		List<Integer> fnopPcs = new LinkedList<Integer>();

		if (parent == null) {
			
		} else {
			if (parent.branchesTo(block)) {
				if (parent.isFunctionNode()) {
					// FIXME - this wont always be a load store but it is for
					// now
					pretransitionIB.branchTo(block.getStartPc(), true);
				} else {
					XtaInstruction prevInst = parent.getInstructions().get(parent.getInstructions().size() - 1);
					pretransitionIB.branchTo(block.getStartPc(), prevInst.isLoadStore());
				}
			} else {
				XtaInstruction prevInst = parent.getInstructions().get(parent.getInstructions().size() - 1);
				pretransitionIB.branchTo(block.getStartPc(), prevInst.isLoadStore());
			}
		}
		
		InstructionBuffer endInstBuffer = new InstructionBuffer(pretransitionIB);
		
		int numStartBytes = pretransitionIB.getNumBytes();

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
