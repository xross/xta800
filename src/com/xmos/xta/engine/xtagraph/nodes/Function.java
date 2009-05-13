package com.xmos.xta.engine.xtagraph.nodes;

import java.util.List;


public class Function extends Path {

	private String m_functionName;
	
	public Function(List<Node> nodes, String functionName) {
		super(nodes);
		m_functionName = functionName;
	}

	public String getFunctionName(){
		return m_functionName;
	}

	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.printf("function(%d)\n", m_id);
	}
}
