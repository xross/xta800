package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;

public class ClockBlock implements Resource {
    
    private boolean m_inUse;
    private Expression m_eventData;
    private Expression m_controlBits;
    public ClockBlock() {
        m_inUse = false;
    }
    public ClockBlock(ClockBlock clockBlock) {
        m_inUse = clockBlock.m_inUse;
        m_controlBits = clockBlock.m_controlBits;
        m_eventData = clockBlock.m_eventData; 
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

    public void FREER() {
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

    public void SETEV(Expression readRegister) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

    public void SETPSC(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

    public void SETPT(Expression expression) {
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
