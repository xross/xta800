package com.xmos.xta.engine.analysis.nodes;

public class SelfLoopNode extends LoopNode {

	private Node m_loop; 
	
	public SelfLoopNode(Node loop){
		super(loop, loop);
		m_loop = loop;
	}
	
	public void display(int indentLevel) {
		printIndent(indentLevel);
		System.out.printf("selfLoop(%d)\n", m_id);	
		m_loop.display(indentLevel+1); 
	}
}
