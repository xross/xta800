package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.MachineState;

public class Port implements Resource {
	private boolean m_inUse;
	private int m_portNo;
	private Expression m_eventData;
	private Expression m_controlBits;
    public Port(int portNo) {
    	m_portNo = portNo;
    }
    public Port(Port port) {
    	m_portNo = port.m_portNo;
    }
    public boolean isInUse() {
		return m_inUse;
	}

	public void setInUse(boolean inUse) {
		m_inUse = inUse;
	}
    
    public void SETV(Expression readRegister) {

    }

    public void SETEV(Expression readRegister) {

    }

    public void GETD(Expression expression) {

    }

    public void SETC(Expression expression) {
    	m_controlBits = expression;
    }

    public void SETD(Expression expression) {
    	m_eventData = expression;
    }

    public void SETCLK(Expression expression) {

    }

    public void SETRDY(Expression expression) {

    }

    public void SETPT(Expression expression) {

    }

    public void GETTS(Expression expression) {

    }

    public void INSHR(Expression expression) {

    }

    public void OUTSHR(Expression expression) {

    }

    public void PEEK(Expression expression) {

    }

    public void SETTW(Expression expression) {

    }

    public void SETPSC(Expression expression) {

    }

    public void ENDIN(Expression expression) {

    }

    public void CLRPT() {

    }

    public void EDU() {

    }

    public void EEF(Expression expression) {

    }

    public void EET(Expression expression) {

    }

    public void EEU() {

    }

    public void IN(Expression destRegister) {

    }

    public void OUT(Expression scr1) {

    }

    public void CHKCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");

    }

    public void FREER() {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");

    }

    public void GETR(int dest, int scr) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");

    }

    public void GETST(Expression dest) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");

    }

    public void INCT(Expression expression) {
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

    public void OUTCT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");

    }

    public void OUTT(Expression expression) {
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
	public int getNumber() {
		return m_portNo;
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
