package com.xmos.xta.ui.editors;

public class InputLine {
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