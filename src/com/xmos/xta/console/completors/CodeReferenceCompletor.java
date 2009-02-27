package com.xmos.xta.console.completors;

import java.util.Collection;
import java.util.List;

import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.maps.SrcLabelInfo;

import jline.Completor;

public class CodeReferenceCompletor implements Completor {
	private Engine m_engine;
	private Command.ArgType m_type;
	
	CodeReferenceCompletor(Engine engine, Command.ArgType type) {	
		m_engine = engine;
		m_type = type;
	}
	
	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, List candidates) {		
        if (m_engine.getExecutable() != null) {
            String start = (buffer == null) ? "" : buffer;
                        
            Collection<SrcLabelInfo> srcLabelInfos = null;
            switch (m_type) {
            case ENDPOINT: 
            	srcLabelInfos = m_engine.getEndpoints().values();
            	break;
            case SRCLABEL: 
            	srcLabelInfos = m_engine.getSrcLabels().values();
            	break;
            default:
            	assert(false);
                break;
            }
            
            for (SrcLabelInfo info : srcLabelInfos) {
            	if (info.getLabel().startsWith(start)) {
        	        candidates.add(info.getLabel());
            	}            	            	
            }

            for (String label : m_engine.getLabels().keySet()) {
            	if (label.startsWith(start)) {
        	        candidates.add(label);
            	}            	            	
            }            
        }
		return 0;
	}
}
