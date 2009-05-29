package com.xmos.xta.engine;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import com.xmos.isa.Instruction;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.patterns.CommonMethods;
import com.xmos.xta.engine.instructions.InstructionMnemonics;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class SuccessorAdder {
	private InstructionDecoder m_id;
	private Map<Integer, TreeSet<Integer>> m_branchMap;
	private MemoryImage m_mi;
	private int m_cpPointer;
	Collection<Node> m_threadEnds;
	InstructionMnemonics m_mnenonics;

	public SuccessorAdder(InstructionDecoder id, Map<Integer, TreeSet<Integer>> branchMap, 
			MemoryImage mi, int cpPointer, Collection<Node> threadEnds, InstructionMnemonics mnenonics) {
		m_threadEnds = threadEnds;
		m_branchMap = branchMap;
		m_id = id;
		m_mi = mi;
		m_cpPointer = cpPointer;
		m_mnenonics = mnenonics;
	}

	public boolean process (){
		//for each instruction add all its successors as connections
		for(Node instruction : m_id.getInstructions()){
			assert(instruction.getInstructions().size() == 1);
			XtaInstruction inst = instruction.getInstructions().get(0);
			
			// need to add all branch targets 
			handleBranches(instruction, inst);
			
			// instructions that continue
			handleNonBranchingInstructions(instruction, inst);
			
			// user and compiler added successors
			handleSuccessors(instruction, inst);
			
			//add branch links when a function would go
			addBranchLinkReturns(instruction, inst);
			
			//not function returns 
		}
		//TODO - return false if there were errors 
		return true;
	}
	
	private void addBranchLinkReturns(Node instruction, XtaInstruction inst) {
		if(inst.isLink()){
			int returnPc = inst.getPc() + inst.getNumBytes();
			Node returnNode = m_id.getNode(returnPc);
			if (returnNode != null){
				instruction.setBranchLinkReturn(returnNode);
			} else {
				//TODO notify of a warning
			}
		}
	}

	private void handleSuccessors(Node instruction, XtaInstruction inst) {
		TreeSet<Integer> nextPcs = m_branchMap.get(inst.getPc());
		if (nextPcs != null) {
			for(int pc : nextPcs){
				connect(instruction, pc);
			}
		}
	}

	private void handleNonBranchingInstructions(Node instruction, XtaInstruction inst) {
		if(!(inst.isBranch() || inst.isLink() || m_mnenonics.isWait(inst) || m_threadEnds.contains(instruction))){
			int nextPc = inst.getPc() + inst.getNumBytes();
			connect(instruction, nextPc);
		} 
	}

	private void handleBranches(Node instruction, XtaInstruction inst) {		
		if (inst.getBranchPcRelative()){
			int target = inst.getBranchPcRelativeTargetPc();
			connect(instruction, target);
		}
		if (m_mnenonics.isConditional(inst)){
			int target = inst.getPc() + inst.getNumBytes();
			connect(instruction, target);
		}		
		if (m_mnenonics.isBlacpShort(inst)) {
			int imm = XtaInstruction.getBits(16, 10, inst.getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
			calculateCPAddress(instruction, imm);
		}		
		if (m_mnenonics.isBlacpLong(inst)) {
			int imm = XtaInstruction.getBitsHL(16, 10, 0, 10, inst.getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
			calculateCPAddress(instruction, imm);
		}
	}

	private void calculateCPAddress(Node instruction, int imm) {
		int address = m_cpPointer + imm;
		int word = 0;
		boolean success = true;
		for (int i = 0; i < 4; i++) {
			try {
				byte b = m_mi.readByte(address + i);
				word = word | (b << (8 * i));
			} catch (Exception e) {
				//TODO
				//m_notifier.notify(Notifier.NOTIFY_WARNING, String.format("Branch address 0x%x out of bounds", address + i));
				success = false;
				continue;
			}
		}
		if(success){
			connect(instruction, word);
		}
	}

	private void connect(Node instruction, int word) {
		Node target = m_id.getNode(word);
		if(target != null){
			CommonMethods.connect(target, instruction);
		} else {
			//TODO warn
			//m_notifier.notify(Notifier.NOTIFY_WARNING, String.format("Branch address 0x%x out of bounds", address + i));
		}
	}
}
