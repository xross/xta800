package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;

public class Channel extends EventableResource {

    private Expression m_controlBits;
    private Expression m_eventData;
    
    private int m_node;
    private int m_core;
    
    public Channel(int channelNum, int node, int core) {
    	super(Resources.RES_TYPE_CHANEND, channelNum);
    	m_node = node;
    	m_core = core;
    }
    
    public Channel(Channel channel) {
    	super(channel);
    	m_node = channel.m_node;
    	m_core = channel.m_core;
    	
        m_controlBits = channel.m_controlBits;
        m_eventData = channel.m_eventData; 
    }
    
	public int getResourceID() {
		int resId = super.getResourceID();
		resId = resId | (m_core << 16) | (m_node << 24);
		return resId;
	}
}
