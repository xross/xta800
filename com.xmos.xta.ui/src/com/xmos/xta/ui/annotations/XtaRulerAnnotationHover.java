package com.xmos.xta.ui.annotations;

import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;

public class XtaRulerAnnotationHover implements IAnnotationHover {

	private XtaAnnotations m_annotations;
	
	public XtaRulerAnnotationHover(XtaAnnotations annotations) {
		m_annotations = annotations;
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

	private String getAnnotationString(long lineNumber) {
		boolean isStartPoint = isStartPoint(lineNumber);
		boolean isEndPoint = isEndPoint(lineNumber);
		if (isStartPoint && isEndPoint) {
			return "Both start and end points set.";
		} else if (isStartPoint) {
			return "Start point set.";
		} else if (isEndPoint) {
			return "End point set.";
		}
		
		if (m_annotations.isValidEndPoint(lineNumber)) {
			return "A valid start/end point for timing.";
		}
		
		return null;
	}

	private String getWarningString(long lineNumber) {
		String warning = "";
		for (Unknown unknown : m_annotations.getUnknownsForLine(lineNumber)) {
			warning += unknown.getDescription() + "\n";
		}
		return warning;
	}

	private boolean isStartPoint(long lineNum) {
		if (m_annotations.getStartPointIndexes().contains(lineNum))
			return true;

		return false;
	}

	private boolean isEndPoint(long lineNum) {
		if (m_annotations.getEndPointIndexes().contains(lineNum))
			return true;

		return false;
	}
}
