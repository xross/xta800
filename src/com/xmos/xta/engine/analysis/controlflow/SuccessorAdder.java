package com.xmos.xta.engine.analysis.controlflow;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.engine.instructions.InstructionMnemonics;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.sections.Sections;

public class SuccessorAdder {
	private InstructionDecoder m_instDecoder;
	private Map<Integer, TreeSet<Integer>> m_branchMap;
	private MemoryImage m_mi;
	private int m_cpPointer;
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

	public boolean process() {
		for (Node node : m_instDecoder.getInstructions()) {
			XtaInstruction inst = node.getInstructions().getFirst();
			boolean debug = false;
			addAllBranchTargets(node, inst, debug);
			handleNonBranchingInstructions(node, inst, debug);
			addUserSuccessors(node, inst, debug);
			addBranchLinkReturns(node, inst);
			
		}
		return true;
	}

	private void addBranchLinkReturns(Node instruction, XtaInstruction inst) {
		if (inst.isLink()) {
			int returnPc = inst.getPc() + inst.getNumBytes();
			Node returnNode = m_instDecoder.getNode(returnPc);
			if (returnNode != null) {
				instruction.setBranchLinkReturn(returnNode);
			} else {
				m_notifier.notify(Notifier.NOTIFY_WARNING, "Unable to find PC for return from: " + inst);
			}
		}
	}

	private void addUserSuccessors(Node instruction, XtaInstruction inst, boolean debug) {
		TreeSet<Integer> nextPcs = m_branchMap.get(inst.getPc());

		//FIXME - this should not be allowed for instruction that are not branches
		if (nextPcs != null) {
			for (int pc : nextPcs) {
				connect(instruction, pc, true, debug);
			}
		}
	}

	private void handleNonBranchingInstructions(Node instruction, XtaInstruction inst, boolean debug) {
		if (!(inst.isBranch() || inst.isLink() || m_mnenonics.isWait(inst) || m_threadEnds.contains(instruction))) {
			int nextPc = inst.getPc() + inst.getNumBytes();
			connect(instruction, nextPc, false, debug);
		}
	}

	private void addAllBranchTargets(Node instruction, XtaInstruction inst, boolean debug) {
		if (inst.getBranchPcRelative()) {
			int target = inst.getBranchPcRelativeTargetPc();
			connect(instruction, target, true, debug);
		}
		if (m_mnenonics.isConditional(inst)) {
			int target = inst.getPc() + inst.getNumBytes();
			connect(instruction, target, false, debug);
		}
		if (m_mnenonics.isBlacpShort(inst)) {
			int imm = XtaInstruction.getBits(16, 10, inst.getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
			calculateCPAddress(instruction, imm, debug);
		}
		if (m_mnenonics.isBlacpLong(inst)) {
			int imm = XtaInstruction.getBitsHL(16, 10, 0, 10, inst.getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
			calculateCPAddress(instruction, imm, debug);
		}
	}

	private void calculateCPAddress(Node instruction, int imm, boolean debug) {
		int address = m_cpPointer + imm;
		try {
			int word = m_mi.readInstructionWord(address);
			connect(instruction, word, true, debug);
		} catch (Exception e) {
			m_notifier.notify(Notifier.NOTIFY_WARNING, String.format("CP address 0x%x out of bounds", address));
		}
	}

	private void connect(Node instruction, int pc, boolean branching, boolean debug) {
		Node target = m_instDecoder.getNode(pc);
		if (target != null) {
			if (branching){
				instruction.addBranchTarget(target);
			}
				
			CommonMethods.connect(target, instruction);
			if(debug) System.out.println(instruction + " -> " + target);
		} else {
			m_notifier.notify(Notifier.NOTIFY_WARNING, "Instruction " + instruction + " failed to find target 0x"
					+ Integer.toHexString(pc));
		}
	}
}
