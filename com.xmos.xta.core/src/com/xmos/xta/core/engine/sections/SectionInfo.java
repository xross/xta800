package com.xmos.xta.core.engine.sections;

import com.xmos.libs.core.utils.CoreId;

public class SectionInfo {
    private Section m_section;

    SectionInfo(Section section) {
        m_section = section;
    }

    public CoreId getCoreId() {
        return m_section.getCoreId();
    }
}
