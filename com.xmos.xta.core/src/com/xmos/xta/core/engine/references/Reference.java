package com.xmos.xta.core.engine.references;

import java.util.ArrayList;
import java.util.List;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.types.ReferenceType;

public class Reference {		
	private ReferenceType m_base;
	private List<ReferenceType> m_backTrail;
	
	Reference(ReferenceType base) {
		m_base = base;
		m_backTrail = new ArrayList<ReferenceType>();
	}

	public String getRef() {
    	String str = "";
    	for (ReferenceType backTrail : m_backTrail) {
    		str += backTrail.getRef() + ",";
    	}
    	str += m_base.getRef();
    	return str;
	}

	public long getPc() {
		assert(m_base != null);
		return m_base.getPc();
	}
	
	public boolean getResolved() {
		assert(m_base != null);
		return m_base.getResolved();		
	}
	
	public long getLineNum() {
		assert(m_base != null);
		return m_base.getLineNum();		
	}
	public String getFileName() {
		assert(m_base != null);
		return m_base.getFileName();		
	}
	
	public List<ReferenceType> getBackTrail() {
		return m_backTrail;
	}
	
	public boolean resolve(Engine engine, int nodeId, int coreId) {
		assert(m_base != null);

		if (!m_base.resolve(engine, nodeId, coreId))
			return false;
			
		for (ReferenceType referenceType : m_backTrail) {
			if (!referenceType.resolve(engine, nodeId, coreId))
				return false;
		}
		return true;
	}
	
	public void addToBackTrail(ReferenceType referenceType) {
		m_backTrail.add(referenceType);
	}

    public boolean equals(Object obj) {
    	if(obj == null)
    	return false;
               
        if(!(obj instanceof Reference))
            return false;

    	// A reference is equal to another if its base pc, and
    	// all of its back trail pc's, match.
    	Reference reference = (Reference)obj;
               
        if (getPc() != reference.getPc())
    	    return false;

        if (m_backTrail.size() != reference.getBackTrail().size()) 
    	    return false;

        for (int i = 0; i < m_backTrail.size(); ++i) {
    	    if (m_backTrail.get(i).getPc() != getBackTrail().get(i).getPc())
    	        return false;
        }
    	return true;
    }	
    
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + m_base.hashCode();
        hash = hash * 31 + (m_backTrail == null ? 0 : m_backTrail.hashCode());
        return hash;
    }
    
    public String toString() {
    	return getRef();
    }
}
