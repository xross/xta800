package com.xmos.xta.ui.annotations;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.ui.IEditorInput;

public interface XtaAnnotatedObject {
	public boolean isDirty();
	public String getFileName();
	public IDocument getDocument();
	public ISourceViewer getViewer();
	public ProjectionAnnotationModel getProjectionAnnotationModel();
	public IAnnotationModelExtension getAnnotationModel();
	public IEditorInput getEditorInput();
	public void refreshInput(IEditorInput input);
}
