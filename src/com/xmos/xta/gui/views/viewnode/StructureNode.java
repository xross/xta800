package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public abstract class StructureNode extends ViewNode {
	
    public StructureNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
    	super(treeNode, canvasProperties);
    }

    public double draw(Display display, GC gc, Case c, double offsetX, double offsetY) {
    	assert(false);
    	return 0;
    }

    public int getPixelsY(Case c) {
    	assert(false);
    	return 0;
    }
}
