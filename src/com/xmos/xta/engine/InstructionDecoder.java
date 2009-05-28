package com.xmos.xta.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.xmos.elfhandler.ElfSection;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.analysis.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.nodes.Node;

public class InstructionDecoder {

	private MemoryImage m_mi;
	private Map<Integer, Node> m_decodedInstructions;
	private XtaInstructionDecoder m_instructionDecoder;
	private XeLoader.Architecture m_architecture;
	private XeLoader m_xeLoader;
	private Config m_config;
	private SymbolTable m_symbolTable;
	
	public InstructionDecoder(MemoryImage mi, XeLoader xeLoader, Config config, SymbolTable symbolTable) {
		m_mi = mi;
		m_architecture = xeLoader.getArchitecture();
		m_xeLoader = xeLoader;
		m_config = config;
		m_symbolTable = symbolTable;
	}
	
	public boolean decode(){
		m_decodedInstructions = new HashMap<Integer, Node>();
		m_instructionDecoder = XtaInstructionDecoder.createInstructionDecoder(m_architecture);
		for(ElfSection section: m_xeLoader.getSymbolTable(m_mi.getNodeId(), m_mi.getCoreId()).getElfHandler().getSections()){
			if(section.isExecutable()){
				int pc = section.getAddress();
				while(pc < section.getAddress() + section.getBytes().length){
					int opcode = 0;
					try {
		                opcode = m_mi.readInstructionWord(pc);
		            } catch (Exception e) {
		            	return false;
		            }
		            XtaInstruction inst = m_instructionDecoder.decode(opcode, pc, m_config);
		            inst.setFunctionName(m_symbolTable.functionName(pc));
		            inst.setFunctionOffset(m_symbolTable.functionOffset(pc));
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
