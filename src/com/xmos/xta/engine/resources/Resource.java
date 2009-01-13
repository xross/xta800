package com.xmos.xta.engine.resources;

public class Resource {

	protected int m_resourceNumber;
	protected int m_resourceType;
	protected int m_resourceId;
	protected int m_masterThreadId;
	protected boolean m_isInUse;
	protected int m_data;

	Resource(int resourceType, int resourceNumber) {
		m_resourceType = resourceType;
		m_resourceId = resourceNumber << 8 | resourceType;
		m_resourceNumber = resourceNumber;
		m_isInUse = false;
	}
	
	Resource(Resource res) {
		m_resourceType = res.m_resourceType;
		m_resourceId = res.m_resourceId;
		m_resourceNumber = res.m_resourceNumber;
		m_isInUse = res.m_isInUse;
	}

	boolean isInUse() {
		return m_isInUse;
	}
	public void setInUse(boolean isInUse) {
		m_isInUse = isInUse;
	}
	
	public int getResourceID() {
		return m_resourceId;
	}

	public void setMaster(int thread) {
		m_masterThreadId = thread;
	}
	
	public void setD(int data) {
		m_data = data;
	}
	
	public int getD() {
		return m_data;
	}
	
}
