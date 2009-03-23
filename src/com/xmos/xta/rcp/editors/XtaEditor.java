package com.xmos.xta.rcp.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.constraint.ConstraintFactory;
import com.xmos.xta.engine.maps.SrcTableInfo;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.dialogs.InstructionTime;

import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.views.ControlView.ControlView;

public class XtaEditor extends TextEditor implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final boolean DEBUG = false;
    
    private Composite m_parent;

    public static final String ID                    = "com.xmos.xtagui.editors.XtaEditor.Test";
    public static final String HighlightAnnotationID = "com.xmos.xta.rcp.highlight";
    public static final String UnknownAnnotationID   = "com.xmos.xta.rcp.unknown";
    public static final String StartAnnotationID     = "com.xmos.xta.rcp.start";
    public static final String StopAnnotationID      = "com.xmos.xta.rcp.stop";
    public static final String StartStopAnnotationID = "com.xmos.xta.rcp.startstop";
    public static final String EndpointID            = "com.xmos.xta.rcp.endpoint";
    public static final String ExpandAllID           = "com.xmos.xtagui.action.ExpandAll";
    public static final String CollapseAllID         = "com.xmos.xtagui.action.CollapseAll";
    public static final String RestoreAllHighlighID  = "com.xmos.xtagui.action.RestoreAllHighligh";

    public static final String ShowStartPointID      = "com.xmos.xtagui.action.ShowStartPoint";
    public static final String ShowEndPointID        = "com.xmos.xtagui.action.ShowEndPoint";
    public static final String SetStartPointID       = "com.xmos.xtagui.action.SetStartPoint";
    public static final String SetEndPointID         = "com.xmos.xtagui.action.SetEndPoint";
    public static final String RemoveStartPointID    = "com.xmos.xtagui.action.RemoveStartPoint";
    public static final String RemoveEndPointID      = "com.xmos.xtagui.action.RemoveEndPoint";
    
    public static final String SetInstructionTimeID  = "com.xmos.xtagui.action.SetInstructionTime";
    public static final String ConstrainRequireID    = "com.xmos.xtagui.action.ConstrainRequire";
    public static final String ConstrainExcludeID    = "com.xmos.xtagui.action.ConstrainExclude";
    
    Action m_showStartPointAction;
    Action m_showEndPointAction;
    Action m_setInstructionTimeAction;
    String m_clickCodeRefString;
    Reference m_clickCodeRef;

    private static final String fnopString = "    ------- \tFNOP\n";
    
    private ProjectionSupport m_projectionSupport;    
    private ProjectionAnnotationModel m_projectionAnnotationModel;
    private IAnnotationModelExtension m_annotationModel;
    private AnnotationPainter m_painter;

    private Annotation[] m_previousProjectionAnnotations;
    private Annotation[] m_previousHighlightAnnotations;
    private Annotation[] m_previousUnknownAnnotations;
    private boolean m_hasHighlightedLines;
    private boolean m_updatingHighlights;
    
    private Node m_currentPathRoot;
    private Node m_selectedNode;
    
	private ISelection m_mostRecent;
    private final String m_type;
    private final String m_fileName;
    private InputLine m_startPointLine;
    private InputLine m_endPointLine;
    private Map<Integer,InputLine> m_endPointLines;
    
    public XtaEditor(final String type, String fileName) {
		if (DEBUG) System.out.printf("XtaEditor()\n");
		m_type = type;
		m_fileName = fileName;

		XtaNotifier.getInstance().addPropertyChangeListener(this);

    	setSourceViewerConfiguration(new XtaConfiguration(this));
    	setDocumentProvider(new XtaDocumentProvider());

    	m_currentPathRoot = null;
    	m_selectedNode = null;
    	m_startPointLine = null;
        m_endPointLine = null;
        m_hasHighlightedLines = false;
        m_updatingHighlights = false;
        
        m_endPointLines = new TreeMap<Integer,InputLine>();
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
        
	    updateHighlighting();
    }
    
    public void dispose() {
		if (DEBUG) System.out.printf("XtaEditor: dispose\n");	
    	
		XtaNotifier.getInstance().removePropertyChangeListener(this);
		getSite().getPage().removeSelectionListener(this);
		super.dispose();
    }
    
	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("XtaEditor: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {

			updateInputLines(false);
			updatePartitions();
			updateEditor();
		} else if (event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			resetGrey();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			m_selectedNode = null;
	    	m_startPointLine = null;
	        m_endPointLine = null;			
			updateEditor();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE)) {
			updateInputLines(false);
			updatePartitions();
    	    
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_START_POINT)) {
			Reference startPoint = XtaNotifier.getInstance().getStartPoint();
			m_startPointLine = findCodeRefLineNum(startPoint);
    	    updateHighlighting();
    	    
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT)) {
			Reference endPoint = XtaNotifier.getInstance().getEndPoint();
			m_endPointLine = findCodeRefLineNum(endPoint);
    	    updateHighlighting();
    	    
		} else if (event.getProperty().equals(Notifier.NOTIFY_SHOW_CODE_POINT)) {
			Reference codePoint = XtaNotifier.getInstance().getCodePoint();
			showCodePoint(codePoint);
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			updateUnknowns();
		}
	}

	public void resetGrey() {
		m_selectedNode = null;
		m_startPointLine = null;
		m_endPointLine = null;			
		updateInputLines(true);
		updatePartitions();
		updateEditor();
	}


	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.printf("XtaEditor: selectionChanged\n"); 
		if (sel != m_mostRecent  && sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
            	m_mostRecent = sel;
            	m_selectedNode = (Node)object;
            	updateEditor();
            }
        }
    }

	private void updateEditor() {
		updateFnops();
		updateHighlighting();
		updateUnknowns();
	}	            		         	        
    	
    private void updatePartitions() {
    	XtaDocumentPartitioner xtaDocumentPartitioner = (XtaDocumentPartitioner)getDocument().getDocumentPartitioner();
		xtaDocumentPartitioner.partitionDocument(this.getDocument(), false);
		getSourceViewer().invalidateTextPresentation();
	}

	private void updateInputLines(boolean reset) {
		m_selectedNode = null;
	    	updateEditor();
		Route route = XtaNotifier.getInstance().getRouteInfo().getRoute();
    	if(route != null && !reset){
	    	
    		for(InputSection inputSection :getInput().getSections()){
    			inputSection.setGrey(true);
				for(InputLine inputLine: inputSection.getLines()){
					inputLine.setGrey(true);
				}
			}
    		
	    	HashSet<Integer> pcsCovered = route.getPcsHit();
	    	
	    	
	    	boolean foundFirstLine = false;
	    	ArrayList<InputSection> noDebug = new ArrayList<InputSection>();
	    	
			for(InputSection inputSection :getInput().getSections()){
				boolean hit = false;
				for(InputLine inputLine: inputSection.getLines()){
					if(pcsCovered.contains(inputLine.getPc())){
						foundFirstLine = true;
						inputLine.setGrey(false);
						hit = true;
					} 	
				}
				
				if(foundFirstLine && inputSection.getLines().size() == 0){
					noDebug.add(inputSection);
				}
				
				if(inputSection.getLines().size() > 0 && hit == false){
					noDebug.clear();
				}
				if(hit == true){
					inputSection.setGrey(false);
					for(InputSection i : noDebug){
						i.setGrey(false);
					}
				}
				/*
				if(route.getFunction()!= null){
					if(inputSection.getHeader().getLine().contains(route.getFunction())){
						inputSection.setGrey(false);
					}
				}
				*/
			}
    	} else {
    		for(InputSection inputSection :getInput().getSections()){
    			inputSection.setGrey(false);
				for(InputLine inputLine: inputSection.getLines()){
					inputLine.setGrey(false);
				}
			}
    	}
	}

	public void updateFoldingStructure(ArrayList<Position> positions, IDocument document) {
	    if (DEBUG) System.out.printf("XtaEditor: updateFoldingStructure\n"); 

	    Annotation[] annotations = new Annotation[positions.size()];
        HashMap<ProjectionAnnotation, Position> annotationPositions = 
    	    new HashMap<ProjectionAnnotation, Position>();

        for(int i = 0; i < positions.size();i++) {
            ProjectionAnnotation annotation = new ProjectionAnnotation();
            annotationPositions.put(annotation, positions.get(i));
            annotations[i] = annotation;
        }
       
        m_projectionAnnotationModel.modifyAnnotations(
        		m_previousProjectionAnnotations, annotationPositions, null);
        
        m_projectionAnnotationModel.collapseAll(0, document.getLength());
        m_previousProjectionAnnotations = annotations;       
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
		fLineNumberRulerColumn= new XtaLineNumberRulerColumn(getInput());
		initializeLineNumberRulerColumn(fLineNumberRulerColumn);
		return fLineNumberRulerColumn;
	}
	
	private HashSet<XtaInstruction> m_prevFnops = new HashSet<XtaInstruction>();
    
    private void updateFnops() {
		if (DEBUG) System.out.printf("XtaEditor: updateFnops\n"); 
		
    	Node pathRoot = null;
    	if (m_selectedNode != null) {
    		pathRoot = m_selectedNode.getPathRoot();
    	}
    	// Don't bother unless the selected path has changed
    	if (pathRoot == m_currentPathRoot)
    		return;

    	m_currentPathRoot = pathRoot;
		
    	HashSet<XtaInstruction> toAdd = new HashSet<XtaInstruction>();
    	HashSet<XtaInstruction> toRemove = new HashSet<XtaInstruction>();
    	HashSet<XtaInstruction> toKeep  = new HashSet<XtaInstruction>();
    	
    	//make list of new fnops to add
    	if (pathRoot != null) {
    		Vector<XtaInstruction> fnops = new Vector<XtaInstruction>();
    		m_currentPathRoot.getFnopPcs(fnops);
    		for(XtaInstruction inst: fnops){
    			if(m_prevFnops.contains(inst)){
    				toKeep.add(inst);
    			} else {
    				toAdd.add(inst);
    			}
    		}
    	}
    
    	//make list of old fnops to remove
    	toRemove = m_prevFnops;
    	toRemove.removeAll(toKeep);
    	
    	//remove old fnops
    	for (XtaInstruction inst : toRemove) {
			//TODO
    		removeFnop(inst.getPc());
		}
    	
    	//add new fnops
    	for (XtaInstruction inst : toAdd) {
			addFnop(inst.getPc());
		}
    	
    	m_prevFnops = toKeep;
    	m_prevFnops.addAll(toAdd);
    	
	    if (m_type.equals(XtaDocumentPartitioner.DISASSEMBLY)) {
		    ((InputDisassembly)getEditorInput()).updateLineNumberMapping();
	    } else {
		    ((InputSource)getEditorInput()).updateLineNumberMapping();	    	
	    }
	}
    
    private void removeFnop(int pc) {
		if (DEBUG) System.out.printf("XtaEditor: removeFnops\n");
		
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
		
		if (m_updatingHighlights)
			return;
		m_updatingHighlights = true;
		
	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        HashMap<Annotation, Position> annotationPositions = 
    	    new HashMap<Annotation, Position>();		
		
        if (m_selectedNode != null) {
        	if (DEBUG) System.out.println("XtaEditor: found " + m_selectedNode.getPcsCovered().size() + " highlights");
			
        	for (Integer pc : m_selectedNode.getPcsCovered()) {
				updateAnnotations(pc, HighlightAnnotationID, annotations, annotationPositions, false, getInput().getPcsToLines());
			}
			
        }
        m_hasHighlightedLines = (annotations.size() > 0);

        int startPointIndex = -1;
        int endPointIndex = -1;
        if (m_startPointLine != null)
        	startPointIndex = m_startPointLine.getIndex();
        if (m_endPointLine != null)
        	endPointIndex = m_endPointLine.getIndex();
        
        m_endPointLines.clear();
        for (SrcTableInfo info : XtaEngine.getEngine().getEndpointTable().values()) {
        	if (info.getFileName().equals(m_fileName)) {
        		InputLine line = findCodeLine(info.getLineNumber());
        		if (line != null) {
        			int index = line.getIndex();
	        		if (index != endPointIndex && index != startPointIndex)
	        			addAnnotation(index, EndpointID, annotations, annotationPositions);
	        		m_endPointLines.put(index, line);
        		}
        	}
        }

        if ((startPointIndex != -1) && (startPointIndex == endPointIndex)) {
        	addAnnotation(startPointIndex, StartStopAnnotationID, annotations, annotationPositions);
        } else {
        	if (startPointIndex != -1)
            	addAnnotation(startPointIndex, StartAnnotationID, annotations, annotationPositions);
        	
        	if (endPointIndex != -1)
            	addAnnotation(endPointIndex, StopAnnotationID, annotations, annotationPositions);
        }
				
        if(DEBUG) System.out.println("XtaEditor: replace highlighting");
        
        
        
		m_annotationModel.replaceAnnotations(m_previousHighlightAnnotations, annotationPositions);        
		m_previousHighlightAnnotations = createArray(annotations);
		
		XtaNotifier.getInstance().notify(Notifier.NOTIFY_UPDATE_HIGHLIGHTS, "");
		m_updatingHighlights = false;
    }
                
    private void updateUnknowns() {

		if (DEBUG) System.out.printf("XtaEditor: upadteUnknowns\n"); 

	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        HashMap<Annotation, Position> annotationPositions = 
    	    new HashMap<Annotation, Position>();		

    	Node pathRoot = null;
    	if (m_selectedNode != null) {
    		pathRoot = m_selectedNode.getPathRoot();
    	}
    	if (pathRoot != null) {
    		if (DEBUG) System.out.println("XtaEditor: found " + pathRoot.getUnknowns().size() + " unknowns");
			HashSet<Integer> unknownsSet = new HashSet<Integer>();
    		for (XtaInstruction instruction : pathRoot.getUnknowns()) {
    			unknownsSet.add(instruction.getPc());
			}
    		for (int pc : unknownsSet) {
				updateAnnotations(pc, UnknownAnnotationID, annotations, annotationPositions, true, getInput().getPcsToLines());
			}
    		
    	}
    	if(DEBUG)  System.out.println("XtaEditor: replace unknowns");
		m_annotationModel.replaceAnnotations(m_previousUnknownAnnotations, annotationPositions);        
		m_previousUnknownAnnotations = createArray(annotations);    
	    
    }

    private void updateAnnotations(int pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions, boolean excludeFnops, HashMap<Integer, InputLine> pcToLine) {
    	InputLine line = pcToLine.get(pc);
    	if(line == null) return;
		if (!getInput().getPcsToSection().get(pc).isFnop(line.getOffset())) {	
			addAnnotation(line.getIndex(), type, annotations, annotationPositions);
			addAnnotation(getInput().getPcsToSection().get(pc).getIndex(), type, annotations, annotationPositions);
		}
		
		//TODO optimise this too with a hashmap
		if(!excludeFnops){
			for(InputSection section : getSections()){
				for(InputLine fnop : section.getFnops()){
					if(fnop.getPc() == pc) { 
						addAnnotation(fnop.getIndex(), type, annotations, annotationPositions);
					}
				}
			}
		}
		
    }

    private void addAnnotation(int index, String type, List<Annotation> annotations,
    		Map<Annotation, Position> annotationPositions) {
		//if (DEBUG) System.out.printf("XtaEditor: addAnnotation, index %d\n", index); 

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

     	setAction(RestoreAllHighlighID, new Action("Restore All Syntax Highlighting") {
     		public void run() {
     			resetGrey();
     			//IDocument document = getSourceViewer().getDocument();
     			//m_projectionAnnotationModel.expandAll(0, document.getLength());
     		}
     	});
     	
     	m_showStartPointAction = new Action("Show start point") {
     		public void run() {
				Reference startPoint = XtaNotifier.getInstance().getStartPoint();
				XtaNotifier.getInstance().notifyShow(startPoint);
     		}
     	};
     	setAction(ShowStartPointID, m_showStartPointAction);
     	
     	m_showEndPointAction = new Action("Show end point") {
     		public void run() {
				Reference endPoint = XtaNotifier.getInstance().getEndPoint();
				XtaNotifier.getInstance().notifyShow(endPoint);
     		}
     	} ;
     	setAction(ShowEndPointID, m_showEndPointAction);

     	setAction(SetStartPointID, new Action("Set start point") {
     		public void run() {
     			XtaNotifier.getInstance().notifySetStartPoint(m_clickCodeRef);
     		}
     	});
     	setAction(SetEndPointID, new Action("Set end point") {
     		public void run() {
     			XtaNotifier.getInstance().notifySetEndPoint(m_clickCodeRef);
     		}
     	});
     	setAction(RemoveStartPointID, new Action("Remove start point") {
     		public void run() {
     			Reference startPoint = ReferenceFactory.createReference(
     					"", ReferenceClass.ENDPOINT_REF);
     			XtaNotifier.getInstance().notifySetStartPoint(startPoint);     			
     		}
     	});
     	setAction(RemoveEndPointID, new Action("Remove end point") {
     		public void run() {
     			Reference endPoint = ReferenceFactory.createReference(
     					"", ReferenceClass.ENDPOINT_REF);
     			XtaNotifier.getInstance().notifySetEndPoint(endPoint);     			
     		}
     	});

     	setAction(ConstrainRequireID, new Action("Require this") {
     		public void run() {
     			addToConstraint(m_clickCodeRef.getRef());
     		}
     	});
     	setAction(ConstrainExcludeID, new Action("Exclude this") {
     		public void run() {
     			addToConstraint("!" + m_clickCodeRef.getRef());
     		}
     	});

     	m_setInstructionTimeAction = new Action("Set instruction time") {
     		public void run() {
     			new InstructionTime(m_parent.getShell(), XtaEngine.getEngine(), m_clickCodeRef);
     		}
     	};
     	setAction(SetInstructionTimeID, m_setInstructionTimeAction);
    }    
    
    protected void addToConstraint(String toAdd) {
		Engine engine = XtaEngine.getEngine();
		Constraint constraint = engine.getConstraint();
		
		String newString = "";
		if (constraint.isValid())		
			newString += constraint.getString() + " && ";
		
		engine.setConstraint(ConstraintFactory.createConstraint(engine, newString + toAdd));
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
    	addAction(menu, RestoreAllHighlighID);
		menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));

		Reference startPoint = XtaNotifier.getInstance().getStartPoint();
		addAction(menu, ShowStartPointID);
		m_showStartPointAction.setEnabled((startPoint != null));

		Reference endPoint = XtaNotifier.getInstance().getEndPoint();
		addAction(menu, ShowEndPointID);
		m_showEndPointAction.setEnabled((endPoint != null));
    }

    protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		if (DEBUG) System.out.printf("XtaEditor: editorContextMenuAboutToShow\n");
		
		int clickLineNum = getVerticalRuler().getLineOfLastMouseButtonActivity();
		
		m_clickCodeRefString = getLineDescription(clickLineNum);				
		if (m_clickCodeRefString != null) {
			
			m_clickCodeRef = ReferenceFactory.createReference(
					m_clickCodeRefString, ReferenceClass.ENDPOINT_REF); 
			
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
			addAction(menu, SetInstructionTimeID);
			addAction(menu, ConstrainExcludeID);
			addAction(menu, ConstrainRequireID);
			
			Engine engine = XtaEngine.getEngine();
			boolean resolved = m_clickCodeRef.resolve(engine, engine.getNodeId(), engine.getCoreId());
			if (resolved &&
				(XtaNotifier.getInstance().getRouteInfo() != null) &&
			     XtaEngine.getEngine().canInstructionPause(m_clickCodeRef.getPc())) {
				
				m_setInstructionTimeAction.setEnabled(true);
			} else {
				m_setInstructionTimeAction.setEnabled(false);
			}
		}

     	super.editorContextMenuAboutToShow(menu);
    }

	private String getLineDescription(int clickLineNum) {
		if (m_endPointLines.containsKey(clickLineNum)) {
			InputLine line = m_endPointLines.get(clickLineNum);
			return line.getCodeReference();
		}
		
    	for (InputSection inputSection : getSections()) {
    		int startLine = inputSection.getIndex();
    		int endLine = startLine + inputSection.getLines().size();
    		int offset = clickLineNum - startLine;

    		// Don't map to the header
    		if ((clickLineNum > startLine) && (clickLineNum <= endLine)) {
    			if (!inputSection.isFnop(offset - 1)) {
    				return inputSection.getLine(offset - 1).getCodeReference();
    			} else {
    				return null;
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
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
		XtaDocumentPartitioner xtaDocumentPartitioner =
			(XtaDocumentPartitioner)getDocument().getDocumentPartitioner();

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

	public boolean hasHighlightedLines() {
		return m_hasHighlightedLines;
	}
}
