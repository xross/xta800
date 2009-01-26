package com.xmos.xta.engine.expression.operations;

import com.xmos.xta.engine.Expression;

public class Add {

	private Expression m_op1;
	private Expression m_op2;
	
	public Add(Expression op1, Expression op2){
		
	}

	
	public boolean canResolve() {
		return (m_op1.canResolve() && m_op2.canResolve());
	}
	/*
	public String getExpressionString() {
		return "(" + m_op1.getExpressionString() +" + " + m_op2.getExpressionString() + ")";
	}

	public int getValue() {
		if(canResolve()){
			return m_op1.getValue() + m_op2.getValue();
		}
		return 0;
	}
	*/
}
