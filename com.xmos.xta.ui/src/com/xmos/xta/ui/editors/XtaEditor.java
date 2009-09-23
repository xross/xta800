package com.xmos.xta.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.dialogs.InstructionTime;
import com.xmos.xta.ui.icons.Icons;

public abstract class XtaEditor extends TextEditor implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    
    public static final String ID                    = "com.xmos.xta.ui.editors.XtaEditor.Test";
    public static final String HighlightAnnotationID = "com.xmos.xta.ui.highlight";
    public static final String UnknownAnnotationID   = "com.xmos.xta.ui.unknown";
    public static final String StartAnnotationID     = "com.xmos.xta.ui.start";
    public static final String StopAnnotationID      = "com.xmos.xta.ui.stop";
    public static final String StartStopAnnotationID = "com.xmos.xta.ui.startstop";
    public static final String ExcludedAnnotationID  = "com.xmos.xta.ui.excluded";
    public static final String EndpointID            = "com.xmos.xta.ui.endpoint";
    public static final String ExpandAllID           = "com.xmos.xta.ui.action.ExpandAll";
    public static final String CollapseAllID         = "com.xmos.xta.ui.action.CollapseAll";
    public static final String ShowStartPointID      = "com.xmos.xta.ui.action.ShowStartPoint";
    public static final String ShowEndPointID        = "com.xmos.xta.ui.action.ShowEndPoint";
    public static final String SetStartPointID       = "com.xmos.xta.ui.action.SetStartPoint";
    public static final String SetEndPointID         = "com.xmos.xta.ui.action.SetEndPoint";
    public static final String RemoveStartPointID    = "com.xmos.xta.ui.action.RemoveStartPoint";
    public static final String RemoveEndPointID      = "com.xmos.xta.ui.action.RemoveEndPoint";
    public static final String SetInstructionTimeID  = "com.xmos.xta.ui.action.SetInstructionTime";
    public static final String AddExclusionID        = "com.xmos.xta.ui.action.AddExclusion";
    public static final String RemoveExclusionID     = "com.xmos.xta.ui.action.RemoveExclusion";
    public static final String SelectPathsID         = "com.xmos.xta.ui.action.ShowPaths";

    private Composite m_parent;
    private Action m_showStartPointAction;
    private Action m_showEndPointAction;
    private Action m_setInstructionTimeAction;
    private String m_clickCodeRefString;
    protected ProjectionAnnotationModel m_projectionAnnotationModel;
    protected IAnnotationModelExtension m_annotationModel;
    private AnnotationPainter m_painter;
    private Annotation[] m_previousHighlightAnnotations;
    private Annotation[] m_previousUnknownAnnotations;
    protected List<Node> m_selectedNodes;    
    private final String m_type;
    private long m_startPointLineIndex;
    private long m_endPointLineIndex;
    private TreeSet<Long> m_excludedIndexes;
	protected TreeSet<XtaInstruction> m_previousFnops;

    protected String m_fileName;
    protected String m_partName;

    // Abstract methods
	public abstract boolean isValidEndPoint(long lineNumber);

    abstract void updateAll();
	abstract long findIndexForPc(long pc);
    abstract boolean updateAnnotations(long pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions, boolean excludeFnops, boolean excludeSections);
	abstract String getLineDescription(long clickLineIndex);
	abstract String lineIndexToString(long index);
	abstract Collection<Long> getPcsForLine(long lineNumber);


    public XtaEditor(final String type) {
		if (DEBUG) System.out.printf("XtaEditor()\n");
		m_type = type;

		GuiEngine.addBroadcastListener(this);

    	setDocumentProvider(new XtaDocumentProvider());

    	m_selectedNodes = new ArrayList<Node>();
        
        m_excludedIndexes = new TreeSet<Long>();
        m_previousFnops = new TreeSet<XtaInstruction>();

        m_startPointLineIndex = -1;
        m_endPointLineIndex = -1;
    }

    public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("XtaEditor: createPartControl\n"); 
    	super.createPartControl(parent);
    	m_parent = parent;
    	
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
    	   	
		ProjectionSupport projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
        projectionSupport.install();

        viewer.doOperation(ProjectionViewer.TOGGLE);

        m_projectionAnnotationModel = viewer.getProjectionAnnotationModel();
        m_annotationModel = (IAnnotationModelExtension)viewer.getAnnotationModel();
        
        viewer.showAnnotations(true);
        viewer.showAnnotationsOverview(true);
        
        m_painter = new AnnotationPainter(viewer, getAnnotationAccess());        
        m_painter.addAnnotationType(HighlightAnnotationID);
        updateHighlighting();
    }
    
    public void dispose() {
		if (DEBUG) System.out.printf("XtaEditor: dispose\n");	
		GuiEngine.removeBroadcastListener(this);
		super.dispose();
    }

	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
    	if (DEBUG) System.out.printf("XtaEditor: createSourceViewer\n"); 

        ISourceViewer viewer = new ProjectionViewer(parent, ruler,
		    getOverviewRuler(), isOverviewRulerVisible(), styles);

        getSourceViewerDecorationSupport(viewer);
        return viewer;
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

	public String getType() {
		return m_type;
	}

	public void updateSelection(List<Node> selection) {
		if (DEBUG) System.out.printf("XtaEditor: selectionChanged\n");
		m_selectedNodes = selection;
		updateAll();
    }


	public TreeSet<Node> getUnknownsForLine(long lineNumber) {
	    if (DEBUG) System.out.printf("XtaEditor: getUnknownsForLine\n"); 

    	TreeSet<Node> nodes = new TreeSet<Node>();
    	Collection<Long> linePcs = getPcsForLine(lineNumber);
		for (Node node : getSelectedRouteUnknowns()) {
			if (linePcs.contains(node.getStartPc())) {
				nodes.add(node);
			}
		}
    	return nodes;
    }
	
	public long getStartPointIndex() {
		return m_startPointLineIndex;
	}

    public long getEndPointIndex() {
		return m_endPointLineIndex;
	}
	
    protected void createActions() {
		if (DEBUG) System.out.printf("XtaEditor: createActions\n"); 
     	super.createActions();
     	
     	m_showStartPointAction = new Action("Show start point") {
     		public void run() {
				GuiEngine.broadcast(Broadcaster.BROADCAST_SHOW_CODE_POINT, "");
     		}
     	};
     	setAction(ShowStartPointID, m_showStartPointAction);
     	
     	m_showEndPointAction = new Action("Show end point") {
     		public void run() {
				GuiEngine.broadcast(Broadcaster.BROADCAST_SHOW_CODE_POINT, "");
     		}
     	};
     	setAction(ShowEndPointID, m_showEndPointAction);

     	setAction(SetStartPointID, new Action("Set from endpoint") {
     		public void run() {
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SET_START_POINT, m_clickCodeRefString);
     		}
     	});
     	
     	setAction(SetEndPointID, new Action("Set to endpoint") {
     		public void run() {
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SET_END_POINT, m_clickCodeRefString);
     		}
     	});
     	
     	setAction(RemoveStartPointID, new Action("Remove from endpoint") {
     		public void run() {
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SET_START_POINT, "");     			
     		}
     	});
     	
     	setAction(RemoveEndPointID, new Action("Remove to endpoint") {
     		public void run() {
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SET_END_POINT, "");
     		}
     	});
     	
     	setAction(AddExclusionID, new Action("Add Exclusion") {
     		public void run() {
     	    	GuiEngine.getEngine().addExclusion(GuiEngine.s_guiCommandSrc, m_clickCodeRefString);		
     		}
     	});

     	setAction(RemoveExclusionID, new Action("Remove Exclusion") {
     		public void run() {
     	    	GuiEngine.getEngine().removeExclusion(GuiEngine.s_guiCommandSrc, m_clickCodeRefString);		
     		}
     	});
     	
     	setAction(SelectPathsID, new Action("Select blocks through here") {
     		public void run() {
     			Reference clickCodeRef = ReferenceFactory.createReference(m_clickCodeRefString, ReferenceClass.ANY_REF);
     			GuiEngine.setCodePoint(clickCodeRef);
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SELECT_NODES, "");
     		}
     	});

     	m_setInstructionTimeAction = new Action("Set instruction time") {
     		public void run() {
     	    	List<Node> nodes = new ArrayList<Node>();
     	    	Engine engine = GuiEngine.getEngine();
     	    	Reference reference = ReferenceFactory.createReference(m_clickCodeRefString, ReferenceClass.ANY_REF);
     	    	if (!reference.resolve(engine, engine.getNodeId(), engine.getCoreId())) {
     	    		assert(false);
     	    	}
     			nodes.addAll(engine.getSelectedRoute().getReferencedNodes(reference));
     			new InstructionTime(m_parent.getShell(), nodes);
     		}
     	};
     	setAction(SetInstructionTimeID, m_setInstructionTimeAction);
    }    

	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		if (DEBUG) System.out.printf("XtaEditor: editorContextMenuAboutToShow\n"); 
     	super.editorContextMenuAboutToShow(menu);

		menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
		Reference startPoint = GuiEngine.getStartPoint();
		addAction(menu, ShowStartPointID);
		m_showStartPointAction.setEnabled((startPoint != null));

		Reference endPoint = GuiEngine.getEndPoint();
		addAction(menu, ShowEndPointID);
		m_showEndPointAction.setEnabled((endPoint != null));
    }

    protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		if (DEBUG) System.out.printf("XtaEditor: rulerContextMenuAboutToShow\n");
		
		long clickLineIndex = getVerticalRuler().getLineOfLastMouseButtonActivity();
		
		m_clickCodeRefString = getLineDescription(clickLineIndex);				
		if (m_clickCodeRefString != null) {	
			if (isStartPointSetForIndex(clickLineIndex)) {
			    addAction(menu, RemoveStartPointID);
			} else {
			    addAction(menu, SetStartPointID);				
			}
			if (isEndPointSetForIndex(clickLineIndex)) {						
			    addAction(menu, RemoveEndPointID);
			} else {
			    addAction(menu, SetEndPointID);				
			}
			menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
			
			if (m_excludedIndexes.contains(clickLineIndex)) {
				addAction(menu, RemoveExclusionID);				
			} else {
				addAction(menu, AddExclusionID);				
			}
			
			addAction(menu, SetInstructionTimeID);			
			addAction(menu, SelectPathsID);
			
			int selectedRouteId = GuiEngine.getEngine().getSelectedRouteId();
			if ((GuiEngine.getEngine().getRoutes().size() > 0) &&
			     GuiEngine.getEngine().canInstructionPause(selectedRouteId, m_clickCodeRefString)) {
				
				m_setInstructionTimeAction.setEnabled(true);
			} else {
				m_setInstructionTimeAction.setEnabled(false);
			}
		}
     	super.editorContextMenuAboutToShow(menu);
    }
	
	protected void updateStartPoint() {
		Reference startPoint = GuiEngine.getStartPoint();
		m_startPointLineIndex = findIndexForRef(startPoint);
	}

	protected void updateEndPoint() {
		Reference endPoint = GuiEngine.getEndPoint();
		m_endPointLineIndex = findIndexForRef(endPoint);
	}
	    	
    protected void updateHighlighting() {
		if (DEBUG) System.out.printf("XtaEditor: updateHighlighting\n");
		
	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        HashMap<Annotation, Position> annotationPositions = new HashMap<Annotation, Position>();		
		
        boolean hasHighlightedLines = false;
        for (Node node : m_selectedNodes) {
        	Collection<XtaInstruction> instructionsCovered = node.getInstructions();
        	for (XtaInstruction instruction : instructionsCovered) {
        		hasHighlightedLines |= updateAnnotations(
        				instruction.getPc(), HighlightAnnotationID, 
        				annotations, annotationPositions,
						false, false);
			}
        }
		updateAnnotations(annotations, annotationPositions);		
		
		if (hasHighlightedLines) {
			setTitleImage(Icons.getIcon("selected"));
		} else {
			setTitleImage(Icons.getIcon("xta16x16"));
		}
    }

	void updateAnnotations(List<Annotation> annotations, HashMap<Annotation, Position> positions) {

		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>(annotations);
		final HashMap<Annotation, Position> allPositions = new HashMap<Annotation, Position>(positions);
		
        long startPointIndex = getStartPointIndex();
        long endPointIndex = getEndPointIndex();

        if ((startPointIndex != -1) && (startPointIndex == endPointIndex)) {
        	addAnnotation(startPointIndex, StartStopAnnotationID, allAnnotations, allPositions);
        } else {
        	if (startPointIndex != -1) {
            	addAnnotation(startPointIndex, StartAnnotationID, allAnnotations, allPositions);
        	}
        	if (endPointIndex != -1) {
            	addAnnotation(endPointIndex, StopAnnotationID, allAnnotations, allPositions);
        	}
        }

    	for (Long excludedIndex : m_excludedIndexes) {
    		addAnnotation(excludedIndex, ExcludedAnnotationID, allAnnotations, allPositions);
		}
    	
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				m_annotationModel.replaceAnnotations(m_previousHighlightAnnotations, allPositions);
			}});
		m_previousHighlightAnnotations = createArray(allAnnotations);
	}

	protected void updateExcluded() {
		m_excludedIndexes.clear();
		
		Collection<Reference> exclusions = GuiEngine.getEngine().getExclusions();
		if (exclusions.size() == 0) {
			return;
		}

		for (Reference exclusion : exclusions) {
			long excludedIndex = findIndexForPc(exclusion.getPc());
			if (excludedIndex != -1) {
				m_excludedIndexes.add(excludedIndex);
			}
		}
	}

	private Collection<Node> getSelectedRouteUnknowns() {
		Engine engine = GuiEngine.getEngine();
		Route route = engine.getSelectedRoute();
		if (route != null) {
            return route.getRouteUnknowns().getUnknowns(UnknownState.UNKNOWN);
		} else {
			return new TreeSet<Node>();
		}
	}

	protected void updateUnknowns() {
		if (DEBUG) System.out.printf("XtaEditor: updateUnknowns\n"); 

	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        final HashMap<Annotation, Position> annotationPositions = new HashMap<Annotation, Position>();		

		TreeSet<Long> unknownsSet = new TreeSet<Long>();
		for (Node node : getSelectedRouteUnknowns()) {
			unknownsSet.add(node.getStartPc());
		}
		boolean excludeSections = m_type.equals(XtaDocumentPartitioner.SOURCE);
		for (long pc : unknownsSet) {
			updateAnnotations(pc, UnknownAnnotationID, annotations, annotationPositions,
					true, excludeSections);
		}

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				m_annotationModel.replaceAnnotations(m_previousUnknownAnnotations, annotationPositions);        
			}
		});
		m_previousUnknownAnnotations = createArray(annotations);
    }

    protected void addAnnotation(long index, String type, List<Annotation> annotations,
    		Map<Annotation, Position> annotationPositions) {

		ISourceViewer sourceViewer = getSourceViewer();
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
        
    private Annotation[] createArray(List<Annotation> annotationList) {
    	Annotation[] annotationArray = new Annotation[annotationList.size()];
    	return annotationList.toArray(annotationArray);    	
    }

	protected void showLocation(int charOffset, long index, boolean expand) {
		ISourceViewer viewer = getSourceViewer();
		if (viewer == null)
			return;

		if (expand) {
			m_projectionAnnotationModel.expandAll(charOffset, 1);
			viewer.setRangeIndication(charOffset, 0, true);
		}

		long topIndex = Math.max(0, index - 10);
		viewer.setTopIndex((int)topIndex);
	}

	private long findIndexForRef(Reference ref) {
		if (ref != null) {
			if (ref.getResolved()) {
			    long pc = ref.getPc();
			    return findIndexForPc(pc);
			}			
		}
		return -1;
	}

	IDocument getDocument() {
		ISourceViewer viewer = getSourceViewer();
		if (viewer == null)
			return null;
		
		return viewer.getDocument();
	}

	private boolean isStartPointSetForIndex(long index) {
		if ((m_startPointLineIndex != -1) && (m_startPointLineIndex == index)) {
			return true;
		}
	    return false;	
	}

	private boolean isEndPointSetForIndex(long index) {
		if ((m_endPointLineIndex != -1) && (m_endPointLineIndex == index)) {
			return true;
		}
	    return false;	
	}

	public void setNames(String partName, String fileName) {
		m_partName = partName;
		setPartName(partName);
		m_fileName = fileName;
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
}
