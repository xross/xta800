package com.xmos.xta.core.engine.analysis.binary;

import java.util.LinkedList;
import java.util.List;

import com.xmos.libs.elfhandler.ElfSection;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.utils.CoreId;

public class InstructionDecoder {
	private CoreId m_coreId;
	private long m_endPc;
	private NonIteratableMap<Long, CfgInstructionNode> m_decodedInstructions;
	private List<CfgInstructionNode> m_orderedInstructions;
	private XeLoader m_xeLoader;

	public InstructionDecoder(CoreId coreId, XeLoader xeLoader) {
		m_coreId = coreId;
		m_xeLoader = xeLoader;
		m_endPc = Long.MIN_VALUE;
		m_decodedInstructions = new NonIteratableMap<Long, CfgInstructionNode>();
		m_orderedInstructions = new LinkedList<CfgInstructionNode>();
	}

	public void decode() throws Exception {
		MemoryImage memoryImage = m_xeLoader.getMemoryImage(m_coreId.getNodeChainIndex(), m_coreId.getCoreNumber());
		SymbolTable symbolTable = m_xeLoader.getSymbolTable(m_coreId.getNodeChainIndex(), m_coreId.getCoreNumber());
		XtaInstructionDecoder instructionDecoder = XtaInstructionDecoder.createInstructionDecoder(m_xeLoader.getArchitecture());
		if (instructionDecoder == null) {
			throw new Exception("Architecture not supported");
		}

		for (ElfSection section: symbolTable.getElfHandler().getSections()) {
			if (section.isExecutable()){
				long sectionAddress = XtaInstruction.makeLongPc(section.getAddress());
				long pc = sectionAddress;
				while (pc < sectionAddress + section.getBytes().length){
					int opcode = 0;
					try {
		                opcode = memoryImage.readInstructionWord(pc);
		            } catch (Exception e) {
		            	throw new Exception("Invalid address detected");
		            }

		            XtaInstruction inst = instructionDecoder.decode(opcode, pc);
		            
		            inst.setFunctionName(symbolTable.functionName(pc));
		            inst.setFunctionOffset(symbolTable.functionOffset(pc));

		            CfgInstructionNode instNode = new CfgInstructionNode(inst);

		            m_decodedInstructions.put(pc, instNode);
		            m_orderedInstructions.add(instNode);

		            pc += inst.getNumBytes();
				}

				if (pc > m_endPc) {
					m_endPc = pc;
				}
			}
		}
	}

	public List<CfgInstructionNode> getInstructions(){
		return m_orderedInstructions;
	}

	public CfgInstructionNode getNode(long pc){
		return m_decodedInstructions.get(pc);
	}

	public long getEndPc() {
		return m_endPc;
	}
}
