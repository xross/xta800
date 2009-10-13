package com.xmos.xta.core.engine.analysis.controlflow;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import com.xmos.libs.isa.Instruction;
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.sections.Sections;

public class SuccessorAdder {
	private InstructionDecoder m_instDecoder;
	private Map<Long, TreeSet<Long>> m_branchMap;
	private MemoryImage m_mi;
	private long m_cpPointer;
	private Collection<InstructionNode> m_threadEnds;
	private InstructionMnemonics m_mnenonics;
	private Notifier m_notifier;

	public SuccessorAdder(int nodeId, int coreId, InstructionDecoder instDecoder, Sections sections,
			Collection<InstructionNode> threadEnds, XeLoader xeLoader, Notifier notifier) {
		m_threadEnds = threadEnds;
		m_branchMap = sections.getBranches(nodeId, coreId);
		m_instDecoder = instDecoder;
		m_mi = xeLoader.getMemoryImage(nodeId, coreId);
		m_cpPointer = xeLoader.getSymbolTable(nodeId, coreId).symbolAddress("_cp");
		m_mnenonics = new InstructionMnemonics(xeLoader.getArchitecture());
		m_notifier = notifier;
	}

	public boolean process(CommandSource src) {
		for (InstructionNode node : m_instDecoder.getInstructions()) {
			XtaInstruction inst = node.getXtaInstruction();
			boolean debug = false;
			addAllBranchTargets(src, node, inst, debug);
			handleNonBranchingInstructions(src, node, inst, debug);
			addUserSuccessors(src, node, inst, debug);
			addBranchLinkReturns(src, node, inst);
		}
		return true;
	}

	private void addBranchLinkReturns(CommandSource src, Node instruction, XtaInstruction inst) {
		if (inst.isLink()) {
			long returnPc = inst.getPc() + inst.getNumBytes();
			Node returnNode = m_instDecoder.getNode(returnPc);
			if (returnNode != null) {
				instruction.setBranchLinkReturn(returnNode);
			} else {
				m_notifier.notify(src, Notifier.NOTIFY_WARNING, "unable to find PC for return from: " + inst);
			}
		}
	}

	private void addUserSuccessors(CommandSource src, Node instruction, XtaInstruction inst, boolean debug) {
		TreeSet<Long> nextPcs = m_branchMap.get(inst.getPc());

		//FIXME - this should not be allowed for instruction that are not branches
		if (nextPcs != null) {
			for (long pc : nextPcs) {
				connect(src, instruction, pc, true, debug);
			}
		}
	}

	private void handleNonBranchingInstructions(CommandSource src, Node instruction, XtaInstruction inst, boolean debug) {
		if (!(inst.isBranch() || inst.isLink() || m_mnenonics.isWait(inst) || m_threadEnds.contains(instruction))) {
			long nextPc = inst.getPc() + inst.getNumBytes();
			
			if(m_instDecoder.getNode(nextPc) != null){
				connect(src, instruction, nextPc, false, debug);
			}
			
		}
	}

	private void addAllBranchTargets(CommandSource src, Node instruction, XtaInstruction inst, boolean debug) {
		if (inst.getBranchPcRelative()) {
			long target = inst.getBranchPcRelativeTargetPc();
			connect(src, instruction, target, true, debug);
		}
		if (m_mnenonics.isConditional(inst)) {
			long target = inst.getPc() + inst.getNumBytes();
			connect(src, instruction, target, false, debug);
		}
		if (m_mnenonics.isBlacpShort(inst)) {
			long imm = XtaInstruction.getBits(16, 10, inst.getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
			calculateCPAddress(src, instruction, imm, debug);
		}
		if (m_mnenonics.isBlacpLong(inst)) {
			long imm = XtaInstruction.getBitsHL(16, 10, 0, 10, inst.getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
			calculateCPAddress(src, instruction, imm, debug);
		}
	}

	private void calculateCPAddress(CommandSource src, Node instruction, long imm, boolean debug) {
		long address = m_cpPointer + imm;
		try {
			int word = m_mi.readInstructionWord(address);
			connect(src, instruction, word, true, debug);
		} catch (Exception e) {
			m_notifier.notify(src, Notifier.NOTIFY_WARNING, String.format("CP address 0x%x out of bounds", address));
		}
	}

	private void connect(CommandSource src, Node instruction, long pc, boolean branching, boolean debug) {
		Node target = m_instDecoder.getNode(pc);
		if (target != null) {
			if (branching){
				instruction.addBranchTarget(target);
			}	
			CommonMethods.connect(target, instruction);
		} else {
			m_notifier.notify(src, Notifier.NOTIFY_WARNING, "target ("+ String.format("0x%x", pc) + 
					") of: " + ((InstructionNode)instruction).getXtaInstruction().toString() + " not found in executable sections");
		}
	}
}
