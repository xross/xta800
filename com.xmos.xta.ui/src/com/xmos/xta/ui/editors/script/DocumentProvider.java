package com.xmos.xta.ui.editors.script;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

public class DocumentProvider extends AbstractDocumentProvider {
    public static final boolean DEBUG = false;
        
    DocumentProvider() {
		if (DEBUG) System.out.printf("DocumentProvider()\n");			                    
    }

	protected IAnnotationModel createAnnotationModel(Object element)
			throws CoreException {
		if (DEBUG) System.out.printf("DocumentProvider: createAnnotationModel\n");			                    
		return new AnnotationModel();
	}

	protected IDocument createDocument(Object element) throws CoreException {
		if (DEBUG) System.out.printf("DocumentProvider: createDocument\n");			                    
		if (element instanceof IEditorInput) {			
			Document document = new Document();
            String contents = (String)((IEditorInput)element).getAdapter(String.class);
            document.set(contents);
			return document;
		}
		return null;
	}

	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		if (DEBUG) System.out.printf("DocumentProvider: doSaveDocument\n");			                    
	}

	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		if (DEBUG) System.out.printf("DocumentProvider: getOperationRunner\n");			                    
		return null;
	}
}
