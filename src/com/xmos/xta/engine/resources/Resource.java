package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.MachineState;

public interface Resource {
	
	public boolean isInUse();
	public void setInUse(boolean inUse);
	
    public void GETR(int dest, int scr);
    
    
    
    
    
    public void TINITCP(Expression expression);
    public void TINITDP(Expression expression);
    public void TINITSP(Expression expression);
    public void TINITPC(Expression expression);
    public void TINITLR(Expression expression);
    public void TSTART();
    

    public void SETV(Expression readRegister);
    public void SETEV(Expression readRegister);
    
    public void SETD(Expression expression);
    public void GETD(Expression expression);
    public void SETC(Expression expression);
    
    public void SETCLK(Expression expression);
    public void SETRDY(Expression expression);
    
    public void SETPT(Expression expression);
    public void GETTS(Expression expression);
    public void INSHR(Expression expression);
    public void OUTSHR(Expression expression);
    public void PEEK(Expression expression);
    public void SETTW(Expression expression);
    public void SETPSC(Expression expression);
    public void ENDIN(Expression expression);
    public void CLRPT();
    
    public void EEU();
    public void EDU();
    public void EET(Expression expression);
    public void EEF(Expression expression);
    
    public void IN(Expression destRegister);
    
    public void OUT(Expression scr1);
    
    public void INT(Expression expression);
    public void INCT(Expression expression);
    public void OUTT(Expression expression);
    public void OUTCT(Expression expression);
    public void TESTCT(Expression expression);
    public void TESTWCT(Expression expression);
    public void CHKCT(Expression expression);
    public void TESTLCL(Expression expression);

    public void TSETR(Expression m_op2, Expression m_op3);
	public int getRegId();
	public int getNumber();
	public int getPortWidth();
	
	public void MJOIN(MachineState state);
	public void MSYNC(MachineState state);
	public void FREER();
    public void GETST(MachineState state);
    
}
