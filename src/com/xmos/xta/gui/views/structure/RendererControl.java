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
    private boolean m_done;
	
    private double m_scale;
	
    private int m_offsetX;
    private int m_offsetY;
    
    private int m_structureCanvasWidth;
    private int m_structureCanvasHeight;
    private int m_timesCanvasHeight;
    
    private double m_worstTime;
    private double m_bestTime;

    private Queue<List<Node>> m_previousSelections;
    private List<Node> m_currentSelection;

    private Display m_display;
    private Image m_structureCanvasImage;
    private Image m_timesCanvasImage;

	public RendererControl(Display display) {
		m_display = display;
		m_structureCanvasImage = new Image(m_display, 1, 1);
		m_timesCanvasImage = new Image(m_display, 1, 1);
		
	    m_imageChanged = false;
	    m_selectionChanged = false;
	    m_imageComplete = false;
	    
	    m_previousSelections = new LinkedList<List<Node>>();
	    m_currentSelection = new ArrayList<Node>();
	    m_structureCanvasWidth = 0;
	    m_structureCanvasHeight = 0;
	    m_timesCanvasHeight = 0;

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
		if (m_viewNode != null) {
			m_viewNode.annotateNodes(m_borderX, m_borderY, true, true);
			updatePixelsXy();
			signalChange();
		}
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
			signalChange();
		}
	}

	public synchronized void setOffsetY(int offsetY) {
		if (m_offsetY != offsetY) {
			if (DEBUG) System.out.println("RendererControl::setOffsetX(" + offsetY + ")");
			m_offsetY = offsetY;
			signalChange();
		}
	}

	public synchronized void setViewNode(ViewNode viewNode) {
		m_viewNode = viewNode;
		updatePixelsXy();
		signalChange();
	}

	public synchronized void setScale(double scale) {
		if (m_scale != scale) {
			if (DEBUG) System.out.println("RendererControl::setScale(" + scale + ")");
			m_scale = scale;
			m_renderRequired = true;
			signalChange();
		}
	}

	public synchronized void setBestWorstTimes(double bestTime, double worstTime) {
		if (m_bestTime != bestTime || m_worstTime != worstTime) {
			if (DEBUG) System.out.println("RendererControl::setBestWorstTimes(" + bestTime + ", " + worstTime + ")");
		    m_bestTime = bestTime;
		    m_worstTime = worstTime;
			m_renderRequired = true;
			m_viewNode.annotateNodes(m_borderX, m_borderY, true, true);
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
	
	public synchronized void setStructureCanvasSize(int width, int height) {
		if (m_structureCanvasWidth != width || m_structureCanvasHeight != height) {
			if (DEBUG) System.out.println("RendererControl::setStructureCanvasSize(" + width + ", " + height + ")");
			m_structureCanvasWidth = width;
			m_structureCanvasHeight = height;

			m_structureCanvasImage.dispose();
			m_structureCanvasImage = new Image(m_display, m_structureCanvasWidth, m_structureCanvasHeight);

			signalChange();
		}
	}

	public synchronized void setTimesCanvasHeight(int height) {
		if (m_timesCanvasHeight != height) {
			if (DEBUG) System.out.println("RendererControl::setTimesCanvasHeight(" + height + ")");
			m_timesCanvasHeight = height;

			m_timesCanvasImage.dispose();
			m_timesCanvasImage = new Image(m_display, m_structureCanvasWidth, m_timesCanvasHeight);

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
	
	public void updateCanvasProperties(ZoomedCanvasProperties canvasProperties) {
		if (m_viewNode != null) {
        	Rectangle visibleArea = new Rectangle(m_offsetX, m_offsetY, m_structureCanvasWidth, m_structureCanvasHeight);
        	canvasProperties.setVisibleArea(visibleArea);
        	canvasProperties.setTimesHeight(m_timesCanvasHeight);

			canvasProperties.setTotalBest(m_bestTime);
	    	canvasProperties.setTotalWorst(m_worstTime);
	    	canvasProperties.setScale(m_scale);
		} else {
        	canvasProperties.setVisibleArea(new Rectangle(0, 0, 0, 0));
        	canvasProperties.setTimesHeight(0);
		}
    }

	public int getStructureCanvasWidth() {
		return m_structureCanvasWidth;
	}

	public int getStructureCanvasHeight() {
		return m_structureCanvasHeight;
	}

	public int getTimesCanvasHeight() {
		return m_timesCanvasHeight;
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
	    
	    m_worstTime = 0;
	    m_bestTime = 0;

	    m_scale = 1.0;
	    updatePixelsXy();
	}
	
	private void updatePixelsXy() {
    	m_pixelsX = (2 * m_borderX);
    	m_pixelsY = (2 * m_borderY);
    	if (m_viewNode != null) {
    		m_pixelsX += m_viewNode.getPixelsX();
    		m_pixelsY += m_viewNode.getPixelsY();
    	}
		m_renderRequired = true;
		if (DEBUG) System.out.println("RendererControl::updatePixelsXy() - " + m_pixelsX + ", " + m_pixelsY);
	}

	public synchronized Image getStructureCanvasImage() {
		return m_structureCanvasImage;
	}

	public synchronized Image getTimesCanvasImage() {
		return m_timesCanvasImage;
	}

	public void updateCanvasImages(Image structureImage, Image timesImage) {
		if (DEBUG) System.out.println("RendererControl::updateCanvasImages()");
		copyImage(structureImage, m_structureCanvasImage, m_offsetX, m_offsetY);
		copyImage(timesImage, m_timesCanvasImage, m_offsetX, 0);
	}

	// Copy images, ensuring bounds are valid
	private void copyImage(Image fromImage, Image toImage, int offsetX, int offsetY) {
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
}
