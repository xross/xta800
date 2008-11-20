package com.xmos.xtagui.editors;

import java.util.ArrayList;

public class Input {	
	private ArrayList<InputSection> m_sections;
	
    Input() {
    	m_sections = new ArrayList<InputSection>();
    }
    
    void addHeader(String header, int index) {
        m_sections.add(new InputSection(header, index));    	
    }
    
    void addLine(String line, int pc, int index) {
    	assert(m_sections.size() > 0);
    	m_sections.get(m_sections.size() - 1).addLine(line, pc, index);
    }
    
    ArrayList<InputSection> getSections() {
    	return m_sections;
    }
}

class InputSection{
	private String m_header;
	private ArrayList<InputLine> m_lines;
	private int m_index;
	
	InputSection(String header, int index) {
		m_header = header;
		m_lines = new ArrayList<InputLine>();
		m_index = index;
	}
	
	void addLine(String line, int pc, int index) {
		m_lines.add(new InputLine(line, pc, index));
	}
	
	String getHeader() {
		return m_header;
	}

	ArrayList<InputLine> getLines() {
		return m_lines;
	}
	
	int getIndex() {
		return m_index;
	}
}

class InputLine{
	private String m_line;
	private int m_pc;
	private int m_index;
    
	InputLine(String line, int pc, int index) {
		m_line = line;
		m_pc = pc;
		m_index = index;
	}
	
	String getLine() {
		return m_line;
	}
	
	int getPc() {
		return m_pc;
	}
	
	int getIndex() {
		return m_index;
	}
}