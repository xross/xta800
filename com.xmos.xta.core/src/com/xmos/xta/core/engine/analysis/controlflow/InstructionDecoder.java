package com.xmos.xta.core.engine.analysis.controlflow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.xmos.libs.elfhandler.ElfSection;
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.instructions.DataCollection;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.instructions.XtaInstructionDecoder;

public class InstructionDecoder {
	private int m_nodeId;
	private int m_coreId;
	private long m_endPc;
	private Map<Long, InstructionNode> m_decodedInstructions;
	private List<InstructionNode> m_orderedInstructions;	
	private XeLoader m_xeLoader;
	private DataCollection m_dataCollection;
	
	public InstructionDecoder(int nodeId, int coreId, XeLoader xeLoader, Config config, DataCollection dataCollection) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_xeLoader = xeLoader;
		m_endPc = Long.MIN_VALUE;
		m_decodedInstructions = new HashMap<Long, InstructionNode>();
		m_orderedInstructions = new LinkedList<InstructionNode>();
		m_dataCollection = dataCollection;
	}
	
	public void decode() throws Exception {		
		MemoryImage memoryImage = m_xeLoader.getMemoryImage(m_nodeId, m_coreId);
		SymbolTable symbolTable = m_xeLoader.getSymbolTable(m_nodeId, m_coreId);
		XtaInstructionDecoder instructionDecoder = XtaInstructionDecoder.createInstructionDecoder(m_xeLoader.getArchitecture());
		if (instructionDecoder == null) {
			throw new Exception("Architecture not supported");		
		}
		
		for (ElfSection section: m_xeLoader.getSymbolTable(m_nodeId, m_coreId).getElfHandler().getSections()) {
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
		            
		            m_dataCollection.checkInstruction(inst);
		            
		            inst.setFunctionName(symbolTable.functionName(pc)); 
		            inst.setFunctionOffset(symbolTable.functionOffset(pc));
		            
		            InstructionNode instNode = new InstructionNode(inst);
		            Node prev = m_decodedInstructions.put(pc, instNode);
		            m_orderedInstructions.add(instNode);
		            assert(prev == null);
		            pc += inst.getNumBytes();
				}

				if (pc > m_endPc) {
					m_endPc = pc;
				}
			}
		}
	}
	
	public List<InstructionNode> getInstructions(){
		return m_orderedInstructions;
	}
	
	public InstructionNode getNode(long pc){
		return m_decodedInstructions.get(pc);
	}
	
	public long getEndPc() {
		return m_endPc;
	}
}
