package com.xmos.xtagui.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class XtaConfiguration extends SourceViewerConfiguration {
    public static final boolean DEBUG = true;

	private XtaEditor m_editor;    
    
    XtaConfiguration(XtaEditor editor) {
		if (DEBUG) System.out.printf("XtaConfiguration()\n");		
		m_editor = editor;
    }

    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if (DEBUG) System.out.printf("XtaConfiguration: getContentAssistant\n");		                    

        ContentAssistant assistant = new ContentAssistant();
        assistant.setContentAssistProcessor(new XtaContentAssistantProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
        assistant.enableAutoActivation(true);        
        return assistant;
    }

    public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (DEBUG) System.out.printf("XtaConfiguration: getReconciler\n");			                    

        XtaReconcilingStrategy strategy = new XtaReconcilingStrategy();
        strategy.setEditor(m_editor);
        
        MonoReconciler reconciler = new MonoReconciler(strategy, false);        
        return reconciler;
    }
}
