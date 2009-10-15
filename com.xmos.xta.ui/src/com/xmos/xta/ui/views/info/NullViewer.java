package com.xmos.xta.ui.views.info;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class NullViewer implements Viewer {
	private Control m_control;
	
	NullViewer(Composite composite) {
		m_control = new Label(composite, SWT.NONE);
	}
	
	public void update() {
	}

	public void updateHighlighting(List<Node> selectedNodes) {
	}

	public Control getControl() {
		return m_control;
	}	
}
