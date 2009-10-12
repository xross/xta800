package com.xmos.xta.ui.editors.disassembly;

import org.eclipse.jface.text.source.LineNumberRulerColumn;

import com.xmos.xta.ui.editors.Input;

public class XtaLineNumberRulerColumn extends LineNumberRulerColumn {
	private Input m_input;
	
	XtaLineNumberRulerColumn(Input input) {
		m_input = input;
	}
	
	protected String createDisplayString(int line) {
		if (m_input.getLineNumberMapping().containsKey((long)line)) {
			long lineNumber = m_input.getLineNumberMapping().get((long)line);
			return Long.toString(lineNumber);
		}
		return "";
    }
}
