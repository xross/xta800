package com.xmos.xta.engine.expression;

public interface ExpressionInterface {
	public boolean canResolve();
	public int getValue();
	public String getExpressionString();
	
}
