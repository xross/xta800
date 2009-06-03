package com.xmos.xta.engine.analysis.controlflow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.xmos.elfhandler.ElfSection;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.instructions.XtaInstructionDecoder;

public class InstructionDecoder {
	private int m_nodeId;
	private int m_coreId;
	private Map<Integer, Node> m_decodedInstructions;
	private XeLoader m_xeLoader;
	private Config m_config;
	
	public InstructionDecoder(int nodeId, int coreId, XeLoader xeLoader, Config config) {
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_xeLoader = xeLoader;
		m_config = config;
		m_decodedInstructions = new HashMap<Integer, Node>();
	}
	
	public boolean decode(){		
		MemoryImage memoryImage = m_xeLoader.getMemoryImage(m_nodeId, m_coreId);
		SymbolTable symbolTable = m_xeLoader.getSymbolTable(m_nodeId, m_coreId);
		XtaInstructionDecoder instructionDecoder = XtaInstructionDecoder.createInstructionDecoder(m_xeLoader.getArchitecture());
		for (ElfSection section: m_xeLoader.getSymbolTable(m_nodeId, m_coreId).getElfHandler().getSections()){
			if (section.isExecutable()){
				int pc = section.getAddress();
				while (pc < section.getAddress() + section.getBytes().length){
					int opcode = 0;
					try {
		                opcode = memoryImage.readInstructionWord(pc);
		            } catch (Exception e) {
		            	return false;
		            }
		            
		            XtaInstruction inst = instructionDecoder.decode(opcode, pc, m_config);
		            inst.setFunctionName(symbolTable.functionName(pc)); 
		            //FIXME - this will do done is a lazy style when needed
		            //inst.setFunctionOffset(m_symbolTable.functionOffset(pc));
		            
		            Node instNode = new InstructionNode(inst);
		            Node prev = m_decodedInstructions.put(pc, instNode);
		            assert(prev == null);
		            pc += inst.getNumBytes();
				}
			}
		}
		return true;
	}
	
	public Collection<Node> getInstructions(){
		return m_decodedInstructions.values();
	}
	
	public Node getNode(int pc){
		return m_decodedInstructions.get(pc);
	}
}
