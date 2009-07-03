package com.xmos.xta.gui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.engine.sections.CodePointTableInfo;
import com.xmos.xta.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.dialogs.InstructionTime;
import com.xmos.xta.gui.views.control.ControlView;

public class XtaEditor extends TextEditor implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final boolean DEBUG = false;
    
    public static final String ID                    = "com.xmos.xta.gui.editors.XtaEditor.Test";
    public static final String HighlightAnnotationID = "com.xmos.xta.gui.highlight";
    public static final String UnknownAnnotationID   = "com.xmos.xta.gui.unknown";
    public static final String StartAnnotationID     = "com.xmos.xta.gui.start";
    public static final String StopAnnotationID      = "com.xmos.xta.gui.stop";
    public static final String StartStopAnnotationID = "com.xmos.xta.gui.startstop";
    public static final String ExcludedAnnotationID  = "com.xmos.xta.gui.excluded";
    public static final String EndpointID            = "com.xmos.xta.gui.endpoint";
    public static final String ExpandAllID           = "com.xmos.xta.gui.action.ExpandAll";
    public static final String CollapseAllID         = "com.xmos.xta.gui.action.CollapseAll";
    public static final String ClearRouteMaskingID   = "com.xmos.xta.gui.action.ClearRouteMasking";
    public static final String RestoreRouteMaskingID = "com.xmos.xta.gui.action.RestoreRouteMasking";
    public static final String ShowStartPointID      = "com.xmos.xta.gui.action.ShowStartPoint";
    public static final String ShowEndPointID        = "com.xmos.xta.gui.action.ShowEndPoint";
    public static final String SetStartPointID       = "com.xmos.xta.gui.action.SetStartPoint";
    public static final String SetEndPointID         = "com.xmos.xta.gui.action.SetEndPoint";
    public static final String RemoveStartPointID    = "com.xmos.xta.gui.action.RemoveStartPoint";
    public static final String RemoveEndPointID      = "com.xmos.xta.gui.action.RemoveEndPoint";
    public static final String SetInstructionTimeID  = "com.xmos.xta.gui.action.SetInstructionTime";
    public static final String ConstrainRequireID    = "com.xmos.xta.gui.action.ConstrainRequire";
    public static final String ConstrainExcludeID    = "com.xmos.xta.gui.action.ConstrainExclude";
    public static final String SelectPathsID         = "com.xmos.xta.gui.action.ShowPaths";

    private static final String fnopString = "    ------- \tFNOP\n";
    
    private Composite m_parent;
    private Action m_showStartPointAction;
    private Action m_showEndPointAction;
    private Action m_setInstructionTimeAction;
    private Action m_clearRouteMaskingAction;
    private Action m_restoreRouteMaskingAction;
    private String m_clickCodeRefString;
    private Reference m_clickCodeRef;
    private ProjectionSupport m_projectionSupport;    
    private ProjectionAnnotationModel m_projectionAnnotationModel;
    private IAnnotationModelExtension m_annotationModel;
    private AnnotationPainter m_painter;
    private Annotation[] m_previousProjectionAnnotations;
    private Annotation[] m_previousHighlightAnnotations;
    private Annotation[] m_previousUnknownAnnotations;
    private boolean m_hasHighlightedLines;
    private Node m_selectedNode;    
    private final String m_type;
    private final String m_fileName;
    private InputLine m_startPointLine;
    private InputLine m_endPointLine;
    private Map<Integer,InputLine> m_endPointLineIndexes;
    private HashSet<Integer> m_excludedIndexes;
    private boolean m_routeMaskEnabled;
	private HashSet<XtaInstruction> m_previousFnops;

    public XtaEditor(final String type, String fileName) {
		if (DEBUG) System.out.printf("XtaEditor()\n");
		m_type = type;
		m_fileName = fileName;

		GuiEngine.addBroadcastListener(this);

    	setSourceViewerConfiguration(new XtaConfiguration(this));
    	setDocumentProvider(new XtaDocumentProvider());

    	m_selectedNode = null;
        m_hasHighlightedLines = false;
        
        m_endPointLineIndexes = new TreeMap<Integer,InputLine>();
        m_excludedIndexes = new HashSet<Integer>();
        
        m_previousFnops = new HashSet<XtaInstruction>();
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
        
        getSite().getPage().addSelectionListener(this);
        
        m_painter = new AnnotationPainter(viewer, getAnnotationAccess());        
        m_painter.addAnnotationType(HighlightAnnotationID);        
    }
    
    public void dispose() {
		if (DEBUG) System.out.printf("XtaEditor: dispose\n");	
    	
		GuiEngine.removeBroadcastListener(this);
		getSite().getPage().removeSelectionListener(this);
		super.dispose();
    }
    
	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("XtaEditor: propertyChange: %s\n", event.getProperty());	

		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			updateInputLinesGreyStatus();
			updatePartitions();

		} else if (event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			clearInputLinesGreyStatus();
			updatePartitions();

		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			m_selectedNode = null;
			updateFnops();
			updateExcluded();
			updateUnknowns();
			updateHighlighting();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE)) {
			m_selectedNode = null;
			updateFnops();
			updateExcluded();
			updateUnknowns();
			updateHighlighting();
			updateInputLinesGreyStatus();
			updatePartitions();
	        updateStartPoint();
	        updateEndPoint();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_START_POINT) ||
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_START_POINT)) {
			updateStartPoint();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT) ||
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_END_POINT)) {
			updateEndPoint();

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SHOW_CODE_POINT)) {
			Reference codePoint = GuiEngine.getCodePoint();
			showCodePoint(codePoint);
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			updateUnknowns();

		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUSIONS)) {
			updateExcluded();
						
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
	        updateStartPoint();
	        updateEndPoint();
		}
	}

	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.printf("XtaEditor: selectionChanged\n"); 
		if (sel instanceof IStructuredSelection) {
			if (((IStructuredSelection) sel).size() != 1)
				return;
			
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
            	if (m_selectedNode != (Node)object) {
            		m_selectedNode = (Node)object;
            		updateFnops();
            		updateExcluded();
            		updateUnknowns();
            		updateHighlighting();
            	}
            }
        }
    }

    public HashSet<Node> getUnknownsForLine(int lineNumber) {
	    if (DEBUG) System.out.printf("XtaEditor: getUnknownsForLine\n"); 

    	HashSet<Node> nodes = new HashSet<Node>();
    	
    	HashSet<Integer> linePcs = getPcsForLine(lineNumber);
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

	public int getStartPointIndex() {
	    if (DEBUG) System.out.printf("XtaEditor: getStartPointIndex\n"); 

		if (m_startPointLine != null)
        	return m_startPointLine.getIndex();
		return -1;
	}

    public int getEndPointIndex() {
	    if (DEBUG) System.out.printf("XtaEditor: getEndPointIndex\n"); 
    	if (m_endPointLine != null) {
        	return m_endPointLine.getIndex();
        }
		return -1;
	}
	
	public boolean isValidEndPoint(int lineNumber) {
	    if (DEBUG) System.out.printf("XtaEditor: isValidEndPoint\n"); 
		if (m_endPointLineIndexes.containsKey(lineNumber)) {
			return true;
		}
		return false;
	}
	
	public boolean hasHighlightedLines() {
	    if (DEBUG) System.out.printf("XtaEditor: hasHighlightedLines\n"); 
		return m_hasHighlightedLines;
	}
	
	protected ISourceViewer createSourceViewer(Composite parent,
    		IVerticalRuler ruler, int styles) {
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
     	
     	setAction(ExpandAllID, new Action("Expand All") {
     		public void run() {
     			IDocument document = getSourceViewer().getDocument();
     			m_projectionAnnotationModel.expandAll(0, document.getLength());
     		}
     	});
     	
     	setAction(CollapseAllID, new Action("Collapse All") {
     		public void run() {
     			IDocument document = getSourceViewer().getDocument();
     			m_projectionAnnotationModel.collapseAll(0, document.getLength());
     		}
     	});

     	m_clearRouteMaskingAction =  new Action("Clear route masking") {
     		public void run() {
     			clearInputLinesGreyStatus();
     			updatePartitions();
     		}
     	};
     	setAction(ClearRouteMaskingID, m_clearRouteMaskingAction);

     	m_restoreRouteMaskingAction = new Action("Restore route masking") {
     		public void run() {
     			updateInputLinesGreyStatus();
     			updatePartitions();
     		}
     	};
     	setAction(RestoreRouteMaskingID, m_restoreRouteMaskingAction);

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

     	setAction(SetStartPointID, new Action("Set start point") {
     		public void run() {
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SET_START_POINT, m_clickCodeRefString);
     		}
     	});
     	
     	setAction(SetEndPointID, new Action("Set end point") {
     		public void run() {
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SET_END_POINT, m_clickCodeRefString);
     		}
     	});
     	
     	setAction(RemoveStartPointID, new Action("Remove start point") {
     		public void run() {
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SET_START_POINT, "");     			
     		}
     	});
     	
     	setAction(RemoveEndPointID, new Action("Remove end point") {
     		public void run() {
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SET_END_POINT, "");
     		}
     	});
     	
     	setAction(ConstrainExcludeID, new Action("Exclude this") {
     		public void run() {
     			addToExclusion("!" + m_clickCodeRef.getRef());
     		}
     	});

     	setAction(SelectPathsID, new Action("Select nodes through here") {
     		public void run() {
     			Reference clickCodeRef = ReferenceFactory.createReference(m_clickCodeRefString, ReferenceClass.ANY_REF);
     			GuiEngine.setCodePoint(clickCodeRef);
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SELECT_NODES, "");
     		}
     	});

     	m_setInstructionTimeAction = new Action("Set instruction time") {
     		public void run() {
     			new InstructionTime(m_parent.getShell(), m_clickCodeRef);
     		}
     	};
     	setAction(SetInstructionTimeID, m_setInstructionTimeAction);
    }    

    protected void addToExclusion(String refString) {  	
		if (DEBUG) System.out.printf("XtaEditor: addToExclusion\n"); 

    	GuiEngine.getEngine().addExclusion(refString);		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			getActivePage().showView(ControlView.ID);
		} catch (PartInitException e) {}	
	}

	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		if (DEBUG) System.out.printf("XtaEditor: editorContextMenuAboutToShow\n"); 
     	super.editorContextMenuAboutToShow(menu);

     	menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));     	
    	addAction(menu, ExpandAllID);
    	addAction(menu, CollapseAllID);
    	addAction(menu, ClearRouteMaskingID);
    	addAction(menu, RestoreRouteMaskingID);
    	m_clearRouteMaskingAction.setEnabled(m_routeMaskEnabled);
    	m_restoreRouteMaskingAction.setEnabled(!m_routeMaskEnabled);
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
		
		int clickLineIndex = getVerticalRuler().getLineOfLastMouseButtonActivity();
		
		m_clickCodeRefString = getLineDescription(clickLineIndex);				
		if (m_clickCodeRefString != null) {			
			if (m_endPointLineIndexes.containsKey(clickLineIndex) ||
				m_type.equals(XtaDocumentPartitioner.DISASSEMBLY)) {
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
			addAction(menu, SetInstructionTimeID);
			addAction(menu, ConstrainExcludeID);
			addAction(menu, ConstrainRequireID);
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
	
	private void updateStartPoint() {
		Reference startPoint = GuiEngine.getStartPoint();
		m_startPointLine = findCodeRefLineNum(startPoint);
	}

	private void updateEndPoint() {
		Reference endPoint = GuiEngine.getEndPoint();
		m_endPointLine = findCodeRefLineNum(endPoint);
	}
	    	
    private void updatePartitions() {
    	IDocument document = getDocument();
    	if (document != null) {
    		XtaDocumentPartitioner xtaDocumentPartitioner = (XtaDocumentPartitioner)document.getDocumentPartitioner();
    		xtaDocumentPartitioner.partitionDocument(document, false);
    		getSourceViewer().invalidateTextPresentation();
    	}
	}

	private void updateInputLinesGreyStatus() {
		Input input = getInput();
    	if (input == null)
    		return;
    	
		Route route = GuiEngine.getEngine().getSelectedRoute();
		if (route != null) {
			m_routeMaskEnabled = true;
    		for (InputSection inputSection : input.getSections()){
    			inputSection.setGrey(true);
				for(InputLine inputLine: inputSection.getLines()){
					inputLine.setGrey(true);
				}
			}
    		
	    	Set<Integer> pcsCovered = route.getPcsHit();
	    	
	    	boolean foundFirstLine = false;
	    	ArrayList<InputSection> noDebug = new ArrayList<InputSection>();
	    	
			for (InputSection inputSection : input.getSections()){
				boolean hit = false;
				for (InputLine inputLine: inputSection.getLines()){
					if (pcsCovered.contains(inputLine.getPc())){
						foundFirstLine = true;
						inputLine.setGrey(false);
						hit = true;
					} 	
				}
				
				if (foundFirstLine && inputSection.getLines().size() == 0){
					noDebug.add(inputSection);
				}
				
				if (inputSection.getLines().size() > 0 && hit == false){
					noDebug.clear();
				}
				if (hit == true){
					inputSection.setGrey(false);
					for (InputSection i : noDebug){
						i.setGrey(false);
					}
				}
			}
    	} else {
    		clearInputLinesGreyStatus();
    	}
	}

	private void clearInputLinesGreyStatus() {
		Input input = getInput();
    	if (input == null)
    		return;
		
		m_routeMaskEnabled = false;
		for (InputSection inputSection : input.getSections()){
			inputSection.setGrey(false);
			for (InputLine inputLine: inputSection.getLines()){
				inputLine.setGrey(false);
			}
		}		
	}
	
    private void updateFnops() {
		if (DEBUG) System.out.printf("XtaEditor: updateFnops\n"); 
		
    	HashSet<XtaInstruction> toAdd = new HashSet<XtaInstruction>();
    	HashSet<XtaInstruction> toRemove = new HashSet<XtaInstruction>();
    	HashSet<XtaInstruction> toKeep  = new HashSet<XtaInstruction>();
    	
   		final Collection<XtaInstruction> fnops = new Vector<XtaInstruction>();
    	if (m_selectedNode != null) {
       		m_selectedNode.traverse(0, new DefaultNodeTraverser() {
       		    public boolean foundInstructionNode(Node node, int depth) {
       		    	XtaInstruction instruction = node.getInstructions().getFirst();
       		    	if (instruction.isFnop()) {   		    		
       	   		    	fnops.add(instruction);
       		    	}
       		    	return true;
       		    }   			
       		});    		
    	}
   		
   		// Makes a list of all the fnops to add.
   		for (XtaInstruction inst: fnops){
   			if (m_previousFnops.contains(inst)){
   				toKeep.add(inst);
   			} else {
   				toAdd.add(inst);
   			}
   		}
    
    	// Makes a list of old fnops to remove
    	toRemove = m_previousFnops;
    	toRemove.removeAll(toKeep);
    	
    	// Removes the old fnops
    	for (XtaInstruction inst : toRemove) {
    		removeFnop(inst.getPc());
		}

    	// Adds the new fnops
    	for (XtaInstruction inst : toAdd) {
			addFnop(inst.getPc());
		}
    	
    	m_previousFnops = toKeep;
    	m_previousFnops.addAll(toAdd);
    	
	    if (m_type.equals(XtaDocumentPartitioner.DISASSEMBLY)) {
		    ((InputDisassembly)getEditorInput()).updateLineNumberMapping();
	    } else {
		    ((InputSource)getEditorInput()).updateLineNumberMapping();	    	
	    }
	}
        
    private void removeFnop(int pc) {
		int fnopsRemoved = 0;
		IDocument document = getDocument();
    	for (InputSection inputSection : getSections()) {
    		inputSection.setIndex(inputSection.getIndex() - fnopsRemoved);
    		
    		InputLine fnopLine = inputSection.removeFnop(pc);
    		while (fnopLine != null) {
    			fnopsRemoved++;
    			int index = fnopLine.getIndex();
				try {
					IRegion lineInfo = document.getLineInformation(index);
		            document.replace(lineInfo.getOffset(), fnopString.length(), "");
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
	    		fnopLine = inputSection.removeFnop(pc);
			}
    	}	
	}
    
    private void addFnop(int pc) {
    	boolean fnopsAdded = false;

    	IDocument document = getDocument();    	
    	for (InputSection inputSection : getSections()) {
    		if (fnopsAdded) {
    			inputSection.incrementIndex();
    		} else {
	    		int lineNum = 0;
	    		while (!fnopsAdded && (lineNum < inputSection.getNumLines())) {
	    			InputLine line = inputSection.getLine(lineNum);
	    			int index = line.getIndex();
					if (line.getPc() == pc) {
						try {
							IRegion lineInfo = document.getLineInformation(index);
							
				            document.replace(lineInfo.getOffset(), 0, fnopString);
							inputSection.addFnop(fnopString, pc, index, getInput());
							fnopsAdded = true;
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					} else {
						lineNum++;
					}
				}
    		}
		}
	}
    
    private void updateHighlighting() {
		if (DEBUG) System.out.printf("XtaEditor: updateHighlighting\n");
		
	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        HashMap<Annotation, Position> annotationPositions = new HashMap<Annotation, Position>();		
		
        if (m_selectedNode != null) {
        	Set<Integer> pcsCovered = m_selectedNode.getPCs();
        	for (Integer pc : pcsCovered) {
				updateAnnotations(pc, HighlightAnnotationID, annotations, annotationPositions, false, getInput().getPcsToLines());
			}
        }
        m_hasHighlightedLines = (annotations.size() > 0);

		updateAnnotations(annotations, annotationPositions);		
		GuiEngine.broadcast(Broadcaster.BROADCAST_HIGHLIGHTS_UPDATED, "");	
    }

	private void updateAnnotations(List<Annotation> annotations,
			HashMap<Annotation, Position> annotationPositions) {

		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>(annotations);
		final HashMap<Annotation, Position> allAnnotationPositions =
			new HashMap<Annotation, Position>(annotationPositions);
		
        int startPointIndex = getStartPointIndex();
        int endPointIndex = getEndPointIndex();

        m_endPointLineIndexes.clear();
        for (CodePointTableInfo info : GuiEngine.getEngine().getEndpointTable().values()) {
        	if (info.getFileName().equals(m_fileName)) {
        		InputLine line = findCodeLine(info.getLineNumber());
        		if (line != null) {
        			int index = line.getIndex();
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

    	for (Integer excludedIndex : m_excludedIndexes) {
    		addAnnotation(excludedIndex, ExcludedAnnotationID, allAnnotations, allAnnotationPositions);
		}
    	
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				m_annotationModel.replaceAnnotations(m_previousHighlightAnnotations, allAnnotationPositions);
			}});
		m_previousHighlightAnnotations = createArray(allAnnotations);
	}

	private void updateExcluded() {
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
				if (checkLineForExclusion(exclusions, line.getCodeReference(), line.getIndex())) {
					m_excludedIndexes.add(inputSection.getIndex());
				}
			}
    	}
	}

    private boolean checkLineForExclusion(Collection<Reference> exclusions, String refStr, int index) {
		Engine engine = GuiEngine.getEngine();
		Reference ref = ReferenceFactory.createReference(refStr, ReferenceClass.ANY_REF);
		if (ref.resolve(engine, engine.getNodeId(),engine.getCoreId())) {			
			for (Reference exclusion : exclusions) {
				if (ref.getPc() == exclusion.getPc()) {
					return true;
				}				
			}
		}
		return false;
	}

	private Collection<Node> getSelectedRouteUnknowns() {
		Engine engine = GuiEngine.getEngine();
		Route route = engine.getSelectedRoute();
		if (route != null) {
            return route.getUnknowns(UnknownState.UNKNOWN);
		} else {
			return new HashSet<Node>();
		}
	}

	private HashSet<Integer> getPcsForLine(int lineNumber) {
		HashSet<Integer> pcs = new HashSet<Integer>();
		HashMap<Integer, InputLine> lineMap = getInput().getPcsToLines();
		Iterator<Map.Entry<Integer, InputLine>> lineIter = lineMap.entrySet().iterator();
		while (lineIter.hasNext()) {
    	    Map.Entry<Integer, InputLine> pair = lineIter.next();
    	    if (pair.getValue().getIndex() == lineNumber) {
    	    	pcs.add(pair.getKey());
    	    }                           
    	}      

		HashMap<Integer, InputSection> sectionMap = getInput().getPcsToSection();
		Iterator<Map.Entry<Integer, InputSection>> sectionIter = sectionMap.entrySet().iterator();
		while (sectionIter.hasNext()) {
    	    Map.Entry<Integer, InputSection> pair = sectionIter.next();
    	    if (pair.getValue().getIndex() == lineNumber) {
    	    	pcs.add(pair.getKey());
    	    }                           
    	}      
		return pcs;
	}

	private void updateUnknowns() {
		if (DEBUG) System.out.printf("XtaEditor: updateUnknowns\n"); 

	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        final HashMap<Annotation, Position> annotationPositions = 
    	    new HashMap<Annotation, Position>();		

		HashSet<Integer> unknownsSet = new HashSet<Integer>();
		for (Node node : getSelectedRouteUnknowns()) {
			unknownsSet.add(node.getStartPc());
		}
		for (int pc : unknownsSet) {
			updateAnnotations(pc, UnknownAnnotationID, annotations, annotationPositions, true, getInput().getPcsToLines());
		}

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				m_annotationModel.replaceAnnotations(m_previousUnknownAnnotations, annotationPositions);        
			}
		});
		m_previousUnknownAnnotations = createArray(annotations);
    }

    private void updateAnnotations(int pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions, boolean excludeFnops, HashMap<Integer, InputLine> pcToLine) {
    	
    	InputLine line = pcToLine.get(pc);
    	if (line == null) return;
		InputSection inputSection = getInput().getPcsToSection().get(pc);
		if (inputSection != null) {
			addAnnotation(inputSection.getIndex(), type, annotations, annotationPositions);
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
    }

    private void addAnnotation(int index, String type, List<Annotation> annotations,
    		Map<Annotation, Position> annotationPositions) {

		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null)
			return;
		
    	IDocument document = sourceViewer.getDocument();
        try {
    		IRegion lineInfo = document.getLineInformation(index);
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

	private String getLineDescription(int clickLineIndex) {
		if (m_endPointLineIndexes.containsKey(clickLineIndex)) {
			InputLine line = m_endPointLineIndexes.get(clickLineIndex);
			return line.getCodeReference();
		}
		
		String srcLabel = getSrcLabel(clickLineIndex);
		if (srcLabel != null)
			return srcLabel;
		
		return getLineReference(clickLineIndex, false);
	}

	private String getSrcLabel(int clickLineIndex) {
        Map<Integer, SrcLabelTableInfo> srcLabelTable = GuiEngine.getEngine().getSrcLabelTable();
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
        					return "0x" + Integer.toHexString(info.getPc());
        			}
        		}
        	}
        } 
		return null;
	}

    private String getLineReference(int index, boolean findHeader) {
    	for (InputSection inputSection : getSections()) {
    		int startLine = inputSection.getIndex();
    		int endLine = startLine + inputSection.getLines().size();
    		int offset = index - startLine;

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

	private void showCodePoint(Reference codePoint) {
    	if ((codePoint == null) || !codePoint.getResolved() || m_type.equals(XtaDocumentPartitioner.SOURCE)) {
    		return;
    	}
    	
		ISourceViewer viewer = getSourceViewer();
		if (viewer == null)
			return;

		IDocument document = getDocument();
		for (InputSection inputSection : getSections()) {
			for (InputLine line : inputSection.getLines()) {
				if ((line.getPc() == codePoint.getPc()) && !inputSection.isFnop(line.getOffset())) {
					try {
						int charOffset = document.getLineOffset(line.getIndex());
						m_projectionAnnotationModel.expandAll(charOffset, 1);
						
						int topIndex = Math.max(0, line.getIndex() - 10);
						viewer.setTopIndex(topIndex);
						viewer.setRangeIndication(charOffset, 0, true);
						viewer.removeRangeIndication();
						
					} catch (BadLocationException e) {}
					return;
				}
			}
    	}
	}

	private InputLine findCodeRefLineNum(Reference codeRef) {
		if (codeRef != null) {
			if (codeRef.getResolved()) {
			    int pc = codeRef.getPc();
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

	private InputLine findPcLine(int pc) {
		for (InputSection inputSection : getSections()) {
			for (InputLine line : inputSection.getLines()) {
				if ((line.getPc() == pc) && !inputSection.isFnop(line.getOffset())) {	
					return line;
				}
			}
    	}
		return null;
	}

	private InputLine findCodeLine(int lineNum) {
		for (InputSection inputSection : getSections()) {
			InputLine header = inputSection.getHeader();
			if (header.getPc() == lineNum) {
				return header;
			}
    	}
		return null;
	}

	private IDocument getDocument() {
		ISourceViewer viewer = getSourceViewer();
		if (viewer == null)
			return null;
		
		return viewer.getDocument();
	}

	private Input getInput() {
		IDocument document = getDocument();
		if (document == null)
			return null;
		
		XtaDocumentPartitioner xtaDocumentPartitioner =
			(XtaDocumentPartitioner)document.getDocumentPartitioner();

		return xtaDocumentPartitioner.getInput();		
	}
	
	private ArrayList<InputSection> getSections() {
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
}
