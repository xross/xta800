package com.xmos.xta.core.engine.analysis.controlflow;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sections.BranchInfo;
import com.xmos.xta.core.engine.sections.Sections;

public class SuccessorAdder {

	private InstructionDecoder m_instDecoder;
	private Map<Reference, Set<BranchInfo>> m_branchMap;
	private Set<InstructionNode> m_functionEnds;
	private InstructionMnemonics m_mnenonics;
	private Notifier m_notifier;
	private CommandSource m_src;

	public SuccessorAdder(int nodeId, int coreId, InstructionDecoder instDecoder, Sections sections,
			Set<InstructionNode> threadEnds, Notifier notifier, CommandSource src, InstructionMnemonics mnenonics) {
		m_functionEnds = threadEnds;
		m_branchMap = sections.getBranches(nodeId, coreId);
		m_instDecoder = instDecoder;
		m_notifier = notifier;
		m_src = src;
		m_mnenonics = mnenonics;
	}

	public void process() {
		for (InstructionNode node : m_instDecoder.getInstructions()) {
			XtaInstruction inst = node.getXtaInstruction();

			addTargetsToAllBranchInstructions(node, inst);
			addTargetsToAllNonBranchingInstructions(node, inst);
			addUserSuccessors(node, inst);
		}
	}

	private void addUserSuccessors(Node instruction, XtaInstruction inst) {
		
		Set<BranchInfo> targets = new HashSet<BranchInfo>();
		for (Reference ref : m_branchMap.keySet()) {
			if (ref.matches(inst.getPc())) {
				Set<BranchInfo> mapEntries = m_branchMap.get(ref);
				targets.addAll(mapEntries);
			}
		}
		
		for (BranchInfo target : targets) {
			for (long pc : target.getRef().getPcs()) {
				if(inst.isBranch()){
					connect(instruction, pc, true);
				} else {
					//TODO warn if this is not a branch --- usb code seems to set a branch on setsr!
					
					//System.err.println("trying to add branch to non branching instruction " + inst);
				}
			}
		}		
	}

	private void addTargetsToAllNonBranchingInstructions(Node instruction, XtaInstruction inst) {
		if (!(inst.isBranch() || inst.isLink() || m_mnenonics.isWait(inst) || m_functionEnds.contains(instruction))) {
			long nextPc = inst.getPc() + inst.getNumBytes();
			if(m_instDecoder.getNode(nextPc) != null){
				connect(instruction, nextPc, false);
			}
		}
	}

	private void addTargetsToAllBranchInstructions(Node instruction, XtaInstruction inst) {

		if(inst.isLink()) return;

		if (inst.getBranchPcRelative() && inst.isBranch()) {
			long target = inst.getBranchPcRelativeTargetPc();
			connect(instruction, target, true);
		}
		if (m_mnenonics.isConditional(inst)) {
			long target = inst.getPc() + inst.getNumBytes();
			connect(instruction, target, false);
		}
	}

	private void connect(Node instruction, long pc, boolean branching) {
		Node target = m_instDecoder.getNode(pc);
		if (target != null) {
			if (branching){
				instruction.addBranchTarget(target);
			}
			CommonMethods.connect(target, instruction);
		} else {
			if(!m_mnenonics.isLdap(((InstructionNode)instruction).getXtaInstruction())){
				m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, "target ("+ String.format("0x%x", pc) +
					") of: " + ((InstructionNode)instruction).getXtaInstruction().toString() + " not found in executable sections");
			}
		}
	}

}
