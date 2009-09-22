package com.xmos.xta.core.console.completors;

import java.util.List;
import java.util.Set;
import com.xmos.xta.core.engine.Engine;

import jline.Completor;

public class FunctionCompletor implements Completor {
	private Engine m_engine;
	
	FunctionCompletor(Engine engine) {	
		m_engine = engine;
	}
	
	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, List candidates) {			
        if (m_engine.getExecutable() != null) {
            String start = (buffer == null) ? "" : buffer;
            
            Set<String> functions = m_engine.getFunctionNames();
            for (String function : functions) {              	
            	if (function.startsWith(start)) {
        	        candidates.add(function);
            	}            	
            }        	
        }
		return 0;
	}
}
