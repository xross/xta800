package com.xmos.xta.rcp;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;

public class XtaEngine {
	private static Engine m_engine;
		
	public static void initialise() {
        m_engine = new Engine(new Config());
        m_engine.registerDisplayer(XtaDisplayer.getInstance());
	}
	
	public static Engine getEngine() {
		assert(m_engine != null);
		return m_engine;		
	}
}
