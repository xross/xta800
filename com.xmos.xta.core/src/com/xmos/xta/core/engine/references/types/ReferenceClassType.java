package com.xmos.xta.core.engine.references.types;

public class ReferenceClassType {
	private boolean m_isVisible;
	private ReferenceType[] m_referenceTypes;

	public ReferenceClassType(boolean isVisible, ReferenceType[] referenceTypes) {
		m_isVisible = isVisible;
		m_referenceTypes = referenceTypes;
	}

	public boolean isVisible() {
		return m_isVisible;
	}

	public ReferenceType[] getReferencTypes() {
    	return m_referenceTypes;
    }
}
