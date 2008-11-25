package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.MachineState;

public class ClockBlock implements Resource {
	
	
    private int m_clockNumber;
    private boolean m_inUse;
    private Expression m_eventData;
    private Expression m_controlBits;
    
    public ClockBlock(int clockNumber) {
        m_inUse = false;
        m_clockNumber = clockNumber;
    }
    public ClockBlock(ClockBlock clockBlock) {
        m_inUse = clockBlock.m_inUse;
        m_controlBits = clockBlock.m_controlBits;
        m_eventData = clockBlock.m_eventData; 
        m_clockNumber = clockBlock.m_clockNumber;
    }

    public boolean isInUse() {
		return m_inUse;
	}

	public void setInUse(boolean inUse) {
		m_inUse = inUse;
	}

	public int getResourceID() {
		int id=0;
		id = Resources.RES_TYPE_SYNC;
		int regId = 0;
		id = id | (regId << 3);
		id = id | (m_clockNumber << 7);
		return id;
	}

}
