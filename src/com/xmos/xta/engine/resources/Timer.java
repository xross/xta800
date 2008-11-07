package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;

public class Timer implements Resource {
    private boolean m_inUse; 
    
    private boolean m_eventEnabledKnown;
    private boolean m_eventEnabled;
    
    private Expression m_vector;
    private Expression m_environmentVector;
    private Expression m_controlBits;
    private Expression m_eventData;
    
    public Timer() {
        m_inUse = false;
    }

    public Timer(Timer timer) {
        m_inUse = timer.m_inUse;
        m_eventEnabledKnown = timer.m_eventEnabledKnown;
        m_vector= timer.m_vector;
        m_eventEnabled= timer.m_eventEnabled;
        m_environmentVector= timer.m_environmentVector;
        m_controlBits= timer.m_controlBits;
        m_eventData = timer.m_eventData;
    }
    
    public void FREER() {
        m_inUse = false;
    }

    public void SETV(Expression register) {
        m_vector = register;
    }
    
    public void SETEV(Expression register) {
        m_environmentVector = register;  
    }

    public void SETCLK(Expression expression) {
        //TODO
    }

    public void SETRDY(Expression expression) {
        //TODO
    }

    public void EDU() {
        m_eventEnabledKnown = true;
        m_eventEnabled = false;
    }

    public void EEF( Expression expression) {
        if(expression.canResolve()){
            m_eventEnabledKnown = true;
            if(expression.getValue() == 0){
                m_eventEnabled = false;
            } else {
                m_eventEnabled = true;
            }
        } else {
            m_eventEnabledKnown = false;
            System.err.println("could not resolve an EEF");
        }
        
    }

    public void EET( Expression expression) {
        if(expression.canResolve()){
            m_eventEnabledKnown = true;
            if(expression.getValue() == 0){
                m_eventEnabled = true;
            } else {
                m_eventEnabled = false;
            }
        } else {
            m_eventEnabledKnown = false;
            System.err.println("could not resolve an EEF");
        }
        
    }

    public void EEU() {
        m_eventEnabledKnown = true;
        m_eventEnabled = true;
    }

    public void IN(Expression destRegister) {
        //TODO
    }

    public void CHKCT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void CLRPT() {
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

    public void SETD(Expression expression) {
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
