package com.xmos.xta.gui.views.structure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.viewnode.ViewNode;

public class RendererControl {
    public static final boolean DEBUG = false;

    private int m_borderX = 2;
    private int m_borderY = 2;
    private int m_pixelsX;
    private int m_pixelsY;

    private ViewNode m_viewNode;
    
    private boolean m_selectionChanged;
    private boolean m_imageChanged;
    private boolean m_imageComplete;
    private boolean m_renderRequired;
    private boolean m_imageMoved;
    private boolean m_canvasResized;
    private boolean m_done;
	
    private double m_scale;
	
    private int m_offsetX;
    private int m_offsetY;
    
    private Queue<List<Node>> m_previousSelections;
    private List<Node> m_currentSelection;

    private Display m_display;
    private Image m_canvasImage;

	public RendererControl(Display display) {
		m_display = display;
		m_canvasImage = new Image(m_display, 1, 1);
		
	    m_imageChanged = false;
	    m_selectionChanged = false;
	    m_imageComplete = false;
	    m_imageMoved = false;
	    m_canvasResized = false;
	    
	    m_previousSelections = new LinkedList<List<Node>>();
	    m_currentSelection = new ArrayList<Node>();

	    resetState();
	}

	public synchronized boolean waitForChange() {
		if (DEBUG) System.out.println("RendererControl::waitForChange()");
        while (!m_imageChanged) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        m_imageChanged = false;
		if (DEBUG) System.out.println("RendererControl::waitForChange() - " + m_done);
		return m_done;
    }

	public synchronized void reset() {
		resetState();
		signalChange();
	}

	public synchronized void refresh() {
		if (DEBUG) System.out.println("RendererControl::refresh()");
	    if (m_viewNode != null) {
			m_viewNode.annotateNodes(m_borderX, m_borderY, true, true);
			updatePixelsXy();
		}
    	m_renderRequired = true;
		signalChange();
	}

	public synchronized void signalDone() {
		if (DEBUG) System.out.println("RendererControl::signalDone()");			
    	m_done = true;
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

	// Need to wait for the selection update to have completed before this can continue
	// otherwise the rendering gets out of step
	public synchronized void setSelection(List<Node> newNodes) {
		if (selectionChanged(newNodes, m_currentSelection)) {
			m_previousSelections.add(m_currentSelection);
			m_currentSelection = newNodes;
			m_renderRequired = true;
			signalChange();
		}
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

	public synchronized void clearRenderRequired() {
		if (DEBUG) System.out.println("RendererControl::clearRenderRequired()");
		m_renderRequired = false;
	}

	public synchronized void clearImageMoved() {
		if (DEBUG) System.out.println("RendererControl::clearImageMoved()");
		m_imageMoved = false;
	}
	
	public synchronized Image getResizedCanvas() {
		if (DEBUG) System.out.println("RendererControl::clearCanvasResized()");
		m_canvasResized = false;
		return m_canvasImage;
	}
	
	public synchronized void setCanvasSize(int width, int height) {
		Rectangle bounds = m_canvasImage.getBounds();
		if (bounds.width != width || bounds.height != height) {
			if (DEBUG) System.out.println("RendererControl::setCanvasSize(" + width + ", " + height + ")");

			m_canvasImage.dispose();
			m_canvasImage = createImage(m_display, width, height);

			m_canvasResized = true;
			signalChange();
		}
	}

	public synchronized boolean waitForRender() {
		if (DEBUG) System.out.println("RendererControl::waitForRender()");
        while (!m_imageComplete) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        m_imageComplete = false;
		if (DEBUG) System.out.println("RendererControl::waitForRender() - " + m_done);
		return m_done;
    }

	public synchronized void signalRenderComplete() {
		if (DEBUG) System.out.println("RendererControl::signalRenderComplete()");
		m_imageComplete = true;
		notifyAll();
    }

	private void signalChange() {
		if (DEBUG) System.out.println("RendererControl::signalChange()");			
    	m_imageChanged = true;
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

	public boolean renderRequired() {
		return m_renderRequired;
	}

	public boolean imageMoved() {
		return m_imageMoved;
	}

	public boolean canvasResized() {
		return m_canvasResized;
	}

	public synchronized void updateCanvasProperties(ZoomedCanvasProperties canvasProperties) {
		if (m_viewNode != null) {
	    	canvasProperties.setScale(m_scale);
		} else {
			if (DEBUG) System.out.printf("RendererControl::updateCanvasProperties(0, 0, 0, 0)\n");
        	canvasProperties.setVisibleArea(new Rectangle(0, 0, 0, 0));
		}
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
    	m_pixelsX = (2 * m_borderX);
    	m_pixelsY = (2 * m_borderY);
    	if (m_viewNode != null) {
    		m_pixelsX += m_viewNode.getPixelsX();
    		m_pixelsY += m_viewNode.getPixelsY();
    	}
		m_renderRequired = true;
		if (DEBUG) System.out.println("RendererControl::updatePixelsXy() - " + m_pixelsX + ", " + m_pixelsY);
	}

	public synchronized Image getCanvasImage() {
		return m_canvasImage;
	}

	public synchronized void updateCanvasImages(Image image, int offsetX) {
		if (DEBUG) System.out.println("RendererControl::updateCanvasImages()");
		copyImage(image, m_canvasImage, offsetX, m_offsetY);
	}

	// Copy images, ensuring bounds are valid
	private synchronized void copyImage(Image fromImage, Image toImage, int offsetX, int offsetY) {
		Rectangle toBounds = toImage.getBounds();
		int canvasX = toBounds.width;
		int canvasY = toBounds.height;
		
		Rectangle fromBounds = fromImage.getBounds();
		if (canvasX > fromBounds.width)
			canvasX = fromBounds.width;
		if (canvasY > fromBounds.height)
			canvasY = fromBounds.height;
		
		if ((offsetX + canvasX) > fromBounds.width)
			offsetX = fromBounds.width - canvasX;
		if ((offsetY + canvasY) > fromBounds.height)
			offsetY = fromBounds.height - canvasY;
		
		GC gc = new GC(toImage);
		gc.drawImage(fromImage,
				offsetX, offsetY, canvasX, canvasY,
				0, 0, canvasX, canvasY);
		gc.dispose();
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

	private Image createImage(Display display, int x, int y) {
		// Ensure image bounds are valid
		if (x < 1) x = 1;
		if (y < 1) y = 1;
		return new Image(display, x, y);
	}

	public int getCanvasWidth() {
		return m_canvasImage.getBounds().width;
	}

	public int getCanvasHeight() {
		return m_canvasImage.getBounds().height;
	}
}
