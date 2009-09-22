package com.xmos.xta.ui.editors;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;

public class XtaReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
    public static final boolean DEBUG = false;
	
	private XtaEditor m_editor;
	private IDocument m_document;
	
	public void reconcile(IRegion partition) {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: reconcile\n");
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: reconcile\n");
		ArrayList<Position> positions = new ArrayList<Position>();
		try {
			for (Position position : m_document.getPositions(XtaDocumentPartitioner.SECTION)) {
				positions.add(position);
			}
		} catch (BadPositionCategoryException e) {
			e.printStackTrace();
		}
		m_editor.updateFoldingStructure(positions, m_document);
	}

	public void setDocument(IDocument document) {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: setDocument\n");	
		m_document = document;
	}

	public void initialReconcile() {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: initialReconcile\n");
		ArrayList<Position> positions = new ArrayList<Position>();
		try {
			for (Position position : m_document.getPositions(XtaDocumentPartitioner.SECTION)) {
				positions.add(position);
			}
		} catch (BadPositionCategoryException e) {
			e.printStackTrace();
		}
		m_editor.updateFoldingStructure(positions, m_document);
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: setProgressMonitor\n");		
	}

    public XtaEditor getEditor() {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: getEditor\n");		
        return m_editor;
    }

    public void setEditor(XtaEditor editor) {
		if (DEBUG) System.out.printf("XtaReconcilingStrategy: setEditor\n");		
    	m_editor = editor;
    }	
}
