package com.xmos.xta.ui.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

public class XtaDisassemblyEditor extends XtaEditor {
    public static final String ID = "com.xmos.xta.ui.editors.XtaDisassemblyEditor";
    public static final String EDITORNAME = "Disassembly";

    public static final String ExpandAllID           = "com.xmos.xta.ui.action.ExpandAll";
    public static final String CollapseAllID         = "com.xmos.xta.ui.action.CollapseAll";
    
    public XtaDisassemblyEditor() {
    	setSourceViewerConfiguration(new XtaDisassemblyConfiguration(this));
    	m_annotations = new XtaDisassemblyEditorAnnotations(this);
    }

    public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("XtaDisassemblyEditor: createPartControl\n"); 
    	super.createPartControl(parent);
    
		ProjectionViewer viewer = (ProjectionViewer)getSourceViewer();

		viewer.getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent event) {
				ISelection selection = getSelectionProvider().getSelection();

				if (selection instanceof ITextSelection) {
					ITextSelection textSelection = (ITextSelection)selection;

					if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
						try {
							IRegion line = getDocument().getLineInformationOfOffset(textSelection.getOffset());
							getProjectionAnnotationModel().expandAll(line.getOffset() + line.getLength(), 1);
						} catch (BadLocationException e) {}
	
					} else if (event.keyCode == SWT.BS) {
						try {
							IRegion line = getDocument().getLineInformationOfOffset(textSelection.getOffset());
							getProjectionAnnotationModel().collapseAll(line.getOffset() + line.getLength(), 1);
						} catch (BadLocationException e) {}
					}
				}
			}
        });
	   	
		ProjectionSupport projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
        projectionSupport.install();

        viewer.doOperation(ProjectionViewer.TOGGLE);
        
        m_annotations.connectToFile();
    }

	protected IVerticalRulerColumn createLineNumberRulerColumn() {
    	if (DEBUG) System.out.printf("XtaDisassemblyEditor: createLineNumberRulerColumn\n"); 

		fLineNumberRulerColumn= new XtaLineNumberRulerColumn(getInput());
		initializeLineNumberRulerColumn(fLineNumberRulerColumn);
		return fLineNumberRulerColumn;
	}

	private Input getInput() {
		IDocument document = getDocument();
		if (document == null) {
			return null;
		}
		
		XtaDocumentPartitioner xtaDocumentPartitioner =
			(XtaDocumentPartitioner)document.getDocumentPartitioner();

		return xtaDocumentPartitioner.getInput();		
	}
	
	protected void createActions() {
		if (DEBUG) System.out.printf("XtaDisassemblyEditor: createActions\n"); 
     	super.createActions();
     	
     	setAction(ExpandAllID, new Action("Expand All") {
     		public void run() {
     			IDocument document = getSourceViewer().getDocument();
     			getProjectionAnnotationModel().expandAll(0, document.getLength());
     		}
     	});
     	
     	setAction(CollapseAllID, new Action("Collapse All") {
     		public void run() {
     			IDocument document = getSourceViewer().getDocument();
     			getProjectionAnnotationModel().collapseAll(0, document.getLength());
     		}
     	});
    }    

	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		if (DEBUG) System.out.printf("XtaDisassemblyEditor: editorContextMenuAboutToShow\n");
		super.editorContextMenuAboutToShow(menu);

		menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
     	addAction(menu, ExpandAllID);
     	addAction(menu, CollapseAllID);
	}

	public boolean isDirty() {
		// Keep the editor pages from showing a '*' when FNOPs are inserted and removed
		return false;
	}

	public void updateFoldingStructure() {
		XtaDisassemblyEditorAnnotations annotations = (XtaDisassemblyEditorAnnotations)getAnnotations();
		ProjectionAnnotationModel model = annotations.updateFoldingStructure();
		annotations.collapseFolding(model);
	}

	public String getFileName() {
		return EDITORNAME;
	}
}
