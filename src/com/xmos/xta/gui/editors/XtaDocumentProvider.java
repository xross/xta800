package com.xmos.xta.gui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

public class XtaDocumentProvider extends AbstractDocumentProvider {
    public static final boolean DEBUG = false;
    
    private Document m_document;
    
    XtaDocumentProvider() {
		if (DEBUG) System.out.printf("XtaDocumentProvider()\n");			                    
    }

	protected IAnnotationModel createAnnotationModel(Object element)
			throws CoreException {
		if (DEBUG) System.out.printf("XtaDocumentProvider: createAnnotationModel\n");			                    
		return new AnnotationModel();
	}

	protected IDocument createDocument(Object element) throws CoreException {
		if (DEBUG) System.out.printf("XtaDocumentProvider: createDocument\n");			                    
		if (element instanceof IEditorInput) {			
			m_document = new Document();
            Input contents = (Input)((IEditorInput)element).getAdapter(Input.class);						
			IDocumentPartitioner partitioner = new XtaDocumentPartitioner(contents);
			partitioner.connect(m_document);
			m_document.setDocumentPartitioner(partitioner);									                        	
			return m_document;
		}
		return null;
	}

	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		if (DEBUG) System.out.printf("XtaDocumentProvider: doSaveDocument\n");			                    
	}

	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		if (DEBUG) System.out.printf("XtaDocumentProvider: getOperationRunner\n");			                    
		return null;
	}
}
