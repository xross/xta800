package com.xmos.xta.ui.annotations;

import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;

public class XtaRulerAnnotationHover implements IAnnotationHover {

	private XtaAnnotations m_annotations;
	
	public XtaRulerAnnotationHover(XtaAnnotations annotations) {
		m_annotations = annotations;
	}
	
	public String getHoverInfo(ISourceViewer sourceViewer, int index) {
		String warning = getWarningString(index);
		if (warning != null && !warning.equals("")) {
			return warning;
		}
		
		String annotation = getAnnotationString(index);
		if (annotation != null && !annotation.equals(""))
			return annotation;
			
		return null;
	}

	private String getAnnotationString(long index) {
		boolean isStartPoint = isStartPoint(index);
		boolean isEndPoint = isEndPoint(index);
		if (isStartPoint && isEndPoint) {
			return "Both from and to endpoints set.";
		} else if (isStartPoint) {
			return "From endpoint set.";
		} else if (isEndPoint) {
			return "To endpoint set.";
		}
		
		if (m_annotations.isValidEndPoint(index)) {
			return "A valid endpoint for timing.";
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

	private boolean isStartPoint(long index) {
		if (m_annotations.getStartPointIndexes().contains(index))
			return true;

		return false;
	}

	private boolean isEndPoint(long index) {
		if (m_annotations.getEndPointIndexes().contains(index))
			return true;

		return false;
	}
}
