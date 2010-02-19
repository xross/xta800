package com.xmos.xta.core.engine.analysis.controlflow;

import com.xmos.libs.isa.Instruction;
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class FunctionReferenceNodeAdder {
	private InstructionDecoder m_instDecoder;
	private Notifier m_notifier;
	private CommandSource m_src;
	private InstructionMnemonics m_mnenonics;
	private MemoryImage m_mi;
	private long m_cpPointer;

	public FunctionReferenceNodeAdder(InstructionDecoder instDecoder, Notifier notifier, CommandSource src,
			XeLoader xeLoader, int nodeId, int coreId){
		m_instDecoder = instDecoder;
		m_notifier = notifier;
		m_src = src;
		m_mi = xeLoader.getMemoryImage(nodeId, coreId);
		m_cpPointer = xeLoader.getSymbolTable(nodeId, coreId).symbolAddress("_cp");
		m_mnenonics = new InstructionMnemonics(xeLoader.getArchitecture());
	}


	public void process() {
		// for each branch and link add a function reference node
		for(InstructionNode inst : m_instDecoder.getInstructions()){
			if(inst.getXtaInstruction().isLink()){
				long targetPc = 0;
				if (m_mnenonics.isBlacpShort(inst.getXtaInstruction())) {
					long imm = XtaInstruction.getBits(16, 10, inst.getXtaInstruction().getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
					try {
						targetPc = m_mi.readInstructionWord(m_cpPointer + imm);
					} catch (Exception e) {
						m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, String.format("CP address 0x%x out of bounds", (m_cpPointer + imm)));
					}
					targetPc = m_cpPointer + imm;
				} else if (m_mnenonics.isBlacpLong(inst.getXtaInstruction())) {
					long imm = XtaInstruction.getBitsHL(16, 10, 0, 10, inst.getXtaInstruction().getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
					try {
						targetPc = m_mi.readInstructionWord(m_cpPointer + imm);
					} catch (Exception e) {
						m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, String.format("CP address 0x%x out of bounds", (m_cpPointer + imm)));
					}
					targetPc = m_cpPointer + imm;
				} else if(inst.getXtaInstruction().getBranchPcRelative()){
					targetPc = inst.getXtaInstruction().getBranchPcRelativeTargetPc();
				}

				InstructionNode target = m_instDecoder.getNode(targetPc);

				//target may be null if the branch target is unknown
				FunctionReferenceNode fr = new FunctionReferenceNode(inst, target);
				fr.setCfgRef();

				long returnPc = inst.getXtaInstruction().getPc() + inst.getXtaInstruction().getNumBytes();
				InstructionNode branchLinkReturn = m_instDecoder.getNode(returnPc);
				if (branchLinkReturn != null) {
					CommonMethods.connect(fr, inst);
					CommonMethods.connect(branchLinkReturn, fr);
				} else {
					m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, "unable to find PC for return from: " + inst.getXtaInstruction());
				}

			}
		}
	}

}
