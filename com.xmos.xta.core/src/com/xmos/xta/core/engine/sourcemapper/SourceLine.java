package com.xmos.xta.core.engine.sourcemapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SourceLine implements Line {
	
	private String             m_line;
    private List<AssemblyLine> m_assembly;
	private long               m_lineNumber;
	private List<Long>         m_statementPcs;
	private List<Long>         m_allPcs;
	
	public SourceLine(String line, long lineNumber) {
	    assert(line!=null);
	    assert(lineNumber>0);
		m_line = line;
		m_lineNumber = lineNumber;
		m_assembly = new ArrayList<AssemblyLine>();
		m_statementPcs = new ArrayList<Long>();
		m_allPcs = new ArrayList<Long>();
	}
	
	public void addAssembly(AssemblyLine newInst){
	    assert(newInst!=null);
		m_assembly.add(newInst);
		m_allPcs.add(newInst.getPc());
	}
	
	public void addStatementPcs(Collection<Long> pcs) {
		for (long pc : pcs) {
			m_statementPcs.add(pc);
		}
	}

	public Collection<Long> getStatementPcs() {
		return m_statementPcs;
	}

	public Collection<Long> getAllPcs() {
		return m_allPcs;
	}

	public long getLineNumber() {
		return m_lineNumber;
	}

    public String getText() {
        return m_line;
    }

    public List<AssemblyLine> getChildern() {
        return m_assembly;
    }
}


