package com.xmos.xta.gui.views;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.viewnode.ViewNode;

public class RendererControl {
    public static final boolean DEBUG = false;

    private int m_leftBorder;
    private int m_topBorder;
    private int m_rightBorder;
    private int m_bottomBorder;
    
    private int m_pixelsX;
    private int m_pixelsY;
    
    private int m_canvasWidth;
    private int m_canvasHeight;

    private ViewNode m_viewNode;
    
    private boolean m_selectionChanged;
    private boolean m_wakeRenderer;
    private boolean m_renderRequired;
    private boolean m_imageMoved;
    private boolean m_canvasResized;
    private boolean m_programExited;
    
    private boolean m_selectBoxChanged;
    private Rectangle m_selectBox;
	
    private double m_scale;
	
    private int m_offsetX;
    private int m_offsetY;
    
    private Queue<List<Node>> m_previousSelections;
    private List<Node> m_currentSelection;
    
    private Image m_canvasImage;

	public RendererControl(int leftBorder, int topBorder, int rightBorder, int bottomBorder) {
	    m_leftBorder = leftBorder;
		m_topBorder = topBorder;
	    m_rightBorder = rightBorder;
	    m_bottomBorder = bottomBorder;

	    m_wakeRenderer = false;
	    m_selectionChanged = false;
	    m_imageMoved = false;
	    m_canvasResized = false;

	    m_selectBoxChanged = false;
	    m_selectBox = null;
	    
	    m_previousSelections = new LinkedList<List<Node>>();
	    m_currentSelection = new ArrayList<Node>();

	    resetState();
	}

	public synchronized boolean waitForChange() {
		if (DEBUG) System.out.println("RendererControl::waitForChange()");
        while (!m_wakeRenderer && !m_programExited) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        m_wakeRenderer = false;
		if (DEBUG) System.out.println("RendererControl::waitForChange() - " + m_programExited);
		return m_programExited;
    }

	public synchronized void reset() {
		resetState();
		signalChange();
	}

	public synchronized void refresh() {
		if (DEBUG) System.out.println("RendererControl::refresh()");
	    if (m_viewNode != null) {
			m_viewNode.annotateNodes(m_leftBorder, m_topBorder, true, true);
			updatePixelsXy();
		}
    	m_renderRequired = true;
		signalChange();
	}

	public synchronized void signalDone() {
		if (DEBUG) System.out.println("RendererControl::signalDone()");			
    	m_programExited = true;
        notifyAll();
    }

	public synchronized void setOffsetX(int offsetX) {
		if (m_offsetX != offsetX) {
			if (DEBUG) System.out.println("RendererControl::setOffsetX(" + offsetX + ")");
			m_offsetX = offsetX;
			m_imageMoved = true;
			signalChange();
		}
	}

	public synchronized void setOffsetY(int offsetY) {
		if (m_offsetY != offsetY) {
			if (DEBUG) System.out.println("RendererControl::setOffsetX(" + offsetY + ")");
			m_offsetY = offsetY;
			m_imageMoved = true;
			signalChange();
		}
	}

	public synchronized void setViewNode(ViewNode viewNode) {
		m_viewNode = viewNode;
		refresh();
	}

	public synchronized void setScale(double scale) {
		if (m_scale != scale) {
			if (DEBUG) System.out.println("RendererControl::setScale(" + scale + ")");
			m_scale = scale;
			m_renderRequired = true;
			signalChange();
		}
	}

	public synchronized void setCanvasSize(int width, int height) {
		if (m_canvasWidth != width || m_canvasHeight != height) {
			if (DEBUG) System.out.println("RendererControl::setCanvasSize(" + width + ", " + height + ")");
			m_canvasWidth = width;
			m_canvasHeight = height;
			m_canvasResized = true;
			signalChange();
		}
	}
	
	public synchronized void setSelection(List<Node> newNodes) {
		if (selectionChanged(newNodes, m_currentSelection)) {
			m_previousSelections.add(m_currentSelection);
			m_currentSelection = newNodes;
			m_renderRequired = true;
			signalChange();
		}
	}

	public synchronized void setSelectBox(Rectangle box) {
		m_selectBox = box;
		m_selectBoxChanged = true;
		signalChange();
	}
	public synchronized Rectangle getSelectBox() {
		m_selectBoxChanged = false;
		return m_selectBox;
	}

	
	public synchronized List<Node> getCurrentSelection() {
		return m_currentSelection;
	}

	public synchronized boolean hasPreviousSelection() {
		return m_previousSelections.size() > 0;
	}

	public synchronized List<Node> getPreviousSelection() {
		return m_previousSelections.remove();
	}

	public synchronized boolean getAndClearRenderRequired() {
		if (DEBUG) System.out.println("RendererControl::clearRenderRequired()");
		boolean renderRequired = m_renderRequired;
		m_renderRequired = false;
		return renderRequired;
	}

	public synchronized boolean getAndClearImageMoved() {
		if (DEBUG) System.out.println("RendererControl::clearImageMoved()");
		boolean imageMoved = m_imageMoved;
		m_imageMoved = false;
		return imageMoved;
	}
	
	public synchronized Rectangle getCanvasBounds() {
		if (DEBUG) System.out.println("RendererControl::clearCanvasResized()");
		m_canvasResized = false;
		return new Rectangle(0, 0, m_canvasWidth, m_canvasHeight);
	}
	
	public synchronized Image getCanvasImage() {
		return m_canvasImage;
	}

	public synchronized void setCanvasImage(Image canvasImage) {
		m_canvasImage = canvasImage;
	}
	
	private void signalChange() {
		if (DEBUG) System.out.println("RendererControl::signalChange()");			
    	m_wakeRenderer = true;
        notifyAll();
    }

    public int getOffsetX() {
		return m_offsetX;
	}

	public int getOffsetY() {
		return m_offsetY;
	}

	public ViewNode getViewNode() {
		return m_viewNode;
	}

	public boolean selectionChanged() {
		return m_selectionChanged;
	}

	public boolean canvasResized() {
		return m_canvasResized;
	}

	public boolean selectBoxChanged() {
		return m_selectBoxChanged;
	}

	public int getPixelsX() {
		return m_pixelsX;
	}

	public int getPixelsY() {
		return m_pixelsY;
	}

	public double getScale() {
		return m_scale;
	}

	private void resetState() {
	    m_viewNode = null;

	    m_offsetX = 0;
	    m_offsetY = 0;
	    
	    m_scale = 1.0;
	    updatePixelsXy();
	}
	
	private synchronized void updatePixelsXy() {
    	m_pixelsX = (m_leftBorder + m_rightBorder);
    	m_pixelsY = (m_topBorder + m_bottomBorder);
    	if (m_viewNode != null) {
    		m_pixelsX += m_viewNode.getPixelsX();
    		m_pixelsY += m_viewNode.getPixelsY();
    	}
		m_renderRequired = true;
		if (DEBUG) System.out.println("RendererControl::updatePixelsXy() - " + m_pixelsX + ", " + m_pixelsY);
	}

	private boolean selectionChanged(List<Node> oldList, List<Node> newList) {
		if (oldList.size() != newList.size())
			return true;
		
		for (Node node : oldList) {
			if (!newList.contains(node))
				return true;
		}
		return false;
	}

	public int scale(int value) {
		return (int)((double)value * m_scale);
	}

	public int getCanvasWidth() {
		return m_canvasWidth;
	}

	public int getCanvasHeight() {
		return m_canvasHeight;
	}
}
