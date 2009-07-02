package com.xmos.xta.gui.views.structure;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.viewnode.ViewNode;

public class Renderer implements Runnable {
    public static final boolean DEBUG = false;

    private Display m_display;
    private RendererControl m_control;
    private ZoomedCanvasProperties m_canvasProperties;

    private Image m_structureImage;
    private Image m_timesImage;

	public Renderer(Display display, RendererControl control, ZoomedCanvasProperties canvasProperties) {
		if (DEBUG) System.out.printf("Renderer()\n");
		m_display = display;
		m_control = control;

		m_canvasProperties = canvasProperties;
		
	    // Dimensions must be minimum of 1
		m_structureImage = new Image(m_display, 1, 1);
		m_timesImage = new Image(m_display, 1, 1);

	    // Determine font metrics
        GC gc = new GC(m_structureImage);
        m_canvasProperties.setAverageCharWidth(gc.getFontMetrics().getAverageCharWidth());
        gc.dispose();
	}

	public void run() {
		boolean done = false;
		while (!done) {
			done = m_control.waitForChange();
			if (!done) {
			    if (m_control.renderRequired() || m_control.hasPreviousSelection()) {
					if (DEBUG) System.out.printf("Renderer::startSyncExec()\n");
					m_display.syncExec(new Runnable() {
						public void run() {
							draw();
						}});

					m_control.clearRenderRequired();
			    }
				
				m_control.updateCanvasImages(m_structureImage, m_timesImage);
				m_control.signalRenderComplete();
			}
		}
	}

	private void draw() {
		if (DEBUG) System.out.printf("StructureRenderer::draw()\n");

	    if (m_control.hasPreviousSelection()) {
    		selectionChanged();
	    } 
	    
	    if (m_control.renderRequired()) {
		    m_control.updateCanvasProperties(m_canvasProperties);
	    	resizeImages();
	    	renderImages();
	    }
		if (DEBUG) System.out.printf("StructureRenderer::draw() done\n");
	}

    protected void resizeImages() {
		double scale = m_control.getScale();
		int imageX = (int)((double)m_control.getPixelsX() * scale);
		int imageY = (int)((double)m_control.getPixelsY() * scale);

		int structureCanvasX = m_control.getStructureCanvasWidth();
    	int structureCanvasY = m_control.getStructureCanvasHeight();
    	int timesCanvasY = m_control.getTimesCanvasHeight();

		if (structureCanvasX > imageX)
			imageX = structureCanvasX;
		if (structureCanvasY > imageY)
			imageY = structureCanvasY;
    	
		m_structureImage.dispose();
		m_structureImage = new Image(m_display, imageX, imageY);
		if (DEBUG) System.out.println("Resize m_structureImage " + imageX + ", " + imageY);

		m_timesImage.dispose();
		if (timesCanvasY == 0) timesCanvasY = 1;
		m_timesImage = new Image(m_display, imageX, timesCanvasY);
		if (DEBUG) System.out.println("Resize m_timesImage " + imageX + ", " + timesCanvasY);

        ViewNode viewNode = m_control.getViewNode();
		if (viewNode != null) {
    		viewNode.updateScaledXy();
    	}
    }
    
	private void renderImages() {
	    GC structureGc = new GC(m_structureImage);
	    structureGc.setAntialias(SWT.ON);
	    structureGc.setTextAntialias(SWT.ON);

	    GC timesGc = new GC(m_timesImage);
	    timesGc.setAntialias(SWT.ON);
	    timesGc.setTextAntialias(SWT.ON);

	    // Draw the structure
        structureGc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        Rectangle imageBounds = m_structureImage.getBounds();
        structureGc.fillRectangle(0, 0, imageBounds.width, imageBounds.height);

        imageBounds = m_timesImage.getBounds();
        timesGc.fillRectangle(0, 0, imageBounds.width, imageBounds.height);

        ViewNode viewNode = m_control.getViewNode();
        if (viewNode != null) {
			viewNode.draw(m_display, structureGc, timesGc);
        }
	    structureGc.dispose();
	    timesGc.dispose();
	}

	private void selectionChanged() {
	    GC structureGc = new GC(m_structureImage);
	    structureGc.setAntialias(SWT.ON);
	    structureGc.setTextAntialias(SWT.ON);

	    GC timesGc = new GC(m_timesImage);
	    timesGc.setAntialias(SWT.ON);
	    timesGc.setTextAntialias(SWT.ON);
	    
	    ViewNode viewNode = m_control.getViewNode();
		List<Node> currentSelection = m_control.getCurrentSelection();
		m_canvasProperties.setSelected(currentSelection);

		if (!m_control.renderRequired()) {
			// Only draw if it isn't going to be redrawn anyway
	    	while (m_control.hasPreviousSelection()) {
				List<Node> previousSelection = m_control.getPreviousSelection();
		    	if (previousSelection.size() > 0) {
		    	    if (viewNode != null)
		    	    	viewNode.redraw(m_display, structureGc, timesGc, previousSelection);
		    	}
	    	}
		    	
	    	if (currentSelection.size() > 0) {
	    	    if (viewNode != null)
	    	    	viewNode.redraw(m_display, structureGc, timesGc, currentSelection);
	    	}
		}
    
	    structureGc.dispose();
	    timesGc.dispose();
	}
}
