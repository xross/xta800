package com.xmos.xta.ui.editors;

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
import com.xmos.xta.core.sourcemapper.SourceFile;
import com.xmos.xta.core.sourcemapper.SourceLine;
import com.xmos.xta.core.sourcemapper.SourceMapper;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

public class XtaSourceEditor extends XtaEditor {
    public static final String ID = "com.xmos.xta.gui.editors.XtaSourceEditor";

    public XtaSourceEditor() {
    	super(XtaDocumentPartitioner.SOURCE);
    	setSourceViewerConfiguration(new XtaSourceConfiguration(this));
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
	        
			Input input = getInput();
	    	if (input == null)
	    		return;	        

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

    boolean updateAnnotations(long pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions, boolean excludeFnops, boolean excludeSections) {
    	
    	boolean hitSection = false;
    	
    	InputLine line = getInput().getPcsToLines().get(pc);
    	if (line == null) 
    		return hitSection;
    	
    	if (!excludeSections) {
			InputSection inputSection = getInput().getPcsToSection().get(pc);
			if (inputSection != null) {
				addAnnotation(inputSection.getIndex(), type, annotations, annotationPositions);
				hitSection = true;
			}
    	}
    	
		InputLine inputLine = getInput().getPcsToLines().get(pc);
		if (inputLine != null) {
			addAnnotation(line.getIndex(), type, annotations, annotationPositions);
		}
		
		return hitSection;
    }

	boolean showCodePoint(long pc) {
		SourceMapper sourceMapper = GuiEngine.getEngine().getSourceMapper();
		SourceFile sourceFile = sourceMapper.getSourceFile(m_fileName);
		SourceLine sourceLine = sourceFile.getLineForPc(pc);
		if (sourceLine != null) {
			try {
				IDocument document = getDocument();
				long lineNumber = sourceLine.getLineNumber();
				int charOffset = document.getLineOffset((int)lineNumber);
				showLocation(charOffset, lineNumber, false);
				return true;
			} catch (BadLocationException e) {}
		}
		return false;
	}
}
