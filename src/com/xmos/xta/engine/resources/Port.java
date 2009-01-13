package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;

public class Port extends EventableResource {
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
	
	public Port(int portNum) {
		super(Resources.RES_TYPE_PORT, portNum);
		m_buffers = Buffers.NOBUFFERS;
		m_status = Status.MASTER;
		m_sampleDelay = SampleDelay.NOSDELAY;
		m_ready = Ready.NOREADY;
		m_portMode = PortMode.DATAPORT;
		m_mode = Mode.EVENT;
		m_driveMode = DriveMode.NOINVERT;
		m_direction = Direction.INPUT;
    }
	
    public Port(Port port) {
    	super(port);
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
}
