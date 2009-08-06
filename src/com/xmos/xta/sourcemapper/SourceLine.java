package com.xmos.xta.sourcemapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SourceLine implements Line {
	
	private String         m_line;
    private ArrayList<AssemblyLine> m_assembly;
	private int             m_lineNumber;
	private ArrayList<Long> m_statementPcs;
	
	public SourceLine(String line, int lineNumber) {
	    assert(line!=null);
	    assert(lineNumber>0);
		m_line = line;
		m_lineNumber = lineNumber;
		m_assembly = new ArrayList<AssemblyLine>();
		m_statementPcs = new ArrayList<Long>();
	}
	
	public void addAssembly(List<Line> assembly){
	    assert(assembly!=null);
	    for(Line al : assembly){
	        addAssembly((AssemblyLine)al);
	    }
	}
	
	public void addAssembly(AssemblyLine newInst){
	    assert(newInst!=null);
		m_assembly.add(newInst);
	}
	
	public void addStatementPcs(Collection<Long> pcs) {
		for (long pc : pcs) {
			m_statementPcs.add(pc);
		}
	}

	public Collection<Long> getStatementPcs() {
		return m_statementPcs;
	}

	public long getLineNumber() {
		return m_lineNumber;
	}

    public String getText() {
        return m_line;
    }

    public ArrayList<AssemblyLine> getChildern() {
        return m_assembly;
    }
}


