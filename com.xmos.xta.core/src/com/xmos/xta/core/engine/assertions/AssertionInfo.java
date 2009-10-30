package com.xmos.xta.core.engine.assertions;

public class AssertionInfo {
    public String m_description;
    public String m_value;
    
    AssertionInfo(String description, String value) {
    	m_description = description;
    	m_value = value;
    }
}
