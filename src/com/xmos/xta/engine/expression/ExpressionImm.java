package com.xmos.xta.engine.expression;

import com.xmos.xta.engine.Expression;

public class ExpressionImm extends Expression implements ExpressionInterface{
	
	private int m_value;
	
	public ExpressionImm(int value){
		super();
		m_value = value;
	}
	
	public boolean canResolve(){
		return true;
	}
	
	public void resolve(){
		//do nothing
	}
	
	public int getValue(){
		return m_value;
	}

	public String getExpressionString() {
		return "[i " + m_value + "]";
	}
}
