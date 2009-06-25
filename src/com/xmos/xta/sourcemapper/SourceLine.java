package com.xmos.xta.sourcemapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SourceLine implements Line {
	
	private String             m_line;
	private boolean            m_showChildren;
	private boolean            m_foldable;
    private ArrayList<Line>    m_assembly;
	private int                m_lineNumber;
	private ArrayList<Integer> m_statementPcs;
	
	public SourceLine(String line, int lineNumber) {
	    assert(line!=null);
	    assert(lineNumber>0);
		m_line = line;
		m_lineNumber = lineNumber;
		m_showChildren = false;
		m_foldable = false;
		m_assembly = new ArrayList<Line>();
		m_statementPcs = new ArrayList<Integer>();
	}

	public void setAssembly(List<Line> assembly) {
	    m_assembly.clear();
	    m_statementPcs.clear();
	    addAssembly(assembly);
	    if (m_assembly.size()==0){
            m_foldable = false;
        } else {
            m_foldable = true;
        }
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
		m_foldable = true;
	}
	
	public void addStatementPcs(Collection<Long> pcs) {
		for (long pc : pcs) {
			m_statementPcs.add((int)pc);
		}
	}

	public Collection<Integer> getStatementPcs() {
		return m_statementPcs;
	}

	public boolean contains(int pc) {
		if (m_statementPcs.contains(pc))
			return true;
		else
			return false;
	}

	public int getLineNumber() {
		return m_lineNumber;
	}

	public int numAssemblyLines() {
		return m_assembly.size();
	}

    public String getText() {
        return m_line;
    }

    public boolean getShowChildern() {
        return m_showChildren;
    }
    
    public void setShowChildern(boolean show) {
        m_showChildren = show;
    }

    public ArrayList<Line> getChildern() {
        return m_assembly;
    }
    
    public boolean isFoldable() {
        return m_foldable;
    }
    
    public DisplayType getLineNumberType() {
        
        return DisplayType.decimal;
    }

    public boolean isPausable() {
        return false;
    }
    
    public int getFnopCount() {
        //could be better implemented but will work for now
        return 0;
    }

    public void setFnopCount(int i) {
        //do nothing
    }
    
    public int getCycles() {
        return 0;
    }

    public double getTime() {
        return 0;
    }

    public boolean isExcluded() {
        for(Line childLine : m_assembly){
            if(childLine.isExcluded()) return true;
        }
        return false;
    }
}


