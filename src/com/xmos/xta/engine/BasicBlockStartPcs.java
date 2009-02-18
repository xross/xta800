package com.xmos.xta.engine;

import java.util.HashMap;

public class BasicBlockStartPcs {
	private HashMap<Integer, Node> m_pcToNode ;
	private int m_node;
	private int m_core;
	
	
	public BasicBlockStartPcs(int node, int core){
		m_node = node;
		m_core = core;
		m_pcToNode = new HashMap<Integer, Node>();
	}
	
	public HashMap<Integer, Node> getBasicBlockMap(){
		return m_pcToNode;
	}
	
	public int getNodeId(){
		return m_node;
	}
	public int getCoreId(){
		return m_core;
	}
}
