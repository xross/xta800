package com.xmos.xta.rcp.editors;

import java.util.ArrayList;

public class Input {	
	private ArrayList<InputSection> m_sections;
	private String m_headerType;
	private String m_lineType;
	
    Input(String headerType, String lineType) {
    	m_sections = new ArrayList<InputSection>();
    	m_headerType = headerType;
    	m_lineType = lineType;
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
    
    String getHeaderType() {
        return m_headerType; 	
    }

    String getLineType() {
        return m_lineType; 	
    }
}

class InputSection {
	private String m_header;
	private ArrayList<InputLine> m_lines;
	private ArrayList<InputLine> m_fnops;
	private int m_index;
	
	InputSection(String header, int index) {
		m_header = header;
		m_lines = new ArrayList<InputLine>();
		m_fnops = new ArrayList<InputLine>();
		m_index = index;
	}
	
	void addLine(String line, int pc, int index) {
		m_lines.add(new InputLine(this, line, pc, index - m_index - 1));
	}

	void addFnop(String line, int pc, int index) {
		int offset = index - m_index - 1;
		
		InputLine fnop = new InputLine(this, line, pc, offset);
		m_lines.add(offset, fnop);
		m_fnops.add(fnop);

		for (int lineNum = offset + 1; lineNum < m_lines.size(); lineNum++) {
			m_lines.get(lineNum).incrementOffset();
		}
	}

	InputLine removeFnop() {
		if (m_fnops.isEmpty())
			return null;
		
		InputLine firstFnop = m_fnops.remove(0);
		int offset = firstFnop.getOffset();
		m_lines.remove(firstFnop);

		for (int lineNum = offset; lineNum < m_lines.size(); lineNum++) {
			m_lines.get(lineNum).decrementOffset();
		}
		return firstFnop;
	}

	String getHeader() {
		return m_header;
	}

	public int getNumLines() {
		return m_lines.size();
	}

	public int getNumFnops() {
		return m_fnops.size();
	}

	ArrayList<InputLine> getLines() {
		return m_lines;
	}

	InputLine getLine(int lineNum) {
		assert(lineNum < m_lines.size());
		return m_lines.get(lineNum);
	}

	int getIndex() {
		return m_index;
	}

	void setIndex(int index) {
		m_index = index;
	}

	public void incrementIndex() {
		m_index++;
	}

	public void decrementIndex() {
		m_index--;
	}
}

class InputLine {
	private InputSection m_section;
	private String m_line;
	private int m_pc;
	private int m_offset;
    
	InputLine(InputSection section, String line, int pc, int offset) {
		m_section = section;
		m_line = line;
		m_pc = pc;
		m_offset = offset;
	}
	
	public String getLine() {
		return m_line;
	}
	
	public int getPc() {
		return m_pc;
	}
	
	public int getIndex() {
		return m_offset + m_section.getIndex() + 1;
	}

	public int getOffset() {
		return m_offset;
	}

	public void incrementOffset() {
		m_offset++;
	}

	public void decrementOffset() {
		m_offset--;
	}
}