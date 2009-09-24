package com.xmos.xta.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public static final String ID = "com.xmos.xta.ui.editors.XtaEditor.Test";
    
    public static final String HighlightAnnotationID = "com.xmos.xta.ui.highlight";
    public static final String SourcePcsAnnotationID = "com.xmos.xta.ui.source";
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
    private Annotation[] m_previousHighlightAnnotations;
    private Annotation[] m_previousUnknownAnnotations;
    protected List<Node> m_selectedNodes;    

    protected String m_fileName;
    protected String m_partName;

    // Abstract methods
	public abstract boolean isValidEndPoint(long lineNumber);
    abstract void updateAll();
	abstract long getIndexForRef(Reference ref);
	abstract long getIndexForPc(long pc);
    abstract boolean addAnnotationsForPc(long pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions);
	abstract String getLineDescription(long clickLineIndex);
	abstract String lineIndexToString(long index);
	abstract Collection<Long> getPcsForLine(long lineNumber);


    public XtaEditor() {
		if (DEBUG) System.out.printf("XtaEditor()\n");
		GuiEngine.addBroadcastListener(this);

    	setDocumentProvider(new XtaDocumentProvider());

    	m_selectedNodes = new ArrayList<Node>();
    }

    public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("XtaEditor: createPartControl\n"); 
    	super.createPartControl(parent);
    	m_parent = parent;
    	
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
        viewer.showAnnotations(true);
        viewer.showAnnotationsOverview(true);
        
        AnnotationPainter painter = new AnnotationPainter(viewer, getAnnotationAccess());        
        painter.addAnnotationType(HighlightAnnotationID);
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
			updateAll();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_START_POINT) ||
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_START_POINT)) {
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT) ||
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_END_POINT)) {
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			updateUnknowns();
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUSIONS)) {
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			updateHighlighting();

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECTION_CHANGED)) {
			m_selectedNodes = GuiEngine.getSelectedNodes();
			updateAll();
			
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
			if (isValidEndPoint(clickLineIndex + 1)) {
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
			}
			
			if (isExcluded(m_clickCodeRefString)) {
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
	
    private boolean isExcluded(String refString) {
		Reference ref = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF);
		Engine engine = GuiEngine.getEngine();
		if (ref.resolve(engine, engine.getNodeId(), engine.getCoreId())) {
			long refPc = ref.getPc();
			Collection<Reference> exclusions = GuiEngine.getEngine().getExclusions();
			for (Reference exclusion : exclusions) {
				if (exclusion.getPc() == refPc) {
					return true;
				}
			}
		}
		return false;
	}
    
	protected void updateHighlighting() {
		if (DEBUG) System.out.printf("XtaEditor: updateHighlighting\n");
		
	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        HashMap<Annotation, Position> annotationPositions = new HashMap<Annotation, Position>();		
		
        boolean hasHighlightedLines = false;
        for (Node node : m_selectedNodes) {
        	Collection<XtaInstruction> instructionsCovered = node.getInstructions();
        	for (XtaInstruction instruction : instructionsCovered) {
        		hasHighlightedLines |= addAnnotationsForPc(
        				instruction.getPc(), HighlightAnnotationID, 
        				annotations, annotationPositions);
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
        	addAnnotationForIndex(startPointIndex, StartStopAnnotationID, allAnnotations, allPositions);
        } else {
        	if (startPointIndex != -1) {
            	addAnnotationForIndex(startPointIndex, StartAnnotationID, allAnnotations, allPositions);
        	}
        	if (endPointIndex != -1) {
            	addAnnotationForIndex(endPointIndex, StopAnnotationID, allAnnotations, allPositions);
        	}
        }

        for (Reference exclusion : GuiEngine.getEngine().getExclusions()) {
			addAnnotationsForPc(exclusion.getPc(), ExcludedAnnotationID, allAnnotations, allPositions);
		}
    	
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				getAnnotationModel().replaceAnnotations(m_previousHighlightAnnotations, allPositions);
			}
		});

		m_previousHighlightAnnotations = createArray(allAnnotations);
	}

    protected void addAnnotationForIndex(long index, String type, List<Annotation> annotations,
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
        
	protected void updateUnknowns() {
	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        final HashMap<Annotation, Position> annotationPositions = new HashMap<Annotation, Position>();		

		TreeSet<Long> unknownsSet = new TreeSet<Long>();
		for (Node node : getSelectedRouteUnknowns()) {
			unknownsSet.add(node.getStartPc());
		}
		for (long pc : unknownsSet) {
			addAnnotationsForPc(pc, UnknownAnnotationID, annotations, annotationPositions);
		}

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				getAnnotationModel().replaceAnnotations(m_previousUnknownAnnotations, annotationPositions);        
			}
		});
		m_previousUnknownAnnotations = createArray(annotations);
    }

	public Set<Node> getUnknownsForLine(long lineNumber) {
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
	
	private Collection<Node> getSelectedRouteUnknowns() {
		Engine engine = GuiEngine.getEngine();
		Route route = engine.getSelectedRoute();
		if (route != null) {
            return route.getRouteUnknowns().getUnknowns(UnknownState.UNKNOWN);
		} else {
			return new TreeSet<Node>();
		}
	}

    private Annotation[] createArray(List<Annotation> annotationList) {
    	Annotation[] annotationArray = new Annotation[annotationList.size()];
    	return annotationList.toArray(annotationArray);    	
    }

	private boolean isStartPointSetForIndex(long index) {
		long startPointLineIndex = getStartPointIndex();
		if ((startPointLineIndex != -1) && (startPointLineIndex == index)) {
			return true;
		}
	    return false;	
	}

	private boolean isEndPointSetForIndex(long index) {
		long endPointIndex = getEndPointIndex();
		if ((endPointIndex != -1) && (endPointIndex == index)) {
			return true;
		}
	    return false;	
	}

	public long getStartPointIndex() {
		Reference ref = GuiEngine.getStartPoint();
		return getIndexForRef(ref);
	}

    public long getEndPointIndex() {
		Reference ref = GuiEngine.getEndPoint();
		return getIndexForRef(ref);
	}

	// Once the name is set then the endpoints can be updated
    public void setNames(String partName, String fileName) {
		m_partName = partName;
		setPartName(partName);
		m_fileName = fileName;
        updateHighlighting();
	}

	protected void showLocation(int charOffset, long index, boolean expand) {
		ISourceViewer viewer = getSourceViewer();
		if (viewer == null)
			return;

		ProjectionAnnotationModel model = getProjectionAnnotationModel();
		if (model != null) {
			model.expandAll(charOffset, 1);
			viewer.setRangeIndication(charOffset, 0, true);
		}

		long topIndex = Math.max(0, index - 5);
		viewer.setTopIndex((int)topIndex);
	}

	boolean showCodePoint(long pc) {
    	long index = getIndexForPc(pc);
    	if (index >= 0) { 
			try {
				IDocument document = getDocument();
				int charOffset = document.getLineOffset((int)index);
				showLocation(charOffset, index, true);
				return true;
			} catch (BadLocationException e) {}
		}
		return false;
	}

	IDocument getDocument() {
		ISourceViewer viewer = getSourceViewer();
		if (viewer == null)
			return null;
		
		return viewer.getDocument();
	}

	public ProjectionAnnotationModel getProjectionAnnotationModel() {
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
		return viewer.getProjectionAnnotationModel();
	}

	private IAnnotationModelExtension getAnnotationModel() {
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
        return (IAnnotationModelExtension)viewer.getAnnotationModel();
	}
}
