package com.xmos.xta.core.engine.sourcemapper;

import java.util.List;

public class Function implements Line {

	private String m_functionName;
	private List<AssemblyLine> m_assembly;

	public Function(String name, List<AssemblyLine> functionAssemby) {
		m_assembly = functionAssemby;
		m_functionName = name;
	}

	//GUI only
	public List<AssemblyLine> getChildren() {
		return m_assembly;
	}

	public String getText() {
		return m_functionName;
	}

	public long getLineNumber() {
		return 0;
	}

	public boolean containsPc(long pc) {
		for (Line line : m_assembly) {
			if (line.getLineNumber() == pc)
				return true;
		}
		return false;
	}

}
