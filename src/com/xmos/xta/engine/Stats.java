package com.xmos.xta.engine;

import com.xmos.xta.engine.analysis.StructureGraph;
import com.xmos.xta.engine.analysis.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.nodes.Node;

public class Stats {
	private StructureGraph m_structureGraph;
	public Stats(StructureGraph structureGraph){
		m_structureGraph = structureGraph;
	}
	
	public void dumpFunctionCalls(){
		for(Node function : m_structureGraph.getFunctions()){
			FunctionNode fn = (FunctionNode)function;
		}
	}
	
	
}
