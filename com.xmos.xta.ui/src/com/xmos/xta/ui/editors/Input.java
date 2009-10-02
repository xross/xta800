package com.xmos.xta.ui.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Input {	
	private ArrayList<InputSection> m_sections;
	private String m_headerType;
	private String m_lineType;
	private Map<Long, Long> m_lineNumberMapping;
	private HashMap<Long, InputLine> m_pcToLine;
	private HashMap<Long, InputSection> m_pcToSection;
	
	public Input(String headerType, String lineType) {
    	m_sections = new ArrayList<InputSection>();
    	m_headerType = headerType;
    	m_lineType = lineType;
    	m_lineNumberMapping = new HashMap<Long, Long>();
    	m_pcToLine = new HashMap<Long, InputLine>();
    	m_pcToSection = new HashMap<Long, InputSection>();
    }
	
	public void addPcToLine(long pc, InputLine line){
		m_pcToLine.put(pc, line);
	}
	
	public HashMap<Long, InputLine> getPcsToLines(){
		return m_pcToLine;
    }
    
	public void addPcToSection(long pc, InputSection sec){
		m_pcToSection.put(pc, sec);
	}
	
	public HashMap<Long, InputSection> getPcsToSection(){
		return m_pcToSection;
    }
	
	public void addHeader(String header, String headerCodeReference, long index, long lineNum) {
        m_sections.add(new InputSection(header, headerCodeReference, index, lineNum, m_headerType));    	
    }
    
	public void addLine(String line, String codeReference, long pc, long index) {
    	getLastSection().addLine(line, codeReference, pc, index, this);
    }
    
	public InputSection getLastSection() {
		assert(m_sections.size() > 0);
    	return m_sections.get(m_sections.size() - 1);
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
	
	public void addLineNumberMapping(long from, long to) {
		m_lineNumberMapping.put(from, to);
	}
		
	public Map<Long, Long> getLineNumberMapping() {
		return m_lineNumberMapping;
	}
}

class InputSection {
	private InputLine m_header;
	private ArrayList<InputLine> m_lines;
	private ArrayList<InputLine> m_fnops;
	private long m_index;
	private String m_headerType;
	
	public InputSection(String header, String headerCodeReference, long index, long lineNum, String headerType) {
		// Header line must have -1 index to counteract +1 in index calculations
		m_headerType = headerType;
		m_header = new InputLine(this, header, headerCodeReference, lineNum, -1, m_headerType);
		m_lines = new ArrayList<InputLine>();
		m_fnops = new ArrayList<InputLine>();
		m_index = index;
	}
	
	public void addLine(String line, String codeReference, long pc, long index, Input input) {
		InputLine newLine = new InputLine(this, line, codeReference, pc, index - m_index - 1, input.getLineType());
		m_lines.add(newLine);
		input.addPcToLine(pc, newLine);
	}
	
	public void addFnop(String line, long pc, long index) {
		long offset = index - m_index - 1;
		
		InputLine fnop = new InputLine(this, line, "", pc, offset, m_headerType);
		m_lines.add((int)offset, fnop);
		m_fnops.add(fnop);
		
		for (long lineNum = offset + 1; lineNum < m_lines.size(); lineNum++) {
			m_lines.get((int)lineNum).incrementOffset();
		}
	}
	
	public InputLine removeFnop(long pc) {
		if (m_fnops.isEmpty())
			return null;
		
		int index = -1;
		for(InputLine fnop : m_fnops){
			if(fnop.getPc() == pc){
				index = m_fnops.indexOf(fnop);
				break;
			}
		}
		if(index == -1) return null;
		
		InputLine firstFnop = m_fnops.remove(index);
		long offset = firstFnop.getOffset();
		m_lines.remove(firstFnop);

		for (long lineNum = offset; lineNum < m_lines.size(); lineNum++) {
			m_lines.get((int)lineNum).decrementOffset();
		}
		return firstFnop;
	}
	
	public boolean isFnop(long lineNum) {
		assert(lineNum < m_lines.size());
		if (m_fnops.contains(m_lines.get((int)lineNum))) {
			return true;
		}
		return false;
	}

	public InputLine getHeader() {
		return m_header;
	}

	public int getNumLines() {
		return m_lines.size();
	}

	public int getNumFnops() {
		return m_fnops.size();
	}

	public ArrayList<InputLine> getFnops(){
		return m_fnops;
	}
	
	public ArrayList<InputLine> getLines() {
		return m_lines;
	}

	public InputLine getLine(long lineNum) {
		assert(lineNum < m_lines.size());
		return m_lines.get((int)lineNum);
	}

	public long getIndex() {
		return m_index;
	}

	public void setIndex(long index) {
		m_index = index;
	}

	public void incrementIndex() {
		m_index++;
	}

	public void decrementIndex() {
		m_index--;
	}
		
	public String getHeaderType() {
		return m_headerType;
	}
}

class InputLine {
	private InputSection m_section;
	private String m_codeReference;
	private String m_line;
	private long m_pc;
	private long m_offset;
	private String m_headerType;
	
	public InputLine(InputSection section, String line, String codeReference, long pc, long offset, String headerType) {
		m_section = section;
		m_line = line;
		m_codeReference = codeReference;
		m_pc = pc;
		m_offset = offset;
		m_headerType = headerType;
	}
	
	public String getHeaderType() {
		return m_headerType;
	}
	
	public String getLine() {
		return m_line;
	}
	
	public String getCodeReference() {
		return m_codeReference;
	}
	
	public long getPc() {
		return m_pc;
	}
	
	public long getIndex() {
		return m_offset + m_section.getIndex() + 1;
	}

	public long getOffset() {
		return m_offset;
	}

	public void incrementOffset() {
		m_offset++;
	}

	public void decrementOffset() {
		m_offset--;
	}
}