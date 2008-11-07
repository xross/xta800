package com.xmos.xtagui;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;

public class XtaEngine {
	private static Config m_config;
	private static Engine m_engine;
	
	public static void initialise() {
        m_config = new Config();
        m_engine = new Engine(m_config);		
	}
	
	public static Engine getEngine() {
		assert(m_engine != null);
		return m_engine;		
	}
}
