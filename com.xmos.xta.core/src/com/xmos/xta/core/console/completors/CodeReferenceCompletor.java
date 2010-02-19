package com.xmos.xta.core.console.completors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jline.Completor;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;

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

            Collection<CodePointInfo> srcLabelInfos = new ArrayList<CodePointInfo>();
            switch (m_type) {
            case ALLREFS:
            	srcLabelInfos.addAll(m_engine.getEndpoints());
            	srcLabelInfos.addAll(m_engine.getSrcLabels());
            	srcLabelInfos.addAll(m_engine.getCalls());
            	break;

            case ENDPOINT:
            	srcLabelInfos.addAll(m_engine.getEndpoints());
            	srcLabelInfos.addAll(m_engine.getCalls());
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

            for (String label : m_engine.getLabels().keySet()) {
            	if (label.startsWith(start)) {
        	        candidates.add(label);
            	}
            }
        }
		return 0;
	}
}
