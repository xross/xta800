package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.utils.Conversion;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;

public class InstructionTimeUnknown extends Unknown {
	InstructionTimeUnknown(Route route, Node node, Type type, State state, Source source, Reference[] references) {
		super(route, node, type, state, source, references);
	}

	public String getDescription() {
		assert(getNode() instanceof InstructionNode);
		Instruction instruction = ((InstructionNode)getNode()).getInstruction();
		switch(getState()) {
		case UNKNOWN:
			return String.format("Time taken for instruction (0x%x: %s) is unknown",
					instruction.getPc(),
					instruction.getMnemonic());

		case KNOWN:
			return String.format("Time for instruction (0x%x: %s) set to %s",
					instruction.getPc(),
					instruction.getMnemonic(),
					Conversion.getTimeString(getNode().getAttributes().getFixedTime().getNs()));
		}
		assert(false);
		return "";
	}

	public String getScriptStr(String routeIdStr, String[] refStrs) {
    	assert(getSource() == Source.SET);
    	return String.format("set instructiontime %s %s %s",
    			routeIdStr, refStrs[0],
    			getNode().getAttributes().getFixedTimeString());
	}
	
    public String getValueStr() {
    	return getNode().getAttributes().getFixedTimeString();
    }	
}
