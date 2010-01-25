package com.xmos.xta.core.engine.analysis.controlflow;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;
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
	private Collection<String> m_systemFunctions;
	private SymbolTable m_symbolTable;
	
	public SuccessorAdder(int nodeId, int coreId, InstructionDecoder instDecoder, Sections sections,
			Set<InstructionNode> threadEnds, Notifier notifier, CommandSource src, InstructionMnemonics mnenonics, XeLoader xeLoader) {
		m_functionEnds = threadEnds;
		m_branchMap = sections.getBranches(nodeId, coreId);
		m_systemFunctions = sections.getSystems(nodeId, coreId);
		m_instDecoder = instDecoder;
		m_notifier = notifier;
		m_src = src;
		m_mnenonics = mnenonics;
		m_symbolTable = xeLoader.getSymbolTable(nodeId, coreId);
	}

	public void process() {
		for (InstructionNode node : m_instDecoder.getInstructions()) {
			XtaInstruction inst = node.getXtaInstruction();
			
			addTargetsToAllBranchInstructions(node, inst);
			addTargetsToAllNonBranchingInstructions(node, inst);
			addUserSuccessors(node, inst);
		}
		warnAboutUnsetBranches();
	}

	private void addUserSuccessors(Node instruction, XtaInstruction inst) {
		Set<BranchInfo> targets = new HashSet<BranchInfo>();
		for (Reference ref : m_branchMap.keySet()) {
			if (ref.matches(inst.getPc())) {
				Set<BranchInfo> mapEntries = m_branchMap.get(ref);
				targets.addAll(mapEntries);
			}
		}

		//FIXME - this should not be allowed for instruction that are not branches
		for (BranchInfo target : targets) {
			for (long pc : target.getRef().getPcs()) {
				connect(instruction, pc, true);
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
		
		if (inst.getBranchPcRelative()) {
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
			m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, "target ("+ String.format("0x%x", pc) + 
					") of: " + ((InstructionNode)instruction).getXtaInstruction().toString() + " not found in executable sections");
		}
	}
	
	
	private void warnAboutUnsetBranches() {

		//this assumes that function name will be correctly extracted from the symbol table.
		for (InstructionNode inst : m_instDecoder.getInstructions()) {
			if (inst.getXtaInstruction().isBranch() && (inst.getNextNodesSize() == 0) 
					&& !inst.getXtaInstruction().getReturn() && !inst.getXtaInstruction().getBranchPcRelative()) {
				String functionName = m_symbolTable.functionName(inst.getXtaInstruction().getPc());
				if (!isSystemFunction(m_systemFunctions, functionName)) {
					m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, "No branch targets set on instruction " + inst.getXtaInstruction());
				}
			}
		}
	}
	
	private boolean isSystemFunction(Collection<String> systems, String functionName) {
		if (functionName.length() == 0) {
			return false;
		} else if (functionName.charAt(0) == '_') {
			return true;
		} else if (systems.contains(functionName)) {
			return true;
		}
		return false;
	}
	
}
