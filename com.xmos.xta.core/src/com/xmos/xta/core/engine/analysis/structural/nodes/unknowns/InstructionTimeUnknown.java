package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;

public class InstructionTimeUnknown extends Unknown {
	InstructionTimeUnknown(Node node, Type type, State state, Source source, Reference[] references) {
		super(node, type, state, source, references);
	}

	public String getDescription() {
		assert(getNode() instanceof InstructionNode);
		XtaInstruction instruction = ((InstructionNode)getNode()).getXtaInstruction();
		switch(getState()) {
		case UNKNOWN:
			return String.format("Time taken for instruction (0x%x: %s) is unknown",
					instruction.getPc(),
					instruction.getNmenonic());	
				
		case KNOWN:
			return String.format("Time for instruction (0x%x: %s) set to %.1fns",
					instruction.getPc(),
					instruction.getNmenonic(),
						getNode().getAttributes().getFixedTime().getNs());
		}	
		assert(false);
		return "";
	}

	public String getScriptStr(String routeIdStr, String[] refStrs) throws Exception {
    	if (getSource() == Source.IMPLICIT) {
    		throw new Exception("No script command avaliable (Not explicitly set by user)");
    	}
    	return String.format("set instructiontime %s %s %s\n", 
    			routeIdStr, refStrs[0], 
    			getNode().getAttributes().getFixedTimeString());
	}
}
