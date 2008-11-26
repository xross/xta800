package com.xmos.xta.engine.resources;

import java.util.ArrayList;
import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.MachineState;

public class Synchroniser implements Resource {
    
	private boolean m_inUse; 
	private int m_syncId;
	private int m_masterThreadId;
    private ArrayList<Thread> m_slaveList;
    private boolean m_mJoin;
    private boolean m_mSynced;
    
    
    public Synchroniser(int syncId) {
    	m_syncId = syncId;
        m_inUse = false;
        m_slaveList = new ArrayList<Thread>();
    }
    public Synchroniser(Synchroniser sync) {
        m_inUse = sync.m_inUse;
        m_syncId = sync.m_syncId;
        m_slaveList = new ArrayList<Thread>(sync.m_slaveList);
    }
    
	public void setInUse(boolean b) {
		m_inUse = b;
	}

	public boolean isInUse() {
		return m_inUse;
	}

	public int getResourceID() {
		int id=0;
		id = Resources.RES_TYPE_SYNC;
		int regId = 0;
		id = id | (regId << 4);
		id = id | (m_syncId << 8);
		return id;
	}

	public void setMaster(int thread) {
		m_masterThreadId = thread;
	}
	public void attachThread(Thread res) {
		m_slaveList.add(res);
	}
}
