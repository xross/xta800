package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.TreeSet;

public class PathQuery {

	private ArrayList<Block> m_blocks;
	private String m_expression;
	private String m_queryName;
	
	public PathQuery(String expression, String name) {
		super();
		m_expression = expression;
		m_queryName = name;
		m_blocks = new ArrayList<Block>();
	}
	
	public void resolve(Route route){
		for(Block path : route.completePaths()){
			TreeSet<Block> allBlocks = new TreeSet<Block>();
			path.getAllBlocks(allBlocks);
			//for(Block block : allBlocks){
				//do something with the expressions here				
			//}
		}
	}
	
	
	
	public ArrayList<Block> getBlocks(){
		return m_blocks;
	}

	public String getName() {
		return m_queryName;
	}

	public String getExpression() {
		return m_expression;
	}
	
	
}
