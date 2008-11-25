package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.MachineState;

public class Lock implements Resource {
	
	private int m_lockNo;
    private Expression m_vector;
    private Expression m_environmentVector;
    
    private boolean m_inUse; 
    
    public Lock(int lockNo) {
        m_inUse = false;
        m_lockNo = lockNo;
    }

    public Lock(Lock lock) {
        m_inUse = lock.m_inUse;
        m_vector = lock.m_vector;
        m_environmentVector = lock.m_environmentVector;
        m_lockNo = lock.m_lockNo;
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
		id = id | (regId << 3);
		id = id | (m_lockNo << 7);
		return id;
	}


}
