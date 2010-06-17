package com.xmos.xta.core.engine.references;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.xmos.libs.util.Conversion;
import com.xmos.libs.util.IteratableSet;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.utils.CoreId;

public class Reference implements Comparable<Reference>  {
	private String m_coreIdString;
	private ReferenceType m_base;
	private List<ReferenceType> m_backTrail;

	Reference(String coreIdString, ReferenceType base) {
		m_coreIdString = coreIdString;
		m_base = base;
		m_backTrail = new ArrayList<ReferenceType>();
	}

	public String getRef() {
    	StringBuffer str = new StringBuffer();
    	
    	if (m_coreIdString != null) {    		
    	    str.append(m_coreIdString);
    	    str.append(ReferenceFactory.CORE_ID_SEPARATOR);
    	}
    	
    	for (ReferenceType backTrail : m_backTrail) {
    		str.append(backTrail.getRef());
    		str.append(",");
    	}
    	str.append(m_base.getRef());
    	return str.toString();
	}

	public String getPcString() {
		assert(m_base != null);
		return Conversion.pcSetToString(m_base.getPcs());
	}

	public Set<Long> getPcs() {
		assert(m_base != null);
		return m_base.getPcs();
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

	public ReferenceType getBase() {
		return m_base;
	}

	public List<ReferenceType> getBackTrail() {
		return m_backTrail;
	}

	public boolean resolve(Engine engine) {
		assert(m_base != null);

		if (!m_base.resolve(engine))
			return false;

		// Resolve backtrail, but don't check it at the moment. The label may contain
		// a backtrail which ignores the reference.
		for (ReferenceType referenceType : m_backTrail) {
			referenceType.resolve(engine);
		}
		return true;
	}

	public boolean isPortable() {
		assert(m_base != null);

		if (!m_base.isPortable())
			return false;

		for (ReferenceType referenceType : m_backTrail) {
			if (!referenceType.isPortable())
				return false;
		}
		return true;
	}

	public void addToBackTrail(ReferenceType referenceType) {
		m_backTrail.add(referenceType);
	}

    public boolean equals(Object obj) {
    	if (obj == null)
    	    return false;

        if (!(obj instanceof Reference))
            return false;

    	// A reference is equal to another if its base pc, and
    	// all of its back trail pc's, match.
    	Reference reference = (Reference)obj;

		assert(m_base != null);
		if (reference.checkPcsMatch()) {
			if (!match(reference.getPcs(), m_base.getPcs())) {
				return false;
			}
		}

        if (reference.checkBackTrailMatches()) {
	        if (m_backTrail.size() != reference.getBackTrail().size()) {
	    	    return false;
	        }

	        for (int i = 0; i < m_backTrail.size(); ++i) {
	    	    if (!match(m_backTrail.get(i).getPcs(), getBackTrail().get(i).getPcs())) {
	    	        return false;
	    	    }
	        }
        }

        if ((m_coreIdString != null) && (reference.m_coreIdString != null)) {
        	if (!m_coreIdString.equals(reference.m_coreIdString)) {
        		return false;
        	}
        }
    	return true;
    }

	public int hashCode() {
        int hash = 1;
        hash = hash * 31 + m_base.hashCode();
        if (m_backTrail == null) {
        	hash = hash * 31;
        } else {
        	for (ReferenceType entry : m_backTrail) {
            	hash = hash * 31 + entry.hashCode();
        	}
        }
        return hash;
    }

    public String toString() {
    	if (m_coreIdString != null) {    		
        	return getRef();    		
    	}
        return getCoreId().toString() + "::" + getRef();    		
    }

	public boolean matches(long pc) {
		return m_base.getPcs().contains(pc);
	}

	public boolean checkPcsMatch() {
		return m_base.checkPcsMatch();
	}

	public boolean checkBackTrailMatches() {
		return m_base.checkBackTrailMatches();
	}

	public int compareTo(Reference o) {
		return toString().compareTo(o.toString());
	}

	public List<String> getPcBackTrail(long pc) {
		return m_base.getPcBackTrail(pc);
	}
	
	public CoreId getCoreId() {
		return m_base.getCoreId();
	}

    private boolean match(Set<Long> set1, Set<Long> set2) {
    	Set<Long> intersection = new IteratableSet<Long>(set1);
    	intersection.retainAll(set2);
    	if (intersection.isEmpty()) {
    		return false;
    	}
    	return true;
	}
}
