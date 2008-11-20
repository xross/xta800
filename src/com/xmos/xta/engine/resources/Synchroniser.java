package com.xmos.xta.engine.resources;

import java.util.ArrayList;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.MachineState;
import com.xmos.xta.engine.resources.Thread.ControlBits;
import com.xmos.xta.engine.resources.Thread.StatusRegister;

public class Synchroniser implements Resource {
    private boolean m_inUse; 
    private int m_ownT;
    private ArrayList<Thread> m_slaveList;
    private boolean m_mJoin;
    private boolean m_mSynced;
    private int m_syncNo;
    
    private ArrayList<Thread> m_threads;
    
    
    public Synchroniser(int syncNo) {
    	m_syncNo = syncNo;
        m_inUse = false;
        m_threads = new ArrayList<Thread>();
    }
    public Synchroniser(Synchroniser sync) {
        m_inUse = false;
        m_syncNo = sync.m_syncNo;
        m_threads = new ArrayList<Thread>(sync.m_threads);
    }
    public boolean isInUse() {
		return m_inUse;
	}
	public void setInUse(boolean inUse) {
		m_inUse = inUse;
	}
	
	public boolean getJoin() {
		return m_mJoin;
	}
	
	public void setMJoin(boolean mJoin) {
		m_mJoin = mJoin;
	}
	
	public ArrayList<Thread> getThreads(){
		return m_threads;
	}
	public void addThread(Thread thread){
		m_threads.add(thread);
	}
	public void removeThread(Thread thread){
		m_threads.remove(thread);
	}
	
	public void setMSynced(boolean mSynced) {
		m_mSynced = mSynced;
	}
	public boolean isMSynced() {
		return m_mSynced;
	}
	public void trap(){
		System.out.println("---------ILLEGAL_RESOURCE exception-------");
	}
	
    public void FREER() {
    	if(m_slaveList.isEmpty()){
    		m_inUse = false;
    	} else {
    		trap();
    	}
    }
    
    public void setOwnT(int ownT) {
		m_ownT = ownT;
	}
    
	public int getOwnT() {
		return m_ownT;
	}
	
    public void GETST(MachineState state) {
    	Resources resources = state.getResources();
    	Resource newThread  = resources.getNewResource(Resources.RES_TYPE_THREAD);
        if(newThread == null) {
        	state.m_op1 = new Expression(0);
        } else {
        	int regId = newThread.getRegId();
        	int resNumber = newThread.getNumber();
        	int portWidth = newThread.getPortWidth();
        	int resourceValue = 0;
        	resourceValue = resources.getResID(Resources.RES_TYPE_THREAD, 
        			regId, resNumber, portWidth);	
        	newThread.setInUse(true);
        	addThread((Thread)newThread);
        	state.m_op1 = new Expression(resourceValue);
        }
    }
        
	public void MJOIN(MachineState state) {
		//Block thisBlock = state.getBlock();
        setMJoin(true);
        setMSynced(true);
        //thisBlock.getBlockThread().setStatusRegisterBit(StatusRegister.EEBLE, false);
        //thisBlock.getBlockThread().setControlBit(ControlBits.MSYNC, true);
	}

    public void MSYNC(MachineState state) {
    	//Block thisBlock = state.getBlock();
    	setMJoin(false);
        setMSynced(true);
        //thisBlock.getBlockThread().setStatusRegisterBit(StatusRegister.EEBLE, false);
        //thisBlock.getBlockThread().setControlBit(ControlBits.MSYNC, true);
    }//

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

    public void GETD(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");

    }

    public void GETR(int dest, int scr) {
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

    public void SETC(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");

    }

    public void SETCLK(Expression expression) {
        System.out.println("---------ILLEGAL_RESOURCE exception-------");

    }

    public void SETD(Expression expression) {
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

    public void SETRDY(Expression expression) {
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
	public int getNumber() {
		return m_syncNo;
	}
	public int getPortWidth() {
		return 0;
	}
	public int getRegId() {
		return 0;
	}

	
	

}
