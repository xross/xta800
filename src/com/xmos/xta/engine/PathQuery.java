package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.TreeSet;

public class PathQuery {

	private ArrayList<PathBasicBlock> m_blocks;
	private String m_expression;
	private String m_queryName;
	
	public PathQuery(String expression, String name) {
		super();
		m_expression = expression;
		m_queryName = name;
		m_blocks = new ArrayList<PathBasicBlock>();
	}
	
	public void resolve(Route route){
		for(PathBasicBlock path : route.getCompletedPaths()){
			TreeSet<PathBasicBlock> allBlocks = new TreeSet<PathBasicBlock>();
			path.getAllBlocks(allBlocks);
			//for(Block block : allBlocks){
				//do something with the expressions here				
			//}
		}
	}
	
	
	
	public ArrayList<PathBasicBlock> getBlocks(){
		return m_blocks;
	}

	public String getName() {
		return m_queryName;
	}

	public String getExpression() {
		return m_expression;
	}
	
	
}
