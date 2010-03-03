package com.xmos.xta.core.console.completors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jline.Completor;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class CodeReferenceCompletor implements Completor {
	private Engine m_engine;
	private Command.ArgType m_type;

	CodeReferenceCompletor(Engine engine, Command.ArgType type) {
		m_engine = engine;
		m_type = type;
	}

	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, final List candidates) {
        if (m_engine.getExecutable() != null) {
            final String start = (buffer == null) ? "" : buffer;

            final Collection<CodePointInfo> srcLabelInfos = new ArrayList<CodePointInfo>();
            switch (m_type) {
            case ALLREFS:
        		for (NodeCorePair activeCore : m_engine.getActiveCores()) {
	            	srcLabelInfos.addAll(m_engine.getEndpoints(activeCore.getNode(), activeCore.getCore()));
	            	srcLabelInfos.addAll(m_engine.getSrcLabels(activeCore.getNode(), activeCore.getCore()));
	            	srcLabelInfos.addAll(m_engine.getCalls(activeCore.getNode(), activeCore.getCore()));
				}            		
            	break;

            case ENDPOINT:
        		for (NodeCorePair activeCore : m_engine.getActiveCores()) {			
	            	srcLabelInfos.addAll(m_engine.getEndpoints(activeCore.getNode(), activeCore.getCore()));
	            	srcLabelInfos.addAll(m_engine.getCalls(activeCore.getNode(), activeCore.getCore()));						
				}
            	break;

            case LABEL:
            	break;

            default:
            	assert(false);
                break;
            }

            for (CodePointInfo info : srcLabelInfos) {
            	if (info.getLabel().startsWith(start)) {
        	        candidates.add(info.getLabel());
            	}
            }

    		for (NodeCorePair activeCore : m_engine.getActiveCores()) {
	            for (String label : m_engine.getLabels(activeCore.getNode(), activeCore.getCore()).keySet()) {
	            	if (label.startsWith(start)) {
	        	        candidates.add(label);
	            	}
	            }					
			}
        }
		return 0;
	}
}
