package com.xmos.xta.core.engine.analysis.structural;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.core.engine.instructions.NullInstruction;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class FnopAnalyzer {

	public FnopAnalyzer() {

	}

	public void analyze(Collection<Node> workingSet) {
		for(Node node : workingSet){
			if(node instanceof BlockNode){
				BlockNode block = (BlockNode)node;
				updateBlock(block);
			}
		}
	}

	private void updateBlock(BlockNode block){
		InstructionBuffer ib =null;
		long startPc = block.getInstructionList().get(0).getStartPc();

		//TODO -- refactor this to not be such a mess!
		if(isWordAligned(startPc)){
			if(getPrevNodeSize(block) == 1){
				if(returningFromAFunction(block)){
					ib = new InstructionBuffer(startPc, 0);
				} else {
					ib = new InstructionBuffer(startPc, 4);
				}
			} else {
				ib = new InstructionBuffer(startPc, 4);
			}
		} else {
			if(getPrevNodeSize(block) == 1){
				if(returningFromAFunction(block)){
					ib = new InstructionBuffer(startPc, 0);
				} else {
					ib = new InstructionBuffer(startPc, 2);
				}
			} else {
				ib = new InstructionBuffer(startPc, 2);
			}
		}


		List<InstructionNode> instCopy = new LinkedList<InstructionNode>(block.getInstructionList());
		for(InstructionNode inst : instCopy){
			update(ib, block.getInstructionList(), inst);
		}

	}
	
	private int getPrevNodeSize(BlockNode block){
		InstructionNode firstInst = block.getInstructionList().get(0);
		return firstInst.getPrevNodes().size();
	}
	

	private void update(InstructionBuffer ib, List<InstructionNode> blockInstructions, InstructionNode instNode) {
		XtaInstruction inst = instNode.getXtaInstruction();
		while (!ib.canIssue(inst.getNumBytes())) {
			ib.fetch();
			int index = blockInstructions.indexOf(instNode);
			XtaInstruction fnop = new NullInstruction("--FNOP--", 0, inst.getPc(), 0);
			InstructionNode fnopNode = new InstructionNode(fnop);

			for(Node prev : instNode.getPrevNodes()){
				prev.removeNextNode(instNode);
				CommonMethods.connect(fnopNode, prev);

			}
			instNode.getPrevNodes().clear();
			CommonMethods.connect(instNode, fnopNode);
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

	private boolean returningFromAFunction(BlockNode block){
		InstructionNode firstInst = block.getInstructionList().get(0);
		for(Node prev : firstInst.getPrevNodes()){
			if(prev instanceof FunctionReferenceNode){
				return true;
			} 
		} 
		return false;
	}

}
