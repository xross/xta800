package com.xmos.xta.ui.editors.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

import com.xmos.xta.ui.GuiScriptManager;

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

		if (element instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput)element;
			IFile ifile = input.getFile();
			return documentFromFile(ifile.getRawLocation().toString());
			
		} else if (element instanceof IEditorInput) {			
            File file = (File)((IEditorInput)element).getAdapter(File.class);
            return documentFromFile(file.getAbsolutePath());
		}
		return null;
	}

	private IDocument documentFromFile(String fileName) {
		String contents = "";
	    try {
	    	FileReader fstream = new FileReader(fileName);
	    	BufferedReader in = new BufferedReader(fstream);

		    String line = in.readLine();
		    while (line != null) {
		    	contents += line + "\n";
		    	line = in.readLine();
		    }
			in.close();
		} catch (IOException e) { }
		
		Document document = new Document();
        document.set(contents);
		return document;
	}

	protected void doSaveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		if (DEBUG) System.out.printf("DocumentProvider: doSaveDocument\n");
		
		String fileName = GuiScriptManager.getScriptFileName();
		if (fileName == null) {
			return;
		}

		GuiScriptManager.writeToFile(fileName, document.get());
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
