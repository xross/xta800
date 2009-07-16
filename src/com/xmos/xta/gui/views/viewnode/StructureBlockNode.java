package com.xmos.xta.gui.views.viewnode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;

public class StructureBlockNode extends StructureNode {
    public static final boolean DEBUG = false;

    private int m_minBlockX = 2;
    private int m_minBlockY = 10;

	public StructureBlockNode(Node treeNode, ZoomedCanvasProperties canvasProperties) {
		super(treeNode, canvasProperties);
	}

	public int getPixelsX() {
		return m_minBlockX + m_treeNode.getChildren().size();
	}

	public int getPixelsY() {
		return m_minBlockY;
	}

	public int getChildrenX() {
		return 0;
	}

	public int getChildrenY() {
		return 0;
	}

	public void draw(Display display, GC gc) {
		if (!isVisible())
			return;

    	gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
    	gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
    	gc.fillRectangle(m_renderX, m_renderY, (int)m_width, (int)m_height);
       
    	gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    	gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        if (isSelected()) {
        	gc.setLineWidth(getLineWidth() + 1);
        	gc.drawRectangle(m_renderX + 1, m_renderY + 1, (int)m_width - 1, (int)m_height - 1);
        	gc.setLineWidth(getLineWidth());
        } else {
        	gc.drawRectangle(m_renderX, m_renderY, (int)m_width, (int)m_height);
        }
        
		if (hasUnknowns()) {
    		gc.drawImage(m_warningImage,
    				m_renderX + (((int)m_width - m_warningX) / 2),
    				m_renderY + (((int)m_height - m_warningY) / 2));
        }
	}

	private boolean found;
	private boolean hasUnknowns() {
	    found = false;
	    m_treeNode.traverse(0, new UntypedNodeTraverser() {
	    	public boolean foundNode(Node node, int depth) {
	    		NodeAttributes nodeAttributes = node.getAttributes();		    		
	    		if (nodeAttributes.isUnknown() &&
	    			(nodeAttributes.getUnknownState() == UnknownState.UNKNOWN)) {
	    			found = true;
	    			return false;
	    		}
	        	return true;
	    	}
	    });
		return found;
	}

	public void annotateNodes(int offsetX, int offsetY, boolean isBestCase, boolean isWorstCase) {
		if (DEBUG) System.out.printf("Annotate StructureBlock (%d, %d)\n", offsetX, offsetY);
        m_pixelOffsetX = offsetX;
		m_pixelOffsetY = offsetY;
		
		m_isBestCase = isBestCase;
		m_isWorstCase = isWorstCase;
	}

    public boolean isInfinite() {
		return false;
	}
}
