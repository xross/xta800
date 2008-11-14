package com.xmos.xtagui.editors;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;

public class XtaEditor extends TextEditor {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.editors.Test";
    
    private ProjectionSupport m_projectionSupport;
    private ProjectionAnnotationModel m_annotationModel;
    private Annotation[] m_oldAnnotations;
   
    public XtaEditor() {
    	setSourceViewerConfiguration(new XtaConfiguration(this));
    	setDocumentProvider(new XtaDocumentProvider());        	
    }

    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
    
        m_projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
        m_projectionSupport.install();

        // Turn projection mode on
        viewer.doOperation(ProjectionViewer.TOGGLE);

        m_annotationModel = viewer.getProjectionAnnotationModel();
    }   
        
    public void updateFoldingStructure(ArrayList<Position> positions) {
       Annotation[] annotations = new Annotation[positions.size()];

       // This will hold the new annotations along with their corresponding positions
       HashMap<ProjectionAnnotation, Position> newAnnotations = new HashMap<ProjectionAnnotation, Position>();

       for(int i = 0; i < positions.size();i++) {
          ProjectionAnnotation annotation = new ProjectionAnnotation();
          newAnnotations.put(annotation, positions.get(i));
          annotations[i] = annotation;
       }
       m_annotationModel.modifyAnnotations(m_oldAnnotations, newAnnotations, null);

       m_oldAnnotations = annotations;
    }    

    protected ISourceViewer createSourceViewer(Composite parent,
            IVerticalRuler ruler, int styles) {
        ISourceViewer viewer = new ProjectionViewer(parent, ruler,
		    getOverviewRuler(), isOverviewRulerVisible(), styles);

        // ensure decoration support has been created and configured.
        getSourceViewerDecorationSupport(viewer);

        return viewer;
    }
}
