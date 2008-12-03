package com.xmos.xta.engine;

import java.util.ArrayList;

public class BranchTarget {

	ArrayList<Integer> m_branchTargets;
	
	public BranchTarget(){
		m_branchTargets = new ArrayList<Integer>();
	}
	
	public void addBranchTarget(int target){
		m_branchTargets.add(target);
	}
	
	public ArrayList<Integer> getBranchTargets(){
		return m_branchTargets;
	}
	
	
}
