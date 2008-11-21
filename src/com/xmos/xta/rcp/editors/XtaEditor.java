package com.xmos.xta.rcp.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;

import com.xmos.xta.rcp.nodes.Node;

public class XtaEditor extends TextEditor implements ISelectionListener {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.editors.Test";
    public static final String AnnotationID = "com.xmos.xta.rcp.path";
    
    private ProjectionSupport m_projectionSupport;
    private ProjectionAnnotationModel m_annotationModel;
    private IAnnotationModel m_highlightAnnotationModel;
    private Annotation[] m_oldAnnotations;
    private AnnotationPainter m_painter;
    
    public XtaEditor() {
    	setSourceViewerConfiguration(new XtaConfiguration(this));
    	setDocumentProvider(new XtaDocumentProvider());        	
    }

    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
        
        m_projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
        m_projectionSupport.install();

        viewer.doOperation(ProjectionViewer.TOGGLE);

        m_annotationModel = viewer.getProjectionAnnotationModel();
        m_highlightAnnotationModel = viewer.getAnnotationModel();
        viewer.showAnnotations(true);
        viewer.showAnnotationsOverview(true);
        
        getSite().getPage().addSelectionListener(this);
        
        m_painter = new AnnotationPainter(viewer, getAnnotationAccess());        
        m_painter.addAnnotationType(AnnotationID);            
    }   
        
    public void updateFoldingStructure(ArrayList<Position> positions, IDocument document) {
       Annotation[] annotations = new Annotation[positions.size()];

       HashMap<ProjectionAnnotation, Position> newAnnotations = 
    	   new HashMap<ProjectionAnnotation, Position>();

       for(int i = 0; i < positions.size();i++) {
          ProjectionAnnotation annotation = new ProjectionAnnotation();
          newAnnotations.put(annotation, positions.get(i));
          annotations[i] = annotation;
       }
       m_annotationModel.modifyAnnotations(m_oldAnnotations, newAnnotations, null);
       m_annotationModel.collapseAll(0, document.getLength());
       m_oldAnnotations = annotations;       
    }    

    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
        if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
        	    updateHighlighting((Node)object);
            }
        }
     }	            		         	        
    
    protected ISourceViewer createSourceViewer(Composite parent,
            IVerticalRuler ruler, int styles) {
        ISourceViewer viewer = new ProjectionViewer(parent, ruler,
		    getOverviewRuler(), isOverviewRulerVisible(), styles);

        getSourceViewerDecorationSupport(viewer);
        return viewer;
    }
    
    private void updateHighlighting(Node node) {
		if (DEBUG) displayNodeInfo(node);

		XtaDocumentPartitioner xtaDocumentPartitioner = 
			(XtaDocumentPartitioner)getSourceViewer().getDocument().getDocumentPartitioner();

		clearHighlightAnnotations();				
		for (Integer pc : node.getPcsCovered()) {
			for (InputSection inputSection : xtaDocumentPartitioner.getInput().getSections()) {	
				boolean hitSection = false;
				for (InputLine line : inputSection.getLines()) {
					if (line.getPc() == pc) {	
						addHighlightAnnotation(line.getIndex());
						hitSection = true;
					}
				}
				if (hitSection) {
					addHighlightAnnotation(inputSection.getIndex());
				}
			}			
		}				
    }
    
    private void addHighlightAnnotation(int index) {
		if (DEBUG) System.out.printf("XtaEditor: addHighlightAnnotation, line %d\n", index); 

    	IDocument document = getSourceViewer().getDocument();
        try {
			IRegion lineInfo = document.getLineInformation(index);
	        Position position = new Position(lineInfo.getOffset(), lineInfo.getLength());
	        Annotation annotation = new Annotation(AnnotationID, true, "");
	        m_highlightAnnotationModel.addAnnotation(annotation, position);									
		} catch (BadLocationException e) {
			e.printStackTrace();
		}				        
    }
    
    @SuppressWarnings("unchecked")
	private void clearHighlightAnnotations() {
		if (DEBUG) System.out.printf("XtaEditor: clearHighlightAnnotations\n"); 
    	
		Iterator<Annotation> iter = m_highlightAnnotationModel.getAnnotationIterator();
		while (iter.hasNext()) {
			m_highlightAnnotationModel.removeAnnotation(iter.next());
		}
    }
    
    private void displayNodeInfo(Node node) {
		System.out.printf("XtaEditor: displayNodeInfo: node(%d,%d): %s\n", 
				node.getId(), node.getInstanceId(), node.getDescription());
		
		System.out.printf("XtaEditor: displayNodeInfo: pcs: ");
		for (Integer pc : node.getPcsCovered()) {
			System.out.printf("0x%05x ", pc);
		}
		System.out.printf("\n");   	
    }
}
