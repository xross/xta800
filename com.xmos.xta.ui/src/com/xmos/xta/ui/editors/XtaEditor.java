package com.xmos.xta.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

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
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

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
    private ProjectionSupport m_projectionSupport;    
    protected ProjectionAnnotationModel m_projectionAnnotationModel;
    private IAnnotationModelExtension m_annotationModel;
    private AnnotationPainter m_painter;
    private Annotation[] m_previousProjectionAnnotations;
    private Annotation[] m_previousHighlightAnnotations;
    private Annotation[] m_previousUnknownAnnotations;
    protected List<Node> m_selectedNodes;    
    private final String m_type;
    private InputLine m_startPointLine;
    private InputLine m_endPointLine;
    private Map<Long, InputLine> m_endPointLineIndexes;
    private TreeSet<Long> m_excludedIndexes;
	protected TreeSet<XtaInstruction> m_previousFnops;

    protected String m_fileName;
	
	public abstract void propertyChange(PropertyChangeEvent event);
	abstract boolean showCodePoint(long pc);
    abstract void updateAll();

    abstract boolean updateAnnotations(long pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions, boolean excludeFnops, boolean excludeSections);

    public XtaEditor(final String type) {
		if (DEBUG) System.out.printf("XtaEditor()\n");
		m_type = type;

		GuiEngine.addBroadcastListener(this);

    	setDocumentProvider(new XtaDocumentProvider());

    	m_selectedNodes = new ArrayList<Node>();
        
        m_endPointLineIndexes = new TreeMap<Long, InputLine>();
        m_excludedIndexes = new TreeSet<Long>();
        
        m_previousFnops = new TreeSet<XtaInstruction>();
    }

    public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("XtaEditor: createPartControl\n"); 
    	super.createPartControl(parent);
    	m_parent = parent;
    	
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
    	   	
        m_projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
        m_projectionSupport.install();

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
    	
    	TreeSet<Long> linePcs = getPcsForLine(lineNumber);
		for (Node node : getSelectedRouteUnknowns()) {
			if (linePcs.contains(node.getStartPc())) {
				nodes.add(node);
			}
		}
    	return nodes;
    }
	
	public void updateFoldingStructure(ArrayList<Position> positions, IDocument document) {
	    if (DEBUG) System.out.printf("XtaEditor: updateFoldingStructure\n"); 

	    Annotation[] annotations = new Annotation[positions.size()];
        HashMap<ProjectionAnnotation, Position> annotationPositions = 
    	    new HashMap<ProjectionAnnotation, Position>();

        for (int i = 0; i < positions.size();i++) {
            ProjectionAnnotation annotation = new ProjectionAnnotation();
            annotationPositions.put(annotation, positions.get(i));
            annotations[i] = annotation;
        }
       
        m_projectionAnnotationModel.modifyAnnotations(
        		m_previousProjectionAnnotations, annotationPositions, null);
        
        m_projectionAnnotationModel.collapseAll(0, document.getLength());
        m_previousProjectionAnnotations = annotations;       
    }

	public long getStartPointIndex() {
	    if (DEBUG) System.out.printf("XtaEditor: getStartPointIndex\n"); 

		if (m_startPointLine != null)
        	return m_startPointLine.getIndex();
		return -1;
	}

    public long getEndPointIndex() {
	    if (DEBUG) System.out.printf("XtaEditor: getEndPointIndex\n"); 
    	if (m_endPointLine != null) {
        	return m_endPointLine.getIndex();
        }
		return -1;
	}
	
	public boolean isValidEndPoint(long lineNumber) {
	    if (DEBUG) System.out.printf("XtaEditor: isValidEndPoint\n"); 
		if (m_endPointLineIndexes.containsKey(lineNumber)) {
			return true;
		}
		return false;
	}
	
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
    	if (DEBUG) System.out.printf("XtaEditor: createSourceViewer\n"); 

        ISourceViewer viewer = new ProjectionViewer(parent, ruler,
		    getOverviewRuler(), isOverviewRulerVisible(), styles);

        getSourceViewerDecorationSupport(viewer);
        return viewer;
    }
    
	protected IVerticalRulerColumn createLineNumberRulerColumn() {
    	if (DEBUG) System.out.printf("XtaEditor: createLineNumberRulerColumn\n"); 

		fLineNumberRulerColumn= new XtaLineNumberRulerColumn(getInput());
		initializeLineNumberRulerColumn(fLineNumberRulerColumn);
		return fLineNumberRulerColumn;
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
			
			if (m_endPointLineIndexes.containsKey(clickLineIndex) ||
				isAssembly(m_clickCodeRefString)) {
				if (isStartPointSet(m_clickCodeRefString)) {
				    addAction(menu, RemoveStartPointID);
				} else {
				    addAction(menu, SetStartPointID);				
				}
				if (isEndPointSet(m_clickCodeRefString)) {						
				    addAction(menu, RemoveEndPointID);
				} else {
				    addAction(menu, SetEndPointID);				
				}
				menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
			}
			
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
	
	private boolean isAssembly(String codeRefString) {
		if (codeRefString.length() < 3) {
			return false;
		}
		
		if (codeRefString.charAt(0) == '0' &&
			codeRefString.charAt(1) == 'x') {
			return true;
		}
		return false;
	}

	protected void updateStartPoint() {
		Reference startPoint = GuiEngine.getStartPoint();
		m_startPointLine = findCodeRefLineNum(startPoint);
	}

	protected void updateEndPoint() {
		Reference endPoint = GuiEngine.getEndPoint();
		m_endPointLine = findCodeRefLineNum(endPoint);
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
						false, false, getInput().getPcsToLines());
			}
        }
		updateAnnotations(annotations, annotationPositions);		
		
		if (hasHighlightedLines) {
			setTitleImage(Icons.getIcon("selected"));
		} else {
			setTitleImage(Icons.getIcon("xta16x16"));
		}
    }

	private void updateAnnotations(List<Annotation> annotations,
			HashMap<Annotation, Position> annotationPositions) {

		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>(annotations);
		final HashMap<Annotation, Position> allAnnotationPositions =
			new HashMap<Annotation, Position>(annotationPositions);
		
        long startPointIndex = getStartPointIndex();
        long endPointIndex = getEndPointIndex();

        m_endPointLineIndexes.clear();
        for (CodePointTableInfo info : GuiEngine.getEngine().getEndpointTable().values()) {
        	if (info.getFileName().equals(m_fileName)) {
        		InputLine line = findCodeLine(info.getLineNumber());
        		if (line != null) {
        			long index = line.getIndex();
	        		if (index != endPointIndex && index != startPointIndex)
	        			addAnnotation(index, EndpointID, allAnnotations, allAnnotationPositions);
	        		m_endPointLineIndexes.put(index, line);
        		}
        	}
        } 

        for (CodePointTableInfo info : GuiEngine.getEngine().getCallTable().values()) {
        	if (info.getFileName().equals(m_fileName)) {
        		InputLine line = findCodeLine(info.getLineNumber());
        		if (line != null) {
        			long index = line.getIndex();
	        		if (index != endPointIndex && index != startPointIndex)
	        			addAnnotation(index, EndpointID, allAnnotations, allAnnotationPositions);
	        		m_endPointLineIndexes.put(index, line);
        		}
        	}
        } 

        if ((startPointIndex != -1) && (startPointIndex == endPointIndex)) {
        	addAnnotation(startPointIndex, StartStopAnnotationID, allAnnotations, allAnnotationPositions);
        } else {
        	if (startPointIndex != -1)
            	addAnnotation(startPointIndex, StartAnnotationID, allAnnotations, allAnnotationPositions);
        	
        	if (endPointIndex != -1)
            	addAnnotation(endPointIndex, StopAnnotationID, allAnnotations, allAnnotationPositions);
        }

    	for (Long excludedIndex : m_excludedIndexes) {
    		addAnnotation(excludedIndex, ExcludedAnnotationID, allAnnotations, allAnnotationPositions);
		}
    	
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				m_annotationModel.replaceAnnotations(m_previousHighlightAnnotations, allAnnotationPositions);
			}});
		m_previousHighlightAnnotations = createArray(allAnnotations);
	}

	protected void updateExcluded() {
		m_excludedIndexes.clear();
		
		Collection<Reference> exclusions = GuiEngine.getEngine().getExclusions();
		if (exclusions.size() == 0) {
			return;
		}
		
		for (InputSection inputSection : getSections()) {
			if (m_type.equals(XtaDocumentPartitioner.SOURCE)) {
				checkLineForExclusion(exclusions, inputSection.getHeader().getCodeReference(),
						inputSection.getIndex());
			}
			for (InputLine line : inputSection.getLines()) {
				long linePc = line.getPc();
				for (Reference exclusion : exclusions) {
					if (linePc == exclusion.getPc()) {
						m_excludedIndexes.add(line.getIndex());
						m_excludedIndexes.add(inputSection.getIndex());
					}
				}
			}
    	}
	}

    private void checkLineForExclusion(Collection<Reference> exclusions, String refStr, long index) {
		Engine engine = GuiEngine.getEngine();
		Reference ref = ReferenceFactory.createReference(refStr, ReferenceClass.ANY_REF);
		if (ref.resolve(engine, engine.getNodeId(), engine.getCoreId())) {			
			for (Reference exclusion : exclusions) {
				if (ref.getPc() == exclusion.getPc()) {
					m_excludedIndexes.add(index);
				}				
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

	private TreeSet<Long> getPcsForLine(long lineNumber) {
		TreeSet<Long> pcs = new TreeSet<Long>();
		HashMap<Long, InputLine> lineMap = getInput().getPcsToLines();
		Iterator<Map.Entry<Long, InputLine>> lineIter = lineMap.entrySet().iterator();
		while (lineIter.hasNext()) {
    	    Map.Entry<Long, InputLine> pair = lineIter.next();
    	    if (pair.getValue().getIndex() == lineNumber) {
    	    	pcs.add(pair.getKey());
    	    }                           
    	}      

		HashMap<Long, InputSection> sectionMap = getInput().getPcsToSection();
		Iterator<Map.Entry<Long, InputSection>> sectionIter = sectionMap.entrySet().iterator();
		while (sectionIter.hasNext()) {
    	    Map.Entry<Long, InputSection> pair = sectionIter.next();
    	    if (pair.getValue().getIndex() == lineNumber) {
    	    	pcs.add(pair.getKey());
    	    }                           
    	}      
		return pcs;
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
					true, excludeSections, getInput().getPcsToLines());
		}

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				m_annotationModel.replaceAnnotations(m_previousUnknownAnnotations, annotationPositions);        
			}
		});
		m_previousUnknownAnnotations = createArray(annotations);
    }

    private boolean updateAnnotations(long pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions, boolean excludeFnops, boolean excludeSections,
    		HashMap<Long, InputLine> pcToLine) {
    	
    	boolean hitSection = false;
    	
    	InputLine line = pcToLine.get(pc);
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
		
		if (!excludeFnops){
			for (InputSection section : getSections()){
				for (InputLine fnop : section.getFnops()){
					if (fnop.getPc() == pc) { 
						addAnnotation(fnop.getIndex(), type, annotations, annotationPositions);
					}
				}
			}
		}
		return hitSection;
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

	private String getLineDescription(long clickLineIndex) {
		if (m_endPointLineIndexes.containsKey(clickLineIndex)) {
			InputLine line = m_endPointLineIndexes.get(clickLineIndex);
			return line.getCodeReference();
		}
		
		String srcLabel = getSrcLabel(clickLineIndex);
		if (srcLabel != null)
			return srcLabel;
		
		return getLineReference(clickLineIndex, false);
	}

	private String getSrcLabel(long clickLineIndex) {
        Map<Long, SrcLabelTableInfo> srcLabelTable = GuiEngine.getEngine().getSrcLabelTable();
		for (SrcLabelTableInfo info : srcLabelTable.values()) {
        	if (info.getFileName().equals(m_fileName)) {
        		InputLine startLine = findCodeLine(info.getLineNumberStart());
        		InputLine endLine = findCodeLine(info.getLineNumberEnd());
        		if ((startLine != null) && (endLine != null)) {
        			
        			if (clickLineIndex >= startLine.getIndex() && clickLineIndex <= endLine.getIndex()) {
        				String lineRef = getLineReference(clickLineIndex, true);
        				if (lineRef != null)
            				return lineRef;
            			else
        					return "0x" + Long.toHexString(info.getPc());
        			}
        		}
        	}
        } 
		return null;
	}

    private String getLineReference(long index, boolean findHeader) {
    	for (InputSection inputSection : getSections()) {
    		long startLine = inputSection.getIndex();
    		long endLine = startLine + inputSection.getLines().size();
    		long offset = index - startLine;

    		if (findHeader) {
        		if (index == startLine) {
    				return inputSection.getHeader().getCodeReference();
    			}
    		} else {
	    		// Don't map to the header
	    		if ((index > startLine) && (index <= endLine)) {
	    			if (!inputSection.isFnop(offset - 1)) {
	    				return inputSection.getLine(offset - 1).getCodeReference();
	    			} else {
	    				return null;
	    			}
				}
    		}
    	}
		return null;
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

	private InputLine findCodeRefLineNum(Reference codeRef) {
		if (codeRef != null) {
			if (codeRef.getResolved()) {
			    long pc = codeRef.getPc();
	    	    if (m_type.equals(XtaDocumentPartitioner.DISASSEMBLY)) {
	    		    return findPcLine(pc);
	    	    } else {
	    	    	if (m_fileName.equals(codeRef.getFileName()))
	    	    		return findCodeLine(codeRef.getLineNum());
	    	    	else
	    	    		return findPcLine(pc);
	    	    }
			}			
		}
		return null;
	}

	private InputLine findPcLine(long pc) {
		for (InputSection inputSection : getSections()) {
			for (InputLine line : inputSection.getLines()) {
				if ((line.getPc() == pc) && !inputSection.isFnop(line.getOffset())) {	
					return line;
				}
			}
    	}
		return null;
	}

	private InputLine findCodeLine(long lineNum) {
		for (InputSection inputSection : getSections()) {
			InputLine header = inputSection.getHeader();
			if (header.getPc() == lineNum) {
				return header;
			}
    	}
		return null;
	}

	IDocument getDocument() {
		ISourceViewer viewer = getSourceViewer();
		if (viewer == null)
			return null;
		
		return viewer.getDocument();
	}

	protected Input getInput() {
		IDocument document = getDocument();
		if (document == null) {
			return null;
		}
		
		XtaDocumentPartitioner xtaDocumentPartitioner =
			(XtaDocumentPartitioner)document.getDocumentPartitioner();

		return xtaDocumentPartitioner.getInput();		
	}
	
	protected ArrayList<InputSection> getSections() {
		IDocument document = getDocument();
		if (document == null)
			return new ArrayList<InputSection>();
		
		XtaDocumentPartitioner xtaDocumentPartitioner =
			(XtaDocumentPartitioner)document.getDocumentPartitioner();

		return xtaDocumentPartitioner.getInput().getSections();
	}
	
	private boolean isStartPointSet(String codeReference) {
		if (m_startPointLine != null) {
			if (m_startPointLine.getCodeReference().equals(codeReference)) {
				return true;
			}
		}
	    return false;	
	}

	private boolean isEndPointSet(String codeReference) {
		if (m_endPointLine != null) {
			if (m_endPointLine.getCodeReference().equals(codeReference)) {
				return true;
			}
		}
	    return false;	
	}

	public boolean isDirty() {
		// Keep the editor pages from showing a '*' when FNOPs are inserted and removed
		return false;
	}

	public void setNames(String partName, String fileName) {
		setPartName(partName);
		m_fileName = fileName;
	}
}
