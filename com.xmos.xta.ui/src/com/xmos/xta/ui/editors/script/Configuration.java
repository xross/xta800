package com.xmos.xta.ui.editors.script;

import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class Configuration extends SourceViewerConfiguration {
    public static final boolean DEBUG = false;
	
    Configuration() {
		if (DEBUG) System.out.printf("Configuration()\n");		
    }

    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
    	if (DEBUG) System.out.printf("Configuration: getPresentationReconciler\n");	
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer srcDamageRepairer = new DefaultDamagerRepairer(new ScannerScript());
        reconciler.setDamager(srcDamageRepairer, "__dftl_partition_content_type");
        reconciler.setRepairer(srcDamageRepairer, "__dftl_partition_content_type");
  
        return reconciler;
    }
}
