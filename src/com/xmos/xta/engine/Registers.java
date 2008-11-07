package com.xmos.xta.engine;

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
		return m_registers[regNum]; 
	}
	
	void writeRegister(int regNum, Expression regValue) {
		m_registers[regNum] = regValue;		
		m_registers[regNum].resolve();
	}
}
