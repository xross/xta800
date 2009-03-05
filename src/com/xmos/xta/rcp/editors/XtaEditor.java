package com.xmos.xta.rcp.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.dialogs.InstructionTime;

import com.xmos.xta.rcp.nodes.Node;

public class XtaEditor extends TextEditor implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final boolean DEBUG = false;
    
    private Composite m_parent;

    public static final String ID                    = "com.xmos.xtagui.editors.Test";
    public static final String HighlightAnnotationID = "com.xmos.xta.rcp.highlight";
    public static final String UnknownAnnotationID   = "com.xmos.xta.rcp.unknown";
    public static final String StartAnnotationID     = "com.xmos.xta.rcp.start";
    public static final String StopAnnotationID      = "com.xmos.xta.rcp.stop";
    public static final String ExpandAllID           = "com.xmos.xtagui.action.ExpandAll";
    public static final String CollapseAllID         = "com.xmos.xtagui.action.CollapseAll";

    public static final String ShowStartPointID      = "com.xmos.xtagui.action.ShowStartPoint";
    public static final String ShowEndPointID        = "com.xmos.xtagui.action.ShowEndPoint";
    public static final String SetStartPointID       = "com.xmos.xtagui.action.SetStartPoint";
    public static final String SetEndPointID         = "com.xmos.xtagui.action.SetEndPoint";
    public static final String RemoveStartPointID    = "com.xmos.xtagui.action.RemoveStartPoint";
    public static final String RemoveEndPointID      = "com.xmos.xtagui.action.RemoveEndPoint";
    
    public static final String SetInstructionTimeID  = "com.xmos.xtagui.action.SetInstructionTime";
    
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
    
    private Node m_currentPathRoot;
    private Node m_selectedNode;
    
    private final String m_type;
    private final String m_fileName;
    private InputLine m_startPointLine;
    private InputLine m_endPointLine;
    
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
    	
		XtaNotifier.getInstance().removePropertyChangeListener(this);
		getSite().getPage().removeSelectionListener(this);
		super.dispose();
    }
    
	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("XtaEditor: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			m_selectedNode = null;
			updateFnops();
    	    updateHighlighting();
			updateUnknowns();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			m_selectedNode = null;
	    	m_startPointLine = null;
	        m_endPointLine = null;			
			updateFnops();
    	    updateHighlighting();
    	    
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

	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.printf("XtaEditor: selectionChanged\n"); 

		if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
            	m_selectedNode = (Node)object;
            	updateFnops();
        	    updateHighlighting();
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
		removeFnops();
    	if (pathRoot != null) {
        	Vector<XtaInstruction> fnops = new Vector<XtaInstruction>();
        	m_currentPathRoot.getFnopPcs(fnops);
			for (XtaInstruction inst : fnops) {
				addFnop(inst.getPc());
			}
    	}
    	
	    if (m_type.equals(XtaDocumentPartitioner.DISASSEMBLY)) {
		    ((InputDisassembly)getEditorInput()).updateLineNumberMapping();
	    } else {
		    ((InputSource)getEditorInput()).updateLineNumberMapping();	    	
	    }
	}

    private void removeFnops() {
		if (DEBUG) System.out.printf("XtaEditor: removeFnops\n");
		
		int fnopsRemoved = 0;
		IDocument document = getDocument();
    	for (InputSection inputSection : getSections()) {
    		inputSection.setIndex(inputSection.getIndex() - fnopsRemoved);
    		
    		InputLine fnopLine = inputSection.removeFnop();
    		while (fnopLine != null) {
    			fnopsRemoved++;
    			int index = fnopLine.getIndex();
				try {
					IRegion lineInfo = document.getLineInformation(index);
		            document.replace(lineInfo.getOffset(), fnopString.length(), "");
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
	    		fnopLine = inputSection.removeFnop();
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
							inputSection.addFnop(fnopString, pc, index);
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
        HashMap<Annotation, Position> annotationPositions = 
    	    new HashMap<Annotation, Position>();		
		
        if (m_selectedNode != null) {
			for (Integer pc : m_selectedNode.getPcsCovered()) {
				updateAnnotations(pc, HighlightAnnotationID, annotations, annotationPositions, false);
			}
        }
        
        m_hasHighlightedLines = (annotations.size() > 0);
        
        if (m_startPointLine != null) {
        	addAnnotation(m_startPointLine.getIndex(), StartAnnotationID, annotations, annotationPositions);
        }
        if (m_endPointLine != null) {
        	addAnnotation(m_endPointLine.getIndex(), StopAnnotationID, annotations, annotationPositions);
        }
				
		m_annotationModel.replaceAnnotations(m_previousHighlightAnnotations, annotationPositions);        
		m_previousHighlightAnnotations = createArray(annotations);
		
		XtaNotifier.getInstance().notify(Notifier.NOTIFY_UPDATE_HIGHLIGHTS, "");
    }
                
    private void updateUnknowns() {
		if (DEBUG) System.out.printf("XtaEditor: upadteUnknowns\n"); 

	    ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        HashMap<Annotation, Position> annotationPositions = 
    	    new HashMap<Annotation, Position>();		
		
		for (XtaInstruction instruction : XtaNotifier.getInstance().getRouteInfo().getRoute().getUnknowns()) {
			updateAnnotations(instruction.getPc(), UnknownAnnotationID, annotations, annotationPositions, true);
		}						    

		m_annotationModel.replaceAnnotations(m_previousUnknownAnnotations, annotationPositions);        
		m_previousUnknownAnnotations = createArray(annotations);    		    
    }

    private void updateAnnotations(int pc, String type, List<Annotation> annotations, 
    		Map<Annotation, Position> annotationPositions, boolean excludeFnops) {
		if (DEBUG) System.out.printf("XtaEditor: updateAnnotations: pc: 0x%05x\n", pc); 

    	for (InputSection inputSection : getSections()) {
			boolean hitSection = false;
			for (InputLine line : inputSection.getLines()) {
				if ((line.getPc() == pc) && (!excludeFnops || !inputSection.isFnop(line.getOffset()))) {	
					addAnnotation(line.getIndex(), type, annotations, annotationPositions);
					hitSection = true;
				}
			}
			if (hitSection) {
				addAnnotation(inputSection.getIndex(), type, annotations, annotationPositions);
			}
		}
    }
    
    private void addAnnotation(int index, String type, List<Annotation> annotations,
    		Map<Annotation, Position> annotationPositions) {
		if (DEBUG) System.out.printf("XtaEditor: addAnnotation, index %d\n", index); 

    	IDocument document = getSourceViewer().getDocument();
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

     	m_setInstructionTimeAction = new Action("Set instruction time") {
     		public void run() {
     			new InstructionTime(m_parent.getShell(), XtaEngine.getEngine(), m_clickCodeRef);
     		}
     	};
     	setAction(SetInstructionTimeID, m_setInstructionTimeAction);
    }    
    
    protected void editorContextMenuAboutToShow(IMenuManager menu) {
		if (DEBUG) System.out.printf("XtaEditor: editorContextMenuAboutToShow\n"); 
     	super.editorContextMenuAboutToShow(menu);

     	menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));     	
    	addAction(menu, ExpandAllID);
    	addAction(menu, CollapseAllID);
    	
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
    	for (InputSection inputSection : getSections()) {
    		int startLine = inputSection.getIndex();
    		int endLine = startLine + inputSection.getLines().size();
    		int offset = clickLineNum - startLine;
    		if (m_type.equals(XtaDocumentPartitioner.SOURCE)) {
    			// Must be some associated assembly
    			if (startLine != endLine) {
    				if ((clickLineNum >= startLine) && (clickLineNum <= endLine)) {
        				if (offset == 0) {
        					return inputSection.getHeader().getCodeReference();
        				} else {
        					return inputSection.getLine(offset - 1).getCodeReference();
        				}
        			}
    			}
    		} else {
    			// Don't map to the header
        		if ((clickLineNum > startLine) && (clickLineNum <= endLine)) {
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
