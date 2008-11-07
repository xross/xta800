package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.Registers;

public class Thread implements Resource{
    
    private int m_pc;
    
    private Registers m_registers;
    
    private boolean m_inUse; 
    
    public Thread(){
        m_inUse = false;
        m_registers = new Registers();
    }
    
    public Thread(Thread thread){
        m_inUse = thread.m_inUse;
        m_registers = new Registers(thread.m_registers);
        m_pc = thread.m_pc;
    }
    
    public Registers getRegisters(){
        return m_registers;
    }
    
    public boolean isInUse(){
        return m_inUse;
    }
    
    public void setInUse(boolean b) {
        m_inUse = b;
    }
    
    public void FREER() {
        m_inUse = false;
    }

    public void TINITCP(Expression expression) {
        
        
    }

    public void TINITDP(Expression expression) {
        
        
    }

    public void TINITLR(Expression expression) {
        
        
    }

    public void TINITPC(Expression expression) {
        
        
    }

    public void TINITSP(Expression expression) {
        
        
    }

    public void TSETR(Expression m_op2, Expression m_op3) {
        
        
    }

    public void TSTART() {
        
        
    }

    public void CHKCT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void CLRPT() {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void EDU() {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void EEF(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void EET(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void EEU() {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void ENDIN(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

   

    public void GETD(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void GETR(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void GETST(Expression dest) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void GETTS(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void IN(Expression destRegister) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void INCT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void INSHR(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void INT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void MJOIN(int resId, int resType) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void MSYNC(int resId, int resType) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void OUT(Expression scr1) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void OUTCT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void OUTSHR(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void OUTT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void PEEK(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void SETC(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void SETCLK(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void SETD(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void SETEV(Expression readRegister) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void SETPSC(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void SETPT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void SETRDY(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void SETTW(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void SETV(Expression readRegister) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void TESTCT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void TESTLCL(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }

    public void TESTWCT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
        
    }


    
   
}
