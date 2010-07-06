package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.xmos.xta.core.engine.analysis.binary.NullInstruction;
import com.xmos.xta.core.engine.analysis.binary.XtaInstruction;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgBlockNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgFunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;

class FnopAnalyzer {

	void analyze(Collection<CfgNode> workingSet) {
		for (CfgNode node : workingSet) {
			if (node instanceof CfgBlockNode) {
				CfgBlockNode block = (CfgBlockNode)node;
				updateBlock(block);
			}
		}
	}

	private void updateBlock(CfgBlockNode block){
		InstructionBuffer ib = null;
		long startPc = block.getInstructionList().get(0).getPc();

		//TODO -- refactor this to not be such a mess!
		if (isWordAligned(startPc)) {
			if (getPrevNodeSize(block) == 1) {
				if (returningFromAFunction(block)) {
					ib = new InstructionBuffer(startPc, 0);
				} else {
					ib = new InstructionBuffer(startPc, 4);
				}
			} else {
				ib = new InstructionBuffer(startPc, 4);
			}
		} else {
			if (getPrevNodeSize(block) == 1) {
				if (returningFromAFunction(block)) {
					ib = new InstructionBuffer(startPc, 0);
				} else {
					ib = new InstructionBuffer(startPc, 2);
				}
			} else {
				ib = new InstructionBuffer(startPc, 2);
			}
		}

		List<CfgInstructionNode> instCopy = new LinkedList<CfgInstructionNode>(block.getInstructionList());
		for(CfgInstructionNode inst : instCopy){
			update(ib, block.getInstructionList(), inst);
		}
	}
	
	private int getPrevNodeSize(CfgBlockNode block){
		CfgInstructionNode firstInst = block.getInstructionList().get(0);
		return firstInst.getPrevNodes().size();
	}

	private void update(InstructionBuffer ib, List<CfgInstructionNode> blockInstructions, CfgInstructionNode instNode) {
		XtaInstruction inst = instNode.getXtaInstruction();
		while (!ib.canIssue(inst.getNumBytes())) {
			ib.fetch();
			int index = blockInstructions.indexOf(instNode);
			XtaInstruction fnop = new NullInstruction("--FNOP--", 0, inst.getPc(), 0, true);
			CfgInstructionNode fnopNode = new CfgInstructionNode(fnop);

			for (CfgNode prev : instNode.getPrevNodes()){
				prev.removeNextNode(instNode);
				CfgNode.connect(fnopNode, prev);

			}
			instNode.getPrevNodes().clear();
			CfgNode.connect(instNode, fnopNode);
			blockInstructions.add(index  , fnopNode);
		}
		if (!inst.isLoadStore()) {
			ib.fetch();
		}
		ib.issue(inst.getNumBytes());

		if(inst.isBranch()){
			ib.branchTo(inst.getBranchPcRelativeTargetPc(), inst.isLoadStore());
		} else {
			ib.setPc(inst.getPc() + inst.getNumBytes());
		}
	}

	private boolean isWordAligned(long address) {
        if ((address & 0x3) == 0)
            return true;
        else
            return false;
    }

	private boolean returningFromAFunction(CfgBlockNode block){
		CfgInstructionNode firstInst = block.getInstructionList().get(0);
		for (CfgNode prev : firstInst.getPrevNodes()){
			if (prev instanceof CfgFunctionReferenceNode){
				return true;
			} 
		} 
		return false;
	}
}
