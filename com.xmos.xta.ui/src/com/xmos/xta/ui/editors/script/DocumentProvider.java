package com.xmos.xta.ui.editors.script;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.GuiEngine;

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
		
		String fileName = GuiEngine.getScriptFileName();
		if (fileName == null) {
			return;
		}

		try {
		    FileWriter fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    out.write(document.get());
		    out.close();
		} catch (Exception e) {
			GuiEngine.getEngine().notify(GuiEngine.s_guiSrc, Notifier.NOTIFY_ERROR,
					String.format("Failed to save to '%s'", fileName));
		}

	}

	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		if (DEBUG) System.out.printf("DocumentProvider: getOperationRunner\n");			                    
		return null;
	}

	public boolean isReadOnly(Object element) {
		return false;
	}

	public boolean isModifiable(Object element) {
		return true;
	}
}
