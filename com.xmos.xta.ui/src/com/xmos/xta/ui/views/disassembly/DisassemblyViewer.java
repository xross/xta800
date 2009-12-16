package com.xmos.xta.ui.views.disassembly;

import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class DisassemblyViewer extends ProjectionViewer {
	public DisassemblyViewer(Composite parent, IVerticalRuler vertRuler, IOverviewRuler ovRuler) {
		super(parent, vertRuler, ovRuler, true, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		setEditable(false);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getControl().setLayoutData(gd);
	}
}
