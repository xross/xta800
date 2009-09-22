package com.xmos.xta.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceLine;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

public class XtaSourceEditor extends XtaEditor {
    public static final String ID = "com.xmos.xta.ui.editors.XtaSourceEditor";

    protected Collection<Long> m_endPointLineIndexes;

    public XtaSourceEditor() {
    	super(XtaDocumentPartitioner.SOURCE);
    	setSourceViewerConfiguration(new XtaSourceConfiguration(this));

        m_endPointLineIndexes = new HashSet<Long>();
    }

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("XtaEditor: propertyChange: %s\n", event.getProperty());	

		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			updateAll();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE)) {
	        updateStartPoint();
	        updateEndPoint();
			updateAll();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_START_POINT) ||
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_START_POINT)) {
			updateStartPoint();
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT) ||
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_END_POINT)) {
			updateEndPoint();
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			updateUnknowns();

		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUSIONS)) {
			updateExcluded();
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
	        updateStartPoint();
	        updateEndPoint();
			updateHighlighting();

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECTION_CHANGED)) {
			updateSelection(GuiEngine.getSelectedNodes());
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SET_FOCUS)) {
			Node focusNode = GuiEngine.getFocus();
			if (focusNode != null) {
				for (XtaInstruction instruction : focusNode.getInstructions()) {
					boolean hit = showCodePoint(instruction.getPc());
					if (hit) {
						return;
					}
				}
			}

		}
	}

    void updateAll() {
		updateExcluded();
		updateUnknowns();
		updateHighlighting();
	}

	void updateAnnotations(List<Annotation> annotations,
			HashMap<Annotation, Position> annotationPositions) {

		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>(annotations);
		final HashMap<Annotation, Position> allAnnotationPositions =
			new HashMap<Annotation, Position>(annotationPositions);
		
        long startPointIndex = getStartPointIndex();
        long endPointIndex = getEndPointIndex();

        m_endPointLineIndexes.clear();
        for (CodePointTableInfo info : GuiEngine.getEngine().getEndpointTable().values()) {
        	if (info.getFileName().equals(m_fileName)) {
        		long index = info.getLineNumber() + 1;
        		m_endPointLineIndexes.add(index);
        		if (index != endPointIndex && index != startPointIndex) {
        			addAnnotation(index, EndpointID, allAnnotations, allAnnotationPositions);
        		}
        	}
        } 

        for (CodePointTableInfo info : GuiEngine.getEngine().getCallTable().values()) {
        	if (info.getFileName().equals(m_fileName)) {
        		long index = info.getLineNumber() + 1;
        		m_endPointLineIndexes.add(index);
        		if (index != endPointIndex && index != startPointIndex) {
        			addAnnotation(index, EndpointID, allAnnotations, allAnnotationPositions);
        		}
        	}
        } 

        super.updateAnnotations(annotations, annotationPositions);
	}

    boolean updateAnnotations(long pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions, boolean excludeFnops, boolean excludeSections) {
    	
    	long index = findIndexForPc(pc);
    	if (index >= 0) { 
    		addAnnotation(index, type, annotations, annotationPositions);
    		return true;
    	} else {
    		return false;
    	}
    }

	boolean showCodePoint(long pc) {
    	long index = findIndexForPc(pc);
    	if (index >= 0) { 
			try {
				IDocument document = getDocument();
				int charOffset = document.getLineOffset((int)index);
				showLocation(charOffset, index, false);
				return true;
			} catch (BadLocationException e) {}
		}
		return false;
	}

	long findIndexForPc(long pc) {
		SourceLine sourceLine = getLineForPc(pc);
		if (sourceLine != null) {
			return sourceLine.getLineNumber() - 1;
		}
		return -1;
	}

	private SourceLine getLineForPc(long pc) {
		SourceMapper sourceMapper = GuiEngine.getEngine().getSourceMapper();
		SourceFile sourceFile = sourceMapper.getSourceFile(m_fileName);
		return sourceFile.getLineForPc(pc);
	}

	String getLineDescription(long index) {
		if (m_endPointLineIndexes.contains(index)) {
			return lineIndexToString(index);
		}
		
		String srcLabel = getSrcLabel(index);
		if (srcLabel != null)
			return srcLabel;
		
		return lineIndexToString(index);
	}
	
	String lineIndexToString(long index) {
		return String.format("%s:%d", m_fileName, index + 1);
	}

	Collection<Long> getPcsForLine(long lineNumber) {
		SourceMapper sourceMapper = GuiEngine.getEngine().getSourceMapper();
		SourceFile sourceFile = sourceMapper.getSourceFile(m_fileName);
		SourceLine sourceLine = sourceFile.getSourceLines().get((int)lineNumber - 1);
		return sourceLine.getAllPcs();
	}

	public boolean isValidEndPoint(long lineNumber) {
		long index = lineNumber - 1;
		if (m_endPointLineIndexes.contains(index)) {
			return true;
		}
		return false;
	}

	String getSrcLabel(long index) {
		long lineNumber = index + 1;
        Map<Long, SrcLabelTableInfo> srcLabelTable = GuiEngine.getEngine().getSrcLabelTable();
		for (SrcLabelTableInfo info : srcLabelTable.values()) {
        	if (info.getFileName().equals(m_fileName)) {
        		long startLineNumber = info.getLineNumberStart();
        		long endLineNumber = info.getLineNumberEnd();
    			if (lineNumber >= startLineNumber && lineNumber <= endLineNumber) {
    				return lineIndexToString(index);
    			}
        	}
        } 
		return null;
	}
}
