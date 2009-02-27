package com.xmos.xta.console.completors;

import java.util.List;

import com.xmos.xeobject.Function;
import com.xmos.xta.engine.Engine;

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
            
            List<Function> functions = m_engine.getXeObject().getFunctions();
            for (Function function : functions) {              	
            	if (function.getText().startsWith(start)) {
        	        candidates.add(function.getText());
            	}            	
            }        	
        }
		return 0;
	}
}
