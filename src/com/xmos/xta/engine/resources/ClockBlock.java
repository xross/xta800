package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.MachineState;

public class ClockBlock implements Resource {
	
	
    private int m_clockNumber;
    private boolean m_inUse;
    private Expression m_eventData;
    private Expression m_controlBits;
    
    public ClockBlock(int clockNumber) {
        m_inUse = false;
        m_clockNumber = clockNumber;
    }
    public ClockBlock(ClockBlock clockBlock) {
        m_inUse = clockBlock.m_inUse;
        m_controlBits = clockBlock.m_controlBits;
        m_eventData = clockBlock.m_eventData; 
        m_clockNumber = clockBlock.m_clockNumber;
    }
    
    public void FREER() {
        m_inUse = false;
    }
    
    public void SETRDY(Expression expression) {
        //TODO
    }
    
    public void SETCLK(Expression expression) {
        //TODO
    }

    public void GETD(Expression expression) {
        expression = m_eventData;
    }

    public void SETD(Expression expression) {
        m_eventData = expression;
    }

    public void SETC(Expression expression) {
        m_controlBits = expression;
    }

    public void CHKCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void CLRPT() {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void EDU() {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void EEF(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void EET(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void EEU() {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void ENDIN(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    

    public void GETR(int dest, int scr) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void GETST(Expression dest) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void GETTS(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void IN(Expression destRegister) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void INCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void INSHR(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void INT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void MJOIN(int resId, int resType) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void MSYNC(int resId, int resType) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void OUT(Expression scr1) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void OUTCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void OUTSHR(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void OUTT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void PEEK(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void SETEV(Expression readRegister) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void SETPSC(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void SETPT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void SETTW(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void SETV(Expression readRegister) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void TESTCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void TESTLCL(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void TESTWCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void TINITCP(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void TINITDP(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void TINITLR(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");   
    }

    public void TINITPC(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void TINITSP(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void TSETR(Expression m_op2, Expression m_op3) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void TSTART() {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }
    public boolean isInUse() {
		return m_inUse;
	}

	public void setInUse(boolean inUse) {
		m_inUse = inUse;
	}
	public int getNumber() {
		return m_clockNumber;
	}
	public int getPortWidth() {
		return 0;
	}
	public int getRegId() {
		return 0;
	}
	public void GETST(MachineState state) {
		// TODO Auto-generated method stub
		
	}
	public void MJOIN(MachineState state) {
		// TODO Auto-generated method stub
		
	}
	public void MSYNC(MachineState state) {
		// TODO Auto-generated method stub
		
	}

}
