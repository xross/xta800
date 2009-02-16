package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.TreeSet;

public class FunctionReturns {

	private int m_node;
	private int m_core;
	private HashMap<Integer, TreeSet<Integer>> m_functionReturns;
	
	public FunctionReturns(int m_node, int m_core) {
		super();
		this.m_node = m_node;
		this.m_core = m_core;
		m_functionReturns = new HashMap<Integer, TreeSet<Integer>>();
	}

	public int getNodeId(){
		return m_node;
	}
	
	public int getCoreId(){
		return m_core;
	}
	
	
	public void addFunction(int from, int to){
		if(m_functionReturns.containsKey(from)){
			TreeSet<Integer> function = m_functionReturns.get(from);
			function.add(to);
		} else {
			TreeSet<Integer> function = new TreeSet<Integer>();
			
			function.add(to);
			m_functionReturns.put(from, function);
		}
	}
	
	public TreeSet<Integer> getFunctionReturns(int startPc){
		TreeSet<Integer> set = m_functionReturns.get(startPc);
		if(set == null){
			return new TreeSet<Integer>();
		} else {
			return set;
		}
	}
}
