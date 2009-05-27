package com.xmos.xta.engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;
import com.xmos.isa.Instruction;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.patterns.CommonMethods;

public class SuccessorAdder {
	private InstructionDecoder m_id;
	private Map<Integer, TreeSet<Integer>> m_branchMap;
	private MemoryImage m_mi;
	private int m_cpPointer;
	Collection<Node> m_threadEnds;

	public SuccessorAdder(InstructionDecoder id, Map<Integer, TreeSet<Integer>> branchMap, 
			MemoryImage mi, int cpPointer, Collection<Node> threadEnds) {
		m_threadEnds = threadEnds;
		m_branchMap = branchMap;
		m_id = id;
		m_mi = mi;
		m_cpPointer = cpPointer;
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
		if(!(inst.isBranch() || inst.isLink() || m_waitInstructions.contains(inst.getNmenonic()) || m_threadEnds.contains(instruction))){
			int nextPc = inst.getPc() + inst.getNumBytes();
			connect(instruction, nextPc);
		} 
	}

	private void handleBranches(Node instruction, XtaInstruction inst) {
		
		if(inst.getBranchPcRelative()){
			int target = inst.getBranchPcRelativeTargetPc();
			connect(instruction, target);
		}
		if(m_conditionalInstructions.contains(inst.getNmenonic())){
			int target = inst.getPc() + inst.getNumBytes();
			connect(instruction, target);
		}
		
		//TODO make this stage more rigorous. currently it is sloppy
		if (inst.getNmenonic().equals(new RevbXtaInstruction_blacp_u10(0, 0, null).getNmenonic())) {
			int imm = XtaInstruction.getBits(16, 10, inst.getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
			calculateCPAddress(instruction, imm);
		}
		
		if (inst.getNmenonic().equals(new RevbXtaInstruction_blacp_lu10(0, 0, null).getNmenonic())) {
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
	
	private static final HashSet<String> m_waitInstructions = new HashSet<String>(Arrays.asList( 
			new RevbXtaInstruction_waitef_1r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_waitet_1r(0, 0, null).getNmenonic(), 
			new RevbXtaInstruction_waiteu_0r(0, 0, null).getNmenonic(),
			new RevaXtaInstruction_waitef_1r(0, 0, null).getNmenonic(),
			new RevaXtaInstruction_waitet_1r(0, 0, null).getNmenonic(), 
			new RevaXtaInstruction_waiteu_0r(0, 0, null).getNmenonic()));
	
	private static final HashSet<String> m_conditionalInstructions = new HashSet<String>(Arrays.asList( 
			new RevaXtaInstruction_brbf_ru6(0, 0, null).getNmenonic(),
			new RevaXtaInstruction_brbf_lru6(0, 0, null).getNmenonic(), 
			new RevaXtaInstruction_brbt_ru6(0, 0, null).getNmenonic(),
			new RevaXtaInstruction_brbt_lru6(0, 0, null).getNmenonic(),
			new RevaXtaInstruction_brff_ru6(0, 0, null).getNmenonic(), 
			new RevaXtaInstruction_brff_lru6(0, 0, null).getNmenonic(),
			new RevaXtaInstruction_brft_ru6(0, 0, null).getNmenonic(), 
			new RevaXtaInstruction_brft_lru6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_brbf_ru6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_brbf_lru6(0, 0, null).getNmenonic(), 
			new RevbXtaInstruction_brbt_ru6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_brbt_lru6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_brff_ru6(0, 0, null).getNmenonic(), 
			new RevbXtaInstruction_brff_lru6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_brft_ru6(0, 0, null).getNmenonic(), 
			new RevbXtaInstruction_brft_lru6(0, 0, null).getNmenonic()));
}
