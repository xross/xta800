package com.xmos.xta.engine.xtagraph.nodes;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.XtaInstruction;

public class PseudoFunction extends Node {

	@Override
	public List<XtaInstruction> getInstructionStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<XtaInstruction> getInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberCycles() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberFNOPS() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<Integer> getPCsCovered() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node clone() {
		return new PseudoFunction();
	}

	@Override
	public Collection<Node> getContaintedNodes() {
		// TODO Auto-generated method stub
		return null;
	}



	public String getGraphvizName() {
		return "cluster_PseudoFunction" + m_id;
	}

	@Override
	public void appendToGraph(StringBuffer body, StringBuffer connections, Collection<Node> last, Collection<Node> next, int indent, Node parent) {
		// TODO Auto-generated method stub
		
	}


}
