package com.xmos.xta.engine.analysis.routes;

import com.xmos.xta.engine.references.Reference;

public class RouteDescription {
	private String m_description;
	private String m_functionName;
	private int m_nodeId;
	private int m_coreId; 
	private RouteMode m_mode;
	private Reference m_from;
	private Reference m_to;

	enum RouteMode {
		PATH, FUNCTION
	};

	public RouteDescription(Reference from, Reference to, int nodeId, int coreId) {
		m_description = String.format("path: %s to %s", from.getRef(), to.getRef());
		m_mode = RouteMode.PATH;
		m_from = from;
		m_to = to;
		m_nodeId = nodeId;
		m_coreId = coreId;
	}

	public RouteDescription(String functionName, int nodeId, int coreId) {
		m_description = String.format("function: %s", functionName);
		m_functionName = functionName;
		m_mode = RouteMode.FUNCTION;
		m_from = null;
		m_to = null;
		m_nodeId = nodeId;
		m_coreId = coreId;
	}

	public int getNodeId(){
		return m_nodeId;
	}
	
	public int getCoreId(){
		return m_coreId;
	}
	
	public String getDescription() {
		return m_description;
	}

	public RouteMode getMode() {
		return m_mode;
	}

	public Reference getStartPoint() {
		return m_from;
	}

	public Reference getEndPoint() {
		return m_to;
	}

	public String getFunctionName() {
		return m_functionName;
	}
}
