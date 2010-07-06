package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Map;
import java.util.Set;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.binary.InstructionMnemonics;
import com.xmos.xta.core.engine.analysis.binary.XtaInstruction;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sections.BranchInfo;
import com.xmos.xta.core.engine.sections.Sections;
import com.xmos.xta.core.engine.utils.CoreId;

class SuccessorAdder {

	private InstructionDecoder m_instDecoder;
	private Map<Reference, Set<BranchInfo>> m_branchMap;
	private Set<CfgInstructionNode> m_functionEnds;
	private InstructionMnemonics m_mnenonics;
	private Notifier m_notifier;
	private CommandSource m_src;

	SuccessorAdder(CoreId coreId, InstructionDecoder instDecoder, Sections sections,
			Set<CfgInstructionNode> threadEnds, Notifier notifier, CommandSource src, InstructionMnemonics mnenonics) {
		m_functionEnds = threadEnds;
		m_branchMap = sections.getBranches(coreId);
		m_instDecoder = instDecoder;
		m_notifier = notifier;
		m_src = src;
		m_mnenonics = mnenonics;
	}

	void process() {
		for (CfgInstructionNode node : m_instDecoder.getInstructions()) {
			XtaInstruction inst = node.getXtaInstruction();
						
			addTargetsToAllBranchInstructions(node, inst);
			addTargetsToAllNonBranchingInstructions(node, inst);
			addUserSuccessors(node, inst);
		}
	}
	
	private void addUserSuccessors(CfgNode instruction, XtaInstruction inst) {
		if(!inst.isLink()){
			for (Reference ref : m_branchMap.keySet()) {
				if (ref.matches(inst.getPc())) {
					for (BranchInfo target :  m_branchMap.get(ref)) {
						for (long pc : target.getRef().getPcs()) {
							connect(instruction, pc, true);
						}
					}
				}
			}
		}
	}

	private void addTargetsToAllNonBranchingInstructions(CfgNode instruction, XtaInstruction inst) {
		if (!(inst.isBranch() || inst.isLink() || m_mnenonics.isWait(inst) || m_functionEnds.contains(instruction))) {
			long nextPc = inst.getPc() + inst.getNumBytes();
			if(m_instDecoder.getNode(nextPc) != null){
				connect(instruction, nextPc, false);
			}
		}
	}

	private void addTargetsToAllBranchInstructions(CfgNode instruction, XtaInstruction inst) {

		if (inst.isLink()) {
			long target = inst.getPc() + inst.getNumBytes();
			CfgInstructionNode blReturn = m_instDecoder.getNode(target);
			if (blReturn != null) {
				instruction.addBranchReturnNode(blReturn);
			}
			return;			
		}

		if (inst.getBranchPcRelative() && inst.isBranch()) {
			long target = inst.getBranchPcRelativeTargetPc();
			connect(instruction, target, true);
		}
		if (m_mnenonics.isConditional(inst)) {
			long target = inst.getPc() + inst.getNumBytes();
			connect(instruction, target, false);
		}
	}

	private void connect(CfgNode instruction, long pc, boolean branching) {
		CfgNode target = m_instDecoder.getNode(pc);
		if (target != null) {
			CfgNode.connect(target, instruction);
		} else {
			if(!m_mnenonics.isLdap(((CfgInstructionNode)instruction).getXtaInstruction())){
				m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, "target ("+ String.format("0x%x", pc) +
					") of: " + ((CfgInstructionNode)instruction).getXtaInstruction().toString() + " not found in executable sections");
			}
		}
	}

}
