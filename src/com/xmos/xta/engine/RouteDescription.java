package com.xmos.xta.engine;

import com.xmos.xta.engine.Route.RouteMode;

public class RouteDescription {
	private String m_description;
	private RouteMode m_mode;
	
	public RouteDescription(String from, String to){
		m_description = String.format("path: %s to %s", from, to);
		m_mode = RouteMode.ABSLOUTE;
	}
	
	public RouteDescription(String functionName){
		m_description = String.format("function: %s", functionName);
		m_mode = RouteMode.FUNCTION;
	}
	
	public String getDescription(){
		return m_description;
	}
	
	public RouteMode getMode(){
		return m_mode;
	}
}
