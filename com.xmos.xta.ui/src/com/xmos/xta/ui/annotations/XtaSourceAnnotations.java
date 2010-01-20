package com.xmos.xta.ui.annotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.FileNameMatcher;
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

public class XtaSourceAnnotations extends XtaAnnotations {

    private Collection<Long> m_endPointLineIndexes;
    private Map<Long, Set<Long>> m_pcToSourceLineIndex;

    public XtaSourceAnnotations(XtaAnnotatedObject source) {
    	super(source);
        m_endPointLineIndexes = new HashSet<Long>();
        m_pcToSourceLineIndex = new HashMap<Long, Set<Long>>();
    }

	public void connectToFile() {
		super.connectToFile();
		updateLineMap();
	}
	
	public void updateSourcePcs(boolean showPosition) {
		try {
			StyledText text = m_source.getViewer().getTextWidget();
			int charOffset = text.getCaretOffset();
			long lineNumber = m_source.getDocument().getLineOfOffset(charOffset) + 1;
			Set<Long> pcs = new TreeSet<Long>(getPcsForLine(lineNumber));
			GuiEngine.setSourcePcs(pcs, showPosition);
		} catch (BadLocationException ex) {
		}
	}
    
    private void updateLineMap() {
		Engine engine = GuiEngine.getEngine();

		m_pcToSourceLineIndex.clear();
		
		if (!engine.getXeLoader().isLoaded() || m_source.isDirty())
			return;

		SourceMapper sourceMapper = engine.getSourceMapper();
		String fileName = sourceMapper.getDebugFileName(getFileName());
		
		for (SrcLabelTableInfo info : engine.getSrcLabelTable().values()) {			
            if (FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), fileName, sourceMapper)) {
            	for (int lineNumber = info.getLineNumberStart(); lineNumber <= info.getLineNumberEnd(); ++lineNumber) {
                    addPcToLineEntry(info.getPc(), lineNumber);            		
            	}
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
		
        Set<Long> startPointIndexes = getStartPointIndexes();
        Set<Long> endPointIndexes = getEndPointIndexes();

        m_endPointLineIndexes.clear();

        Engine engine = GuiEngine.getEngine();        
		if (canAnnotate()) {
			String fileName = engine.getSourceMapper().getDebugFileName(getFileName());
			
			for (CodePointTableInfo info : engine.getEndpointTable().values()) {
	        	if (FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), fileName, engine.getSourceMapper())) {
	        		long index = info.getLineNumber() - 1;
	        		m_endPointLineIndexes.add(index);
	        		if (!endPointIndexes.contains(index) && !startPointIndexes.contains(index)) {
	        			addAnnotationForIndex(index, EndpointID, allAnnotations, allPositions);
	        		}
	        	}
	        } 
	
	        for (CodePointTableInfo info : engine.getCallTable().values()) {
	        	if (FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), fileName, engine.getSourceMapper())) {
	        		long index = info.getLineNumber() - 1;
	        		m_endPointLineIndexes.add(index);
	        		if (!endPointIndexes.contains(index) && !startPointIndexes.contains(index)) {
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
		if (m_source.isDirty())
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

	public Set<Long> getIndexesForRef(Reference ref) {
		Set<Long> indexes = new TreeSet<Long>();
		if (ref != null) {
			if (ref.getResolved()) {
				if (FileNameMatcher.isMatch(ref.getFileName(), getFileName(), 
						GuiEngine.getEngine().getSourceMapper())) {				
					indexes.add(ref.getLineNum() - 1);
					
				} else {
					for (long pc : ref.getPcs()) {
						indexes.add(getIndexForPc(pc));
					}
				}
			}			
		}
		return indexes;
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
		if (sourceFile == null)
			return null;
			
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

	public boolean isValidEndPoint(long index) {
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
			if (FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), fileName, 
					GuiEngine.getEngine().getSourceMapper())) {
				
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
		return m_source.getFileName();
	}
}
