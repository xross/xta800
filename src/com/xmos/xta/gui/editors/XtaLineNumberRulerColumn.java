package com.xmos.xta.gui.editors;

import org.eclipse.jface.text.source.LineNumberRulerColumn;

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
