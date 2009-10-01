package com.xmos.xta.ui.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.ui.IEditorInput;

public interface XtaAnnotatedEditor {
	public IDocument getDocument();
	public ProjectionAnnotationModel getProjectionAnnotationModel();
	public IAnnotationModelExtension getAnnotationModel();
	public ISourceViewer getViewer();
	public IEditorInput getEditorInput();
	public void refreshInput(IEditorInput input);
	public String getFileName();
	public boolean isDirty();
}
