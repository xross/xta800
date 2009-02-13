package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.HashSet;

public class FunctionReturns {

	private int m_node;
	private int m_core;
	private HashMap<String, HashSet<Integer>> m_functionReturns;
	
	public FunctionReturns(int m_node, int m_core,
			HashMap<String, HashSet<Integer>> returns) {
		super();
		this.m_node = m_node;
		this.m_core = m_core;
		m_functionReturns = returns;
	}

	public int getNode(){
		return m_node;
	}
	
	public int getCore(){
		return m_core;
	}
	
	public HashSet<Integer> getFunctionReturns(String functionName){
		HashSet<Integer> set = m_functionReturns.get(functionName);
		if(set == null){
			return new HashSet<Integer>();
		} else {
			return set;
		}
	}
}
