package com.xmos.xta.rcp.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.xmos.xta.engine.maps.EndpointTableInfo;
import com.xmos.xta.engine.maps.SrcLabelTableInfo;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;
import com.xmos.xta.engine.unknowns.Unknown;
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
    public static final String ExcludedAnnotationID  = "com.xmos.xta.rcp.excluded";
    public static final String EndpointID            = "com.xmos.xta.rcp.endpoint";
    public static final String ExpandAllID           = "com.xmos.xtagui.action.ExpandAll";
    public static final String CollapseAllID         = "com.xmos.xtagui.action.CollapseAll";
    public static final String RestoreAllHighlightID = "com.xmos.xtagui.action.RestoreAllHighlight";

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

    private List<Annotation> m_currentHighlightAnnotations;
	private HashMap<Annotation, Position> m_currentHighlightAnnotationPositions;
    private boolean m_hasHighlightedLines;
    
    private Node m_currentPathRoot;
    private Node m_selectedNode;
    
    private final String m_type;
    private final String m_fileName;
    private InputLine m_startPointLine;
    private InputLine m_endPointLine;
    private Map<Integer,InputLine> m_endPointLineIndexes;
    private HashSet<Integer> m_excludedIndexes;
    
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
        
        m_endPointLineIndexes = new TreeMap<Integer,InputLine>();
        m_excludedIndexes = new HashSet<Integer>();
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
	        updateAnnotations(m_currentHighlightAnnotations, m_currentHighlightAnnotationPositions);
    	    
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT)) {
			Reference endPoint = XtaNotifier.getInstance().getEndPoint();
			m_endPointLine = findCodeRefLineNum(endPoint);
	        updateAnnotations(m_currentHighlightAnnotations, m_currentHighlightAnnotationPositions);
    	    
		} else if (event.getProperty().equals(Notifier.NOTIFY_SHOW_CODE_POINT)) {
			Reference codePoint = XtaNotifier.getInstance().getCodePoint();
			showCodePoint(codePoint);
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			updateUnknowns();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_CONSTRAINT) ||
				   event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			updateExcluded();
	        updateAnnotations(m_currentHighlightAnnotations, m_currentHighlightAnnotationPositions);

		}
	}

	public void resetGrey() {
		m_selectedNode = null;
		updateInputLines(true);
		updatePartitions();
		updateEditor();
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
            		updateEditor();
            	}
            }
        }
    }

	private void updateEditor() {
		updateFnops();
		updateExcluded();
		updateHighlighting();
		updateUnknowns();
	}	            		         	        
    	
    private void updatePartitions() {
    	IDocument document = getDocument();
    	if (document != null) {
    		XtaDocumentPartitioner xtaDocumentPartitioner = (XtaDocumentPartitioner)document.getDocumentPartitioner();
    		xtaDocumentPartitioner.partitionDocument(document, false);
    		getSourceViewer().invalidateTextPresentation();
    	}
	}

	private void updateInputLines(boolean reset) {
		m_selectedNode = null;
	    updateEditor();

		Input input = getInput();
    	if (input == null)
    		return;
    	
		Route route = XtaNotifier.getInstance().getRouteInfo().getRoute();
		if (route != null && !reset){
	    	
    		for (InputSection inputSection : input.getSections()){
    			inputSection.setGrey(true);
				for(InputLine inputLine: inputSection.getLines()){
					inputLine.setGrey(true);
				}
			}
    		
	    	HashSet<Integer> pcsCovered = route.getPcsHit();
	    	
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
    		for (InputSection inputSection : input.getSections()){
    			inputSection.setGrey(false);
				for (InputLine inputLine: inputSection.getLines()){
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
        	HashSet<Integer> pcsCovered = m_selectedNode.getPcsCovered(true);
			if (DEBUG) System.out.println("XtaEditor: found " + pcsCovered.size() + " highlights");
			
        	for (Integer pc : pcsCovered) {
				updateAnnotations(pc, HighlightAnnotationID, annotations, annotationPositions, false, getInput().getPcsToLines());
			}
        }
        m_hasHighlightedLines = (annotations.size() > 0);

        if (DEBUG) System.out.println("XtaEditor: replace highlighting");
        m_currentHighlightAnnotations = annotations;
		m_currentHighlightAnnotationPositions = annotationPositions;
		updateAnnotations(annotations, annotationPositions);
		
		XtaNotifier.getInstance().notify(Notifier.NOTIFY_HIGHLIGHTS_UPDATED, "");
    }

    // Add all annotations after highlights
	private void updateAnnotations(List<Annotation> annotations,
			HashMap<Annotation, Position> annotationPositions) {

		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>(annotations);
		final HashMap<Annotation, Position> allAnnotationPositions =
			new HashMap<Annotation, Position>(annotationPositions);
		
        int startPointIndex = getStartPointIndex();
        int endPointIndex = getEndPointIndex();

        m_endPointLineIndexes.clear();
        for (EndpointTableInfo info : XtaEngine.getEngine().getEndpointTable().values()) {
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
		Constraint constraint = XtaEngine.getEngine().getConstraint();
		if ((constraint == null) || !constraint.isValid()) {
			return;
		}
		
		for (InputSection inputSection : getSections()) {
			if (m_type.equals(XtaDocumentPartitioner.SOURCE)) {
				checkLineForExclusion(constraint, inputSection.getHeader().getCodeReference(),
						inputSection.getIndex());
			}
			for (InputLine line : inputSection.getLines()) {
				if (checkLineForExclusion(constraint, line.getCodeReference(), line.getIndex())) {
					m_excludedIndexes.add(inputSection.getIndex());
				}
			}
    	}
	}

    private boolean checkLineForExclusion(Constraint constraint, String refStr, int index) {
		Engine engine = XtaEngine.getEngine();
		Reference headerRef = ReferenceFactory.createReference(refStr, ReferenceClass.SRCLABEL_REF);
		if (headerRef.resolve(engine, engine.getNodeId(),engine.getCoreId())) {
			Constraint clone = constraint.clone();
			if (clone.isFalse(headerRef.getPc())) {
				m_excludedIndexes.add(index);
				return true;
			}
		}
		return false;
	}

	public int getStartPointIndex() {
        if (m_startPointLine != null)
        	return m_startPointLine.getIndex();
		return -1;
	}

    public int getEndPointIndex() {
        if (m_endPointLine != null)
        	return m_endPointLine.getIndex();
		return -1;
	}

    public HashSet<Unknown> getUnknownsForLine(int lineNumber) {
    	HashSet<Unknown> unknowns = new HashSet<Unknown>();

    	HashSet<Integer> linePcs = getPcsForLine(lineNumber);

		for (Unknown unknown : getSelectedPathUnknowns()) {
			if (linePcs.contains(unknown.getPc())) {
				unknowns.add(unknown);
			}
		}
    	return unknowns;
    }
    
	private HashSet<Unknown> getSelectedPathUnknowns() {
		int selectedRouteId = XtaNotifier.getInstance().getSelectedRouteId();
		Route route = XtaEngine.getEngine().getRoute(selectedRouteId);
		if (route != null) {
			HashSet<Integer> selectedBlocks = getSelectedPathBlockIds();
			return route.getUnknowns().getSelectedUnknowns(selectedBlocks);
		} else {
			return new HashSet<Unknown>();
		}
	}

	private HashSet<Integer> getSelectedPathBlockIds() {
		HashSet<Integer> selectedBlocks = new HashSet<Integer>();

		Node pathRoot = null;
    	if (m_selectedNode != null) {
    		pathRoot = m_selectedNode.getPathRoot();
    	}
    	if (pathRoot != null)
    		pathRoot.getBlockIds(selectedBlocks);

    	return selectedBlocks;
	}

	public boolean isValidEndPoint(int lineNumber) {
		if (m_endPointLineIndexes.containsKey(lineNumber)) {
			return true;
		}
		return false;
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
		for (Unknown unknown : getSelectedPathUnknowns()) {
			unknownsSet.add(unknown.getPc());
		}
		for (int pc : unknownsSet) {
			updateAnnotations(pc, UnknownAnnotationID, annotations, annotationPositions, true, getInput().getPcsToLines());
		}
    		
    	if (DEBUG) System.out.println("XtaEditor: replace unknowns");
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {      
				m_annotationModel.replaceAnnotations(m_previousUnknownAnnotations, annotationPositions);        
			}});
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
		
		//TODO optimise this too with a hashmap
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

     	setAction(RestoreAllHighlightID, new Action("Restore All Syntax Highlighting") {
     		public void run() {
     			resetGrey();
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
     			Route route = XtaEngine.getEngine().getRoute(XtaNotifier.getInstance().getSelectedRouteId());
     			new InstructionTime(m_parent.getShell(), XtaEngine.getEngine(), m_clickCodeRef, route);
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
    	addAction(menu, RestoreAllHighlightID);
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
		
		int clickLineIndex = getVerticalRuler().getLineOfLastMouseButtonActivity();
		
		m_clickCodeRefString = getLineDescription(clickLineIndex);				
		if (m_clickCodeRefString != null) {
			m_clickCodeRef = ReferenceFactory.createReference(m_clickCodeRefString, ReferenceClass.ENDPOINT_REF); 
			
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
        Map<Integer, SrcLabelTableInfo> srcLabelTable = XtaEngine.getEngine().getSrcLabelTable();
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

	public boolean hasHighlightedLines() {
		return m_hasHighlightedLines;
	}
}
