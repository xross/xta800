package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.MachineState;

public class Channel implements Resource {

    private boolean m_inUse; 
    private int m_channelNumber;
    
    private boolean m_eventEnabledKnown;
    private boolean m_eventEnabled;
    
    private Expression m_vector;
    private Expression m_environmentVector;
    private Expression m_controlBits;
    private Expression m_eventData;
    
    public Channel(int channelNumber) {
        m_inUse = false;
        m_channelNumber = channelNumber;
    }
    
    public Channel(Channel channel) {
        m_inUse = channel.m_inUse;
        m_eventEnabledKnown = channel.m_inUse;
        m_eventEnabled = channel.m_inUse;
        m_vector = channel.m_vector;
        m_environmentVector = channel.m_environmentVector;
        m_controlBits = channel.m_controlBits;
        m_eventData = channel.m_eventData; 
        m_channelNumber = channel.m_channelNumber;
    }
    
    
    public void GETR(int dest, int scr) {
    	m_inUse = true;
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
    

    public void CLRPT() {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }
    
    public void ENDIN(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------"); 
    }

    

    public void GETST(Expression dest) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void GETTS(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void INSHR(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }
    
    public void MJOIN(int resId, int resType) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void MSYNC(int resId, int resType) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void OUTSHR(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void PEEK(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");  
    }

    public void SETCLK(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void SETPSC(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void SETPT(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void SETRDY(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");
    }

    public void SETTW(Expression expression) {
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

	public int getProcessor() {
		return 0;
	}

	public int getNode() {
		return 0;
	}

	public int getNumber() {
		return m_channelNumber;
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
