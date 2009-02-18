package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.TreeSet;

public class FunctionCallLocations {
	private HashMap<Integer, TreeSet<Integer>> m_functionCallLocations;
	private int m_nodeId;
	private int m_coreId;
	
	public FunctionCallLocations(int nodeId, int coreId){
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_functionCallLocations  = new HashMap<Integer, TreeSet<Integer>>();
	}
	
	public int getNodeId(){
		return m_nodeId;
	}
	
	public int getCordId(){
		return m_coreId;
	}
	
	public HashMap<Integer, TreeSet<Integer>> getFunctionCallLocations(){
		return m_functionCallLocations;
	}

}
