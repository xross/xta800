package com.xmos.xta.rcp.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
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

import com.xmos.xta.engine.Unknown;
import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.nodes.Node;

public class XtaEditor extends TextEditor implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID                    = "com.xmos.xtagui.editors.Test";
    public static final String HighlightAnnotationID = "com.xmos.xta.rcp.highlight";
    public static final String UnknownAnnotationID   = "com.xmos.xta.rcp.unknown";
    public static final String ExpandAllID           = "com.xmos.xtagui.action.ExpandAll";
    public static final String CollapseAllID         = "com.xmos.xtagui.action.CollapseAll";
    
    private ProjectionSupport m_projectionSupport;
    private ProjectionAnnotationModel m_projectionAnnotationModel;
    private IAnnotationModel m_annotationModel;
    private Annotation[] m_oldAnnotations;
    private AnnotationPainter m_painter;
    
    public XtaEditor() {
		if (DEBUG) System.out.printf("XtaEditor()\n"); 
		XtaDisplayer.getInstance().addPropertyChangeListener(this);

    	setSourceViewerConfiguration(new XtaConfiguration(this));    	
    	setDocumentProvider(new XtaDocumentProvider());        	
    }

    public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("XtaEditor: createPartControl\n"); 
    	super.createPartControl(parent);

    	ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
        
        m_projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
        m_projectionSupport.install();

        viewer.doOperation(ProjectionViewer.TOGGLE);

        m_projectionAnnotationModel = viewer.getProjectionAnnotationModel();
        m_annotationModel = viewer.getAnnotationModel();
        
        viewer.showAnnotations(true);
        viewer.showAnnotationsOverview(true);
        
        getSite().getPage().addSelectionListener(this);
        
        m_painter = new AnnotationPainter(viewer, getAnnotationAccess());        
        m_painter.addAnnotationType(HighlightAnnotationID);                      
    }   
    
    protected void createActions() {
		if (DEBUG) System.out.printf("XtaEditor: createActions\n"); 
     	super.createActions();
     	
     	setAction(ExpandAllID, new Action("Expand All") {
     		public void run() {
     			System.out.printf("Expand All\n");     	
     			IDocument document = getSourceViewer().getDocument();
     			m_projectionAnnotationModel.expandAll(0, document.getLength());
     		}
     	});
     	setAction(CollapseAllID, new Action("Collapse All") {
     		public void run() {
     			System.out.printf("Collapse All\n");
     			IDocument document = getSourceViewer().getDocument();
     			m_projectionAnnotationModel.collapseAll(0, document.getLength());
     		}
     	});
    }
    
    protected void editorContextMenuAboutToShow(IMenuManager menu) {    
		if (DEBUG) System.out.printf("XtaEditor: editorContextMenuAboutToShow\n"); 
     	super.editorContextMenuAboutToShow(menu);

     	menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));     	
    	addAction(menu, ExpandAllID); 
    	addAction(menu, CollapseAllID);      	
    }

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Tree: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals("DisplayRoute")) {
			upadteUnknowns();
		}
	}
    
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.printf("XtaEditor: selectionChanged\n"); 

		if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
        	    updateHighlighting((Node)object);
            }
        }
     }	            		         	        
    
    public void updateFoldingStructure(ArrayList<Position> positions, IDocument document) {
	    if (DEBUG) System.out.printf("XtaEditor: updateFoldingStructure\n"); 

	    Annotation[] annotations = new Annotation[positions.size()];

        HashMap<ProjectionAnnotation, Position> newAnnotations = 
    	    new HashMap<ProjectionAnnotation, Position>();

        for(int i = 0; i < positions.size();i++) {
            ProjectionAnnotation annotation = new ProjectionAnnotation();
            newAnnotations.put(annotation, positions.get(i));
            annotations[i] = annotation;
        }
       
        m_projectionAnnotationModel.modifyAnnotations(m_oldAnnotations, newAnnotations, null);
        m_projectionAnnotationModel.collapseAll(0, document.getLength());
        m_oldAnnotations = annotations;       
    }
    
    protected ISourceViewer createSourceViewer(Composite parent,
    		IVerticalRuler ruler, int styles) {
    	if (DEBUG) System.out.printf("XtaEditor: createSourceViewer\n"); 

        ISourceViewer viewer = new ProjectionViewer(parent, ruler,
		    getOverviewRuler(), isOverviewRulerVisible(), styles);

        getSourceViewerDecorationSupport(viewer);
        return viewer;
    }
    
    private void updateHighlighting(Node node) {
		if (DEBUG) System.out.printf("XtaEditor: updateHighlighting\n"); 

		clearAnnotations(HighlightAnnotationID);				
		for (Integer pc : node.getPcsCovered()) {
			updateAnnotations(pc, HighlightAnnotationID);
		}				
    }
                
    private void upadteUnknowns() {
		if (DEBUG) System.out.printf("XtaEditor: upadteUnknowns\n"); 
  
		clearAnnotations(UnknownAnnotationID);				
		for (Unknown unknown : XtaDisplayer.getInstance().getRouteInfo().getRoute().getUnknowns()) {
			updateAnnotations(unknown.getPc(), UnknownAnnotationID);
		}						
    }

    private void updateAnnotations(int pc, String type) {
		XtaDocumentPartitioner xtaDocumentPartitioner = 
			(XtaDocumentPartitioner)getSourceViewer().getDocument().getDocumentPartitioner();

    	for (InputSection inputSection : xtaDocumentPartitioner.getInput().getSections()) {	
			boolean hitSection = false;
			for (InputLine line : inputSection.getLines()) {
				if (line.getPc() == pc) {	
					addAnnotation(line.getIndex(), type);
					hitSection = true;
				}
			}
			if (hitSection) {
				addAnnotation(inputSection.getIndex(), type);
			}
		}			    	
    }
    
    private void addAnnotation(int index, String type) {
		if (DEBUG) System.out.printf("XtaEditor: addAnnotation, line %d\n", index); 

    	IDocument document = getSourceViewer().getDocument();
        try {
			IRegion lineInfo = document.getLineInformation(index);
	        Position position = new Position(lineInfo.getOffset(), lineInfo.getLength());
	        Annotation annotation = new Annotation(type, true, "");
	        m_annotationModel.addAnnotation(annotation, position);									
		} catch (BadLocationException e) {
			e.printStackTrace();
		}				        
    }
    
    @SuppressWarnings("unchecked")
	private void clearAnnotations(String type) {
		if (DEBUG) System.out.printf("XtaEditor: clearAnnotations\n"); 
    	
		Iterator<Annotation> iter = m_annotationModel.getAnnotationIterator();
		while (iter.hasNext()) {
			Annotation annotation = iter.next();
			if (annotation.getType() == type)
			    m_annotationModel.removeAnnotation(annotation);
		}
    }    
}
