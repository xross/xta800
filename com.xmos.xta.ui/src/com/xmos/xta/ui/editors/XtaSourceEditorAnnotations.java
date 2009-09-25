package com.xmos.xta.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceLine;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.ui.GuiEngine;

public class XtaSourceEditorAnnotations extends XtaEditorAnnotations {

    private Collection<Long> m_endPointLineIndexes;
    private Map<Long, Set<Long>> m_pcToSourceLineIndex;

    public XtaSourceEditorAnnotations(XtaAnnotatedEditor editor) {
    	super(editor);
        m_endPointLineIndexes = new HashSet<Long>();
        m_pcToSourceLineIndex = new HashMap<Long, Set<Long>>();
    }

	public void updateSourcePcs(boolean showPosition) {
		StyledText text = m_editor.getViewer().getTextWidget();
		int charOffset = text.getCaretOffset();
		try {
			long lineNumber = m_editor.getDocument().getLineOfOffset(charOffset) + 1;
			List<Long> pcs = new ArrayList<Long>(getPcsForLine(lineNumber));
			GuiEngine.setSourcePcs(pcs, showPosition);
		} catch (BadLocationException ex) {
		}
	}
    
    public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("XtaDisassemblyEditor: propertyChange: %s\n", event.getProperty());	

		if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE) ||
			event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			updateLineMap();

		}
		
		super.propertyChange(event);
	}

    private void updateLineMap() {
		Engine engine = GuiEngine.getEngine();
		int nodeId = engine.getNodeId();
		int coreId = engine.getCoreId();

		m_pcToSourceLineIndex.clear();

		for (long lineNumber = 1; lineNumber < m_editor.getDocument().getNumberOfLines(); lineNumber++) {
    		String refString = String.format("%s:%d", getFileName(), lineNumber);
    		Reference ref = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);

			if (ref.resolve(engine, nodeId, coreId)) {
    			long linePc = ref.getPc();
    			addPcToLineEntry(linePc, lineNumber);
    		}
    	}
	}

	private void addPcToLineEntry(long pc, long lineNumber) {
		Set<Long> indexes = m_pcToSourceLineIndex.get(pc);
		if (indexes == null) {
			indexes = new HashSet<Long>();
			m_pcToSourceLineIndex.put(pc, indexes);
		}
		indexes.add(lineNumber - 1);
	}

	void updateAll() {
		updateUnknowns();
		updateHighlighting();
	}

	void updateAnnotations(List<Annotation> annotations, HashMap<Annotation, Position> positions) {

		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>(annotations);
		final HashMap<Annotation, Position> allPositions = new HashMap<Annotation, Position>(positions);
		
        long startPointIndex = getStartPointIndex();
        long endPointIndex = getEndPointIndex();

        m_endPointLineIndexes.clear();
        for (CodePointTableInfo info : GuiEngine.getEngine().getEndpointTable().values()) {
        	if (info.getFileName().equals(getFileName())) {
        		long index = info.getLineNumber() - 1;
        		m_endPointLineIndexes.add(index);
        		if (index != endPointIndex && index != startPointIndex) {
        			addAnnotationForIndex(index, EndpointID, allAnnotations, allPositions);
        		}
        	}
        } 

        for (CodePointTableInfo info : GuiEngine.getEngine().getCallTable().values()) {
        	if (info.getFileName().equals(getFileName())) {
        		long index = info.getLineNumber() - 1;
        		m_endPointLineIndexes.add(index);
        		if (index != endPointIndex && index != startPointIndex) {
        			addAnnotationForIndex(index, EndpointID, allAnnotations, allPositions);
        		}
        	}
        } 

        super.updateAnnotations(allAnnotations, allPositions);
	}

    boolean addAnnotationsForPc(long pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions) {
    	
    	boolean hit = false;
    	for (long index : getIndexesForPc(pc)) {
	    	if (index >= 0) { 
	    		addAnnotationForIndex(index, type, annotations, annotationPositions);
	    		hit = true;
	    	}
    	}
    	return hit;
    }

	long getIndexForRef(Reference ref) {
		if (ref != null) {
			if (ref.getResolved()) {
				if (ref.getFileName().equals(getFileName())) {
					return ref.getLineNum() - 1;
				} else {
					long pc = ref.getPc();
					return getIndexForPc(pc);
				}
			}			
		}
		return -1;
	}

	Collection<Long> getIndexesForPc(long pc) {
		Set<Long> indexes = m_pcToSourceLineIndex.get(pc);
		if (indexes == null) {
			return new HashSet<Long>();
		} else {
			return indexes;
		}
	}

	long getIndexForPc(long pc) {
		SourceLine sourceLine = getLineForPc(pc);
		if (sourceLine != null) {
			return sourceLine.getLineNumber() - 1;
		}
		return -1;
	}

	private SourceLine getLineForPc(long pc) {
		SourceMapper sourceMapper = GuiEngine.getEngine().getSourceMapper();
		SourceFile sourceFile = sourceMapper.getSourceFile(getFileName());
		return sourceFile.getLineForPc(pc);
	}

	String getLineDescription(long index) {
		if (m_endPointLineIndexes.contains(index)) {
			return lineIndexToString(index);
		}
		
		String srcLabel = getSrcLabel(index);
		if (srcLabel != null)
			return srcLabel;
		
		return null;
	}
	
	String lineIndexToString(long index) {
		return String.format("%s:%d", getFileName(), index + 1);
	}

	Collection<Long> getPcsForLine(long lineNumber) {
		SourceMapper sourceMapper = GuiEngine.getEngine().getSourceMapper();
		SourceFile sourceFile = sourceMapper.getSourceFile(getFileName());
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
        	if (info.getFileName().equals(getFileName())) {
        		long startLineNumber = info.getLineNumberStart();
        		long endLineNumber = info.getLineNumberEnd();
    			if (lineNumber >= startLineNumber && lineNumber <= endLineNumber) {
    				return lineIndexToString(index);
    			}
        	}
        } 
		return null;
	}
	
	private String getFileName() {
		return m_editor.getFileName();
	}
}
