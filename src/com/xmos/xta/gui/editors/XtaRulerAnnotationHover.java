package com.xmos.xta.gui.editors;

import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class XtaRulerAnnotationHover implements IAnnotationHover {

	private XtaEditor m_editor;
	
	public XtaRulerAnnotationHover(XtaEditor editor) {
		m_editor = editor;
	}
	
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		String warning = getWarningString(lineNumber);
		if (warning != null && !warning.equals("")) {
			return warning;
		}
		
		String annotation = getAnnotationString(lineNumber);
		if (annotation != null && !annotation.equals(""))
			return annotation;
			
		return null;
	}

	private String getAnnotationString(int lineNumber) {
		boolean isStartPoint = isStartPoint(lineNumber);
		boolean isEndPoint = isEndPoint(lineNumber);
		if (isStartPoint && isEndPoint) {
			return "Both start and end points set.";
		} else if (isStartPoint) {
			return "Start point set.";
		} else if (isEndPoint) {
			return "End point set.";
		}
		
		if (m_editor.isValidEndPoint(lineNumber)) {
			return "A valid start/end point for timing.";
		}
		
		return null;
	}

	private String getWarningString(int lineNumber) {
		String warning = "";
		for (Node nodes : m_editor.getUnknownsForLine(lineNumber)) {
			warning += nodes.getAttributes().getUnknownDescription() + "\n";
		}
		return warning;
	}

	private boolean isStartPoint(int lineNum) {
		if (lineNum == m_editor.getStartPointIndex())
			return true;

		return false;
	}

	private boolean isEndPoint(int lineNum) {
		if (lineNum == m_editor.getEndPointIndex())
			return true;

		return false;
	}
}
