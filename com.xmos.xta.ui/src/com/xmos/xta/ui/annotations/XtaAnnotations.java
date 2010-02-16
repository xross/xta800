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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.editors.XtaDocumentPartitioner;
import com.xmos.xta.ui.input.InputSection;

public abstract class XtaAnnotations implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    
    public static final String HighlightAnnotationID = "com.xmos.xta.ui.highlight";
    public static final String SourcePcsAnnotationID = "com.xmos.xta.ui.source";
    public static final String UnknownAnnotationID   = "com.xmos.xta.ui.unknown";
    public static final String StartAnnotationID     = "com.xmos.xta.ui.start";
    public static final String StopAnnotationID      = "com.xmos.xta.ui.stop";
    public static final String StartStopAnnotationID = "com.xmos.xta.ui.startstop";
    public static final String ExcludedAnnotationID  = "com.xmos.xta.ui.excluded";
    public static final String EndpointID            = "com.xmos.xta.ui.endpoint";

    protected boolean m_isDisposed;
    private Annotation[] m_previousHighlightAnnotations;
    private Annotation[] m_previousUnknownAnnotations;
    protected List<Node> m_selectedNodes;    

	protected XtaAnnotatedObject m_source;

    // Abstract methods
	public abstract boolean isValidEndPoint(long index);
	public abstract String getLineDescription(long clickLineIndex);

	public abstract void updateAll();
	public abstract Set<Long> getIndexesForRef(Reference ref);
	public abstract long getIndexForPc(long pc);
	public abstract boolean addAnnotationsForPc(long pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions);
	public abstract String lineIndexToString(long index);
	public abstract Collection<Long> getPcsForLine(long lineNumber);
	public abstract boolean canAnnotate();

    public XtaAnnotations(XtaAnnotatedObject source) {
		if (DEBUG) System.out.printf("XtaEditor()\n");
		m_source = source;
		GuiEngine.addBroadcastListener(this);

    	m_selectedNodes = new ArrayList<Node>();
    	m_isDisposed = false;
    }

	public void dispose() {
		GuiEngine.removeBroadcastListener(this);
		m_isDisposed = true;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("XtaEditor: propertyChange: %s\n", event.getProperty());	
		if (m_isDisposed)
			return;

		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			connectToFile();
			updateAll();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE)) {
			updateAll();

		} else if (event.getProperty().equals(Notifier.NOTIFY_FROM_END_POINT_SET) ||
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_START_POINT)) {
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_TO_END_POINT_SET) ||
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_END_POINT)) {
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			updateUnknowns();
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUSIONS)) {
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			connectToFile();
			updateHighlighting();

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECTION_CHANGED)) {
			m_selectedNodes = GuiEngine.getSelectedNodes();
			updateAll();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SET_FOCUS)) {
			Node focusNode = GuiEngine.getFocus();
			if (focusNode != null) {
				Set<Long> pcs = new TreeSet<Long>();
				for (XtaInstruction instruction : focusNode.getInstructions()) {
					pcs.add(instruction.getPc());
				}
				boolean hit = showCodePoint(pcs);
				if (hit) {
					return;
				}
			}
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_PERSPECTIVE_CHANGED)) {
			updateAll();			
		}
	}

	public void updateSourcePcs(boolean showPosition) {
		// Do nothing
	}

	public void connectToFile() {
		m_selectedNodes = GuiEngine.getSelectedNodes();
	}

	public boolean isExcluded(String refString) {
		Reference ref = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		Engine engine = GuiEngine.getEngine();
		if (ref.resolve(engine)) {
			Collection<Reference> exclusions = GuiEngine.getEngine().getExclusions();
			for (Reference exclusion : exclusions) {
				if (exclusion.equals(ref)) {
					return true;
				}
			}
		}
		return false;
	}
    
	public void updateHighlighting() {
		if (DEBUG) System.out.printf("XtaEditorAnnotations: updateHighlighting\n");
		
	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        HashMap<Annotation, Position> annotationPositions = new HashMap<Annotation, Position>();		
		
        boolean hasHighlightedLines = false;
        
        if (canAnnotate()) {
        	Collection<Long> pcs = new HashSet<Long>();
	        for (Node node : m_selectedNodes) {
	        	for (XtaInstruction instruction : node.getInstructions()) {
	        		pcs.add(instruction.getPc());
	        	}
	        }
        	for (long pc : pcs) {
        		hasHighlightedLines |= addAnnotationsForPc(pc, HighlightAnnotationID, 
        				annotations, annotationPositions);
			}
        }
		updateAnnotations(annotations, annotationPositions);		
    }

	public void updateAnnotations(List<Annotation> annotations, HashMap<Annotation, Position> positions) {

		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>(annotations);
		final HashMap<Annotation, Position> allPositions = new HashMap<Annotation, Position>(positions);
		
		if (canAnnotate()) {
	        Set<Long> startPointIndexes = getStartPointIndexes();
	        Set<Long> endPointIndexes = getEndPointIndexes();
	        
	        startPointIndexes.remove(Long.valueOf(-1));
	        endPointIndexes.remove(Long.valueOf(-1));
	
	        for (long startPointIndex : startPointIndexes) {
	        	if (endPointIndexes.contains(startPointIndex)) {
		        	addAnnotationForIndex(startPointIndex, StartStopAnnotationID, allAnnotations, allPositions);
	        	} else {
	            	addAnnotationForIndex(startPointIndex, StartAnnotationID, allAnnotations, allPositions);
	        	}
	        }
	        for (long endPointIndex : endPointIndexes) {
	        	if (!startPointIndexes.contains(endPointIndex)) {
	        		addAnnotationForIndex(endPointIndex, StopAnnotationID, allAnnotations, allPositions);
	        	}
	        }
	
	        for (Reference exclusion : GuiEngine.getEngine().getExclusions()) {
	        	for (long pc : exclusion.getPcs()) {
	        		addAnnotationsForPc(pc, ExcludedAnnotationID, allAnnotations, allPositions);
	        	}
			}
		}
    	
        if (!m_isDisposed) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {      
					m_source.getAnnotationModel().replaceAnnotations(m_previousHighlightAnnotations, allPositions);
				}
			});
        }

		m_previousHighlightAnnotations = createArray(allAnnotations);
	}

    protected void addAnnotationForIndex(long index, String type, List<Annotation> annotations,
    		Map<Annotation, Position> annotationPositions) {

		ISourceViewer sourceViewer = m_source.getViewer();
		if (sourceViewer == null)
			return;
		
    	IDocument document = sourceViewer.getDocument();
        try {
    		IRegion lineInfo = document.getLineInformation((int)index);
            Position position = new Position(lineInfo.getOffset(), lineInfo.getLength());
            Annotation annotation = new Annotation(type, true, "");
            annotationPositions.put(annotation, position);
            annotations.add(annotation);
            
		} catch (BadLocationException e) {
			e.printStackTrace();
		}						
    }
        
	protected void updateUnknowns() {
	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        final HashMap<Annotation, Position> annotationPositions = new HashMap<Annotation, Position>();		

		TreeSet<Long> unknownsSet = new TreeSet<Long>();
		for (Unknown unknown : getSelectedRouteUnknowns()) {
			unknownsSet.add(unknown.getNode().getStartPc());
		}
		for (long pc : unknownsSet) {
			addAnnotationsForPc(pc, UnknownAnnotationID, annotations, annotationPositions);
		}

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				m_source.getAnnotationModel().
					replaceAnnotations(m_previousUnknownAnnotations, annotationPositions);        
			}
		});
		m_previousUnknownAnnotations = createArray(annotations);
    }

	public Set<Unknown> getUnknownsForLine(long lineNumber) {
	    if (DEBUG) System.out.printf("XtaEditor: getUnknownsForLine\n"); 
    	TreeSet<Unknown> unknowns = new TreeSet<Unknown>();
    	Collection<Long> linePcs = getPcsForLine(lineNumber);
		for (Unknown unknown : getSelectedRouteUnknowns()) {
			if (linePcs.contains(unknown.getNode().getStartPc())) {
				unknowns.add(unknown);
			}
		}
    	return unknowns;
    }

	private Collection<Unknown> getSelectedRouteUnknowns() {
		Engine engine = GuiEngine.getEngine();
		Route route = engine.getSelectedRoute();
		if (route != null) {
            return route.getRouteUnknowns().getUnknowns(State.UNKNOWN);
		} else {
			return new TreeSet<Unknown>();
		}
	}

    private Annotation[] createArray(List<Annotation> annotationList) {
    	Annotation[] annotationArray = new Annotation[annotationList.size()];
    	return annotationList.toArray(annotationArray);    	
    }

	public boolean isStartPointSetForIndex(long index) {
		Set<Long> startPointLineIndexes = getStartPointIndexes();
		if (startPointLineIndexes.contains(index)) {
			return true;
		}
	    return false;	
	}

	public boolean isEndPointSetForIndex(long index) {
		Set<Long> endPointLineIndexes = getEndPointIndexes();
		if (endPointLineIndexes.contains(index)) {
			return true;
		}
	    return false;	
	}

	public Set<Long> getStartPointIndexes() {
		Reference ref = GuiEngine.getEngine().getFromEndPoint();
		return getIndexesForRef(ref);
	}

    public Set<Long> getEndPointIndexes() {
		Reference ref = GuiEngine.getEngine().getToEndPoint();
		return getIndexesForRef(ref);
	}

	protected void showLocation(int charOffset, long index, boolean expand) {
		ISourceViewer viewer = m_source.getViewer();
		if (viewer == null)
			return;

		ProjectionAnnotationModel model = m_source.getProjectionAnnotationModel();
		if (model != null) {
			model.expandAll(charOffset, 1);
			viewer.setRangeIndication(charOffset, 0, true);
		}

		long topIndex = Math.max(0, index - 5);
		viewer.setTopIndex((int)topIndex);
	}

	public boolean showCodePoint(Set<Long> pcs) {
		for (long pc : pcs) {
	    	long index = getIndexForPc(pc);
	    	if (index >= 0) { 
				try {
					IDocument document = m_source.getDocument();
					int charOffset = document.getLineOffset((int)index);
					showLocation(charOffset, index, true);
					return true;
				} catch (BadLocationException e) {}
			}
		}
		return false;
	}
	
	private ArrayList<InputSection> getSections() {
		IDocument document = m_source.getDocument();
		if (document == null)
			return new ArrayList<InputSection>();
		
		XtaDocumentPartitioner xtaDocumentPartitioner =
			(XtaDocumentPartitioner)document.getDocumentPartitioner();

		return xtaDocumentPartitioner.getInput().getSections();
	}

    public boolean isSection(long index) {
    	for (InputSection inputSection : getSections()) {
    		long sectionIndex = inputSection.getIndex();
			if (sectionIndex == index) {
    			return true;
    		} else if (sectionIndex > index) {
    			return false;
    		}
    	}
		return false;
	}
}
