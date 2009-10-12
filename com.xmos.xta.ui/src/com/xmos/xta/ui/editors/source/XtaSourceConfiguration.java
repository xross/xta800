package com.xmos.xta.ui.editors.source;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.xmos.xta.ui.editors.XtaDocumentPartitioner;
import com.xmos.xta.ui.editors.XtaEditor;
import com.xmos.xta.ui.editors.XtaRulerAnnotationHover;

public class XtaSourceConfiguration extends SourceViewerConfiguration {
    public static final boolean DEBUG = false;

	private XtaEditor m_editor;    
	
    XtaSourceConfiguration(XtaEditor editor) {
		if (DEBUG) System.out.printf("XtaSourceConfiguration()\n");		
		m_editor = editor;
    }

    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
    	if (DEBUG) System.out.printf("XtaSourceConfiguration: getPresentationReconciler\n");	
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer srcDamageRepairer = new DefaultDamagerRepairer(new ScannerSource());
        reconciler.setDamager(srcDamageRepairer, XtaDocumentPartitioner.SOURCE);
        reconciler.setRepairer(srcDamageRepairer, XtaDocumentPartitioner.SOURCE);
  
        return reconciler;
    }
    
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
    	if (DEBUG) System.out.printf("XtaSourceConfiguration: getAnnotationHover\n");	
		return new XtaRulerAnnotationHover(m_editor);
	}
	
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
    	if (DEBUG) System.out.printf("XtaSourceConfiguration: getHyperlinkDetectors\n");	
		return new IHyperlinkDetector[] { new URLHyperlinkDetector() };
	}
}
