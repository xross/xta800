package com.xmos.xtagui.editors;

import java.util.ArrayList;

public class Input {	
	private ArrayList<InputSection> m_sections;
	
    Input() {
    	m_sections = new ArrayList<InputSection>();
    }
    
    void addHeader(String header) {
        m_sections.add(new InputSection(header));    	
    }
    
    void addLine(String line) {
    	assert(m_sections.size() > 0);
    	m_sections.get(m_sections.size() - 1).addLine(line);
    }
    
    ArrayList<InputSection> getSections() {
    	return m_sections;
    }
}

class InputSection{
	private String m_header;
	private ArrayList<String> m_lines;
	
	InputSection(String header) {
		m_header = header;
		m_lines = new ArrayList<String>();
	}
	
	void addLine(String line) {
		m_lines.add(line);
	}
	
	String getHeader() {
		return m_header;
	}

	ArrayList<String> getLines() {
		return m_lines;
	}
}
