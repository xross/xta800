package com.xmos.xta.engine;

import com.xmos.xta.engine.expression.Expression;

public class Registers {
	final static int NUM_REGISTERS = 16;	
	private Expression[] m_registers;

	
	
	public Registers() { 
	    m_registers = new Expression[NUM_REGISTERS];	
		for (int regIndex = 0; regIndex < NUM_REGISTERS; ++regIndex) {
			m_registers[regIndex] = new Expression(regIndex, 0, 
			    Expression.Type.REGISTER, Expression.Status.UNDEFINED);
		}
	}

    public Registers(Registers registers) {
        m_registers = new Expression[NUM_REGISTERS];    
        for (int regIndex = 0; regIndex < NUM_REGISTERS; ++regIndex) {
            m_registers[regIndex] = new Expression(registers.m_registers[regIndex]);
        }
    }
	
	Expression readRegister(int regNum) { 
		if(m_registers[regNum] == null){
			return new Expression(regNum, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
		} else {
			return m_registers[regNum]; 
		}
	}
	
	void writeRegister(int regNum, Expression regValue) {
		if(regValue == null){
			m_registers[regNum] = new Expression(regNum, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
		} else {
			
			//if(!regValue.canResolve())
			//	m_registers[regNum] = null;
			//else 
				m_registers[regNum] = regValue;
		}	
	}
	
	public Expression[] getRegisters(){
		return m_registers;
	}
	
	public String toString(){
		String out = "" ;
		for(int i=0;i<NUM_REGISTERS;i++){
			Expression reg = m_registers[i];
			if(reg.canResolve()){
				try {
					out +="r" + i + " " + Integer.toHexString(reg.getValue()) + "\t";
				} catch (Exception e) {
					out +="r" + i + " unknown\t";
				}
			}
		}
		return out;
	}
}
