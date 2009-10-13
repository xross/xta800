package com.xmos.xta.ui.editors.source;

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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.xmos.libs.util.FileNameCompare;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceLine;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.XtaPerspective;
import com.xmos.xta.ui.editors.XtaAnnotatedEditor;
import com.xmos.xta.ui.editors.XtaEditorAnnotations;

public class XtaSourceEditorAnnotations extends XtaEditorAnnotations {

    private Collection<Long> m_endPointLineIndexes;
    private Map<Long, Set<Long>> m_pcToSourceLineIndex;

    public XtaSourceEditorAnnotations(XtaAnnotatedEditor editor) {
    	super(editor);
        m_endPointLineIndexes = new HashSet<Long>();
        m_pcToSourceLineIndex = new HashMap<Long, Set<Long>>();
    }

	public void connectToFile() {
		super.connectToFile();
		updateLineMap();
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
    
    private void updateLineMap() {
		Engine engine = GuiEngine.getEngine();

		m_pcToSourceLineIndex.clear();
		
		if (!engine.getXeLoader().isLoaded() || m_editor.isDirty())
			return;

		for (long lineNumber = 1; lineNumber < m_editor.getDocument().getNumberOfLines(); lineNumber++) {
			String fileName = engine.getSourceMapper().getDebugFileName(getFileName());
    		String refString = String.format("%s:%d", fileName, lineNumber);
    		Reference ref = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);

			if (ref.resolve(engine)) {
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

	public void updateAll() {
		updateUnknowns();
		updateHighlighting();
	}

	public void updateAnnotations(List<Annotation> annotations, HashMap<Annotation, Position> positions) {

		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>(annotations);
		final HashMap<Annotation, Position> allPositions = new HashMap<Annotation, Position>(positions);
		
        long startPointIndex = getStartPointIndex();
        long endPointIndex = getEndPointIndex();

        m_endPointLineIndexes.clear();

        Engine engine = GuiEngine.getEngine();        
		if (canAnnotate()) {
			String fileName = engine.getSourceMapper().getDebugFileName(getFileName());
			
			for (CodePointTableInfo info : engine.getEndpointTable().values()) {
	        	if (FileNameCompare.fileNamesMatch(info.getCompilationDir(), info.getFileName(), fileName)) {
	        		long index = info.getLineNumber() - 1;
	        		m_endPointLineIndexes.add(index);
	        		if (index != endPointIndex && index != startPointIndex) {
	        			addAnnotationForIndex(index, EndpointID, allAnnotations, allPositions);
	        		}
	        	}
	        } 
	
	        for (CodePointTableInfo info : engine.getCallTable().values()) {
	        	if (FileNameCompare.fileNamesMatch(info.getCompilationDir(), info.getFileName(), fileName)) {
	        		long index = info.getLineNumber() - 1;
	        		m_endPointLineIndexes.add(index);
	        		if (index != endPointIndex && index != startPointIndex) {
	        			addAnnotationForIndex(index, EndpointID, allAnnotations, allPositions);
	        		}
	        	}
	        }
		}

        super.updateAnnotations(allAnnotations, allPositions);
	}

    public boolean canAnnotate() {
		if (!GuiEngine.getEngine().getXeLoader().isLoaded())
			return false;
		if (m_editor.isDirty())
			return false;

		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (!page.getPerspective().getId().equals(XtaPerspective.ID)) {
			return false;
		}
			
		return true;
	}

	public boolean addAnnotationsForPc(long pc, String type, List<Annotation> annotations, 
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

	public long getIndexForRef(Reference ref) {
		if (ref != null) {
			if (ref.getResolved()) {
				if (FileNameCompare.fileNamesMatch(ref.getFileName(), getFileName())) {
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

	public long getIndexForPc(long pc) {
		SourceLine sourceLine = getLineForPc(pc);
		if (sourceLine != null) {
			return sourceLine.getLineNumber() - 1;
		}
		return -1;
	}

	private SourceLine getLineForPc(long pc) {
		SourceMapper sourceMapper = GuiEngine.getEngine().getSourceMapper();
		String fileName = sourceMapper.getDebugFileName(getFileName());		
		SourceFile sourceFile = sourceMapper.getSourceFile(fileName);
		return sourceFile.getLineForPc(pc);
	}

	public String getLineDescription(long index) {
		if (!GuiEngine.getEngine().getXeLoader().isLoaded())
			return null;
		
		if (m_endPointLineIndexes.contains(index)) {
			return lineIndexToString(index);
		}
		
		String srcLabel = getSrcLabel(index);
		if (srcLabel != null)
			return srcLabel;
		
		return null;
	}
	
	public String lineIndexToString(long index) {
		return String.format("%s:%d", getFileName(), index + 1);
	}

	public Collection<Long> getPcsForLine(long lineNumber) {
		SourceMapper sourceMapper = GuiEngine.getEngine().getSourceMapper();
		if (sourceMapper != null) {
			String fileName = sourceMapper.getDebugFileName(getFileName());					
			SourceFile sourceFile = sourceMapper.getSourceFile(fileName); 
			if (sourceFile != null) {
				List<SourceLine> sourceLines = sourceFile.getSourceLines();
				if (lineNumber <= sourceLines.size()) {
					SourceLine sourceLine = sourceLines.get((int)lineNumber - 1);
					return sourceLine.getAllPcs();
				}
			}
		}
		return new ArrayList<Long>();
	}

	public boolean isValidEndPoint(long lineNumber) {
		long index = lineNumber - 1;
		if (m_endPointLineIndexes.contains(index)) {
			return true;
		}
		return false;
	}

	String getSrcLabel(long index) {
    	String fileName = getFileName();
		long lineNumber = index + 1;
        Map<Long, SrcLabelTableInfo> srcLabelTable = GuiEngine.getEngine().getSrcLabelTable();
		for (SrcLabelTableInfo info : srcLabelTable.values()) {
			if (FileNameCompare.fileNamesMatch(info.getCompilationDir(), info.getFileName(), fileName)) {
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
