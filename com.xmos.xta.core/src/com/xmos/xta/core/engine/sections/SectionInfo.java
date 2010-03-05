package com.xmos.xta.core.engine.sections;

import com.xmos.xta.core.engine.utils.NodeCorePair;

public class SectionInfo {
	private Section m_section;
	
	SectionInfo(Section section) {
		m_section = section;
	}

	public NodeCorePair getNodeCorePair() {
		return new NodeCorePair(m_section.getNodeNumber(), m_section.getCoreNumber());
	}
}
