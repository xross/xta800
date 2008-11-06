package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;

public class Synchroniser implements Resource {
    private boolean m_inUse; 
    
    private boolean m_eventEnabledKnown;
    private boolean m_eventEnabled;
    
    private Expression m_vector;
    private Expression m_environmentVector;
    private Expression m_controlBits;
    private Expression m_eventData;
    
    public Synchroniser() {
        m_inUse = false;
    }

    public void FREER() {
        m_inUse = false;
    }

    public void GETST(Expression dest) {
        //TODO
    }

    public void MJOIN(int resId, int resType) {
      //TODO
    }

    public void MSYNC(int resId, int resType) {
      //TODO
    }

    public void CHKCT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void CLRPT(Expression expression) {
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

    public void TINITCP(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

    public void TINITDP(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

    public void TINITLR(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");   
    }

    public void TINITPC(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

    public void TINITSP(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

    public void TSETR(Expression m_op2, Expression m_op3) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

    public void TSTART() {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

}
