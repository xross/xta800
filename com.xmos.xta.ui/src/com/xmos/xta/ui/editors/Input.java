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


