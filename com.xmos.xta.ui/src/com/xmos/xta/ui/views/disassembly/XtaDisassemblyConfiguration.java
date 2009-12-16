package com.xmos.xta.ui.views.disassembly;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.xmos.xta.ui.annotations.XtaAnnotations;
import com.xmos.xta.ui.annotations.XtaRulerAnnotationHover;
import com.xmos.xta.ui.editors.XtaDocumentPartitioner;

public class XtaDisassemblyConfiguration extends SourceViewerConfiguration {
    public static final boolean DEBUG = false;

	private XtaAnnotations m_annotations;    
	
    XtaDisassemblyConfiguration(XtaAnnotations annotations) {
		if (DEBUG) System.out.printf("XtaDisassemblyConfiguration()\n");		
		m_annotations = annotations;
    }
    
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
    	if (DEBUG) System.out.printf("XtaDisassemblyConfiguration: getPresentationReconciler\n");	
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer asmDamageRepairer = new DefaultDamagerRepairer(new ScannerDisassembly());
        reconciler.setDamager(asmDamageRepairer, XtaDocumentPartitioner.DISASSEMBLY);
        reconciler.setRepairer(asmDamageRepairer, XtaDocumentPartitioner.DISASSEMBLY);
        
        DefaultDamagerRepairer asmFnDamageRepairer = new DefaultDamagerRepairer(new ScannerDisassemblyFunction());
        reconciler.setDamager(asmFnDamageRepairer, XtaDocumentPartitioner.DISASSEMBLYFUNCTION);
        reconciler.setRepairer(asmFnDamageRepairer, XtaDocumentPartitioner.DISASSEMBLYFUNCTION);
        
        return reconciler;
    }
    
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
    	if (DEBUG) System.out.printf("XtaDisassemblyConfiguration: getAnnotationHover\n");	
		return new XtaRulerAnnotationHover(m_annotations);
	}
	
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
    	if (DEBUG) System.out.printf("XtaDisassemblyConfiguration: getHyperlinkDetectors\n");	
		return new IHyperlinkDetector[] {
			new BranchTargetHyperlinkDetector(),
			new URLHyperlinkDetector()
		};
	}

	public int getHyperlinkStateMask(ISourceViewer sourceViewer) {
    	if (DEBUG) System.out.printf("XtaDisassemblyConfiguration: getHyperlinkStateMask\n");	
		return 0;
	}
}
