package com.xmos.xta.engine;

import com.xmos.xta.engine.Route.RouteMode;

public class RouteDescription {
	private String m_description;
	private RouteMode m_mode;
	private String m_function;
		
	public RouteDescription(String function, String description, RouteMode mode){
		m_description = description;
		m_mode = mode;
		m_function = function;
	}
		
	public String getDescription(){
		return m_description;
	}
		
	public RouteMode getMode(){
		return m_mode;
	}
		
	public String getFunction(){
		return m_function;
	}
}
