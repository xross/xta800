package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;

public class Channel implements Resource {

    private boolean m_inUse; 
    
    private boolean m_eventEnabledKnown;
    private boolean m_eventEnabled;
    
    private Expression m_vector;
    private Expression m_environmentVector;
    private Expression m_controlBits;
    private Expression m_eventData;
    
    private Channel() {
        m_inUse = false;
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
    
    public void SETD(Expression expression) {
        m_eventData = expression;
    }

    public void SETC(Expression expression) {
        m_controlBits = expression;
    }
    
    public void GETD( Expression expression) {
        expression = m_eventData;
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
    
    public void IN(Expression expression) {
         
    }
    
    public void OUT(Expression scr1) {
        
    }
    
    public void INT(Expression expression) {
        
    }

    public void INCT(Expression expression) {
        
    }
    
    public void OUTT(Expression expression) {

    }

    public void OUTCT(Expression expression) {
        
    }
    
    public void TESTCT(Expression expression) {

        
    }

    public void TESTLCL(Expression expression) {

        
    }

    public void TESTWCT(Expression expression) {

        
    }
    
    public void CHKCT(Expression expression) {

        
    }
    

    public void CLRPT(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }
    
    public void ENDIN(Expression expression) {
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

    public void INSHR(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }
    
    public void MJOIN(int resId, int resType) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

    public void MSYNC(int resId, int resType) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

    public void OUTSHR(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");
    }

    public void PEEK(Expression expression) {
        System.err.println("ILLEGAL_RESOURCE exception");  
    }

    public void SETCLK(Expression expression) {
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
