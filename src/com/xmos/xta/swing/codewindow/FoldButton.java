package com.xmos.xta.swing.codewindow;

import java.awt.Rectangle;

import com.xmos.xeobject.Line;

public class FoldButton {
	
	private Rectangle m_area;
	private Line      m_line;
	
	public FoldButton(Rectangle area, Line line) {
		m_area = area;
		m_line = line;
	}

	public Line getLine() {
		return m_line;
	}
	
	public boolean containsPoint(int x, int y) {
		return m_area.contains(x, y);
	}
}
