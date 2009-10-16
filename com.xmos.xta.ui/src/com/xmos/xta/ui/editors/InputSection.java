package com.xmos.xta.ui.editors;

import java.util.ArrayList;

public class InputSection {
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
