package com.xmos.xta.ui.editors;

import java.util.ArrayList;
import java.util.List;

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
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.dialogs.InstructionTime;

public abstract class XtaEditor extends TextEditor implements XtaAnnotatedEditor {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.ui.editors.XtaEditor.Test";
    
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

    protected String m_partName;
    protected XtaEditorAnnotations m_annotations;

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
    	m_parent = parent;
    	
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();
        viewer.showAnnotations(true);
        viewer.showAnnotationsOverview(true);
        
        AnnotationPainter painter = new AnnotationPainter(viewer, getAnnotationAccess());        
        painter.addAnnotationType(XtaEditorAnnotations.HighlightAnnotationID);
    }
    
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
    	if (DEBUG) System.out.printf("XtaEditor: createSourceViewer\n"); 

        ISourceViewer viewer = new ProjectionViewer(parent, ruler,
        		getOverviewRuler(), isOverviewRulerVisible(), styles);

        getSourceViewerDecorationSupport(viewer);
        return viewer;
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
     			if(!nodes.isEmpty()){
     				new InstructionTime(m_parent.getShell(), nodes);
     			}
     			
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
		
		m_clickCodeRefString = m_annotations.getLineDescription(clickLineIndex);				
		if (m_clickCodeRefString != null) {
			if (m_annotations.isValidEndPoint(clickLineIndex + 1)) {
				if (m_annotations.isStartPointSetForIndex(clickLineIndex)) {
				    addAction(menu, RemoveStartPointID);
				} else {
				    addAction(menu, SetStartPointID);				
				}
				if (m_annotations.isEndPointSetForIndex(clickLineIndex)) {						
				    addAction(menu, RemoveEndPointID);
				} else {
				    addAction(menu, SetEndPointID);				
				}
				menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
			}
			
			if (m_annotations.isExcluded(m_clickCodeRefString)) {
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
	
	public XtaEditorAnnotations getAnnotations() {
		return m_annotations;
	}

	public void refreshInput(IEditorInput input) {
		setInput(input);
		setPartName(m_partName);
	}
}
