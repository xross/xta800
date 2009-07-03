package com.xmos.xta.gui.views.structure;

import java.util.ArrayList;
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

    private static final int MAX_IMAGE_SIZE = 1024 * 1024 * 50;

    private Display m_display;
    private RendererControl m_control;
    private ZoomedCanvasProperties m_canvasProperties;

    private Image m_structureImage;
    private Image m_timesImage;
    private boolean m_imageTooLarge;
    
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
				if (m_control.hasPreviousSelection()) {
					selectionChanged();
				}
				
			    if (m_control.renderRequired()) {
			    	updateCanvasProperties();
			    }
		    	
			    if (m_imageTooLarge) {
					m_display.syncExec(new Runnable() {
						public void run() {
							render(m_control.getOffsetX(), m_control.getOffsetY());
						}});
					m_control.updateCanvasImages(m_structureImage, m_timesImage, 0);
					
			    } else {
			    	if (m_control.renderRequired()) {
						m_display.syncExec(new Runnable() {
							public void run() {
								render(0, 0);
							}});
						m_control.clearRenderRequired();
			    	}
					m_control.updateCanvasImages(m_structureImage, m_timesImage, m_control.getOffsetX());
			    	
			    }
			}
		}
	}

	private void render(int offsetX, int offsetY) {
		if (DEBUG) System.out.printf("StructureRenderer::render(%d, %d)\n", offsetX, offsetY);

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
        	Rectangle visibleArea = new Rectangle(offsetX, offsetY, imageBounds.width, imageBounds.height);
        	m_canvasProperties.setVisibleArea(visibleArea);
    		viewNode.updateScaledXy();
        	viewNode.draw(m_display, structureGc, timesGc);
        }
	    structureGc.dispose();
	    timesGc.dispose();

	    if (DEBUG) System.out.printf("StructureRenderer::render() done\n");
	}

	private void selectionChanged() {
	    ViewNode viewNode = m_control.getViewNode();
		List<Node> currentSelection = m_control.getCurrentSelection();
		m_canvasProperties.setSelected(currentSelection);

    	while (m_control.hasPreviousSelection()) {
    		// Must iterate over the previous selections in order to clear the list
			List<Node> previousSelection = m_control.getPreviousSelection();

			if (m_control.renderRequired() || (viewNode == null) ||
				(previousSelection.size() == 0) || m_imageTooLarge) {
    			continue;
    		}
    		redraw(viewNode, previousSelection);
    	}

    	if ((currentSelection.size() > 0) && (viewNode != null)) {
    		redraw(viewNode, currentSelection);
		}
	}

	private void redraw(final ViewNode viewNode, final List<Node> selection) {
		final GC structureGc = new GC(m_structureImage);
	    structureGc.setAntialias(SWT.ON);
	    structureGc.setTextAntialias(SWT.ON);

	    final GC timesGc = new GC(m_timesImage);
	    timesGc.setAntialias(SWT.ON);
	    timesGc.setTextAntialias(SWT.ON);

		m_display.syncExec(new Runnable() {
			public void run() {
    	    	viewNode.redraw(m_display, structureGc, timesGc, selection);
			}});

	    structureGc.dispose();
	    timesGc.dispose();
	}

	private void updateCanvasProperties() {
    	m_control.updateCanvasProperties(m_canvasProperties);
    	double scale = m_control.getScale();
		int imageX = (int)((double)m_control.getPixelsX() * scale);
		int imageY = (int)((double)m_control.getPixelsY() * scale);
		
		int structureCanvasX = m_control.getStructureCanvasWidth();
    	int structureCanvasY = m_control.getStructureCanvasHeight();
    	int timesCanvasY = m_control.getTimesCanvasHeight();
		if (timesCanvasY == 0) timesCanvasY = 1;

		if (structureCanvasX > imageX)
			imageX = structureCanvasX;
		if (structureCanvasY > imageY)
			imageY = structureCanvasY;

		m_structureImage.dispose();
		m_timesImage.dispose();

		if ((imageX * imageY) > MAX_IMAGE_SIZE) {
			m_imageTooLarge = true;
			assert((structureCanvasX * structureCanvasY) < MAX_IMAGE_SIZE);
			imageX = structureCanvasX;
			imageY = structureCanvasY;
		} else {
			m_imageTooLarge = false;
		}

		m_structureImage = new Image(m_display, imageX, imageY);
		if (DEBUG) System.out.println("Resize m_structureImage " + imageX + ", " + imageY);

		m_timesImage = new Image(m_display, imageX, timesCanvasY);
		if (DEBUG) System.out.println("Resize m_timesImage " + imageX + ", " + timesCanvasY);

        ViewNode viewNode = m_control.getViewNode();
		if (viewNode != null) {
    		viewNode.updateScaledXy();
    	}
	}
	
    protected void resizeImages() {
		double scale = m_control.getScale();
		int imageX = (int)((double)m_control.getPixelsX() * scale);
		int imageY = (int)((double)m_control.getPixelsY() * scale);

    }
}
