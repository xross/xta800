package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;

public class Channel implements Resource {

    private boolean m_inUse; 
    private int m_channelNumber;
    
    private boolean m_eventEnabledKnown;
    private boolean m_eventEnabled;
    
    private Expression m_vector;
    private Expression m_environmentVector;
    private Expression m_controlBits;
    private Expression m_eventData;
    
    private int m_processor;
    private int m_node;
    
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
    
    public boolean isEventEnabeled() {
		return m_eventEnabled;
	}
    
    public Expression getEventVector() {
		return m_vector;
	}
	
	public void setEventVector(Expression vector) {
		m_vector = vector;
	}

	public boolean isInUse() {
		return m_inUse;
	}

	public void setInUse(boolean inUse) {
		m_inUse = inUse;
	}

	public void setEventEnabeled(boolean b) {
		m_eventEnabledKnown = true;
        m_eventEnabled = b;
	}

	public int getResourceID() {
		int id=0;
		id = Resources.RES_TYPE_CHANEND;
		int regId = 0;
		id = id | (regId << 3);
		id = id | (m_channelNumber << 7);
		id = id | (m_processor<<15);
		id = id | (m_node<<23);
		return id;
	}


}
