package com.xmos.xta.core.engine.functionmapper;

import java.util.List;

public class Function {
	private String m_name;
	private List<AssemblyLine> m_assemblyLines;

	public Function(String name, List<AssemblyLine> assemblyLines) {
		m_name = name;
		m_assemblyLines = assemblyLines;
	}

	public String getName() {
		return m_name;
	}

	public List<AssemblyLine> getAssemblyLines() {
		return m_assemblyLines;
	}
	
	public boolean containsPc(long pc) {
		for (AssemblyLine line : m_assemblyLines) {
			if (line.getLineNumber() == pc)
				return true;
		}
		return false;
	}
}
