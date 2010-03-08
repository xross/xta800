package com.xmos.xta.core.engine.sections;

import com.xmos.xta.core.engine.references.Reference;

public class BranchInfo extends SectionInfo implements Comparable<BranchInfo> {
	private Reference m_ref;
	private boolean m_isUser;

	BranchInfo(Section section, Reference ref, boolean isUser) {
		super(section);
		
		m_ref = ref;
		m_isUser = isUser;
	}

	public Reference getRef() {
		return m_ref;
	}

	public boolean isUser() {
		return m_isUser;
	}

	public int compareTo(BranchInfo o) {
		return m_ref.compareTo(o.m_ref);
	}
}
