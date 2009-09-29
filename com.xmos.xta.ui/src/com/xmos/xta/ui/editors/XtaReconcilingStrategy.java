package com.xmos.xta.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;

public class XtaReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
    public static final boolean DEBUG = false;
	
	private XtaDisassemblyEditor m_editor;
	
	public void reconcile(IRegion partition) {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: reconcile\n");
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: reconcile\n");
	}

	public void setDocument(IDocument document) {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: setDocument\n");	
	}

	public void initialReconcile() {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: initialReconcile\n");
		m_editor.updateFoldingStructure();
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: setProgressMonitor\n");		
	}

    public XtaEditor getEditor() {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: getEditor\n");		
        return m_editor;
    }

    public void setEditor(XtaDisassemblyEditor editor) {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: setEditor\n");		
    	m_editor = editor;
    }	
}
