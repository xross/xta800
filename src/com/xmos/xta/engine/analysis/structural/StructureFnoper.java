package com.xmos.xta.engine.analysis.structural;

import java.util.LinkedList;
import java.util.List;

import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class StructureFnoper {

	public void analyse(Node decomposedGraph, boolean isFunction) {
		decomposedGraph.addFnops();
	}

	
	public static List<Integer> makeFnops(Node node, InstructionBuffer copy, Node parent){
		List<Integer> fnops = new LinkedList<Integer>();
		
		//first perform the transition to the block.
		InstructionBuffer endInstBuffer = new InstructionBuffer(copy);
		if (parent == null) {
			
		} else {
			if (parent.branchesTo(node.getStartPc())) {
				if (parent.isFunctionNode()) {
					// FIXME - this wont always be a load store but it is for now
					endInstBuffer.branchTo(node.getStartPc(), true);
				} else {
					XtaInstruction prevInst = parent.getInstructions().getLast();
					endInstBuffer.branchTo(node.getStartPc(), prevInst.isLoadStore());
				}
			} else {
				XtaInstruction prevInst = parent.getInstructions().getLast();
				endInstBuffer.branchTo(node.getStartPc(), prevInst.isLoadStore());
			}
		}
		
		//then simulate each of the instructions in this block.

		for (XtaInstruction inst : node.getInstructions()) {

			if (inst.isBranch()) {
				endInstBuffer.branchTo(inst.getBranchPcRelativeTargetPc(), inst.isLoadStore());
			} else {
				update(endInstBuffer, fnops, inst);
				endInstBuffer.setPc(inst.getPc() + inst.getNumBytes());
			}

		}
		
		return fnops;
	}
	
	private static void update(InstructionBuffer ib, List<Integer> fnopPcs, XtaInstruction inst) {
		while (!ib.canIssue(inst.getNumBytes())) {
			ib.fetch();
			fnopPcs.add(inst.getPc());
		}
		if (!inst.isLoadStore()) {
			ib.fetch();
		}
		ib.issue(inst.getNumBytes());
	}
}
