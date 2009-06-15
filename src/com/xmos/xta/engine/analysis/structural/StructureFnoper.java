package com.xmos.xta.engine.analysis.structural;

import java.util.LinkedList;
import java.util.List;

import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class StructureFnoper {

	public void analyse(Node decomposedGraph, boolean isFunction) {
		
		
		//decomposedGraph.addFnops();
	}

	
	public static List<Integer> makeFnops(Node node, InstructionBuffer copy){
		List<Integer> fnops = new LinkedList<Integer>();
		return fnops;
	}
}
