package com.xmos.xta.ui.views.info;

import java.util.List;

import org.eclipse.swt.widgets.Control;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public interface Viewer {
	void update();
    void updateHighlighting(List<Node> selectedNodes);
    Control getControl();
}
