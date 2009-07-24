package com.xmos.xta.engine.references;

import java.util.ArrayList;
import java.util.List;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.references.types.ReferenceType;

public class Reference {		
	private ReferenceType m_base;
	private List<ReferenceType> m_backTrail;
	
	Reference(ReferenceType base) {
		m_base = base;
		m_backTrail = new ArrayList<ReferenceType>();
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
		
	public String getRef() {
		assert(m_base != null);
		return m_base.getRef();
	}

	public int getPc() {
		assert(m_base != null);
		return m_base.getPc();
	}
	
	public boolean getResolved() {
		assert(m_base != null);
		return m_base.getResolved();		
	}

	public String getFileName() {
		assert(m_base != null);
		return m_base.getFileName();		
	}

	public int getLineNum() {
		assert(m_base != null);
		return m_base.getLineNum();		
	}
	
	public String description() {
		assert(m_base != null);
		return m_base.description();
	}

	public String name() {
		assert(m_base != null);
		return m_base.name();
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
	
	public int hashCode() {
		//this is needed to complete the equals method
		return 0;
	}
}
