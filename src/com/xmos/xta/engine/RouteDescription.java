package com.xmos.xta.engine;

import com.xmos.xta.engine.Route.RouteMode;
import com.xmos.xta.engine.references.Reference;

class RouteDescription {
	private String m_description;
	private RouteMode m_mode;
	private Reference m_from;
	private Reference m_to;
	
	public RouteDescription(Reference from, Reference to){
		m_description = String.format("path: %s to %s", from.getRef(), to.getRef());
		m_mode = RouteMode.ABSLOUTE;
		m_from = from;
		m_to = to;
	}
	
	public RouteDescription(String functionName){
		m_description = String.format("function: %s", functionName);
		m_mode = RouteMode.FUNCTION;
		m_from = null;
		m_to = null;
	}
	
	public String getDescription(){
		return m_description;
	}
	
	public RouteMode getMode(){
		return m_mode;
	}

	public Reference getStartPoint() {
		return m_from;
	}

	public Reference getEndPoint() {
		return m_to;
	}
}
