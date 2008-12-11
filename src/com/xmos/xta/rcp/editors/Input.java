package com.xmos.xta.rcp.editors;

import java.util.ArrayList;

public class Input {	
	private ArrayList<InputSection> m_sections;
	private String m_headerType;
	private String m_lineType;
	
	public Input(String headerType, String lineType) {
    	m_sections = new ArrayList<InputSection>();
    	m_headerType = headerType;
    	m_lineType = lineType;
    }
    
	public void addHeader(String header, String headerCodeReference, int index) {
        m_sections.add(new InputSection(header, headerCodeReference, index));    	
    }
    
	public void addLine(String line, String codeReference, int pc, int index) {
    	assert(m_sections.size() > 0);
    	m_sections.get(m_sections.size() - 1).addLine(line, codeReference, pc, index);
    }
    
	public ArrayList<InputSection> getSections() {
    	return m_sections;
    }
    
	public String getHeaderType() {
        return m_headerType; 	
    }

	public String getLineType() {
        return m_lineType; 	
    }
}

class InputSection {
	private String m_header;
	private String m_headerCodeReference;
	private ArrayList<InputLine> m_lines;
	private ArrayList<InputLine> m_fnops;
	private int m_index;
	
	public InputSection(String header, String headerCodeReference, int index) {
		m_header = header;
		m_headerCodeReference = headerCodeReference;
		m_lines = new ArrayList<InputLine>();
		m_fnops = new ArrayList<InputLine>();
		m_index = index;
	}
	
	public void addLine(String line, String codeReference, int pc, int index) {
		m_lines.add(new InputLine(this, line, codeReference, pc, index - m_index - 1));
	}

	public void addFnop(String line, int pc, int index) {
		int offset = index - m_index - 1;
		
		InputLine fnop = new InputLine(this, line, "", pc, offset);
		m_lines.add(offset, fnop);
		m_fnops.add(fnop);

		for (int lineNum = offset + 1; lineNum < m_lines.size(); lineNum++) {
			m_lines.get(lineNum).incrementOffset();
		}
	}

	public InputLine removeFnop() {
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
	
	public boolean isFnop(int lineNum) {
		assert(lineNum < m_lines.size());
		if (m_fnops.contains(m_lines.get(lineNum))) {
			return true;
		}
		return false;
	}

	public String getHeader() {
		return m_header;
	}

	public String getHeaderCodeReference() {
		return m_headerCodeReference;
	}

	public int getNumLines() {
		return m_lines.size();
	}

	public int getNumFnops() {
		return m_fnops.size();
	}

	public ArrayList<InputLine> getLines() {
		return m_lines;
	}

	public InputLine getLine(int lineNum) {
		assert(lineNum < m_lines.size());
		return m_lines.get(lineNum);
	}

	public int getIndex() {
		return m_index;
	}

	public void setIndex(int index) {
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
	private String m_codeReference;
	private String m_line;
	private int m_pc;
	private int m_offset;
    
	public InputLine(InputSection section, String line, String codeReference, int pc, int offset) {
		m_section = section;
		m_line = line;
		m_codeReference = codeReference;
		m_pc = pc;
		m_offset = offset;
	}
	
	public String getLine() {
		return m_line;
	}
	
	public String getCodeReference() {
		return m_codeReference;
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