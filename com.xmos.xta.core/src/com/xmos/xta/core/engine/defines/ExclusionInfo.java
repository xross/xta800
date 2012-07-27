package com.xmos.xta.core.engine.defines;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.references.Reference;

public class ExclusionInfo implements DefineInfo {
	private Reference m_reference;

	public ExclusionInfo(Reference reference) {
		m_reference = reference;
	}
	
	public Reference getReference() {
		return m_reference;
	}
	
	public CoreId getCoreId() {
		return m_reference.getCoreId();
	}

	public String getDescription() {
		return m_reference + " " + m_reference.getPcString();
	}

	public Reference[] getReferences() {
		return new Reference[] {m_reference};
	}

	public String getValue() {
		return "";
	}

	public boolean equals(Object o) {
		boolean ret = false;
		if (o != null) {
			if (o instanceof ExclusionInfo) {
				ret = ((ExclusionInfo)o).m_reference.equals(m_reference) &&
			          (((ExclusionInfo)o).m_reference.getCoreId().equals(m_reference.getCoreId()));
			}
		}
		return ret;		
	}
}
