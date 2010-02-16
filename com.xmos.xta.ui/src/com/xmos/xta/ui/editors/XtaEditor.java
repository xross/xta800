package com.xmos.xta.ui.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.actions.SourceActions;
import com.xmos.xta.ui.annotations.XtaAnnotatedObject;
import com.xmos.xta.ui.annotations.XtaAnnotations;

public abstract class XtaEditor extends TextEditor implements XtaAnnotatedObject {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.ui.editors.XtaEditor.Test";
    
    private SourceActions m_actions;

    protected String m_partName;
    protected XtaAnnotations m_annotations;

	public abstract String getFileName();
    
    public XtaEditor() {
		if (DEBUG) System.out.printf("XtaEditor()\n");
    	setDocumentProvider(new XtaDocumentProvider());
    }

    public void dispose() {
		if (DEBUG) System.out.printf("XtaEditor: dispose\n");
		m_annotations.dispose();
		super.dispose();
    }

    public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("XtaEditor: createPartControl\n"); 
    	super.createPartControl(parent);
    	m_actions = new SourceActions(parent);
    	
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
        viewer.showAnnotations(true);
        viewer.showAnnotationsOverview(true);
        
        AnnotationPainter painter = new AnnotationPainter(viewer, getAnnotationAccess());        
        painter.addAnnotationType(XtaAnnotations.HighlightAnnotationID);
    }
    
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
    	if (DEBUG) System.out.printf("XtaEditor: createSourceViewer\n"); 

        ISourceViewer viewer = new ProjectionViewer(parent, ruler,
        		getOverviewRuler(), isOverviewRulerVisible(), styles);

        getSourceViewerDecorationSupport(viewer);
        return viewer;
    }

	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		if (DEBUG) System.out.printf("XtaEditor: editorContextMenuAboutToShow\n"); 
     	super.editorContextMenuAboutToShow(menu);

		menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
		Engine engine = GuiEngine.getEngine();
		Reference fromEndPoint = engine.getFromEndPoint();
		Action showStartAction = m_actions.get(SourceActions.ShowStartPointID);
		menu.add(showStartAction);
		showStartAction.setEnabled((fromEndPoint != null));

		Reference toEndPoint = engine.getToEndPoint();
		Action showEndAction = m_actions.get(SourceActions.ShowEndPointID);
		menu.add(showEndAction);
		showEndAction.setEnabled((toEndPoint != null));
    }

    protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		if (DEBUG) System.out.printf("XtaEditor: rulerContextMenuAboutToShow\n");
		long clickLineIndex = getVerticalRuler().getLineOfLastMouseButtonActivity();
		m_actions.createRulerContextMenu(menu, m_annotations, clickLineIndex);
     	super.editorContextMenuAboutToShow(menu);
    }

	// Once the name is set then the endpoints can be updated
    public void setName(String partName) {
		m_partName = partName;
		setPartName(partName);
	}

	public IDocument getDocument() {
		ISourceViewer viewer = getSourceViewer();
		if (viewer == null)
			return null;
		
		return viewer.getDocument();
	}

	public ProjectionAnnotationModel getProjectionAnnotationModel() {
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
		return viewer.getProjectionAnnotationModel();
	}

	public IAnnotationModelExtension getAnnotationModel() {
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
        return (IAnnotationModelExtension)viewer.getAnnotationModel();
	}

	public ISourceViewer getViewer() {
		return getSourceViewer();
	}
	
	public XtaAnnotations getAnnotations() {
		return m_annotations;
	}

	public void refreshInput(IEditorInput input) {
		setInput(input);
		setPartName(m_partName);
	}
}
