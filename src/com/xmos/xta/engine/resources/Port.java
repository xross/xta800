package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.MachineState;

public class Port implements Resource {
	private boolean m_inUse;
	private int m_portNo;
	private int m_currentTime;
	private int m_ioTime;
	private boolean m_waitForTime; 
	
	enum Ready {
		NOREADY, STROBED, HANDSHAKEN
	};
	enum Buffers {
		BUFFERS , NOBUFFERS 
	};
	enum Mode {
		EVENT, INTERUPT
	};
	
	enum Status {
		MASTER, SLAVE
	};
	
	enum SampleDelay{
		NOSDELAY, SDELAY 
	};
	
	enum PortMode {
		DATAPORT, CLOCKPORT, READYPORT
	};
	
	enum DriveMode {
		INPUT, NOINVERT
	};
	
	enum Direction {
		INPUT, OUTPUT
	}
	
	private Direction m_direction;
	private DriveMode m_driveMode;
	private SampleDelay m_sampleDelay;
	private PortMode m_portMode;
	private Mode m_mode;
	private Buffers m_buffers;
	private Status m_status;
	private Ready m_ready;
	private Expression m_eventData;
	private Expression m_controlBits;
	private boolean m_eventEnabled;
    
    private Expression m_vector;
	
	public Port(int portNo) {
		m_buffers = Buffers.NOBUFFERS;
		m_status = Status.MASTER;
		m_sampleDelay = SampleDelay.NOSDELAY;
		m_ready = Ready.NOREADY;
		m_portMode = PortMode.DATAPORT;
		m_mode = Mode.EVENT;
		m_driveMode = DriveMode.NOINVERT;
		m_direction = Direction.INPUT;
		m_portNo = portNo;
    }
	
    public Port(Port port) {
    	m_portNo = port.m_portNo;
    }
    
    public int getCurrentTime(){
    	return m_currentTime;
    }
    
    public void setCurrentTime(int ct){
    	m_currentTime = ct;
    }
    
    public int getIOTime(){
    	return m_ioTime;
    }
    
    public void setIOTime(int io){
    	m_ioTime = io;
    }
    
    public void setWaitForTime(boolean wait){
    	m_waitForTime = wait;
    }
    public boolean getWaitForTime(){
    	return m_waitForTime;
    }
    
	public void setEventEnabeled(boolean b) {
		// TODO Auto-generated method stub
		
	}
	public boolean isEventEnabeled() {
		// TODO Auto-generated method stub
		return false;
	}
	public Expression getEventVector() {
		return m_vector;
	}
	
	public void setEventVector(Expression vector) {
		m_vector = vector;
	}
	public int getResourceID() {
		int id=0;
		id = Resources.RES_TYPE_SYNC;
		int regId = 0;
		id = id | (regId << 3);
		id = id | (m_portNo << 7);
		return id;
	}
	public void setInUse(boolean b) {
		m_inUse = b;
	}

	public boolean isInUse() {
		return m_inUse;
	}

}
